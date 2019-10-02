package Parser;

import Parser.Match.Binary;
import Parser.Match.Match;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Predict {
    HLTVparser hltv;
   Binary binary;
    Match match;
    Team t1;
    Team t2;

    public Predict(HLTVparser hltv, String team, String team1, int matchType, String tournament,ArrayList<String> maps) {
        this.hltv = hltv;
        t1 = hltv.getTeams().get(team);
        t2 = hltv.getTeams().get(team1);
        match = new Match(new Pair<>(t1, null),
                          new Pair<>(t2, null),
                          t1.getCode(), t2.getCode(), matchType, new ArrayList<String>(maps),
                          hltv.getTournaments().getOrDefault(tournament, Tiers.OTHER));
       binary =new Binary(hltv);
    }

    public double[] getInputV(String team) {
        ArrayList<Match> hTh;
        Team t1 = hltv.getTeams().get(team);
        ArrayList<Match> matches = t1.getMatches();
        ArrayList<Integer> vec = new ArrayList<Integer>(binary.matchToVector(match, t1, true));
        ArrayList<ArrayList<Integer>> hth = new ArrayList<>();
        int vecLen = binary.matchToVector(matches.get(0), t1, false).size();
        hTh = findHtH(matches, team);
        if (hTh.isEmpty()) {
            hth.add(new ArrayList<Integer>(Collections.nCopies(vecLen, 0)));
            hth.add(new ArrayList<Integer>(Collections.nCopies(vecLen, 0)));
        } else {
            for (int k = 0; k < 2; k++) {
                if (hTh.size() < 2 && k == 1) {
                    hth.add(new ArrayList<Integer>(Collections.nCopies(vecLen, 0)));
                } else {
                    hth.add(binary.matchToVector(hTh.get(k), t1, false));
                }
            }
        }
        for (int i = 0; i < hth.size(); i++) {
            vec.addAll(hth.get(i));
        }
        for (int i = 0; i < 5; i++) {
            vec.addAll(binary.matchToVector(matches.get(i), t1, false));
        }
        return listToArray(vec);
    }


    private double[] listToArray(ArrayList<Integer> arrayList) {
        double[] mas = new double[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            mas[i] = arrayList.get(i);
        }
        return mas;
    }

    private ArrayList<Match> findHtH(ArrayList<Match> matches, String team1) {
        ArrayList<Match> hth = new ArrayList<>(2);
        Team team = t1.getName().equals(team1)? t2 : t1;
        for (Match match1 : matches) {
            if (match1.getTeam1Score().getKey().equals(team)||match1.getTeam2Score().getKey().equals(team)) {
                hth.add(match1);
                if (!match1.getPastHtH().isEmpty()) {
                    hth.add(match1.getPastHtH().get(0));
                }
                break;
            }
        }
        return hth;
    }
}