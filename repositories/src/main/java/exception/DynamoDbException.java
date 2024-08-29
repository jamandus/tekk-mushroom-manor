package exception;

public class DynamoDbException extends RuntimeException {

  public DynamoDbException(String message, Exception exception) {
    super(message, exception);
  }

  public DynamoDbException(Exception exception) {
    super(exception);
  }
}
