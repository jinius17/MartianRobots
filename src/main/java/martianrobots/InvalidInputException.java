package martianrobots;

final class InvalidInputException extends RuntimeException {
    InvalidInputException(String message) {
        super(message, null, false, false);
    }
}
