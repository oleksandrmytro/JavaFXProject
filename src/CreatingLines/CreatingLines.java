package CreatingLines;

import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CreatingLines extends Application {

    Stage stage;

    private final double RADIUS_CIRCLE = 5;

    ObservableList<Circle> list = FXCollections.observableArrayList();
    ObservableList<CubicCurve> curves = FXCollections.observableArrayList();

    Pane pane = new Pane();
    Color barvaKrivky = Color.BLUE;
    int pocetBodu = 0;
    Label pocet = new Label();
    Button btnVymazat;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        VBox root = new VBox();
        HBox controlPanel = new HBox();

        VBox.setVgrow(pane, Priority.ALWAYS);

        addControl(controlPanel);

        pane.widthProperty().addListener(o -> {
            clear();
        });
        pane.heightProperty().addListener(o -> {
            clear();
        });

        root.getChildren().addAll(pane, controlPanel);

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, (t) -> {
            if (t.getButton() == MouseButton.PRIMARY) {
                Point2D point = new Point2D(t.getX(), t.getY());
                Circle circle = getCircle(point);
                draw(circle);
                list.add(circle);
                drawCurves();
            }

        });

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Cubic Curve");
        stage.setScene(scene);
        stage.setOnCloseRequest(we -> setupCloseRequest(we));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }

    private void addControl(HBox pane) {

        Label lblPocet = new Label("Pocet bodu:");
        Label lblBarva = new Label("Barva:");
        btnVymazat = new Button("Vymazat");
        Button btnKonec = new Button("Konce");
        
        btnVymazat.setDisable(true);
        

        ColorPicker colorPicker = new ColorPicker(Color.BLUE);
        pane.setStyle("-fx-background-color: #9cb893; -fx-border-color: #9cb8b6;"
                + " -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 10");
        pane.getChildren().addAll(btnKonec, btnVymazat, lblBarva, colorPicker, lblPocet, pocet);

        HBox.setMargin(btnKonec, new Insets(15, 10, 10, 0));
        HBox.setMargin(btnVymazat, new Insets(15, 10, 10, 0));
        HBox.setMargin(lblBarva, new Insets(20, 10, 15, 0));
        HBox.setMargin(lblPocet, new Insets(20, 10, 15, 0));
        HBox.setMargin(colorPicker, new Insets(15, 10, 10, 0));
        HBox.setMargin(pocet, new Insets(15, 10, 10, 0));

        pane.setAlignment(Pos.CENTER);
        toDefault();

        btnKonec.setOnAction(we -> setupCloseRequest(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        btnVymazat.setOnAction(o -> {
            clear();
        });
        colorPicker.setOnAction(o -> {
            Color color = colorPicker.getValue();
            if (color != null) {
                barvaKrivky = color;
                redraw();
            }
        });

    }

    private void toDefault() {
        pane.setStyle("-fx-background-color: #f98e50");
        pane.getChildren().clear();
        list.clear();
        curves.clear();

    }

    private void redraw() {
        pane.getChildren().clear();

        for (Circle circle : list) {
            draw(circle);
            drawCurves();
        }
        pocet.setText(String.valueOf(pocetBodu));
    }

    private Circle getCircle(Point2D point) {
        Circle circle = new Circle(point.getX(), point.getY(), RADIUS_CIRCLE);
        pocetBodu++;

        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);
        if (pocetBodu == 1 || (pocetBodu - 1) % 3 == 0) {
            circle.setFill(Color.RED);
        } else {
            circle.setFill(Color.TRANSPARENT);
        }
        circle.addEventFilter(MouseEvent.MOUSE_DRAGGED, (t) -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                circle.setCenterX(t.getX());
                circle.setCenterY(t.getY());
            }
        });
        circle.addEventFilter(MouseEvent.MOUSE_RELEASED, (t) -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                redraw();
            }
        });
        circle.addEventFilter(MouseEvent.MOUSE_PRESSED, (t) -> {
            if (t.getButton() == MouseButton.MIDDLE) {
                pocetBodu--;
                list.remove(circle);
                pane.getChildren().remove(circle);
                redraw();
                if (pocetBodu == 0) {
                    btnVymazat.setDisable(true);
                }
            }
        });
        return circle;
    }

    private boolean draw(Circle circle) {
        if (circle != null) {
            if (circle.getCenterX() <= pane.getWidth() || circle.getCenterY() <= pane.getHeight()) {
                pane.getChildren().add(circle);
                btnVymazat.setDisable(false);
                pocet.setText(String.valueOf(pocetBodu));
                return true;
            }
        }
        return false;
    }

    private void clear() {
        toDefault();
        pocetBodu = 0;
        pocet.setText(String.valueOf(pocetBodu));
        btnVymazat.setDisable(true);
    }

    private void drawCurves() {
        if (pocetBodu >= 4 && (pocetBodu - 1) % 3 == 0) {
            CubicCurve curve = new CubicCurve();
            curves.add(curve);
            curve.setStartX(list.get(pocetBodu - 4).getCenterX());
            curve.setStartY(list.get(pocetBodu - 4).getCenterY());
            curve.setControlX1(list.get(pocetBodu - 3).getCenterX());
            curve.setControlY1(list.get(pocetBodu - 3).getCenterY());
            curve.setControlX2(list.get(pocetBodu - 2).getCenterX());
            curve.setControlY2(list.get(pocetBodu - 2).getCenterY());
            curve.setEndX(list.get(pocetBodu - 1).getCenterX());
            curve.setEndY(list.get(pocetBodu - 1).getCenterY());
            curve.setStroke(barvaKrivky);
            curve.setFill(null);
            pane.getChildren().add(curve);
        }
    }

    private void setupCloseRequest(WindowEvent we) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you wanna close?");
        alert.setTitle("Close?");
        alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.YES)) {
            Platform.exit();
        } else {
            we.consume();
        }
    }

}
