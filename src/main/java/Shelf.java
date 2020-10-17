import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

@Builder
@ToString
@Slf4j
public class Shelf {
  String name;

  /** What temperature of food is allowed to put on this shelf */
  @Getter Temp temp;

  int capacity;

  /** Decay rate affected by shelf */
  int shelfDecayModifier;

  @Setter LinkedList<Order> orders;

  static ShelfBuilder Builder = new Shelf.ShelfBuilder();

  /** ======= PUBLIC CONSTANTS ======= */
  public static final Shelf HOT_SHELF =
      Shelf.builder()
          .name("Hot Shelf")
          .temp(Temp.HOT)
          .capacity(10)
          .shelfDecayModifier(1)
          .orders(new LinkedList<>())
          .build();

  public static final Shelf COLD_SHELF =
      Shelf.builder()
          .name("Cold Shelf")
          .temp(Temp.COLD)
          .capacity(10)
          .shelfDecayModifier(1)
          .orders(new LinkedList<>())
          .build();

  public static final Shelf FROZEN_SHELF =
      Shelf.builder()
          .name("Frozen Shelf")
          .temp(Temp.FROZEN)
          .capacity(10)
          .shelfDecayModifier(1)
          .orders(new LinkedList<>())
          .build();

  public static final Shelf OVERFLOW_SHELF =
      Shelf.builder()
          .name("Overflow Shelf")
          .temp(Temp.ANY)
          .capacity(15)
          .shelfDecayModifier(2)
          .orders(new LinkedList<>())
          .build();

  /** ======= PUBLIC METHODS ======= */

  /** Whether this shelve has capacity left */
  public boolean hasCapacity() {
    return orders.size() < capacity;
  }

  /** Put order onto this shelf */
  public void put(Order order) {
    orders.offer(order);
  }

  /**
   * Number of orders on this shelf
   */
  public int size() {
    return orders.size();
  }

  /** Whether the shelf type fits the order tmp property */
  public boolean typeMatch(Order order) {
    return temp == order.getBasic().getTemp();
  }

  /** Is this an overflow shelf */
  public boolean isOverflowShelf() {
    return temp == Temp.ANY;
  }

  /** Remove random order from this shelf */
  public Order randomDiscard() {
    return orders.remove(ThreadLocalRandom.current().nextInt(orders.size()));
  }

  /**
   * Remove orders that is picked up or expired
   *
   * @param t current time
   * @return number of orders that are removed
   */
  public OrderUpdate updateDeliveryAndExpired(long t) {
    LinkedList<Order> keeping = new LinkedList<>();
    OrderUpdate res = new OrderUpdate();
    for (Order order : orders) {
      if (t >= order.getPickupTime()) {
        res.pickedUp++;
        continue; // Not keeping this order
      }

      final int shelfLife = order.getBasic().getShelfLife();
      final long orderAge = t - order.getOrderTime();
      final float decayRate = t - order.getBasic().getDecayRate();
      final float val =
          (shelfLife - orderAge - orderAge * decayRate * shelfDecayModifier) / shelfLife;

      // Float comparison, doesn't have to be exact zero
      log.debug("{} val: {}" , order.getBasic().getName(), val);
      if (val < 1E-6) {
        res.discarded++;
        continue;
      }

      // If it neither picked up of exipred, keeping it in the shelf
      keeping.add(order);
    }
    orders = keeping;
    return res;
  }
}
