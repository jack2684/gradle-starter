import lombok.Builder;

/** ShelfManager in charge of order assignment */
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
  public SimulationReport.Snapshot updateDeliveryAndExpired(long t) {
    OrderUpdate update = new OrderUpdate();
    SimulationReport.Snapshot snapshot = new SimulationReport.Snapshot(t);
    for (Shelf shelf : shelves) {
      update.merge(shelf.updateDeliveryAndExpired(t));
      snapshot.inc(temp2Place(shelf.getTemp()), shelf.size());
    }

    snapshot.inc(SimulationReport.Place.DELIVERY, update.pickedUp);
    snapshot.inc(SimulationReport.Place.TRASH, update.discarded);

    return snapshot;
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
