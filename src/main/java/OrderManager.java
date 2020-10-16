import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * ShelfManager in charge of order assignment
 */
@AllArgsConstructor
public class OrderManager {
  private final Map<Temp, Shelf> shelves = new HashMap<>();

  /**
   * Keep track of current time, in sec. Starts with 0.
   */
  private int time = 0;
}
