package util;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

public class ConstantUtil {

  public static final Region REGION;

  static {
    REGION = getRegion();
  }

  private ConstantUtil() {
  }

  private static Region getRegion() {
    return Region.getRegion(Regions.EU_WEST_1);
  }
}
