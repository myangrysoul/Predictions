package Parser.Match;

import Parser.Patterns;
import Parser.Team;
import Parser.Tiers;
import javafx.util.Pair;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// сэт матчей соответственно повторений не будет HashSet matches=getMatches;
//из всех матчей выбирает матч с такой же ссылкой и записывает в HtH
public class Match implements Serializable {
    private static final long serialVersionUID = 806031897586484588L;
    private Pair<Team, Integer> team1Score;
    private Pair<Team, Integer> team2Score;

    public Pair<Team, Integer> getTeam1Score() {
        return team1Score;
    }

    public Pair<Team, Integer> getTeam2Score() {
        return team2Score;
    }

    private final int rank1;
    private final int rank2;
    private String matchPage;
    private final int matchType;
    private LinkedHashSet<Match> pastHtH;
    private final ArrayList<String> mapsPlayed;
    private ArrayList<Integer> mapScores;
    private final Tiers eventTier;
    private LocalDate date;
    private long matchId;



    public int getRank1() {
        return rank1;
    }

    public double getAnswer(Team team) {
        double answ;
        int a = 0;
        int b = 0;
        for (int i = 0; i < mapScores.size(); i += 2) {
            a += mapScores.get(i);
            b += mapScores.get(i + 1);
        }
        if (team1Score.getValue() > team2Score.getValue()) {
            if (team.equals(team1Score.getKey())) {
                answ = a /(double) (a + b);
            } else {
                answ = b / (double)(a + b);
            }
        } else {
            if (team.equals(team1Score.getKey())) {
                answ = a / (b + a * 0.8f);
            } else {
                answ = b / (b + a * 0.8f);
            }
        }
        return Math.round(answ*100)/100D;
    }

    public long matchId(String matchPage){
       Matcher matcher= Pattern.compile("(0|[1-9][0-9]*)").matcher(matchPage);
       matcher.find();
       return Long.parseLong(matcher.group());
    }
    public int getRank2() {
        return rank2;
    }

    public int getMatchType() {
        return matchType;
    }

    public ArrayList<Match> getPastHtH() {
        return new ArrayList<Match>(pastHtH);
    }

    public ArrayList<String> getMapsPlayed() {
        return mapsPlayed;
    }

    public ArrayList<Integer> getMapScores() {
        return mapScores;
    }

    public Tiers getEventTier() {
        return eventTier;
    }

    public Match(Pair<Team, Integer> team1Score, Pair<Team, Integer> team2Score, String matchPage, int matchType,
                 ArrayList<String> mapsPlayed,
                 ArrayList<Integer> mapScores, Tiers eventTier, LocalDate date, int rank1, int rank2) {
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.matchPage = matchPage;
        this.matchType = matchType;
        pastHtH = new LinkedHashSet<>();
        this.mapsPlayed = mapsPlayed;
        this.mapScores = mapScores;
        this.eventTier = eventTier;
        this.date = date;
        this.rank1 = rank1;
        this.rank2 = rank2;
        matchId=matchId(matchPage);
    }

    public long getMatchId() {
        return matchId;
    }

    public Match(Pair<Team, Integer> team1Score, Pair<Team, Integer> team2Score, int rank1, int rank2, int matchType,
                 ArrayList<String> mapsPlayed, Tiers eventTier) {
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.rank1 = rank1;
        this.rank2 = rank2;
        this.matchType = matchType;
        this.mapsPlayed = mapsPlayed;
        this.eventTier = eventTier;
    }
    public LocalDate getDate() {
        return date;
    }

    public void addHtH(Match match) {
        pastHtH.add(match);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Match match = (Match) o;
        return rank1 == match.rank1 &&
               rank2 == match.rank2 &&
               matchType == match.matchType &&
               matchId == match.matchId &&
               Objects.equals(team1Score, match.team1Score) &&
               Objects.equals(team2Score, match.team2Score) &&
               Objects.equals(matchPage, match.matchPage) &&
               Objects.equals(pastHtH, match.pastHtH) &&
               Objects.equals(mapsPlayed, match.mapsPlayed) &&
               Objects.equals(mapScores, match.mapScores) &&
               eventTier == match.eventTier &&
               Objects.equals(date, match.date);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(team1Score, team2Score, rank1, rank2, matchPage, matchType, pastHtH, mapsPlayed, mapScores,
                      eventTier,
                      date, matchId);
    }

    @Override
    public String toString() {
        ArrayList<String> pages = new ArrayList<>();
        pastHtH.forEach(obj -> pages.add(obj.matchPage));
        return "Match{" +
               "team1Score=" + team1Score.getKey().getName() + '=' + team1Score.getValue() + '\n' +
               ", team2Score=" + team2Score.getKey().getName() + '=' + team2Score.getValue() + '\n' +
               ", matchPage='" + matchPage + '\'' + '\n' +
               ", matchType=" + matchType + '\n' +
               ", mapsPlayed=" + mapsPlayed + '\n' +
               ", mapScores=" + mapScores + '\n' +
               ", eventTier=" + eventTier + '\n' +
               '}' + "\n...." + pages;
    }
}
