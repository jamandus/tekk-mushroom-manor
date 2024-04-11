package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class MushroomEntity {

  @JsonProperty("pk")
  private String pk;
  @JsonProperty("position")
  private String position;
  @JsonProperty("name")
  private String name;
  @JsonProperty("poisonous")
  private boolean poisonous;

  public String getPk() {
    return pk;
  }

  public void setPk(String pk) {
    this.pk = pk;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isPoisonous() {
    return poisonous;
  }

  public void setPoisonous(boolean poisonous) {
    this.poisonous = poisonous;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MushroomEntity mushroomEntity = (MushroomEntity) o;
    return poisonous == mushroomEntity.poisonous && Objects.equals(pk, mushroomEntity.pk)
        && Objects.equals(position, mushroomEntity.position) && Objects.equals(name,
        mushroomEntity.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pk, position, name, poisonous);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Mushroom{");
    sb.append("id='").append(pk).append('\'');
    sb.append(", position='").append(position).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", poisonous=").append(poisonous);
    sb.append('}');
    return sb.toString();
  }
}