import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

/** Order class host the properties of an order. */
@Data
public class Order {
  @JsonProperty("id")
  String id;

  @JsonProperty("name")
  String name;

  /** frozen|cold|hot */
  @JsonProperty("temp")
  Temp temp;

  /** TODO: add doc */
  @JsonProperty("shelfLife")
  int shelfLife;

  /** TODO: add doc */
  @JsonProperty("decayRate")
  float decayRate;

  public Order(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("temp") Temp temp,
      @JsonProperty("shelfLife") int shelfLife,
      @JsonProperty("decayRate") float decayRate) {
    this.id = id;
    this.name = name;
    this.temp = temp;
    this.shelfLife = shelfLife;
    this.decayRate = decayRate;
  }

  public enum Temp {
    @JsonProperty("hot")
    HOT,
    @JsonProperty("frozen")
    FROZEN,
    @JsonProperty("cold")
    COLD;
  }
}
