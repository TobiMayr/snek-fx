import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private static int canvasWidth = 600,
            canvasHeight = 600,
            amountWidth = 20,
            amountHeight = 20,
            blockWidth = canvasWidth / amountWidth,
            blockHeight = canvasHeight / amountHeight,
            foodX,
            foodY;
    private static double speed = 0.2;
    private static Random rand = new Random();
    private static List<Block> snekList = new ArrayList<>();
    private static Direction direction = Direction.RIGHT;
    private static boolean gameOver = false,
            paused = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox vBox = new VBox();
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        vBox.getChildren().add(canvas);
        stage.setTitle("Snek");
        Scene scene = new Scene(vBox, canvasWidth, canvasHeight);
        setKeyEvents(scene);
        stage.setScene(scene);
        stage.show();
        createFood();
        setUpSnek();
        new AnimationTimer() {
            long lastFrame = 0;

            @Override
            public void handle(long now) {
                if (paused) {
                    showControls(graphicsContext);
                }
                double elapsedTime = (now - lastFrame) / 1_000_000_000.0;
                if (lastFrame == 0) {
                    lastFrame = now;
                    return;
                }
                if (elapsedTime > speed && !paused) {
                    lastFrame = now;
                    frame(graphicsContext);
                }
            }
        }.start();
        frame(graphicsContext);
    }

    private void frame(GraphicsContext graphicsContext) {
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        if (gameOver) {
            showGameOver(graphicsContext);
            return;
        }
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        showGrid(graphicsContext);
        showFood(graphicsContext);
        moveSnek();
        showSnek(graphicsContext);
        eatFood();
        checkIfSnekEatsSnek();
        showScore(graphicsContext);
    }

    private void showGrid(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.DARKGRAY);
        graphicsContext.setLineWidth(0.1);
        for (int i = 0; i < amountWidth; i++) {
            graphicsContext.strokeLine(i * blockWidth, 0, i * blockWidth, canvasHeight);
        }
        for (int i = 0; i < amountHeight; i++) {
            graphicsContext.strokeLine(0, i * blockHeight, canvasWidth, i * blockHeight);
        }
    }

    private void showControls(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(new Font("", 15));
        graphicsContext.fillText("WASD to Move - SPACE to Pause", canvasWidth / 2, canvasHeight / 3 * 2);
    }


    private void showGameOver(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.RED);
        graphicsContext.setFont(new Font("", 50));
        graphicsContext.fillText("GAME OVER", canvasWidth / 2, canvasHeight / 2);
    }

    private void setKeyEvents(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.W) {
                if (direction != Direction.DOWN && !paused) {
                    direction = Direction.UP;
                }
            } else if (key.getCode() == KeyCode.A) {
                if (direction != Direction.RIGHT && !paused) {
                    direction = Direction.LEFT;
                }
            } else if (key.getCode() == KeyCode.S) {
                if (direction != Direction.UP && !paused) {
                    direction = Direction.DOWN;
                }
            } else if (key.getCode() == KeyCode.D) {
                if (direction != Direction.LEFT && !paused) {
                    direction = Direction.RIGHT;
                }
            } else if (key.getCode() == KeyCode.SPACE) {
                paused = !paused;
            }
        });
    }

    private void showScore(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(new Font("", 30));
        graphicsContext.fillText("Score: " + (snekList.size() - 4), canvasWidth / 2, 30);
    }

    private void eatFood() {
        if (foodX == snekList.get(0).getPosX() && foodY == snekList.get(0).getPosY()) {
            snekList.add(new Block(0, 0));
            createFood();
        }
    }

    private void checkIfSnekEatsSnek() {
        for (int i = 1; i < snekList.size(); i++) {
            if (snekList.get(0).getPosX() == snekList.get(i).getPosX() && snekList.get(0).getPosY() == snekList.get(i).getPosY()) {
                gameOver = true;
            }
        }
    }

    private void moveSnek() {
        for (int i = snekList.size() - 1; i >= 1; i--) {
            snekList.get(i).setPosX(snekList.get(i - 1).getPosX());
            snekList.get(i).setPosY(snekList.get(i - 1).getPosY());
        }
        switch (direction) {
            case RIGHT:
                snekList.get(0).setPosX(snekList.get(0).getPosX() + 1);
                if (snekList.get(0).getPosX() > amountWidth - 1) {
                    gameOver = true;
                }
                break;
            case LEFT:
                snekList.get(0).setPosX(snekList.get(0).getPosX() - 1);
                if (snekList.get(0).getPosX() < 0) {
                    gameOver = true;
                }
                break;
            case DOWN:
                snekList.get(0).setPosY(snekList.get(0).getPosY() + 1);
                if (snekList.get(0).getPosY() > amountHeight - 1) {
                    gameOver = true;
                }
                break;
            case UP:
                snekList.get(0).setPosY(snekList.get(0).getPosY() - 1);
                if (snekList.get(0).getPosY() < 0) {
                    gameOver = true;
                }
                break;
        }
    }

    private void createFood() {
        boolean isInSnek;
        do {
            isInSnek = false;
            foodX = rand.nextInt(amountWidth);
            foodY = rand.nextInt(amountHeight);
            for (Block block : snekList) {
                if (foodX == block.getPosX() && foodY == block.getPosY()) {
                    isInSnek = true;
                    break;
                }
            }
        } while (isInSnek);

    }

    private void showFood(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(foodX * blockWidth, foodY * blockHeight, blockWidth, blockHeight);
    }

    private void setUpSnek() {
        int middleX = amountWidth / 2;
        int middleY = amountHeight / 2;
        snekList.add(new Block(middleX, middleY));
        snekList.add(new Block(middleX - 1, middleY));
        snekList.add(new Block(middleX - 2, middleY));
        snekList.add(new Block(middleX - 3, middleY));
    }

    private void showSnek(GraphicsContext graphicsContext) {
        for (Block block : snekList) {
            int xCoord = block.getPosX() * blockWidth;
            int yCoord = block.getPosY() * blockHeight;
            graphicsContext.setFill(Color.LIGHTGREY);
            graphicsContext.fillRect(xCoord, yCoord, blockWidth, blockHeight);
            graphicsContext.setFill(Color.GREY);
            graphicsContext.fillRect(xCoord - 2, yCoord - 2, blockWidth - 2, blockHeight - 2);
        }
    }
}