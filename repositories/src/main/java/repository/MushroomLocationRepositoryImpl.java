package repository;

import static util.ConstantUtil.HARVEST_LOCATION_GSI;
import static util.ConstantUtil.MUSHROOM_LOCATION_GSI;
import static util.ConstantUtil.MUSHROOM_LOCATION_TABLE_NAME;
import static util.ConstantUtil.PARTITION_KEY_ATTRIBUTE;
import static util.ConstantUtil.REGION;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.Mushroom;
import org.apache.logging.log4j.LogManager;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class MushroomLocationRepositoryImpl implements MushroomLocationRepository {

  // TODO - create exception handling
  //      - implement batching and pagination actions
  //      - implement conditional update expressions
  //      - implement querying/scanning
  //      - implement ddb enhance client actions
  //      - refactor secret handling


  private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("MushroomLocationRepositoryImpl");
  private final String tableName;
  private final String firstGlobalIndexName;
  private final String secondGlobalIndexName;
  private final DynamoDbClient dynamodb;

  public MushroomLocationRepositoryImpl() {
    this.tableName = MUSHROOM_LOCATION_TABLE_NAME;
    this.firstGlobalIndexName = MUSHROOM_LOCATION_GSI;
    this.secondGlobalIndexName = HARVEST_LOCATION_GSI;

    this.dynamodb = getClient(Region.of(REGION));
  }

  /**
   * Only used for test.
   */
  public MushroomLocationRepositoryImpl(DynamoDbClient client, String tableName,
      String firstGlobalIndexName, String secondGlobalIndexName) {
    this.dynamodb = client;
    this.tableName = tableName;
    this.firstGlobalIndexName = firstGlobalIndexName;
    this.secondGlobalIndexName = secondGlobalIndexName;
  }

  @Override
  public void save(Mushroom mushroom) {
    LOGGER.debug("Attempting to save Mushroom with partition key: " + mushroom.pk());
    dynamodb.putItem(convertToPutItemRequest(mushroom));
  }

  @Override
  public void saveBatch(List<String> pks) throws NotImplementedException {
    throw new NotImplementedException("Needs implementation");
  }

  @Override
  public Optional<Mushroom> findByPartitionKey(String pk) {
    LOGGER.debug("Attempting to find Mushroom with partition key: " + pk);
    return Optional.of(dynamodb.getItem(GetItemRequest.builder()
                .tableName(this.tableName)
                .key(bundlePartitionKeyWithValue(pk))
                .build())
            .item())
        .stream()
        .filter(this::isPresent)
        .map(this::convertToMushroom)
        .findFirst();
  }

  @Override
  public List<Optional<Mushroom>> findByPositionBatch(String position) throws NotImplementedException {
    throw new NotImplementedException("Needs implementation");
  }

  @Override
  public List<Optional<Mushroom>> findByForagedBatch(String foraged) throws NotImplementedException {
    throw new NotImplementedException("Needs implementation");
  }

  public void update(Mushroom mushroom) {
    LOGGER.debug("Attempting to update Mushroom with partition key: " + mushroom.pk());
    dynamodb.updateItem(UpdateItemRequest.builder()
        .tableName(this.tableName)
        .key(bundlePartitionKeyWithValue(mushroom.pk()))
        .attributeUpdates(convertMushroomToAttributes(mushroom))
        .build());
  }

  private Map<String, AttributeValueUpdate> convertMushroomToAttributes(Mushroom mushroom) {
    return bundleAttributes(mushroom.name(), mushroom.position(), mushroom.foraged().toString());
  }

  private Map<String, AttributeValueUpdate> bundleAttributes(String name, String position, String foraged) {
    HashMap<String, AttributeValueUpdate> newAttributes = new HashMap<>();
    newAttributes.put("name", AttributeValueUpdate.builder()
        .value(AttributeValue.builder().s(name).build())
        .action(AttributeAction.PUT)
        .build());
    newAttributes.put("position", AttributeValueUpdate.builder()
        .value(AttributeValue.builder().s(position).build())
        .action(AttributeAction.PUT)
        .build());
    newAttributes.put("foraged", AttributeValueUpdate.builder()
        .value(AttributeValue.builder().s(foraged).build())
        .action(AttributeAction.PUT)
        .build());
    return newAttributes;
  }

  @Override
  public void deleteByPartitionKey(String pk) {
    LOGGER.debug("Attempting to delete Mushroom with partition key: " + pk);
    dynamodb.deleteItem(DeleteItemRequest.builder()
        .tableName(this.tableName)
        .key(bundlePartitionKeyWithValue(pk))
        .build());
  }

  private PutItemRequest convertToPutItemRequest(Mushroom mushroom) {
    return PutItemRequest.builder()
        .tableName(this.tableName)
        .item(convertAttributesToItem(mushroom))
        .build();
  }

  private HashMap<String, AttributeValue> bundlePartitionKeyWithValue(String pk) {
    HashMap<String, AttributeValue> bundle = new HashMap<>();
    bundle.put(PARTITION_KEY_ATTRIBUTE, AttributeValue.builder().s(pk).build());
    return bundle;
  }

  private boolean isPresent(Map<String, AttributeValue> item) {
    return !item.isEmpty();
  }

  private Mushroom convertToMushroom(Map<String, AttributeValue> item) {
    return new Mushroom(
        item.get("pk").s(),
        item.get("name").s(),
        item.get("position").s(),
        LocalDate.parse(item.get("foraged").s()));
  }

  private HashMap<String, AttributeValue> convertAttributesToItem(Mushroom entity) {
    HashMap<String, AttributeValue> item = new HashMap<>();
    item.put("pk", AttributeValue.builder().s(entity.pk()).build());
    item.put("name", AttributeValue.builder().s(entity.name()).build());
    item.put("position", AttributeValue.builder().s(entity.position()).build());
    item.put("foraged", AttributeValue.builder().s(entity.foraged().toString()).build());
    return item;
  }

  private DynamoDbClient getClient(Region region) {
    return DynamoDbClient.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider
            .create(AwsBasicCredentials
                .create("accessKeyId", "secretKey")))
        .build();
  }
}