import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = new Pane();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        try {
            parent = FXMLLoader.load(getClass().getResource("Main.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Predictions");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon/appIcon.png")));
        primaryStage.show();
        primaryStage.toFront();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
    /* *//* Document doc = Jsoup.connect("https://liquipedia.net/counterstrike/HLTV/Team_Ranking").userAgent("Mozilla").get();
        Elements elements = doc.getElementsByClass("team-template-image");
        int i = 0;
        for (Element elems : elements) {
            URL url = new URL("https://liquipedia.net/"+elems.child(0).child(0).attr("src"));
            try {
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();
                FileOutputStream fos = new FileOutputStream(
                        "D:/Predictions/src/main/resources/Teams/" + elems.child(0).attr("title") + ".png");
                fos.write(response);
                fos.close();
                if (i == 19) {
                    break;
                }
                i++;
            } catch (IOException e) {
                throw new RuntimeException("vse huevo");
            }

        }*//*
        HLTVparser hltv;
        NeuralNet net;
         try {
            ObjectInputStream is1 = new ObjectInputStream(new FileInputStream("hltv.out"));
             ObjectInputStream is = new ObjectInputStream(new FileInputStream("Netx.ser"));
           hltv= (HLTVparser) is1.readObject();
           net = (NeuralNet) is.readObject();
           ArrayList<Integer> expectedValue=new ArrayList<>();
             for (Team team:hltv.getTeamsSet()) {
                 int counter=0;
                 ArrayList<Match> matches=new ArrayList<>(team.getMatches());
                 int value=0;
                 for (Match match:matches) {
                     if(match.getTeam1Score().getKey().equals(team)){
                         ArrayList<Integer> score=match.getMapScores();
                         for(int i = 0;i<score.size(); i += 2){
                             value+=score.get(i);
                             counter++;
                         }
                     }
                     else if(match.getTeam2Score().getKey().equals(team)){
                         ArrayList<Integer> score=match.getMapScores();
                         for(int i = 1;i<score.size(); i += 2){
                             value+=score.get(i);
                             counter++;
                         }
                     }
                     else {
                         throw new NullPointerException("ADA");
                     }
                 }
                 expectedValue.add(value/counter);
             }
             ArrayList<Team> teams=new ArrayList<>(hltv.getTeamsSet());

             for (Team team:teams) {
                 float ver=0;
                 ArrayList<Match> matches=new ArrayList<>(team.getMatches());
                 Match match=matches.get(0);
                 System.out.println(match);
                 double exp=0;
                 int exp1=0;
                 if(match.getTeam1Score().getKey().equals(team)){
                     exp=expectedValue.get(teams.indexOf(team));
                     exp=Math.pow(exp,16)/factorial(16) * Math.exp(-exp);
                     exp1=expectedValue.get(teams.indexOf(match.getTeam2Score().getKey()));
                     for(int i=0;i<15;i++){
                         ver+= Math.pow(exp1, i) / factorial(i) * Math.exp(-exp1);
                     }
                 }
                 else if(match.getTeam2Score().getKey().equals(team)){
                     exp=expectedValue.get(teams.indexOf(team));
                     exp=Math.pow(exp,16)/factorial(16) * Math.exp(-exp);
                     exp1=expectedValue.get(teams.indexOf(match.getTeam1Score().getKey()));
                     for(int i=0;i<15;i++){
                         ver+= Math.pow(exp1, i) / factorial(i) * Math.exp(-exp1);
                     }
                 }
                 System.out.println(exp*ver);
             }



      *//*     ArrayList<String> maps=new ArrayList<>(
                   Arrays.asList("Dust2", "Inferno", "Train"));
             for (Team team:hltv.getTeamsSet()
                  ) {
                 System.out.println(team.getName());
                 test(net,hltv,"ENCE",team.getName(),2,"ECS Season 7 Finals",maps);
             }
*//*           *//*Binary binary=new Binary(hltv);
           net=new NeuralNet(316,300,10);

             study.initNet(binary);
             study.study();*//*
        } catch (FileNotFoundException | ClassNotFoundException | InvalidClassException e) {
            System.out.println(e);
            hltv = new HLTVparser();
            hltv.parseTournamets();
            hltv.parseTeams();
            hltv.parseMatches();

        }
    }
   static long factorial(long n) {return n <= 1 ? 1 : n*factorial(n-1);}
    static  void test(NeuralNet net,HLTVparser hltv,String team1,String team2, int matchtype,String event,ArrayList<String> maps){
        Predict predict = new Predict(hltv, team1,
                                      team2, matchtype,
                                      event,maps);
        double[] a = predict.getInputV(team1);
        double[] b = predict.getInputV(team2);
        net.setInputLayer(a);
        double[] a1 = net.prohod().clone();
        net.setInputLayer(b);
        double[] b1 = net.prohod().clone();
        double ind = 0;
        double ind1 = 0;
        double sum = 0;
        double sum1 = 0;
        double max1 = 0;
        double max2 = 0;
        for (int i = 0; i < a1.length; i++) {
            sum += a1[i];
            sum1 += b1[i];
        }
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] / sum > max1) {
                max1 = a1[i] / sum;
                ind = i;
            }
            if (b1[i] / sum1 > max2) {
                max2 = b1[i] / sum1;
                ind1 = i;
            }

        }
        double res1 = (ind / 10 + (1 - ind1 / 10)) / 2;
        double res2 = (ind1 / 10 + (1 - ind / 10)) / 2;
        System.out.println("Team 1:"+Math.round(res1 * 100) / 100D);
      System.out.println("Team 2:"+Math.round(res2 * 100) / 100D);
    }*/
      /*  try {
            ObjectInputStream is1 = new ObjectInputStream(new FileInputStream("Net4.ser"));
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("hltv.out"));
            hltv = (HLTVparser) is.readObject();
            //hltv.restartParseMatches();
            net =(NeuralNet) is1.readObject();
            Study study = new Study(net);
            study.study();
        } catch (FileNotFoundException | ClassNotFoundException | InvalidClassException e) {
            System.out.println(e);
            hltv = new HLTVparser();
            hltv.parseTournamets();
            hltv.parseTeams();
            hltv.parseMatches();
            for (Team team : hltv.getTeams().values()
            ) {
                System.out.println(team.getMatches());
            }

        }*/
}



