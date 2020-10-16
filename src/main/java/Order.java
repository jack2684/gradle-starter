import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/** Order class host the properties of an order. */
@Data
public class Order {
  @JsonProperty("id")
  String id;

  @JsonProperty("name")
  String name;

  @JsonProperty("temp")
  Temp temp;

  /** Shelf wait max duration (seconds) */
  @JsonProperty("shelfLife")
  int shelfLife;

  /** Value deterioration modifier */
  @JsonProperty("decayRate")
  float decayRate;

  /**
   * Value of on order degrades over time when below zero it should be thrown away
   */
  @JsonIgnore
  int value;

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
}
