package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Objects;

public record MushroomEntity(
    String pk,
    String name,
    String position,
    LocalDate foraged) {

  public MushroomEntity {
    if (Objects.isNull(pk) || pk.isBlank()) {
      throw new IllegalArgumentException("Partition key cannot be null or empty");
    }

    if (Objects.isNull(foraged)) {
      throw new IllegalArgumentException("Date cannot be null");
    }
  }
}
