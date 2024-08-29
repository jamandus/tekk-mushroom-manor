package util;

public class ConstantUtil {

  public static final String REGION;
  public static final String DEFAULT_REGION = "eu-west-1";
  public static final String PARTITION_KEY_ATTRIBUTE = "pk";
  public static final String MUSHROOM_LOCATION_TABLE_NAME;
  public static final String HARVEST_LOCATION_GSI;
  public static final String MUSHROOM_LOCATION_GSI;


  static {
    REGION = getRegion();
    MUSHROOM_LOCATION_TABLE_NAME = getMushroomLocationTableName();
    HARVEST_LOCATION_GSI = getHarvestLocationIndexName();
    MUSHROOM_LOCATION_GSI = getMushroomLocationIndexName();
  }

  private ConstantUtil() {
  }

  private static String getRegion() {
    return getEnvironmentVariable("REGION", DEFAULT_REGION);
  }

  private static String getMushroomLocationTableName() {
    return getEnvironmentVariable("MUSHROOM_LOCATION_TABLE_NAME", "MushroomLocation");
  }

  private static String getHarvestLocationIndexName() {
    return getEnvironmentVariable("HARVEST_LOCATION_GSI", "HarvestLocationIndex");
  }

  private static String getMushroomLocationIndexName() {
    return getEnvironmentVariable("MUSHROOM_LOCATION_GSI", "MushroomLocationIndexName");
  }

  private static String getEnvironmentVariable(String variableName, String defaultVariable) {
    String variable = System.getenv(variableName);
    return variable != null ? variable : defaultVariable;
  }
}
