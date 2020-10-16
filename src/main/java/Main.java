import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public class Main {

  // Uncommente when need to debug
  // Logger.getRootLogger().setLevel(Level.DEBUG);

  /** By default, the system ingest 2 orders per second. */
  public static final int DEFAULT_INGESTION_RATE = 2;

  public static void main(String[] args) throws IOException {
    log.info("=========Reading CLI inputs=========");
    int ingestionRate = DEFAULT_INGESTION_RATE;
    if (args.length > 0) {
      ingestionRate = Integer.parseInt(args[0]);
    }
    log.info("Order ingestion rate: {}/sec", ingestionRate);

    log.info("=========Loading Orders=========");
    OrderBasic[] orders = loadOrders();
    log.info("Load orders done. Number of orders: {}", orders.length);

    log.info("=========Initiating Shelves=========");
    Shelf[] shelves = initShelves();
    log.info("Initiating shelves done: " + Arrays.toString(shelves));
    OrderManager manager = OrderManager.builder().shelves(shelves).build();

    log.info("=========Simulation Being=========");
    Simulation.init(manager, orders, ingestionRate).run();
  }

  private static Shelf[] initShelves() {
    Shelf[] shelves = new Shelf[4];
    shelves[0] = Shelf.HOT_SHELF;
    shelves[1] = Shelf.COLD_SHELF;
    shelves[2] = Shelf.FROZEN_SHELF;
    shelves[3] = Shelf.OVERFLOW_SHELF;
    return shelves;
  }

  static OrderBasic[] loadOrders() throws IOException {
    URL url = Resources.getResource("orders.json");
    String rawData = Resources.toString(url, StandardCharsets.UTF_8);
    log.debug("Snippet of input data:\n" + rawData.substring(0, 200));

    final ObjectMapper objMapper = new ObjectMapper();
    OrderBasic[] orders = objMapper.readValue(rawData, OrderBasic[].class);

    log.debug("First order: " + orders[0]);
    return orders;
  }
}
