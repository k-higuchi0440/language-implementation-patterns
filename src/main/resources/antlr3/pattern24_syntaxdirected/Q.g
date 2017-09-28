grammar Q;

@members {
    Interpreter interpreter;
    public QParser(TokenStream input, Interpreter interpreter) {
        this(input);
        this.interpreter = interpreter;
    }
}

program
    :   stat+
    ;

stat:   table
    |   insert
    |   assign
    |   query
    |   print
    ;

print:  'print' expr ';' {interpreter.print((ResultSet) $expr.value);}
    ;

// START: table
table
    :   'create' 'table' tbl=ID
        '(' 'primary' 'key' key=ID (',' columns+=ID)+ ')' ';'
        {interpreter.createTable($tbl.text, $key.text, JavaConverters.asScalaBuffer($columns));}
    ;
// END: table

// START: assign
assign : ID '=' expr ';'  {interpreter.store($ID.text, (ResultSet) $expr.value);} ;
// END: assign

// START: insert
insert
    : 'insert' 'into' ID 'set' setFields[interpreter.tables.get($ID.text)] ';'
      {interpreter.insertInto($ID.text, $setFields.row);}
    ;
// END: insert
    
// START: fields
setFields[Table t] returns [Row row]
@init { $row = new Row(t.columns()); } // set return value
    :   set[$row] (',' set[$row])*
    ;
set[Row row] // pass in Row we're filling in
    :   ID '=' expr {row.row.setValue($ID.text, new Value($expr.value.toString()));}
    ;
// END: fields
    
query returns [Object value]
    :   'select' columns+=ID (',' columns+=ID)* 'from' tbl=ID
        (   'where' key=ID '=' expr
            {
            if($expr instanceof String) {
              $expr = new Value((String) $expr);
            }
            $value = interpreter.select($tbl.text, JavaConverters.asScalaBuffer($columns), $key.text, (Value) $expr.value);
            }
        |
        {$value = interpreter.select($tbl.text, JavaConverters.asScalaBuffer($columns);}
        )
    ;

// START: expr
// Match a simple value or do a query
expr returns [Object value] // access as $expr.value in other rules
    :   ID      {$value = interpreter.load($ID.text);}
    |   INT     {$value = $INT.int;}
    |   STRING  {$value = $STRING.text;}
    |   query   {$value = $query.value;}
    ;
// END: expr

ID  :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;

INT :   '0'..'9'+ ;

STRING
    :   '\'' ~'\''* '\''
        {setText(getText().substring(1, getText().length()-1));}
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;
