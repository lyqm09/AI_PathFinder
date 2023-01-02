package be.lyam.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class FXController extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	public static final String TITLE = "AI";

	private Stage window;

	
	public void start(Stage stage) throws Exception {
		this.window = stage;

		this.window.setOnCloseRequest(e -> {
			e.consume();
			this.window.close();
			System.exit(0);
		});

		this.window.setTitle(TITLE);
		this.window.setResizable(false);
	//	this.window.setScene(HomeScene.getScene(this));
		this.window.setScene(HomeUI.getScene(this));
		this.window.show();
	}

	public Stage getWindow() {
		return this.window;
	}


}
