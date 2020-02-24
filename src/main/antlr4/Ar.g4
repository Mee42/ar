grammar Ar;

main: ((definition | typeDef) ';'?)*;


definition: IDENTIFIER '=' value;

typeDef: IDENTIFIER '=>' type;

type: type '->' type    # functionalType
    | TYPE_IDENTIFIER   # rawType
    | '(' type ')'      # parenthesesType;


value: INTEGER            # integerValue
     | IDENTIFIER         # variableCallValue
     | value value        # functionApplicationValue
     | '(' value ')'      # parenthesesValue
     | lambda             # lambdaValue
     ;

lambda: '(' IDENTIFIER+ '=>' type  '\\' value ')';


INTEGER: [0-9]+; // for now, just integers

IDENTIFIER: ([a-z][a-zA-Z0-9_:]*)|([+\-*/=><^@#!]+);


TYPE_IDENTIFIER: [A-Z][a-zA-Z0-9_]*;


WHITESPACE : [ \t\n\r]+ -> skip;
