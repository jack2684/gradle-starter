package simulation;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Snapshot {
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

  /** @return Number of orders expired */
  public int getExpired() {
    return placements.getOrDefault(Place.EXPIRED, 0);
  }

  /** @return Number of orders got discarded */
  public int getTrash() {
    return placements.getOrDefault(Place.TRASH, 0);
  }

  /** @return Number of orders completed */
  public int getCompleted() {
    return getTrash() + getDelivery() + getExpired();
  }

  /** Take snapshot */
  public void inc(Place place, int cnt) {
    placements.put(place, cnt + placements.getOrDefault(place, 0));
  }
}
