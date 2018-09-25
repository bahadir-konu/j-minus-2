package program;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import program.icode.SymbolTablePopulator;
import program.interpreter.Interpreter;
import program.interpreter.PrintStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static program.Util.getProgramWalkerFor;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class InterpreterTest extends BaseIntegrationTest {

    private PrintStream printStream;

    private Interpreter interpreter = new Interpreter();

    @Before
    public void before() {
        printStream = mock(PrintStream.class);

//        printStream = new PrintStream() {
//            @Override
//            public void println(Object o) {
//                System.out.println(o);
//            }
//        };

        interpreter.setPrintStream(printStream);
    }


    @Test
    public void shouldExecuteNestedMethodCalls3() throws Exception {

        interpret("/nestedMethod3.jm");

        verify(printStream, times(1)).println(10);
        verify(printStream, times(1)).println(666);
        verify(printStream, times(1)).println(2);
        verify(printStream, times(1)).println(0);
    }


    @Test
    public void shouldExecuteNestedMethodCalls2
            () throws Exception {

        interpret("/nestedMethod2.jm");

        verify(printStream, times(1)).println(10);
        verify(printStream, times(1)).println(0);
    }

    @Test
    public void shouldExecuteNestedMethodCalls
            () throws Exception {

        interpret("/nestedMethod1.jm");

        verify(printStream, times(1)).println(null);
    }


    @Test
    public void shouldExecuteComplexProgram
            () throws Exception {

        interpret("/complexProgram.jm");

        verify(printStream, times(1)).println(5);
        verify(printStream, times(1)).println(6);
        verify(printStream, times(1)).println(7);
    }

    @Test
    public void shouldExecuteEmptyMainMethod
            () throws Exception {
        interpret("/emptyMainMethod.jm");

        verifyZeroInteractions(printStream);
    }

    @Test
    public void shouldExecuteIfStatement
            () throws Exception {
        interpret("/ifStatement.jm");

        verify(printStream, times(1)).println(5);
    }

    @Test
    public void shouldExecuteMainMethod
            () throws Exception {

        interpret("/mainMethod.jm");

        verify(printStream, times(1)).println(5);
        verify(printStream, never()).println(10);
    }

    @Test
    public void shouldExecuteMainMethodWithIfStatement
            () throws Exception {

        interpret("/mainMethodWithIf.jm");

        verify(printStream, times(1)).println(0);
    }

    @Test
    public void shouldExecuteMethodCall
            () throws Exception {

        interpret("/methodCall.jm");

        verify(printStream, times(1)).println(5);
        verify(printStream, times(1)).println(10);
    }

    @Test
    public void shouldExecuteProgramWithEqualityExpression
            () throws Exception {
        interpret("/equality.jm");

        verify(printStream, times(1)).println(0);
    }

    @Test
    public void shouldExecuteWhileStatement
            () throws Exception {

        interpret("/while.jm");

        verify(printStream, times(1)).println(0);
        verify(printStream, times(1)).println(1);
        verify(printStream, times(1)).println(2);
        verify(printStream, times(1)).println(3);
        verify(printStream, never()).println(4);

    }

    @Test
    public void shouldRespectVariableScopes
            () throws Exception {
        interpret("/variableScopes.jm");

        verify(printStream, times(1)).println(0);

    }

    @Test
    public void shouldHandleVoidReturnValueType() throws Exception {

        interpret("/voidReturn.jm");

        verify(printStream, times(1)).println(5);

    }

    @Test
    public void shouldCalculateFibonacciNumbers() throws Exception {

        interpret("/fibonacci.jm");

        verify(printStream, times(1)).println(5);

    }



    private void interpret(String sourceFile) throws Exception {

        String sourceCode = getSourceCodeFrom(sourceFile);

        ProgramWalker symbolTablePopulator = getProgramWalkerFor(sourceCode);
        symbolTablePopulator.prog();

        interpreter.setRoot(Util.getASTRoot(sourceCode));
        interpreter.setSymTabStack(SymbolTablePopulator.instance().getSymTabStack());

        interpreter.interpret();

    }


}
