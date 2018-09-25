package program.interpreter;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import program.ProgramWalker;
import program.icode.SymTabEntry;
import program.icode.SymTabKey;
import program.icode.SymTabStack;
import program.message.Message;
import program.message.MessageHandler;
import program.message.MessageListener;
import program.message.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class Interpreter {

    private static final String ARG_TOKEN = "ARG";

    private CommonTree root;
    private SymTabStack symTabStack;
    private PrintStream printStream =
            new PrintStream() {
                @Override
                public void println(Object o) {
                    System.out.println(o);
                }
            };

    private MemorySpace globals = new MemorySpace("globals");       // global memory
    private MemorySpace currentSpace = globals;


    private Stack<FunctionSpace> runtimeStack = new Stack<FunctionSpace>();

    private static MessageHandler messageHandler;
    private int currentLine = 0;

    static {
        messageHandler = new MessageHandler();
    }

    public void interpret() {

        block(root);

    }

    private void block(CommonTree statement) {

        List stats = statement.getChildren();

        if (stats == null)
            return;

        for (Object stat : stats) exec((CommonTree) stat);
    }

    private Object exec(CommonTree statement) throws InterpreterException {

        switch (statement.getType()) {
            case ProgramWalker.BLOCK:
            case ProgramWalker.MAINMETHOD:
                block(statement);
                break;
            case ProgramWalker.PRINT:
                print(statement);
                sendSourceLineMessage(statement);
                break;
            case ProgramWalker.VARDECL:
                currentSpace.put(statement.getChild(1).getText(), null);
                sendSourceLineMessage(statement);
            case ProgramWalker.METHOD:
                break;
            case ProgramWalker.INT:
                return Integer.parseInt(statement.getText());
            case ProgramWalker.ID:
                MemorySpace memorySpace = getSpaceWithSymbol(statement.getText());

                if (memorySpace == null) memorySpace = currentSpace;

                return memorySpace.get(statement.getText());
            case ProgramWalker.ADD:
                Tree left = statement.getChild(0);
                Tree right = statement.getChild(1);
                int leftValue = (Integer) exec((CommonTree) left);
                int rightValue = (Integer) exec((CommonTree) right);
                return leftValue + rightValue;
            case ProgramWalker.LT:
                Tree lhs = statement.getChild(0);
                Tree rhs = statement.getChild(1);
                int lhsValue = (Integer) exec((CommonTree) lhs);
                int rhsValue = (Integer) exec((CommonTree) rhs);
                if (lhsValue < rhsValue)
                    return 1;
                return -1;
            case ProgramWalker.EQ:
                Tree eleft = statement.getChild(0);
                Tree eright = statement.getChild(1);
                int eleftValue = (Integer) exec((CommonTree) eleft);
                int erightValue = (Integer) exec((CommonTree) eright);
                if (eleftValue == erightValue)
                    return 1;
                return -1;
            case ProgramWalker.ASSIGN:
                Tree aleft = statement.getChild(0);
                Tree aright = statement.getChild(1);
                Object value = exec((CommonTree) aright);

                MemorySpace space = getSpaceWithSymbol(aleft.getText());
                if (space == null) space = currentSpace; // create in current space
                space.put(aleft.getText(), value);         // store

                sendSourceLineMessage(statement);

                messageHandler.sendMessage(new Message(MessageType.ASSIGN, new Object[]{statement.getLine(),
                        aleft.getText(),
                        value}));
                break;
            case ProgramWalker.IF:
                Tree conditionExpression = statement.getChild(0);
                CommonTree ifStatement = (CommonTree) statement.getChild(1);
                Tree elseTree = statement.getChild(2);
                CommonTree elseStatement = (CommonTree) elseTree.getChild(0);

                Integer result = (Integer) exec((CommonTree) conditionExpression);

                sendSourceLineMessage(statement);

                if (result == 1) {
                    return exec(ifStatement);
                } else {
                    return exec(elseStatement);
                }
            case ProgramWalker.WHILE:
                handleWhileStatement(statement);
                sendSourceLineMessage(statement);
                break;
            case ProgramWalker.METHOD_CALL:
                sendSourceLineMessage(statement);
                return handleMethodCall(statement);
            case ProgramWalker.FUNC_RETURN:
                sendSourceLineMessage(statement);
                throw new ReturnValue(statement.getChild(0));

            default: // catch unhandled node types
                throw new InterpreterException("Node " +
                        statement.getText() + "<" + statement.getType() + "> not handled");
        }

        return null;
    }

    private void sendSourceLineMessage(CommonTree statement) {
        if (statement.getLine() > currentLine) {
            messageHandler.sendMessage(
                    new Message(MessageType.SOURCE_LINE, statement.getLine()));
            currentLine = statement.getLine();
        }
    }

    private void handleWhileStatement(CommonTree statement) {
        Tree conditionExpression = statement.getChild(0);
        List<Tree> statements = new ArrayList<Tree>();

        for (int i = 1; i < statement.getChildCount(); ++i)
            statements.add(statement.getChild(i));

        Integer result = (Integer) exec((CommonTree) conditionExpression);

        while (result == 1) {
            for (Tree statement2 : statements) {
                exec((CommonTree) statement2);
            }

            result = (Integer) exec((CommonTree) conditionExpression);
        }
    }

    private Object handleMethodCall(CommonTree statement) {

//get AST for method code
        String methodName = statement.getChild(0).toString();
        SymTabEntry methodEntry = symTabStack.lookup(methodName);
        Tree methodAST = (Tree) methodEntry.getAttribute(SymTabKey.METHOD_AST);

        FunctionSpace fspace = new FunctionSpace(methodEntry);
        MemorySpace saveSpace = currentSpace;
        currentSpace = fspace;

        // What are method params? You ll init them.
        List<Tree> params = new ArrayList<Tree>();
        for (int i = 0; i < methodAST.getChildCount(); ++i) {
            if (methodAST.getChild(i).getText().equals(ARG_TOKEN))
                params.add(methodAST.getChild(i));
        }

        // passed param values:
        List<Object> paramValues = new ArrayList<Object>();
        for (int i = 1; i < statement.getChildCount(); ++i) {
            Object value = exec((CommonTree) statement.getChild(i));
            paramValues.add(value);
        }

        // init params that are in scope of method
        for (int i = 0; i < params.size(); ++i) {

            String paramName = params.get(i).getChild(1).getText();
            fspace.put(paramName, paramValues.get(i));

        }

        CommonTree block = null;

        for (int i = 0; i < methodAST.getChildCount(); ++i) {
            if (methodAST.getChild(i).getType() == ProgramWalker.BLOCK)
                block = (CommonTree) methodAST.getChild(i);
        }

        Object result = null;
        try {
            runtimeStack.push(fspace);
            exec(block);
        }
        catch (ReturnValue returnValue) {
            result = exec((CommonTree) returnValue.value);
        }

        runtimeStack.pop();
        currentSpace = saveSpace;

        return result;
    }

    MemorySpace getSpaceWithSymbol(String id) {
        if (runtimeStack.size() > 0 && runtimeStack.peek().exists(id)) {
            return runtimeStack.peek();
        }

        if (globals.exists(id)) return globals;

        return null;
    }

    private void print(CommonTree statement) {

        CommonTree expr = (CommonTree) statement.getChild(0);
        printStream.println(exec(expr));

    }

    public void addListener(MessageListener listener) {
        messageHandler.addListener(listener);
    }

    public Stack<FunctionSpace> getRuntimeStack() {
        return runtimeStack;
    }

    public void setRoot(CommonTree root) {
        this.root = root;
    }

    public void setSymTabStack(SymTabStack symTabStack) {
        this.symTabStack = symTabStack;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }
}
