// START: header
tree grammar DefRef;
options {
  tokenVocab = Cymbol;
  ASTLabelType = CommonTree;
  filter = true;
}
@members {
    SymbolTable symtab;
    Scope currentScope;
    public DefRef(TreeNodeStream input, SymbolTable symtab) {
        this(input);
        this.symtab = symtab;
        currentScope = symtab;
    }
}
// END: header

topdown
    :   enterBlock
    |   enterMethod
    |   enterStruct
    |   varDeclaration
    |   resolveExpr
    |   assignment
    ;

bottomup
    :   exitBlock
    |   exitMethod
    |   exitStruct
    ;

// S C O P E S

enterBlock
    :   BLOCK
        {
        System.out.println("[scope] create BlockScope");
        currentScope = BlockScope.apply(currentScope); // push scope
        }
    ;
exitBlock
    :   BLOCK
        {
        System.out.println("[scope] remove " + currentScope);
        currentScope = currentScope.enclosingScope().getOrElse(() -> currentScope);    // pop scope
        }
    ;

// START: struct
enterStruct // match as we discover struct nodes (on the way down)
    : ^('struct' ID .+)
      {
      System.out.println("[scope] create" + $ID.text + "'s StructScope");
      System.out.println("line "+$ID.getLine()+": def struct "+$ID.text);
      StructSymbol ss =  StructSymbol.apply($ID.text, Option.apply(currentScope));
      Scope updated = currentScope.define(ss); // def struct in current scope
      currentScope = ss.copy(ss.copy$default$1(), Option.apply(updated), ss.copy$default$3());       // set current scope to struct scope
      }
    ;
exitStruct // match as we finish struct nodes (on the way up)
    :   'struct' // don't care about children, just trigger upon struct
        {
        StructSymbol ss = (StructSymbol)currentScope;
        System.out.println("[scope] remove " + ss.name() + "'s " + ss.toStringAsScope());
        Scope enclosing = currentScope.enclosingScope().getOrElse(() -> currentScope);
        currentScope = enclosing.define((Symbol) currentScope);    // pop scope
        }
    ;
// END: struct

enterMethod
    :   ^(METHOD_DECL type ID .*) // match method subtree with 0-or-more args
        {
        System.out.println("[scope] create MethodScope");
        System.out.println("line "+$ID.getLine()+": def method "+$ID.text);
        MethodSymbol ms = MethodSymbol.apply($ID.text,$type.tsym,Option.apply(currentScope));
        Scope updated = currentScope.define(ms); // def method in globals
        currentScope = ms.copy(ms.copy$default$1(), ms.copy$default$2(), Option.apply(updated), ms.copy$default$4());       // set current scope to method scope
        }
    ;
exitMethod
    :   METHOD_DECL
        {
        System.out.println("[scope] remove " + ((MethodSymbol)currentScope).toStringAsScope());
        currentScope = currentScope.enclosingScope().getOrElse(() -> currentScope);    // pop arg scope
        }
    ;

// D e f i n e  s y m b o l s

// START: var
varDeclaration // global, parameter, or local variable
    :   ^((FIELD_DECL|VAR_DECL|ARG_DECL) type ID .?)
        {
        System.out.println("line "+$ID.getLine()+": def "+$ID.text);
        VariableSymbol vs = new VariableSymbol($ID.text,$type.tsym);
        currentScope = currentScope.define(vs);
        }
    ;
// END: field

/** Not included in tree pattern matching directly.  Needed by declarations */
type returns [Type tsym]
@after {$tsym = (Type)currentScope.resolve($text).getOrElse(() -> NullTypeSymbol$.MODULE$);} // return Type
    :   'float'
    |   'int'
    |   'void'
    |   ID // struct name
    ;
    
// R e s o l v e  I D s

assignment
    :   ^( eq='=' member . )
        {
        System.out.println("line "+$eq.getLine()+": assign to type "+
                           $member.type.name());
        }
    ;

resolveExpr : ^(EXPR member) ;

// START: member
member returns [Type type] // expr.x; E.g., "a", "a.b", "a.b.c", ...
    : ^('.' m=member ID)
      {
      StructSymbol scope=(StructSymbol)$m.type;// get scope of expr
      Symbol s = scope.resolveMember($ID.text).getOrElse(() -> NullTypeSymbol$.MODULE$);// resolve ID in scope
      System.out.println("line "+$ID.getLine()+": ref "+
                         $m.type.name()+"."+$ID.text+" = "+s);
      if ( s!=null ) $type = s.typ();           // return ID's type
      }
    | ID                                       // resolve, return type
      {
      Symbol s = currentScope.resolve($ID.text).getOrElse(() -> NullTypeSymbol$.MODULE$);
      System.out.println("line "+$ID.getLine()+": ref "+$ID.text+" = "+s);
      if ( s!=null ) $type = s.typ();
      }
    ;
// END: member
