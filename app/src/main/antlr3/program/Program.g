grammar Program;

options {
backtrack=true;
output=AST;
ASTLabelType=CommonTree;
}

tokens {
MAINMETHOD;
VARDECL;
METHOD;
BLOCK;
ARG;
FUNC_RETURN;
METHOD_CALL;
PROG;
ADD;
}

@header {
package program;
}

@lexer::header {
package program;
}

prog: mainMethod methodDecl* -> ^(PROG mainMethod methodDecl*)
;

mainMethod:
   'main' LPAREN RPAREN block -> ^(MAINMETHOD block)
;

varDecl: type ID ';' -> ^(VARDECL type ID)
;

methodDecl: returnType ID LPAREN ( formalParameter (',' formalParameter)* )? RPAREN
block   -> ^(METHOD returnType ID formalParameter* block)
;

block: '{' varDecl* statement* '}' -> ^(BLOCK varDecl* statement*)
;

funcReturn: RETURN exp ';' -> ^(FUNC_RETURN exp)
;

formalParameter
   : type ID -> ^(ARG type ID)
;

whileStatement: WHILE LPAREN exp RPAREN '{' statement+ '}' -> ^(WHILE exp statement+)
;

conditionalStatement: IF parExp statement ELSE statement -> ^(IF parExp statement ^(ELSE statement) )
;

printStatement: PRINT exp ';' -> ^(PRINT exp )
;

assignStatement
	:	 exp ASSIGN exp ';' -> ^(ASSIGN exp exp)
;

statement
	:	 assignStatement | printStatement | conditionalStatement | whileStatement | funcReturn
;

exp
    :   equalityExpression
    ;

equalityExpression: relationalExpression (EQ relationalExpression)* -> ^(EQ relationalExpression+)
;

relationalExpression: additiveExpression (LT additiveExpression )* -> ^(LT additiveExpression+)
;

additiveExpression
    :   simpleExp ((PLUS | MINUS) simpleExp)* -> ^(ADD simpleExp+)
    ;

simpleExp : parExp -> parExp
 | ID (expList?)? -> ^(METHOD_CALL expList*)
 | INT -> INT
 ;

parExp: LPAREN exp RPAREN -> exp
;

expList: exp (',' exp)* -> exp+
;

type: 'int' -> 'int'
 | 'boolean' -> 'boolean'
;

returnType: type -> type
 | 'void' -> 'void'
;

LPAREN: '(';
RPAREN: ')';
ASSIGN: '=';
PLUS: '+';
MINUS: '-';
LT: '<';
EQ: '==';
IF: 'if';
WHILE: 'while';
ELSE: 'else';
RETURN: 'return';
PRINT: 'print';
ID : ('a'..'z' |'A'..'Z' )+ ;
INT : '0'..'9' + ;
WS : (' ' |'\t' |'\n' |'\r' )+ {skip();};


