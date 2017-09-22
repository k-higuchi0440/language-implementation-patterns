// START: header
tree grammar Def;
options {
  tokenVocab = Cymbol;
  ASTLabelType = CymbolAST;
  filter = true;
}
@members {
    public SymbolTable symtab;
    Scope currentScope;
    MethodSymbol currentMethod;
    public Def(TreeNodeStream input, SymbolTable symtab) {
        this(input);
        this.symtab = symtab;
        currentScope = symtab;
    }
}
// END: header

topdown
    :   enterBlock
    |   enterMethod
    |   enterClass
    |   atoms
    |   varDeclaration
    |   ret
    ;

bottomup
    :   exitBlock
    |   exitMethod
    |   exitClass
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
                currentScope.symbols(),
                method.copy$default$5()
        );
        enclosingMethodScope.enclosingScope().get().define(updated);
        currentScope = enclosingMethodScope;
        }
    ;

// START: class
enterClass
    :   ^('class' name=ID (^(':' sup=ID))? .)
        { // Def class but leave superclass blank until ref phase
        Option<Type> superclassTypeOpt = Option.empty();
        ClassSymbol superclass = null;
        if ( sup!=null ) { // can only ref classes above in file
            Option<Symbol> symbolOpt = currentScope.resolve((sup!=null?sup.getText():null)); // find superclass
            superclass = (ClassSymbol) symbolOpt.get();
            superclassTypeOpt = Some.apply(superclass.typ());
            sup.symbol_$eq(Some.apply(superclass));
        }
        Type classType = Type$.MODULE$.defineClassType((name!=null?name.getText():null), superclassTypeOpt);
        ClassSymbol cs = ClassSymbol.apply((name!=null?name.getText():null), classType, Some.apply(superclass), Some.apply(name));
        name.symbol_$eq(Some.apply(cs)); // point from AST into symbol table

        currentScope.define(cs);  // def struct in current scope
        currentScope = ClassScope.apply(cs.name(), Some.apply(currentScope)); // set current scope to struct scope
        }
    ;
// END: class

exitClass
    :   'class'
        {
        Scope enclosingScope = currentScope.enclosingScope().get();
        ClassSymbol clazz = (ClassSymbol) enclosingScope.resolve(currentScope.symbolName().get()).get();
        ClassSymbol updated = clazz.copy(
                clazz.copy$default$1(),
                clazz.copy$default$2(),
                clazz.copy$default$3(),
                currentScope.symbols(),
                clazz.copy$default$5()
        );
        enclosingScope.define(updated);
        currentScope = enclosingScope; // pop scope
        }
    ;

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
        $ID.symbol_$eq(Some.apply(ms)); // track in AST
        currentScope.define(ms);
        currentScope = MethodScope.apply(ms.name(), Some.apply(currentScope)); // set current scope to method scope
        }
    ;

/** Track method associated with this return. */
ret :   ^('return' .) {$ret.start.symbol_$eq(Some.apply(currentMethod));}
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
        enclosingScope.define(updated);
        currentScope = enclosingScope; // pop arg scope
        }
    ;

// D e f i n e  s y m b o l s

// START: atoms
/** Set scope for any identifiers in expressions or assignments */
atoms
@init {CymbolAST t = (CymbolAST)input.LT(1);}
    :  {t.hasAncestor(EXPR)||t.hasAncestor(ASSIGN)}? ID
       {t.scope_$eq(Some.apply(currentScope));}
    ;
//END: atoms

// START: var
varDeclaration // global, parameter, or local variable
    :   ^((FIELD_DECL|VAR_DECL|ARG_DECL) type ID .?)
        {
        //System.out.println("line "+$ID.getLine()+": def "+$ID.text);
        VariableSymbol vs = VariableSymbol.apply($ID.text, $type.type, Some.apply(ID4));
        $ID.symbol_$eq(Some.apply(vs)); // track in AST
        currentScope.define(vs);
        }
    ;
// END: field

/** Not included in tree pattern matching directly.  Needed by declarations */
// START: type
type returns [Type type]
    :   ^('*' typeElement)  {$type = Type$.MODULE$.definePointerType($typeElement.type);}
    |   typeElement         {$type = $typeElement.type;}
    ;
// END: type

/** Not included in tree pattern matching directly.  Needed by declarations */
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
    |   ID // class name
    ;
