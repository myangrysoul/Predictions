package Parser;

import Parser.Match.Match;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HLTVparser implements Serializable {
    private static final long serialVersionUID = 1372545293329104584L;
    private int c;
    private final String top30;
    private final Pattern pattern = Pattern.compile("\\b(0|[1-9][0-9]*)\\b");
    private final Pattern patt = Pattern.compile("(th|rd|st|nd) of");
    private final LinkedHashMap<String, Match> matches;
    private final LinkedHashMap<String, Team> teams;
    private ArrayList<Team> teamsSet;
    private final HashMap<String, Tiers> tournaments;
    private final ArrayList<String> futureTournaments;

    // current code
    //data matcha parsit'+ metod datediff peredelat'

    public HLTVparser() {
        top30 = "https://www.hltv.org/ranking/teams/2019/june/3";
        matches = new LinkedHashMap<>();
        teams = new LinkedHashMap<>();
        futureTournaments=new ArrayList<>();
        tournaments = new HashMap<>();
    }


    public void parseMatches() throws IOException {
        teamsSet = new ArrayList<>();
        teamsSet.addAll(teams.values());
        Document doc;
        for (int i = c; i < teamsSet.size(); i++) {
            System.out.println(teamsSet.get(i).getName());
            try {
                doc = Jsoup.connect(teamsSet.get(i).getMatchesPage()).userAgent("Mozilla").get();
            } catch (SocketException e) {
                doc = Jsoup.connect(teamsSet.get(i).getMatchesPage()).userAgent("Mozilla").get();
            }
            Elements elements = doc.getElementsByAttributeValue("class", "results-holder").get(0).
                    getElementsByAttributeValue("class", "result-con");
            int a = 0;
            System.out.println(elements.size());
            for (Element elem : elements
            ) {
                Match match = parseMatchInfo(elem, true);
                if (match != null) {
                    System.out.println("A:" + a);
                    a++;
                }

            }

            c = i + 1;
            try {
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("hltv.out"));
                os.writeObject(this);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            System.out.println(i);

        }
    }

    public void restartParseMatches() throws IOException {
        Document doc;
        for (int i = c; i < teamsSet.size(); i++) {
            System.out.println(i);
            System.out.println(teamsSet.get(i).getName());
            try {
                doc = Jsoup.connect(teamsSet.get(i).getMatchesPage()).userAgent("Mozilla").get();
            } catch (SocketException e) {
                doc = Jsoup.connect(teamsSet.get(i).getMatchesPage()).userAgent("Mozilla").get();
            }
            Elements elements = doc.getElementsByAttributeValue("class", "result-con");
            int a = 0;
            for (Element elem : elements
            ) {
                Match match = parseMatchInfo(elem, true);
                if (match != null) {
                    System.out.println("A:" + a);
                    a++;
                }
            }
            c = i + 1;
            try {
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("hltv.out"));
                os.writeObject(this);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public LocalDate parseMatchDate(Element element) {
        Element element1 = element.getElementsByClass("timeAndEvent").get(0);
        String date = element1.child(1).text();
        Matcher matcher = patt.matcher(date);
        date = matcher.replaceFirst("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(Locale.US);
        return LocalDate.from(formatter.parse(date));
    }


    private boolean dateDiff(LocalDate hthDate, LocalDate date) {
        return ChronoUnit.DAYS.between(hthDate, date) <= 120;
    }

    public boolean normalizeDate(String date, String format, LocalDate date1) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format).withLocale(Locale.US);
        StringBuilder normalDate = new StringBuilder();
        LocalDate normalDate1;

        normalDate.append(date.replaceFirst(" ", "/"));
        normalDate1 = LocalDate.from(formatter.parse(normalDate));
        return dateDiff(normalDate1, date1);
    }


    public Match parseMatchInfo(Element element, boolean bool) throws IOException {
        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
        ArrayList<Integer> mapScores = new ArrayList<>();


        Element elem1 = element.child(0);
        String href = "https://www.hltv.org" + elem1.attr("href");
        Document doc;
        try {
            doc = Jsoup.connect(href).userAgent("Opera").referrer(element.baseUri()).
                    get();
        } catch (SocketException e) {
            doc = Jsoup.connect(href).userAgent("Opera").referrer(element.baseUri()).
                    get();
        }

        Elements elements = doc.getElementsByAttributeValue("class", "match-page").get(0)
                               .getElementsByAttributeValue("class", "team");
        for (Element element1 : elements) {
            int score;
            String str;
            Matcher matcher = pattern.matcher(element1.text());
            if (matcher.find()) {
                score = Integer.valueOf(matcher.group());
                if (score > 100) {
                    return null;
                }
                str = element1.text().substring(0, matcher.start() - 1);
                if (!teams.containsKey(str)) {
                    String link = "https://www.hltv.org" + element1.child(1).child(0).attr("href");
                    String text = Jsoup.connect(link).userAgent("Mozilla").referrer(href).get()
                                       .getElementsByAttributeValue("href", "/ranking/teams").get(1).text();
                    int a;
                    try {
                        String b = text.substring(1);
                        a = Integer.parseInt(b);
                        if (a > 50) {
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }

                    teams.put(str, new Team(a, str, link, getRanksHistory(compilePattern("201[8-9]/[a-z]+"),
                                                                          compilePattern("\"value\":\"[1-9][0-9]*\""),
                                                                          compilePattern("[1-9][0-9]*"),
                                                                          link)));
                }
                String a = parseMatchDate(doc).format(DateTimeFormatter.ofPattern("yyyy/MMMM").withLocale(Locale.US))
                                              .toLowerCase();
                if (!teams.get(str).getRanks()
                          .containsKey(a)) {
                    return null;
                }
                scores.add(new Pair<>(str, score));

            }

        }
        ArrayList<String> mapsPicked = new ArrayList<>();
        Elements elems = doc.getElementsByAttributeValue("class", "played");
        String map;
        for (Element element1 : elems) {
            mapsPicked.add(element1.child(0).child(1).text());
            mapScores.add(Integer.valueOf(element1.nextElementSibling().child(0).text()));
            mapScores.add(Integer.valueOf(element1.nextElementSibling().child(2).text()));
        }
        elems = doc.getElementsByAttributeValue("class", "spacing optional");
        if (!elems.isEmpty()) {
            for (Element element1 : elems) {
                map=element1.child(0).child(1).text();
                if("TBA".equals(map) || "Default".equals(map)) {
                    return null;
                }
                mapsPicked.add(map);
            }
        }


        Elements padText = doc.getElementsByAttributeValue("class", "match-page")
                              .get(0)
                              .getElementsByAttributeValue("class", "padding preformatted-text");
        String matchType;
        if (padText.isEmpty()) {
            matchType = "best of 1";
        } else {
            matchType = padText.get(0).text().substring(0, 9);
        }
        Match match = null;
        String t1 = scores.get(0).getKey();
        String t2 = scores.get(1).getKey();
        Team team1 = teams.get(t1);
        Team team2 = teams.get(t2);
        int r1 = getCurrentRank(doc, team1);
        int r2 = getCurrentRank(doc, team2);
        if (r1 > 50 || r2 > 50) {
            return null;
        }
        String tournament = doc.getElementsByAttributeValue("class", "event text-ellipsis").get(0).child(0).text();
        switch (matchType.toLowerCase()) {
        case "best of 1":
            match = new Match(new Pair<>(team1, scores.get(0).getValue()),
                              new Pair<>(team2, scores.get(1).getValue()),
                              href, 1, mapsPicked, mapScores, tournaments.getOrDefault(tournament, Tiers.OTHER),
                              parseMatchDate(doc.getElementsByAttributeValue("class", "match-page").get(0)),
                              getCurrentRank(doc, team1), getCurrentRank(doc, team2));
            matches.put(href, match);
            findHtH(doc.getElementsByClass("match-page").get(0), match);
            if (bool) {
                team1.addMatch(match);
                team2.addMatch(match);
            }

            return match;
        case "best of 3":
            match = new Match(new Pair<>(team1, scores.get(0).getValue()),
                              new Pair<>(team2, scores.get(1).getValue()),
                              href, 2, mapsPicked, mapScores, tournaments.getOrDefault(tournament, Tiers.OTHER),
                              parseMatchDate(doc.getElementsByAttributeValue("class", "match-page").get(0)),
                              getCurrentRank(doc, team1), getCurrentRank(doc, team2));
            matches.put(href, match);
            findHtH(doc.getElementsByClass("match-page").get(0), match);
            if (bool) {
                team1.addMatch(match);
                team2.addMatch(match);
            }
            return match;
        case "best of 5":

            match = new Match(new Pair<>(team1, scores.get(0).getValue()),
                              new Pair<>(team2, scores.get(1).getValue()),
                              href, 3, mapsPicked, mapScores, tournaments.getOrDefault(tournament, Tiers.OTHER),
                              parseMatchDate(doc.getElementsByAttributeValue("class", "match-page").get(0)),
                              getCurrentRank(doc, team1), getCurrentRank(doc, team2));
            matches.put(href, match);
            findHtH(doc.getElementsByClass("match-page").get(0), match);
            if (bool) {
                team1.addMatch(match);
                team2.addMatch(match);
            }
            return match;
        }
        return match;
    }

    public void findHtH(Element element, Match match) throws IOException {
        Elements elements = element.getElementsByAttributeValue("class", "row nowrap  ");
        Elements elements1 = element.getElementsByAttributeValue("class", "row nowrap  alt new-match-begin");
        Elements elements2 = element.getElementsByAttributeValue("class", "row nowrap   new-match-begin");
        iteration(elements, match);
        iteration(elements1, match);
        iteration(elements2, match);


    }

    public void iteration(Elements elements, Match match) throws IOException {
        String str;
        for (Element element : elements
        ) {
            Element elem = element.getElementsByClass("date").get(0);
            str = "https://www.hltv.org" + elem.child(0).attr("href");
            if (normalizeDate(elem.child(0).child(0).text(), "d/M/yy", match.getDate()) && !matches.containsKey(str)) {
                Match match1 = parseMatchInfo(elem, false);
                if (match1 != null) {
                    match.addHtH(match1);
                }
            } else if (normalizeDate(elem.child(0).child(0).text(), "d/M/yy", match.getDate()) &&
                       matches.containsKey(str)) {
                match.addHtH(matches.get(str));

            }

        }
    }


    public void parseTeams() throws IOException {
        Pattern pattern = Pattern.compile("201[8-9]/[a-z]+");
        Pattern pattern1 = Pattern.compile("\"value\":\"[1-9][0-9]*\"");
        Pattern pattern2 = Pattern.compile("[1-9][0-9]*");
        int c = 1;

        Document doc = Jsoup.connect(top30).userAgent("Mozilla").get();
        Elements elements = doc.getElementsByAttributeValue("class", "bg-holder");
        for (Element element : elements) {
            Element elem1 = element.child(0).child(2).child(0).child(0);
            Element elem2 = element.child(1).child(1);
            Elements elems = elem2.getElementsByAttribute("href");
            String href = "https://www.hltv.org" + elems.get(0).attr("href");
            teams.put(elem1.text(),
                      new Team(c, elem1.text(), href, getRanksHistory(pattern, pattern1, pattern2, href)));
            if (c == 25) {
                break;
            }
            c++;

        }
        teams.forEach((k, v) -> System.out.println(v));

    }


    private LinkedHashMap<String, Integer> getRanksHistory(Pattern pattern, Pattern pattern1, Pattern pattern2,
                                                           String connectTo) throws IOException {
        Document doc;
        try {
            doc = Jsoup.connect(connectTo).userAgent("Mozilla").get();
        } catch (SocketException e) {
            doc = Jsoup.connect(connectTo).userAgent("Mozilla").get();

        }

        Element element = doc.getElementsByAttributeValue("class", "graph").get(0);
        String str = element.attr("data-fusionchart-config");

        Matcher matcher = pattern.matcher(str);
        Matcher matcher1 = pattern1.matcher(str);
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Integer> integers = new ArrayList<>();
        while (matcher.find()) {
            strings.add(matcher.group());
        }

        while (matcher1.find()) {
            Matcher matcher2 = pattern2.matcher(matcher1.group());
            if (matcher2.find()) {
                integers.add(Integer.parseInt(matcher2.group()));
            } else {
                throw new RuntimeException("loh");
            }

        }
        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        int a = integers.size();
        int b = strings.size();
        String bufffer = strings.get(0);
        float scorecount = 0;
        int c = 0;
        for (int i = 0; i < b; i++) {
            if (bufffer.equals(strings.get(i))) {
                scorecount += integers.get(a - b + i);
            } else {
                res.put(bufffer, Math.round(scorecount / c));
                c = 0;
                scorecount = integers.get(a - b + i);

            }
            bufffer = strings.get(i);
            c++;
        }
        res.put(bufffer, Math.round(scorecount / c));
        return res;

    }

    int getCurrentRank(Document doc, Team team) {
        LocalDate date = parseMatchDate(doc.getElementsByAttributeValue("class", "match-page").get(0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MMMM").withLocale(Locale.US);
        String date1 = date.format(formatter).toLowerCase();
        return team.getRank(date1);
    }


    private Pattern compilePattern(String pattern1) {
        return Pattern.compile(pattern1);
    }

    public void parseTournamets() throws IOException {
        String[] linksToParse = {
                "https://www.hltv.org/events/archive?eventType=INTLLAN",
                "https://www.hltv.org/events/archive?offset=50&eventType=INTLLAN",
        };
        for (String aLinksToParse : linksToParse) {
            parseEventPage(aLinksToParse);
        }
        Document doc;
        try {
            doc = Jsoup.connect("https://www.hltv.org/events?eventType=INTLLAN#tab-FEATURED").userAgent("Mozilla")
                       .referrer("https://www.hltv.org/").get();
        } catch (SocketException e) {
            doc = Jsoup.connect("https://www.hltv.org/events?eventType=INTLLAN#tab-FEATURED").userAgent("Mozilla")
                       .referrer("https://www.hltv.org/").get();
        }
        Elements elements = doc.getElementsByAttributeValue("class", "a-reset ongoing-event");
        elements.forEach(element -> {
            Tiers put=tournaments.put(element.child(0).child(0).attr("title"), Tiers.BIGLAN);
            if(put==null){
                futureTournaments.add(element.child(0).child(0).attr("title"));
            }

        });
        tournaments.put("IEM Katowice 2019", Tiers.MAJOR);
        tournaments.put("FACEIT Major 2018", Tiers.MAJOR);
        try {
            doc = Jsoup.connect("https://www.hltv.org/events?eventType=INTLLAN").userAgent("Mozilla")
                       .referrer("https://www.hltv.org/").get();
        } catch (SocketException e) {
            doc = Jsoup.connect("https://www.hltv.org/events?eventType=INTLLAN").userAgent("Mozilla")
                       .referrer("https://www.hltv.org/").get();
        }
        elements = doc.getElementsByAttributeValue("class", "big-event-info");
        elements.forEach(element -> {
           Tiers put= tournaments.put(element.child(0).child(0).text(), Tiers.BIGLAN);
            if(put==null){
                futureTournaments.add(element.child(0).child(0).text());
            }
        });
        System.out.println(futureTournaments.size());
    }


    private void parseEventPage(String link) throws IOException {
        Document doc;
        try {
            doc = Jsoup.connect(link).userAgent("Mozilla").referrer("https://www.hltv.org/").get();
        } catch (SocketException e) {
            doc = Jsoup.connect(link).userAgent("Mozilla").referrer("https://www.hltv.org/").get();
        }
        Elements elements = doc.getElementsByAttributeValue("class", "col-value event-col");
        int a;
        Element element;

        for (a = 0; a < 50; a++) {
            element = elements.get(a);
            int prizePool;
            try {
                String res = element.siblingElements().get(1).text().replace("$", "").replace(",", "");
                prizePool = Integer.parseInt(res);
                if (prizePool >= 100000) {
                    res = element.child(0).text();
                    tournaments.put(res, Tiers.BIGLAN);
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
    }

/**/
    public ArrayList<String> getFutureTournaments() {
        return new ArrayList<>(futureTournaments);
    }

    public HashMap<String, Team> getTeams() {
        return new HashMap<>(teams);
    }

    public ArrayList<Team> getTeamsSet() {
        return new ArrayList<>(teamsSet);
    }

    public HashMap<String, Tiers> getTournaments() {
        return new HashMap<>(tournaments);
    }

    public HashMap<String, Match> getMatches() {
        return new HashMap<>(matches);
    }
}

