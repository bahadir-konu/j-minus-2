package program;

import org.antlr.runtime.TokenStream;
import program.ProgramParser;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
class ProgramParserTestUtil extends ProgramParser {

    private int errCount = 0;

    public ProgramParserTestUtil(TokenStream input) {
        super(input);
    }

    @Override
    public void emitErrorMessage(String msg) {
        ++errCount;
        super.emitErrorMessage(msg);
    }

    public int getErrCount() {
        return errCount;
    }
}
