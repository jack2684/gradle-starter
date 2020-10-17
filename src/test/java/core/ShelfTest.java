package core;

import data.Order;
import data.OrderBasic;
import data.Temp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class ShelfTest {
  Shelf hotShelf;
  Shelf overflowShelf;
  Order dummyHotOrder;
  Order dummyExpireOrder;

  static final int PICKUP_TIME = 2;

  @BeforeEach
  public void setup() {
    hotShelf = Shelf.createHotShelf();
    overflowShelf = Shelf.createHotShelf();

    Order.OrderBuilder orderBuilder =
        Order.builder()
            .basic(
                OrderBasic.builder()
                    .id("0ff534a7-a7c4-48ad-b6ec-7632e36af950")
                    .name("Cheese Pizza")
                    .temp(Temp.HOT)
                    .shelfLife(300)
                    .decayRate(0.45f)
                    .build())
            .orderTime(0)
            .pickupTime(PICKUP_TIME);

    dummyHotOrder = orderBuilder.build();
    dummyExpireOrder = orderBuilder.pickupTime(Long.MAX_VALUE).build();
  }

  @Test
  void allShelvesEmptyAtBeginning() {
    assertEquals(
        "All Shelves should be empty at the beginning, while it is " + hotShelf.size(),
        0,
        hotShelf.size());
  }

  @Test
  void properlyPickedUp() {
    hotShelf.put(dummyHotOrder);
    hotShelf.updateDeliveryAndExpired(PICKUP_TIME + 1);
    assertEquals("All Shelves should be empty after long time", 0, hotShelf.size());
  }

  @Test
  void properlyExpire() {
    hotShelf.put(dummyExpireOrder);
    hotShelf.updateDeliveryAndExpired(Long.MAX_VALUE - 1);
    assertEquals("All Shelves should be empty after long time", 0, hotShelf.size());
  }
}
