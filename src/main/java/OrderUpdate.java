import lombok.Data;

@Data
public class OrderUpdate {
  int discarded;
  int pickedUp;

  OrderUpdate merge(OrderUpdate o) {
    discarded += o.discarded;
    pickedUp += o.pickedUp;
    return this;
  }
}
