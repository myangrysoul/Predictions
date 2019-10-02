package Parser;

import Parser.Match.Match;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Team implements Serializable {
    private static final long serialVersionUID = -5793901141299978498L;
    private final int code;
    private final String name;
    private final String href;
    private final int id;
    private final String matchesPage;
    private  TreeSet<Match> matches;
    private final LinkedHashMap<String, Integer> ranks;


    public LinkedHashMap<String, Integer> getRanks() {
        return ranks;
    }

    @Override
    public String toString() {
        return "Team{" +
               "code=" + code +
               ", name='" + name + '\'' +
               ", href='" + href + '\'' +
               ", id=" + id +
               ", matchesPage='" + matchesPage + '\'' +
               ", matches=" + matches +
               ", ranks=" + ranks +
               '}';
    }

    public String getMatchesPage() {
        return matchesPage;
    }

    public void addMatch(Match match) {
          boolean bool=matches.add(match);
    }

    public Team(int code, String name, String href, LinkedHashMap<String, Integer> ranks) {
        this.code = code;
        this.name = name;
        this.href = href;
        Pattern pattern = Pattern.compile("(0|[1-9][0-9]*)");
        Matcher matcher = pattern.matcher(href);
        if (matcher.find()) {
            id = Integer.valueOf(matcher.group());
            matchesPage = "https://www.hltv.org/results?team=" + id;
        } else {
            throw new RuntimeException("team const err");
        }
        this.ranks = ranks;
        matches = new TreeSet<>(SerializedComp.INSTANCE);
    }

    public void recompare(){
        TreeSet<Match> matches1=new TreeSet<>(SerializedComp.INSTANCE);
        matches1.addAll(matches);
        matches=matches1;
    }
    public int getRank(String key) {
        return ranks.get(key);
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getHref() {
        return href;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Match> getMatches() {
        return new ArrayList<Match>(matches);
    }
}
