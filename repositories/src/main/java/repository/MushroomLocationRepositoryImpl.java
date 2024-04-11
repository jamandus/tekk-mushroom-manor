package repository;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ItemConversionException;
import model.MushroomEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.utils.StringUtils;
import util.ConstantUtil;

public class MushroomLocationRepositoryImpl implements MushroomLocationRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      MushroomLocationRepositoryImpl.class);

  private final String tableName;
  private final String globalIndexName;

  private final DynamoDB dynamoDb;
  private final AmazonDynamoDB dynamoDbClient;
  private final ObjectMapper objectMapper;

  public MushroomLocationRepositoryImpl() {
    this.tableName = System.getenv("MushroomLocation");
    this.globalIndexName = System.getenv("HarvestLocationIndex");

    this.dynamoDbClient = getClient(ConstantUtil.REGION);
    this.dynamoDb = new DynamoDB(dynamoDbClient);
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void saveMushroom(MushroomEntity mushroomEntity) {
    getTable().putItem(convertToItem(mushroomEntity));
  }

  private AmazonDynamoDB getClient(Region region) {
    return AmazonDynamoDBClientBuilder
        .standard()
        .withRegion(region.getName())
        .build();
  }

  private Table getTable() {
    return dynamoDb.getTable(getTableName());
  }

  private Index getIndex() {
    return dynamoDb.getTable(getTableName()).getIndex(getIndexName());
  }

  String getTableName() {
    if (StringUtils.isBlank(this.tableName)) {
      throw new IllegalArgumentException("Table name has not been defined");
    }
    return this.tableName;
  }

  String getIndexName() {
    if (StringUtils.isBlank(this.globalIndexName)) {
      throw new IllegalArgumentException("Global index name has not been defined");
    }
    return this.globalIndexName;
  }

  protected Item convertToItem(final MushroomEntity entity) {
    try {
      return Item.fromJSON(objectMapper.writeValueAsString(entity));
    } catch (JsonProcessingException e) {
      LOGGER.error(String.format("Could not convert to Item, error message: %s", e.getMessage()), e);
      throw new ItemConversionException("Converting from Mushroom to Item failed");
    }
  }
}