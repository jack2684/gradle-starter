package data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Preferred shelf storage temperature
 *
 * <p>valid values: frozen|cold|hot
 */
public enum Temp {
  @JsonProperty("any")
  ANY,
  @JsonProperty("hot")
  HOT,
  @JsonProperty("frozen")
  FROZEN,
  @JsonProperty("cold")
  COLD
}