package model;

import java.time.LocalDate;
import java.util.Objects;

public record Mushroom(
    String pk,
    String name,
    String position,
    LocalDate foraged) {

  // TODO - null and blank constraints are debatable, update expressions and flow of architecture decides, but PK should at least be validated

  public Mushroom {
    if (Objects.isNull(pk) || pk.isBlank()) {
      throw new IllegalArgumentException("Partition key cannot be null or blank");
    }

    if (Objects.isNull(name) || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }

    if (Objects.isNull(position) || position.isBlank()) {
      throw new IllegalArgumentException("Position cannot be null or blank");
    }

    if (Objects.isNull(foraged)) {
      foraged = LocalDate.now();
    }
  }
}
