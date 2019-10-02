import Parser.HLTVparser;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainController {
    HLTVparser hltv;

    @FXML
    AnchorPane parent;
    @FXML
    AnchorPane mapPick;
    @FXML
    AnchorPane predictWindow;
    @FXML
    AnchorPane matchDetails;



    @FXML
    private MapPickController mapPickController;
    @FXML
    private PredictWindowController predictWindowController;
    @FXML
    private MatchDetailsController matchDetailsController;
    @FXML
    JFXButton mapsButton, predictButton, detailsButton;

    AnchorPane activePane;

    JFXButton current;

    private double x, y;
    Stage stage;

    static final String style1 = "-fx-border-width: 0px 0px 3px 0px";
    static final String defaultStyle = "-fx-border-width: 0px 0px 0px 0px";

    public void open_maps() {
        buttonScript(mapsButton, mapPick);
    }


    @FXML
    private void initialize() {
        try {
            hltv = (HLTVparser) new ObjectInputStream(getClass().getResourceAsStream("hltv.out")).readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("NoSuchFile");
        }

        predictWindowController.init(this);
        mapPickController.init(this);
        matchDetailsController.init(this);

        mapPick.setVisible(false);
        predictWindow.setVisible(false);
        matchDetails.setVisible(false);
        makeDragable();

    }

    public void open_details() {
        buttonScript(detailsButton, matchDetails);

    }
    public void shutdown() {
        Platform.exit();

    }
    public void open_predict() {

        buttonScript(predictButton, predictWindow);
    }

    private void buttonScript(JFXButton button, AnchorPane pane) {
        if (activePane != null) {
            activePane.setVisible(false);
        }
        if (activePane != pane) {
            activePane = pane;
            pane.setVisible(true);
        }
        if (current != button) {
            if (current != null) {
                current.setStyle(defaultStyle);
            }
            current = button;
            current.setStyle(style1);
        }
    }

    public HLTVparser getHltv() {
        return hltv;
    }

    private void makeDragable() {
        parent.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        parent.setOnMouseDragged(event -> {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });
    }

    ArrayList<String> gtMaps() {
        return mapPickController.getSelectedMaps();
    }

    int getMatchType() {
        switch (matchDetailsController.getMatchType().toLowerCase()) {
        case "best of 1":
            return 1;
        case "best of 3":
            return 2;
        case "best of 5":
            return 3;
        default:
            return 1;
        }
    }

    String getEvent() {
        return matchDetailsController.getTournament();
    }

    void clearCheckboxes() {
      mapPickController.clearCheckboxes();
    }

    void enableCheckboxes() {
       mapPickController.enableCheckboxes();
    }
}

