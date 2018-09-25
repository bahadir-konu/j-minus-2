package program; /**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Before;
import org.junit.Test;
import program.icode.SymbolTablePopulator;

import static junit.framework.Assert.assertEquals;

public class TypeCheckerTest {

    @Before
    public void setup() {
        SymbolTablePopulator.instance().reset();
    }

    @Test
    public void shouldComplainForBooleanPlusInt_local() throws Exception {

        String sourceCode = "main() { int a; a = 5; }\n" +
                "                   int foo()" +
                "                    { boolean x; int y; return x + y; }";

        assertErrorCountFor(sourceCode).is(2);
    }

    @Test
    public void shouldComplainForBooleanPlusInt_methodParam() throws Exception {

        String sourceCode = "main() { }\n" +
                "                   int foo(boolean x, int y)" +
                "                    { return x + y; }";

        assertErrorCountFor(sourceCode).is(2);
    }


    @Test
    public void shouldComplainForUndefinedVariableInAssignment() throws Exception {

        String sourceCode = "main() { a = 5; }\n" +
                "                   int foo(int y)" +
                "                    { return y; }}";

        assertErrorCountFor(sourceCode).is(1);
    }

    @Test
    public void shouldComplainForUndefinedVariableInScope() throws Exception {

        String sourceCode = "main() { }\n" +
                "                  int foo(int y)" +
                "                    { return x + y; }}";

        assertErrorCountFor(sourceCode).is(2);
    }

    @Test
    public void shouldComplainForInvalidReturn() throws Exception {

        String sourceCode = "main() { }\n" +
                "                  void foo(int y)" +
                "                    { return y; }";

        assertErrorCountFor(sourceCode).is(1);

    }

    private class SpyErrorChannel implements ErrorChannel {
        private int complaintCount = 0;

        @Override
        public void complain(String message) {
            System.out.println(message);
            ++complaintCount;
        }

        public int getComplaintCount() {
            return complaintCount;
        }
    }

    private class TypeCheckerTestUtil {
        private String sourceCode;

        public TypeCheckerTestUtil(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        public void is(int errorCount) throws RecognitionException {
            getProgramWalker().prog();
            TypeChecker.instance().setSymTabStack(SymbolTablePopulator.instance().getSymTabStack());

            SpyErrorChannel errorChannel = new SpyErrorChannel();
            TypeChecker.instance().setErrorChannel(errorChannel);

            ProgramWalker typeCheckerWalker = getProgramWalker();
            typeCheckerWalker.setVisitor(TypeChecker.instance());


            typeCheckerWalker.prog();

            assertEquals(errorCount, errorChannel.getComplaintCount());
        }

        private ProgramWalker getProgramWalker() throws RecognitionException {
            ANTLRStringStream input = new ANTLRStringStream(sourceCode);

            ProgramLexer lexer = new ProgramLexer(input);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            ProgramParserTestUtil parser = new ProgramParserTestUtil(tokens);

            ProgramParser.prog_return result = parser.prog();

            CommonTree t = ((CommonTree) result.getTree());

            String resultString = t.toStringTree();

            System.out.println(resultString);

            CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
            nodes.setTokenStream(tokens);

            return new ProgramWalker(nodes);
        }
    }

    private TypeCheckerTestUtil assertErrorCountFor(String sourceCode) {
        return new TypeCheckerTestUtil(sourceCode);
    }

}
