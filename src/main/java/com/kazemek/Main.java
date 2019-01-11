package com.kazemek;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Main {

  private static final String TEAM_ROS_FILE = "team_ros.csv";
  private static final String LEAGUE_ROS_FILE = "league_ros.csv";
  private static final String TEAM_WEEK_FILE = "team_week.csv";
  private static final String LEAGUE_WEEK_FILE = "league_week.csv";
  private static final double RATIO_TRADE_DISTANCE = 0.25;
  private static final int MAX_TRADE_DISTANCE = 30;
  private static final int MIN_TRADE_DISTANCE = 15;

  public static void main(String[] args) throws IOException, URISyntaxException {
    Set<Player> teamRos = getPlayers(TEAM_ROS_FILE);
    Set<Player> teamWeek = getPlayers(TEAM_WEEK_FILE);
    List<Player> leagueRos = getPlayers(LEAGUE_ROS_FILE)
        .stream()
        .sorted(Comparator.comparing(player -> player.getRank()))
        .collect(Collectors.toList());
    Set<Player> leagueWeek = getPlayers(LEAGUE_WEEK_FILE);

    double activeRosterRosScore = RosterHelper.getActiveRoster(teamRos);
    double activeRosterWeekScore = RosterHelper.getActiveRoster(teamWeek);
    double fullRosterRosScore = RosterHelper.getFullRoster(teamRos);

    for (int i = 0; i < leagueRos.size(); i++) {
      Player myPlayer = leagueRos.get(i);
      if (teamRos.contains(myPlayer)) {
        teamRos.remove(myPlayer);
        Optional<Player> myWeekPlayer = getPlayer(myPlayer, teamWeek);
        myWeekPlayer.ifPresent(player -> teamWeek.remove(player));
        double searchDistance =
            Math.min(i, Math.min(Math.max(MIN_TRADE_DISTANCE, i * RATIO_TRADE_DISTANCE), MAX_TRADE_DISTANCE));
        for (int j = 5; j <=
            searchDistance; j++) {
          Player otherPlayer = leagueRos.get(i - j);
          Optional<Player> otherWeekPlayer = getPlayer(otherPlayer, leagueWeek);
          if (!teamRos.contains(otherPlayer) && myPlayer.getPosition() != otherPlayer.getPosition()) {
            teamRos.add(otherPlayer);
            otherWeekPlayer.ifPresent(player -> teamWeek.add(player));
            double newActiveRosterRosScore = RosterHelper.getActiveRoster(teamRos);
            double newActiveRosterWeekScore = RosterHelper.getActiveRoster(teamWeek);
            double newFullRosterRosScore = RosterHelper.getFullRoster(teamRos);
            if (newActiveRosterRosScore <= activeRosterRosScore &&
                newFullRosterRosScore < fullRosterRosScore &&
                newActiveRosterWeekScore <= activeRosterWeekScore) {
              System.out.println("Trade " + myPlayer + " for " + otherPlayer);
            }
            teamRos.remove(otherPlayer);
            otherWeekPlayer.ifPresent(player -> teamWeek.remove(player));
          }
        }
        teamRos.add(myPlayer);
        myWeekPlayer.ifPresent(player -> teamWeek.add(player));
      }
    }
  }

  private static Optional<Player> getPlayer(Player player, Collection<Player> players) {
    return players.stream().filter(player1 -> player1.equals(player)).findFirst();
  }

  private static Set<Player> getPlayers(String file) throws IOException, URISyntaxException {
    Set<Player> team = new HashSet<>();
    Reader reader = Files.newBufferedReader(
        Paths.get(
            Thread.currentThread().getContextClassLoader().getResource(file).toURI()));
    CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
    for (CSVRecord csvRecord : csvParser) {
      if (csvRecord.isConsistent()) {
        Player player = new Player.PartialPlayer()
            .setName(csvRecord.get("Flex Position (RB/WR/TE)"))
            .setPosition(Arrays.stream(Position.values()).filter(position ->
                csvRecord.get("Pos").startsWith(position.getMatch())).findFirst().orElse(null))
            .setRank(Double.valueOf(csvRecord.get("Avg")))
            .build();
        team.add(player);
      }
    }
    return team;
  }
}
