import lombok.Builder;

/**
 * ShelfManager in charge of order assignment
 */
@Builder
public class OrderManager {
  private final Shelf[] shelves;

  public void assign(Order order) {
    for (Shelf shelf : shelves) {
      if (shelf.typeMatch(order) || shelf.isOverflowShelf()) {
        if (!shelf.hasCapacity()) {
          if (shelf.isOverflowShelf()) {
            shelf.randomDiscard();
          } else {
            continue; // Skip this shelf
          }
        }
        shelf.put(order);
      }
    }
  }

  /**
   * @param t current time
   * @return
   */
  public int updatePickedUpAndExpired(long t) {
    int cnt = 0;
    for (Shelf shelf : shelves) {
      cnt += shelf.updatePickedUpAndExpired(t);
    }
    return cnt;
  }
}
