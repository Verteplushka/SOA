package exception;

public class EmptyCitiesListException extends RuntimeException {
    public EmptyCitiesListException (String message) {
        super(message);
    }
}
