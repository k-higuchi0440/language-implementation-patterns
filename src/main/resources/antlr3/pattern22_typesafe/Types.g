// START: header
tree grammar Types;
options {
  tokenVocab = Cymbol;
  ASTLabelType = CymbolAST;
  filter = true;
}
@members {
    SymbolTable symtab;
    CymbolListener listener
    public Types(TreeNodeStream input, SymbolTable symtab, CymbolListener listener) {
        this(input);
        this.symtab = symtab;
        this.listener = listener;
    }
}
// END: header

bottomup // match subexpressions innermost to outermost
    :   exprRoot
    |	decl
    |	ret
    |	assignment
    |	ifstat
    ;

// promotion and type checking

// START: ifstat
ifstat : ^('if' cond=. s=. e=.?) {Type$.MODULE$.ifStat(cond, listener);} ;
// END: ifstat

decl:   ^(VAR_DECL . ID (init=.)?) // call declinit if we have init expr
        {if ( $init!=null && $init.evalType()!=null )
             Type$.MODULE$.promoteDeclExprType($ID, init, listener);}
    ;    

ret :   ^('return' v=.) {Type$.MODULE$.promoteReturnExprType((MethodSymbol)$start.symbol().get(), v, listener);} ;

assignment // don't walk expressions, just examine types
    :   ^('=' lhs=. rhs=.) {Type$.MODULE$.promoteAssignExprType(lhs, rhs, listener);}
    ;

// type computations and checking

exprRoot // invoke type computation rule after matching EXPR
    :   ^(EXPR expr) {$EXPR.evalType_$eq(Some.apply($expr.type);} // annotate AST
    ;

expr returns [Type type]
@after { $start.evalType = $type; }
    :   'true'      {$type = Type$.MODULE$.tBoolean();}
    |   'false'     {$type = Type$.MODULE$.tBoolean();}
    |   CHAR        {$type = Type$.MODULE$.tChar();}
    |   INT         {$type = Type$.MODULE$.tInt();}
    |   FLOAT       {$type = Type$.MODULE$.tFloat();}
    |   ID          {Option<Symbol> s = $ID.scope().get().resolve($ID.text);
                     $ID.symbol_$eq(s);
                     retval.type = s.get().typ();}
    |   ^(UNARY_MINUS a=expr)   {$type=Type$.MODULE$.unaryMinus($a.start);}
    |   ^(UNARY_NOT a=expr)     {$type=Type$.MODULE$.unaryNot($a.start);}
    |   member      {$type = $member.type;}
    |   arrayRef    {$type = $arrayRef.type;}
    |   call        {$type = $call.type;}
    |   binaryOps   {$type = $binaryOps.type;}
    ;

binaryOps returns [Type type]
@after { $start.evalType = $type; }
	:	(	^(bop a=expr b=expr)    {$type=Type$.MODULE$.binaryOp($a.start, $b.start);}
		|	^(relop a=expr b=expr)  {$type=Type$.MODULE$.relationalOp($a.start, $b.start);}
		|	^(eqop a=expr b=expr)   {$type=Type$.MODULE$.equalityOp($a.start, $b.start);}
		)
	;

arrayRef returns [Type type]
	:	^(INDEX ID expr)
		{
		$type = symtab.arrayIndex($ID, $expr.start);
        $start.evalType = $type;
		}
	;

call returns [Type type]
@init {List args = new ArrayList();}
	:	^(CALL ID ^(ELIST (expr {args.add($expr.start);})*))
		{
		$type = Type$.MODULE$.arrayIndex($ID, $expr.start);
        $start.evalType_$eq(Some.apply($type));
		}
    ;

member returns [Type type]
	:	^('.' expr ID)	
		{
        $type = Type$.MODULE$.member($expr.start, $ID);
        $start.evalType_$eq(Some.apply($type)); // save computed type
		}
    ;

bop	:	'+' | '-' | '*' | '/' ;

relop:	'<' | '>' | '<=' | '>=' ;

eqop:	'!=' | '==' ;
