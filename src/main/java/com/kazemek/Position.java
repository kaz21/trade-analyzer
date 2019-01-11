package com.kazemek;

public enum Position {
  QUARTER_BACK("QB", false),
  WIDE_RECIEVER("WR", true),
  RUNNING_BACK("RB", true),
  TIGHT_END("TE", true),
  DEFENSE("DST", false),
  KICKER("K", false),
  FLEX("F", true);

  private String match;
  private boolean flex;

  Position(String match, boolean flex) {
    this.match = match;
    this.flex = flex;
  }

  public String getMatch() {
    return match;
  }

  public boolean isFlex() {
    return flex;
  }
}
