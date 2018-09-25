package program;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.hamcrest.core.Is;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class ParserTest extends BaseIntegrationTest {

    @Test
    public void shouldRecognizeMainMethod() throws Exception {

        String sourceCode = getSourceCodeFrom("/mainMethod.jm");

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeMainMethodWithStatements() throws Exception {
        String sourceCode = "main() {" +
                "int a; a = 5; print a;" +
                " }\n";

        assertErrorCountFor(sourceCode).is(0);
    }


//    @Test
//    public void shouldNotRecognizeBadMainClass() throws Exception {
//        String sourceCode = "class Program public { static void main(String [] args) { }}\n";
//
//        assertErrorCountFor(sourceCode).is(1);
//    }
//
//    @Test
//    public void shouldRecognizeClassDecl() throws Exception {
//        String sourceCode = "class Program { public static void main(String [] args) { }}" +
//                "class Sample { int a; int b;}\n";
//
//        assertErrorCountFor(sourceCode).is(0);
//    }

    @Test
    public void shouldRecognizeMethodDecl() throws Exception {
        String sourceCode = "main() { } \n" +
                "int foo(int x, int y)\n { return x + y; } } \n";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldNotRecognizeInvalidMethodDecl() throws Exception {
        String sourceCode = "main() { } \n" +
                "int foo int x, int y)\n { return x + y; } } \n";


        assertErrorCountFor(sourceCode).is(1);
    }

    @Test
    public void shouldComplainForMissingType() throws RecognitionException {
        String sourceCode = "main() { a = 5; }\n" +
                "int foo() {int a; b;}";

        assertErrorCountFor(sourceCode).is(1);
    }

    @Test
    public void shouldComplainForBadType() throws Exception {

        String sourceCode = "main() { int a; a = 5; }\n" +
                "int foo() { unknown a; int b; return b; }";

        assertErrorCountFor(sourceCode).is(4);
    }

    @Test
    public void shouldComplainForMissingTypeInMethodParam() throws Exception {
        String sourceCode = "main() { } \n" +
                "int foo(x, int y)\n { return x + y; } \n";

        assertErrorCountFor(sourceCode).is(2);
    }

    @Test
    public void shouldRecognizeLessThanExpression() throws Exception {
        String sourceCode = "main() { } \n" +
                "int foo(int x, int y)\n" +
                " { if (x < y) \n" +
                "   x = 5; else x = 6;\n" +
                " return x + y; }  \n";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeLessThanExpression2() throws Exception {
        String sourceCode = "main() { } \n" +
                "int foo()\n" +
                " { if (x < y) \n" +
                "   x = 5; else x = 6;\n" +
                " return x + y; }  \n";

        assertErrorCountFor(sourceCode).is(0);
    }


    @Test
    public void shouldRecognizeIfStatement() throws Exception {
        String sourceCode = "main() { } \n" +
                "    int foo(int x, int y)\n " +
                "    { if (x + y) x = 5; " +
                "       else x = 6;" +
                "       return x;}";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeWhileStatement() throws Exception {
        String sourceCode = "main() { } \n" +
                "int foo(int x, int y)\n " +
                "    { while (x + y)  { x = 5; } " +
                "       return x;}";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeMethodCall() throws Exception {
        String sourceCode = "main() { foo(1, 2); } \n" +
                "int foo(int x, int y)\n " +
                "    { while (x + y)  { x = 5; } " +
                "       return x;}";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeMethodCallWithManyParams() throws Exception {
        String sourceCode = "main() { int a; a = 5; show(a, a + 5);}\n " +
                "int show(int x, int y) \n" +
                "{ print valA; print valB; return 0;}";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeEqualityExpression() throws Exception {
        String sourceCode = "main() { int a; a = 5; if (a == 5) show(a); else show(666);  } \n" +
                "int show(int x)\n " +
                "    { print x; " +
                "       return x;}";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeEqualityExpression2() throws Exception {
        String sourceCode = "main() { \n" +
                "int a; a = 5; \n" +
                "while (a < 10) \n{" +
                "if (a == 2)" +
                "show(a);" +
                "else show (0);" +
                "a = a + 1;" +
                "}" +
                "}\n " +
                "int show(int val) \n" +
                "{ print val; val = val + 100; return 0;}";

        assertErrorCountFor(sourceCode).is(0);
    }

    @Test
    public void shouldRecognizeVoidReturnType() throws Exception {

        String sourceCode = "main() { \n" +
                "show(5); \n" +
                "}" +
                "void show(int val) \n" +
                "{ print val;}";

        ANTLRStringStream input = new ANTLRStringStream(sourceCode);

        ProgramLexer lexer = new ProgramLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ProgramParserTestUtil parser = new ProgramParserTestUtil(tokens);

        ProgramParser.prog_return result = parser.prog();

        CommonTree t = ((CommonTree) result.getTree());

        String resultString = t.toStringTree();

        System.out.println(resultString);


        assertErrorCountFor(sourceCode).is(0);

    }

    @Test
    public void shouldRecognizeFibonacciProgram() throws Exception {

        String sourceCode = getSourceCodeFrom("/fibonacci.jm");

        assertErrorCountFor(sourceCode).is(0);
    }


    private ParserTestUtil assertErrorCountFor(String sourceCode) {
        return new ParserTestUtil(sourceCode);
    }

    private class ParserTestUtil {
        private String sourceCode;

        public ParserTestUtil(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        public void is(int errorCount) throws RecognitionException {

            ANTLRStringStream input = new ANTLRStringStream(sourceCode);

            ProgramLexer lexer = new ProgramLexer(input);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            ProgramParserTestUtil parser = new ProgramParserTestUtil(tokens);

            parser.prog();

            assertEquals(errorCount, parser.getErrCount());
        }
    }
}
