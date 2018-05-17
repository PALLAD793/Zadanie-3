package tictactoe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		try {
			Controller controller = new Controller();

			Scene scene = controller.initWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Kółko i krzyżyk");
			primaryStage.setResizable(false);
			primaryStage.setOnHiding(e -> controller.stageHiding());
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}