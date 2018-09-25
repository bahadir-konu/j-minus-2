package program.icode;

import java.util.TreeMap;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class SymTab extends TreeMap<String, SymTabEntry>
{
    private int nestingLevel;

    public SymTab(int nestingLevel)
    {
        this.nestingLevel = nestingLevel;
    }

    public int getNestingLevel()
    {
        return nestingLevel;
    }

    public SymTabEntry enter(String name, String type)
    {
        SymTabEntry entry = new SymTabEntry(name, type, this);
        put(name, entry);

        return entry;
    }

    public SymTabEntry lookup(String name)
    {
        return get(name);
    }
}
