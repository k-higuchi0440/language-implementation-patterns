package pattern.interpreter.pattern25_treebased.pie

import antlr3.pattern25_treebased.pie.{PieLexer, PieParser}
import org.antlr.runtime.{ANTLRStringStream, TokenRewriteStream}
import pattern.interpreter.pattern25_treebased.pie.ast.{PieAST, PieAdaptor, PieListener}
import pattern.interpreter.pattern25_treebased.pie.exception.ReturnValueException
import pattern.interpreter.pattern25_treebased.pie.memory._
import pattern.interpreter.pattern25_treebased.pie.scope.GlobalScope
import pattern.interpreter.pattern25_treebased.pie.symbol.{FunctionSymbol, StructSymbol}

import scala.collection.JavaConverters

class PieInterpreter(string: String)
{
  private val listener = PieListener

  val globalScope: GlobalScope = GlobalScope() // global scope is filled by the parser

  private val globalMemory: MemorySpace = GlobalMemorySpace()
  private var currentSpace: MemorySpace = globalMemory

  private val stack = FunctionCallStack() // call stack

  private var root: Option[PieAST] = None // the AST represents our code memory

  private val lexer: PieLexer = new PieLexer(new ANTLRStringStream(string))
  private val tokens: TokenRewriteStream = new TokenRewriteStream(lexer)
  private val parser: PieParser = new PieParser(tokens, this)
  parser.setTreeAdaptor(PieAdaptor)

  def run(): Unit = {
    val programReturn = parser.program()
    if(parser.getNumberOfSyntaxErrors == 0) {
      val tree = programReturn.getTree.asInstanceOf[PieAST]
      root = Some(tree)
      block(tree) // 記述全体をコードブロックと認識して評価を開始する
    }
  }

  private def eval(tree: PieAST): Any =
    tree.getType match {
      case PieParser.BLOCK => block(tree)
      case PieParser.ASSIGN => assign(tree)
      case PieParser.RETURN => ret(tree)
      case PieParser.PRINT => printTree(tree)
      case PieParser.IF => ifStat(tree)
      case PieParser.WHILE => whileLoop(tree)
      case PieParser.CALL => call(tree)
      case PieParser.NEW => instantiate(tree)
      case PieParser.ADD => add(tree)
      case PieParser.SUB => op(tree)
      case PieParser.MUL => op(tree)
      case PieParser.EQ => equal(tree)
      case PieParser.LT => lessThan(tree)
      case PieParser.INT => tree.getText.toInt
      case PieParser.CHAR => tree.getText.charAt(1)
      case PieParser.FLOAT => tree.getText.toFloat
      case PieParser.STRING => tree.getText.replace("\"", "")
      case PieParser.DOT => load(tree)
      case PieParser.ID => load(tree)
      case _ => new UnsupportedOperationException(s"Node ${tree.getText}<${tree.getType}> not handled")
    }

  def block(tree: PieAST): Unit =
    if (tree.getType == PieParser.BLOCK)
      JavaConverters
        .asScalaBuffer(tree.getChildren)
        .foreach(stat => eval(stat.asInstanceOf[PieAST]))
    else
      listener.error("not a block: " + tree.toStringTree)

  def assign(tree: PieAST): Unit = {
    val lhs = tree.getChild(0).asInstanceOf[PieAST]
    val expr = tree.getChild(1).asInstanceOf[PieAST]
    val value = eval(expr)
    // フィールドへの代入ならインスタンスメモリ空間に書き込み
    if (lhs.getType == PieParser.DOT) {
      fieldAssign(lhs, value)
    } else {
      // すでに
      findMemorySpaceWithDefinedSymbol(lhs.getText) match {
        case Some(space) =>
          space.write(lhs.getText, value)
        case None =>
          currentSpace.write(lhs.getText, value)
      }
    }
  }

  // field ^('=' ^('.' a x) expr)
  private def fieldAssign(lhs: PieAST, value: Any): Unit = {
    val struct = lhs.getChild(0).asInstanceOf[PieAST]
    val expr = lhs.getChild(1).asInstanceOf[PieAST]
    load(struct) match {
      case instanceSpace: StructInstanceMemorySpace =>
        val fieldName = expr.getText
        instanceSpace.struct.resolveMember(fieldName) match {
          case Some(_) =>
            instanceSpace.write(fieldName, value)
          case None =>
            listener.error(s"can't assign; ${instanceSpace.name} has no $fieldName field ${expr.token}")
        }
      case _ =>
        val leftPart = parser.input.toString(lhs.getTokenStartIndex, lhs.getTokenStopIndex - 2)
        val all = parser.input.toString(lhs.getTokenStartIndex, lhs.getTokenStopIndex)
        listener.error(s"$leftPart is not a struct in $all ${struct.token}")
    }
  }

  def ret(tree: PieAST): Nothing = {
    val value = eval(tree.getChild(0).asInstanceOf[PieAST])
    ReturnValueException.exception.value = value
    throw ReturnValueException.exception
  }

  def printTree(tree: PieAST): Unit = {
    val expr = tree.getChild(0).asInstanceOf[PieAST]
    println(eval(expr))
  }

  def ifStat(tree: PieAST): Unit = {
    val condCodeStart = tree.getChild(0).asInstanceOf[PieAST]
    val trueCodeStart = tree.getChild(1).asInstanceOf[PieAST]
    val elseCodeStart =
      if (tree.getChildCount == 3)
        Some(tree.getChild(2).asInstanceOf[PieAST])
      else None

    val cond = eval(condCodeStart).asInstanceOf[Boolean]
    if(cond) eval(trueCodeStart)
    else
      elseCodeStart match {
        case Some(elseCode) => eval(elseCode)
        case None => ()
      }
  }

  def whileLoop(tree: PieAST): Unit = {
    val condStart = tree.getChild(0).asInstanceOf[PieAST]
    val codeStart = tree.getChild(1).asInstanceOf[PieAST]
    var c = eval(condStart).asInstanceOf[Boolean]
    while (c) {
      eval(codeStart)
      c = eval(condStart).asInstanceOf[Boolean]
    }
  }

  def call(tree: PieAST): Any = {
    val funcName = tree.getChild(0).getText
    tree.scope.flatMap(_.resolve(funcName)) match {
      case Some(fs) =>
        val originalSpace = currentSpace
        val funcSpace = FunctionMemorySpace(funcName)
        val funcSymbol = fs.asInstanceOf[FunctionSymbol]
        val argCount = tree.getChildCount - 1

        // 宣言された引数と与えられた引数の数をチェック
        if (funcSymbol.hasSameNumOfArguments(argCount)) {
          for {
            (argName: String, _) <- funcSymbol.arguments.value
            argOfTree <- JavaConverters.asScalaBuffer(tree.getChildren).tail
          } {
            // 宣言された引数名と与えられた引数の値の組をメモリ空間に書き込み
            val value = eval(argOfTree.asInstanceOf[PieAST])
            funcSpace.write(argName, value)
          }

          stack.push(funcSpace)
          currentSpace = funcSpace

          val result = try {
            funcSymbol.blockAST.map(eval).getOrElse(())
          } catch {
            // この関数のBlockの子ノード中のどこかの階層でreturnが見つかったらループを終了する
            case ret: ReturnValueException => ret.value
          }

          stack.pop()
          currentSpace = originalSpace

          result
        } else {
          listener.error("function " + funcSymbol.name + " argument list mismatch")
        }
      case None =>
        listener.error(s"no such function $funcName", tree.token)
    }
  }

  def instantiate(tree: PieAST): Any = {
    val structNameNode = tree.getChild(0).asInstanceOf[PieAST]
    structNameNode.scope.flatMap(_.resolve(structNameNode.getText)) match {
      case Some(struct) =>
        StructInstanceMemorySpace(struct.asInstanceOf[StructSymbol])
      case None => ()
    }
  }

  def add(tree: PieAST): Any = {
    val a = eval(tree.getChild(0).asInstanceOf[PieAST])
    val b = eval(tree.getChild(1).asInstanceOf[PieAST])
    if (a.isInstanceOf[String] || b.isInstanceOf[String])
      a.toString + b.toString
    else
      op(tree)
  }

  def op(tree: PieAST): Any = {
    val a = eval(tree.getChild(0).asInstanceOf[PieAST])
    val b = eval(tree.getChild(1).asInstanceOf[PieAST])
    if (a.isInstanceOf[Float] || b.isInstanceOf[Float]) {
      val x = a.asInstanceOf[Number].floatValue()
      val y = b.asInstanceOf[Number].floatValue()
      tree.getType match {
        case PieParser.ADD => x + y
        case PieParser.SUB => x - y
        case PieParser.MUL => x * y
      }
    } else if (a.isInstanceOf[Int] || b.isInstanceOf[Int]) {
      val x = a.asInstanceOf[Number].intValue()
      val y = b.asInstanceOf[Number].intValue()
      tree.getType match {
        case PieParser.ADD => x + y
        case PieParser.SUB => x - y
        case PieParser.MUL => x * y
      }
    } else 0
  }

  def equal(tree: PieAST): Boolean = {
    val a = eval(tree.getChild(0).asInstanceOf[PieAST])
    val b = eval(tree.getChild(1).asInstanceOf[PieAST])
    a == b
  }

  def lessThan(tree: PieAST): Boolean = {
    val a = eval(tree.getChild(0).asInstanceOf[PieAST])
    val b = eval(tree.getChild(1).asInstanceOf[PieAST])
    a match {
      case x: Number if b.isInstanceOf[Number] =>
        val y = b.asInstanceOf[Number]
        x.floatValue < y.floatValue
      case _ => false
    }
  }

  def load(tree: PieAST): Any = {
    if (tree.getType == PieParser.DOT)
      fieldLoad(tree)
    else {
      findMemorySpaceWithDefinedSymbol(tree.getText) match {
        case Some(space) => space.read(tree.getText).get
        case None => listener.error(s"no such variable ${tree.getText}", tree.token)
      }
    }
  }

  private def fieldLoad(tree: PieAST): Any = { // E.g., a.b in tree ^('.' a b)
    val expr = tree.getChild(0).asInstanceOf[PieAST]
    val field = tree.getChild(1).asInstanceOf[PieAST]
    val fieldName = field.getText
    val instance = load(expr).asInstanceOf[StructInstanceMemorySpace] // find expr
    instance.struct.resolveMember(fieldName) match {
      case Some(_) =>
        instance.read(fieldName)
      case None =>
        listener.error(s"${instance.name} has no $fieldName field ${field.token}")
    }
  }

  // グローバルメモリ空間かコールスタックのトップの中に、
  // 既にシンボルが定義されているか確認、定義されていればその空間を返す
  private def findMemorySpaceWithDefinedSymbol(symbolName: String): Option[MemorySpace] = {
    val peek = stack.peek.asInstanceOf[Option[MemorySpace]]
    peek.map(_.read(symbolName)) match {
      case Some(_) => peek
      case None =>
        globalMemory.read(symbolName).fold[Option[MemorySpace]](None)(_ => Some(globalMemory))
    }
  }

}
