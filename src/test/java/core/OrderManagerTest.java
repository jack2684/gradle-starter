package core;

import data.Order;
import data.OrderBasic;
import data.Snapshot;
import data.Temp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrderManagerTest {
  Shelf[] shelves;
  OrderManager manager;
  Order dummyHotOrder;
  Order dummyExpireOrder;

  static final int PICKUP_TIME = 2;

  @BeforeEach
  public void setup() {
    shelves = OrderManager.initShelves();
    manager = OrderManager.builder().shelves(shelves).build();

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

  Shelf getHotShelf() {
    for (Shelf shelf : shelves) {
      if (shelf.getTemp() == Temp.HOT) {
        return shelf;
      }
    }
    return null;
  }

  Shelf getOverflowShelf() {
    for (Shelf shelf : shelves) {
      if (shelf.isOverflowShelf()) {
        return shelf;
      }
    }
    return null;
  }

  @Test
  void allShelvesEmptyAtBeginning() {
    assertTrue(
        "All Shelves should be empty at the beginning, while it is " + manager.totalAssigned(),
        manager.isAllShelfEmpty());
  }

  @Test
  void allShelvesEmptyAfterOrderCompleted() {
    manager.assign(dummyHotOrder);
    manager.updateDeliveryAndExpired(Long.MAX_VALUE);
    assertTrue("All Shelves should be empty after long time", manager.isAllShelfEmpty());
  }

  @Test
  public void placedAtCorrespondingTemperatureShelf() {
    manager.assign(dummyHotOrder);
    for (Shelf shelf : shelves) {
      if (shelf.getTemp() == dummyHotOrder.getBasic().getTemp()) {
        assertEquals(
            "Expect the correct shelf contains that particular one order. ", 1, shelf.size());
      }
    }
  }

  @Test
  public void expectToPutOverFlow() {
    while (getHotShelf().hasCapacity()) {
      manager.assign(dummyHotOrder);
    }
    manager.assign(dummyHotOrder);

    assertFalse("Expect hot shelf has no capacity", getHotShelf().hasCapacity());
    assertEquals(
        "Expect hot shelf has correct size, while it is " + getHotShelf().size(),
        getHotShelf().capacity,
        getHotShelf().size());
    assertEquals(
        "Expect overflow shelf has 1 item, while it is " + getOverflowShelf().size(),
        1,
        getOverflowShelf().size());
  }

  @Test
  public void expectToOverflowTheOverFlow() {
    while (getHotShelf().hasCapacity()) {
      manager.assign(dummyHotOrder);
    }
    while (getOverflowShelf().hasCapacity()) {
      manager.assign(dummyHotOrder);
    }
    manager.assign(dummyHotOrder);

    assertFalse("Expect hot shelf has no capacity", getHotShelf().hasCapacity());
    assertFalse("Expect overflow shelf has no capacity", getOverflowShelf().hasCapacity());
    assertTrue(
        "Expect overflow shelf has latest order even after it is overflown",
        getOverflowShelf().containsOrder(dummyHotOrder));
  }

  @Test
  public void updateDelivery() {
    manager.assign(dummyHotOrder);
    Snapshot snapshot = manager.updateDeliveryAndExpired(PICKUP_TIME + 1);

    assertEquals(1, snapshot.getDelivery());
  }

  @Test
  public void updateExpire() {
    manager.assign(dummyExpireOrder);
    Snapshot snapshot = manager.updateDeliveryAndExpired(Long.MAX_VALUE - 1);

    assertEquals(1, snapshot.getExpired());
  }
}
