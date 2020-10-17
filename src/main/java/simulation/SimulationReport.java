package simulation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SimulationReport {
  @Getter
  List<Snapshot> snapshots = new ArrayList<>();

  public void addSnapshot(Snapshot snapshot) {
    snapshots.add(snapshot);
  }

  public Snapshot lastSnapshot() {
    return snapshots.isEmpty() ? new Snapshot(0) : snapshots.get(snapshots.size() - 1);
  }


}
