import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {

    private static int canvasWidth = 600,
            canvasHeight = 600,
    amountWidth = 20,
    amountHeight = 20,
    blockWidth = canvasWidth / amountWidth,
    blockHeight = canvasHeight / amountHeight;

    static Random rand = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox vBox = new VBox();
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        vBox.getChildren().add(canvas);
        stage.setTitle("Snek");
        stage.setScene(new Scene(vBox, canvasWidth, canvasHeight));
        stage.show();
        new AnimationTimer() {
            long lastFrame = 0;
            @Override
            public void handle(long now) {
                double elapsedTime = (now - lastFrame) / 1000000000.0;
                if (lastFrame == 0) {
                    lastFrame = now;
                    return;
                }
                if(elapsedTime > 1){
                    lastFrame = now;
                    createFood(graphicsContext);
                }
            }
        }.start();
        frame(graphicsContext);
    }

    private void frame(GraphicsContext graphicsContext){
        createFood(graphicsContext);
    }

    private void createFood(GraphicsContext graphicsContext){
        int xPos = rand.nextInt(amountWidth) * blockWidth;
        int yPos = rand.nextInt(amountHeight) * blockHeight;
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(xPos, yPos, blockWidth, blockHeight);
    }

}
