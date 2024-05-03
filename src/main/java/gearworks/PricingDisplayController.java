package gearworks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class PricingDisplayController {

	private Preferences preferences;

	@FXML
	private VBox pricingDisplay, legendContents;
	@FXML
	private ScrollPane legendScrollPane, jobScrollPane;
	@FXML
	private GridPane jobContents;
	@FXML
	private Label jobHeader, currentJobLabel;
	private Label totalMobsLabel, upToMobsLabel, additionalMobsLabel;

	private List<Label> lineItemSpecNumberLabels = new ArrayList<Label>();
	private List<Label> lineItemDescriptionLabels = new ArrayList<Label>();
	private List<Label> lineItemQuantityLabels = new ArrayList<Label>();
	private List<Label> lineItemUnitLabels = new ArrayList<Label>();
	private List<Label> denominations = new ArrayList<Label>();
	private TreeMap<Integer, Map<specialRowContent, Object>> specialRowsInUse = new TreeMap<Integer, Map<specialRowContent, Object>>();
	private TextField totalMobsTextField;
	private TextField upToMobsTextField;
	private TextField additionalMobsTextField;
	private List<TextField> lineItemTextFields = new ArrayList<TextField>();
	private Button addSpecialButton = new Button("+ Add Special Line Item");

	private App app;
	private List<Job> filteredJobList;
	private List<Button> jobButtons;
	private int currentJobIndex = 0;

	private final int DESCRIPTION_COLUMN = 0;
	private final int LINE_ITEM_BUTTONS_COLUMN = 0;
	private final int SPEC_NUMBER_COLUMN = 1;
	private final int LI_DESCRIPTION_COLUMN = 2;
	private final int QUANTITY_COLUMN = 3;
	private final int UNITS_COLUMN = 4;
	private final int DENOMINATION_COLUMN = 5;
	private final int PRICE_COLUMN = 6;
	private final int PER_COLUMN = 7;

	private enum specialRowContent {
		MANIPULATION_BUTTONS, DESCRIPTION_TEXTFIELD, QUANTITY_BUTTON, PRICE_BUTTON, QUANTITY_TEXTFIELD, PRICE_TEXTFIELD
	}

	public void customizeAppearance() {

		setupLegend();
		setupPricing();
		Platform.runLater(() -> {
			totalMobsTextField.requestFocus();
		});
	}

	private void setupLegend() {

		jobButtons = new ArrayList<Button>();

		for (int index = 0; index < filteredJobList.size(); index++) {

			final int finalIndex = index;
			Job job = filteredJobList.get(index);

			// create county buffer and take off possible ", ETC"
			String bufferCounty = job.getCounty();
			if (bufferCounty.length() > 5 && bufferCounty.substring(bufferCounty.length() - 5).equals(", ETC"))
				bufferCounty = bufferCounty.substring(0, bufferCounty.length() - 5);

			final String CSJ = job.getCsj().substring(8, 11);
			final String county = bufferCounty;

			jobButtons.add(new Button() {
				{
					setText(String.format("%-13s %s", county, CSJ));
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					setTextAlignment(TextAlignment.LEFT);
					setId(String.format("%d", finalIndex));
					setOnMousePressed(e -> changeState(finalIndex));
				}
			});

			legendContents.getChildren().add(jobButtons.get(index));
		}
		jobButtons.get(currentJobIndex).getStyleClass().add("button-hover");
	}

	private void setupPricing() {

		TexasCityFinder cityFinder = new TexasCityFinder();

		Job currentJob = filteredJobList.get(currentJobIndex);
		jobHeader.setText(String.format("%-20s%-20s%-20s%-20s", currentJob.getCsj(),
				currentJob.getCounty(),
				currentJob.getHighway(),
				"county's largest city:  ".concat(cityFinder.getLargestCity(currentJob.getCounty()) != null
						? cityFinder.getLargestCity(currentJob.getCounty())
						: "no city found")));
		jobContents.getChildren().clear();
		// jobContents.setOnMouseMoved(this::handleMouseMoved); // TODO

		denominations = new ArrayList<Label>();
		lineItemSpecNumberLabels = new ArrayList<Label>();
		lineItemDescriptionLabels = new ArrayList<Label>();
		lineItemQuantityLabels = new ArrayList<Label>();
		lineItemUnitLabels = new ArrayList<Label>();
		specialRowsInUse = new TreeMap<Integer, Map<specialRowContent, Object>>();

		int nextAvailableGridRowIndex = 0;

		addTotalMobsToPricingPage();
		nextAvailableGridRowIndex++;

		if (preferences.isUpToMobsVisible()) {
			addUpTo_MobsToPricingPage();
			nextAvailableGridRowIndex++;
		}

		if (preferences.isAdditionalMobsVisible()) {
			addAdditionalMobsToPricingPage();
			nextAvailableGridRowIndex++;
		}

		addLineItemsToPricingPage(nextAvailableGridRowIndex);
		nextAvailableGridRowIndex += currentJob.getLineItems().size();
		resetAddSpecialButton(nextAvailableGridRowIndex);

		Platform.runLater(() -> {
			totalMobsTextField.requestFocus();
		});
	}
	// MARK: mouse show rowButtons
	// private void handleMouseMoved(MouseEvent event) { // TODO

	// double mouseY = event.getY();
	// int numRows = jobContents.getRowCount();
	// double rowHeight = jobContents.getHeight() / numRows;
	// int currentRow = (int) (mouseY / rowHeight);
	// System.out.println("Mouse in row: " + currentRow);
	// }

	public void changeState(int index) {

		setPrices();
		jobButtons.get(currentJobIndex).getStyleClass().remove("button-hover");
		jobButtons.get(index).getStyleClass().add("button-hover");
		currentJobIndex = index;
		app.updateCurrentJobItems(currentJobIndex);
		setupPricing();
		legendAutoFollow();
	}

	private void legendAutoFollow() {

		final double SCROLL_PANE_MARGIN = 2;
		double buttonHeight, topOfCurrentButton, currentButton, visibleRegionHeight, fullRegionHeight,
				compoundedButtonHeight, compoundedSpacing, currentPositionInPixels, scrollablePixels, targetVvalue;

		buttonHeight = jobButtons.get(currentJobIndex).getHeight();
		currentButton = currentJobIndex + 1; // makes the first button No. 1

		visibleRegionHeight = legendScrollPane.getHeight() - SCROLL_PANE_MARGIN;
		fullRegionHeight = legendContents.getHeight();

		scrollablePixels = fullRegionHeight - visibleRegionHeight;
		currentPositionInPixels = legendScrollPane.getVvalue() * scrollablePixels; // points to the top most pixel

		compoundedButtonHeight = currentButton * buttonHeight;
		compoundedSpacing = currentJobIndex * legendContents.getSpacing();
		topOfCurrentButton = compoundedButtonHeight + compoundedSpacing;

		if (topOfCurrentButton > currentPositionInPixels + visibleRegionHeight) {
			/*
			 * if button is below visible area, set value to (all space above it minus the
			 * visible region)/full region to get scroll value from 0-1;
			 */
			targetVvalue = (topOfCurrentButton - visibleRegionHeight) / scrollablePixels;
			animateScroll(targetVvalue);
		}

		if (topOfCurrentButton - buttonHeight < currentPositionInPixels) {
			/*
			 * if button is above visible area, set value to (all space above it minus one
			 * button height)/full region to get scroll value from 0-1;
			 */
			targetVvalue = (topOfCurrentButton - buttonHeight) / scrollablePixels;
			animateScroll(targetVvalue);
		}
	}

	private void animateScroll(double targetVvalue) {

		KeyValue keyValue = new KeyValue(legendScrollPane.vvalueProperty(), targetVvalue, Interpolator.EASE_BOTH);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(.25), keyValue);
		Timeline timeline = new Timeline(keyFrame);
		timeline.play();
	}

	private void addTotalMobsToPricingPage() {

		totalMobsLabel = new Label("Total Mobilizations Price?  ") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("secondaryLabel");
			}
		};
		jobContents.add(totalMobsLabel, DESCRIPTION_COLUMN, 0, 3, 1);

		Label denomination = new Label("$") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("quaternaryLabel");
			}
		};
		denominations.add(denomination);
		jobContents.add(denomination, DENOMINATION_COLUMN, 0);

		totalMobsTextField = new TextField();
		totalMobsTextField.setOnMouseClicked(e -> {
			totalMobsTextField.selectAll();
		});

		totalMobsTextField.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				totalMobsTextField.selectAll();
			}
		});
		totalMobsTextField.setText(String.format("%.0f",
				filteredJobList.get(currentJobIndex).getTotalMobs()));
		jobContents.add(totalMobsTextField, PRICE_COLUMN, 0);
		totalMobsTextField.requestFocus();
		setKeybinds(totalMobsTextField);

	}

	private void addUpTo_MobsToPricingPage() {

		upToMobsLabel = new Label("How Many Mobilizations Included?  ") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("secondaryLabel");
			}
		};
		jobContents.add(upToMobsLabel, DESCRIPTION_COLUMN, 1, 3, 1);

		upToMobsTextField = new TextField();
		upToMobsTextField.setOnMouseClicked(e -> {
			upToMobsTextField.selectAll();
		});

		upToMobsTextField.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				upToMobsTextField.selectAll();
			}
		});
		upToMobsTextField.setText(String.format("%d",
				filteredJobList.get(currentJobIndex).getUpTo_Mobs()));
		jobContents.add(upToMobsTextField, PRICE_COLUMN, 1);

		setKeybinds(upToMobsTextField);

	}

	private void addAdditionalMobsToPricingPage() {

		int yIndex = 1;
		if (preferences.isUpToMobsVisible()) {
			yIndex++;
		}
		additionalMobsLabel = new Label("Additional Mobilization Price?  ") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("secondaryLabel");
			}
		};
		jobContents.add(additionalMobsLabel, DESCRIPTION_COLUMN, yIndex, 3, 1);

		Label denomination = new Label("$") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("quaternaryLabel");
			}
		};
		denominations.add(denomination);
		jobContents.add(denomination, DENOMINATION_COLUMN, yIndex);
		additionalMobsTextField = new TextField();
		additionalMobsTextField.setOnMouseClicked(e -> {
			additionalMobsTextField.selectAll();
		});

		additionalMobsTextField.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				additionalMobsTextField.selectAll();
			}
		});
		additionalMobsTextField.setText(String.format("%.0f",
				filteredJobList.get(currentJobIndex).getAdditionalMobs()));
		jobContents.add(additionalMobsTextField, PRICE_COLUMN, yIndex);

		setKeybinds(additionalMobsLabel);

	}

	private void addLineItemsToPricingPage(int startingIndexForLineItems) {

		lineItemTextFields.clear();

		for (int lineItemIndex = 0; lineItemIndex < filteredJobList.get(currentJobIndex).getLineItems()
				.size(); lineItemIndex++) {

			final int finalIndex = lineItemIndex;

			LineItem currentLineItem = filteredJobList.get(currentJobIndex).getLineItems().get(finalIndex);

			// ========================= ADD MANIPULATION BUTTONS ====================
			jobContents.add(getLineItemManipulationButtons(finalIndex),
					LINE_ITEM_BUTTONS_COLUMN,
					lineItemIndex + startingIndexForLineItems); // TODO make the edit button show on start

			// ========================= ADD LINE ITEM SPEC NUMBER ===================
			lineItemSpecNumberLabels.add(finalIndex, new Label() {
				{
					setText(String.format("%7s", currentLineItem.getSpecNumber()));
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("primaryLabel");
				}
			});
			jobContents.add(lineItemSpecNumberLabels.get(finalIndex), SPEC_NUMBER_COLUMN,
					lineItemIndex + startingIndexForLineItems);

			// ========================= ADD LINE ITEM DESCRIPTION ===================
			lineItemDescriptionLabels.add(finalIndex, new Label() {
				{
					setText(String.format("%-39s", currentLineItem.getDescription()));
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("primaryLabel");
					setWrapText(true);
					setPrefWidth(400);
				}
			});
			jobContents.add(lineItemDescriptionLabels.get(finalIndex), LI_DESCRIPTION_COLUMN,
					lineItemIndex + startingIndexForLineItems);
			addDragAndDropFunctionToLabel(lineItemDescriptionLabels.get(finalIndex), finalIndex);

			// ========================= ADD LINE ITEM QUANTITY ======================
			lineItemQuantityLabels.add(finalIndex, new Label() {
				{
					setText(String.format("%,14.2f", currentLineItem.getQuantity()));
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("primaryLabel");
				}
			});
			jobContents.add(lineItemQuantityLabels.get(finalIndex), QUANTITY_COLUMN,
					lineItemIndex + startingIndexForLineItems);

			// ========================= ADD LINE ITEM UNITS =========================
			lineItemUnitLabels.add(finalIndex, new Label() {
				{
					setText(String.format("%-4s", currentLineItem.getUnit()));
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("primaryLabel");
				}
			});
			jobContents.add(lineItemUnitLabels.get(finalIndex), UNITS_COLUMN,
					lineItemIndex + startingIndexForLineItems);

			// ========================= ADD DENOMINATION LABEL ======================

			Label denomination = new Label("$") {
				{
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("quaternaryLabel");
				}
			};
			denominations.add(denomination);
			jobContents.add(denomination, DENOMINATION_COLUMN, lineItemIndex + startingIndexForLineItems);

			// ========================= ADD PRICING INPUT ===========================

			lineItemTextFields.add(new TextField() {
				{
					setText(String.format("%1.2f",
							filteredJobList.get(currentJobIndex).getLineItems().get(finalIndex).getPrice()));
				}
			});
			addTextFieldAutoSelect(lineItemTextFields.get(finalIndex));
			jobContents.add(lineItemTextFields.get(finalIndex), PRICE_COLUMN,
					lineItemIndex + startingIndexForLineItems);

			// ========================= ADD PER LABEL ===============================

			Label unitsLabel = new Label(String.format(" (per %s)", currentLineItem.getUnit())) {
				{
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("quaternaryLabel");
				}
			};
			denominations.add(unitsLabel);
			jobContents.add(unitsLabel, PER_COLUMN, lineItemIndex + startingIndexForLineItems);
			lineItemTextFields.forEach(x -> setKeybinds(x));
		}

	}

	private void addTextFieldAutoSelect(TextField textField) {

		textField.setOnMouseClicked(e -> {

			textField.selectAll();
		});

		textField.focusedProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue)
				textField.selectAll();
		});
	}

	private void addDragAndDropFunctionToLabel(Label lineItemLabel, final int finalIndex) {

		// Add mouse event handlers for drag and drop
		lineItemLabel.setOnDragDetected(event -> {

			lineItemLabel.setStyle("-fx-background-color: lightblue;");
			Dragboard db = lineItemLabel.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString(Integer.toString(finalIndex));
			db.setContent(content);
			event.consume();
		});

		lineItemLabel.setOnDragOver(event -> {

			lineItemLabel.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
			if (event.getGestureSource() != lineItemLabel && event.getDragboard().hasString()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();
		});

		lineItemLabel.setOnDragDropped(event -> {

			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasString()) {
				int draggedIndex = Integer.parseInt(db.getString());
				moveLineItem(draggedIndex, finalIndex);
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});

		lineItemLabel.setOnDragExited(event -> {
			// Reset the styles
			lineItemLabel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

			// Rest of the code
		});
	}

	private void moveLineItem(int fromIndex, int toIndex) {

		if (fromIndex == toIndex)
			return;

		if (toIndex <= 0)
			return;

		if (toIndex >= filteredJobList.get(currentJobIndex).getLineItems().size())
			return;

		LineItem draggedItem = filteredJobList.get(currentJobIndex).getLineItems().remove(fromIndex);
		filteredJobList.get(currentJobIndex).getLineItems().add(toIndex, draggedItem);

		updateJobDisplay();
	}

	// MARK: resetAddSpecialButton
	private void resetAddSpecialButton(int rowIndexToAddTo) {

		addSpecialButton.setOnAction(event -> {

			jobContents.getChildren().remove(addSpecialButton); // Remove the button when clicked
			specialRowsInUse.put(
					rowIndexToAddTo,
					addSpecialLineToGrid(rowIndexToAddTo));
					checkMapOccupancy(rowIndexToAddTo);
		});
		jobContents.getChildren().remove(addSpecialButton);
		jobContents.add(addSpecialButton, LI_DESCRIPTION_COLUMN, rowIndexToAddTo);
	}

	// MARK: addSpecialLineToGrid
	private Map<specialRowContent, Object> addSpecialLineToGrid(int rowIndexToAddTo) {

		System.out.println("== addLine " + "=".repeat(89));

		// int gridIndex = jobContents.getRowCount();

		GridPane manipulationButtons = getLineItemManipulationButtons(rowIndexToAddTo);
		TextField specialLineDescription = new TextField();
		TextField priceTextField = new TextField();
		TextField quantityTextField = new TextField();
		Button addPriceButton = new Button("+ Price");
		Button addQuantityButton = new Button("+ Quantity");

		// ========================= ADD MANIPULATION BUTTONS ====================
		jobContents.add(manipulationButtons, LINE_ITEM_BUTTONS_COLUMN, rowIndexToAddTo);

		// ========================= LINE ITEM DESCRIPTION ===================
		specialLineDescription.setPromptText("Enter description...");
		jobContents.add(specialLineDescription, LI_DESCRIPTION_COLUMN, rowIndexToAddTo);

		// ========================= LINE ITEM QUANTITY ======================
		System.out.println("addLineToGrid, ABI, row:  " + rowIndexToAddTo + ", QB, TQB, TQTF");
		addButtonImplementation(
				rowIndexToAddTo,
				addQuantityButton,
				"Enter Quantity",
				"Add Quantity...",
				QUANTITY_COLUMN,
				specialRowContent.QUANTITY_BUTTON,
				specialRowContent.QUANTITY_TEXTFIELD);

		System.out.println("addLineToGrid, ATFI, row:  " + rowIndexToAddTo + ", QTF, TQTF, TQB");
		addTextFieldImplementation(
				rowIndexToAddTo,
				quantityTextField,
				"Enter Quantity",
				"Add Quantity...",
				QUANTITY_COLUMN,
				specialRowContent.QUANTITY_TEXTFIELD,
				specialRowContent.QUANTITY_BUTTON);

		jobContents.add(addQuantityButton, QUANTITY_COLUMN, rowIndexToAddTo); // Use the correct row index

		// quantityTextField.setPromptText("Enter description...");
		// jobContents.add(quantityTextField, QUANTITY_COLUMN, rowIndexToAddTo); // Use
		// the correct row index

		// ========================= PRICING INPUT ===========================
		System.out.println("addLineToGrid, ABI, row:  " + rowIndexToAddTo + ", PB, TPB, TPTF");
		addButtonImplementation(
				rowIndexToAddTo,
				addPriceButton,
				"Enter Price",
				"Add Price...",
				PRICE_COLUMN,
				specialRowContent.PRICE_BUTTON,
				specialRowContent.PRICE_TEXTFIELD);

		System.out.println("addLineToGrid, ATFI, row:  " + rowIndexToAddTo + ", PTF, TPTF, TPB");
		addTextFieldImplementation(
				rowIndexToAddTo,
				priceTextField,
				"Enter Price",
				"Add Price...",
				PRICE_COLUMN,
				specialRowContent.PRICE_TEXTFIELD,
				specialRowContent.PRICE_BUTTON);

		jobContents.add(addPriceButton, PRICE_COLUMN, rowIndexToAddTo);

		// priceTextField.setPromptText("Enter description...");
		// jobContents.add(priceTextField, PRICE_COLUMN, rowIndexToAddTo); // Use the
		// correct row index

		resetAddSpecialButton(rowIndexToAddTo + 1);

		Map<specialRowContent, Object> specialLineItemContents = new TreeMap<>();
		specialLineItemContents.put(specialRowContent.MANIPULATION_BUTTONS, manipulationButtons);
		specialLineItemContents.put(specialRowContent.DESCRIPTION_TEXTFIELD, specialLineDescription);
		specialLineItemContents.put(specialRowContent.QUANTITY_BUTTON, addQuantityButton);
		// specialLineItemContents.put(specialRowContent.QUANTITY_TEXTFIELD,
		// quantityTextField);
		specialLineItemContents.put(specialRowContent.PRICE_BUTTON, addPriceButton);
		// specialLineItemContents.put(specialRowContent.PRICE_TEXTFIELD,
		// priceTextField);

		System.out.println("== addLine end " + "=".repeat(85));
		System.out.println();
		return specialLineItemContents;
	}

	private GridPane getLineItemManipulationButtons(int rowIndex) {

		GridPane manipulationGrid = new GridPane();

		Button moveDownButton = new Button("\u2193");
		Button moveUpButton = new Button("\u2191");
		Button editButton = new Button("\u270E");
		Button deleteButton = new Button("\u2717");
		Button confirmButton = new Button("\u2713");
		Button copyButton = new Button("\uD83D\uDCCB");

		List.of(moveUpButton, moveDownButton, editButton, deleteButton, confirmButton, copyButton)
				.forEach(button -> button.getStyleClass().add("manipulationButton"));
		moveUpButton.setId("upLeft");
		moveDownButton.setId("downLeft");
		editButton.setId("upRight");
		deleteButton.setId("deleteButton");
		confirmButton.setId("upRight");
		copyButton.setId("downRight");

		moveUpButton.setOnAction(event -> {

			moveLineItem(rowIndex, rowIndex - 1);
		});
		moveDownButton.setOnAction(event -> {

			moveLineItem(rowIndex, rowIndex + 1);
		});
		editButton.setOnAction(event -> {

			editButtonAction(manipulationGrid, editButton);
		});
		confirmButton.setOnAction(event -> {

			confirmButtonAction(manipulationGrid, editButton);
		});
		deleteButton.setOnAction(event -> {

			deleteButtonAction(rowIndex);
		});
		copyButton.setOnAction(event -> {

			copyButtonAction();
		});

		GridPane.setRowSpan(deleteButton, 2);
		manipulationGrid.setHgap(2);
		manipulationGrid.add(moveUpButton, 0, 0);
		manipulationGrid.add(moveDownButton, 0, 1);
		manipulationGrid.add(confirmButton, 1, 0);
		manipulationGrid.add(copyButton, 1, 1);
		manipulationGrid.add(deleteButton, 2, 0, 1, 2);
		return manipulationGrid;
	}

	// MARK: Copy Action
	private void copyButtonAction() {
		// copy line item to buffer
		// add buffer to index+1 as text field
	}

	// MARK: Delete Action
	private void deleteButtonAction(int rowIndexToDelete) {

		if (rowIndexToDelete < 0)
			return;

		if (rowIndexToDelete < filteredJobList.get(currentJobIndex).getLineItems().size()) {

			if (!preferences.isDeletionWarningsShown()) {

				filteredJobList.get(currentJobIndex).getLineItems().remove(rowIndexToDelete);
				updateJobDisplay();
				return;
			}
			if (showConfirmationAlert(rowIndexToDelete)) {

				filteredJobList.get(currentJobIndex).getLineItems().remove(rowIndexToDelete);
				updateJobDisplay();
				return;
			}
		}
		if (rowIndexToDelete >= filteredJobList.get(currentJobIndex).getLineItems().size()) {
			if (jobContents.getChildren()
					.contains(specialRowsInUse
							.get(rowIndexToDelete)
							.get(specialRowContent.DESCRIPTION_TEXTFIELD))) {

				removeSpecialRowContents(rowIndexToDelete);
				specialRowsInUse.remove(rowIndexToDelete);
				updateListAfterDeletion();
				remapStoredChildren();
			}
		}
	}

	// MARK: Confirm Action
	private void confirmButtonAction(GridPane manipulationGrid, Button editButton) {
		// remove confirm button
		// add edit button
		manipulationGrid.add(editButton, 1, 0);
		// switch text fields to labels
	}

	// MARK: Edit Action
	private void editButtonAction(GridPane manipulationGrid, Button editButton) {
		// remove edit button
		manipulationGrid.getChildren().remove(editButton);
		// add confirm button
		// switch label with text fields
	}

	// MARK: updateListAfterDeletion
	private void updateListAfterDeletion() {

		TreeMap<Integer, Map<specialRowContent, Object>> tempTreeMap = new TreeMap<Integer, Map<specialRowContent, Object>>();
		int rowCount = 1; // this is to count totalMobilizations row

		if (preferences.isAdditionalMobsVisible())
			rowCount++;
		if (preferences.isUpToMobsVisible())
			rowCount++;
		rowCount += filteredJobList.get(currentJobIndex).getLineItems().size();
		for (Map<specialRowContent, Object> value : specialRowsInUse.values())
			tempTreeMap.put(rowCount++, value);
		specialRowsInUse = tempTreeMap;
	}

	// MARK: remapStoredChildren
	private void remapStoredChildren() {

		System.out.println("== remap " + "=".repeat(91));
		specialRowsInUse.forEach((key, map) -> {

			System.out.println(key);
			System.out.println();
			checkMapOccupancy(key);

			// =MANIPULATION_BUTTONS===============================================================================
			jobContents.getChildren().remove(map.get(specialRowContent.MANIPULATION_BUTTONS));

			GridPane manipulationButtons = getLineItemManipulationButtons(key);
			specialRowsInUse.get(key).put(specialRowContent.MANIPULATION_BUTTONS, manipulationButtons);
			jobContents.add(manipulationButtons, LINE_ITEM_BUTTONS_COLUMN,
					key);

			// =DESCRIPTION_TEXTFIELD==============================================================================
			if (map.containsKey(specialRowContent.DESCRIPTION_TEXTFIELD)) {

				jobContents.getChildren().remove(map.get(specialRowContent.DESCRIPTION_TEXTFIELD));

				jobContents.add((TextField) map.get(specialRowContent.DESCRIPTION_TEXTFIELD),
						LI_DESCRIPTION_COLUMN, key);
			}
			// =QUANTITY_BUTTON====================================================================================

			if (map.containsKey(specialRowContent.QUANTITY_BUTTON)) {

				System.out.println(jobContents.getChildren().remove(map.get(specialRowContent.QUANTITY_BUTTON)));
				System.out.println("remap, ABI, row:  " + key + ", QB, TQB, TQTF");
				addButtonImplementation(
						key,
						(Button) map.get(specialRowContent.QUANTITY_BUTTON),
						"Enter Quantity",
						"Add Quantity...",
						QUANTITY_COLUMN,
						specialRowContent.QUANTITY_BUTTON,
						specialRowContent.QUANTITY_TEXTFIELD);

				if (!jobContents.getChildren().contains((Button) map.get(specialRowContent.QUANTITY_BUTTON)))
					jobContents.add((Button) map.get(specialRowContent.QUANTITY_BUTTON),
							QUANTITY_COLUMN, key);
			}
			// =QUANTITY_TEXTFIELD=================================================================================

			if (map.containsKey(specialRowContent.QUANTITY_TEXTFIELD)) {

				System.out.println(jobContents.getChildren().remove(map.get(specialRowContent.QUANTITY_TEXTFIELD)));
				System.out.println("remap, ATFI, row:  " + key + ", QTF, TQTF, TQB");
				addTextFieldImplementation(
						key,
						(TextField) map.get(specialRowContent.QUANTITY_TEXTFIELD),
						"Enter Quantity",
						"Add Quantity...",
						QUANTITY_COLUMN,
						specialRowContent.QUANTITY_BUTTON,
						specialRowContent.QUANTITY_TEXTFIELD);

				if (!jobContents.getChildren().contains((TextField) map.get(specialRowContent.QUANTITY_TEXTFIELD)))
					jobContents.add((TextField) map.get(specialRowContent.QUANTITY_TEXTFIELD), QUANTITY_COLUMN, key);
			}
			// =PRICE_BUTTON=======================================================================================

			if (map.containsKey(specialRowContent.PRICE_BUTTON)) {

				System.out.println(jobContents.getChildren().remove(map.get(specialRowContent.PRICE_BUTTON)));
				System.out.println("remap, ABI, row:  " + key + ", PB, TPB, TPTF");
				addButtonImplementation(
						key,
						(Button) map.get(specialRowContent.PRICE_BUTTON),
						"Enter Price",
						"Add Price...",
						PRICE_COLUMN,
						specialRowContent.PRICE_BUTTON,
						specialRowContent.PRICE_TEXTFIELD);

				if (!jobContents.getChildren().contains((Button) map.get(specialRowContent.PRICE_BUTTON)))
					jobContents.add((Button) map.get(specialRowContent.PRICE_BUTTON), PRICE_COLUMN, key);
			}
			// =PRICE_TEXTFIELD====================================================================================

			if (map.containsKey(specialRowContent.PRICE_TEXTFIELD)) {

				System.out.println(jobContents.getChildren().remove(map.get(specialRowContent.PRICE_TEXTFIELD)));
				System.out.println("remap, ABI, row:  " + key + ", PTF, TPTF, TPB");
				addTextFieldImplementation(
						key,
						(TextField) map.get(specialRowContent.PRICE_TEXTFIELD),
						"Enter Price",
						"Add Price...",
						PRICE_COLUMN,
						specialRowContent.PRICE_TEXTFIELD,
						specialRowContent.PRICE_BUTTON);
				if (!jobContents.getChildren().contains((TextField) map.get(specialRowContent.PRICE_TEXTFIELD)))
					jobContents.add((TextField) map.get(specialRowContent.PRICE_TEXTFIELD), PRICE_COLUMN, key);
			}
			checkMapOccupancy(key);
		});
		jobContents.getChildren().remove(addSpecialButton);
		if (!specialRowsInUse.isEmpty()) {
			resetAddSpecialButton(specialRowsInUse.lastKey() + 1);
			System.out.println("== remap end SR>0 " + "=".repeat(82));
			System.out.println();
			return;
		}
		int usedRowCount = 1 + (preferences.isAdditionalMobsVisible() ? 1 : 0)
				+ (preferences.isUpToMobsVisible() ? 1 : 0)
				+ filteredJobList.get(currentJobIndex).getLineItems().size();
		resetAddSpecialButton(usedRowCount);
		System.out.println("== remap end SR=0 " + "=".repeat(82));
		System.out.println();
	}

	private void removeSpecialRowContents(int rowIndexToRemove) {

		Map<specialRowContent, Object> rowMap = specialRowsInUse.get(rowIndexToRemove);

		boolean hasManipulationButton = rowMap.containsKey(specialRowContent.MANIPULATION_BUTTONS);
		boolean hasDescriptionTextField = rowMap.containsKey(specialRowContent.DESCRIPTION_TEXTFIELD);
		boolean hasQuantityButton = rowMap.containsKey(specialRowContent.QUANTITY_BUTTON);
		boolean hasQuantityTextField = rowMap.containsKey(specialRowContent.QUANTITY_TEXTFIELD);
		boolean hasPriceButton = rowMap.containsKey(specialRowContent.PRICE_BUTTON);
		boolean hasPriceTextField = rowMap.containsKey(specialRowContent.PRICE_TEXTFIELD);

		System.out.println("-".repeat(50));
		System.out.println("RemoveRowContents         Row: " + rowIndexToRemove);
		System.out.println("- - - - - - - - -         Nav: " + hasManipulationButton);
		System.out.println("- - - - - - - - - Description: " + hasDescriptionTextField);
		System.out.println("- - - - - - - - -   QuantityB: " + hasQuantityButton);
		System.out.println("- - - - - - - - -  QuantityTF: " + hasQuantityTextField);
		System.out.println("- - - - - - - - -      PriceB: " + hasPriceButton);
		System.out.println("- - - - - - - - -     PriceTF: " + hasPriceTextField);
		System.out.println("-".repeat(50));
		System.out.println();

		if (hasManipulationButton)
			jobContents.getChildren().remove(rowMap.get(specialRowContent.MANIPULATION_BUTTONS));

		if (hasDescriptionTextField)
			jobContents.getChildren().remove(rowMap.get(specialRowContent.DESCRIPTION_TEXTFIELD));

		if (hasQuantityButton)
			jobContents.getChildren().remove(rowMap.get(specialRowContent.QUANTITY_BUTTON));

		if (hasQuantityTextField)
			jobContents.getChildren().remove(rowMap.get(specialRowContent.QUANTITY_TEXTFIELD));

		if (hasPriceButton)
			jobContents.getChildren().remove(rowMap.get(specialRowContent.PRICE_BUTTON));

		if (hasPriceTextField)
			jobContents.getChildren().remove(rowMap.get(specialRowContent.PRICE_TEXTFIELD));
	}

	// MARK: Add B/TF Functions
	private void addButtonImplementation(int rowIndexToAddTo, Button button, String textFieldPrompt, String buttonText,
			int column, specialRowContent removeType, specialRowContent addType) {

		button.setOnAction(event -> {

			TextField textField = new TextField();
			System.out.println("ABI, ATFI, row:  " + rowIndexToAddTo + ", prompt:  " + buttonText + ", " + removeType
					+ ", " + addType);
			addTextFieldImplementation(
					rowIndexToAddTo,
					textField,
					textFieldPrompt,
					buttonText,
					column,
					addType,
					removeType);

					jobContents.getChildren().remove(button);
			jobContents.add(textField, column, rowIndexToAddTo);
			specialRowsInUse.get(rowIndexToAddTo).remove(removeType);
			specialRowsInUse.get(rowIndexToAddTo).put(addType, textField);
			checkMapOccupancy(rowIndexToAddTo);
		});
	}

	private void addTextFieldImplementation(int rowIndexToAddTo, TextField textField,
			String textFieldPrompt, String buttonText, int column, specialRowContent removeType,
			specialRowContent addType) {

		textField.setPromptText(textFieldPrompt);
		textField.setOnKeyPressed(e -> {

			if (e.getCode() == KeyCode.DELETE && e.isControlDown()) {

				Button button = new Button(buttonText);
				System.out.println("ATFI, ABI, row:  " + rowIndexToAddTo + ", prompt:  " + buttonText + ", "
						+ removeType + ", " + addType);
				addButtonImplementation(
						rowIndexToAddTo,
						button,
						textFieldPrompt,
						buttonText,
						column,
						addType,
						removeType);
						jobContents.getChildren().remove(textField);
				jobContents.add(button,
						column,
						rowIndexToAddTo);
				checkMapOccupancy(rowIndexToAddTo);
				specialRowsInUse.get(rowIndexToAddTo).remove(removeType);
				specialRowsInUse.get(rowIndexToAddTo).put(addType, button);
				checkMapOccupancy(rowIndexToAddTo);
			}
		});
	}

	private void swapLineItemToEdit(int lineItemIndex) {

	}

	private void swapLineItemToConfirmed(int gridRowIndex) {

	}

	private boolean showConfirmationAlert(int lineItemIndex) {
		// Display warning dialog
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Warning: Removing Line Item");
		alert.setContentText("Are you sure you want to remove this line item?");

		// Add confirmation buttons
		ButtonType confirmRemoval = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelRemoval = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(confirmRemoval, cancelRemoval);

		// Show and wait for user response
		Optional<ButtonType> result = alert.showAndWait();

		// If user confirms, remove the line item
		return result.isPresent() && result.get() == confirmRemoval;
	}

	public void checkMapOccupancy(int rowIndex) {

		Map<specialRowContent, Object> rowMap = specialRowsInUse.get(rowIndex);

		boolean hasManipulationButton = rowMap.containsKey(specialRowContent.MANIPULATION_BUTTONS);
		boolean hasDescriptionTextField = rowMap.containsKey(specialRowContent.DESCRIPTION_TEXTFIELD);
		boolean hasQuantityButton = rowMap.containsKey(specialRowContent.QUANTITY_BUTTON);
		boolean hasQuantityTextField = rowMap.containsKey(specialRowContent.QUANTITY_TEXTFIELD);
		boolean hasPriceButton = rowMap.containsKey(specialRowContent.PRICE_BUTTON);
		boolean hasPriceTextField = rowMap.containsKey(specialRowContent.PRICE_TEXTFIELD);

		System.out.println("-".repeat(50));
		System.out.println("        Key:  " + rowIndex);
		System.out.println("        Nav:  " + hasManipulationButton);
		System.out.println("Description:  " + hasDescriptionTextField);
		System.out.println("  QuantityB:  " + hasQuantityButton);
		System.out.println(" QuantityTF:  " + hasQuantityTextField);
		System.out.println("     PriceB:  " + hasPriceButton);
		System.out.println("    PriceTF:  " + hasPriceTextField);
		System.out.println("-".repeat(50));
		System.out.println();
	}

	public void setPrices() {

		if (!isPricingValid())
			return;

		filteredJobList.get(currentJobIndex).setTotalMobs(new BigDecimal(totalMobsTextField.getText()));
		if (preferences.isUpToMobsVisible())
			filteredJobList.get(currentJobIndex).setUpTo_Mobs(Integer.valueOf(upToMobsTextField.getText()));
		filteredJobList.get(currentJobIndex).setAdditionalMobs(new BigDecimal(totalMobsTextField.getText()));
		if (preferences.isAdditionalMobsVisible())
			filteredJobList.get(currentJobIndex).setAdditionalMobs(new BigDecimal(additionalMobsTextField.getText()));

		for (int index = 0; index < lineItemTextFields.size(); index++) {
			filteredJobList
					.get(currentJobIndex)
					.getLineItems()
					.get(index)
					.setPrice(new BigDecimal(lineItemTextFields.get(index).getText()));
		}
		filteredJobList.get(currentJobIndex).setMinimumDayCharge(new BigDecimal(preferences.getDropDeadPrice()));
		filteredJobList.get(currentJobIndex).setStandbyPrice(new BigDecimal(preferences.getStandByPrice()));
		lineItemTextFields.clear();
	}

	public boolean isPricingValid() {

		boolean valid = true;
		String invalidInput;
		// ----totalMobsTextField----------------------------
		invalidInput = checkBigDecTextField(totalMobsTextField);

		if (invalidInput != null) {

			app.showWarning("Warning", "Invalid input",
					invalidInput + " is not a valid number for total mobilizations");
			valid = false;
		}

		// ----upToMobsTextField-----------------------------
		if (preferences.isUpToMobsVisible()) {

			invalidInput = checkIntegerTextField(upToMobsTextField);

			if (invalidInput != null) {

				app.showWarning("Warning", "Invalid input",
						invalidInput + " is not a valid number for number of mobilizations");
				valid = false;
			}
		}

		// ----additionalMobsTextField-----------------------
		if (preferences.isAdditionalMobsVisible()) {

			invalidInput = checkBigDecTextField(additionalMobsTextField);

			if (invalidInput != null) {

				app.showWarning("Warning", "Invalid input",
						invalidInput + " is not a valid number for additional mobilizations");
				valid = false;
			}
		}

		// ----lineItemTextFields----------------------------
		invalidInput = checkBigDecTextFields(lineItemTextFields);
		if (invalidInput != null) {

			app.showWarning("Warning", "Invalid input",
					String.format("\"%s\" is not a valid number for line item price", invalidInput));
			valid = false;
		}
		return valid;
	}

	private String checkBigDecTextFields(List<TextField> textFields) {

		for (TextField textField : textFields) {

			try {

				new BigDecimal(textField.getText());
			} catch (NumberFormatException | NullPointerException e) {

				return textField.getText();
			}
		}
		return null;
	}

	private String checkBigDecTextField(TextField textField) {

		try {

			new BigDecimal(textField.getText());
		} catch (NumberFormatException | NullPointerException e) {

			return textField.getText();
		}
		return null;
	}

	private String checkIntegerTextField(TextField textField) {
		try {

			Integer.parseInt(textField.getText());
		} catch (NumberFormatException | NullPointerException e) {

			return textField.getText();
		}
		return null;
	}

	public void updateJobDisplay() {

		setupPricing();
	}

	public void setKeybinds(Control control) {

		control.setOnKeyPressed(event -> {

			switch (event.getCode()) {

				case PAGE_UP:
					app.getPreviousJobButton().fire();
					break;

				case PAGE_DOWN:
					app.getNextJobButton().fire();
					break;

				default:
					break;
			}
		});
	}

	public void setJobList(List<Job> jobList) {

		this.filteredJobList = jobList;
	}

	public void setCurrentJobIndex(int currentJob) {

		this.currentJobIndex = currentJob;
	}

	public int getCurrentJobIndex() {

		return currentJobIndex;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}
}
