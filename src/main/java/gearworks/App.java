package gearworks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

	/*
	 * TODO take in all line items from whitley siddons and filter out what you
	 * want. add ability to change that filter.
	 */

	private Stage window;
	private Scene scene;

	@FXML
	private BorderPane root;
	@FXML
	private MenuBar menuBar;
	@FXML
	private StackPane displayPanel;
	@FXML
	private HBox openFilePanel,
			dataManipulationPanel,
			jobFilterPanel,
			jobSelectionPanel,
			saveFilePanel;
	@FXML
	private VBox startupDisplay;
	@FXML
	private Label openFilePath,
			currentJobLabel,
			directoryPath;
	@FXML
	private Button chooseOpenFile,
			updateBidders,
			chooseSaveFolder,
			saveExcel,
			filterJobs,
			addPricing,
			previousJob,
			nextJob;
	private Button undoFilter = new Button("Undo Filtering");

	@FXML
	private Menu viewMenu, optionsMenu;
	private CheckMenuItem upToMobsCMI, additionalMobsCMI;
	private ToggleGroup themeToggleGroup;

	private final String CSS_Styles = this.getClass().getResource("Element_Styles.css").toExternalForm();
	private final String CSS_Colors_Dark = this.getClass().getResource("Element_Colors_Dark.css").toExternalForm();
	private final String CSS_Colors_Light = this.getClass().getResource("Element_Colors_Light.css").toExternalForm();

	private enum Display {
		STARTUP, UNFILTERED, FILTERED, PRICING, UPDATE
	};

	private Display currentDisplay = Display.STARTUP;

	private enum SaveFileFormats {
		V1, V2, V3
	}

	private SaveFileFormats preferredExcelFormat = SaveFileFormats.V3;

	private FileManager fileManager = new FileManager();
	private InputFileProcessor fileProcessor = new InputFileProcessor();
	private JSON_Manager json_Manager = new JSON_Manager();
	private Preferences preferences;

	private List<Job> currentJobList, filteredJobList;
	private List<Integer> filteredIndices;
	private int currentJob = 0;

	private UnfilteredDisplayController unfilteredController;
	private FilteredDisplayController filteredController;
	private PricingDisplayController pricingController;
	// private UpdateInfoDisplayController updateController;

	private String lettingMonthDirectory;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		window = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Base_Gui.fxml"));

		try {

			root = loader.load();
			if (root == null) {

				throw new IOException("FXML root is null");
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		scene = new Scene(root, 1600, 900); // 1300, 600

		// window.initStyle(StageStyle.TRANSPARENT);
		window.setScene(scene);
		window.setTitle("Williams Road LLC Bid Form Program");

		InputStream stream = getClass().getResourceAsStream("Logo.jpg");
		if (stream != null) {
			window.getIcons().add(new Image(stream));
		} else {
			System.err.println("Failed to load the image stream");
		}
		window.setOnCloseRequest(e -> window.close());
		window.show();
	}

	@FXML
	private void initialize() { // runs during fxml loading

		themeToggleGroup = new ToggleGroup();

		// Add RadioMenuItems dynamically
		for (Themes theme : Themes.values()) {

			RadioMenuItem radioMenuItem = new RadioMenuItem(theme.toString());
			radioMenuItem.setToggleGroup(themeToggleGroup);
			radioMenuItem.setOnAction(event -> handleThemeChange(theme));
			viewMenu.getItems().add(radioMenuItem);
		}

		preferences = json_Manager.loadPreferences("src\\main\\resources\\gearworks\\config.json", Preferences.class);
		upToMobsCMI = new CheckMenuItem("Show Up to Mobs Option in Pricing") {
			{
				setOnAction(event -> {
					event.consume();
					upToMobsOptionChange();
				});
				setSelected(preferences.isUpToMobsVisible());

			}
		};
		additionalMobsCMI = new CheckMenuItem("Show Add. Mobs Option in Pricing") {
			{
				setOnAction(event -> {
					event.consume();
					additionalMobsOptionChange();
				});
				setSelected(preferences.isAdditionalMobsVisible());
			}
		};
		optionsMenu.getItems().add(upToMobsCMI);
		optionsMenu.getItems().add(additionalMobsCMI);
		optionsMenu.getItems().add(getTextFieldMenuItem("Drop Dead Price:  ", "DROPDEAD"));
		optionsMenu.getItems().add(getTextFieldMenuItem("Standby Price:  ", "STANDBY"));

		root.getStylesheets().add(CSS_Styles);
		if (preferences.getTheme() == Themes.DARK)
			root.getStylesheets().add(CSS_Colors_Dark);
		if (preferences.getTheme() == Themes.LIGHT)
			root.getStylesheets().add(CSS_Colors_Light);

		// toggleStartingTheme(preferences.getTheme());
		loadDisplayFXML("StartupDisplay.fxml", Display.STARTUP);
		undoFilter.setOnAction(e -> removeFilter());
		addColors();
		// addDragControl();

		// updateBidders.setDisable(true);
		chooseSaveFolder.setDisable(true);
		saveExcel.setDisable(true);
		filterJobs.setDisable(true);
		addPricing.setDisable(true);
		previousJob.setDisable(true);
		nextJob.setDisable(true);
	}

	private CustomMenuItem getTextFieldMenuItem(String labelText, String option) {

		CustomMenuItem customMenuItem;

		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(5);
		hBox.setPrefWidth(250);

		Label label = new Label(labelText);
		// label.setFont(Font.font("Courier New", FontWeight.NORMAL, 12));
		hBox.getChildren().add(label);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		hBox.getChildren().add(spacer);
		String textFieldContent;
		switch (option) {
			case "DROPDEAD":
				textFieldContent = String.valueOf(preferences.getDropDeadPrice());
				break;
			case "STANDBY":
				textFieldContent = String.valueOf(preferences.getStandByPrice());
				break;
			default:
				textFieldContent = "";
				break;
		}
		TextField textField = new TextField(textFieldContent);
		textField.setAlignment(Pos.CENTER_RIGHT);
		textField.setPrefWidth(50);
		textField.getStyleClass().add("optionTextField");
		hBox.getChildren().add(textField);

		Button button = new Button("\u2713");
		button.setOnAction(event -> {

			int intContent;
			try {

				intContent = Integer.valueOf(textField.getText());
			} catch (NumberFormatException e) {

				intContent = 0;
			}
			switch (option) {
				case "DROPDEAD":
					preferences.setDropDeadPrice(intContent);
					break;
				case "STANDBY":
					preferences.setStandByPrice(intContent);
					break;
				default:
					break;
			}
			json_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences,
					Preferences.class);
			if (currentDisplay == Display.PRICING) {

				pricingController.setPreferences(preferences);
			}
		});
		button.getStyleClass().add("customMenuButton");
		hBox.getChildren().add(button);

		customMenuItem = new CustomMenuItem(hBox);
		customMenuItem.setHideOnClick(false);
		return customMenuItem;
	}

	private void upToMobsOptionChange() {

		preferences.setUpToMobsVisible(upToMobsCMI.isSelected());
		json_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences, Preferences.class);
		if (currentDisplay == Display.PRICING) {

			pricingController.setPreferences(preferences);
			pricingController.updateJobDisplay();
		}
	}

	private void additionalMobsOptionChange() {

		preferences.setAdditionalMobsVisible(additionalMobsCMI.isSelected());
		json_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences, Preferences.class);
		if (currentDisplay == Display.PRICING) {

			pricingController.setPreferences(preferences);
			pricingController.updateJobDisplay();
		}
	}

	private void addColors() {

		ArrayList<Label> labels = new ArrayList<>(
				Arrays.asList(openFilePath, currentJobLabel, directoryPath));

		labels.forEach(x -> x.getStyleClass().add("quaternaryLabel"));
		ArrayList<Region> primaryRegions = new ArrayList<>(
				Arrays.asList(openFilePanel, saveFilePanel, dataManipulationPanel,
						jobFilterPanel, jobSelectionPanel));

		primaryRegions
				.forEach(x -> x.getStyleClass().add("primaryBackground"));
	}

	private void loadDisplayFXML(String fxml, Display currentDisplay) {

		Display previousDisplay = this.currentDisplay;
		this.currentDisplay = currentDisplay;
		VBox display = null;

		previousJob.setVisible(false);
		nextJob.setVisible(false);
		currentJobLabel.setVisible(false);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

			display = loader.load();
			displayPanel.getChildren().clear();
			displayPanel.getChildren().add(display);
			switch (currentDisplay) {

				case STARTUP:
					break;

				case UNFILTERED:
					if (previousDisplay == Display.PRICING)
						pricingController.setPrices();
					addPricing.setDisable(true);
					currentJob = 0;
					unfilteredController = new UnfilteredDisplayController();
					unfilteredController = loader.getController();
					unfilteredController.setJobList(currentJobList);
					unfilteredController.setFilteredIndexes(filteredIndices);
					unfilteredController.customizeAppearance();
					break;

				case FILTERED:

					filteredJobList = unfilteredController.getFilteredList();
					filteredIndices = unfilteredController.getFilteredIndexes();
					filteredController = new FilteredDisplayController();
					filteredController = loader.getController();
					filteredController.setFilteredJobList(filteredJobList);
					filteredController.customizeAppearance();
					break;

				case PRICING:
					previousJob.setVisible(true);
					nextJob.setVisible(true);
					currentJobLabel.setVisible(true);
					addPricing.setDisable(true);
					updateCurrentJobItems(currentJob);
					pricingController = new PricingDisplayController();
					pricingController = loader.getController();
					pricingController.setPreferences(preferences);
					pricingController.setJobList(filteredJobList);
					pricingController.setCurrentJobIndex(currentJob);
					pricingController.customizeAppearance();
					pricingController.setApp(this);
					break;

				case UPDATE:
					// updateController = new UpdateInfoDisplayController();
					// updateController = loader.getController();
					break;

				default:
					break;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@FXML
	private void openFile() {

		// Create a filter for .txt files
		FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");

		File inputFile = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, txtFilter);

		if (inputFile == null) {

			showWarning("Warning", "Error", "No file selected");
			return;
		}

		try {

			currentJobList = fileProcessor.parseFile(inputFile.getAbsolutePath());
		} catch (UnsupportedOperationException e) {

			return;
		}

		lettingMonthDirectory = Paths.get(inputFile.getAbsolutePath()).getParent().toString();

		openFilePath.setText("File Path: " + inputFile);
		chooseSaveFolder.setDisable(false);
		filterJobs.setDisable(false);
		undoFilter.setDisable(false);
		addPricing.setDisable(true);
		updateBidders.setDisable(false);
		previousJob.setDisable(true);
		nextJob.setDisable(true);

		// Replace the existing filterJobs button with the newButton
		jobFilterPanel.getChildren().set(0, filterJobs);
		if (filteredIndices != null)
			filteredIndices.clear(); // reset filter

		loadDisplayFXML("UnfilteredDisplay.fxml", Display.UNFILTERED);
		// changeDisplay(getUnfilteredDisplay(), Display.UNFILTERED);
	}

	@FXML
	private void updateInfo() {

		// Create a map to store multiple instances with identifiers
		Map<String, Job> complexObjects = new HashMap<>();

		filteredJobList.forEach(job -> complexObjects.put(job.getCsj(), job));

		// Create ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();

		// Specify the path to the output file
		String outputPath = "C:\\Users\\Jacob\\Desktop\\New folder (2)\\output.json";

		try {
			// Write the map of objects to the JSON file
			objectMapper.writeValue(new File(outputPath), complexObjects);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void filterJobs() {

		// Replace the existing filterJobs button with the newButton
		jobFilterPanel.getChildren().set(0, undoFilter);

		loadDisplayFXML("FilteredDisplay.fxml", Display.FILTERED);
		addPricing.setDisable(false);
	}

	@FXML
	private void removeFilter() {

		// Replace the existing filterJobs button with the newButton
		jobFilterPanel.getChildren().set(0, filterJobs);

		loadDisplayFXML("UnfilteredDisplay.fxml", Display.UNFILTERED);
	}

	@FXML
	private void switchToPricing() {

		loadDisplayFXML("PricingDisplay.fxml", Display.PRICING);
	}

	@FXML
	private void previousJob() {

		if (!pricingController.isPricingValid())
			return;
		currentJob--;
		updateCurrentJobItems(currentJob);
		pricingController.changeState(currentJob);
		pricingController.setCurrentJobIndex(currentJob);
		pricingController.updateJobDisplay();
	}

	@FXML
	private void nextJob() {

		if (!pricingController.isPricingValid())
			return;

		currentJob++;
		updateCurrentJobItems(currentJob);
		pricingController.changeState(currentJob);
		pricingController.setCurrentJobIndex(currentJob);
		pricingController.updateJobDisplay();
	}

	@FXML
	private void saveFiles() {

		if (currentDisplay == Display.PRICING && !pricingController.isPricingValid())
			return;
		pricingController.setPrices();

		lettingMonthDirectory = fileManager.chooseDirectory(lettingMonthDirectory);
		// Get the selected file
		File userFriendlyOutput = fileManager.chooseFile(
				lettingMonthDirectory + "\\Program Output (User Friendly).txt", null,
				FileManager.fileChooserOptions.SAVE, null);
		File emailList = fileManager.chooseFile(lettingMonthDirectory + "\\Email List.txt", null,
				FileManager.fileChooserOptions.SAVE, null);

		ArrayList<String> userFriendlyOutputBuffer = new ArrayList<String>();
		ArrayList<String> emailListBuffer = new ArrayList<String>();

		fileProcessor.saveFileFormat(filteredJobList, lettingMonthDirectory + "\\Program Output.txt",
				InputFileProcessor.FileFormat.V2);

		ContractorStorage storage = new ContractorStorage();
		// add all job data to fileContentBuffer
		for (Job job : filteredJobList) {

			userFriendlyOutputBuffer.addAll(job.formatUserFriendlyJobInfo());
			userFriendlyOutputBuffer.add("-".repeat(100));

			emailListBuffer.addAll(job.formatEmailList());

			job.getContractorList().forEach(contractor -> storage.addToContractList(contractor));
		}
		storage.formatContractorList();
		fileManager.saveFile(userFriendlyOutput, userFriendlyOutputBuffer);
		fileManager.saveFile(emailList, emailListBuffer);

		// // Set the prices for the current job
		// setPrices();

		// Set the file path label to show the chosen file
		directoryPath.setText("Directory Path:  " + lettingMonthDirectory);

		// Enable the save button
		saveExcel.setDisable(false);

		// Disable the updateBidders button
		updateBidders.setDisable(true);
	}

	@FXML
	private void saveExcel() {

		filteredJobList.forEach(job -> {
			job.setMinimumDayCharge(new BigDecimal(preferences.getDropDeadPrice()));
			job.setStandbyPrice(new BigDecimal(preferences.getStandByPrice()));
		});
		ExcelFormatInterface excelOutput;
		switch (preferredExcelFormat) {
			case V1:
				excelOutput = new V1ExcelFormat();
				break;

			case V2:
				excelOutput = new V2ExcelFormat();
				break;

			default:
				excelOutput = new V2ExcelFormat();
				break;
		}
		excelOutput.createExcelFile(filteredJobList, lettingMonthDirectory);
	}

	private void handleThemeChange(Themes theme) {

		preferences.setTheme(Themes.valueOf(((RadioMenuItem) themeToggleGroup.getSelectedToggle()).getText()));
		json_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences, Preferences.class);

		if (preferences.getTheme() == Themes.LIGHT) {
			root.getStylesheets().remove(CSS_Colors_Dark);
			root.getStylesheets().add(CSS_Colors_Light);
		}
		if (preferences.getTheme() == Themes.DARK) {
			root.getStylesheets().remove(CSS_Colors_Light);
			root.getStylesheets().add(CSS_Colors_Dark);
		}
		addColors();
	}

	public void updateCurrentJobItems(int currentJobIndex) {

		currentJob = currentJobIndex;

		// Update the label text as needed
		currentJobLabel.setText(String.format("(%02d/%02d)", currentJob + 1, filteredJobList.size()));
		enableIterationButtons();
	}

	private void enableIterationButtons() {

		if (filteredJobList.size() == 1) {

			previousJob.setDisable(true);
			nextJob.setDisable(true);
		}

		if (currentJob == 0) {

			previousJob.setDisable(true);
			nextJob.setDisable(false);
		}
		if (currentJob == filteredJobList.size() - 1) {

			previousJob.setDisable(false);
			nextJob.setDisable(true);
		}
		if (currentJob > 0 && currentJob < filteredJobList.size() - 1) {

			previousJob.setDisable(false);
			nextJob.setDisable(false);
		}
	}

	public void showWarning(String header, String warningMessage, String argument) {

		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(header);
		alert.setHeaderText(null);
		alert.setContentText(warningMessage + ": " + argument);
		alert.showAndWait();
	}

	@FXML
	private void closeApplication() {

		Platform.exit();
	}
}
