package be.lyam.ui;

import be.lyam.AIPlayer;
import be.lyam.Game;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class HomeUI {


	private static HomeUI i;
	private Scene scene;
	private FXController main;
	
	private int players;

	public static Scene getScene(FXController main) {
		return (i == null) ? (new HomeUI(main)).createScene() : i.createScene();
	}
	
	public Scene createScene() {
		return this.scene;
	}
	
	public HomeUI(FXController main) {
		this.main = main;
		VBox root = new VBox(20);
		
		BorderPane options = new BorderPane();
		VBox b = new VBox();
		Label p = new Label("numbers of players : ");
		TextField count = new TextField();
		count.setPromptText("]0; 10]");
		options.setPadding(new Insets(5, 20, 5, 20));
		b.getChildren().addAll(p,count);
		options.setCenter(b);

		Button start = new Button("start");
		start.setOnAction((e) -> {
			try {
				final int c = (count.getText() != null ? Integer.parseInt(count.getText()) : 1);
				players = (c <= 10 && c > 0 ? c : 1);
			} catch(Exception t) {
				players = 1;
			}
			this.createGame();
		});
		
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(options, start);
		this.scene = new Scene(root, 300, 200);
	}
	
	public void createGame() {
		Game game = new Game(new AIPlayer(), players);
		game.setConsolePrinter(false);
		this.main.getWindow().setScene(new BoardUI(main, game).createScene());
	}
	
}
