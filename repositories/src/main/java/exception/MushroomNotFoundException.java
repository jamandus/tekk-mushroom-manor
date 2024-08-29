package exception;

public class MushroomNotFoundException extends RuntimeException {

  public MushroomNotFoundException(String message) {
    super(message);
  }
}