package repository;

import static java.util.Collections.singletonList;
import static util.ConstantUtil.HARVEST_LOCATION_GSI;
import static util.ConstantUtil.MUSHROOM_LOCATION_GSI;
import static util.ConstantUtil.REGION;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import exception.DynamoDbException;
import java.net.URI;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;


public class DynamoDB {
  //  TODO - refactor database schemas with dynamic data
  //       - confirm ACTIVE table status and return value
  //       - utilize CreateTableResponse or clean up method


  private static final Logger LOGGER = LogManager.getLogger("DynamoDB");
  private static final String HOST = "http://localhost";
  private static final String PORT = "8000";
  private DynamoDbClient client;
  private DynamoDBProxyServer server;


  public DynamoDB(final String tableName) {
    try {
      LOGGER.debug("Initializing DynamoDB server...");
      initializeServer();
      configureDynamoDbClient();
      createDatabaseSchemas(singletonList(tableName));
    } catch (Exception e) {
      LOGGER.error("Unable to start DynamoDB Test Server", e);
      throw new DynamoDbException(e);
    }
  }

  private void initializeServer() throws ParseException {
    System.setProperty("sqlite4java.library.path", "native-libs");
    this.server = ServerRunner.createServerFromCommandLineArgs(
        new String[]{"-inMemory", "-port", PORT, "-sharedDb"});
  }

  private void configureDynamoDbClient() {
    this.client = DynamoDbClient.builder()
        .credentialsProvider(StaticCredentialsProvider
            .create(AwsBasicCredentials
                .create("accessKeyId", "secretKey")))
        .endpointOverride(URI.create(HOST + ":" + PORT))
        .region(Region.of(REGION))
        .build();
  }

  private void createDatabaseSchemas(List<String> tables) {
    try {
      this.server.start();
      LOGGER.debug("Creating database tables...");
      tables.forEach(this::createTable);
    } catch (Exception e) {
      throw new DynamoDbException("Unable to start DynamoDB", e);
    }
  }

  public void createTable(DynamoDbClient client, String tableName, List<AttributeDefinition> attributes, List<KeySchemaElement> keys, GlobalSecondaryIndex... gsi) {
    throw new NotImplementedException("Needs implementation");
  }

  private void createTable(String tableName) {
    CreateTableRequest request = CreateTableRequest.builder()
        .tableName(tableName)
        .attributeDefinitions(
            AttributeDefinition.builder()
                .attributeName("pk")
                .attributeType(ScalarAttributeType.S)
                .build(),
            AttributeDefinition.builder()
                .attributeName("name")
                .attributeType(ScalarAttributeType.S)
                .build(),
            AttributeDefinition.builder()
                .attributeName("position")
                .attributeType(ScalarAttributeType.S)
                .build(),
            AttributeDefinition.builder()
                .attributeName("foraged")
                .attributeType(ScalarAttributeType.S)
                .build())
        .keySchema(KeySchemaElement.builder()
            .attributeName("pk")
            .keyType(KeyType.HASH)
            .build())
        .globalSecondaryIndexes(createIndexes())
        .provisionedThroughput(ProvisionedThroughput.builder()
            .readCapacityUnits(5L)
            .writeCapacityUnits(5L)
            .build())
        .build();
    try {
      CreateTableResponse response = this.client.createTable(request);
      waitForTableCreation(this.client.waiter(), tableName);

    } catch (DynamoDbException e) {
      LOGGER.error("Failed to create table", e);
    }
  }

  private List<GlobalSecondaryIndex> createIndexes() {
    return List.of(GlobalSecondaryIndex.builder()
            .indexName(MUSHROOM_LOCATION_GSI)
            .keySchema(KeySchemaElement.builder()
                    .attributeName("name")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("position")
                    .keyType(KeyType.RANGE)
                    .build())
            .projection(Projection.builder()
                .projectionType(ProjectionType.INCLUDE)
                .nonKeyAttributes("foraged")
                .build())
            .provisionedThroughput(ProvisionedThroughput.builder()
                .readCapacityUnits(5L)
                .writeCapacityUnits(5L)
                .build()).build(),
        GlobalSecondaryIndex.builder()
            .indexName(HARVEST_LOCATION_GSI)
            .keySchema(KeySchemaElement.builder()
                    .attributeName("position")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("foraged")
                    .keyType(KeyType.RANGE)
                    .build())
            .projection(Projection.builder()
                .projectionType(ProjectionType.INCLUDE)
                .nonKeyAttributes("name")
                .build())
            .provisionedThroughput(ProvisionedThroughput.builder()
                .readCapacityUnits(5L)
                .writeCapacityUnits(5L)
                .build()).build());
  }

  private void waitForTableCreation(DynamoDbWaiter waiter, String tableName) {
    WaiterResponse<DescribeTableResponse> waiterResponse = waiter
        .waitUntilTableExists(table -> table.tableName(tableName));

    waiterResponse.matched().response()
        .ifPresent(table -> LOGGER.debug(
            String.format("Table was created with name: %s and ARN: %s", table.table().tableName(),
                table.table().tableArn())));
  }

  public void stop() {
    try {
      this.server.stop();
      LOGGER.debug("DynamoDB stopped...");
    } catch (Exception e) {
      throw new DynamoDbException("DynamoDB failed to stop", e);
    }
  }

  public DynamoDbClient getClient() {
    return this.client;
  }
}
