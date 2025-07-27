package com.example.projektsjavafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * GameBoard menezuje novy Pane s hracou plochou, kontroluje vyhru, sirenie korupcie, menenie tahov, generuje
 * plochu z malych hexagovnov. Ma dve podtriedy:
 * private static class Pair
 * private class Hexagon
 */
public class GameBoard {
    private final int minWidth = 800;
    private final int minHeight = 800;
    private final int hexagonWidth = 55; // assuming each small hexagon is 55x55 pixels
    private final int hexagonHeight = 55;
    private final int solidHexagonWidth = 48; // adjusting solid small hexagon image size to 55x55 pixels
    private final int solidHexagonHeight = 43;
    private final double xOffset = hexagonWidth * 0.65; // for small hexagons
    private final double yOffset = hexagonHeight * 0.38;
    private int boardShape, winConditions, gamePlay;
    private boolean gameEnded = false;
    private final List<List<Hexagon>> hexagons = new ArrayList<>();
    private final List<List<Hexagon>> loadedHexagons = new ArrayList<>();
    private final Image background = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/corruption/background_stars.png")));
    private final Image solidHexagonImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/corruption/solid.png")));
    private final Image idleHexagonImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/corruption/idle.png")));
    private final Image blueHexagonImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/corruption/blue.png")));
    private final Image orangeHexagonImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/corruption/orange.png")));
    private final Set<Pair> hexagonHexagonSolids = Set.of(new Pair(10, 8),
            new Pair(11, 9), new Pair(12, 10), new Pair(13, 11), new Pair(15, 11), new Pair(17, 11), new Pair(19, 11),
            new Pair(20, 10), new Pair(21, 9), new Pair(22, 8), new Pair(21, 7), new Pair(20, 6), new Pair(19, 5),
            new Pair(17, 5), new Pair(15, 5), new Pair(13, 5), new Pair(12, 6), new Pair(11, 7), new Pair(12, 8),
            new Pair(13, 9), new Pair(14, 10), new Pair(16, 10), new Pair(18, 10), new Pair(19, 9), new Pair(20, 8),
            new Pair(19, 7), new Pair(18, 6), new Pair(16, 6), new Pair(14, 6), new Pair(13, 7), new Pair(14, 8),
            new Pair(15, 9), new Pair(17, 9), new Pair(18, 8), new Pair(17, 7), new Pair(15, 7), new Pair(16, 8)
    );
    private final Set<Pair> hexagon3TrianglesSolids = Set.of(
            new Pair(7, 1), new Pair(8, 2), new Pair(9, 3), new Pair(10, 4), new Pair(11, 5), new Pair(12, 6),
            new Pair(13, 7), new Pair(11, 7), new Pair(9, 7), new Pair(7, 7), new Pair(5, 7), new Pair(3, 7),
            new Pair(1, 7), new Pair(2, 6), new Pair(3, 5), new Pair(4, 4), new Pair(5, 3), new Pair(6, 2),
            new Pair(7, 3), new Pair(8, 4), new Pair(9, 5), new Pair(10, 6), new Pair(8, 6), new Pair(6, 6),
            new Pair(4, 6), new Pair(5, 5), new Pair(6, 4), new Pair(7, 5), new Pair(19, 7), new Pair(21, 7),
            new Pair(23, 7), new Pair(25, 7), new Pair(27, 7), new Pair(29, 7), new Pair(31, 7), new Pair(30, 6),
            new Pair(28, 6), new Pair(26, 6), new Pair(24, 6), new Pair(22, 6), new Pair(20, 6), new Pair(21, 5),
            new Pair(23, 5), new Pair(25, 5), new Pair(27, 5), new Pair(29, 5), new Pair(28, 4), new Pair(26, 4),
            new Pair(24, 4), new Pair(22, 4), new Pair(23, 3), new Pair(25, 3), new Pair(27, 3), new Pair(26, 2),
            new Pair(24, 2), new Pair(25, 1), new Pair(22, 16), new Pair(21, 15), new Pair(20, 14), new Pair(19, 13),
            new Pair(18, 12), new Pair(17, 11), new Pair(16, 10), new Pair(15, 11), new Pair(14, 12), new Pair(13, 13),
            new Pair(12, 14), new Pair(11, 15), new Pair(10, 16), new Pair(12, 16), new Pair(13, 15), new Pair(14, 14),
            new Pair(15, 13), new Pair(16, 12), new Pair(17, 13), new Pair(18, 14), new Pair(19, 15), new Pair(20, 16),
            new Pair(18, 16), new Pair(17, 15), new Pair(16, 14), new Pair(15, 15), new Pair(16, 16), new Pair(14, 16)
    );
    private final Set<Pair> triangleMiddleTriangle = Set.of(
            new Pair(16, 6), new Pair(17, 7), new Pair(18, 8), new Pair(19, 9), new Pair(20, 10), new Pair(21, 11),
            new Pair(22, 12), new Pair(23, 13), new Pair(21, 13), new Pair(20, 12), new Pair(19, 11), new Pair(18, 10),
            new Pair(17, 9), new Pair(16, 8), new Pair(15, 7), new Pair(14, 8), new Pair(15, 9), new Pair(16, 10),
            new Pair(17, 11), new Pair(18, 12), new Pair(19, 13), new Pair(17, 13), new Pair(16, 12), new Pair(15, 11),
            new Pair(14, 10), new Pair(13, 9), new Pair(12, 10), new Pair(13, 11), new Pair(14, 12), new Pair(15, 13),
            new Pair(13, 13), new Pair(12, 12), new Pair(11, 11), new Pair(10, 12), new Pair(11, 13), new Pair(9, 13));
    private final Random random = new Random();
    private int totalHexagons = 217;
    private final SimpleDoubleProperty bluePercentage = new SimpleDoubleProperty(0.459); // 1/217 * 100
    private final SimpleDoubleProperty orangePercentage = new SimpleDoubleProperty(0.459); // 1/217 * 100
    private int blueHexagons = 1; // Initial hexagon count for blue
    private int orangeHexagons = 1; // Initial hexagon count for orange
    private boolean blueTurn = true;
    private final Set<Hexagon> visited = new HashSet<>();
    private final int[][] directions = {
            {2, 0, 180}, {1, -1, 240}, {-1, -1, 300}, {-2, 0, 0}, {-1, 1, 60}, {1, 1, 120}
    };
    private Label turnLabel;
    private double multiX = 1.0;
    private double multiY = 1.0;

    /**
     * Sluzi iba na ulozenie suradnic solidnych hexagonov (prekazok) pre zlozitejsie rozlozenia prekazok
     */
    private static class Pair{
        int first, second;
        Pair(int f, int s){
            first = f;
            second = s;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair intPair = (Pair) o;
            return first == intPair.first && second == intPair.second;
        }
        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }

    /**
     * Enum pre farby hexagonov:
     *      INVISIBLE - pomocne hexagony s 0-vym rozmerom sluziace na normalne pouzivanie hexagonoveho suradnicoveho systemu
     *      SOLID - nezkorumpovana nezkorumpovatelna plocha (prekazky)
     *      IDLE - nezkorumpovana zkorumpovatelna plocha
     *      BLUE - plocha zkorumpovana modrym
     *      ORANGE - plocha zkorumpovana oranzovym
     */
    enum HexagonColor {
        INVISIBLE, SOLID, IDLE, BLUE, ORANGE
    }

    /**
     * Hexagon - na objektifikaciu dielikov plochy, aby kazdy mohol mat svoj
     *      ImageView imageView - obrazok
     *      int rotation - otocenie, vzdy nasobok 60 % 360
     *      int row, col - hexagony su ukladane vzdy v obdlzniku
     *      HexagonColor color - jeho farba
     */
    private class Hexagon {
        ImageView imageView;
        int rotation, row, col;
        HexagonColor color;

        Hexagon(ImageView imageView, int rotation, int row, int col, HexagonColor color) {
            this.imageView = imageView;
            this.rotation = rotation;
            this.row = row;
            this.col = col;
            this.color = color;
        }

        void setRotation(int rotation) {
            this.rotation = rotation;
            this.imageView.setRotate(rotation);
        }

        void setColor(HexagonColor color) {
            this.color = color;
            switch (color) {
                case BLUE -> this.imageView.setImage(blueHexagonImage);
                case ORANGE -> this.imageView.setImage(orangeHexagonImage);
                case IDLE -> this.imageView.setImage(idleHexagonImage);
                default -> this.imageView.setVisible(false);
            }
        }
        @Override
        public String toString() {
            return "new Pair(" + row + ", " + col+"),";
        }
    }

    /**
     * Zacne hru so specifikovanymi parametrami
     * @param stage Hlavna stage aplikacie
     * @param mainScene Hlavna scena - hlavne menu, aby sme sa donho mohli vratit z GameBoardu
     * @param selectedGameplay Vybraty rezim hrania
     * @param selectedShape Vybraty tvar plochy
     * @param selectedWinConditions Vybrane podmienky vyhry.
     */
    public void startGame(Stage stage, Scene mainScene, int selectedGameplay, int selectedShape, int selectedWinConditions) {
        // Create the root pane and canvas for the game board
        Pane root = new Pane();
        Canvas canvas = new Canvas(minWidth, minHeight); // Initial dimensions
        root.getChildren().add(canvas);
        stage.setMinWidth(800);
        stage.setMinHeight(800);
        stage.setMaxWidth(800);
        stage.setMaxHeight(800);
        boardShape = selectedShape;
        winConditions = selectedWinConditions;
        gamePlay = selectedGameplay;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        // Generate hexagonal board
        if(loadedHexagons.isEmpty()) {
            switch (boardShape / 10) {
                case 1 -> generateHexagonalBoard(root);
                case 2 -> generateTriangleBoard(root);
                default -> generateHexagonalBoard(root); // basic hexagon no obstacles (no solid hexagons)
            }
            bluePercentage.set(1.0 / totalHexagons * 100);
            orangePercentage.set(1.0 / totalHexagons * 100);
        }else{ // game is being loaded
            generateBoardFromLoaded(root);
        }
        switch (boardShape){
            case 11 -> totalHexagons = 217;
            case 12 -> totalHexagons = 180;
            case 13 -> totalHexagons = 133;
            case 21 -> totalHexagons = 153;
            case 22 -> totalHexagons = 101;
            case 23 -> totalHexagons = 117;
            default -> totalHexagons = 217;
        }

        // Display selected options
        Label selectedOptionsLabel = new Label("Game Mode: " + getGameMode(selectedGameplay) + " | Shape: " + getShape() + " | Win Conditions: " + getWinConditions(selectedWinConditions));
        selectedOptionsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-background-color: black;");
        selectedOptionsLabel.setLayoutX(10);
        selectedOptionsLabel.setLayoutY(10);
        root.getChildren().add(selectedOptionsLabel);

        // Turn label
        turnLabel = new Label((blueTurn)?"Turn: Blue" : "Turn: Orange");
        turnLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: white; -fx-background-color: black;");
        turnLabel.setLayoutX(10);
        turnLabel.setLayoutY(60);
        root.getChildren().add(turnLabel);

        // Back to Main Menu button
        Button backToMainMenuButton = new Button("Back To Main Menu");
        backToMainMenuButton.setLayoutX(10);
        backToMainMenuButton.setLayoutY(100);
        root.getChildren().add(backToMainMenuButton);

        // "Restart" button
        Button restartButton = new Button("Restart");
        restartButton.setLayoutX(10);
        restartButton.setLayoutY(140);
        root.getChildren().add(restartButton);

        // "Save" button
        Button saveButton = new Button("Save");
        saveButton.setLayoutX(10);
        saveButton.setLayoutY(180);
        root.getChildren().add(saveButton);

        saveButton.setOnAction(saveEvent -> {
            saveGame();
        });

        restartButton.setOnAction(restartEvent -> {
            resetBoard(root); // Call resetBoard to restart the game
            startGame(stage, mainScene, selectedGameplay, selectedShape, selectedWinConditions);
        });

        backToMainMenuButton.setOnAction(backEvent -> {
            resetBoard(root); // Reset the gameboard when going back to the main menu
            stage.setScene(mainScene);
        });

        // Percentage counters
        VBox counters = new VBox(10);
        if(boardShape / 10 == 1 || boardShape == 0) // hexagon shaped board
            counters.setLayoutX(630);
        else if(boardShape / 10 == 2) // triangle shaped board
            counters.setLayoutX(200);
        counters.setLayoutY(40);
        Label blueCounter = new Label();
        blueCounter.textProperty().bind(bluePercentage.asString("Blue: %.1f%%"));
        blueCounter.setStyle("-fx-font-size: 25px; -fx-text-fill: rgb(0,119,255);-fx-background-color: black;");
        Label orangeCounter = new Label();
        orangeCounter.textProperty().bind(orangePercentage.asString("Orange: %.1f%%"));
        orangeCounter.setStyle("-fx-font-size: 25px; -fx-text-fill: orange;-fx-background-color: black;");
        counters.getChildren().addAll(blueCounter, orangeCounter);
        root.getChildren().add(counters);

        // Setting the scene
        Scene gameScene = new Scene(root, minWidth, minHeight);
        stage.setScene(gameScene);

        // Add listeners to handle window resizing
//        stage.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas(canvas, stage));
//        stage.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas(canvas, stage));
//        stage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
//            if (isNowMaximized) {
//                stage.setMaxWidth(Double.MAX_VALUE);
//                stage.setMaxHeight(Double.MAX_VALUE);
//            } else {
//                stage.setMaxWidth(1920);
//                stage.setMaxHeight(1080);
//            }
//        });
    }

    /**
     * Pouzije sa pri nacitavani zo suboru
     * Ak ma clenskej premennej loadedHexagons predpripravene hexagony na nacitanie, pomocou metod emplace...
     * ich popresuva do hlavnej plochy
     */
    private void generateBoardFromLoaded(Pane root){
        double centerX = 230; // Center X of the canvas
        double centerY = 40; // Center Y of the canvas
        for(int row=0; row<loadedHexagons.size(); row++){
            hexagons.add(new ArrayList<>());
            for(int col=0; col<loadedHexagons.get(row).size(); col++){
                double x = centerX + (col - 4) * xOffset;
                double y = centerY + row * yOffset;
                HexagonColor color = loadedHexagons.get(row).get(col).color;
                int rotation = loadedHexagons.get(row).get(col).rotation;
                switch (color){
                    case BLUE -> emplaceColoredHexagon(root, row+1, col, x, y, HexagonColor.BLUE, rotation);
                    case ORANGE -> emplaceColoredHexagon(root, row+1, col, x, y, HexagonColor.ORANGE, rotation);
                    case IDLE -> emplaceColoredHexagon(root, row+1, col, x, y, HexagonColor.IDLE, rotation);
                    case SOLID -> emplaceSolidHexagon(root, row+1, col, x, y);
                    default -> emplaceInvisibleHexagon(root, row+1, col, x, y);
                }
            }
        }
        loadedHexagons.clear();
    }

    /**
     * Vola sa z Korupcia::saveLoadedGame , dostava od nej parsnutu hlavicku - zakladne parametre hry:
     * @param stage hlavna stage aplikacie
     * @param mainScene spolu so stage sa posielaju dalej metode startGame, ktoru vzdy vola
     * @param loadedGameplay
     * @param loadedShape
     * @param loadedWinConditions
     * @param loadedBlueTurn
     * @param loadedBlueHexagons
     * @param loadedOrangeHexagons
     * @param loadedBluePercentage
     * @param loadedOrangePercentage
     * @param hexagonData list neparsnutych hexagonov
     *
     * Dany list hexagonData zparsuje a jeho obsah ulozi do clenskej premennej loadedHexagons
     * Potom nacita hru odznovu, zavola startGame, ktora v tomto pripade zavola generovanie plochy z loadnutych hexagonov
     */
    public void loadGame(Stage stage, Scene mainScene, int loadedGameplay, int loadedShape, int loadedWinConditions,
                         boolean loadedBlueTurn, int loadedBlueHexagons, int loadedOrangeHexagons,
                         double loadedBluePercentage, double loadedOrangePercentage, List<String> hexagonData) {
        boardShape = loadedShape;
        gamePlay = loadedGameplay;
        winConditions = loadedWinConditions;
        blueTurn = loadedBlueTurn;
        blueHexagons = loadedBlueHexagons;
        orangeHexagons = loadedOrangeHexagons;
        bluePercentage.set(loadedBluePercentage);
        orangePercentage.set(loadedOrangePercentage);

        // Clear existing hexagons and load from file data
        hexagons.clear();
        loadedHexagons.clear();
        for (String line : hexagonData) {
            String[] parts = line.split(" ");
            int row = Integer.parseInt(parts[2]);
            int col = Integer.parseInt(parts[4]);
            HexagonColor color = HexagonColor.valueOf(parts[6].toUpperCase());
            int rotation = Integer.parseInt(parts[8]);
            ImageView hexagonView = new ImageView();
            hexagonView.setFitWidth(hexagonWidth);
            hexagonView.setFitHeight(hexagonHeight);
            Hexagon hexagon = new Hexagon(hexagonView, rotation, row, col, color);
            hexagon.setRotation(rotation);
            hexagon.setColor(color);
            if (loadedHexagons.size() <= row) {
                for (int i = loadedHexagons.size(); i <= row; i++) {
                    loadedHexagons.add(new ArrayList<>());
                }
            }
            loadedHexagons.get(row).add(hexagon);
        }
        startGame(stage, mainScene, loadedGameplay, loadedShape, loadedWinConditions);
    }

    /**
     * Pouzita iba na pohodlne spolahlive spocitanie zkorumpovatelnych hexagonov v ploche
     */
    private void countNonIdleNonInvisible(){
        int count = 0;
        for(var row : hexagons)
            for(var hexagon : row)
                if(hexagon.color != HexagonColor.INVISIBLE && hexagon.color != HexagonColor.SOLID)
                    count++;
        System.out.println(count);
    }

    /**
     * Vola sa zo startGame, generuje novu hru pre sestuholnikove plochy
     * @param root pane GameBoardu
     */
    private void generateHexagonalBoard(Pane root) {
        hexagons.clear();
        double centerX = 230; // Center X of the canvas
        double centerY = 10;
        for (int row = 1; row <= 33; row++) {
            hexagons.add(new ArrayList<>());
            for (int col = 0; col < 17; col++) {
                double x = centerX + (col - 4) * xOffset;
                double y = centerY + row * yOffset;
                if ((row % 2 == 0 && col % 2 == 0) || (row % 2 == 1 && col % 2 == 1)) {// invisible hexagon
                    emplaceInvisibleHexagon(root, row, col, x, y);
                    continue;
                }
                if((boardShape == 12 && hexagonHexagonSolids.contains(new Pair(row - 1, col)))
                        || (boardShape == 13 && hexagon3TrianglesSolids.contains(new Pair(row - 1, col)))){ // hexagonal board with inner solid hexagons
                    emplaceSolidHexagon(root, row, col, x, y);
                }
                else if ((row < 9 && (col <= 8 + row && col >= 8 - row)) || (row >= 9 && row <= 25) || (row > 25 && (col >= row - 25 && col <= 17 - (row - 25)))) {
                    emplaceColoredHexagon(root, row, col, x, y, HexagonColor.IDLE, -1);
                }
                else { // invisible hexagon
                    emplaceInvisibleHexagon(root, row, col, x, y);
                }
            }
        }
        // Set initial positions for blue and orange players
        if(boardShape == 13) {
            setInitialHexagons(0, 8, 32, 8, 240, 120);
        }
        else
            setInitialHexagons(0, 8, 32, 8, 240, 60);
    }

    /**
     * Vola sa zo startGame, generuje novu hru pre trojuholnikove plochy
     * @param root pane GameBoardu
     */
    private void generateTriangleBoard(Pane root){
        hexagons.clear();
        double centerX = 230; // Center X of the canvas
        double centerY = 10;
        for (int row = 1; row <= 33; row++) {
            hexagons.add(new ArrayList<>());
            for (int col = 0; col < 17; col++) {
                double x = centerX + (col - 4) * xOffset;
                double y = centerY + row * yOffset;
                if ((row % 2 == 0 && col % 2 == 0) || (row % 2 == 1 && col % 2 == 1) || (col < Math.abs(row - 17))) {   // invisible hexagon
                    emplaceInvisibleHexagon(root, row, col, x, y);
                }
                else if((boardShape == 22 && (row >= 11 && col >= 9 && row <= 23))
                        || (boardShape == 23 && triangleMiddleTriangle.contains(new Pair(row - 1, col)))){ // solid hexagons
                    emplaceSolidHexagon(root, row, col, x, y);
                }
                else{
                    emplaceColoredHexagon(root, row, col, x, y, HexagonColor.IDLE, -1);
                }
            }
        }
        setInitialHexagons(0, 16, 32, 16, 300, 60);
    }

    /**
     * Na konci generovania novej prazdnej hracej plochy sa umiestnia vychodzie spravne otocene hexagony hracov,
     * vzdy zacinaju s 1 hexagonom
     * @param initialBlueRow vzdy som volil najvrchnejsi hexagon v ploche
     * @param initialBlueCol
     * @param initialOrangeRow vzdy som volil najspodnejsi hexagon v ploche
     * @param initialOrangeCol
     * @param initialBlueRotation
     * @param initialOrangeRotation povodne otocenia su otocene tak, aby po prvom kliknuti uz mohli korumpovat
     */
    private void setInitialHexagons(int initialBlueRow, int initialBlueCol, int initialOrangeRow, int initialOrangeCol, int initialBlueRotation, int initialOrangeRotation){
        hexagons.get(initialBlueRow).get(initialBlueCol).setColor(HexagonColor.BLUE);
        hexagons.get(initialBlueRow).get(initialBlueCol).setRotation(initialBlueRotation);
        hexagons.get(initialOrangeRow).get(initialOrangeCol).setColor(HexagonColor.ORANGE);
        hexagons.get(initialOrangeRow).get(initialOrangeCol).setRotation(initialOrangeRotation);
    }

    /**
     * Vola sa z metod, ktore generuju alebo nacitavaju plochu, umiestnuje iba solidne hexagony (prekazky)
     * @param root pane GameBoardu
     * @param row
     * @param col suradnice v clenskom ArrayListe hexagons
     * @param x
     * @param y suradnice v canvase
     */
    private void emplaceSolidHexagon(Pane root, int row, int col, double x, double y){
        ImageView hexagonView = new ImageView(solidHexagonImage);
        hexagonView.setFitWidth(solidHexagonWidth);
        hexagonView.setFitHeight(solidHexagonHeight);
        hexagonView.setLayoutX(x+3.5);
        hexagonView.setLayoutY(y+5);
        Hexagon hexagon = new Hexagon(hexagonView, 0, row - 1, col, HexagonColor.SOLID);
        root.getChildren().add(hexagonView);
        hexagons.get(row - 1).add(hexagon);
    }

    /**
     * Vola sa z metod, ktore generuju alebo nacitavaju plochu, umiestnuje iba neviditelne hexagony
     * @param root pane GameBoardu
     * @param row
     * @param col suradnice v clenskom ArrayListe hexagons
     * @param x
     * @param y suradnice v canvase
     */
    private void emplaceInvisibleHexagon(Pane root, int row, int col, double x, double y){
        ImageView hexagonView = new ImageView(idleHexagonImage);
        hexagonView.setFitWidth(0);
        hexagonView.setFitHeight(0);
        hexagons.get(row - 1).add(new Hexagon(hexagonView, 0, row - 1, col, HexagonColor.INVISIBLE));
    }

    /**
     * Vola sa z metod, ktore generuju alebo nacitavaju plochu, umiestnuje farebne a sede hexagony
     * @param root pane GameBoardu
     * @param row
     * @param col suradnice v clenskom ArrayListe hexagons
     * @param x
     * @param y suradnice v canvase
     */
    private void emplaceColoredHexagon(Pane root, int row, int col, double x, double y, HexagonColor color, int rotation){
        ImageView hexagonView;
        switch(color){
            case BLUE -> hexagonView = new ImageView(blueHexagonImage);
            case ORANGE -> hexagonView = new ImageView(orangeHexagonImage);
            default -> hexagonView = new ImageView(idleHexagonImage);
        }
        hexagonView.setFitWidth(hexagonWidth);
        hexagonView.setFitHeight(hexagonHeight);
        if(rotation == -1)
            rotation = random.nextInt(6) * 60;
        hexagonView.setRotate(rotation);
        hexagonView.setLayoutX(x);
        hexagonView.setLayoutY(y);
        Hexagon hexagon = new Hexagon(hexagonView, rotation, row - 1, col, color);
        hexagonView.setOnMouseClicked(e -> {
            if (!gameEnded) {
                rotateHexagon(hexagon);
            }
        });
        root.getChildren().add(hexagonView);
        hexagons.get(row - 1).add(hexagon);
    }

    /**
     * Metoda rotateHexagon je bindnuta na event kliknutia na Image Hexagonu,
     * otacanie je vzdy o 60 stupnov v smere hodinovych ruciciek
     * @param hexagon otacany hexagon, aby sa mohol aktualizovat jeho atribut rotacie
     */
    private void rotateHexagon(Hexagon hexagon) {
        if ((blueTurn && hexagon.color == HexagonColor.BLUE) || (!blueTurn && hexagon.color == HexagonColor.ORANGE)) {
            hexagon.setRotation((hexagon.rotation + 60) % 360);
            blueTurn = !blueTurn; // Switch turns
            turnLabel.setText("Turn: " + (blueTurn ? "Blue" : "Orange"));
            visited.clear();
            checkAndChangeHexagons(hexagon);
        }
    }

    /**
     * Meni farbu hexagonu, na ktory nanovo otoceny hexagon ukazuje.
     * Toto sa rekurzivne vola aj na dalsie hexagony, ktore na nieco mozu ukazovat.
     * Potom este skontroluje, ci nan neukazuju ine hexagony, v tom pripade sa tiez maju zafarbit,
     * to ale riesi metoda checkNeighborHexagons
     * @param hexagon - podla jeho farby sa maju zafarbit vsetky prepojene hexagony
     */
    private void checkAndChangeHexagons(Hexagon hexagon) {
        int row = hexagon.row;
        int col = hexagon.col;
        Hexagon target = null;
        try {
            switch (hexagon.rotation) {
                case 0 -> target = hexagons.get(row + 2).get(col);
                case 60 -> target = hexagons.get(row + 1).get(col - 1);
                case 120 -> target = hexagons.get(row - 1).get(col - 1);
                case 180 -> target = hexagons.get(row - 2).get(col);
                case 240 -> target = hexagons.get(row - 1).get(col + 1);
                case 300 -> target = hexagons.get(row + 1).get(col + 1);
            }
        } catch (Exception e) {
            // index out of range - no targets, no actions
        }
        // always change what is pointed to
        if (target != null && target.color != hexagon.color && target.color != HexagonColor.INVISIBLE && target.color != HexagonColor.SOLID) {
            if (hexagon.color == HexagonColor.BLUE) {
                if (target.color == HexagonColor.ORANGE) orangeHexagons--;
                target.setColor(HexagonColor.BLUE);
                blueHexagons++;
            } else if (hexagon.color == HexagonColor.ORANGE) {
                if (target.color == HexagonColor.BLUE) blueHexagons--;
                target.setColor(HexagonColor.ORANGE);
                orangeHexagons++;
            }
            updatePercentages();
            visited.add(hexagon);
            checkAndChangeHexagons(target);
            checkNeighborHexagons(hexagon);
            checkNeighborHexagons(target);
            visited.add(target);
            if(!gameEnded)
                checkWinConditions();
        }
        else if(target == null || (target.color != hexagon.color)){
            checkNeighborHexagons(hexagon);
            visited.add(hexagon);
        }
    }

    /**
     * Rekurzivne kontroluje, ci nan neukazuju ine hexagony, v tom pripade sa tiez maju zafarbit
     * @param hexagon - podla jeho farby sa maju zafarbit vsetky prepojene hexagony
     */
    private void checkNeighborHexagons(Hexagon hexagon) {
        for (int[] direction : directions) {
            int newRow = hexagon.row + direction[0];
            int newCol = hexagon.col + direction[1];
            int desiredRotation = direction[2];
            try {
                Hexagon neighbor = hexagons.get(newRow).get(newCol);
                if (neighbor.rotation == desiredRotation && neighbor.color != HexagonColor.INVISIBLE && neighbor.color != HexagonColor.SOLID) {
                    if (neighbor.color == HexagonColor.ORANGE && hexagon.color == HexagonColor.BLUE) {
                        blueHexagons++;
                        orangeHexagons--;
                    } else if (neighbor.color == HexagonColor.BLUE && hexagon.color == HexagonColor.ORANGE) {
                        blueHexagons--;
                        orangeHexagons++;
                    } else if (neighbor.color == HexagonColor.IDLE && hexagon.color == HexagonColor.ORANGE) {
                        orangeHexagons++;
                    } else if (neighbor.color == HexagonColor.IDLE && hexagon.color == HexagonColor.BLUE) {
                        blueHexagons++;
                    }

                    updatePercentages();
                    if (!visited.contains(neighbor) ) {
                        neighbor.setColor(hexagon.color);
                        checkNeighborHexagons(neighbor);
                    } else {
                        neighbor.setColor(hexagon.color);
                    }
                }
            } catch (Exception e) {
                continue; // Index out of bounds, just continue checking
            }
        }
        visited.add(hexagon);
    }

    /**
     * Podla podmienok vyhry oznami koniec hry a zablokuje dalsie tahy.
     */
    private void checkWinConditions() {
        if (blueHexagons == 0 || orangeHexagons == 0) {
            gameEnded = true;
            showAlert("Game Over", blueHexagons <= 0 ? "Orange Won!" : "Blue Won!");
        } else if (winConditions == 1 && bluePercentage.get() >= 70) {
            gameEnded = true;
            showAlert("Game Over", "Blue Won by Domination!");
        } else if (winConditions == 1 && orangePercentage.get() >= 70) {
            gameEnded = true;
            showAlert("Game Over", "Orange Won by Domination!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePercentages() {
        bluePercentage.set((blueHexagons * 100.0) / totalHexagons);
        orangePercentage.set((orangeHexagons * 100.0) / totalHexagons);
    }

    private String getGameMode(int gameMode) {
        return switch (gameMode) {
            case 1 -> "2-Player Mode";
            case 2 -> "Play Against Easy AI";
            case 3 -> "Play Against Hard AI";
            default -> "Unknown";
        };
    }

    private String getShape() {
        return switch (boardShape) {
            case 11 -> "Hexagon";
            case 12 -> "Hexagon-Hexagon";
            case 13 -> "Hexagon-3Triangles";
            case 21 -> "Triangle";
            case 22 -> "Triangle-Rectangle";
            case 23 -> "Triangle-Triangle";
            default -> "Unknown";
        };
    }

    private String getWinConditions(int winConditions) {
        return switch (winConditions) {
            case 1 -> "Domination";
            case 2 -> "Obliteration";
            default -> "Unknown";
        };
    }

    private void resetBoard(Pane root) {
        root.getChildren().clear();
        hexagons.clear();
        gameEnded = false;
        blueHexagons = 1;
        orangeHexagons = 1;
        blueTurn = true;
        visited.clear();
        bluePercentage.set(1.0 / totalHexagons * 100);
        orangePercentage.set(1.0 / totalHexagons * 100);
    }

    /**
     * Po stlaceni Buttonu Save ulozi aktualny stav hry do textoveho suboru v zlozke saves
     */
    private void saveGame() {
        try {
            String directoryPath = "saves";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();
            String dateString = formatter.format(date);

            String filename = String.format("%s_%s_%d.txt", getShape(), dateString, new Random().nextInt(1000));
            File file = new File(directoryPath + "/" + filename);
            FileWriter writer = new FileWriter(file);

            writer.write("Game Mode: " + getGameMode(gamePlay) + "\n");
            writer.write("Shape: " + getShape() + "\n");
            writer.write("Win Conditions: " + getWinConditions(winConditions) + "\n");
            writer.write("Turn: " + (blueTurn ? "Blue" : "Orange") + "\n");
            writer.write("Blue Hexagons: " + blueHexagons + "\n");
            writer.write("Orange Hexagons: " + orangeHexagons + "\n");
            writer.write("Blue Percentage: " + bluePercentage.get() + "\n");
            writer.write("Orange Percentage: " + orangePercentage.get() + "\n");
            writer.write("Hexagons:\n");

            for (List<Hexagon> row : hexagons) {
                for (Hexagon hex : row) {
                    writer.write(String.format("Hexagon row %d col %d color %s rotation %d\n",
                            hex.row, hex.col, hex.color, hex.rotation));
                }
            }

            writer.close();

            showAlert("Save Game", "Game saved successfully!");
        } catch (IOException e) {
            showAlert("Save Game", "Error saving game: " + e.getMessage());
        }
    }
}
