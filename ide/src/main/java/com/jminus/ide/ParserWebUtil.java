package com.jminus.ide;

import com.vaadin.ui.TextArea;
import org.antlr.runtime.TokenStream;
import program.ProgramParser;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 * Date: Mar 26, 2012
 * Time: 3:08:54 PM
 */
public class ParserWebUtil extends ProgramParser {

    private TextArea outputText;

    public ParserWebUtil(TokenStream input, TextArea outputText) {
        super(input);
        this.outputText = outputText;
    }

    @Override
    public void emitErrorMessage(String msg) {
        outputText.setValue(outputText.getValue() + "\n" + msg);        

    }
}