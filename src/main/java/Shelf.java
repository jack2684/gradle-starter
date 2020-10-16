import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Shelf {
  String name;

  /**
   * What temperature of food is allowed to put on this shelf
   */
  Temp temp;

  int capacity;

  static ShelfBuilder Builder = new Shelf.ShelfBuilder();
}
