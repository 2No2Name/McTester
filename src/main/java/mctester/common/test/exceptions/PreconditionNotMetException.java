package mctester.common.test.exceptions;

public class PreconditionNotMetException extends RuntimeException {
    public static PreconditionNotMetException INSTANCE = new PreconditionNotMetException();

    private PreconditionNotMetException() {
    }
}
