package program.icode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class SymTabEntry extends HashMap<SymTabKey, Object> {
    private String name;
    private SymTab symTab;                   // parent symbol table
    private String type;

    private Object value;
    private ArrayList<Integer> lineNumbers = new ArrayList<Integer>();

    public SymTabEntry(String name, String type, SymTab symTab) {
        this.name = name;
        this.symTab = symTab;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public SymTab getSymTab() {
        return symTab;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAttribute(SymTabKey key, Object value) {
        put(key, value);
    }

    public Object getAttribute(SymTabKey key) {
        return get(key);
    }

    public void appendLineNumber(int lineNumber) {
        lineNumbers.add(lineNumber);
    }

}
