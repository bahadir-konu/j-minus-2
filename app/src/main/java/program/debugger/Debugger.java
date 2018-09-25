package program.debugger;

import program.interpreter.FunctionSpace;
import program.message.Message;
import program.message.MessageListener;
import program.message.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Stack;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class Debugger implements MessageListener {

    private Stack<FunctionSpace> stack = new Stack<FunctionSpace>();

    private HashSet<String> watchPoints = new HashSet<String>();
    private HashSet<Integer> breakpoints = new HashSet<Integer>();

    private boolean steppingMode = false;

    public Debugger(Stack<FunctionSpace> stack) {
        this.stack = stack;
    }

    @Override
    public void messageReceived(Message message) {

        MessageType type = message.getType();

        switch (type) {

            case SOURCE_LINE:
                int lineNumber = (Integer) message.getBody();

                if (steppingMode) {

                    System.out.println("At line: " + lineNumber);
                    askForCommand();

                } else if (isBreakpoint(lineNumber)) {
                    System.out.println("\n>>> Breakpoint at line " + lineNumber);
                    askForCommand();

                }
                break;
            case ASSIGN:
                onAssign(message);
                break;

        }
    }

    private void askForCommand() {
        System.out.print(">>> Command? ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String command = reader.readLine();

            if (command.equals("stack"))
                printStack();

            //TODO: support more commands
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onAssign(Message message) {
        Object body[] = (Object[]) message.getBody();
        String variableName = ((String) body[1]).toLowerCase();

        if (isWatchPoint(variableName)) {
            int lineNumber = (Integer) body[0];
            Object value = body[2];

            System.out.println("\n>>> At line " + lineNumber + ": " +
                    variableName + " := " + value.toString());
        }
    }

    private boolean isBreakpoint(int lineNumber) {
        return breakpoints.contains(lineNumber);
    }

    private void printStack() {

        for (FunctionSpace functionSpace : stack) {
            System.out.println(functionSpace.toString());
        }

    }

    public void setWatchPoint(String name) {
        watchPoints.add(name);
    }

    public void unsetWatchPoint(String name) {
        watchPoints.remove(name);
    }

    public boolean isWatchPoint(String name) {
        return watchPoints.contains(name);
    }

    public void setSteppingMode(boolean steppingMode) {
        this.steppingMode = steppingMode;
    }

    public void setBreakPoint(int line) {
        this.breakpoints.add(line);
    }
}