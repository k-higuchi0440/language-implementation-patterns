// START: header
tree grammar Types;
options {
  tokenVocab = Cymbol;
  ASTLabelType = CymbolAST;
  filter = true;
}
@members {
    SymbolTable symtab;
    CymbolListener listener;
    public Types(TreeNodeStream input, SymbolTable symtab, CymbolListener listener) {
        this(input);
        this.symtab = symtab;
        this.listener = listener;
    }
}
// END: header

// START: root
bottomup // match subexpressions innermost to outermost
    :   exprRoot // only match the start of expressions (root EXPR)
    ;

exprRoot // invoke type computation rule after matching EXPR
    :   ^(EXPR expr) {EXPR1.evalType_$eq($expr.type);} // annotate AST
    ;
// END: root

// START: expr
expr returns [Type type]
@after { $start.evalType = $type; } // do after any alternative
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
// END: expr

// START: binaryOps
binaryOps returns [Type type]
@after { $start.evalType = $type; }
    :   ^(bop a=expr b=expr)   {$type=Type$.MODULE$.binaryOp($a.start, $b.start);}
    |   ^(relop a=expr b=expr) {$type=Type$.MODULE$.relationalOp($a.start, $b.start);}
    |   ^(eqop a=expr b=expr)  {$type=Type$.MODULE$.equalityOp($a.start, $b.start);}
    ;
// END: binaryOps

// START: arrayRef
arrayRef returns [Type type]
    :   ^(INDEX ID expr)
        {
        $type = Type$.MODULE$.arrayIndex($ID, $expr.start);
        $start.evalType_$eq(Some.apply($type)); // save computed type
        }
    ;
// END: arrayRef

// START: call
call returns [Type type]
@init {List args = new ArrayList();}
    :   ^(CALL ID ^(ELIST (expr {args.add($expr.start);})*))
        {
        $type = Type$.MODULE$.call($ID, args);
        $start.evalType_$eq(Some.apply($type));
        }
    ;
// END: call

// START: member
member returns [Type type]
    :   ^('.' expr ID)           // match expr.ID subtrees
        { // $expr.start is root of tree matched by expr rule
        $type = Type$.MODULE$.member($expr.start, $ID);
        $start.evalType_$eq(Some.apply($type)); // save computed type
        }
    ;
// END: member

bop :   '+' | '-' | '*' | '/' ;

relop:  '<' | '>' | '<=' | '>=' ;

eqop:   '!=' | '==' ;
