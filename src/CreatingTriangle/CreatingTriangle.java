package CreatingTriangle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

public class CreatingTriangle extends Application {

    Stage stage;
    Label coordinates = new Label("[000:000]");
    private final List<Circle> points = new ArrayList<>();
    private final Polygon triangle = new Polygon();
    private final Label countLabel = new Label("Počet bodů: 0");
    private Color selectedColor = Color.RED;
    Button deleteButton;
    Slider sliderCircleSize = new Slider(0, 30, 6);
    ComboBox<String> cbStrokeType = new ComboBox<>();

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Pane drawingPane = setupDrawingPane();
        GridPane controlPane = setupControlPane(drawingPane);
        BorderPane mainPane = new BorderPane();
        deleteButton.setDisable(true);
        mainPane.setCenter(drawingPane);
        mainPane.setBottom(controlPane);

        Scene scene = new Scene(mainPane, 600, 400);

        triangle.setStroke(Color.rgb(0, 0, 204));
        triangle.setStrokeWidth(3);
        triangle.getStrokeDashArray().addAll(10.0, 10.0);
        triangle.setStrokeLineCap(StrokeLineCap.ROUND);
        triangle.setFill(Color.rgb(0, 102, 255, 0.5));
        drawingPane.getChildren().add(triangle);

        stage.setOnCloseRequest(we -> setCloseRequest(we));

        stage.setTitle("Triangle App");
        stage.setScene(scene);
        stage.show();
    }

    private LinearGradient createGradient() {
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.REFLECT,
                new Stop(0, Color.WHITE),
                new Stop(0.25, Color.YELLOW),
                new Stop(0.5, Color.WHITE),
                new Stop(0.75, Color.YELLOW),
                new Stop(1, Color.WHITE));
    }

    private Pane setupDrawingPane() {
        Pane drawingPane = new Pane();
        drawingPane.setBackground(new Background(new BackgroundFill(createGradient(), null, null)));

        drawingPane.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                addPoint(drawingPane, event.getX(), event.getY());
            } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                removePoint(drawingPane, event.getX(), event.getY());
            }
        });
        drawingPane.setOnMouseMoved(this::updateCoordinates);
        drawingPane.setOnMouseDragged(this::updateCoordinates);
        drawingPane.getChildren().add(coordinates);
        return drawingPane;
    }

    private void addPoint(Pane drawingPane, double x, double y) {
        Circle newPoint = new Circle(x, y, sliderCircleSize.getValue(), selectedColor);
        newPoint.setStroke(Color.BLACK);
        
        String value = cbStrokeType.getValue();
        if (value.equals("solid")) {
            newPoint.getStrokeDashArray().clear();
        } else {
            newPoint.getStrokeDashArray().addAll(10d, 5d, 10d);
        }
        
        points.add(newPoint);
        drawingPane.getChildren().add(newPoint);
        countLabel.setText("Počet bodů: " + points.size());
        updateTriangle(drawingPane);
    }

    private void removePoint(Pane drawingPane, double x, double y) {
        Circle pointToRemove = null;
        for (Circle point : points) {
            if (point.contains(x, y)) {
                pointToRemove = point;
                break;
            }
        }
        if (pointToRemove != null) {
            points.remove(pointToRemove);
            drawingPane.getChildren().remove(pointToRemove);
            countLabel.setText("Počet bodů: " + points.size());
            updateTriangle(drawingPane);
        }
    }

    private GridPane setupControlPane(Pane drawingPane) {
        ColorPicker colorPicker = new ColorPicker(selectedColor);
        colorPicker.setOnAction(event -> {
            selectedColor = colorPicker.getValue();
            points.forEach(circle -> circle.setFill(selectedColor));
        });

        Button endButton = new Button("Konec");
        endButton.setOnAction(event -> setCloseRequest(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));

        deleteButton = new Button("Vymazat");
        deleteButton.setOnAction(event -> {
            points.clear();
            drawingPane.getChildren().clear();
            drawingPane.getChildren().add(coordinates);
            countLabel.setText("Počet bodů: 0");
            updateTriangle(drawingPane);
        });

        countLabel.setFont(new Font("Arial", 12));

        setupSliderCircleSize(sliderCircleSize);
        setupComboBox(cbStrokeType);

//        HBox controlPane = new HBox(10, endButton, deleteButton, new Label("Barva"), colorPicker, countLabel);
        GridPane controlPane = new GridPane();
        controlPane.setAlignment(Pos.CENTER);
        controlPane.setVgap(5);
        controlPane.setHgap(5);
        controlPane.add(endButton, 0, 0);
        controlPane.add(deleteButton, 1, 0);
        controlPane.add(new Label("Barva"), 2, 0);
        controlPane.add(colorPicker, 3, 0);
        controlPane.add(countLabel, 4, 0);
        controlPane.add(new Label("Circle size:"), 0, 1);
        controlPane.add(sliderCircleSize, 1, 1);
        controlPane.add(new Label("Stroke type:"), 2, 1);
        controlPane.add(cbStrokeType, 3, 1);
        controlPane.setPadding(new Insets(10));
        controlPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        return controlPane;
    }

    private void updateTriangle(Pane drawingPane) {
        if (points.isEmpty()) {
            deleteButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
        }
        drawingPane.getChildren().remove(triangle);
        if (points.size() > 3) {
            double centerX = drawingPane.getWidth() / 2;
            double centerY = drawingPane.getHeight() / 2;
            points.sort(Comparator.comparingDouble(point -> -Math.hypot(point.getCenterX() - centerX, point.getCenterY() - centerY)));
            triangle.getPoints().setAll(
                    points.get(0).getCenterX(), points.get(0).getCenterY(),
                    points.get(1).getCenterX(), points.get(1).getCenterY(),
                    points.get(2).getCenterX(), points.get(2).getCenterY());
            drawingPane.getChildren().add(triangle);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setCloseRequest(WindowEvent we) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you wanna close?");
        alert.setTitle("Close?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result != null && result.get().equals(ButtonType.YES)) {
            Platform.exit();
        } else {
            we.consume();
        }
    }

    private void updateCoordinates(MouseEvent t) {

        coordinates.setText(String.format("[%03.0f:%03.0f]", t.getSceneX(), t.getScreenY()));
    }

    private void setupSliderCircleSize(Slider sliderCircleSize) {
        sliderCircleSize.setSnapToTicks(true);
        sliderCircleSize.setShowTickMarks(true);
        sliderCircleSize.setShowTickLabels(true);
        sliderCircleSize.setMajorTickUnit(10);
        sliderCircleSize.setBlockIncrement(2);
        sliderCircleSize.setPrefWidth(100);
    }

    private void setupComboBox(ComboBox<String> cbStrokeType) {
        cbStrokeType.getItems().addAll("solid", "dash");
        cbStrokeType.setValue("solid");
    }

}
