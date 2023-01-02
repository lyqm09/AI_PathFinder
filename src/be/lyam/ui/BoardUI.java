package be.lyam.ui;
import java.util.List;

import javax.swing.Timer;

import be.lyam.AIPlayer;
import be.lyam.Board;
import be.lyam.Game;
import be.lyam.Location;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BoardUI {
	
	private Game game;
	private double SIZE;
	@SuppressWarnings("unused")
	private FXController main;
	
	private StackPane v;
	private TextField speed;
	
	private int rpl = 0;
	private int apl = 0;
	
	
	public BoardUI(FXController main, Game game) {
		this.main = main;
		this.game = game;
		this.SIZE = 400;		
	}
	
	public Scene createScene() {
		v = new StackPane();
		BorderPane root = new BorderPane();
		
		HBox h = new HBox();
		this.speed = new TextField(this.DELAY+"");
		
		Button stats = new Button("Stats");
		stats.setOnAction(e -> {
			this.game.getStats().show();
		});
		
		
		h.getChildren().addAll(this.speed, stats);
		h.setAlignment(Pos.CENTER);
		
		this.update();
		
		root.setTop(h);
		root.setCenter(v);
		Scene view = new Scene(root, 900, 500);
		KeyCombination up = new KeyCodeCombination(KeyCode.UP, KeyCodeCombination.CONTROL_DOWN);
		KeyCombination down = new KeyCodeCombination(KeyCode.DOWN, KeyCodeCombination.CONTROL_DOWN);
		view.setOnKeyPressed(e -> {
			if(up.match(e)) {
				this.apl+=1;
			} else if(down.match(e)) {
				this.rpl+=1;
			}
		});
		return view;
	}
	
	private int DELAY = 100;
	private Timer timer;
	
	public void update() {
		if(game.isOver()) {
			HBox b = this.gameOver();b.setAlignment(Pos.CENTER);v.getChildren().add(b);
			timer.stop();
			return;
		}
		
		if(this.rpl>0 && this.rpl<10) this.game.removePlayers(this.rpl); this.rpl = 0;
		if(this.apl<10 && this.apl>0) this.game.addPlayers(this.apl); this.apl = 0;
		
		try {
			final int c = (speed.getText() != null ? Integer.parseInt(speed.getText()) : this.DELAY);
			this.DELAY = (c >= 0 && c <= 10001 ? c : this.DELAY);
		} catch(Exception t) {
			speed.setText(this.DELAY+"");
		}
		
		this.runPlayer();
		v.getChildren().clear();
		v.getChildren().add(this.create());
		
	}
	
	private void runPlayer() {
		AIPlayer player = this.game.getPlayer();
		// Set a timer to run
		this.timer = new Timer(DELAY, (e) -> {
			
			for(int p = 1; p <= this.game.getNPlayers(); p++) {
				if(player.update(p-1,game))
					break;
			}
			
			this.timer.stop();
			Platform.runLater(() -> this.update());
		});
		this.timer.start();
	}
	
	private HBox gameOver() {
			Label o = new Label("Game Over");
			o.setPadding(new Insets(10,10,10,10));
			o.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
			o.setTextFill(Color.RED);
			o.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(30), new BorderWidths(2))));
		return new HBox(o);
	}

	private StackPane create() {
		List<Location> players = this.game.getPlayer().getLocations();
		StackPane root = new StackPane();
		
		VBox panel = new VBox();
		HBox boards = new HBox();
		
		Group groupe = new Group();
		
		//first 
		StackPane board = new StackPane();
		VBox columns = new VBox();
		for(int y = 0; y < Board.SIZE; y++) {
			HBox lines = new HBox();
			for(int x = 0; x < Board.SIZE; x++) {
				StackPane square = new StackPane();
				Rectangle r = new Rectangle();
				r.setWidth(SIZE/Board.SIZE);
				r.setHeight(SIZE/Board.SIZE);
				r.setFill(this.isPaire(x+y) ? Color.BLACK: Color.WHITE);
				square.getChildren().add(r);
				Circle c = new Circle();
				c.setRadius(SIZE/Board.SIZE/2);
				c.setFill(Color.DARKGRAY);
				c.setStrokeType(StrokeType.INSIDE);
				
				Location p = null;
				for(Location l : players) {
					if(l != null && l.equals2D(x, y)) {
						p = l;
						continue;
					}
						
				}
				
				if(p != null && p.equals2D(x,y)) {
					c.setStroke(Color.RED);
					square.getChildren().add(c);
					Label floor = new Label(String.valueOf(p.z));
					floor.setAlignment(Pos.CENTER);
					square.getChildren().add(floor);
				} else if(this.game.getBoard().getFlag().equals2D(x,y)) {
					c.setStroke(Color.BLUE);
					square.getChildren().add(c);
					Label floor = new Label(String.valueOf(this.game.getBoard().getFlag().z));
					floor.setAlignment(Pos.CENTER);
					square.getChildren().add(floor);
				} else if(this.game.getBoard().hasPortals2D(x,y)) {
					c.setStroke(Color.PINK);
					square.getChildren().add(c);
					Label floor = new Label(String.valueOf(this.game.getBoard().getPortalZLocation(x, y)));
					floor.setAlignment(Pos.CENTER);
					square.getChildren().add(floor);
				}
				lines.getChildren().add(square);
			}
			columns.getChildren().add(lines);
		}
		board.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
		board.getChildren().add(columns);
		groupe.getChildren().add(board);
		
		boards.setSpacing(20);
		boards.setAlignment(Pos.CENTER);
		boards.getChildren().addAll(groupe);
		
		HBox infos = new HBox();
		Label games = new Label("Game : "+this.game.getIterator());
		games.setPrefWidth(70);
		Label shots = new Label("Shots : "+this.game.getShots());
		shots.setPrefWidth(70);
		infos.setSpacing(30);
		infos.setAlignment(Pos.CENTER);
		infos.getChildren().addAll(games, shots);
		
		panel.getChildren().addAll(boards, infos);
		
		root.getChildren().add(panel);
		return root;
	}
	
	private boolean isPaire(int i) {
		for(int j = 0; j <= i; j++) {
			if(i == j*2)
				return true;
			if(i < j*2)
				return false;
		}
		return false;
	}
	
/*	private Timer timer;
	
	private void update() {
		if(game.isOver()) {
			return;
		}
		this.runPlayer();
		
		v.getChildren().clear();
		v.getChildren().add(this.create());
		
		System.out.println("test");
	}
	
	private void runPlayer() {
		AIPlayer player = this.game.getPlayer();
		// Set a timer to run
		this.timer = new Timer(DELAY, (e) -> {
			player.update(game);
			this.timer.stop();
			this.update();
		});
		this.timer.start();
	}
	*/
	/*
	public HBox createBoard() {
		
		
		HBox root = new HBox();
		root.setSpacing(2);
		
		Group groupe = new Group();
		Group groupe2 = new Group();
		
		// first board x-z
		StackPane board = new StackPane();
		VBox columns = new VBox(); 
		for(int z = 0; z < Board.SIZE; z++) {
			HBox lines = new HBox(); 
			for(int x = 0; x < Board.SIZE; x++) {
				StackPane square = new StackPane();
				Rectangle r = new Rectangle();
				Circle c = null;
				for(int y = 0; y < Board.SIZE; y++) {
					if(game.getBoard().get(new Location(x,y,z)) == Piece.EMPTY) {
						continue;
					} else if(game.getBoard().get(new Location(x,y,z)) == Piece.PLAYER) {
					//	if(this.game.getPlayer().getLocation().equals(new Location(x,y,z))) {
					//		System.out.println(this.game.getPlayer().getLocation().toString());
						c = new Circle();
						c.setStroke(Color.RED);
						break;
					} else if(game.getBoard().get(new Location(x,y,z)) == Piece.PORTAL){
						c = new Circle();
						c.setStroke(Color.PINK);
						break;
					} else if(game.getBoard().get(new Location(x,y,z)) == Piece.FLAG) {
						c = new Circle();
						c.setStroke(Color.BLUE);
						break;
					}
				}
				r.setWidth(SIZE/Board.SIZE);
				r.setHeight(SIZE/Board.SIZE);
				r.setFill(this.isPaire(z+x) ? Color.BLACK: Color.WHITE);
				square.getChildren().add(r);
				if(c != null) {
					c.setRadius(SIZE/Board.SIZE/2);
					c.setFill(Color.DARKGRAY);
					c.setStrokeType(StrokeType.INSIDE);
					square.getChildren().add(c);
				}
				lines.getChildren().add(square);
			}
			columns.getChildren().add(lines);
		}
		board.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
		board.getChildren().add(columns);
		groupe.getChildren().add(board);
		
		// second board x-y
		StackPane board2 = new StackPane();
		VBox columns2 = new VBox(); 
		for(int y = 0; y < Board.SIZE; y++) {
			HBox lines2 = new HBox(); 
			for(int x = 0; x < Board.SIZE; x++) {
				StackPane square = new StackPane();
				Rectangle r = new Rectangle();
				Circle c = null;
				for(int z = 0; z < Board.SIZE; z++) {
					if(game.getBoard().get(new Location(x,y,z)) == Piece.EMPTY) {
						continue;
					} else if(game.getBoard().get(new Location(x,y,z)) == Piece.PLAYER) {
						c = new Circle();
						c.setStroke(Color.RED);
						break;
					} else if(game.getBoard().get(new Location(x,y,z)) == Piece.PORTAL){
						c = new Circle();
						c.setStroke(Color.PINK);
						break;
					} else if(game.getBoard().get(new Location(x,y,z)) == Piece.FLAG) {
						c = new Circle();
						c.setStroke(Color.BLUE);
						break;
					}
				}
				r.setWidth(SIZE/Board.SIZE);
				r.setHeight(SIZE/Board.SIZE);
				r.setFill(this.isPaire(y+x) ? Color.BLACK: Color.WHITE);
				square.getChildren().add(r);
				if(c != null) {
					c.setRadius(SIZE/Board.SIZE/2);
					c.setFill(Color.DARKGRAY);
					c.setStrokeType(StrokeType.INSIDE);
					square.getChildren().add(c);
				}
				lines2.getChildren().add(square);
			}
			columns2.getChildren().add(lines2);
		}
		board2.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
		board2.getChildren().add(columns2);
		groupe2.getChildren().add(board2);
		
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(groupe, groupe2);
		
		return root;
	}
	*/
	
}
