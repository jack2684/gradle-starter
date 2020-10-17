package simulation;

public enum Place {
  HOT_SHELF("HOT_SHELF"),
  COLD_SHELF("COLD_SHELF"),
  FROZEN_SHELF("FROZEN_SHELF"),
  OVERFLOW_SHELF("OVERFLOW_SHELF"),
  DELIVERY("DELIVERED"),
  EXPIRED("EXPIRED"),
  TRASH("TRASH"),
  COMPLETED("COMPLETED"),
  UNKNOWN("UNKNOWN");

  private final String text;

  /** @param text */
  Place(final String text) {
    this.text = text;
  }

  /* (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }
}