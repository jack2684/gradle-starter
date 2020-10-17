import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class OrderManagerTest {
  Shelf[] shelves;
  OrderManager manager;
  Order dummyHotOrder;

  @BeforeEach
  public void setup() {
    shelves = Main.initShelves();
    manager = OrderManager.builder().shelves(shelves).build();

    dummyHotOrder =
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
            .pickupTime(2)
            .build();
  }

  @Test
  void allShelvesEmptyAtBeginning() {
    assertTrue("All Shelves should be empty at the beginning", manager.isAllShelfEmpty());
  }

  @Test
  void allShelvesEmptyAfterOrderCompleted() {
    manager.assign(dummyHotOrder);
    manager.updateDeliveryAndExpired(Long.MAX_VALUE);
    assertTrue("All Shelves should be empty at the beginning", manager.isAllShelfEmpty());
  }

//  @Test
//  public void oldPlacedAtCorrespondingTemperatureShelf() {
//    manager.assign(dummyHotOrder);
//    for (Shelf shelf : shelves) {
//      if (shelf.getTemp().equals(dummyHotOrder.getBasic().getTemp())) {
//        shelf.getTemp();
//      }
//    }
//  }
}
