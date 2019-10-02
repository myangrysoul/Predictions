/*
package Parser;

import Parser.Match.Match;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Scaler {
    private final ArrayList<Team> teamsSet;
    private final HashMap<String, Match> matches;
    private final ArrayList<String> maps;

    public Scaler(HLTVparser hltv) {
        teamsSet=hltv.getTeamsSet();
        matches=hltv.getMatches();
        maps= new ArrayList<>(
                Arrays.asList("Dust2", "Inferno", "Train", "Nuke", "Cache", "Mirage", "Vertigo", "Overpass"));
    }

    public ArrayList<Double> mapsToVec(ArrayList<String> maps) {
        ArrayList<Double> vector = new ArrayList<Double>(Arrays.asList(0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D));
        for (String map : maps
        ) {
            vector.set(this.maps.indexOf(map), 1D);
        }
        return vector;
    }

    private ArrayList<Double> scale(int... integers){
        ArrayList<Double> values=new ArrayList<>();
        for (int ints:integers){
            values.add((double)ints);
        }
        return values;
    }


    public ArrayList<Double> matchScale(Match match,Team mainTeam, boolean refferingMatch ){
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
        ArrayList<Double> vector = new ArrayList<Double>(scale(t1.getKey().getCode(),t2.getKey().getCode()));
        if(!refferingMatch){
            if(t1.getValue()+t2.getValue()>15){
                vector.addAll(scale(t1.getValue()>t2.getValue()?1:0));
                vector.addAll(scale(t2.getValue()>t1.getValue()?1:0));
            }
            else{
                vector.addAll(scale(t1.getValue()));
                vector.addAll(scale(t2.getValue()));
            }
        }
        vector.addAll(scale(match.getMatchType(),match.getEventTier().getTier(),r1,r2));
        vector.addAll(mapsToVec(match.getMapsPlayed()));
        return vector;
    }

    public Patterns toPatterns() {
        ArrayList<ArrayList<Double>> refferingMatches = new ArrayList<>();
        ArrayList<ArrayList<Double>> hth = new ArrayList<>();
        ArrayList<ArrayList<Double>> vecM = new ArrayList<>();
        ArrayList<ArrayList<Double>> resV = new ArrayList<>();
        ArrayList<Double> answers = new ArrayList<>();
        int vecLen =matchScale(
                matches.get("https://www.hltv.org/matches/2333175/mousesports-vs-windigo-dreamhack-open-tours-2019"),
                teamsSet.get(0), false).size();
        ArrayList<Match> hTh;
        for (Team team : teamsSet
        ) {
            ArrayList<Match> matches = new ArrayList<>(team.getMatches());
            for (int i = 0; i < matches.size() - 5; i++) {
                hTh = new ArrayList<>(matches.get(i).getPastHtH());
                answers.add(matches.get(i).getAnswer(team));
                refferingMatches.add(matchScale(matches.get(i), team, true));
                if (hTh.isEmpty()) {
                    hth.add(new ArrayList<Double>(Collections.nCopies(vecLen, 0D)));
                    hth.add(new ArrayList<Double>(Collections.nCopies(vecLen, 0D)));
                } else {
                    for (int k = 0; k < 2; k++) {
                        if (hTh.size() < 2 && k == 1) {
                            hth.add(new ArrayList<Double>(Collections.nCopies(vecLen, 0D)));
                        } else {
                            hth.add(matchScale(hTh.get(k), team, false));
                        }
                    }
                }
            }
            for (int i = 1; i < matches.size(); i++) {
                vecM.add(matchScale(matches.get(i), team, false));
            }

        }
        ArrayList<Double> buff;
        for (int i = 0; i < refferingMatches.size(); i++) {
            buff = new ArrayList<>(refferingMatches.get(i));
            for (int k = i * 2; k < i * 2 + 2; k++) {  //0 0<2  1 2 <4 4<6
                ArrayList<Double> abc = hth.get(k);
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
*/
