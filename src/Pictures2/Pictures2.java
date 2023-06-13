package Pictures2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

public class Pictures2 extends Application {

//    double startX;
//    double startY;
    private boolean hasContent = false;
    Stage stage;
    ColorPicker cpDrawStroke;
    Slider spStrokeWidth;
    ImageView imageView;
    Canvas canvas;
    GraphicsContext gc;
    Button loadBtn;
    Button clearBtn;
    Button saveBtn;
    BorderPane workspace = setWorkspace();
    HBox controls = setControls();
    double newImageWidth;
    double newImageHeight;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        clearBtn.setDisable(true);
        saveBtn.setDisable(true);
        BorderPane root = new BorderPane();
        root.setCenter(workspace);
        root.setBottom(controls);
        Scene scene = new Scene(root, 1200, 500);
        stage.setScene(scene);
        stage.setOnCloseRequest(we -> {
            setCloseRequest(we);
        });
        stage.show();
    }

    private void setCloseRequest(WindowEvent we) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Close??");
        alert.setContentText("Are you sure you wanna close?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.YES)) {
            Platform.exit();
        } else {
            we.consume();
        }
    }

    private BorderPane setWorkspace() {
        workspace = new BorderPane();
        workspace.setStyle("-fx-background-color: #e9684a; -fx-border-color: #745b81; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 10");
        imageView = new ImageView();
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();

//        canvas.setOnMousePressed(e -> {
//            gc.setStroke(cpDrawStroke.getValue());
//            gc.setLineWidth(spStrokeWidth.getValue());
//
//            startX = e.getX();
//            startY = e.getY();
//        });
//
//        canvas.setOnMouseDragged(e -> {
//            double endX = e.getX();
//            double endY = e.getY();
//            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());  // Clear the canvas
//            drawRectangle(startX, startY, endX, endY);
//        });
//
//        canvas.setOnMouseReleased(e -> {
//            double endX = e.getX();
//            double endY = e.getY();
//            drawRectangle(startX, startY, endX, endY);
//        });
        canvas.setOnMousePressed(e -> {
            gc.setStroke(cpDrawStroke.getValue());
            gc.setLineWidth(spStrokeWidth.getValue());

            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
            hasContent = true;
            clearBtn.setDisable(false);
            saveBtn.setDisable(false);
        });

        canvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseReleased(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            gc.closePath();
        });

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageView, canvas);
        workspace.setCenter(stackPane);
        return workspace;
    }

    private void loadPicture() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("ALL", "*.*")
        );

        File file = fc.showOpenDialog(stage);
        if (file != null) {
            try {
                Image image = new Image(new FileInputStream(file));
                imageView.setImage(image);
                fitImageToWorkspace();
                canvas.setWidth(newImageWidth);
                canvas.setHeight(newImageHeight);
            } catch (FileNotFoundException ex) {
                ex.getStackTrace();
            }
        }
    }

    private HBox setControls() {
        controls = new HBox();
        cpDrawStroke = new ColorPicker(Color.ALICEBLUE);
        setSlider();
        loadBtn = new Button("Load");
        clearBtn = new Button("Clear");
        saveBtn = new Button("Save");
        controls.setAlignment(Pos.CENTER);
        loadBtn.setOnAction(e -> loadPicture());
        clearBtn.setOnAction(e -> clearDrawing());
        saveBtn.setOnAction(e -> saveDrawing());
        controls.getChildren().addAll(loadBtn, clearBtn, saveBtn, cpDrawStroke, spStrokeWidth);
        controls.setSpacing(15);
        controls.setMinHeight(80);
        controls.setStyle("-fx-border-color: #d0b281; -fx-border-width: 5; -fx-border-radius: 15; -fx-background-color: #e93e35; -fx-background-radius: 15");
        return controls;
    }

    private void setSlider() {
        spStrokeWidth = new Slider(0, 30, 5);
        spStrokeWidth.setBlockIncrement(5);
        spStrokeWidth.setShowTickMarks(true);
        spStrokeWidth.setShowTickLabels(true);
        spStrokeWidth.setMinorTickCount(5);
        spStrokeWidth.setMajorTickUnit(5);
        spStrokeWidth.snapToTicksProperty();
        spStrokeWidth.setPrefWidth(200);
    }

    private void fitImageToWorkspace() {
        double workspaceWidth = workspace.getWidth();
        double workspaceHeight = workspace.getHeight();
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();
        double scaleFactor = Math.min(workspaceWidth / imageWidth, workspaceHeight / imageHeight);
        newImageWidth = imageWidth * scaleFactor;
        newImageHeight = imageHeight * scaleFactor;
        imageView.setFitWidth(newImageWidth);
        imageView.setFitHeight(newImageHeight);

    }

//    private void drawRectangle(double startX, double startY, double endX, double endY) {
//
//        double rectStartX = Math.min(startX, endX);
//        double rectStartY = Math.min(startY, endY);
//        double rectEndX = Math.max(startX, endX);
//        double rectEndY = Math.max(startY, endY);
//
//        gc.strokeRect(rectStartX, rectStartY, rectEndX - rectStartX, rectEndY - rectStartY);
//
//    }
    private void clearDrawing() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        hasContent = false;
        clearBtn.setDisable(true);
        saveBtn.setDisable(true);
    }

    private void saveDrawing() {
        if (hasContent) {
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(imageView, canvas);
            WritableImage image = stackPane.snapshot(new SnapshotParameters(), null);

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    hasContent = false;
                    clearBtn.setDisable(true);
                    saveBtn.setDisable(true);
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error while saving the image!");
                    alert.showAndWait();
                }
            }
        }
    }

}
