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
        .build();
  }

  /** Run simulation */
  public void run() {
    LinkedList<OrderBasic> orderQueue = new LinkedList<>(Arrays.asList(orders));

    try (ProgressBar pb = new ProgressBar("Simulation", orderQueue.size())) {
      int orderCompleted = 0;
      while (!orderQueue.isEmpty()) {
        // 1. Update states of shelves and orders based on t,
        // either the order is expired or picked up
        removeCompletedOrders(pb);

        // 2. Poll order based on ingestion rate, controlled by bucket
        pollAndAssignOrder(orderQueue);

        // 3. Increment t, and update bucket for next incoming orders
        timeInc();
      }
    }
  }

  /**
   * @return number of order completed
   * @param pb
   */
  private int removeCompletedOrders(ProgressBar pb) {
    int completed = manager.updatePickedUpAndExpired(t);
    pb.stepBy(completed);
    return completed;
  }

  @SneakyThrows
  private void timeInc() {
    t++;
    bucket += ingestionRate;
    Thread.sleep(1_000);
  }

  private void pollAndAssignOrder(LinkedList<OrderBasic> orderQueue) {
    while (bucket > 0) {
      OrderBasic next = orderQueue.poll();
      long pickupWait = ThreadLocalRandom.current().nextLong(4) + 2; // Wait for 2-6 sec for pickup
      Order order = Order.builder().basic(next).orderTime(t).pickupTime(t + pickupWait).build();
      manager.assign(order);
      bucket--;
    }
  }
}
