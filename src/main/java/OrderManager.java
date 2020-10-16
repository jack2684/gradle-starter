import lombok.Builder;

/**
 * ShelfManager in charge of order assignment
 */
@Builder
public class OrderManager {
  private final Shelf[] shelves;

  public void assign(Order order) {
    for (Shelf shelf : shelves) {
      if (shelf.typeMatch(order) || shelf.isOverflow()) {
        if (!shelf.hasCapacity()) {
          if (shelf.isOverflow()) {
            shelf.randomDiscard();
          } else {
            continue; // Skip this shelf
          }
        }
        shelf.put(order);
      }
    }
  }
}
