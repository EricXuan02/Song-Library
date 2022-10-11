//Eric Xuan (ejx2) and Michael Bazarsky (mab777)

package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
//import view.SceneController;
import view.*;

public class SongLib extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/Scene.fxml"));
//		System.out.println(getClass().getResource("/view/Scene.fxml"));

		AnchorPane root = (AnchorPane)loader.load();

		SceneController sceneController = loader.getController();
		sceneController.start(primaryStage);

		Scene scene = new Scene(root, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	//Implement compareTo function with this
	
	
	public static void main(String[] args) {
		launch(args);
		
		SongLib sl = new SongLib();
		
//		System.out.println(sl.getClass().getResource("/view/Scene.fxml"));
	}

}
