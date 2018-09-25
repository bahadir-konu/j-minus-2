package program.icode;

import java.util.ArrayList;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class SymTabStack extends ArrayList<SymTab> {
    private int currentNestingLevel;  // current scope nesting level

    public SymTabStack() {
        this.currentNestingLevel = 0;
        add(new SymTab(currentNestingLevel));
    }

    public int getCurrentNestingLevel() {
        return currentNestingLevel;
    }

    public SymTab getLocalSymTab() {
        return get(currentNestingLevel);
    }

    public SymTabEntry enterLocal(String name, String type) {
        return get(currentNestingLevel).enter(name, type);
    }

    public SymTabEntry lookupLocal(String name) {
        return get(currentNestingLevel).lookup(name);
    }

    public SymTabEntry lookup(String name) {
        SymTabEntry foundEntry = null;

        // Search the current and enclosing scopes.
        for (int i = currentNestingLevel; (i >= 0) && (foundEntry == null); --i) {
            foundEntry = get(i).lookup(name);
        }

        return foundEntry;
    }

    public SymTab push() {
        SymTab symTab = new SymTab(++currentNestingLevel);
        add(symTab);

        return symTab;
    }

    public SymTab push(SymTab symTab) {
        ++currentNestingLevel;
        add(symTab);

        return symTab;
    }

    public SymTab pop() {
        SymTab symTab = get(currentNestingLevel);
        remove(currentNestingLevel--);

        return symTab;
    }


}
