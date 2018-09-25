package program;

import program.debugger.Debugger;
import program.icode.SymbolTablePopulator;
import program.interpreter.Interpreter;

import static program.Util.getProgramWalkerFor;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class DebuggerProgram {

    public static void main(String[] args) throws Exception {
        // TODO: How to send message at source *line* ? Not on every statement 

        String sourceCode = "main() {\n " +
                "int a; a = 5; \n" +
                "while (a < 8) {\n" +
                "show(a);\n" +
                "a = a + 1;\n" +
                "}\n" +
                "}\n " +
                "int show(int val) \n" +
                "{ " +
                "   print val;\n" +
                "   val = val + 100;" +
                "   return 0;" +
                "}";

        // Symbol table stuff
        ProgramWalker symbolTablePopulator = getProgramWalkerFor(sourceCode);
        symbolTablePopulator.prog();

        Interpreter interpreter = new Interpreter();

        interpreter.setRoot(Util.getASTRoot(sourceCode));
        interpreter.setSymTabStack(SymbolTablePopulator.instance().getSymTabStack());

        Debugger debugger = new Debugger(interpreter.getRuntimeStack());

        debugger.setSteppingMode(false);
        //debugger.setWatchPoint("val");

        debugger.setBreakPoint(10);

        interpreter.addListener(debugger);

        interpreter.interpret();              


    }

}
