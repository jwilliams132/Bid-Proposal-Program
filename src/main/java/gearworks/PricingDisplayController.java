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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
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
	private List<Label> lineItemDenominations = new ArrayList<Label>();
	private TreeMap<Integer, Map<SpecialRowContent, Control>> specialRowsInUse = new TreeMap<Integer, Map<SpecialRowContent, Control>>();
	private TreeMap<ManipulationButtonTypes, Control> manipulationButtonRef = new TreeMap<ManipulationButtonTypes, Control>();
	private TextField totalMobsTextField;
	private TextField upToMobsTextField;
	private TextField additionalMobsTextField;
	private List<TextField> lineItemPriceTextFields = new ArrayList<TextField>();
	private Button addSpecialButton = new Button("+ Add Special Line Item");
	private GridPane manipulationButtons;

	private App app;
	private List<Job> filteredJobList;
	private List<Button> jobButtons;
	private int currentJobIndex = 0;
	private int hoveredRowIndex = 0;

	private final int MOBS_COLUMN = 0;
	private final int LINE_ITEM_BUTTONS_COLUMN = 0;
	private final int SPEC_NUMBER_COLUMN = 1;
	private final int DESCRIPTION_COLUMN = 2;
	private final int QUANTITY_COLUMN = 3;
	private final int UNITS_COLUMN = 4;
	private final int DENOMINATION_COLUMN = 5;
	private final int PRICE_COLUMN = 6;
	private final int PER_COLUMN = 7;

	private enum SpecialRowContent {

		// MANIPULATION_BUTTONS,
		SPEC_NUMBER_LABEL,
		DESCRIPTION_LABEL,
		DESCRIPTION_TEXTFIELD,
		QUANTITY_BUTTON,
		QUANTITY_LABEL,
		QUANTITY_TEXTFIELD,
		UNIT_LABEL,
		PRICE_BUTTON,
		PRICE_TEXTFIELD,
		DENOMINATION_LABEL
	}

	private enum ManipulationButtonTypes {

		MOVE_UP,
		MOVE_DOWN,
		EDIT,
		CONFIRM,
		COPY_DOWN,
		DELETE
	}

	public void customizeAppearance() {

		setupLegend();
		updateJobDisplay();
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

	// MARK: setupPricing
	public void updateJobDisplay() {

		TexasCityFinder cityFinder = new TexasCityFinder();

		Job currentJob = filteredJobList.get(currentJobIndex);
		jobHeader.setText(String.format("%-20s%-20s%-20s%-20s", currentJob.getCsj(),
				currentJob.getCounty(),
				currentJob.getHighway(),
				"county's largest city:  ".concat(cityFinder.getLargestCity(currentJob.getCounty()) != null
						? cityFinder.getLargestCity(currentJob.getCounty())
						: "no city found")));
		jobContents.getChildren().clear();
		addGridConstraints();

		jobContents.setOnMouseMoved(this::handleMouseMoved); // TODO hover for nav

		lineItemDenominations = new ArrayList<Label>();
		lineItemSpecNumberLabels = new ArrayList<Label>();
		lineItemDescriptionLabels = new ArrayList<Label>();
		lineItemQuantityLabels = new ArrayList<Label>();
		lineItemUnitLabels = new ArrayList<Label>();
		specialRowsInUse = new TreeMap<Integer, Map<SpecialRowContent, Control>>();

		hoveredRowIndex = -1;

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
		setAddSpecialButton(nextAvailableGridRowIndex);

		Platform.runLater(() -> {
			totalMobsTextField.requestFocus();
		});
	}

	public void addGridConstraints() {

		int zero = 80;
		int one = 90;
		int two = 400;
		int three = 140;
		int four = 50;
		int five = 5;
		int six = 180;
		int seven = 100;
		int total = zero + one + two + three + four + five + six + seven;

		jobContents.setPrefWidth(total + 50);
		jobContents.setMinWidth(total + 50);

		jobContents.setPrefHeight(650);
		jobContents.setMinHeight(650);

		ColumnConstraints col0Constraints = new ColumnConstraints();
		col0Constraints.setMinWidth(zero);
		col0Constraints.setMaxWidth(zero);
		col0Constraints.setPrefWidth(zero);
		jobContents.getColumnConstraints().add(0, col0Constraints);

		ColumnConstraints col1Constraints = new ColumnConstraints();
		col1Constraints.setMinWidth(one);
		col1Constraints.setMaxWidth(one);
		col1Constraints.setPrefWidth(one);
		jobContents.getColumnConstraints().add(1, col1Constraints);

		ColumnConstraints col2Constraints = new ColumnConstraints();
		col2Constraints.setMinWidth(two);
		col2Constraints.setMaxWidth(two);
		col2Constraints.setPrefWidth(two);
		jobContents.getColumnConstraints().add(2, col2Constraints);

		ColumnConstraints col3Constraints = new ColumnConstraints();
		col3Constraints.setMinWidth(three);
		col3Constraints.setMaxWidth(three);
		col3Constraints.setPrefWidth(three);
		jobContents.getColumnConstraints().add(3, col3Constraints);

		ColumnConstraints col4Constraints = new ColumnConstraints();
		col4Constraints.setMinWidth(four);
		col4Constraints.setMaxWidth(four);
		col4Constraints.setPrefWidth(four);
		jobContents.getColumnConstraints().add(4, col4Constraints);

		ColumnConstraints col5Constraints = new ColumnConstraints();
		col5Constraints.setMinWidth(five);
		col5Constraints.setMaxWidth(five);
		col5Constraints.setPrefWidth(five);
		jobContents.getColumnConstraints().add(5, col5Constraints);

		ColumnConstraints col6Constraints = new ColumnConstraints();
		col6Constraints.setMinWidth(six);
		col6Constraints.setMaxWidth(six);
		col6Constraints.setPrefWidth(six);
		jobContents.getColumnConstraints().add(6, col6Constraints);

		ColumnConstraints col7Constraints = new ColumnConstraints();
		col7Constraints.setMinWidth(seven);
		col7Constraints.setMaxWidth(seven);
		col7Constraints.setPrefWidth(seven);
		jobContents.getColumnConstraints().add(7, col7Constraints);
	}

	// MARK: mouse show rowButtons
	private void handleMouseMoved(MouseEvent event) { // TODO

		double mouseY = event.getY();
		// int numRows = specialRowsInUse.lastKey();
		// double rowHeight = jobContents.getHeight() / numRows;
		// int currentRow = (int) (mouseY / rowHeight);

		int currentRow = -1;

		double totalMobs = 44.5;
		double other = 49;
		double nonLIHeight = totalMobs + (preferences.isAdditionalMobsVisible() ? other : 0)
				+ (preferences.isUpToMobsVisible() ? other : 0);

		// System.out.println(specialRowsInUse);
		double maxRowHeight = nonLIHeight;
		
		for (Integer integer : specialRowsInUse.keySet()) {
			double bufferHeight = 0;
			for (Control control : specialRowsInUse.get(integer).values()) {
				bufferHeight = (control.getHeight() + 9) > bufferHeight ? (control.getHeight() + 9) : bufferHeight;
			}
			maxRowHeight += bufferHeight;
			if (mouseY < maxRowHeight) {
				currentRow = integer;
				break;
			}
		}

		// System.out.println("MouseY:  " + mouseY);

		if (currentRow == hoveredRowIndex)
			return;

		int lineItemStartRowIndex = 1
				+ (preferences.isAdditionalMobsVisible() ? 1 : 0)
				+ (preferences.isUpToMobsVisible() ? 1 : 0);
		int lineItemEndRowIndex = lineItemStartRowIndex
				+ filteredJobList.get(currentJobIndex).getLineItems().size() - 1;

		// System.out.println("LISR: " + lineItemStartRowIndex);
		// System.out.println("LIER: " + lineItemEndRowIndex);
		// System.out.println("Current Row: " + currentRow);
		if (currentRow >= lineItemStartRowIndex && currentRow <= lineItemEndRowIndex) {

			if (manipulationButtons != null)

				jobContents.getChildren().remove(manipulationButtons);

			GridPane newManipulationButtons = getLineItemManipulationButtons(currentRow);
			// if (currentRow == lineItemEndRowIndex &&
			// !jobContents.getChildren().contains(addSpecialButton))

			jobContents.add(newManipulationButtons, LINE_ITEM_BUTTONS_COLUMN, currentRow);

			manipulationButtons = newManipulationButtons;
			if (specialRowsInUse.get(currentRow).containsKey(SpecialRowContent.DESCRIPTION_TEXTFIELD)) {

				manipulationButtons.getChildren().remove(manipulationButtonRef.get(ManipulationButtonTypes.EDIT));
				manipulationButtons.add(manipulationButtonRef.get(ManipulationButtonTypes.CONFIRM), 1, 0);
			}
			hoveredRowIndex = currentRow;
			return;
		}
		if (currentRow > lineItemEndRowIndex && !jobContents.getChildren().contains(addSpecialButton)) {

			if (manipulationButtons != null)

				jobContents.getChildren().remove(manipulationButtons);

			GridPane newManipulationButtons = getLineItemManipulationButtons(currentRow);
			jobContents.add(newManipulationButtons, LINE_ITEM_BUTTONS_COLUMN, currentRow);
			manipulationButtons = newManipulationButtons;
			manipulationButtons.getChildren().remove(manipulationButtonRef.get(ManipulationButtonTypes.EDIT));
			manipulationButtons.add(manipulationButtonRef.get(ManipulationButtonTypes.CONFIRM), 1, 0);

			hoveredRowIndex = currentRow;
		}
	}

	public void changeState(int index) {

		setPrices();
		jobButtons.get(currentJobIndex).getStyleClass().remove("button-hover");
		jobButtons.get(index).getStyleClass().add("button-hover");
		currentJobIndex = index;
		app.updateCurrentJobItems(currentJobIndex);
		updateJobDisplay();
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

		totalMobsLabel = new Label("Total Mobilizations Price?  ");
		setFontAndColor(totalMobsLabel, "secondaryLabel", null);
		jobContents.add(totalMobsLabel, MOBS_COLUMN, 0, 3, 1);

		Label denomination = new Label("$");
		setFontAndColor(denomination, "quaternaryLabel", null);

		lineItemDenominations.add(denomination);
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

		upToMobsLabel = new Label("How Many Mobilizations Included?  ");
		setFontAndColor(upToMobsLabel, "secondaryLabel", null);
		jobContents.add(upToMobsLabel, MOBS_COLUMN, 1, 3, 1);

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
		additionalMobsLabel = new Label("Additional Mobilization Price?  ");
		setFontAndColor(additionalMobsLabel, "secondaryLabel", null);
		jobContents.add(additionalMobsLabel, MOBS_COLUMN, yIndex, 3, 1);

		Label denomination = new Label("$");
		setFontAndColor(denomination, "quaternaryLabel", null);
		lineItemDenominations.add(denomination);
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

		lineItemPriceTextFields.clear();

		for (int lineItemIndex = 0; lineItemIndex < filteredJobList.get(currentJobIndex).getLineItems()
				.size(); lineItemIndex++) {

			int gridRowIndex = lineItemIndex + startingIndexForLineItems;

			LineItem currentLineItem = filteredJobList.get(currentJobIndex).getLineItems().get(lineItemIndex);

			// ========================= ADD LINE ITEM SPEC NUMBER ===================
			lineItemSpecNumberLabels.add(lineItemIndex, new Label());
			setFontAndColor(lineItemSpecNumberLabels.get(lineItemIndex), "primaryLabel",
					String.format("%7s",
							currentLineItem.getSpecNumber() == null ? "" : currentLineItem.getSpecNumber()));
			jobContents.add(lineItemSpecNumberLabels.get(lineItemIndex), SPEC_NUMBER_COLUMN,
					gridRowIndex);

			// ========================= ADD LINE ITEM DESCRIPTION ===================
			lineItemDescriptionLabels.add(lineItemIndex, new Label());
			setFontAndColor(lineItemDescriptionLabels.get(lineItemIndex), "primaryLabel",
					String.format("%-39s", currentLineItem.getDescription()));
			lineItemDescriptionLabels.get(lineItemIndex).setWrapText(true);
			lineItemDescriptionLabels.get(lineItemIndex).setPrefWidth(400);
			jobContents.add(lineItemDescriptionLabels.get(lineItemIndex), DESCRIPTION_COLUMN,
					gridRowIndex);

			// ========================= ADD LINE ITEM QUANTITY ======================
			lineItemQuantityLabels.add(lineItemIndex, new Label());
			setFontAndColor(lineItemQuantityLabels.get(lineItemIndex), "primaryLabel",
					String.format("%,14.2f", currentLineItem.getQuantity()));
			jobContents.add(lineItemQuantityLabels.get(lineItemIndex), QUANTITY_COLUMN,
					gridRowIndex);

			// ========================= ADD LINE ITEM UNITS =========================
			lineItemUnitLabels.add(lineItemIndex, new Label());
			setFontAndColor(lineItemUnitLabels.get(lineItemIndex), "primaryLabel",
					String.format("%-4s", currentLineItem.getUnit() == null ? "SY" : currentLineItem.getUnit()));
			jobContents.add(lineItemUnitLabels.get(lineItemIndex), UNITS_COLUMN,
					gridRowIndex);

			// ========================= ADD DENOMINATION LABEL ======================

			Label denomination = new Label("$");
			setFontAndColor(denomination, "quaternaryLabel", null);
			lineItemDenominations.add(denomination);
			jobContents.add(denomination, DENOMINATION_COLUMN, gridRowIndex);

			// ========================= ADD PRICING INPUT ===========================
			int finalIndex = lineItemIndex;
			lineItemPriceTextFields.add(new TextField() {
				{
					setText(String.format("%1.2f",
							filteredJobList.get(currentJobIndex).getLineItems().get(finalIndex).getPrice()));
				}
			});
			addTextFieldAutoSelect(lineItemPriceTextFields.get(lineItemIndex));
			jobContents.add(lineItemPriceTextFields.get(lineItemIndex), PRICE_COLUMN,
					gridRowIndex);

			// ========================= ADD PER LABEL ===============================

			Label unitsLabel = new Label();
			setFontAndColor(unitsLabel, "quaternaryLabel",
					String.format(" (per %s)", currentLineItem.getUnit() == null ? "SY" : currentLineItem.getUnit()));
			lineItemDenominations.add(unitsLabel);
			jobContents.add(unitsLabel, PER_COLUMN, gridRowIndex);
			lineItemPriceTextFields.forEach(x -> setKeybinds(x));

			Map<SpecialRowContent, Control> lineItemContents = new TreeMap<SpecialRowContent, Control>();
			lineItemContents.put(SpecialRowContent.SPEC_NUMBER_LABEL, lineItemSpecNumberLabels.get(lineItemIndex));
			lineItemContents.put(SpecialRowContent.DESCRIPTION_LABEL, lineItemDescriptionLabels.get(lineItemIndex));
			lineItemContents.put(SpecialRowContent.QUANTITY_LABEL, lineItemQuantityLabels.get(lineItemIndex));
			lineItemContents.put(SpecialRowContent.UNIT_LABEL, lineItemUnitLabels.get(lineItemIndex));
			lineItemContents.put(SpecialRowContent.PRICE_TEXTFIELD, lineItemPriceTextFields.get(lineItemIndex));
			lineItemContents.put(SpecialRowContent.DENOMINATION_LABEL, lineItemDenominations.get(lineItemIndex));

			specialRowsInUse.put(gridRowIndex, lineItemContents);
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

	private void moveLineItem(int fromIndex, int toIndex) {

		int nonLineItemRows = 1
				+ (preferences.isAdditionalMobsVisible() ? 1 : 0)
				+ (preferences.isUpToMobsVisible() ? 1 : 0);

		if (fromIndex == toIndex ||
				toIndex <= nonLineItemRows ||
				toIndex - nonLineItemRows >= filteredJobList.get(currentJobIndex).getLineItems().size())
			return;

		Map<SpecialRowContent, Control> from = specialRowsInUse.get(fromIndex);
		Map<SpecialRowContent, Control> to = specialRowsInUse.get(toIndex);

		removeRowContents(fromIndex);
		removeRowContents(toIndex);

		specialRowsInUse.put(toIndex, from);
		specialRowsInUse.put(fromIndex, to);

		LineItem lineItem = filteredJobList.get(currentJobIndex).getLineItems().remove(fromIndex - nonLineItemRows);
		filteredJobList.get(currentJobIndex).getLineItems().add(toIndex - nonLineItemRows, lineItem);

		updateJobDisplay();
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

			editButtonAction(confirmButton, rowIndex);
		});
		confirmButton.setOnAction(event -> {

			confirmButtonAction(editButton, rowIndex);
		});
		deleteButton.setOnAction(event -> {

			deleteButtonAction(rowIndex);
		});
		copyButton.setOnAction(event -> {

			copyButtonAction(rowIndex);
		});

		GridPane.setRowSpan(deleteButton, 2);
		manipulationGrid.setHgap(2);
		manipulationGrid.add(moveUpButton, 0, 0);
		manipulationGrid.add(moveDownButton, 0, 1);
		manipulationGrid.add(editButton, 1, 0);
		manipulationGrid.add(copyButton, 1, 1);
		manipulationGrid.add(deleteButton, 2, 0, 1, 2);

		manipulationButtonRef.put(ManipulationButtonTypes.MOVE_UP, moveUpButton);
		manipulationButtonRef.put(ManipulationButtonTypes.MOVE_DOWN, moveDownButton);
		manipulationButtonRef.put(ManipulationButtonTypes.EDIT, editButton);
		manipulationButtonRef.put(ManipulationButtonTypes.CONFIRM, confirmButton);
		manipulationButtonRef.put(ManipulationButtonTypes.COPY_DOWN, copyButton);
		manipulationButtonRef.put(ManipulationButtonTypes.DELETE, deleteButton);

		return manipulationGrid;
	}

	// MARK: TODO Copy Action
	private void copyButtonAction(int rowIndex) {

		int nonLineItemRows = 1
				+ (preferences.isAdditionalMobsVisible() ? 1 : 0)
				+ (preferences.isUpToMobsVisible() ? 1 : 0);
		int lineItemListSize = filteredJobList.get(currentJobIndex).getLineItems().size();
		int lastUsedGridRowIndex = nonLineItemRows + lineItemListSize - 1;
		// copy line item to buffer
		Map<SpecialRowContent, Control> buffer = specialRowsInUse.get(rowIndex);
		for (int index = lastUsedGridRowIndex; index > rowIndex; index--) {
			moveLineItem(index, index + 1);
		}
		specialRowsInUse.put(rowIndex + 1, buffer);
		remapStoredChildren();
		LineItem referenceLI = filteredJobList.get(currentJobIndex).getLineItems()
				.get(rowIndex - nonLineItemRows);
		LineItem bufferLI = new LineItem(referenceLI.getItemNumber(), referenceLI.getDescriptionCode(), referenceLI.getDescription(),
				referenceLI.getUnit(), referenceLI.getQuantity(), referenceLI.getPrice());
		filteredJobList.get(currentJobIndex).getLineItems().add(rowIndex - nonLineItemRows + 1, bufferLI);
		updateJobDisplay();
		// add buffer to index+1 as text field
	}

	// MARK: Delete Action
	private void deleteButtonAction(int rowIndexToDelete) {

		int lineItemIndex = rowIndexToDelete - 1
				- (preferences.isAdditionalMobsVisible() ? 1 : 0)
				- (preferences.isUpToMobsVisible() ? 1 : 0);
		if (!preferences.isDeletionWarningsShown()) {

			if (lineItemIndex < filteredJobList.get(currentJobIndex).getLineItems().size())

				filteredJobList.get(currentJobIndex).getLineItems().remove(lineItemIndex);

			removeRowContents(rowIndexToDelete);
			remapStoredChildren();
			updateListAfterDeletion();
			updateJobDisplay();
			return;
		}

		if (showConfirmationAlert(rowIndexToDelete)) {

			if (lineItemIndex < filteredJobList.get(currentJobIndex).getLineItems().size())

				filteredJobList.get(currentJobIndex).getLineItems().remove(lineItemIndex);

			removeRowContents(rowIndexToDelete);
			remapStoredChildren();
			updateListAfterDeletion();
			updateJobDisplay();
		}
	}

	// MARK: Confirm Action
	private void confirmButtonAction(Button editButton, int rowIndex) {

		BigDecimal quantity = null;
		BigDecimal price = null;
		if (jobContents.getChildren()
				.contains(specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_TEXTFIELD))) {

			TextField bufferTF = (TextField) specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_TEXTFIELD);
			try {

				if (bufferTF.getText().equals(""))

					quantity = new BigDecimal(0.00);
				else

					quantity = new BigDecimal(bufferTF.getText());

			} catch (NumberFormatException e) {

				app.showWarning("Warning", "Invalid input",
						"\"" + bufferTF.getText()
								+ "\" is not a valid number for quantity. Make sure there are only numbers and up to one decimal point.");
				return;
			}
		}
		if (jobContents.getChildren()
				.contains(specialRowsInUse.get(rowIndex).get(SpecialRowContent.PRICE_TEXTFIELD))) {

			TextField bufferTF = (TextField) specialRowsInUse.get(rowIndex).get(SpecialRowContent.PRICE_TEXTFIELD);
			try {

				if (bufferTF.getText().equals(""))

					price = new BigDecimal(0.00);
				else

					price = new BigDecimal(bufferTF.getText());

			} catch (NumberFormatException e) {

				app.showWarning("Warning", "Invalid input",
						"\"" + bufferTF.getText()
								+ "\" is not a valid number for price. Make sure there are only numbers and up to one decimal point.");
				return;
			}
		}
		manipulationButtons.getChildren().remove(manipulationButtonRef.get(ManipulationButtonTypes.CONFIRM));
		manipulationButtons.add(manipulationButtonRef.get(ManipulationButtonTypes.EDIT), 1, 0);

		int lineItemStartRowIndex = 1
				+ (preferences.isAdditionalMobsVisible() ? 1 : 0)
				+ (preferences.isUpToMobsVisible() ? 1 : 0);
		int lineItemEndRowIndex = lineItemStartRowIndex
				+ filteredJobList.get(currentJobIndex).getLineItems().size() - 1;

		boolean isSpecialLineItem = rowIndex > lineItemEndRowIndex;
		LineItem lineItem = isSpecialLineItem ? new LineItem()
				: filteredJobList.get(currentJobIndex).getLineItems()
						.get(rowIndex - lineItemStartRowIndex);

		// ========================= LINE ITEM DESCRIPTION ===================

		TextField description = (TextField) specialRowsInUse.get(rowIndex).get(SpecialRowContent.DESCRIPTION_TEXTFIELD);
		// remove from grid and map
		jobContents.getChildren()
				.remove(specialRowsInUse.get(rowIndex).remove(SpecialRowContent.DESCRIPTION_TEXTFIELD));

		// add new label and style it
		Label tempDLabel = new Label();
		setFontAndColor(tempDLabel, "primaryLabel", String.format("%-39s", description.getText()));
		tempDLabel.setWrapText(true);
		tempDLabel.setPrefWidth(400);
		specialRowsInUse.get(rowIndex).put(SpecialRowContent.DESCRIPTION_LABEL, tempDLabel);

		// add label to grid
		jobContents.add(specialRowsInUse.get(rowIndex).get(SpecialRowContent.DESCRIPTION_LABEL),
				DESCRIPTION_COLUMN, rowIndex);

		// set line item description
		if (isSpecialLineItem) {

			lineItem.setDescription(description.getText());
		} else {

			filteredJobList.get(currentJobIndex).getLineItems().get(rowIndex - lineItemStartRowIndex)
					.setDescription(description.getText());
		}

		// ========================= LINE ITEM QUANTITY ======================
		if (jobContents.getChildren().contains(specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_BUTTON))) {

			jobContents.getChildren()
					.remove(specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_BUTTON));
			quantity = new BigDecimal(0.00);

		} else {

			jobContents.getChildren()
					.remove(specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_TEXTFIELD));
		}

		// add new label and style it
		specialRowsInUse.get(rowIndex).put(SpecialRowContent.QUANTITY_LABEL, new Label());
		setFontAndColor((Label) specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_LABEL),
				"primaryLabel",
				String.format("%,14.2f", quantity));

		// add label to grid
		jobContents.add(specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_LABEL),
				QUANTITY_COLUMN, rowIndex);

		// ========================= LINE ITEM PRICE =========================
		if (jobContents.getChildren().contains(specialRowsInUse.get(rowIndex).get(SpecialRowContent.PRICE_BUTTON))) {

			jobContents.getChildren().remove(specialRowsInUse.get(rowIndex).get(SpecialRowContent.PRICE_BUTTON));
			jobContents.add(specialRowsInUse.get(rowIndex).get(SpecialRowContent.PRICE_TEXTFIELD), PRICE_COLUMN,
					rowIndex);
			price = new BigDecimal(0.00);
		}
		// set line item description
		if (isSpecialLineItem) {

			lineItem.setQuantity(quantity);
			lineItem.setPrice(price);
			filteredJobList.get(currentJobIndex).getLineItems().add(lineItem);
		} else {

			filteredJobList.get(currentJobIndex).getLineItems().get(rowIndex - lineItemStartRowIndex)
					.setQuantity(quantity);
		}
		filteredJobList.get(currentJobIndex).getLineItems().get(rowIndex - lineItemStartRowIndex).setQuantity(quantity);

		if (isSpecialLineItem) {

			setAddSpecialButton(rowIndex + 1);
		}
	}

	// MARK: Edit Action
	private void editButtonAction(Button confirmButton, int rowIndex) {

		manipulationButtons.getChildren().remove(manipulationButtonRef.get(ManipulationButtonTypes.EDIT));
		manipulationButtons.add(manipulationButtonRef.get(ManipulationButtonTypes.CONFIRM), 1, 0);

		int lineItemStartRowIndex = 1
				+ (preferences.isAdditionalMobsVisible() ? 1 : 0)
				+ (preferences.isUpToMobsVisible() ? 1 : 0);
		// int lineItemEndRowIndex = lineItemStartRowIndex
		// + filteredJobList.get(currentJobIndex).getLineItems().size() - 1;

		LineItem lineItem = filteredJobList.get(currentJobIndex).getLineItems()
				.get(rowIndex - lineItemStartRowIndex);

		// ========================= LINE ITEM DESCRIPTION ===================
		jobContents.getChildren()
				.remove(specialRowsInUse.get(rowIndex).remove(SpecialRowContent.DESCRIPTION_LABEL));
		specialRowsInUse.get(rowIndex).put(SpecialRowContent.DESCRIPTION_TEXTFIELD,
				new TextField(lineItem.getDescription()) {
					{
						setText(lineItem.getDescription());
						setPrefWidth(400);
					}
				});
		jobContents.add(specialRowsInUse.get(rowIndex).get(SpecialRowContent.DESCRIPTION_TEXTFIELD),
				DESCRIPTION_COLUMN, rowIndex);

		// ========================= LINE ITEM QUANTITY ======================
		jobContents.getChildren()
				.remove(specialRowsInUse.get(rowIndex).remove(SpecialRowContent.QUANTITY_LABEL));
		specialRowsInUse.get(rowIndex).put(SpecialRowContent.QUANTITY_TEXTFIELD,
				new TextField() {
					{
						setText(
								(lineItem.getQuantity() != null ? lineItem.getQuantity().toString() : "0"));
						setPrefWidth(135);
					}
				});
		jobContents.add(specialRowsInUse.get(rowIndex).get(SpecialRowContent.QUANTITY_TEXTFIELD),
				QUANTITY_COLUMN, rowIndex);
	}

	// MARK: resetAddSpecialButton
	private void setAddSpecialButton(int rowIndexToAddTo) {

		addSpecialButton.setOnAction(event -> {

			jobContents.getChildren().remove(addSpecialButton); // Remove the button when clicked
			specialRowsInUse.put(
					rowIndexToAddTo,
					addSpecialLineToGrid(rowIndexToAddTo));
			// checkMapOccupancy(rowIndexToAddTo);
		});
		jobContents.getChildren().remove(addSpecialButton);
		jobContents.add(addSpecialButton, DESCRIPTION_COLUMN, rowIndexToAddTo);
	}

	// MARK: addSpecialLineToGrid
	private Map<SpecialRowContent, Control> addSpecialLineToGrid(int rowIndexToAddTo) {

		TextField specialLineDescription = new TextField();
		TextField priceTextField = new TextField();
		TextField quantityTextField = new TextField();
		Button addPriceButton = new Button("+ Price");
		Button addQuantityButton = new Button("+ Quantity");

		// ========================= LINE ITEM DESCRIPTION ===================
		specialLineDescription.setPromptText("Enter description...");
		jobContents.add(specialLineDescription, DESCRIPTION_COLUMN, rowIndexToAddTo);

		// ========================= LINE ITEM QUANTITY ======================
		addButtonImplementation(
				rowIndexToAddTo,
				addQuantityButton,
				"Enter Quantity",
				"Add Quantity...",
				QUANTITY_COLUMN,
				SpecialRowContent.QUANTITY_BUTTON,
				SpecialRowContent.QUANTITY_TEXTFIELD);

		addTextFieldImplementation(
				rowIndexToAddTo,
				quantityTextField,
				"Enter Quantity",
				"Add Quantity...",
				QUANTITY_COLUMN,
				SpecialRowContent.QUANTITY_TEXTFIELD,
				SpecialRowContent.QUANTITY_BUTTON);

		jobContents.add(addQuantityButton, QUANTITY_COLUMN, rowIndexToAddTo); // Use the correct row index

		// ========================= PRICING INPUT ===========================
		addButtonImplementation(
				rowIndexToAddTo,
				addPriceButton,
				"Enter Price",
				"Add Price...",
				PRICE_COLUMN,
				SpecialRowContent.PRICE_BUTTON,
				SpecialRowContent.PRICE_TEXTFIELD);

		addTextFieldImplementation(
				rowIndexToAddTo,
				priceTextField,
				"Enter Price",
				"Add Price...",
				PRICE_COLUMN,
				SpecialRowContent.PRICE_TEXTFIELD,
				SpecialRowContent.PRICE_BUTTON);

		jobContents.add(addPriceButton, PRICE_COLUMN, rowIndexToAddTo);

		Map<SpecialRowContent, Control> specialLineItemContents = new TreeMap<>();
		specialLineItemContents.put(SpecialRowContent.DESCRIPTION_TEXTFIELD, specialLineDescription);
		specialLineItemContents.put(SpecialRowContent.QUANTITY_BUTTON, addQuantityButton);
		specialLineItemContents.put(SpecialRowContent.QUANTITY_TEXTFIELD, quantityTextField);
		specialLineItemContents.put(SpecialRowContent.PRICE_BUTTON, addPriceButton);
		specialLineItemContents.put(SpecialRowContent.PRICE_TEXTFIELD, priceTextField);
		return specialLineItemContents;
	}

	// MARK: updateListAfterDeletion
	private void updateListAfterDeletion() {

		TreeMap<Integer, Map<SpecialRowContent, Control>> tempTreeMap = new TreeMap<Integer, Map<SpecialRowContent, Control>>();
		int rowCount = 1; // this is to count totalMobilizations row

		if (preferences.isAdditionalMobsVisible())
			rowCount++;
		if (preferences.isUpToMobsVisible())
			rowCount++;
		rowCount += filteredJobList.get(currentJobIndex).getLineItems().size();
		for (Map<SpecialRowContent, Control> value : specialRowsInUse.values())
			tempTreeMap.put(rowCount++, value);
		specialRowsInUse = tempTreeMap;
	}

	// MARK: remapStoredChildren
	private void remapStoredChildren() {

		specialRowsInUse.forEach((key, map) -> {

			// =DESCRIPTION_TEXTFIELD==============================================================================
			if (map.containsKey(SpecialRowContent.DESCRIPTION_TEXTFIELD)) {

				jobContents.getChildren().remove(map.get(SpecialRowContent.DESCRIPTION_TEXTFIELD));

				jobContents.add((TextField) map.get(SpecialRowContent.DESCRIPTION_TEXTFIELD),
						DESCRIPTION_COLUMN, key);
			}

			// =QUANTITY_BUTTON====================================================================================
			if (map.containsKey(SpecialRowContent.QUANTITY_BUTTON)) {

				addButtonImplementation(
						key,
						(Button) map.get(SpecialRowContent.QUANTITY_BUTTON),
						"Enter Quantity",
						"Add Quantity...",
						QUANTITY_COLUMN,
						SpecialRowContent.QUANTITY_BUTTON,
						SpecialRowContent.QUANTITY_TEXTFIELD);

				if (!jobContents.getChildren().contains((Button) map.get(SpecialRowContent.QUANTITY_BUTTON)))
					jobContents.add((Button) map.get(SpecialRowContent.QUANTITY_BUTTON),
							QUANTITY_COLUMN, key);
			}

			// =QUANTITY_TEXTFIELD=================================================================================
			if (map.containsKey(SpecialRowContent.QUANTITY_TEXTFIELD)) {

				addTextFieldImplementation(
						key,
						(TextField) map.get(SpecialRowContent.QUANTITY_TEXTFIELD),
						"Enter Quantity",
						"Add Quantity...",
						QUANTITY_COLUMN,
						SpecialRowContent.QUANTITY_BUTTON,
						SpecialRowContent.QUANTITY_TEXTFIELD);

				if (!jobContents.getChildren().contains((TextField) map.get(SpecialRowContent.QUANTITY_TEXTFIELD)))
					jobContents.add((TextField) map.get(SpecialRowContent.QUANTITY_TEXTFIELD), QUANTITY_COLUMN, key);
			}

			// =PRICE_BUTTON=======================================================================================
			if (map.containsKey(SpecialRowContent.PRICE_BUTTON)) {

				addButtonImplementation(
						key,
						(Button) map.get(SpecialRowContent.PRICE_BUTTON),
						"Enter Price",
						"Add Price...",
						PRICE_COLUMN,
						SpecialRowContent.PRICE_BUTTON,
						SpecialRowContent.PRICE_TEXTFIELD);

				if (!jobContents.getChildren().contains((Button) map.get(SpecialRowContent.PRICE_BUTTON)))
					jobContents.add((Button) map.get(SpecialRowContent.PRICE_BUTTON), PRICE_COLUMN, key);
			}

			// =PRICE_TEXTFIELD====================================================================================
			if (map.containsKey(SpecialRowContent.PRICE_TEXTFIELD)) {

				addTextFieldImplementation(
						key,
						(TextField) map.get(SpecialRowContent.PRICE_TEXTFIELD),
						"Enter Price",
						"Add Price...",
						PRICE_COLUMN,
						SpecialRowContent.PRICE_TEXTFIELD,
						SpecialRowContent.PRICE_BUTTON);
				if (!jobContents.getChildren().contains((TextField) map.get(SpecialRowContent.PRICE_TEXTFIELD)))
					jobContents.add((TextField) map.get(SpecialRowContent.PRICE_TEXTFIELD), PRICE_COLUMN, key);
			}
		});

		jobContents.getChildren().remove(addSpecialButton);

		if (!specialRowsInUse.isEmpty()) {

			setAddSpecialButton(specialRowsInUse.lastKey() + 1);
			return;
		}

		int usedRowCount = 1 + (preferences.isAdditionalMobsVisible() ? 1 : 0)
				+ (preferences.isUpToMobsVisible() ? 1 : 0)
				+ filteredJobList.get(currentJobIndex).getLineItems().size();
		setAddSpecialButton(usedRowCount);
	}

	private void removeRowContents(int rowIndexToRemove) {

		Map<SpecialRowContent, Control> rowMap = specialRowsInUse.get(rowIndexToRemove);

		boolean hasSpecNumberLabel = rowMap.containsKey(SpecialRowContent.SPEC_NUMBER_LABEL);
		boolean hasDescriptionLabel = rowMap.containsKey(SpecialRowContent.DESCRIPTION_LABEL);
		boolean hasDescriptionTextField = rowMap.containsKey(SpecialRowContent.DESCRIPTION_TEXTFIELD);
		boolean hasQuantityLabel = rowMap.containsKey(SpecialRowContent.QUANTITY_LABEL);
		boolean hasQuantityButton = rowMap.containsKey(SpecialRowContent.QUANTITY_BUTTON);
		boolean hasQuantityTextField = rowMap.containsKey(SpecialRowContent.QUANTITY_TEXTFIELD);
		boolean hasUnitLabel = rowMap.containsKey(SpecialRowContent.UNIT_LABEL);
		boolean hasPriceButton = rowMap.containsKey(SpecialRowContent.PRICE_BUTTON);
		boolean hasPriceTextField = rowMap.containsKey(SpecialRowContent.PRICE_TEXTFIELD);
		boolean hasDenominationLabel = rowMap.containsKey(SpecialRowContent.DENOMINATION_LABEL);

		if (hasSpecNumberLabel)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.SPEC_NUMBER_LABEL));

		if (hasDescriptionLabel)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.DESCRIPTION_LABEL));

		if (hasDescriptionTextField)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.DESCRIPTION_TEXTFIELD));

		if (hasQuantityButton)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.QUANTITY_BUTTON));

		if (hasQuantityLabel)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.QUANTITY_LABEL));

		if (hasQuantityTextField)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.QUANTITY_TEXTFIELD));

		if (hasUnitLabel)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.UNIT_LABEL));

		if (hasPriceButton)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.PRICE_BUTTON));

		if (hasPriceTextField)
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.PRICE_TEXTFIELD));

		if (hasDenominationLabel) {
			jobContents.getChildren().remove(rowMap.get(SpecialRowContent.DENOMINATION_LABEL));
		}
		specialRowsInUse.remove(rowIndexToRemove);
	}

	// MARK: Add B/TF Functions
	private void addButtonImplementation(int rowIndexToAddTo, Button button, String textFieldPrompt, String buttonText,
			int column, SpecialRowContent removeType, SpecialRowContent addType) {

		button.setOnAction(event -> {

			TextField textField = new TextField();
			// System.out.println("ABI, ATFI, row: " + rowIndexToAddTo + ", prompt: " +
			// buttonText + ", " + removeType + ", " + addType);
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
			// checkMapOccupancy(rowIndexToAddTo);
		});
	}

	private void addTextFieldImplementation(int rowIndexToAddTo, TextField textField,
			String textFieldPrompt, String buttonText, int column, SpecialRowContent removeType,
			SpecialRowContent addType) {

		textField.setPromptText(textFieldPrompt);
		textField.setOnKeyPressed(e -> {

			if (e.getCode() == KeyCode.DELETE && e.isControlDown()) {

				Button button = new Button(buttonText);
				// System.out.println("ATFI, ABI, row: " + rowIndexToAddTo + ", prompt: " +
				// buttonText + ", " + removeType + ", " + addType);
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
				// checkMapOccupancy(rowIndexToAddTo);
				specialRowsInUse.get(rowIndexToAddTo).remove(removeType);
				specialRowsInUse.get(rowIndexToAddTo).put(addType, button);
				// checkMapOccupancy(rowIndexToAddTo);
			}
		});
	}

	// ====================================================================================================
	// Utilities
	// ====================================================================================================

	private void setFontAndColor(Label label, String color, String textLine) {

		label.setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
		label.getStyleClass().add(color);
		if (textLine != null)
			label.setText(textLine);
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

		Map<SpecialRowContent, Control> rowMap = specialRowsInUse.get(rowIndex);

		boolean hasSpecNumberLabel = rowMap.containsKey(SpecialRowContent.SPEC_NUMBER_LABEL);
		boolean hasDescriptionLabel = rowMap.containsKey(SpecialRowContent.DESCRIPTION_LABEL);
		boolean hasDescriptionTextField = rowMap.containsKey(SpecialRowContent.DESCRIPTION_TEXTFIELD);
		boolean hasQuantityLabel = rowMap.containsKey(SpecialRowContent.QUANTITY_LABEL);
		boolean hasQuantityButton = rowMap.containsKey(SpecialRowContent.QUANTITY_BUTTON);
		boolean hasQuantityTextField = rowMap.containsKey(SpecialRowContent.QUANTITY_TEXTFIELD);
		boolean hasUnitLabel = rowMap.containsKey(SpecialRowContent.UNIT_LABEL);
		boolean hasPriceButton = rowMap.containsKey(SpecialRowContent.PRICE_BUTTON);
		boolean hasPriceTextField = rowMap.containsKey(SpecialRowContent.PRICE_TEXTFIELD);
		boolean hasDenominationLabel = rowMap.containsKey(SpecialRowContent.DENOMINATION_LABEL);

		System.out.println("-".repeat(50));
		System.out.println("Key:  " + rowIndex);
		System.out.println("Spec:            " + hasSpecNumberLabel);
		System.out.println("Description L:   " + hasDescriptionLabel);
		System.out.println("Description TF:  " + hasDescriptionTextField);
		System.out.println("Quantity L:      " + hasQuantityLabel);
		System.out.println("Quantity B:      " + hasQuantityButton);
		System.out.println("Quantity TF:     " + hasQuantityTextField);
		System.out.println("Units:           " + hasUnitLabel);
		System.out.println("Price B:         " + hasPriceButton);
		System.out.println("Price TF:        " + hasPriceTextField);
		System.out.println("Denomination L:  " + hasDenominationLabel);
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

		for (int index = 0; index < lineItemPriceTextFields.size(); index++) {
			filteredJobList
					.get(currentJobIndex)
					.getLineItems()
					.get(index)
					.setPrice(new BigDecimal(lineItemPriceTextFields.get(index).getText()));
		}
		filteredJobList.get(currentJobIndex).setMinimumDayCharge(new BigDecimal(preferences.getDropDeadPrice()));
		filteredJobList.get(currentJobIndex).setStandbyPrice(new BigDecimal(preferences.getStandByPrice()));
		lineItemPriceTextFields.clear();
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
		invalidInput = checkBigDecTextFields(lineItemPriceTextFields);
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
