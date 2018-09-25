package program;

import org.antlr.runtime.tree.Tree;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public abstract class ProgramVisitor {

    public void beforeMethod() {
    }

    public void afterMethod() {
    }

    public void onMethodName(Tree method) {
    }

    public void onVariableDecl(Tree id, String type) {
    }

    public void onMethodParam(String id, String type) {
    }

    public void onAddition(String left, String right) {
    }

    public void onLessThan(String left, String right) {
    }

    public void onAssignStatement(Tree tree) {
    }

    public void onPrintStatement(Tree tree) {
    }

    public void beforeIfStatement() {
    }

    public void afterIfStatement() {
    }

    public void onIfStatement(Tree tree) {
    }

    public void onWhileStatement(Tree tree) {
    }

    public void onMethodCall(Tree tree) {

    }

    public void onMainMethod(Tree tree) {

    }

    public void onEquality(String left, String right) {

    }

    public void onFuncReturn(Tree funcReturn) {
        
    }

}
