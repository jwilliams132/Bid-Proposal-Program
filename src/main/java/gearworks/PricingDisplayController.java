package gearworks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
	private List<Label> lineItemLabels = new ArrayList<Label>();
	private List<Label> denominations = new ArrayList<Label>();
	private TextField totalMobsTextField;
	private TextField upToMobsTextField;
	private TextField additionalMobsTextField;
	private List<TextField> lineItemTextFields = new ArrayList<TextField>();

	private App app;
	private List<Job> filteredJobList;
	private List<Button> jobButtons;
	private int currentJobIndex = 0;

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
				"county's largest city:  ".concat(cityFinder.getLargestCity(currentJob.getCounty()))));
		jobContents.getChildren().clear();
		denominations = new ArrayList<Label>();
		lineItemLabels = new ArrayList<Label>();
		addTotalMobsToPricingPage();
		if (preferences.isUpToMobsVisible())
			addUpTo_MobsToPricingPage();
		if (preferences.isAdditionalMobsVisible())
			addAdditionalMobsToPricingPage();
		addLineItemsToPricingPage();

		addSpecialLineItemButton();

		Platform.runLater(() -> {
			totalMobsTextField.requestFocus();
		});
	}

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

	/*
	 * addToPricingPage Format {
	 * 
	 * count all before lines for row index
	 * 
	 * ----create label
	 * ----jobContents.add(label, 0, 0);
	 * 
	 * if needed
	 * ----create denomination label
	 * ----jobContents.add(denomination, 1, 0);
	 * 
	 * ----create text textfield
	 * ----TextField.setOnMouseClicked(e ->
	 * ----TextField.addListener(e ->
	 * ----TextField.setText(previous value)
	 * ----jobContents.add(textfield, 2, 0);
	 * ----setKeybinds(TextField);
	 * }
	 */

	private void addTotalMobsToPricingPage() {

		totalMobsLabel = new Label("Total Mobilizations Price?  ") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("secondaryLabel");
			}
		};
		jobContents.add(totalMobsLabel, 0, 0);

		Label denomination = new Label("$") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("quaternaryLabel");
			}
		};
		denominations.add(denomination);
		jobContents.add(denomination, 1, 0);

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
		jobContents.add(totalMobsTextField, 2, 0);
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
		jobContents.add(upToMobsLabel, 0, 1);

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
		jobContents.add(upToMobsTextField, 2, 1);
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
		jobContents.add(additionalMobsLabel, 0, yIndex);

		Label denomination = new Label("$") {
			{
				setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
				getStyleClass().add("quaternaryLabel");
			}
		};
		denominations.add(denomination);
		jobContents.add(denomination, 1, yIndex);
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
		jobContents.add(additionalMobsTextField, 2, yIndex);
		setKeybinds(additionalMobsLabel);
	}

	private void addLineItemsToPricingPage() {

		int yIndexStart = 1;
		if (preferences.isUpToMobsVisible()) {
			yIndexStart++;
		}
		if (preferences.isAdditionalMobsVisible()) {
			yIndexStart++;
		}
		lineItemTextFields.clear();

		for (int lineItemIndex = 0; lineItemIndex < filteredJobList.get(currentJobIndex).getLineItems()
				.size(); lineItemIndex++) {

			final int finalIndex = lineItemIndex;
			LineItem currentLineItem = filteredJobList.get(currentJobIndex).getLineItems().get(finalIndex);
			lineItemLabels.add(finalIndex, new Label() {
				{
					setText(currentLineItem.returnLabelFormattedString());
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("primaryLabel");
				}
			});
			jobContents.add(lineItemLabels.get(finalIndex), 0, lineItemIndex + yIndexStart);

			Label denomination = new Label("$") {
				{
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("quaternaryLabel");
				}
			};
			denominations.add(denomination);
			jobContents.add(denomination, 1, lineItemIndex + yIndexStart);

			lineItemTextFields.add(new TextField() {
				{
					setText(String.format("%1.2f",
							filteredJobList.get(currentJobIndex).getLineItems().get(finalIndex).getPrice()));
				}
			});
			lineItemTextFields.get(finalIndex).setOnMouseClicked(e -> {
				lineItemTextFields.get(finalIndex).selectAll();
			});

			lineItemTextFields.get(finalIndex).focusedProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue) {
					lineItemTextFields.get(finalIndex).selectAll();
				}
			});
			jobContents.add(lineItemTextFields.get(finalIndex), 2, lineItemIndex + yIndexStart);

			Label unitsLabel = new Label(String.format(" (per %s)", currentLineItem.getUnit())) {
				{
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					getStyleClass().add("quaternaryLabel");
				}
			};
			denominations.add(unitsLabel);
			jobContents.add(unitsLabel, 3, lineItemIndex + yIndexStart);
			lineItemTextFields.forEach(x -> setKeybinds(x));
		}
	}

	// ====================================================================================================
	//
	// ====================================================================================================

	private void addSpecialLineItemButton() {

		Button addSpecialButton = new Button("+ Add Special Line Item");
		addSpecialButton.setOnAction(event -> {

			addSpecialLineToPricingPage();
			jobContents.getChildren().remove(addSpecialButton); // Remove the button when clicked
		});
		jobContents.add(addSpecialButton, 0, jobContents.getRowCount());
	}

	private void addSpecialLineToPricingPage() {

    int currentRowIndex = jobContents.getRowCount();

    TextField specialLineDescription = new TextField();
    specialLineDescription.setPromptText("Enter description...");
    jobContents.add(specialLineDescription, 0, currentRowIndex);

    Button addQuantityButton = new Button("+ Quantity");
    addQuantityButton.setOnAction(event -> {

        // Replace the button with a text field for entering quantity
        TextField quantityTextField = new TextField();
        quantityTextField.setPromptText("Enter quantity...");
        jobContents.add(quantityTextField, 1, currentRowIndex); // Use the correct row index

        // Add Ctrl+Delete key event handler for removing quantity text field
        quantityTextField.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.DELETE && e.isControlDown()) {
				
                jobContents.getChildren().remove(quantityTextField);
                jobContents.add(addQuantityButton, 1, currentRowIndex);
            }
        });

        jobContents.getChildren().remove(addQuantityButton); // Remove the original button
    });
    jobContents.add(addQuantityButton, 1, currentRowIndex); // Use the correct row index

    // Similarly, add the listener to the price text field
    Button addPriceButton = new Button("+ Price");
    addPriceButton.setOnAction(event -> {

        // Replace the button with a text field for entering price
        TextField priceTextField = new TextField();
        priceTextField.setPromptText("Enter price...");
        jobContents.add(priceTextField, 2, currentRowIndex); // Use the correct row index

        // Add Ctrl+Delete key event handler for removing price text field
        priceTextField.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.DELETE && e.isControlDown()) {

                jobContents.getChildren().remove(priceTextField);
                jobContents.add(addPriceButton, 2, currentRowIndex);
            }
        });

        jobContents.getChildren().remove(addPriceButton); // Remove the original button
    });
    jobContents.add(addPriceButton, 2, currentRowIndex); // Use the correct row index

    addSpecialLineItemButton(); // Add a new "+ Add Special Line Item" button below the current row
}
	
	// ====================================================================================================
	//
	// ====================================================================================================

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
