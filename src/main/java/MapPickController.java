import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Arrays;

public class MapPickController {
    MainController main;
    private int trueCheckboxes = 0;
    @FXML
    private Circle first, second, third, fourth, fifth;
    @FXML
    private JFXCheckBox dust2, inferno, nuke, overpass, train, vertigo, mirage, cache;
    @FXML
    JFXButton save;
    private ArrayList<ImagePattern> patterns;
    private ArrayList<JFXCheckBox> checkBoxes;
    private ArrayList<Circle> circles;

    private ArrayList<String> selectedMaps;
    @FXML
    private AnchorPane mapPane;


    @FXML
    private void accept(ActionEvent actionEvent) {
        mapAccept();
    }

    @FXML
    private void cancel(ActionEvent event) {
        checkBoxes.forEach(cb -> cb.setSelected(false));
        circles.forEach(c -> {
            c.setVisible(false);
            c.fillProperty().unbind();
        });
        selectedMaps.clear();

    }

    private void mapAccept() {
        int matchtype = main.getMatchType() * 2 - 1;
        selectedMaps.clear();
        circles.forEach(c -> {
            c.setVisible(false);
            c.fillProperty().unbind();
        });
        int c = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            JFXCheckBox cb = checkBoxes.get(i);
            if (cb.isSelected() && c < 5) {
                selectedMaps.add(cb.getText());
                circles.get(c).setFill(patterns.get(i));
                circles.get(c).setVisible(true);
                c++;
            }
        }
        // TODO O(KO)SHE(CHK)O (A)SHIBKI))
        //TODO MakeAlertWindow
    }

    @FXML
    private void initialize() {
        selectedMaps = new ArrayList<>();
        patterns = new ArrayList<>();
        checkBoxes = new ArrayList<>(Arrays.asList(dust2, inferno, nuke, overpass, train, vertigo, mirage, cache));
        circles = new ArrayList<>(Arrays.asList(first, second, third, fourth, fifth));
        Image im;
        for (JFXCheckBox cb : checkBoxes) {
            im = new Image(getClass().getResourceAsStream("Maps/" + cb.getId() + ".png"));
            patterns.add(new ImagePattern(im));
        }
        circles.forEach(c -> c.setVisible(false));

    }


    @FXML
    private void checkbox(ActionEvent event) {
        int matchtype = main.getMatchType() + main.getMatchType() - 1;
        JFXCheckBox checkBox = (JFXCheckBox) event.getSource();
        if (checkBox.isSelected()) {
            trueCheckboxes++;
            if (trueCheckboxes >= matchtype) {
                disableCheckboxes();
            }
        } else {
            if (trueCheckboxes == matchtype) {
                enableCheckboxes();
            }
            trueCheckboxes--;
        }
    }

    private void disableCheckboxes() {
        checkBoxes.forEach(checkBox -> {
            if (!checkBox.isSelected()) {
                checkBox.setDisable(true);
            }
        });
    }

    void clearCheckboxes() {
        trueCheckboxes = 0;
        checkBoxes.forEach(checkBox -> {
            if (checkBox.isSelected()) {
                checkBox.setSelected(false);
            }
            if (checkBox.isDisabled()) {
                checkBox.setDisable(false);
            }
        });
        selectedMaps.clear();
        circles.forEach(c -> {
            c.setVisible(false);
            c.fillProperty().unbind();
        });
    }

    void enableCheckboxes() {
        checkBoxes.forEach(checkBox -> {
            if (checkBox.isDisabled()) {
                checkBox.setDisable(false);
            }
        });
    }

    public AnchorPane getMapPane() {
        return mapPane;
    }

    public ArrayList<String> getSelectedMaps() {
        return new ArrayList<>(selectedMaps);
    }

    public void init(MainController controller) {
        main = controller;
    }
}
