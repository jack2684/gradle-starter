import lombok.Builder;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

@Builder
public class Shelf {
  String name;

  /** What temperature of food is allowed to put on this shelf */
  Temp temp;

  int capacity;

  /** Decay rate affected by shelf */
  int shelfDecayModifier;

  LinkedList<Order> orders = new LinkedList<>();

  public boolean hasCapacity() {
    return orders.size() <= capacity;
  }

  public void put(Order order) {
    orders.offer(order);
  }

  /**
   * Whether the shelf type fits the order tmp property
   *
   * @return
   */
  public boolean typeMatch(Order order) {
    return temp == order.getBasic().getTemp();
  }

  public boolean isOverflow() {
    return temp == Temp.ANY;
  }

  /**
   * Remove random order
   * @return
   */
  public Order randomDiscard() {
    return orders.remove(ThreadLocalRandom.current().nextInt(orders.size()));
  }

  static ShelfBuilder Builder = new Shelf.ShelfBuilder();

  public static final Shelf HOT_SHELF =
      Shelf.builder().name("Hot Shelf").temp(Temp.HOT).capacity(10).shelfDecayModifier(1).build();

  public static final Shelf COLD_SHELF =
      Shelf.builder().name("Cold Shelf").temp(Temp.COLD).capacity(10).shelfDecayModifier(1).build();

  public static final Shelf FROZEN_SHELF =
      Shelf.builder()
          .name("Frozen Shelf")
          .temp(Temp.FROZEN)
          .capacity(10)
          .shelfDecayModifier(1)
          .build();

  public static final Shelf OVERFLOW_SHELF =
      Shelf.builder()
          .name("Overflow Shelf")
          .temp(Temp.ANY)
          .capacity(15)
          .shelfDecayModifier(2)
          .build();
}
