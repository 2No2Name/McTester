package mctester.common.test.exceptions;

public class NotEvaluatedException extends RuntimeException {
    public static NotEvaluatedException INSTANCE = new NotEvaluatedException();

    private NotEvaluatedException() {
    }
}
