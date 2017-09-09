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
        currentScope = symtab.globals;
    }
}
// END: header

topdown
    :   enterBlock
    |   enterMethod
    |   varDeclaration
    ;

bottomup
    :   exitBlock
    |   exitMethod
    |   assignment
    |   idref
    ;

// S C O P E S

// START: block
enterBlock
    :   BLOCK
        {
        System.out.println("[scope] create BlockScope");
        currentScope = BlockScope.apply(currentScope);
        }
    ;
exitBlock
    :   BLOCK
        {
        System.out.println("[scope] remove " + currentScope);
        currentScope = currentScope.enclosingScope().getOrElse(() -> currentScope);// pop scope
        }
    ;
// END: block

// START: method
enterMethod // match method subtree with 0-or-more args
    :   ^(METHOD_DECL type ID .*) 
        {
        System.out.println("[scope] create MethodScope");
        System.out.println("line "+ID1.getLine()+": def method "+ $ID.text);
        Type retType = $type.tsym; // rule type returns a Type symbol
        MethodSymbol ms = new MethodSymbol($ID.text,retType,currentScope);
        currentScope = ms.copy( // set current scope to method scope
          ms.copy$default$1(),
          ms.copy$default$2(),
          ms.copy$default$3().map(c -> c.define(ms)), // def method in globals
          ms.copy$default$4()
        );
        }
    ;
exitMethod
    :   METHOD_DECL
        {
        System.out.println("[scope] remove " + ((MethodSymbol)currentScope).toStringAsScope());
        currentScope = currentScope.enclosingScope().getOrElse(() -> this.currentScope);// pop arg scope
        }
    ;
// END: method

// D e f i n e  s y m b o l s

// START: var
varDeclaration // global, parameter, or local variable
    :   ^((ARG_DECL|VAR_DECL) type ID .?) 
        {
        System.out.println("line "+$ID.getLine()+": def "+$ID.text);
        VariableSymbol vs = new VariableSymbol($ID.text,$type.tsym);
        currentScope = currentScope.define(vs);
        }
    ;
// END: var

/** Not included in tree pattern matching directly.  Needed by declarations */
type returns [Type tsym]
@after {$tsym = (Type)currentScope.resolve($text);} // return Type
    :   'float'
    |   'int'
    |   'void'
    ;

// R e s o l v e  I D s

assignment
    :   ^('=' ID .)
        {
        VariableSymbol vs = (VariableSymbol)currentScope.resolve($ID.text).getOrElse(() -> VariableSymbol.apply("null", NullTypeSymbol$.MODULE$));
        System.out.println("line "+$ID.getLine()+": assign to "+vs);
        }
    ;

// START: idref
idref
    :   {$start.hasAncestor(EXPR)}? ID
        {
        Symbol s = currentScope.resolve($ID.text).getOrElse(() -> NullTypeSymbol$.MODULE$);
        System.out.println("line "+$ID.getLine()+": ref "+s);
        }
    ;
// END: idref
