package Parser;

import Parser.Match.Match;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;

  enum SerializedComp implements Comparator<Match> {
    INSTANCE;
    @Override
    public int compare(Match o1, Match o2) {
        if(o1.equals(o2)||o1.getMatchId()==o2.getMatchId()) {
            return 0;
        } else{
            if(o1.getDate().isAfter(o2.getDate())){
                return -1;
            }
            else if(o1.getDate().isEqual(o2.getDate())){
                return o1.getMatchId()>o2.getMatchId()?-1:1;
            }
            else {
                return 1;
            }

        }
    }
}