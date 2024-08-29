package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.ConstantUtil.HARVEST_LOCATION_GSI;
import static util.ConstantUtil.MUSHROOM_LOCATION_GSI;
import static util.ConstantUtil.MUSHROOM_LOCATION_TABLE_NAME;

import java.time.LocalDate;
import java.util.Optional;
import model.Mushroom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class MushroomLocationRepositoryImplTest {
  // TODO - create exception handling and sad test cases
  //      - implement update expressions instead of updateItem action

  private static final Logger LOGGER = LogManager.getLogger("MushroomLocRepImplTest");
  private final DynamoDB dynamoDB;
  private final MushroomLocationRepositoryImpl unitUnderTest;

  public MushroomLocationRepositoryImplTest() {
    this.dynamoDB = new DynamoDB(MUSHROOM_LOCATION_TABLE_NAME);
    this.unitUnderTest = new MushroomLocationRepositoryImpl(dynamoDB.getClient(),
        MUSHROOM_LOCATION_TABLE_NAME, MUSHROOM_LOCATION_GSI, HARVEST_LOCATION_GSI);
  }

  @BeforeAll
  public void setup() {
    LOGGER.debug("Setting up MushroomLocRepImplTest testing suite");
  }

  @AfterAll
  public void teardown() {
    LOGGER.debug("Tearing down MushroomLocRepImplTest testing suite");
    dynamoDB.stop();
  }

  @Test
  public void listTable() {
    assertEquals(MUSHROOM_LOCATION_TABLE_NAME,
        dynamoDB.getClient().listTables().tableNames().stream()
            .findFirst()
            .orElse("other table name"));
  }


  @Test
  public void addMushroom_happyCase() {
    // Given
    Mushroom mushroom = new Mushroom("ABC123456789", "Porcini", "59.24944900540981,18.4383637108842", LocalDate.parse("2023-08-08"));

    // When
    unitUnderTest.save(mushroom);

    // Then
    assertTrue(unitUnderTest.findByPartitionKey(mushroom.pk()).isPresent());
    assertEquals("ABC123456789", unitUnderTest.findByPartitionKey(mushroom.pk()).get().pk());
  }

  @Test
  public void findItemByPartitionKey_found() {
    // Given
    Mushroom mushroom = new Mushroom("EDF123456789", "Black Truffle", "57.340921, 18.530928", LocalDate.parse("2023-08-17"));
    unitUnderTest.save(mushroom);

    // When
    Optional<Mushroom> result = unitUnderTest.findByPartitionKey(mushroom.pk());

    // Then
    assertTrue(result.isPresent());
    assertEquals("EDF123456789", result.get().pk());
  }

  @Test
  public void updateMushroom_happyCase() {
    // Given
    Mushroom initialMushroom = new Mushroom("GHI123456789", "Porcini", "59.24944900540981,18.4383637108842", LocalDate.parse("2023-08-08"));
    unitUnderTest.save(initialMushroom);

    Optional<Mushroom> mushroomBeforeUpdate = unitUnderTest.findByPartitionKey(initialMushroom.pk());
    Mushroom updatedMushroom = new Mushroom("GHI123456789", "Chanterelle", "59.24944900540981,18.4383637108842", LocalDate.parse("2023-08-09"));

    // When
    unitUnderTest.update(updatedMushroom);
    Optional<Mushroom> mushroomAfterUpdate = unitUnderTest.findByPartitionKey(initialMushroom.pk());

    // Then
    assertTrue(mushroomBeforeUpdate.isPresent());
    assertEquals("Porcini", mushroomBeforeUpdate.get().name());
    assertEquals("2023-08-08", mushroomBeforeUpdate.get().foraged().toString());

    assertTrue(mushroomAfterUpdate.isPresent());
    assertEquals("Chanterelle", mushroomAfterUpdate.get().name());
    assertEquals("2023-08-09", mushroomAfterUpdate.get().foraged().toString());
  }

  @Test
  public void deleteMushroom_happyCase() {
    // Given
    Mushroom mushroom = new Mushroom("ABC123456789", "Porcini", "59.24944900540981,18.4383637108842", LocalDate.parse("2023-08-08"));
    unitUnderTest.save(mushroom);

    // When
    unitUnderTest.deleteByPartitionKey(mushroom.pk());

    // Then
    assertFalse(unitUnderTest.findByPartitionKey(mushroom.pk()).isPresent());
  }
}
