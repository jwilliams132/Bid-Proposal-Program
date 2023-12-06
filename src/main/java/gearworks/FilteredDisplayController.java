package gearworks;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FilteredDisplayController {
    
    @FXML
    VBox filteredDisplay, contentRegion;

    @FXML
    ScrollPane scrollPane;

    @FXML
    Label header;

    private List<Job> filteredJobList;

    private List<Label> jobTitles = new ArrayList<Label>();
    private List<Label> lineItemLabels = new ArrayList<Label>();

    public void customizeAppearance() {

        header.setText(String.format("%-20s%-20s%-20s%16s", "CSJ", "County", "Highway",
                "Total Quantities"));

        for (int index = 0; index < filteredJobList.size(); index++) {
            int finalInt = index;
            int lineItemCount = 0;

            Job currentJob = filteredJobList.get(index);
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
            contentRegion.getChildren().add(jobTitles.get(finalInt));
            for (LineItem lineItem : currentJob.getLineItems()) {
                lineItemCount++;
                lineItemLabels.add(lineItemCount - 1, new Label(
                        String.format("  %-40s     %,10.2f%19s", lineItem.getDescription(), lineItem.getQuantity(),
                                "")) {
                    {
                        setFont(Font.font("Courier New", FontWeight.BOLD, 16));
                        getStyleClass().add("primaryLabel");
                    }
                });
                contentRegion.getChildren().add(lineItemLabels.get(lineItemCount - 1));
            }
        }
    }

    public void setFilteredJobList(List<Job> jobList) {

        this.filteredJobList = jobList;
    }
}
