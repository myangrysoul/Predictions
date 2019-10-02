import NeuralNet.NeuralNet;
import Parser.HLTVparser;
import Parser.Predict;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PredictWindowController {
    MainController main;
    HLTVparser hltv;
    NeuralNet net;

    @FXML
    private AnchorPane predictWindow;

    @FXML
    private Circle logo1;

    @FXML
    private Circle logo2;

    @FXML
    private Label score1;

    @FXML
    private Label score2;

    @FXML
    private JFXButton predictButton;

    @FXML
    private JFXComboBox<Label> team1;

    @FXML
    private JFXComboBox<Label> team2;

    @FXML
    private void initialize() {
        try {
            hltv = (HLTVparser) new ObjectInputStream(getClass().getResourceAsStream("hltv.out")).readObject();
            net = (NeuralNet) new ObjectInputStream(getClass().getResourceAsStream("Netx.ser")).readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("NoSuchFile");
        }
        ArrayList<Label> labels = new ArrayList<>();
        hltv.getTeamsSet().forEach(team -> {
            labels.add(new Label(team.getName()));
        });
        ArrayList<Label> labels1=new ArrayList<>();
        labels.forEach(label -> {
            labels1.add(new Label(label.getText()));
        });
        team1.getItems().addAll(labels);
        team2.getItems().addAll(labels1);
        team1.valueProperty().addListener(new ChangeListener<Label>() {
            @Override
            public void changed(ObservableValue<? extends Label> observableValue, Label label, Label t1) {
                Image im;
                if (!t1.equals(label)) {
                    im = new Image(getClass().getResourceAsStream("Teams/" + t1.getText() + ".png"));
                    logo1.setFill(new ImagePattern(im));
                    if(logo1.getStrokeWidth()>0){
                        logo1.setStrokeWidth(0);
                    }

                }
            }
        });
        team2.valueProperty().addListener(new ChangeListener<Label>() {
            @Override
            public void changed(ObservableValue<? extends Label> observableValue, Label label, Label t1) {
                Image im;
                if (!t1.equals(label)) {
                    im = new Image(getClass().getResourceAsStream("Teams/" + t1.getText() + ".png"));
                    logo2.setFill(new ImagePattern(im));
                    if(logo2.getStrokeWidth()>0){
                        logo2.setStrokeWidth(0);
                    }
                }
            }
        });
    }

    public void init(MainController controller) {
        main = controller;
    }

    public void prediction(ActionEvent event) {
        try {
            buttonScript();
        } catch (NullPointerException e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Missing information to predict a match");
            errorAlert.setContentText(e.getMessage());
            DialogPane dialogPane = errorAlert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("config.css").toExternalForm());
            dialogPane.getStyleClass().add("myDialog");
            Scene scene = dialogPane.getScene();
            Stage stage = (Stage) scene.getWindow();
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.UNDECORATED);
            Stage mainStage =(Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setY(mainStage.getY()+150);
            stage.setX(mainStage.getX()+5);
            errorAlert.showAndWait();
        }
    }

    private void buttonScript() {
        if (!team1.getSelectionModel().isEmpty() && !team2.getSelectionModel().isEmpty()) {
            String team1 = this.team1.getSelectionModel().getSelectedItem().getText();
            String team2 = this.team2.getSelectionModel().getSelectedItem().getText();
            if (main.getEvent() == null) {
                throw new NullPointerException("Select tournament to predict a match");
            }
            if (main.gtMaps() == null || main.gtMaps().isEmpty()) {
                throw new NullPointerException("Select maps to be played to predict a match");
            }
            Predict predict = new Predict(hltv, team1,
                                          team2, main.getMatchType(),
                                          main.getEvent(), main.gtMaps());
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
            score1.setText(Integer.toString((int) Math.round(res1 * 100))+'%');
            score2.setText(Integer.toString((int) Math.round(res2 * 100))+'%');
            score1.setVisible(true);
            score2.setVisible(true);

        } else {
            throw new NullPointerException("Pick teams to predict a match");
        }
    }
}
