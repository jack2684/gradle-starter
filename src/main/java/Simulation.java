import lombok.Builder;
import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
    try (ProgressBar pb =
        new ProgressBar(
            "Order Simulation",
            orders.length,
            100,
            System.err,
            ProgressBarStyle.COLORFUL_UNICODE_BLOCK,
            "",
            1,
            false,
            null,
            ChronoUnit.SECONDS,
            0L,
            Duration.ZERO)) {
      while (!orderQueue.isEmpty() || !manager.isAllShelfEmpty()) {
        // 1. Update states of shelves and orders based on t,
        // either the order is expired or picked up
        SimulationReport.Snapshot snapshot = updateCompletedOrders();

        // 2. Poll order based on ingestion rate, controlled by bucket
        snapshot = pollAndAssignOrder(orderQueue, snapshot);

        // 3. Accumulate data from previous snapshot
        storeSnapshot(snapshot, pb);

        // 4. Increment t, and update bucket for next incoming orders
        timeInc();
      }
    }
  }

  private void storeSnapshot(SimulationReport.Snapshot snapshot, ProgressBar pb) {
    // Accumulate the delivery and expiration from last snapshot
    SimulationReport.Snapshot last = report.lastSnapshot();
    snapshot.inc(SimulationReport.Place.DELIVERY, last.getDelivery());
    snapshot.inc(SimulationReport.Place.EXPIRED, last.getExpired());
    snapshot.inc(SimulationReport.Place.TRASH, last.getTrash());
    snapshot.inc(SimulationReport.Place.COMPLETED, snapshot.getCompleted());
    pb.stepTo(snapshot.getCompleted());
    report.addSnapshot(snapshot);
  }

  public void printReport() {
    String base = "%5s%12s%12s%15s%16s |%10s%10s%7s |%7s";
    System.out.printf(
        (base) + "%n",
        "TIME",
        SimulationReport.Place.HOT_SHELF,
        SimulationReport.Place.COLD_SHELF,
        SimulationReport.Place.FROZEN_SHELF,
        SimulationReport.Place.OVERFLOW_SHELF,
        SimulationReport.Place.DELIVERY,
        SimulationReport.Place.EXPIRED,
        SimulationReport.Place.TRASH,
        SimulationReport.Place.COMPLETED);
    for (SimulationReport.Snapshot snapshot : report.getSnapshots()) {
      System.out.printf(
          (base) + "%n",
          snapshot.getT(),
          snapshot.getPlacements().get(SimulationReport.Place.HOT_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.COLD_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.FROZEN_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.OVERFLOW_SHELF),
          snapshot.getPlacements().get(SimulationReport.Place.DELIVERY),
          snapshot.getPlacements().get(SimulationReport.Place.EXPIRED),
          snapshot.getPlacements().get(SimulationReport.Place.TRASH),
          snapshot.getPlacements().get(SimulationReport.Place.COMPLETED));
    }
  }

  /** @return number of order completed */
  private SimulationReport.Snapshot updateCompletedOrders() {
    // Update all orders based on time t
    SimulationReport.Snapshot snapshot = manager.updateDeliveryAndExpired(t);
    return snapshot;
  }

  @SneakyThrows
  private void timeInc() {
    t++;
    bucket += ingestionRate;
    Thread.sleep(100); // 10x of real world time
  }

  private SimulationReport.Snapshot pollAndAssignOrder(
      LinkedList<OrderBasic> orderQueue, SimulationReport.Snapshot snapshot) {
    while (bucket > 0 && !orderQueue.isEmpty()) {
      OrderBasic next = orderQueue.poll();
      long pickupWait = ThreadLocalRandom.current().nextLong(4) + 2; // Wait for 2-6 sec for pickup
      Order order = Order.builder().basic(next).orderTime(t).pickupTime(t + pickupWait).build();
      int overflowDiscard = manager.assign(order);
      snapshot.inc(SimulationReport.Place.TRASH, overflowDiscard);
      bucket--;
    }

    return snapshot;
  }
}
