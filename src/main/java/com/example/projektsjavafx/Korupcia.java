package com.example.projektsjavafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vychodzia trieda hry Korupcia, vykresluje hlavne menu s buttonmi na vyber hracieho modu, tvaru hernej plochy,
 * sposob vyhry. Okrem toho umoznuje nacitat ulozene hry.
 */
public class Korupcia extends Application {
    final int minWidth = 800;
    final int minHeight = 600;
    int selectedShape = 0; // 11 - hexagon, 12 - hexagon-hexagon, 13 - hexagon-3triangles
                        // 21 - triangle, 22 - triangle-triangle, 23 - triangle-rectangle
    int selectedGameplay = 0; // 0 - null, 1 - 2-player Mode, 2 - Play Against Easy AI, 3 - Play Against Hard AI
    int selectedWinConditions = 0; // 0 - null, 1 - Domination, 2 - Obliteration
    double multiX = 1;
    double multiY = 1;
    boolean boardIsHexagon = true; // true = hexagon, false = triangle
    VBox gameplayOptions, boardShapeOptions, furtherShapeOptions, winConditions, boardShapes, furtherTriangles, furtherHexagons;
    HBox bottomButtons;
    Label furtherOptionsLabel, errorLabel;
    ImageView hexagonView, triangleView, triangleView2, triangleTriangleView, triangleRectangleView, hexagonView2, hexagonHexagonView, hexagon3TriangleView;
    final int imageWidth = 70;

    /**
     * Vytvara Canvas, vklada don vsetky Images a Buttons. Canvas je resizable iba nazaciatku.
     */
    @Override
    public void start(Stage stage) {
        // Setting up the main layout
        Pane root = new Pane();

        Canvas canvas = new Canvas(minWidth, minHeight);
        root.getChildren().add(canvas);

        ImageView hexagonView, triangleView;
        triangleView2 = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/triangle.png")).toExternalForm()));
        triangleTriangleView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/triangle_triangle.png")).toExternalForm()));
        triangleRectangleView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/triangle_rectangle.png")).toExternalForm()));
        hexagonView2 = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/hexagon.png")).toExternalForm()));
        hexagonHexagonView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/hexagon_with_hexagon.png")).toExternalForm()));
        hexagon3TriangleView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/hex_3triangles.png")).toExternalForm()));
        triangleView2.setFitWidth(imageWidth);
        triangleView2.setFitHeight(imageWidth);
        triangleTriangleView.setFitWidth(imageWidth);
        triangleTriangleView.setFitHeight(imageWidth);
        triangleRectangleView.setFitWidth(imageWidth);
        triangleRectangleView.setFitHeight(imageWidth);
        hexagonView2.setFitWidth(imageWidth);
        hexagonView2.setFitHeight(imageWidth);
        hexagonHexagonView.setFitWidth(imageWidth);
        hexagonHexagonView.setFitHeight(imageWidth);
        hexagon3TriangleView.setFitWidth(imageWidth);
        hexagon3TriangleView.setFitHeight(imageWidth);

        furtherShapeOptions = new VBox(10);
        furtherShapeOptions.setAlignment(Pos.CENTER);
        furtherShapeOptions.setLayoutX(440);
        furtherShapeOptions.setLayoutY(10);
        furtherShapeOptions.setStyle("-fx-background-color: black;");
        furtherTriangles = new VBox(10);
        furtherTriangles.setAlignment(Pos.CENTER);
        furtherTriangles.setStyle("-fx-background-color: black;");
        furtherTriangles.setVisible(true);
        furtherTriangles.getChildren().addAll(triangleView2, triangleTriangleView, triangleRectangleView);
        furtherHexagons = new VBox(10);
        furtherHexagons.setAlignment(Pos.CENTER);
        furtherHexagons.getChildren().addAll(hexagonView2, hexagonHexagonView, hexagon3TriangleView);
        furtherOptionsLabel = new Label("Further Options");
        furtherOptionsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        // Get the GraphicsContext of the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image background = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/corruption/background.png")));
        root.setPadding(new Insets(10));

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
            if (gameplayOptions != null && winConditions != null) {
                gameplayOptions.setLayoutX(10 * multiX);
                gameplayOptions.setLayoutY(10 * multiY);
                boardShapeOptions.setLayoutX(250 * multiX);
                boardShapeOptions.setLayoutY(10 * multiY);
                furtherShapeOptions.setLayoutX(440 * multiX);
                furtherShapeOptions.setLayoutY(10 * multiY);
                winConditions.setLayoutX(canvas.getWidth() - 150 * multiX);
                winConditions.setLayoutY(10 * multiY);
                errorLabel.setLayoutX(290 * multiX);
                errorLabel.setLayoutY(400 * multiY);
                bottomButtons.setLayoutX(300 * multiX);
                bottomButtons.setLayoutY(canvas.getHeight() - 100 * multiY);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Gameplay Options
        gameplayOptions = new VBox(10);
        gameplayOptions.setAlignment(Pos.CENTER);
        gameplayOptions.setLayoutX(10);
        gameplayOptions.setLayoutY(10);
        gameplayOptions.setStyle("-fx-background-color: black;");
        Label gameplayLabel = new Label("Gameplay Options");
        gameplayLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        Button twoPlayerModeButton = new Button("2 - Player Mode");
        Button easyAIButton = new Button("Play Against Easy AI");
        Button hardAIButton = new Button("Play Against Hard AI");

        // Make AI buttons non-clickable and darker
        easyAIButton.setDisable(true);
        easyAIButton.setStyle("-fx-background-color: darkgrey;");
        hardAIButton.setDisable(true);
        hardAIButton.setStyle("-fx-background-color: darkgrey;");

        gameplayOptions.getChildren().addAll(
                gameplayLabel,
                twoPlayerModeButton,
                easyAIButton,
                hardAIButton
        );
        root.getChildren().add(gameplayOptions);

        // Board Size and Shapes
        boardShapeOptions = new VBox(10);
        boardShapeOptions.setAlignment(Pos.CENTER);
        boardShapeOptions.setLayoutX(250);
        boardShapeOptions.setLayoutY(10);
        boardShapeOptions.setStyle("-fx-background-color: black;");
        boardShapeOptions.setVisible(true);
        Label boardShapeLabel = new Label("Board Shape");
        boardShapeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        boardShapes = new VBox(10);
        boardShapes.setAlignment(Pos.CENTER);
        hexagonView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/hexagon.png")).toExternalForm()));
        hexagonView.setFitWidth(imageWidth);
        hexagonView.setFitHeight(imageWidth);
        triangleView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/corruption/triangle.png")).toExternalForm()));
        triangleView.setFitWidth(imageWidth);
        triangleView.setFitHeight(imageWidth);
        boardShapes.getChildren().addAll(hexagonView, triangleView);
        boardShapeOptions.getChildren().addAll(boardShapeLabel, boardShapes);
        root.getChildren().add(boardShapeOptions);

        // Win Conditions
        winConditions = new VBox(10);
        winConditions.setAlignment(Pos.CENTER);
        winConditions.setLayoutX(canvas.getWidth() - 150); // Adjusted positioning
        winConditions.setLayoutY(10);
        winConditions.setStyle("-fx-background-color: black;");
        Label winLabel = new Label("Win Conditions");
        winLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        Button dominationButton = new Button("Domination");
        Button obliterationButton = new Button("Obliteration");

        winConditions.getChildren().addAll(winLabel, dominationButton, obliterationButton);
        root.getChildren().add(winConditions);

        // Error Label
        errorLabel = new Label("You did not select all options!");
        errorLabel.setStyle("-fx-font-size: 18px;-fx-background-color: black; -fx-text-fill: red;");
        errorLabel.setVisible(false);
        errorLabel.setLayoutX(290);
        errorLabel.setLayoutY(400);
        root.getChildren().add(errorLabel);

        // Bottom buttons
        bottomButtons = new HBox(10);
        bottomButtons.setAlignment(Pos.CENTER);
        Button startButton = new Button("START");
        Button loadButton = new Button("Load Game");

        // Make the buttons larger
        startButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px;");
        loadButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px;");

        bottomButtons.getChildren().addAll(startButton, loadButton);
        bottomButtons.setLayoutX(300); // Adjusted positioning
        bottomButtons.setLayoutY(canvas.getHeight() - 50);
        root.getChildren().add(bottomButtons);

        // Setting the scene and stage
        Scene scene = new Scene(root, minWidth, minHeight);
        stage.setTitle("Korupcia");
        stage.setScene(scene);
        stage.show();
        GameBoard gameBoard = new GameBoard();
        // Add listeners to handle window resizing
        stage.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas(canvas, stage));
        stage.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas(canvas, stage));
        stage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (isNowMaximized) {
                // When maximized, remove the maximum width and height constraints
                stage.setMaxWidth(Double.MAX_VALUE);
                stage.setMaxHeight(Double.MAX_VALUE);
                stage.setWidth(1920);
                stage.setHeight(1080);
            } else {
                // When restored, reapply the initial maximum width and height constraints
                stage.setMaxWidth(1920);
                stage.setMaxHeight(1080);
            }
        });

        // Event handling for board shapes
        hexagonView.setOnMouseClicked(event -> {
            boardIsHexagon = true;
            if (selectedShape != 12 && selectedShape != 13)
                selectedShape = 11;
            hexagonView.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
            triangleView.setStyle(""); // Remove highlight from the other
            furtherShapeOptions.getChildren().clear();
            furtherHexagons.setVisible(true);
            furtherShapeOptions.setVisible(true);
            furtherShapeOptions.getChildren().addAll(furtherOptionsLabel, furtherHexagons);
            if (!root.getChildren().contains(furtherShapeOptions)) {
                root.getChildren().add(furtherShapeOptions);
            }
        });

        triangleView.setOnMouseClicked(event -> {
            boardIsHexagon = false;
            if (selectedShape != 22 && selectedShape != 23)
                selectedShape = 21;
            triangleView.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
            hexagonView.setStyle(""); // Remove highlight from the other
            furtherShapeOptions.getChildren().clear();
            furtherTriangles.setVisible(true);
            furtherShapeOptions.setVisible(true);
            furtherShapeOptions.getChildren().addAll(furtherOptionsLabel, furtherTriangles);
            if (!root.getChildren().contains(furtherShapeOptions)) {
                root.getChildren().add(furtherShapeOptions);
            }
        });

        // Event handling for further shape options
        hexagonView2.setOnMouseClicked(event -> {
            selectedShape = 11;
            clearFurtherShapesHighlight();
            hexagonView2.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
        });
        hexagonHexagonView.setOnMouseClicked(event -> {
            selectedShape = 12;
            clearFurtherShapesHighlight();
            hexagonHexagonView.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
        });
        hexagon3TriangleView.setOnMouseClicked(event -> {
            selectedShape = 13;
            clearFurtherShapesHighlight();
            hexagon3TriangleView.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
        });
        triangleView2.setOnMouseClicked(event -> {
            selectedShape = 21;
            clearFurtherShapesHighlight();
            triangleView2.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
        });
        triangleRectangleView.setOnMouseClicked(event -> {
            selectedShape = 22;
            clearFurtherShapesHighlight();
            triangleRectangleView.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
        });
        triangleTriangleView.setOnMouseClicked(event -> {
            selectedShape = 23;
            clearFurtherShapesHighlight();
            triangleTriangleView.setStyle("-fx-effect: dropshadow(gaussian, red, 5, 0.95, 0, 0)");
        });

        // Event handling for win condition buttons
        dominationButton.setOnAction(event -> {
            selectedWinConditions = 1;
            dominationButton.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            obliterationButton.setStyle(""); // Remove highlight from the other
        });

        obliterationButton.setOnAction(event -> {
            selectedWinConditions = 2;
            obliterationButton.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            dominationButton.setStyle(""); // Remove highlight from the other
        });

        // Event handling for gameplay buttons
        twoPlayerModeButton.setOnAction(event -> {
            selectedGameplay = 1;
            twoPlayerModeButton.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            easyAIButton.setStyle("");
            hardAIButton.setStyle("");
        });

        easyAIButton.setOnAction(event -> {
            selectedGameplay = 2;
            easyAIButton.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            twoPlayerModeButton.setStyle("");
            hardAIButton.setStyle("");
        });

        hardAIButton.setOnAction(event -> {
            selectedGameplay = 3;
            hardAIButton.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            twoPlayerModeButton.setStyle("");
            easyAIButton.setStyle("");
        });

        // Event handling for start button
        startButton.setOnAction(event -> {
            if (selectedShape == 0 || selectedGameplay == 0 || selectedWinConditions == 0) {
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                gameBoard.startGame(stage, scene, selectedGameplay, selectedShape, selectedWinConditions);
            }
        });

        // Event handling for load button
        loadButton.setOnAction(event -> {
            File saveDir = new File("saves");
            File[] saveFiles = saveDir.listFiles();
            if (saveFiles != null && saveFiles.length > 0) {
                VBox loadBox = new VBox(10);
                loadBox.setAlignment(Pos.CENTER);
                for (File saveFile : saveFiles) {
                    Button saveButton = new Button(saveFile.getName());
                    saveButton.setOnAction(e -> {
                        loadSavedGame(saveFile, gameBoard, stage, scene);
                    });
                    loadBox.getChildren().add(saveButton);
                }
                Scene loadScene = new Scene(loadBox, 300, 400);
                Stage loadStage = new Stage();
                loadStage.setTitle("Load Game");
                loadStage.setScene(loadScene);
                loadStage.show();
            }
        });
    }

    /**
     * Resetuje zvyraznenie ostatnych Buttonov, ked je kliknuty novy.
     */
    private void clearFurtherShapesHighlight() {
        hexagonView2.setStyle("");
        hexagonHexagonView.setStyle("");
        hexagon3TriangleView.setStyle("");
        triangleView2.setStyle("");
        triangleTriangleView.setStyle("");
        triangleRectangleView.setStyle("");
    }

    /**
     * Toto je iba pre prvy vstup do menu, potom to GameBoard nastavi fixne na 850 na 850
     */
    private void resizeCanvas(Canvas canvas, Stage stage) {
        double width = stage.getWidth();
        double height = stage.getHeight();

        // Limit aspect ratio between 17:9 and 1:1
        if (width / height > 17.0 / 9.0) {
            width = height * 17.0 / 9.0;
        } else if (width / height < 1.0) {
            height = width;
        } else {
            stage.setMinHeight(Math.max(width * 9 / 17, minHeight));
            stage.setMinWidth(Math.max(height, minWidth));
            stage.setMaxHeight(Math.max(width, minHeight));
            stage.setMaxWidth(Math.max(height * 17 / 9, minWidth));
            multiX = canvas.getWidth() / minWidth;
            multiY = canvas.getHeight() / minHeight;
        }

        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    /**
     * Parsuje subory zo zlozky saves, nacitane hexagony posle do Gameboard
     * najprv nacita hlavicku suboru, potom hexagony posiela ako neparsnute stringy
     */
    private void loadSavedGame(File saveFile, GameBoard gameBoard, Stage stage, Scene mainScene) {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String gameMode = reader.readLine().split(": ")[1];
            String shape = reader.readLine().split(": ")[1];
            String winCondition = reader.readLine().split(": ")[1];
            String turn = reader.readLine().split(": ")[1];
            int blueHexagons = Integer.parseInt(reader.readLine().split(": ")[1]);
            int orangeHexagons = Integer.parseInt(reader.readLine().split(": ")[1]);
            double bluePercentage = Double.parseDouble(reader.readLine().split(": ")[1]);
            double orangePercentage = Double.parseDouble(reader.readLine().split(": ")[1]);
            reader.readLine(); // Skip the "Hexagons:" line

            List<String> hexagonData = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                hexagonData.add(line);
            }

            int loadedShape = switch (shape) {
                case "Hexagon" -> 11;
                case "Hexagon-Hexagon" -> 12;
                case "Hexagon-3Triangles" -> 13;
                case "Triangle" -> 21;
                case "Triangle-Triangle" -> 22;
                case "Triangle-Rectangle" -> 23;
                default -> 0;
            };

            int loadedWinConditions = switch (winCondition) {
                case "Domination" -> 1;
                case "Obliteration" -> 2;
                default -> 0;
            };

            int loadedGameplay = switch (gameMode) {
                case "2-Player Mode" -> 1;
                case "Play Against Easy AI" -> 2;
                case "Play Against Hard AI" -> 3;
                default -> 0;
            };

            boolean loadedBlueTurn = turn.equals("Blue");
            gameBoard.loadGame(stage, mainScene, loadedGameplay, loadedShape, loadedWinConditions,
                    loadedBlueTurn, blueHexagons, orangeHexagons, bluePercentage, orangePercentage, hexagonData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
