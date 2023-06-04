package Pictures1;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Stack;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;

public class Pictures extends Application {

    Stage stage;

    File file;
    private Label coordinates = new Label("Coordinates: ");
    Stack<Image> images = new Stack<>();
    private Button closeBtn = setCloseBtn();
    private Button clearBtn = setClearBtn();
    private Button previousPictureBtn = setPreviousPicture();
    private Button loadBtn = setLoadBtn();
    private LineChart<Number, Number> lineChart = setLineChart();
    private MenuBar menuBar;
    private final Label colorRGBLbl = new Label("RGB color: ");
    private Rectangle colorDisplay = setRectangle();
    private Menu menu = setMenu();
    private Pane workspace = createWorkspace();
    private VBox leftControl = setLeftControl();
    private HBox controlsBottom = setControlsBottom();

    private ImageView imageView;
    private double startX;
    private double startY;
    private Rectangle rectangle;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        BorderPane root = new BorderPane();

        root.setCenter(workspace);
        root.setBottom(controlsBottom);
        root.setRight(lineChart);
        root.setLeft(leftControl);
        root.setTop(menuBar);

        windowSizeChange(root);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add(0, "stylePictures");

        stage.setOnCloseRequest(we -> {
            setCloseRequest(we);
        });
        stage.setScene(scene);
        stage.show();
    }

    private void windowSizeChange(BorderPane root) {
        root.heightProperty().addListener(obs -> {
            clear();
        });

        root.widthProperty().addListener(obs -> {
            clear();
        });
    }

    private Pane createWorkspace() {
        workspace = new Pane();
        imageView = new ImageView();
        workspace.setMinSize(300, 300);

        workspace.setStyle("-fx-border-color: green; -fx-border-width: 2; -fx-border-radius: 6; -fx-padding: 10");
        workspace.getChildren().add(imageView);

        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handlerImageViewMouseClicked);
        imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handlerImageViewMousePressed);
        imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handlerImageViewMouseDragged);
        imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handlerImageViewMouseReleased);
        return workspace;
    }

    private LineChart<Number, Number> setLineChart() {
        NumberAxis xAxis = new NumberAxis(0, 255, 64);
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setUpperBound(100);

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setMaxWidth(300);
        lineChart.setMinWidth(300);
        lineChart.setCreateSymbols(false);
        lineChart.setBorder(new Border(new BorderStroke(Color.rgb(0, 0, 255), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        return lineChart;
    }

    private Rectangle setRectangle() {
        colorDisplay = new Rectangle(150, 150, Color.GREY);
        return colorDisplay;
    }

    private VBox setLeftControl() {
        leftControl = new VBox();
        leftControl.getChildren().addAll(colorDisplay, colorRGBLbl);
        leftControl.setAlignment(Pos.CENTER);
        leftControl.setSpacing(10);

        leftControl.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-padding: 10; -fx-border-radius: 5");

        return leftControl;
    }

    private Button setClearBtn() {
        clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clear());
        return clearBtn;
    }

    private Menu setMenu() {
        menu = new Menu("Menu");
        MenuItem load = new MenuItem("Load");
        MenuItem reset = new MenuItem("Reset");
        menu.getItems().addAll(load, reset);
        menuBar = new MenuBar(menu);
        load.setOnAction(e -> loadPicture());
        reset.setOnAction(e -> clear());
        return menu;
    }

    private void loadPicture() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );

        file = fc.showOpenDialog(stage);
        if (file != null) {
            try {
                Image image = new Image(new FileInputStream(file));
                images.push(image);
                imageView.setImage(image);
                fitImageToWorkspace();
            } catch (FileNotFoundException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("An error occurred while loading the file \n Plese select the correct file");
            }
        }
    }

    private void clear() {
        if (imageView.getImage() != null) {
            images.push(imageView.getImage());
            imageView.setImage(null);
            colorDisplay.setFill(Color.GREY);
            colorRGBLbl.setText("RGB color: ");
            lineChart.getData().clear();
            workspace.getChildren().remove(rectangle);
            coordinates.setText("Coordinates: ");
        }
    }

    private void fitImageToWorkspace() {
        double workspaceWidth = workspace.getWidth();
        double workspaceHeight = workspace.getHeight();
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();

        double scaleFactor = Math.min(workspaceWidth / imageWidth, workspaceHeight / imageHeight);

        imageView.setFitWidth(imageWidth * scaleFactor);
        imageView.setFitHeight(imageHeight * scaleFactor);

        // Update the image view position after scaling
        double newImageWidth = imageWidth * scaleFactor;
        double newImageHeight = imageHeight * scaleFactor;
        imageView.setX((workspaceWidth - newImageWidth) / 2);
        imageView.setY((workspaceHeight - newImageHeight) / 2);
    }

    private void setCloseRequest(WindowEvent we) {
        Alert closeAlert = new Alert(AlertType.CONFIRMATION);
        closeAlert.setTitle("Close??");
        closeAlert.setContentText("Are you sure you wanna close?");
        Optional<ButtonType> result = closeAlert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            Platform.exit();
        } else {
            we.consume();
        }
    }

    private Button setCloseBtn() {
        closeBtn = new Button("Close");
        closeBtn.setOnAction(w -> {
            setCloseRequest(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        closeBtn.setPrefWidth(50);
        return closeBtn;
    }

    private HBox setControlsBottom() {
        controlsBottom = new HBox();
        controlsBottom.setMinHeight(60);
        controlsBottom.setSpacing(15);
        controlsBottom.getChildren().addAll(closeBtn, loadBtn,  clearBtn, previousPictureBtn, coordinates);
        controlsBottom.setAlignment(Pos.CENTER);
        return controlsBottom;
    }

    private void handlerImageViewMouseClicked(MouseEvent t) {
        if (t.getButton().equals(MouseButton.PRIMARY)) {
            coordinates.setText(String.format("Coordinates: [%4.0f, %4.0f]", t.getX(), t.getY()));
            Image image = imageView.getImage();
            if (image != null) {
                double imageWidth = image.getWidth();
                double imageHeight = image.getHeight();
                double imageViewHeight = imageView.getBoundsInLocal().getHeight();
                double imageViewWidth = imageView.getBoundsInLocal().getWidth();

                double scaleX = imageWidth / imageViewWidth;
                double scaleY = imageHeight / imageViewHeight;

                int x = (int) ((t.getX() - imageView.getX()) * scaleX);
                int y = (int) ((t.getY() - imageView.getY()) * scaleY);

                if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                    Color pixelColor = image.getPixelReader().getColor(x, y);
                    colorDisplay.setFill(pixelColor);
                    colorRGBLbl.setText(String.format("RGB color: %d, %d, %d",
                            (int) (pixelColor.getRed() * 255),
                            (int) (pixelColor.getGreen() * 255),
                            (int) (pixelColor.getBlue() * 255)
                    ));
                }
            }
        }
    }

    private void handlerImageViewMousePressed(MouseEvent e) {
        if (e.getButton().equals(MouseButton.SECONDARY)) {
            startX = e.getX();
            startY = e.getY();
            if (rectangle != null) {
                workspace.getChildren().remove(rectangle);
                rectangle = null;
            }
            rectangle = new Rectangle(startX, startY, 0, 0);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.RED);
            rectangle.setStrokeWidth(2);
            workspace.getChildren().add(rectangle);
        }
    }

    private void handlerImageViewMouseDragged(MouseEvent e) {
        if (e.getButton().equals(MouseButton.SECONDARY)) {
            if (rectangle != null) {
                double newWidth = Math.abs(e.getX() - startX);
                double newHeight = Math.abs(e.getY() - startY);
                double newX = Math.min(startX, e.getX());
                double newY = Math.min(startY, e.getY());

                // Обмежуємо розміри прямокутника в межах картинки.
                double rightEdge = Math.min(newX + newWidth, imageView.getX() + imageView.getFitWidth());
                double bottomEdge = Math.min(newY + newHeight, imageView.getY() + imageView.getFitHeight());
                double leftEdge = Math.max(newX, imageView.getX());
                double topEdge = Math.max(newY, imageView.getY());

                rectangle.setX(leftEdge);
                rectangle.setY(topEdge);
                rectangle.setWidth(rightEdge - leftEdge);
                rectangle.setHeight(bottomEdge - topEdge);
            }
        }
    }

    private void handlerImageViewMouseReleased(MouseEvent e) {
        if (e.getButton().equals(MouseButton.SECONDARY) && rectangle != null) {

            // Clear old chart data
            lineChart.getData().clear();

            // Prepare chart series for Red, Green, Blue
            XYChart.Series<Number, Number> redSeries = new XYChart.Series<>();
            redSeries.setName("Red");
            XYChart.Series<Number, Number> greenSeries = new XYChart.Series<>();
            greenSeries.setName("Green");
            XYChart.Series<Number, Number> blueSeries = new XYChart.Series<>();
            blueSeries.setName("Blue");

            // Calculate pixel coordinates considering the scale factor
            Image image = imageView.getImage();
            double scaleX = image.getWidth() / imageView.getFitWidth();
            double scaleY = image.getHeight() / imageView.getFitHeight();
            int x1 = (int) ((rectangle.getX() - imageView.getX()) * scaleX);
            int y1 = (int) ((rectangle.getY() - imageView.getY()) * scaleY);
            int x2 = (int) ((rectangle.getX() + rectangle.getWidth() - imageView.getX()) * scaleX);
            int y2 = (int) ((rectangle.getY() + rectangle.getHeight() - imageView.getY()) * scaleY);

            // Create frequency map for each color channel
            int[] redFreq = new int[256];
            int[] greenFreq = new int[256];
            int[] blueFreq = new int[256];

            // These variables will keep track of the maximum frequencies among the red, green, and blue channels
            int maxRedFreq = 0;
            int maxGreenFreq = 0;
            int maxBlueFreq = 0;

            // Iterate over each pixel in the rectangle
            for (int y = y1; y < y2; y++) {
                for (int x = x1; x < x2; x++) {
                    Color color = image.getPixelReader().getColor(x, y);
                    int red = (int) (color.getRed() * 255);
                    int green = (int) (color.getGreen() * 255);
                    int blue = (int) (color.getBlue() * 255);
                    redFreq[red]++;
                    greenFreq[green]++;
                    blueFreq[blue]++;
                    maxRedFreq = Math.max(maxRedFreq, redFreq[red]);
                    maxGreenFreq = Math.max(maxGreenFreq, greenFreq[green]);
                    maxBlueFreq = Math.max(maxBlueFreq, blueFreq[blue]);
                }
            }

            // Find the maximum value among the red, green, and blue channels
            int maxFreq = Math.max(maxRedFreq, Math.max(maxGreenFreq, maxBlueFreq));

            // Add frequency data to the chart, scaling the frequencies so that the maximum is 100
            for (int i = 0; i < 256; i++) {
                redSeries.getData().add(new XYChart.Data<>(i, (redFreq[i] / (double) maxFreq) * 100));
                greenSeries.getData().add(new XYChart.Data<>(i, (greenFreq[i] / (double) maxFreq) * 100));
                blueSeries.getData().add(new XYChart.Data<>(i, (blueFreq[i] / (double) maxFreq) * 100));
            }

            // Add the series to the chart
            lineChart.getData().add(redSeries);
            lineChart.getData().add(greenSeries);
            lineChart.getData().add(blueSeries);

        }
    }

    private Button setPreviousPicture() {
        previousPictureBtn = new Button("Previous Picture");
        previousPictureBtn.setOnAction(e -> loadPrevious());
        return previousPictureBtn;
    }

    private void loadPrevious() {
        if (!images.empty()) {
            imageView.setImage(images.pop());
            fitImageToWorkspace();
        } else {
            errorPreviousPicture();
        }
    }

    private void errorPreviousPicture() {
        Alert emptyStack = new Alert(AlertType.ERROR);
        emptyStack.setTitle("Error");
        emptyStack.setContentText("There is not previous picture");
        emptyStack.showAndWait();
    }

    private Button setLoadBtn() {
        loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> {
            loadPicture();
        });
        return loadBtn;
    }
}
