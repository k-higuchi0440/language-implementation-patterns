// START: header
tree grammar Def;
options {
  tokenVocab = Cymbol;
  ASTLabelType = CymbolAST;
  filter = true;
}
@members {
    SymbolTable symtab;
    Scope currentScope;
    MethodSymbol currentMethod;
    public Def(TreeNodeStream input, SymbolTable symtab) {
        this(input);
        this.symtab = symtab;
        currentScope = symtab;
    }
    public SymbolTable getSymtab() { return (SymbolTable) currentScope; }
}
// END: header

topdown
    :   enterBlock
    |   enterMethod
    |   enterStruct
    |	atoms
    |   varDeclaration
    |	ret
    ;

bottomup
    :   exitBlock
    |   exitMethod
    |   exitStruct
    ;

// S C O P E S

enterBlock
    :   BLOCK { currentScope = BlockScope.apply(Some.apply(currentScope)); } // push scope
    ;
exitBlock
    :   BLOCK
        {
        //System.out.println("locals: "+currentScope);
        MethodScope enclosingMethodScope = (MethodScope) currentScope.enclosingScope().get();
        String funcName = enclosingMethodScope.symbolName().get();
        MethodSymbol method = (MethodSymbol) enclosingMethodScope.enclosingScope().get().resolve(funcName).get();
        MethodSymbol updated =  method.copy(
                method.copy$default$1(),
                method.copy$default$2(),
                method.copy$default$3(),
                method.copy$default$4(),
                currentScope.symbols(),
                method.copy$default$6()
        );
        Scope methodUpdatedScope = enclosingMethodScope.enclosingScope().get().define(updated);
        currentScope = enclosingMethodScope.copy(
                enclosingMethodScope.copy$default$1(),
                enclosingMethodScope.copy$default$2(),
                Some.apply(methodUpdatedScope)
        );
        }
    ;

// START: struct
enterStruct
    :   ^('struct' ID .+)
        {
        StructSymbol ss = StructSymbol.apply(
                $ID.text,
                newStructType, // 型定義しとく
                Some.apply(currentScope),
                Some.apply($ID));
        $ID.symbol_$eq(Some.apply(ss));
        currentScope = StructScope.apply(ss.name(), Some.apply(currentScope.define(ss))); // set current scope to struct scope
        }
    ;
exitStruct
    :   'struct'
        {
        //System.out.println("fields: "+currentScope);
        Scope enclosingScope = currentScope.enclosingScope().get();
        StructSymbol struct = (StructSymbol)enclosingScope.resolve(currentScope.symbolName().get()).get();
        StructSymbol updated = struct.copy(
                struct.copy$default$1(),
                struct.copy$default$2(),
                struct.copy$default$3(),
                currentScope.symbols(),
                struct.copy$default$5()
        );
        currentScope = enclosingScope.define(updated);    // pop scope
        }
    ;
// END: struct

enterMethod
    :   ^(METHOD_DECL type ID .*) // match method subtree with 0-or-more args
        {
        //System.out.println("line "+$ID.getLine()+": def method "+$ID.text);
        MethodSymbol ms = MethodSymbol.apply(
                $ID.text,
                $type.type,
                Some.apply(currentScope),
                Some.apply($ID)
        );
        currentMethod = ms;
        $ID.symbol_$eq(Some.apply(ms));         // track in AST
        currentScope = MethodScope.apply(ms.name(), Some.apply(currentScope.define(ms))); // set current scope to method scope
        }
    ;

/** Track method associated with this return. */
ret	:	^('return' .) {$ret.start.symbol = currentMethod;}
	;
	
exitMethod
    :   METHOD_DECL
        {
        Scope enclosingScope = currentScope.enclosingScope().get();
        MethodSymbol method = (MethodSymbol) enclosingScope.resolve(currentScope.symbolName().get()).get();
        MethodSymbol updated = method.copy(
                method.copy$default$1(),
                method.copy$default$2(),
                method.copy$default$3(),
                currentScope.symbols(),
                method.copy$default$5(),
                method.copy$default$6()
        );
        currentScope = enclosingScope.define(updated);    // pop arg scope
        }
    ;

// D e f i n e  s y m b o l s

// START: atoms
/** Set scope for any identifiers in expressions or assignments */
atoms
@init {CymbolAST t = (CymbolAST)input.LT(1);}
    :  {t.hasAncestor(EXPR)||t.hasAncestor(ASSIGN)}? ID
       {t.scope = currentScope;}
    ;
//END: atoms

// START: var
varDeclaration // global, parameter, or local variable
    :   ^((FIELD_DECL|VAR_DECL|ARG_DECL) type ID .?)
        {
        //System.out.println("line "+$ID.getLine()+": def "+$ID.text);
        VariableSymbol vs = VariableSymbol.apply($ID.text, $type.type, Some.apply(ID4));
        $ID.symbol_$eq(Some.apply(vs));         // track in AST
        currentScope = currentScope.define(vs);
        }
    ;
// END: field

/** Not included in tree pattern matching directly.  Needed by declarations */
type returns [Type type]
	:	^('[]' typeElement)	{$type = new ArrayType($typeElement.type);}
	|	typeElement 	    {$type = $typeElement.type;}
	;

typeElement returns [Type type]
@init {CymbolAST t = (CymbolAST)input.LT(1);}
@after {
    t.symbol_$eq(currentScope.resolve(t.getText())); // return Type
    t.scope_$eq(Some.apply(currentScope));
    type = t.symbol().map(Symbol::typ).getOrElse(() -> NullType$.MODULE$);
}
    :   'float'
    |   'int'
    |   'char'
    |   'boolean'
    |   'void'
    |   ID // struct name
    ;

