package repository;

import model.MushroomEntity;

public interface MushroomLocationRepository {

  void saveMushroom(MushroomEntity mushroomEntity);
}