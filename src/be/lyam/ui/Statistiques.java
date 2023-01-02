package be.lyam.ui;

import be.lyam.Game;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Statistiques {
	
	@SuppressWarnings("unused")
	private Game game;
	private Scene scene;
	private Stage stage;
	private StackPane stats;
	
	private XYChart.Series<Number,Number> shots;
	private XYChart.Series<Number,Number> explor;
	
//	private XYChart.Series<Number,Number> error;
	
	public Statistiques(Game game) {
		this.game = game;
		this.stats = new StackPane();
		this.stats.getChildren().add(this.graphs());
		
		BorderPane pane = new BorderPane();
		pane.setCenter(this.stats);
		this.scene = new Scene(pane);
	}
	
	public StackPane graphs() {
		
		NumberAxis x = new NumberAxis();
		x.setLabel("Games");
		NumberAxis y = new NumberAxis();
		y.setLabel("shots");
		LineChart<Number, Number> l = new LineChart<Number, Number>(x, y);
		
		if(this.shots == null)
			shots = new XYChart.Series<>();
		shots.setName("shots");
		if(this.explor == null)
			explor = new XYChart.Series<>();
		explor.setName("explor");
		
	//	shots.getData().add(new XYChart.Data<Number, Number>(j, i));
		
		l.getData().add(shots);
		l.getData().add(explor);
		
		StackPane pane = new StackPane();
		pane.getChildren().add(l);
		return pane;
	}
	
	public boolean update(int shots, int explor, int i) {
		
		//System.out.println(i);
		
		this.explor.getData().add(new XYChart.Data<Number,Number>(i,explor));
		this.shots.getData().add(new XYChart.Data<Number,Number>(i,shots));
	
		
		this.stats.getChildren().clear();
		this.stats.getChildren().add(this.graphs());
		
		return true;
	}
	
	public void show() {
		if (this.stage != null) {
			this.stage.toFront();
		} else {
			this.stage = new Stage(StageStyle.DECORATED);
			this.stage.setTitle("Game's statistics");
			this.stage.setScene(this.scene);
			this.stage.setOnCloseRequest((e) -> this.stage = null);
			this.stage.show();
		}
	}

}
