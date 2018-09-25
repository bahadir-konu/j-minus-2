package com.jminus.ide;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import program.InterpreterProgram;
import program.ProgramLexer;
import program.ProgramParser;
import program.ProgramWalker;
import program.icode.SymbolTablePopulator;
import program.interpreter.Interpreter;
import program.interpreter.InterpreterException;
import program.interpreter.PrintStream;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class JMinusApplication extends Application implements MediatorListener {

    private MainWindow mainWindow;

    private VerticalLayout mainVerticalLayout;

    private TextArea inputText;
    private TextArea outputText;

    @Override
    public void init() {

        setTheme("runo");

        mainWindow = new MainWindow();

        mainWindow.getContent().setSizeFull();

        mainVerticalLayout = new VerticalLayout();

        Button interpretButton = new Button("Interpret");
        interpretButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                outputText.setValue("");

                String sourceCode = inputText.getValue().toString();

                ProgramWalker symbolTablePopulator = null;
                try {
                    interpret(sourceCode);
                } catch (RecognitionException e) {
                    outputText.setValue(e.getMessage());
                }
                catch (InterpreterException e) {
                    outputText.setValue(e.getMessage());
                }

            }
        });

        mainVerticalLayout.addComponent(interpretButton);

        outputText = new TextArea("Output");
        outputText.setSizeFull();
        outputText.setHeight("200px");

        inputText = new TextArea("Input");
        inputText.setSizeFull();
        inputText.setHeight("300px");

        mainVerticalLayout.addComponent(inputText);
        mainVerticalLayout.addComponent(outputText);

        mainWindow.setContent(mainVerticalLayout);

        mainWindow.addCollaborator(this);

        setMainWindow(mainWindow);
    }

    private void interpret(String sourceCode) throws RecognitionException {
        ProgramWalker symbolTablePopulator;
        symbolTablePopulator = getProgramWalkerFor(sourceCode);

        symbolTablePopulator.prog();

        Interpreter interpreter = new Interpreter();

        interpreter.setRoot(InterpreterProgram.getASTRoot(sourceCode));

        interpreter.setSymTabStack(SymbolTablePopulator.instance().getSymTabStack());

        interpreter.setPrintStream(new PrintStream(){
            @Override
            public void println(Object o) {
                outputText.setValue(outputText.getValue() + o.toString());
            }
        });

        interpreter.interpret();
    }

    @Override
    public MainWindow getMainWindow() {
        return mainWindow;
    }

    @Override
    public void handleEvent(UIEvent event) {

    }

    public ProgramWalker getProgramWalkerFor(String sourceCode) throws RecognitionException {
        ANTLRStringStream input = new ANTLRStringStream(sourceCode);

        ProgramLexer lexer = new ProgramLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ParserWebUtil parser = new ParserWebUtil(tokens, outputText);

        ProgramParser.prog_return result = parser.prog();

        CommonTree t = ((CommonTree) result.getTree());

        CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
        nodes.setTokenStream(tokens);

        return new ProgramWalker(nodes);
    }

}
