import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationReport {
  @Getter
  List<Snapshot> snapshots = new ArrayList<>();

  public void addSnapshot(Snapshot snapshot) {
    snapshots.add(snapshot);
  }

  public Snapshot lastSnapshot() {
    return snapshots.isEmpty() ? new Snapshot(0) : snapshots.get(snapshots.size() - 1);
  }

  @Getter
  static class Snapshot {
    public Snapshot(long t) {
      this.t = t;
      this.placements = new HashMap<>();
    }

    /** Timestamp of the snapshot */
    long t;

    /** Placements of orders */
    Map<Place, Integer> placements;

    /** @return Number of orders got delivered */
    public int getDelivery() {
      return placements.getOrDefault(Place.DELIVERY, 0);
    }

    /** @return Number of orders got discarded */
    public int getTrash() {
      return placements.getOrDefault(Place.TRASH, 0);
    }

    /** Take snapshot */
    public void inc(Place place, int cnt) {
      placements.put(place, cnt + placements.getOrDefault(place, 0));
    }
  }

  public enum Place {
    HOT_SHELF("HOT_SHELF"),
    COLD_SHELF("COLD_SHELF"),
    FROZEN_SHELF("FROZEN_SHELF"),
    OVERFLOW_SHELF("OVERFLOW_SHELF"),
    DELIVERY("DELIVERY"),
    TRASH("TRASH"),
    UNKNOWN("UNKNOWN");

    private final String text;

    /** @param text */
    Place(final String text) {
      this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return text;
    }
  }
}
