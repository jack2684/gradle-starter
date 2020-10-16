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
    int ingestionRate = DEFAULT_INGESTION_RATE;
    if (args.length > 0) {
      ingestionRate = Integer.parseInt(args[0]);
    }
    log.info("Order ingestion rate: {}/sec", ingestionRate);
    log.info("=========Loading Orders=========");
    Order[] orders = loadOrders();
    log.info("Load orders done. Number of orders: {}", orders.length);
    log.info("=========Initiating Shelves=========");
    Shelf[] shelves = initShelves();
    log.info("Initiating shelves done: " + Arrays.toString(shelves));
  }

  private static Shelf[] initShelves() {
    Shelf[] shelves = new Shelf[4];
    shelves[0] = Shelf.builder().name("Hot Shelf").capacity(10).temp(Temp.HOT).build();
    shelves[1] = Shelf.builder().name("Cold Shelf").capacity(10).temp(Temp.COLD).build();
    shelves[2] = Shelf.builder().name("Frozen Shelf").capacity(10).temp(Temp.FROZEN).build();
    shelves[3] = Shelf.builder().name("Overflow Shelf").capacity(15).temp(Temp.ANY).build();
    return shelves;
  }

  static Order[] loadOrders() throws IOException {
    URL url = Resources.getResource("orders.json");
    String rawData = Resources.toString(url, StandardCharsets.UTF_8);
    log.debug("Snippet of input data:\n" + rawData.substring(0, 200));

    final ObjectMapper objMapper = new ObjectMapper();
    Order[] orders = objMapper.readValue(rawData, Order[].class);

    log.debug("First order: " + orders[0]);
    return orders;
  }
}
