package gearworks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
			chooseOpenFolder,
			updateBidders,
			chooseSaveFolder,
			createClearText,
			saveExcel,
			filterJobs,
			addPricing,
			previousJob,
			nextJob;
	private Button undoFilter = new Button("Undo Filtering") {
		{
			setPrefWidth(144);
		}
	};

	@FXML
	private Menu filteringMenu, viewMenu, optionsMenu;
	private CheckMenuItem upToMobsCMI, additionalMobsCMI, inputDirectoryUsed, deletionWarningsShown;
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

	private enum InputType {
		FILE, DIRECTORY
	}

	private InputType inputType;
	private FileManager fileManager = new FileManager();
	private JSON_Manager json_Manager = new JSON_Manager();
	private InputFileProcessor fileProcessor = new InputFileProcessor();
	private CountyManager countyManager = new CountyManager();
	private Preferences_Manager preferences_Manager = new Preferences_Manager();
	private Preferences preferences;

	private JobFilterChain filterChain = new JobFilterChain();
	private JobFilter_FileName fileNameFilter;
	private JobFilter_QuantitySum quantitySumFilter;
	private JobFilter_District districtFilter;

	private List<Job> jobListFromInput, filteredJobList, confirmedJobList;
	private List<Integer> filteredIndices;
	private Map<String, CheckBox> districtCheckBoxes = new TreeMap<String, CheckBox>();
	private Set<String> blacklistedDistricts;
	private int currentJob = 0;

	private Controller_UnfilteredDisplay unfilteredController;
	private Controller_FilteredDisplay filteredController;
	private Controller_PricingDisplay pricingController;
	// private Controller_UpdateInfoDisplay updateController;

	private String lettingMonthDirectory;
	private boolean isFilterChainEmpty = true;

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
		// window.setMaximized(true);
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

		preferences = preferences_Manager.loadPreferences("src\\main\\resources\\gearworks\\config.json",
				Preferences.class);

		addFilterMenuItems();

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
		inputDirectoryUsed = new CheckMenuItem("Use Input File Folder for Output Files") {
			{
				setOnAction(event -> {
					event.consume();
					inputDirectoryUsedChange();
				});
				setSelected(preferences.isInputDirectoryUsed());
			}
		};
		deletionWarningsShown = new CheckMenuItem("Show Warnings for Line Item Deletion") {
			{
				setOnAction(event -> {
					event.consume();
					showDeletionWarningsChange();
				});
				setSelected(preferences.isDeletionWarningsShown());
			}
		};

		optionsMenu.getItems().add(inputDirectoryUsed);
		optionsMenu.getItems().add(deletionWarningsShown);
		optionsMenu.getItems().add(upToMobsCMI);
		optionsMenu.getItems().add(additionalMobsCMI);
		optionsMenu.getItems().add(getTextFieldMenuItem("Drop Dead Price:  ", "DROPDEAD"));
		optionsMenu.getItems().add(getTextFieldMenuItem("Standby Price:  ", "STANDBY"));

		root.getStylesheets().add(CSS_Styles);
		if (preferences.getTheme() == Themes.DARK)
			root.getStylesheets().add(CSS_Colors_Dark);
		if (preferences.getTheme() == Themes.LIGHT)
			root.getStylesheets().add(CSS_Colors_Light);
		// root.getStylesheets().add(preferences.getTheme() == Themes.DARK ?
		// CSS_Colors_Dark : CSS_Colors_Light);

		loadDisplayFXML(Display.STARTUP);
		undoFilter.setOnAction(e -> removeFilter());
		addColors();

		// updateBidders.setDisable(true);
		createClearText.setDisable(true);
		chooseSaveFolder.setDisable(true);
		saveExcel.setDisable(true);
		filterJobs.setDisable(true);
		addPricing.setDisable(true);
		previousJob.setDisable(true);
		nextJob.setDisable(true);
	}

	private void addFilterMenuItems() {

		CheckMenuItem useMonthlyFilter = new CheckMenuItem("This Month's Created Filter");
		useMonthlyFilter.setOnAction(event -> {

			monthlyFilterChangeState(useMonthlyFilter.isSelected());
		});

		Menu useDistrictFilter = new Menu("Use District Filter (Districts to Blacklist ->)");

		countyManager.getDistricts()
				.forEach(districtName -> districtCheckBoxes.put(districtName, new CheckBox(districtName) {
					{
						setOnAction(event -> districtFilterChangeState());
					}
				}));
		districtCheckBoxes.forEach((key, value) -> useDistrictFilter.getItems().add(new CustomMenuItem(value) {
			{
				setHideOnClick(false);
			}
		}));
		CheckMenuItem useQuantityLimitFilter = new CheckMenuItem("Quantity Limit Filter");

		filteringMenu.getItems().addAll(useMonthlyFilter, useDistrictFilter, useQuantityLimitFilter);
	}

	private void monthlyFilterChangeState(boolean isSelected) {

		if (!isSelected) {

			fileNameFilter = null;
			filterChangeState();
			return;
		}
		String monthlyFileFilterPath = lettingMonthDirectory + File.separator + "Filter_FileName.json";
		File inputFile = new File(monthlyFileFilterPath);
		List<String> fileNames;
		try {

			fileNames = json_Manager.parseJsonFile(inputFile, String[].class);
		} catch (NullPointerException e) {

			System.err.println(monthlyFileFilterPath + " WAS NOT FOUND.  ->  " + e.getMessage());
			return;
		} catch (IOException e) {

			System.err.println("THERE WAS AN ISSUE WITH THIS FILE:  " + monthlyFileFilterPath + " BEING LOADED.  ->  "
					+ e.getMessage());
			return;
		}
		fileNameFilter = new JobFilter_FileName(fileNames);
		filterChangeState();
	}

	private void districtFilterChangeState() {

		blacklistedDistricts = new TreeSet<String>();

		districtCheckBoxes.forEach((key, value) -> {

			if (value.isSelected())
				blacklistedDistricts.add(key);
		});
		boolean isAnySelected = districtCheckBoxes.values().stream().anyMatch(checkbox -> checkbox.isSelected());
		districtFilter = isAnySelected ? new JobFilter_District(blacklistedDistricts) : null;

		filterChangeState();

	}

	private void filterChangeState() {

		filterChain = new JobFilterChain();
		if (fileNameFilter != null)

			filterChain.addFilter(fileNameFilter);

		if (quantitySumFilter != null)

			filterChain.addFilter(quantitySumFilter);

		if (districtFilter != null)

			filterChain.addFilter(districtFilter);
		isFilterChainEmpty = filterChain.getFilters().size() == 0 ? true : false;
		filteredJobList = jobListFromInput.stream()
				.filter(filterChain::apply)
				.collect(Collectors.toList());
		filteredJobList.forEach(x -> {
			System.out.println(x.getCounty() + "  " + x.getCsj());
		});
		loadDisplayFXML(currentDisplay);
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
			preferences_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences,
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
		preferences_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences,
				Preferences.class);
		if (currentDisplay == Display.PRICING) {

			pricingController.setPreferences(preferences);
			pricingController.updateJobDisplay();
		}
	}

	private void additionalMobsOptionChange() {

		preferences.setAdditionalMobsVisible(additionalMobsCMI.isSelected());
		preferences_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences,
				Preferences.class);
		if (currentDisplay == Display.PRICING) {

			pricingController.setPreferences(preferences);
			pricingController.updateJobDisplay();
		}
	}

	private void inputDirectoryUsedChange() {

		preferences.setInputDirectoryUsed(inputDirectoryUsed.isSelected());
		preferences_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences,
				Preferences.class);
	}

	private void showDeletionWarningsChange() {

		preferences.setDeletionWarningsShown(deletionWarningsShown.isSelected());
		preferences_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences,
				Preferences.class);
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

	private void loadDisplayFXML(Display currentDisplay) {

		Display previousDisplay = this.currentDisplay;
		this.currentDisplay = currentDisplay;
		VBox display = null;

		String fxml;
		switch (currentDisplay) {
			case STARTUP:
				fxml = "StartupDisplay.fxml";
				break;
			case UNFILTERED:
				fxml = "UnfilteredDisplay.fxml";
				break;
			case FILTERED:
				fxml = "FilteredDisplay.fxml";
				break;
			case PRICING:
				fxml = "PricingDisplay.fxml";
				break;
			case UPDATE:
				fxml = "UpdateDisplay.fxml";
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + currentDisplay);
		}
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
					createClearText.setDisable(true);
					chooseSaveFolder.setDisable(true);
					currentJob = 0;
					// unfilteredController = new Controller_UnfilteredDisplay();
					unfilteredController = loader.getController();
					unfilteredController.setJobList(isFilterChainEmpty ? jobListFromInput : filteredJobList);
					unfilteredController.setFilteredIndexes(filteredIndices);
					unfilteredController.customizeAppearance();
					break;

				case FILTERED:
					confirmedJobList = unfilteredController.getFilteredList();
					filteredIndices = unfilteredController.getFilteredIndexes();
					filteredController = new Controller_FilteredDisplay();
					filteredController = loader.getController();
					filteredController.setConfirmedJobList(confirmedJobList);

					// save filter as JSON
					List<String> filteredJobs = confirmedJobList.stream()
							.map(job -> String.format("%s-%s(%s).txt",
									job.getCounty().replace(", ETC", ""),
									job.getCsj(),
									job.getHighway().replace(", ETC", "")))
							.collect(Collectors.toList());
					// JobFilter_FileName fileNameFilter = new JobFilter_FileName(filteredJobs);
					json_Manager.saveToJSON(lettingMonthDirectory + "\\Filter_FileName.json",
							false, filteredJobs);
					filteredController.customizeAppearance();
					break;

				case PRICING:
					previousJob.setVisible(true);
					nextJob.setVisible(true);
					currentJobLabel.setVisible(true);
					addPricing.setDisable(true);
					chooseSaveFolder.setDisable(false);
					updateCurrentJobItems(currentJob);
					pricingController = new Controller_PricingDisplay();
					pricingController = loader.getController();
					pricingController.setPreferences(preferences);
					pricingController.setJobList(confirmedJobList);
					pricingController.setCurrentJobIndex(currentJob);
					pricingController.customizeAppearance();
					pricingController.setApp(this);
					break;

				case UPDATE:
					// updateController = new Controller_UpdateInfoDisplay();
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

		inputType = InputType.FILE;

		FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
		allFilter = null;
		File inputFile = fileManager.chooseFile(null, false, null, FileManager.fileChooserOptions.OPEN, allFilter);

		if (inputFile == null) {

			showWarning("Warning", "Error", "No file selected");
			return;
		}

		if (inputFile.getName().endsWith(".txt"))
			jobListFromInput = fileProcessor.parseFile(inputFile.getAbsolutePath());

		if (inputFile.getName().endsWith(".json")) {

			try {
				jobListFromInput = json_Manager.parseJsonFile(inputFile, Job[].class);
			} catch (Exception e) {

				e.printStackTrace();
				return;
			}
		}

		lettingMonthDirectory = Paths.get(inputFile.getAbsolutePath()).getParent().toString();

		openFilePath.setText("File Path: " + inputFile);
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

		loadDisplayFXML(Display.UNFILTERED);
		// changeDisplay(getUnfilteredDisplay(), Display.UNFILTERED);
	}

	@FXML
	private void openFolder() {

		inputType = InputType.DIRECTORY;

		List<String> fileNameFilter = null; // for when you implement .json file filter

		File jobsDirectory = new File(
				fileManager.chooseDirectory(System.getProperty("user.home") + "/Desktop/Letting/2024") + File.separator
						+ "Jobs");

		if (fileNameFilter == null) {

			// filter for files that fit the naming scheme used by Whitley Siddons
			List<File> jobFiles = List.of(jobsDirectory.listFiles()).stream()
					.filter(file -> file.getName()
							.matches("[A-Z]*-[0-9]{4}-[0-9]{2}-[0-9]{3}\\(.*\\)\\.txt"))
					.collect(Collectors.toList());

			FileFormat_TxDot_Single parser = new FileFormat_TxDot_Single();
			jobListFromInput = parser.jobsFromFormat(jobFiles);
		}

		lettingMonthDirectory = Paths.get(jobsDirectory.getAbsolutePath()).getParent().toString();

		openFilePath.setText("File Path: " + jobsDirectory);
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

		loadDisplayFXML(Display.UNFILTERED);
	}

	@FXML
	private void updateInfo() {

		// File file = new File("C:\\Users\\Jacob\\Desktop\\Letting\\Line Item
		// Codes.csv");
		// JSON_Manager manager = new JSON_Manager();
		// manager.csvToJSON(file, TxDotLineItem.class);
	}

	@FXML
	private void filterJobs() {

		// Replace the existing filterJobs button with the newButton
		jobFilterPanel.getChildren().set(0, undoFilter);

		loadDisplayFXML(Display.FILTERED);
		addPricing.setDisable(false);
		createClearText.setDisable(false);
	}

	@FXML
	private void removeFilter() {

		// Replace the existing filterJobs button with the newButton
		jobFilterPanel.getChildren().set(0, filterJobs);

		loadDisplayFXML(Display.UNFILTERED);
	}

	@FXML
	private void switchToPricing() {

		loadDisplayFXML(Display.PRICING);
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
	private void createClearText() {

		if (!preferences.isInputDirectoryUsed())
			lettingMonthDirectory = fileManager.chooseDirectory(lettingMonthDirectory);
		fileProcessor.saveFileFormat(confirmedJobList, lettingMonthDirectory + "\\Program Output (User Friendly).txt",
				InputFileProcessor.FileFormat.CLEAR_TEXT);
	}

	@FXML
	private void saveFiles() {

		if (currentDisplay == Display.PRICING && !pricingController.isPricingValid())
			return;
		pricingController.setPrices();

		if (!preferences.isInputDirectoryUsed())
			lettingMonthDirectory = fileManager.chooseDirectory(lettingMonthDirectory);

		fileProcessor.saveFileFormat(confirmedJobList, lettingMonthDirectory + "\\Email List.txt",
				InputFileProcessor.FileFormat.EMAIL);
		// fileProcessor.saveFileFormat(filteredJobList, lettingMonthDirectory + "\\V2
		// Output.txt",
		// InputFileProcessor.FileFormat.V2);
		// fileProcessor.saveFileFormat(filteredJobList, lettingMonthDirectory + "\\V3
		// Output.txt",
		// InputFileProcessor.FileFormat.V3);

		json_Manager.saveToJSON(lettingMonthDirectory + "\\Job Data.json", true, confirmedJobList);

		ContractorStorage storage = new ContractorStorage();
		for (Job job : confirmedJobList)
			job.getContractorList().forEach(contractor -> storage.addToContractList(contractor));
		storage.formatContractorList();

		// Set the file path label to show the chosen file
		directoryPath.setText("Directory Path:  " + lettingMonthDirectory);

		// Enable the save button
		saveExcel.setDisable(false);

		// Disable the updateBidders button
		updateBidders.setDisable(true);
	}

	@FXML
	private void saveExcel() {

		confirmedJobList.forEach(job -> {
			job.setMinimumDayCharge(new BigDecimal(preferences.getDropDeadPrice()));
			job.setStandbyPrice(new BigDecimal(preferences.getStandByPrice()));
		});
		ExcelFormatInterface excelOutput;
		switch (preferredExcelFormat) {
			case V1:
				excelOutput = new ExcelFormat_V1();
				break;

			case V2:
				excelOutput = new ExcelFormat_V2();
				break;
			case V3:
				excelOutput = new ExcelFormat_V3();
				break;
			default:
				excelOutput = new ExcelFormat_V2();
				break;
		}
		excelOutput.createExcelFile(confirmedJobList, lettingMonthDirectory);
	}

	private void handleThemeChange(Themes theme) {

		preferences.setTheme(Themes.valueOf(((RadioMenuItem) themeToggleGroup.getSelectedToggle()).getText()));
		preferences_Manager.savePreferences("src\\main\\resources\\gearworks\\config.json", preferences,
				Preferences.class);

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
		currentJobLabel.setText(String.format("(%02d/%02d)", currentJob + 1, confirmedJobList.size()));
		enableIterationButtons();
	}

	private void enableIterationButtons() {

		if (confirmedJobList.size() == 1) {

			previousJob.setDisable(true);
			nextJob.setDisable(true);
		}

		if (currentJob == 0) {

			previousJob.setDisable(true);
			nextJob.setDisable(false);
		}
		if (currentJob == confirmedJobList.size() - 1) {

			previousJob.setDisable(false);
			nextJob.setDisable(true);
		}
		if (currentJob > 0 && currentJob < confirmedJobList.size() - 1) {

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

	public Button getPreviousJobButton() {

		return previousJob;
	}

	public Button getNextJobButton() {

		return nextJob;
	}
}
