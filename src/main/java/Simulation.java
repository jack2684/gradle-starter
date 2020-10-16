import lombok.Builder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

@Builder
public class Simulation {
  OrderManager manager;
  OrderBasic[] orders;
  int ingestionRate;

  /** Time of the simulation */
  long t;

  /** How many orders are ready to be processed. This bucket will be filled as time is moving oon */
  int bucket;

  public static Simulation init(OrderManager manager, OrderBasic[] orders, int ingestionRate) {
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

    while (!orderQueue.isEmpty()) {
      // 1. Poll order based on ingestion rate
      pollAndAssignOrder(orderQueue);

      // 2. Update states of shelves and orders based on t, either expire or picked up

      // 3. Increment t, and update bucket
      timeInc();
    }
  }

  private void timeInc() {
    t++;
    bucket += ingestionRate;
  }

  private void pollAndAssignOrder(LinkedList<OrderBasic> orderQueue) {
    while (bucket-- > 0) {
      OrderBasic next = orderQueue.poll();
      long pickupWait = ThreadLocalRandom.current().nextLong(4) + 2; // Wait for 2-6 sec for pickup
      Order order = Order.builder().basic(next).orderTime(t).pickupTime(t + pickupWait).build();
      manager.assign(order);
    }
  }
}
