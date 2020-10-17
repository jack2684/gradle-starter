package core;

import com.google.common.annotations.VisibleForTesting;
import data.Order;
import data.OrderUpdate;
import data.Temp;
import lombok.Builder;
import lombok.Getter;
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

  LinkedList<Order> orders;

  static ShelfBuilder Builder = new Shelf.ShelfBuilder();

  /** ======= PUBLIC CONSTANTS ======= */
  public static Shelf createHotShelf() {
    return Shelf.builder()
        .name("Hot Shelf")
        .temp(Temp.HOT)
        .capacity(10)
        .shelfDecayModifier(1)
        .orders(new LinkedList<>())
        .build();
  }

  public static Shelf createColdShelf() {
    return Shelf.builder()
        .name("Cold Shelf")
        .temp(Temp.COLD)
        .capacity(10)
        .shelfDecayModifier(1)
        .orders(new LinkedList<>())
        .build();
  }

  public static Shelf createFrozenShelf() {
    return Shelf.builder()
        .name("Frozen Shelf")
        .temp(Temp.FROZEN)
        .capacity(10)
        .shelfDecayModifier(1)
        .orders(new LinkedList<>())
        .build();
  }

  public static Shelf createOverflowShelf() {
    return Shelf.builder()
        .name("Overflow Shelf")
        .temp(Temp.ANY)
        .capacity(15)
        .shelfDecayModifier(2)
        .orders(new LinkedList<>())
        .build();
  }

  /** ======= PUBLIC METHODS ======= */

  /** Whether this shelve has capacity left */
  public boolean hasCapacity() {
    return orders.size() < capacity;
  }

  /** Put order onto this shelf */
  public void put(Order order) {
    orders.offer(order);
  }

  /** Number of orders on this shelf */
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
        res.incPickedUp();
        continue; // Not keeping this order
      }

      final int shelfLife = order.getBasic().getShelfLife();
      final long orderAge = t - order.getOrderTime();
      final float decayRate = t - order.getBasic().getDecayRate();
      final float val =
          (shelfLife - orderAge - orderAge * decayRate * shelfDecayModifier) / shelfLife;

      // Float comparison, doesn't have to be exact zero
      log.debug("{} val: {}", order.getBasic().getName(), val);
      if (val < 1E-6) {
        res.incDiscarded();
        continue;
      }

      // If it neither picked up of exipred, keeping it in the shelf
      keeping.add(order);
    }
    orders = keeping;
    return res;
  }

  @VisibleForTesting
  boolean containsOrder(Order o) {
    for (Order order : orders) {
      if (o.equals(order)) {
        return true;
      }
    }
    return false;
  }
}
