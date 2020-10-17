package core;

import com.google.common.annotations.VisibleForTesting;
import data.Order;
import data.OrderUpdate;
import data.Temp;
import lombok.Builder;
import simulation.Place;
import simulation.Snapshot;

/** ShelfManager in charge of order assignment */
@Builder
public class OrderManager {
  private final Shelf[] shelves;

  /**
   * return how many were discarded due to overflow.
   *
   * <p>TODO: optimize the efficiency if needed. (currently we got only 4 shelves, it's ok to just
   * loop through all of them)
   */
  public int assign(Order order) {
    // Put on a suitable shelf
    for (Shelf shelf : shelves) {
      if (shelf.typeMatch(order) && shelf.hasCapacity()) {
        shelf.put(order);
        return 0;
      }
    }

    // If no shelf available, put on overflow shelf
    int discarded = 0;
    for (Shelf shelf : shelves) {
      if (shelf.isOverflowShelf()) {
        while (!shelf.hasCapacity()) {
          shelf.randomDiscard();
          discarded++;
        }
        shelf.put(order);
        return discarded;
      }
    }
    return 0;
  }

  /**
   * @param t current time
   * @return
   */
  public Snapshot updateDeliveryAndExpired(long t) {
    OrderUpdate update = new OrderUpdate();
    Snapshot snapshot = new Snapshot(t);
    for (Shelf shelf : shelves) {
      update.merge(shelf.updateDeliveryAndExpired(t));
      snapshot.inc(temp2Place(shelf.getTemp()), shelf.size());
    }

    snapshot.inc(Place.DELIVERY, update.getPickedUp());
    snapshot.inc(Place.EXPIRED, update.getDiscarded());

    return snapshot;
  }

  public boolean isAllShelfEmpty() {
    for (Shelf shelf : shelves) {
      if (shelf.size() != 0) {
        return false;
      }
    }
    return true;
  }

  @VisibleForTesting
  int totalAssigned() {
    int cnt = 0;
    for (Shelf shelf : shelves) {
      cnt += shelf.size();
    }
    return cnt;
  }

  /** Tranlate shelf temperature into places */
  private Place temp2Place(Temp temp) {
    switch (temp) {
      case ANY:
        return Place.OVERFLOW_SHELF;
      case HOT:
        return Place.HOT_SHELF;
      case FROZEN:
        return Place.FROZEN_SHELF;
      case COLD:
        return Place.COLD_SHELF;
    }
    return Place.UNKNOWN;
  }


  @VisibleForTesting
  public static Shelf[] initShelves() {
    Shelf[] shelves = new Shelf[4];
    shelves[0] = Shelf.createHotShelf();
    shelves[1] = Shelf.createColdShelf();
    shelves[2] = Shelf.createFrozenShelf();
    shelves[3] = Shelf.createOverflowShelf();
    return shelves;
  }
}
