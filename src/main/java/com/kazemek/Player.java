package com.kazemek;

import java.util.Objects;

public class Player {

  private Position position;
  private Double rank;
  private String name;

  public Position getPosition() {
    return position;
  }

  public Double getRank() {
    return rank;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player)) {
      return false;
    }
    Player player = (Player) o;
    return position == player.position &&
        Objects.equals(name, player.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, name);
  }

  @Override
  public String toString() {
    return "Player{" +
        "position=" + position +
        ", rank=" + rank +
        ", name='" + name + '\'' +
        '}';
  }

  private Player(Position position, Double rank, String name) {
    this.position = position;
    this.rank = rank;
    this.name = name;
  }

  public static class PartialPlayer {

    private Position position;
    private Double rank;
    private String name;

    public PartialPlayer setPosition(Position position) {
      this.position = position;
      return this;
    }

    public PartialPlayer setRank(Double rank) {
      this.rank = rank;
      return this;
    }

    public PartialPlayer setName(String name) {
      this.name = name;
      return this;
    }

    public Player build() {
      return new Player(position, rank, name);
    }

  }

}
