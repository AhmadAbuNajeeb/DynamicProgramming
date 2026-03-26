package application;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class Main extends Application {
	
	static class Path {
		private String path;
		private int cost;

		public Path(String path, int cost) {
			this.path = path;
			this.cost = cost;
		}
		public String getPath() {return path;}
		
		public int getCost() {return cost;}
	}
	
	//======================================= static variables ============================================================
	
	public static int numOfCities = 0;
	public static int fromCity = 0;
	public static int toCity = 0;
	public static String path;
	public static String destination;
	public static String[][] next;
	public static String[] city;
	public static String startingCity = "";
	public static String input = "";
	public static int[][] table;
	public static int d = 0;

	//============================================== start method =========================================================
	
	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {

		Font font = Font.font("Verdana", FontPosture.ITALIC, 25);

		
		Label startCity = new Label("From City:");
		startCity.setTextFill(Color.BLACK);
		startCity.setFont(font);
		
		Label endCity = new Label("To City:");
		endCity.setTextFill(Color.BLACK);
		endCity.setFont(font);

		ComboBox<String> start = new ComboBox<String>();
		start.setPrefWidth(70);
		start.setPrefHeight(35);
		
		ComboBox<String> end = new ComboBox<String>();
		end.setPrefWidth(70);
		end.setPrefHeight(35);
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a File");
		File file = fileChooser.showOpenDialog(primaryStage);
		Scanner sc = new Scanner(file);

		String number = sc.nextLine();
		numOfCities = Integer.parseInt(number); // number of cities
		int numOfLine = 0;
		city = new String[numOfCities];
		String cityEnd = "";
		input = "";

		while (sc.hasNext()) {
			numOfLine++;
			if (numOfLine == 1) {
				String s = sc.nextLine();
				String[] str = s.split(", ");
				cityEnd = str[1]; // str[1] = end
				city[0] = str[0];
				city[numOfCities - 1] = str[1];
				continue;
			}
			String s = sc.nextLine(); 
			input += s + "\n";
			String[] str = s.split(", "); // first city in each line
			city[numOfLine - 2] = str[0]; // add cities to the array
			start.getItems().addAll(str[0]); // add cities to comboBox
			end.getItems().addAll(str[0]); // add cities to comboBox
		}

		start.getItems().addAll(cityEnd); // add endCity to comboBox
		end.getItems().addAll(cityEnd); // add endCity to comboBox

		start.setValue(start.getItems().get(0));
		end.setValue(end.getItems().get(numOfCities - 1));

		HBox hbStart = new HBox();
		hbStart.getChildren().addAll(startCity, start);
		hbStart.setSpacing(5);

		HBox hbEnd = new HBox();
		hbEnd.getChildren().addAll(endCity, end);
		hbEnd.setSpacing(5);
		
		HBox hbChooser = new HBox();
		hbChooser.getChildren().addAll(hbStart, hbEnd);
		hbChooser.setSpacing(200);
		
		TextField bestCostField = new TextField();
		bestCostField.setEditable(false);
		bestCostField.setPrefColumnCount(6);
		bestCostField.setPrefWidth(200);
		bestCostField.setPrefHeight(20);
		bestCostField.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		Button findMinCost = new Button("Find Minimun Cost");
		findMinCost.setFont(font);
		findMinCost.setTextFill(Color.BLACK);
		
		HBox best = new HBox(bestCostField, findMinCost);
		best.setSpacing(30);
		
		TextField pathField = new TextField();
		pathField.setEditable(false);
		pathField.setPrefColumnCount(6);
		pathField.setPrefWidth(400);
		pathField.setPrefHeight(100);
		pathField.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		Button printPath = new Button("Print best path");
		printPath.setFont(font);
		printPath.setTextFill(Color.BLACK);
		
		HBox hbPath = new HBox();
		hbPath.getChildren().addAll(pathField, printPath);
		hbPath.setSpacing(30);

		Button btTable = new Button("Table --->");
		btTable.setFont(font);
		btTable.setTextFill(Color.BLACK);
		
		HBox hbTable = new HBox();
		hbTable.getChildren().addAll(hbPath, btTable);
		hbTable.setSpacing(200);
		
		TextArea othersArea = new TextArea();
		othersArea.setEditable(false);
		othersArea.setPrefHeight(200);
		othersArea.setPrefWidth(500);
		othersArea.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		Button printOthers = new Button("Print alternative paths");
		printOthers.setFont(font);
		printOthers.setTextFill(Color.BLACK);

		HBox hbAlternative = new HBox();
		hbAlternative.getChildren().addAll(othersArea, printOthers);
		hbAlternative.setSpacing(40);

		VBox vBox = new VBox();
		vBox.getChildren().addAll(hbChooser, best, hbTable, btTable,hbAlternative );
		vBox.setSpacing(30);
		vBox.setFillWidth(false);
		vBox.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGREY, CornerRadii.EMPTY, Insets.EMPTY)));

		Scene scene = new Scene(vBox, 1000, 600);
		
	//======================================= Second Scene ================================================================

		Label lbTable = new Label(" DP Table :");
		TextArea taTable = new TextArea();
		taTable.setEditable(false);
		taTable.setPrefHeight(600);
		taTable.setPrefWidth(600);
		taTable.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		lbTable.setTextFill(Color.BLACK);
		lbTable.setFont(font);

		Button showTable = new Button("Show table");
		showTable.setFont(font);
		showTable.setTextFill(Color.BLACK);
		
		HBox hbTableCost = new HBox();
		hbTableCost.getChildren().addAll(lbTable, showTable);
		hbTableCost.setSpacing(30);

		Button back = new Button("<--");
		back.setFont(font);
		back.setTextFill(Color.BLACK);

		VBox vbTable = new VBox(hbTableCost,taTable, back);
		vbTable.setSpacing(50);
		vbTable.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGREY, CornerRadii.EMPTY, Insets.EMPTY)));

		Scene sceneTable = new Scene(vbTable, 1400, 1000);
		
	//========================================  Buttons ============================================================

		findMinCost.setOnAction(e -> {
			if (toCity >= fromCity) {
				algorithm(start, end, input);
				bestCostField.setText(Integer.toString(table[0][numOfCities - 1]));
			} else
				bestCostField.setText("Sorry, There is no direct way.");
		});

		printPath.setOnAction(e -> {
			if (toCity >= fromCity && table[0][numOfCities - 1] != Integer.MAX_VALUE)
				pathField.setText(printPath(next, startingCity, next[0][numOfCities - 1]) + destination);
			else
				pathField.setText("Sorry, There is no direct way.");
		});

		printOthers.setOnAction(e -> {
			if (toCity >= fromCity && table[0][numOfCities - 1] != Integer.MAX_VALUE)
				othersArea.setText(printShortestPathsToDestination(table, city, startingCity, destination));
			else
				othersArea.setText("Sorry, There is no direct way.");
		});

		btTable.setOnAction(e -> {
			primaryStage.setScene(sceneTable);
		});

		back.setOnAction(e -> {
			primaryStage.setScene(scene);
		});

		showTable.setOnAction(e -> {
			taTable.setText("");
			if (toCity >= fromCity) {
				StringBuilder outputBuilder = new StringBuilder();
				for (int i = 1; i < toCity; i++) {
					if (next[0][i].equals("X")) {
						next[0][i] = startingCity;
					}
				}	
				outputBuilder.append("          ");

				for (int i = d; i <= toCity + d; i++) {
					outputBuilder.append(String.format("%-10s", city[i]));
				}
				outputBuilder.append("\n");

				for (int i = 0; i < numOfCities; i++) { // print the paths
					outputBuilder.append(String.format("%-10s", city[i + d]));
					for (int j = 0; j < numOfCities; j++) {
						if (table[i][j] == Integer.MAX_VALUE || j < i) {
							outputBuilder.append(String.format("%-10s", ""));
						}
						else {
							outputBuilder.append(String.format("%-10s", table[i][j]));
						}
						if (j == numOfCities - 1) {
							outputBuilder.append("\n");
							outputBuilder.append("\n");
						}
					}
				}
				taTable.setStyle("-fx-font-family: 'Courier New', monospaced;");
				taTable.appendText(outputBuilder.toString());
			}
			else
				taTable.appendText("Sorry, The road is in one way and you can't back.");
		});
		
		primaryStage.setTitle("Trips");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	//=============================================== printPath method ====================================================
	
	public static String printPath(String[][] arr, String src, String cities) {
		int size = toCity;
		String path = "";
		int diff = 0;
		while (!cities.equals(src)) {
			for (int j = size; j >= fromCity; j--) {
				if (j == fromCity)
					break;
				if (city[j + d].equals(cities)) {
					diff = size - j;
					path = city[j + d] + " -> " + path; //Start -> B -> E -> I -> J
					cities = arr[0][toCity - diff]; //I
				}
			}
		}
		return src + " -> " + path;
	}

	//================================= printShortestPathsToDestination method ============================================
	
	public static String printShortestPathsToDestination(int[][] table, String[] city, String source,String destination) {
		String result = "";
		int shortestDistance = table[0][numOfCities - 1];

		if (shortestDistance != Integer.MAX_VALUE) { // If there is a direct connection from the source to the destination
			List<Path> shortestPaths = new ArrayList<>();
			List<String> currentPath = new ArrayList<>();
			currentPath.add(city[fromCity]);
			printAllShortestPathsHelper(table, city, fromCity, toCity, currentPath, shortestPaths);
			result += "Shortest paths from " + source + " to " + destination + ":\n";
			if (shortestPaths.isEmpty()) {
				result += "No paths found.\n";
			}
			else {
				int count = 0;
				for (int i = 1; i < shortestPaths.size(); i++) {
					Path path = shortestPaths.get(i);
					result += "Path: " + path.getPath() + "\n";
					result += "Cost: " + path.getCost() + "\n";
					count++;
					if (count == 2) {
						break;
					}
				}
			}
			return result;
		}
		return "No direct connection from " + source + " to " + destination;
	}

	//==================================== printAllShortestPathsHelper method =============================================
	
	private static void printAllShortestPathsHelper(int[][] table, String[] city, int currentIndex,int destinationIndex
																, List<String> currentPath, List<Path> shortestPaths) {
		if (currentIndex == destinationIndex) { // Add the current path to the list of shortest paths
			shortestPaths.add(new Path(String.join(" -> ", currentPath), calculatePathCost(table, currentPath)));
		}
		else {
			for (int i = 0; i < city.length; i++) {
				if (table[currentIndex][i] != Integer.MAX_VALUE && i != currentIndex) {
					currentPath.add(city[i]);
					printAllShortestPathsHelper(table, city, i, destinationIndex, currentPath, shortestPaths);
					currentPath.remove(currentPath.size() - 1);
				}
			}
		}
	}

	//====================================== calculatePathCost method =====================================================
	
	private static int calculatePathCost(int[][] table, List<String> path) {
		int cost = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			int city1Index = getIndex(path.get(i));
			int city2Index = getIndex(path.get(i + 1));
			cost += table[city1Index][city2Index];
		}
		return cost;
	}

	//=================================================== getIndex method =================================================
	
	private static int getIndex(String cityName) { // Assuming the city array contains unique city names
		for (int i = 0; i < city.length; i++) {
			if (city[i].equals(cityName)) {
				return i;
			}
		}
		return -1;
	}

	//============================================== applyAlgorithm method=================================================
	
	public void algorithm(ComboBox<String> start, ComboBox<String> end, String input) {
		startingCity = start.getValue();
		destination = end.getValue();
		fromCity = start.getSelectionModel().getSelectedIndex();
		toCity = end.getSelectionModel().getSelectedIndex();
		d = fromCity; // d = 2
		numOfCities = toCity - fromCity + 1;
		table = new int[numOfCities][numOfCities];
		next = new String[numOfCities][numOfCities];

		for (int i = 0; i < numOfCities; i++) { // fill the table with initial values
			for (int j = 0; j < numOfCities; j++) {
				if (i == j)
					table[i][j] = 0;
				else
					table[i][j] = Integer.MAX_VALUE;
				next[i][j] = "X";
			}
		}
		
		String[] line = new String[numOfCities - 1];
		line = input.split("\n");
		for (int i = 0; i < numOfCities - 1; i++) {
			String[] parts = line[i + fromCity].split(", (?=\\[)");
			int city1 = i; // city1 0 --> numOfCities - 1
			for (int j = 1; j < parts.length; j++) {
				String[] cityAndCosts = parts[j].replaceAll("[\\[\\]]", "").split(",");
				String item = cityAndCosts[0].trim(); // item is the city
				int city2 = 0; // Start, [A,22,70], [B,8,80], [C,12,80] // A, [D,8,50], [E,10,70]
				for (int k = fromCity; k <= toCity; k++) { // to get the index of city2 in the comboBox
					if (start.getItems().get(k).equals(item)) {
						city2 = k - fromCity;
						break;
					}
				}
				int petrolCost = Integer.parseInt(cityAndCosts[1].trim());
				int hotelCost = Integer.parseInt(cityAndCosts[2].trim());
				table[city1][city2] = petrolCost + hotelCost;
			}
		}
		for (int i = 0; i < numOfCities; i++) {
			for (int j = 0; j < numOfCities; j++) {
				if (j < i)
					table[i][j] = Integer.MAX_VALUE;
				if (i == j)
					table[i][j] = 0;
			}
		}
		System.out.println();
		for (int i = 0; i < numOfCities; i++) { // fill the table with minimum
			for (int j = 0; j < numOfCities; j++) {
				for (int k = 0; k < numOfCities; k++) {
					if (table[j][i] == Integer.MAX_VALUE || table[i][k] == Integer.MAX_VALUE)
						continue;
					if (table[j][k] > table[j][i] + table[i][k]) {
						table[j][k] = table[j][i] + table[i][k];
						next[j][k] = city[i + d];
					}
				}
			}
		}
		for (int i = 1; i < numOfCities; i++) {
			if (next[0][i] == "X")
				next[0][i] = startingCity;
			else
				break;
		}
		fromCity = fromCity - d; // fromCity = 0
		toCity = toCity - d;
		for (int i = 0; i < next.length; i++)
			if (table[0][i] == 0 || table[0][i] == Integer.MAX_VALUE)
				next[0][i] = "X";
	}

	//=========================================== main method =============================================================
	
	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
	}
}