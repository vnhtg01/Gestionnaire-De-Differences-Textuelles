package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.TextMergedView;

public class Main extends Application {

    private static final String MERGE_TITLE = "TEXT_MERGE";
    private static final int MERGE_WIDTH = 900;
    private static final int MERGE_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        Scene MainSceneView = new Scene(new TextMergedView()); // Creation View
        new control.TextMergedController((TextMergedView) MainSceneView.getRoot()); // Créer un contrôleur à convertir en View
        primaryStage.setTitle(MERGE_TITLE);
        primaryStage.setScene(MainSceneView);
        primaryStage.setWidth(MERGE_WIDTH);
        primaryStage.setHeight(MERGE_HEIGHT);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}