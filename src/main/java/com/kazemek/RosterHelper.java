package com.kazemek;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class RosterHelper {

  private static Map ACTIVE_ROSTER = new ImmutableMap.Builder<>()
//      .put(Position.QUARTER_BACK, 1)
//      .put(Position.DEFENSE, 1)
//      .put(Position.KICKER, 1)
      .put(Position.WIDE_RECIEVER, 2)
      .put(Position.RUNNING_BACK, 2)
      .put(Position.TIGHT_END, 1)
      .put(Position.FLEX, 1)
      .build();
  private static Map FULL_ROSTER = new ImmutableMap.Builder<>()
//      .put(Position.QUARTER_BACK, 2)
//      .put(Position.DEFENSE, 2)
//      .put(Position.KICKER, 1)
      .put(Position.WIDE_RECIEVER, 3)
      .put(Position.RUNNING_BACK, 3)
      .put(Position.TIGHT_END, 2)
      .put(Position.FLEX, 0)
      .build();

  public static double getActiveRoster(Collection<Player> players) {
    return getRoster(players, ACTIVE_ROSTER, false);
  }

  public static double getFullRoster(Collection<Player> players) {
    return getRoster(players, FULL_ROSTER, true);
  }

  private static double getRoster(Collection<Player> players, Map roster, boolean allowExtras) {
    Map<Position, Integer> toAllocate = Maps.newHashMap(roster);
    double toReturn = players.stream()
        .sorted(Comparator.comparing(player -> player.getRank()))
        .filter(player -> {
          if (toAllocate.get(player.getPosition()) > 0) {
            toAllocate.put(player.getPosition(), toAllocate.get(player.getPosition()) - 1);
            return true;
          } else if (player.getPosition().isFlex() && toAllocate.get(Position.FLEX) > 0) {
            toAllocate.put(Position.FLEX, toAllocate.get(Position.FLEX) - 1);
            return true;
          }
          return allowExtras;
        })
        .mapToDouble(player -> Math.sqrt(player.getRank()))
        .sum();
    return toAllocate.values().stream().noneMatch(value -> value != 0) ? toReturn : Double.MAX_VALUE;
  }
}
