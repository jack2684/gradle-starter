package data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

/** data.Order class host the basic properties of an order. This is used for json deserialization. */
@Value
@Builder
public class OrderBasic {
  @JsonProperty("id")
  String id;

  @JsonProperty("name")
  String name;

  @JsonProperty("temp")
  Temp temp;

  /** core.Shelf wait max duration (seconds) */
  @JsonProperty("shelfLife")
  int shelfLife;

  /** Value deterioration modifier */
  @JsonProperty("decayRate")
  float decayRate;

  public OrderBasic(
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
