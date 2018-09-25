tree grammar ProgramWalker;

options {
tokenVocab=Program; 
ASTLabelType=CommonTree;
}

@header {
package program;
import program.icode.SymbolTablePopulator;
}

@members {
ProgramVisitor visitor = SymbolTablePopulator.instance();

public void setVisitor(ProgramVisitor visitor) {
    this.visitor = visitor;
}

}

prog: ^(PROG mainMethod methodDecl*)
;

mainMethod:
    ^(MAINMETHOD statement*) {visitor.onMainMethod($MAINMETHOD);}
;

varDecl: ^(VARDECL type ID) {visitor.onVariableDecl($ID, $type.text);}
;

methodDecl
@init {visitor.beforeMethod();}
@after {visitor.afterMethod();}
: ^(METHOD returnType ID formalParameter* block) {visitor.onMethodName($ID);}
;

funcReturn: ^(FUNC_RETURN exp) {visitor.onFuncReturn($FUNC_RETURN);}
;

block: ^(BLOCK varDecl* statement*)
;

formalParameter
    : ^(ARG type ID)  { visitor.onMethodParam($ID.text, $type.text);}
;

statement: varDecl | assignStatement | printStatement | conditionalStatement
 | whileStatement | methodCallStatement | funcReturn
;

methodCallStatement: methodCallExp ';'
;

methodCallExp: ^(METHOD_CALL ID exp*) {visitor.onMethodCall($METHOD_CALL);}
;

conditionalStatement
@init {visitor.beforeIfStatement();}
@after {visitor.afterIfStatement();}
: ^(IF exp statement ^(ELSE statement)) { visitor.onIfStatement($IF);}
;

whileStatement: ^(WHILE exp statement+) { visitor.onWhileStatement($WHILE);}
;

assignStatement: ^(ASSIGN ID exp ) { visitor.onAssignStatement($ID.parent);}
;

printStatement: ^(PRINT exp ) { visitor.onPrintStatement($PRINT);}
;

exp: ADD exp
;

equalityExp: ^(EQ left=exp right=exp )
{ visitor.onEquality($left.text, $right.text);}
;

additionExp: ^(ADD left=exp right=exp )
{ visitor.onAddition($left.text, $right.text);}
;

lessThanExp: ^(LT left=exp right=exp )
{ visitor.onLessThan($left.text, $right.text);}
;

type: 'int' | 'boolean'
;

returnType: type | 'void'
;
