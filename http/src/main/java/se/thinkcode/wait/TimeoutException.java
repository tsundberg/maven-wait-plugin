package se.thinkcode.wait;

class TimeoutException extends RuntimeException {
    TimeoutException(String message) {
        super(message);
    }
}
