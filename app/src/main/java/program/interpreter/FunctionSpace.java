package program.interpreter;

import program.icode.SymTabEntry;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com)
 */
public class FunctionSpace extends MemorySpace {
    private SymTabEntry functionEntry;
    public FunctionSpace(SymTabEntry functionEntry) {
		super(functionEntry.getName() + " runtime");
        this.functionEntry = functionEntry;
	}
}
