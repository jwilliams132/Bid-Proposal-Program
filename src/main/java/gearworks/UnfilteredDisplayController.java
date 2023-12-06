package gearworks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UnfilteredDisplayController {

	private List<Job> jobList;
	private List<CheckBox> jobCheckBoxes;
	private List<Integer> filteredIndices;
	private List<Label> jobTitles = new ArrayList<Label>();
	private List<Label> lineItemLabels = new ArrayList<Label>();
	private List<Label> selectionLabels = new ArrayList<Label>();

	@FXML
	private VBox unfilteredDisplay, selectedList;

	@FXML
	private GridPane contentRegion;

	@FXML
	private Label selectedCount, header;

	@FXML
	private ScrollPane scrollPane, selectedListScrollPane;

	@FXML
	private CheckBox checkAll;

	public void customizeAppearance() {

		editContentRegion();
		updateSelectedList();
	}

	private void editContentRegion() {

		header.setText(String.format("%-20s%-20s%-20s%16s", "CSJ", "County", "Highway",
				"Total Quantities"));

		jobCheckBoxes = new ArrayList<CheckBox>();

		int lineItemCount = 0;

		for (int index = 0; index < jobList.size(); index++) {
			int finalInt = index;

			Job currentJob = jobList.get(index);

			jobCheckBoxes.add(new CheckBox(String.format("  %2d:", index + 1)) {
				{
					getStyleClass().add("checkbox");
					getStyleClass().add("tertiaryLabel");
					setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
					setOnAction(e -> changeState());
				}
			});

			int yIndex = index + lineItemCount;

			contentRegion.add(jobCheckBoxes.get(index), 0, yIndex);
			jobTitles.add(finalInt, new Label(String.format("%-20s%-20s%-20s     %,11.2f",
					currentJob.getCsj(),
					currentJob.getCounty(),
					currentJob.getHighway(),
					currentJob.getSumOfQuantities())) {
				{
					setFont(Font.font("Courier New", FontWeight.BOLD, 16));
					getStyleClass().add("secondaryLabel");
					setStyle("-fx-padding: 1 0 0 0; ");
				}
			});
			contentRegion.add(new VBox() {
				{
					setFillWidth(true);
					getChildren().add(jobTitles.get(finalInt));
				}
			}, 1, yIndex);

			for (LineItem lineItem : currentJob.getLineItems()) {

				lineItemCount++;
				yIndex = index + lineItemCount;
				lineItemLabels.add(lineItemCount - 1, new Label(
						String.format("  %-40s     %,10.2f%19s", lineItem.getDescription(), lineItem.getQuantity(),
								"")) {
					{
						setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
						getStyleClass().add("primaryLabel");
					}
				});
				contentRegion.add(lineItemLabels.get(lineItemCount - 1), 1, yIndex);
			}
		}
		selectCheckboxes();
	}

	@FXML
	private void allChangeState() {

		boolean isSelected = checkAll.isSelected();
		jobCheckBoxes.forEach(checkBox -> checkBox.setSelected(isSelected));
		updateSelectedList();
	}

	private void changeState() {

		updateSelectedList();
		if (checkAll.isSelected())
			checkAll.setSelected(false);
	}

	private void updateSelectedList() {

		StringBuilder labelString;
		Job bufferJob;
		String bufferCounty, bufferCsj;
		Integer count = 0;

		selectedList.getChildren().clear(); // reset
		selectedList.getChildren().add(selectedCount);
		for (int index = 0; index < jobCheckBoxes.size(); index++) {

			if (jobCheckBoxes.get(index).isSelected()) {

				count++;
				bufferJob = jobList.get(index);
				bufferCounty = bufferJob.getCounty();
				bufferCsj = bufferJob.getCsj();

				labelString = new StringBuilder();
				labelString.append(bufferCounty.substring(0, 3));
				labelString.append(" ");
				labelString.append(bufferCsj.substring(bufferCsj.length() - 3));
				selectionLabels.add(count - 1, new Label(labelString.toString()) {
					{
						setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
						getStyleClass().add("secondaryLabel");
					}
				});
				selectedList.getChildren().add(selectionLabels.get(count - 1));
			}
		}
		selectedCount.setText(String.format("(%02d) selected", count));
	}

	public void setJobList(List<Job> jobList) {

		this.jobList = jobList;
	}

	public List<Job> getFilteredList() {

		return getFilteredIndexes().stream()
				.map(jobList::get)
				.collect(Collectors.toList());
	}

	public List<Integer> getFilteredIndexes() {

		List<Integer> selectedIndices = new ArrayList<>();
		for (int i = 0; i < jobCheckBoxes.size(); i++) {

			if (jobCheckBoxes.get(i).isSelected()) {

				selectedIndices.add(i);
			}
		}
		if (selectedIndices.size() == 0) {

			for (int i = 0; i < jobCheckBoxes.size(); i++) {

				selectedIndices.add(i);
			}
		}
		return selectedIndices;
	}

	public void setFilteredIndexes(List<Integer> filteredIndices) {

		this.filteredIndices = filteredIndices;
	}

	public void selectCheckboxes() {

		if (filteredIndices == null)
			return;
		filteredIndices.forEach(index -> jobCheckBoxes.get(index).setSelected(true));
		if (filteredIndices.size() == jobCheckBoxes.size())
			checkAll.setSelected(true);
	}
}
