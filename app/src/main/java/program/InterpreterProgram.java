package program;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import program.icode.SymbolTablePopulator;
import program.interpreter.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class InterpreterProgram {

    public static void main(String[] args) {
        try {

            String sourceFile = args[0];

            File inputFile = new File(sourceFile);
            if (!inputFile.exists()) {
                System.out.println("Input file '" + sourceFile +
                        "' does not exist.");
                System.out.println("USAGE: ...");
                return;
            }

            String sourceCode = getContents(inputFile);

            ProgramWalker symbolTablePopulator = getProgramWalkerFor(sourceCode);
            symbolTablePopulator.prog();

            Interpreter interpreter = new Interpreter();

            interpreter.setRoot(getASTRoot(sourceCode));

            interpreter.setSymTabStack(SymbolTablePopulator.instance().getSymTabStack());

            interpreter.interpret();

        } catch (RecognitionException e) {
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static ProgramWalker getProgramWalkerFor(String sourceCode) throws RecognitionException {
        ANTLRStringStream input = new ANTLRStringStream(sourceCode);

        ProgramLexer lexer = new ProgramLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ProgramParser parser = new ProgramParser(tokens);

        ProgramParser.prog_return result = parser.prog();

        CommonTree t = ((CommonTree) result.getTree());

        CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
        nodes.setTokenStream(tokens);

        return new ProgramWalker(nodes);
    }

    public static CommonTree getASTRoot(String sourceCode) throws RecognitionException {
        ANTLRStringStream input = new ANTLRStringStream(sourceCode);
        ProgramLexer lexer = new ProgramLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ProgramParser parser = new ProgramParser(tokens);
        ProgramParser.prog_return result = parser.prog();
        return ((CommonTree) result.getTree());
    }

    private static String getContents(File aFile) {
        StringBuilder contents = new StringBuilder();

        try {
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }

}
