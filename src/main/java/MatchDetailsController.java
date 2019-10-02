import Parser.HLTVparser;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class MatchDetailsController {
    HLTVparser hltv;
    MainController main;
    @FXML
    JFXComboBox<Label> comboBox;
    @FXML
    JFXComboBox<Label> comboTournaments;
    @FXML
    Label error_label;
    @FXML
    AnchorPane details;

    private String selectedEvent;
    private String selectedMatchType;
    private int selectedMatchTypeIndex;





    @FXML
    private void initialize() {
            try {
                hltv = (HLTVparser) new ObjectInputStream(getClass().getResourceAsStream("hltv.out")).readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException("NoSuchFile");
            }
        comboBox.getItems().add(new Label("Best of 1"));
        comboBox.getItems().add(new Label("Best of 3"));
        comboBox.getItems().add(new Label("Best of 5"));
        comboBox.getSelectionModel().select(0);
        ArrayList<String> tournaments = hltv.getFutureTournaments();
        tournaments.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        ArrayList<Label> labels = new ArrayList<>();
        tournaments.forEach(t -> {
            Label label = new Label(t);
            labels.add(label);
        });
        comboTournaments.getItems().addAll(labels);
        selectedMatchType=comboBox.getSelectionModel().getSelectedItem().getText();
    }


    public void save(ActionEvent event) {
        int selectIndex=comboBox.getSelectionModel().getSelectedIndex();

        if(!comboTournaments.getSelectionModel().isEmpty()){
            if(selectedMatchTypeIndex>selectIndex){
                main.clearCheckboxes();
            }
            else if(selectedMatchTypeIndex<selectIndex) {
                main.enableCheckboxes();
            }
            selectedMatchTypeIndex=selectIndex;
            selectedMatchType=comboBox.getSelectionModel().getSelectedItem().getText();
            error_label.setVisible(false);
            selectedEvent=comboTournaments.getSelectionModel().getSelectedItem().getText();
        }
        else {
            comboBox.getSelectionModel().select(0);
            error_label.setVisible(true);
        }
    }

    public void cancel(ActionEvent event) {
        selectedMatchType=comboBox.getItems().get(0).getText();
        selectedEvent=null;
        comboTournaments.getSelectionModel().clearSelection();
        comboBox.getSelectionModel().select(0);
    }

    public AnchorPane getPane() {
        return details;
    }

    public String getMatchType(){
        return selectedMatchType;
    }

    public String getTournament(){
        return selectedEvent;
    }

    public void init(MainController controller) {
        main = controller;
    }


}
