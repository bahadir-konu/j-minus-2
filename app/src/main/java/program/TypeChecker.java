package program;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import program.icode.SymTabEntry;
import program.icode.SymTabStack;
import program.interpreter.MemorySpace;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class TypeChecker extends ProgramVisitor {
    private static TypeChecker instance = new TypeChecker();

    private ErrorChannel errorChannel;
    private SymTabStack symTabStack;

    public static TypeChecker instance() {
        return instance;
    }

    private TypeChecker() {
    }

    @Override
    public void onAddition(String left, String right) {
        SymTabEntry leftEntry = symTabStack.lookup(left);
        SymTabEntry rightEntry = symTabStack.lookup(right);

        checkAddition(leftEntry, rightEntry);


    }

    private void checkAddition(SymTabEntry left, SymTabEntry right) {
        //TODO: Name of symbol and it's location should be reported too.
        if (left == null)
            errorChannel.complain("Undefined symbol!");
        if (right == null)
            errorChannel.complain("Undefined symbol!");

        if (right == null || left == null)
            return;

        String expression = String.format("(%s+%s)", left.getName(), right.getName());

        if (!left.getType().equals("int"))
            errorChannel.complain(
                    String.format("Left of %s must be of type int", expression));

        if (!right.getType().equals("int"))
            errorChannel.complain(
                    String.format("Right of %s must be of type int", expression));

    }

    @Override
    public void onAssignStatement(Tree tree) {

        SymTabEntry leftEntry = symTabStack.lookup(tree.getChild(0).getText());

        if (leftEntry == null)
            errorChannel.complain(
                    String.format("Undefined variable:  %s", tree.getChild(0).getText()));

    }

    @Override
    public void onFuncReturn(Tree funcReturn) {

        String actualReturnType = findType(funcReturn.getChild(0));
        String methodReturnType = funcReturn.getParent().getParent().getChild(0).getText();

        if (actualReturnType == null) {
            errorChannel.complain(String.format("Cannot find type of %s", funcReturn.getChild(0).getText()));
            return;
        }

        if (!actualReturnType.equals(methodReturnType))
            errorChannel.complain(String.format("Invalid return type! Expected: {%s} But was: {%s}", methodReturnType, actualReturnType));

    }

    private String findType(Tree statement) {
        //TODO: what about boolean?

        switch (statement.getType()) {
            case ProgramWalker.INT:
                return "int";
            case ProgramWalker.ID:
                SymTabEntry symTabEntry = symTabStack.lookup(statement.getText());
                if (symTabEntry == null)
                    return null;
                return symTabEntry.getType();
            case ProgramWalker.ADD:
                Tree left = statement.getChild(0);
                return findType(left);
            case ProgramWalker.LT:
                Tree lhs = statement.getChild(0);
                return symTabStack.lookup(lhs.getText()).getType();
            case ProgramWalker.EQ:
                Tree eleft = statement.getChild(0);
                return findType(eleft);
        }

        return null;

    }

    public void setErrorChannel(ErrorChannel errorChannel) {
        this.errorChannel = errorChannel;
    }

    public void setSymTabStack(SymTabStack symTabStack) {
        this.symTabStack = symTabStack;
    }
}
