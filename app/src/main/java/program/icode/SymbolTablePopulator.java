package program.icode;

import org.antlr.runtime.tree.Tree;
import program.ProgramVisitor;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class SymbolTablePopulator extends ProgramVisitor {
    private static SymbolTablePopulator instance = new SymbolTablePopulator();

    private SymTabStack symTabStack = new SymTabStack();

    public static SymbolTablePopulator instance() {
        return instance;
    }

    private SymbolTablePopulator() {
    }

    @Override
    public void beforeMethod() {
        symTabStack.push();
    }

    @Override
    public void onMethodName(Tree method) {
        symTabStack.enterLocal(method.getText(), "METHOD_DECL");
        SymTabEntry methodEntry = symTabStack.lookup(method.getText());
        methodEntry.setAttribute(SymTabKey.METHOD_AST, method.getParent());
    }

    @Override
    public void onVariableDecl(Tree id, String type) {
        SymTabEntry entry = symTabStack.enterLocal(id.getText(), type);
        entry.appendLineNumber(id.getLine());
    }

    @Override
    public void onMethodParam(String id, String type) {
        symTabStack.enterLocal(id, type);
    }

    
    public SymTabStack getSymTabStack() {
        return symTabStack;
    }

    public void reset() {
        symTabStack = new SymTabStack();
    }
}
