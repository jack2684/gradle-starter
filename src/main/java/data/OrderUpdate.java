package data;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class OrderUpdate {
  int discarded;
  int pickedUp;

  public void incPickedUp() {
    pickedUp++;
  }

  public void  incDiscarded() {
    discarded++;
  }

  public OrderUpdate merge(OrderUpdate o) {
    discarded += o.discarded;
    pickedUp += o.pickedUp;
    return this;
  }
}
