package program.interpreter;

public class ReturnValue extends Error {
    public Object value;

    public ReturnValue(Object value) {
        this.value = value;
    }

    public ReturnValue() {
        super("");
    }
}
