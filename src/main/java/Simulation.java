import lombok.Builder;
import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

@Builder
public class Simulation {
  OrderManager manager;
  OrderBasic[] orders;
  float ingestionRate;
  SimulationReport report;

  /** Time of the simulation */
  long t;

  /** How many orders are ready to be processed. This bucket will be filled as time is moving oon */
  float bucket;

  public static Simulation init(OrderManager manager, OrderBasic[] orders, float ingestionRate) {
    return Simulation.builder()
        .manager(manager)
        .orders(orders)
        .ingestionRate(ingestionRate)
        .t(0)
        .bucket(ingestionRate)
        .report(new SimulationReport())
        .build();
  }

  /** Run simulation */
  public void run() {
    LinkedList<OrderBasic> orderQueue = new LinkedList<>(Arrays.asList(orders));
    try (ProgressBar pb = new ProgressBar("Simulation", orderQueue.size())) {
      while (!orderQueue.isEmpty() || !manager.isAllShelfEmpty()) {
        // 1. Update states of shelves and orders based on t,
        // either the order is expired or picked up
        updateCompletedOrders(pb);

        // 2. Poll order based on ingestion rate, controlled by bucket
        pollAndAssignOrder(orderQueue);

        // 3. Increment t, and update bucket for next incoming orders
        timeInc();
      }
    }
  }

  public void printReport() {
//    String base = "%8s\t%8s\t%8s\t%8s\t%8s\t%8s\t%8s";
    String base = "%5s%12s%12s%15s%16s   |%10s%7s";
    System.out.printf(
        (base) + "%n",
        "TIME",
        SimulationReport.Place.HOT_SHELF,
        SimulationReport.Place.COLD_SHELF,
        SimulationReport.Place.FROZEN_SHELF,
        SimulationReport.Place.OVERFLOW_SHELF,
        SimulationReport.Place.DELIVERY,
        SimulationReport.Place.TRASH);
    for (SimulationReport.Snapshot snapshot : report.getSnapshots()) {
      System.out.printf(
          (base) + "%n",
          snapshot.getT(),
          snapshot.getPlacements().get(SimulationReport.Place.HOT_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.COLD_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.FROZEN_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.OVERFLOW_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.DELIVERY),
          snapshot.getPlacements().get(SimulationReport.Place.TRASH));
    }
  }

  /**
   * @return number of order completed
   * @param pb
   */
  private SimulationReport.Snapshot updateCompletedOrders(ProgressBar pb) {
    // Update all orders based on time t
    SimulationReport.Snapshot snapshot = manager.updateDeliveryAndExpired(t);

    // Accumulate the delivery and trash from last snapshot
    SimulationReport.Snapshot last = report.lastSnapshot();
    snapshot.inc(SimulationReport.Place.DELIVERY, last.getDelivery());
    snapshot.inc(SimulationReport.Place.TRASH, last.getTrash());
    report.addSnapshot(snapshot);

    pb.stepTo(snapshot.getDelivery() + snapshot.getTrash());
    return snapshot;
  }

  @SneakyThrows
  private void timeInc() {
    t++;
    bucket += ingestionRate;
//        Thread.sleep(1_000);
  }

  private void pollAndAssignOrder(LinkedList<OrderBasic> orderQueue) {
    while (bucket > 0 && !orderQueue.isEmpty()) {
      OrderBasic next = orderQueue.poll();
      long pickupWait = ThreadLocalRandom.current().nextLong(4) + 2; // Wait for 2-6 sec for pickup
      Order order = Order.builder().basic(next).orderTime(t).pickupTime(t + pickupWait).build();
      manager.assign(order);
      bucket--;
    }
  }
}
