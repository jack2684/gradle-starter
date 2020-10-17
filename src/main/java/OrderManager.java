import lombok.Builder;

/** ShelfManager in charge of order assignment */
@Builder
public class OrderManager {
  private final Shelf[] shelves;

  /**
   * return how many were discarded due to overflow
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
  public SimulationReport.Snapshot updateDeliveryAndExpired(long t) {
    OrderUpdate update = new OrderUpdate();
    SimulationReport.Snapshot snapshot = new SimulationReport.Snapshot(t);
    for (Shelf shelf : shelves) {
      update.merge(shelf.updateDeliveryAndExpired(t));
      snapshot.inc(temp2Place(shelf.getTemp()), shelf.size());
    }

    snapshot.inc(SimulationReport.Place.DELIVERY, update.pickedUp);
    snapshot.inc(SimulationReport.Place.EXPIRED, update.discarded);

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

  SimulationReport.Place temp2Place(Temp temp) {
    switch (temp) {
      case ANY:
        return SimulationReport.Place.OVERFLOW_SHELF;
      case HOT:
        return SimulationReport.Place.HOT_SHELF;
      case FROZEN:
        return SimulationReport.Place.FROZEN_SHELF;
      case COLD:
        return SimulationReport.Place.COLD_SHELF;
    }
    return SimulationReport.Place.UNKNOWN;
  }
}
