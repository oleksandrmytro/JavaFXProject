package CreatingCircles;

import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CreatingCircles extends Application {

    Stage stage;

    double startX;
    double startY;
    int amount = 0;
    Label amountLbl;
    Button closeBtn;
    Button resetBtn;
    Circle one;
    ObservableList<Shape> shapes = FXCollections.observableArrayList();
    Pane workspace = setupWorkspace();
    HBox controls = setupControls();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setCenter(workspace);
        root.setBottom(controls);

        
        workspace.widthProperty().addListener((obs, oldVal, newVal) -> {
            createGradient(newVal);
            reset();
        });
        
        workspace.heightProperty().addListener((obs, oldVal, newVal) -> {
            createGradient(newVal);
            reset();
        });

        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.setOnCloseRequest(we -> setupCloseRequest(we));
        stage.show();
    }

    private void createGradient(Number newVal) {
        LinearGradient gradient = new LinearGradient(
                0,
                0,
                newVal.doubleValue(),
                workspace.getHeight(),
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.AQUA),
                new Stop(1, Color.AZURE)
        );
        
        workspace.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private Pane setupWorkspace() {
        workspace = new Pane();

        workspace.setOnMousePressed(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                Circle circle = createCircle(e);
                one = circle;
                circle.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                    if (ev.getButton().equals(MouseButton.SECONDARY)) {
                        removeCircle(circle);
                    }
                });
            }
        });

        workspace.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                double radius = calculateRadius(e);
                one.setRadius(radius);
            }
        });

        workspace.setOnMouseReleased(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {

                shapes.add(one);
                amount++;
                updateAmount();
                checkBounds(one);
            }
        });

        return workspace;
    }

    private HBox setupControls() {
        controls = new HBox();
        controls.setAlignment(Pos.CENTER);
        closeBtn = new Button("Close");
        resetBtn = new Button("Reset");
        amountLbl = new Label("Amount of circles: 0");
        closeBtn.setOnAction(we -> setupCloseRequest(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        resetBtn.setOnAction(e -> reset());
        controls.setSpacing(10);
        controls.setMinHeight(70);
        controls.setStyle("-fx-border-color: #9c9835; -fx-border-width: 5; -fx-border-radius: 15;"
                + " -fx-background-color: #9cb835; -fx-background-radius: 15");
        controls.getChildren().addAll(closeBtn, resetBtn, amountLbl);
        return controls;
    }

    private void setupCloseRequest(WindowEvent we) {
        Alert alert = new Alert(AlertType.CONFIRMATION,
                "Are you sure you wanna close?");
        alert.setTitle("Close?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.YES)) {
            Platform.exit();
        } else {
            we.consume();
        }
    }

    private void reset() {
        shapes.clear();
        workspace.getChildren().clear();
        amountLbl.setText("Amount of circles: 0");
        amount = 0;
    }

    private Circle createCircle(MouseEvent e) {
        Circle newCircle = new Circle();
        newCircle.setStroke(Color.BLACK);
        newCircle.setFill(Color.GREEN);
        newCircle.setStrokeWidth(2);
        newCircle.getStrokeDashArray().add(20d);
        startX = e.getX();
        startY = e.getY();
        newCircle.setCenterX(startX);
        newCircle.setCenterY(startY);
        newCircle.setRadius(0);
        workspace.getChildren().add(newCircle);
        return newCircle;
    }

    private void removeCircle(Circle circle) {
        workspace.getChildren().remove(circle);
        amount--;
        shapes.remove(circle);
        updateAmount();
        shapes.forEach(this::checkBounds);
    }

    private void updateAmount() {
        amountLbl.setText("Amount of circles: " + String.valueOf(amount));
    }

    private void checkBounds(Shape block) {
        boolean collisionDetected = false;
        for (Shape static_block : shapes) {
            if (static_block != block) {
                if (block.getBoundsInParent().intersects(static_block.getBoundsInParent())) {
                    collisionDetected = true;
                    static_block.setFill(new Color(0, 0, 1, 0.5));
                }
            }
        }
        if (collisionDetected) {
            block.setFill(new Color(0, 0, 1, 0.5));
        } else {
            block.setFill(Color.GREEN);
        }
    }

    private double calculateRadius(MouseEvent e) {
        return Math.sqrt(Math.pow(e.getX() - startX, 2) + Math.pow(e.getY() - startY, 2));
    }

}
