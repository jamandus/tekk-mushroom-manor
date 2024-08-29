package repository;

import java.util.List;
import java.util.Optional;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.Mushroom;

public interface MushroomLocationRepository {

  void save(Mushroom mushroom);

  void saveBatch(List<String> pks) throws NotImplementedException;

  Optional<Mushroom> findByPartitionKey(String pk);

  List<Optional<Mushroom>> findByPositionBatch(String position) throws NotImplementedException;

  List<Optional<Mushroom>> findByForagedBatch(String foraged) throws NotImplementedException;

  void update(Mushroom mushroom);

  void deleteByPartitionKey(String pk);
}