package Parser.Match;

import Parser.HLTVparser;
import Parser.Patterns;
import Parser.Team;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Binary {
    private final ArrayList<Team> teamsSet;
    private final HashMap<String, Match> matches;
    private final ArrayList<String> maps;

    public Binary(HLTVparser hltv) {
       teamsSet=hltv.getTeamsSet();
       matches=hltv.getMatches();
       maps= new ArrayList<>(
               Arrays.asList("Dust2", "Inferno", "Train", "Nuke", "Cache", "Mirage", "Vertigo", "Overpass"));
    }
    public ArrayList<Integer> mapsToVec(ArrayList<String> maps) {
        ArrayList<Integer> vector = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
        for (String map : maps
        ) {
            vector.set(this.maps.indexOf(map), 1);
        }
        return vector;
    }

    public ArrayList<Integer> toBinaryVector(int number, int scale) {
        StringBuilder res = new StringBuilder(Integer.toUnsignedString(number, 2)).reverse();
        if (res.length() != scale) {
            int a = scale - res.length();
            for (int i = 0; i < a; i++) {
                res.append(0);

            }
        }
        res.reverse();
        ArrayList<Integer> bits = new ArrayList<>();
        for (int i = 0; i < res.length(); i++) {
            bits.add(Character.getNumericValue(res.charAt(i)));
        }
        return bits;
    }

   public ArrayList<Integer> matchToVector(Match match, Team mainTeam, boolean refferingMatch) {
        int r1;
        int r2;
        Pair<Team, Integer> t1;
        Pair<Team, Integer> t2;
        if (match.getTeam1Score().getKey().equals(mainTeam)) {
            r1 = match.getRank1();
            r2 = match.getRank2();
            t1 = match.getTeam1Score();
            t2 = match.getTeam2Score();
        } else {
            r2 = match.getRank1();
            r1 = match.getRank2();
            t1 = match.getTeam2Score();
            t2 = match.getTeam1Score();
        }
        ArrayList<Integer> vector = new ArrayList<Integer>(toBinaryVector(t1.getKey().getCode(), 6));
        ArrayList<Integer> bits = new ArrayList<>(toBinaryVector(t2.getKey().getCode(), 6));
        vector.addAll(bits);
        if (!refferingMatch) {
            if(t1.getValue()+t2.getValue()>15){
                bits=t1.getValue()>t2.getValue()?toBinaryVector(1,2):toBinaryVector(0,2);
                vector.addAll(bits);
                bits=t2.getValue()>t1.getValue()?toBinaryVector(1,2):toBinaryVector(0,2);
            }
            else{
                bits = toBinaryVector(t1.getValue(), 2);
                vector.addAll(bits);
                bits = toBinaryVector(t2.getValue(), 2);
            }
            vector.addAll(bits);

        }
        bits = toBinaryVector(match.getMatchType(), 2);
        vector.addAll(bits);
        bits = mapsToVec(match.getMapsPlayed());
        vector.addAll(bits);
        bits = toBinaryVector(match.getEventTier().getTier(), 2);
        vector.addAll(bits);
        bits = toBinaryVector(r1, 6);
        vector.addAll(bits);
        bits = toBinaryVector(r2, 6);
        vector.addAll(bits);
        if (vector.size() > 46) {
            throw new RuntimeException();
        }
        return vector;
    }

    public Patterns toPatterns() {
        ArrayList<ArrayList<Integer>> refferingMatches = new ArrayList<>();
        ArrayList<ArrayList<Integer>> hth = new ArrayList<>();
        ArrayList<ArrayList<Integer>> vecM = new ArrayList<>();
        ArrayList<ArrayList<Integer>> resV = new ArrayList<>();
        ArrayList<Double> answers = new ArrayList<>();
        int vecLen = matchToVector(
                matches.get("https://www.hltv.org/matches/2333175/mousesports-vs-windigo-dreamhack-open-tours-2019"),
                teamsSet.get(0), false).size();
        ArrayList<Match> hTh;
        for (Team team : teamsSet
        ) {
            ArrayList<Match> matches = new ArrayList<>(team.getMatches());
            for (int i = 0; i < matches.size() - 5; i++) {
                hTh = new ArrayList<>(matches.get(i).getPastHtH());
                answers.add(matches.get(i).getAnswer(team));
                refferingMatches.add(matchToVector(matches.get(i), team, true));
                if (hTh.isEmpty()) {
                    hth.add(new ArrayList<Integer>(Collections.nCopies(vecLen, 0)));
                    hth.add(new ArrayList<Integer>(Collections.nCopies(vecLen, 0)));
                } else {
                    for (int k = 0; k < 2; k++) {
                        if (hTh.size() < 2 && k == 1) {
                            hth.add(new ArrayList<Integer>(Collections.nCopies(vecLen, 0)));
                        } else {
                            hth.add(matchToVector(hTh.get(k), team, false));
                        }
                    }
                }
            }
            for (int i = 1; i < matches.size(); i++) {
                vecM.add(matchToVector(matches.get(i), team, false));
            }

        }
        ArrayList<Integer> buff;
        for (int i = 0; i < refferingMatches.size(); i++) {
            buff = new ArrayList<>(refferingMatches.get(i));
            for (int k = i * 2; k < i * 2 + 2; k++) {  //0 0<2  1 2 <4 4<6
                ArrayList<Integer> abc = hth.get(k);
                buff.addAll(abc);
            }
            for (int c = i; c < i + 5; c++) {
                buff.addAll(vecM.get(c));
            }
            resV.add(buff);
        }
        return new Patterns(resV, answers);
    }
}
