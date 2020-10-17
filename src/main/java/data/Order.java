package data;

import lombok.Builder;
import lombok.Data;

/**
 * In addition to basic properties from {@link Order}, this also has additional time-related fields for simulation
 */
@Data
@Builder
public class Order {
  /**
   * Basic properties of an order
   */
  OrderBasic basic;

  /**
   * When was the ordered palced, in sec
   */
  long orderTime;

  /**
   * The time when courrier picks up this order
   */
  long pickupTime;
}
