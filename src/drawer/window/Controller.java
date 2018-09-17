package drawer.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import drawer.view.ImageDrawer;

public class Controller {

    @FXML
    private VBox mainVBox;
    @FXML
    private TextField fileNameField;

    private ImageDrawer drawer = new ImageDrawer(800);

    @FXML
    public void initialize(){
        mainVBox.getChildren().add(0, drawer);
    }

    public void openFile(ActionEvent actionEvent) {
    }

    public void loadImage(ActionEvent actionEvent) {
    }

    public void saveImage(ActionEvent actionEvent) {
    }

    public void clearImage(ActionEvent actionEvent) {
        drawer.clear();
    }
}
