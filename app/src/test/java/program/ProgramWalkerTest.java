package program;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 * <p/>
 * Verify if AST visitor methods are invoked properly
 */
public class ProgramWalkerTest {

    @Test
    public void shouldVisitVariableDeclaration() throws Exception {
        SpyProgramVisitor programVisitor = new SpyProgramVisitor();

        String sourceCode = "main() { a = 5; }\n" +
                "                   int foo()" +
                "                    { boolean x; int y; return x + y; }";

        ProgramWalker walker = Util.getProgramWalkerFor(sourceCode);

        walker.setVisitor(programVisitor);

        walker.prog();

        assertEquals(2, programVisitor.varDeclCount);

    }

    @Test
    public void shouldVisitAddition() throws Exception {
        SpyProgramVisitor programVisitor = new SpyProgramVisitor();

        String sourceCode = "main() { a = 5; }\n" +
                "                   int foo()" +
                "                    { boolean x; int y; return x + y; }";

        ProgramWalker walker = Util.getProgramWalkerFor(sourceCode);

        walker.setVisitor(programVisitor);

        walker.prog();

        assertEquals(1, programVisitor.additionCount);

    }

    @Test
    public void shouldVisitPrintStatements() throws Exception {
        SpyProgramVisitor programVisitor = new SpyProgramVisitor();

        String sourceCode = "main() { \n" +
                                "show(a);" +
                            "}" +
                        "int show(int val) \n" +
                        "{ print val; return 0;}";


        ProgramWalker walker = Util.getProgramWalkerFor(sourceCode);

        walker.setVisitor(programVisitor);

        walker.prog();

        assertEquals(1, programVisitor.printStmCount);

    }


    private class SpyProgramVisitor extends ProgramVisitor {
        public int varDeclCount = 0;
        public int additionCount = 0;
        private int printStmCount = 0;

        @Override
        public void onAddition(String left, String right) {
            ++additionCount;
        }

        @Override
        public void onPrintStatement(Tree tree) {
            ++printStmCount;
        }

        @Override
        public void onVariableDecl(Tree id, String type) {
            ++varDeclCount;
        }


    }
}
