package program;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import program.ProgramLexer;
import program.ProgramParser;
import program.ProgramWalker;

class Util {
    static ProgramWalker getProgramWalkerFor(String sourceCode) throws RecognitionException {
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
}