import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingConstants;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.GridLayout;

public class TestApp {


	private String path_to_List_of_counties_in_Texas = "BidProposalProject\\src\\main\\resources\\List_of_counties_in_Texas.csv";
	private String path_to_Cropped_WR_LLC_logo = "BidProposalProject\\src\\main\\resources\\Cropped WR LLC logo.jpg";
	private Audit audit = new Audit();
	private ParseFullDoc parseFullDoc;
	private FileManager fileManager = new FileManager();
	private TexasCityFinder cityFinder = new TexasCityFinder(path_to_List_of_counties_in_Texas);
	private String lettingMonthDirectory;

	private ArrayList<JLabel> jobLabels = new ArrayList<JLabel>();
	private ArrayList<JCheckBox> jobCheckBoxes = new ArrayList<JCheckBox>();
	private ArrayList<JTextField> lineItemPrices = new ArrayList<JTextField>();
	private int jobIndex = 0;
	boolean ifFirstJob;
	boolean ifLastJob;

	private enum displayPages {
		JOB_FILTERING, PRICING
	};

	private displayPages currentDisplay = null;

	private FileFilter txtFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
		}

		public String getDescription() {
			return "Text files (*.txt)";
		}
	};

	private FileFilter xslmFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".xlsm") || f.getName().toLowerCase().endsWith(".xlsx")
					|| f.isDirectory();
		}

		public String getDescription() {
			return "Excel Files (*.xlsx or *.xlsm)";
		}
	};

	// =====Frame===============
	private JFrame frmWilliamsRoadLlc;

	// =====Open File Panel=====
	/**/private JPanel openFilePanel;

	/* -- */private JButton chooseOpenFile;
	/* -- */private JLabel openFilePathLabel;
	/* -- */private JButton updateBidders;

	// =====Data Panel==========
	/**/private JPanel dataPanel;

	// ==========Data Scroll Pane====
	/* -- */private JScrollPane dataScrollPane;

	/* ------ */private JLabel dataHeaderLabel;
	/* ------ */private JPanel rowHeaderContainer;
	/* ---------- */private JPanel rowHeaderPanel;
	/* ------ */private JPanel viewportContainer;
	/* ---------- */private JPanel viewportPanel;
	/* -------------- */private JTextField upToMobsTextField;
	/* -------------- */private JTextField totalMobsTextField;
	/* -------------- */private JTextField additionalMobsTextField;

	// ==========Data Manipulation Panel
	/* -- */private JPanel dataManipulationPanel;
	/* ------ */private JPanel jobFilterPanel;
	/* ---------- */private JButton filterForCheckedBoxes;
	/* ---------- */private JButton addPricing;
	/* ------ */private JPanel jobSelectionPanel;
	/* ---------- */private JButton previousJob;
	/* ---------- */private JButton nextJob;

	// =====Save File Panel=====
	/**/private JPanel saveFilePanel;

	/* -- */private JButton chooseSaveDirectory;
	/* -- */private JLabel saveFilePathLabel;
	/* -- */private JButton save;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestApp window = new TestApp();
					window.frmWilliamsRoadLlc.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		audit.add("Start");
		frmWilliamsRoadLlc = new JFrame();
		frmWilliamsRoadLlc
				.setIconImage(Toolkit.getDefaultToolkit().getImage(path_to_Cropped_WR_LLC_logo));
		frmWilliamsRoadLlc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmWilliamsRoadLlc.setTitle("Williams Road LLC Bid Form Program");
		frmWilliamsRoadLlc.getContentPane().setLayout(new BorderLayout(0, 0));
		frmWilliamsRoadLlc.setSize(1000, 600);

		// ====================================================================================================
		// Main Panel (North) Open File
		// ====================================================================================================

		openFilePanel = new JPanel();
		openFilePanel.setBackground(new Color(255, 255, 255));
		openFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		openFilePanel.setLayout(new BorderLayout(10, 10));
		frmWilliamsRoadLlc.getContentPane().add(openFilePanel, BorderLayout.NORTH);

		chooseOpenFile = new JButton("Choose file to open...");
		openFilePanel.add(chooseOpenFile, BorderLayout.WEST);

		updateBidders = new JButton("Add Updated Bidders");
		updateBidders.setHorizontalAlignment(SwingConstants.LEFT);
		updateBidders.setEnabled(false);
		openFilePanel.add(updateBidders, BorderLayout.EAST);

		openFilePathLabel = new JLabel("File Path:  ");
		openFilePanel.add(openFilePathLabel);

		// ====================================================================================================
		// Main Panel (South) Save File
		// ====================================================================================================

		saveFilePanel = new JPanel();
		frmWilliamsRoadLlc.getContentPane().add(saveFilePanel, BorderLayout.SOUTH);
		saveFilePanel.setLayout(new BorderLayout(10, 10));
		saveFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		chooseSaveDirectory = new JButton("Choose Directory...");
		chooseSaveDirectory.setEnabled(false);
		saveFilePanel.add(chooseSaveDirectory, BorderLayout.WEST);

		save = new JButton("Export Excel Files");
		save.setEnabled(false);
		saveFilePanel.add(save, BorderLayout.EAST);

		saveFilePathLabel = new JLabel("Directory Path:  ");
		saveFilePanel.add(saveFilePathLabel);

		// ====================================================================================================
		// Main Panel (Center) Display Data
		// ====================================================================================================

		dataPanel = new JPanel();
		frmWilliamsRoadLlc.getContentPane().add(dataPanel, BorderLayout.CENTER);
		dataPanel.setLayout(new BorderLayout(0, 0));

		dataScrollPane = new JScrollPane();
		dataScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		dataScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		dataPanel.add(dataScrollPane, BorderLayout.CENTER);

		dataHeaderLabel = new JLabel("");
		dataHeaderLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
		dataScrollPane.setColumnHeaderView(dataHeaderLabel);

		rowHeaderPanel = new JPanel();
		dataScrollPane.setAlignmentX(0.0f);
		dataScrollPane.setAlignmentY(0.0f);

		rowHeaderPanel.setLayout(new GridLayout(10, 1, 10, 10));

		rowHeaderContainer = new JPanel();
		rowHeaderContainer.add(rowHeaderPanel);

		dataScrollPane.setRowHeaderView(rowHeaderContainer);

		viewportPanel = new JPanel();
		viewportPanel.setLayout(new GridLayout(10, 1, 10, 10));

		viewportContainer = new JPanel();
		viewportContainer.setLayout(new BorderLayout(0, 0));
		viewportContainer.add(viewportPanel, BorderLayout.WEST);

		dataScrollPane.setViewportView(viewportContainer);

		// ====================================================================================================
		// Sub Panel (MP (Center)) (South) Manipulation Buttons
		// ====================================================================================================

		dataManipulationPanel = new JPanel();
		dataPanel.add(dataManipulationPanel, BorderLayout.SOUTH);
		dataManipulationPanel.setLayout(new BorderLayout(0, 0));

		jobFilterPanel = new JPanel();
		dataManipulationPanel.add(jobFilterPanel, BorderLayout.WEST);
		jobFilterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		filterForCheckedBoxes = new JButton("Confirm Selected Jobs");
		filterForCheckedBoxes.setEnabled(false);
		jobFilterPanel.add(filterForCheckedBoxes);

		addPricing = new JButton("Add Pricing to Jobs");
		addPricing.setEnabled(false);
		jobFilterPanel.add(addPricing);

		jobSelectionPanel = new JPanel();
		jobSelectionPanel.setLayout(new FlowLayout());
		dataManipulationPanel.add(jobSelectionPanel, BorderLayout.CENTER);

		previousJob = new JButton("<< Previous Job");
		previousJob.setEnabled(false);
		jobSelectionPanel.add(previousJob);

		nextJob = new JButton("Next Job >>");
		nextJob.setEnabled(false);
		jobSelectionPanel.add(nextJob);

		audit.add("All GUI elements added.");

		// ====================================================================================================
		// Button Functions
		// ====================================================================================================

		// choose a file to open button
		chooseOpenFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("chooseOpenFile Button was pressed.");

				// The "chooseOpenFileButton" was pressed, so show the file chooser
				File inputFile = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, txtFileFilter);

				if (inputFile == null) {
					showWarning("Warning", "Error", "No file selected");
					audit.add("No file was selected");
					return;
				}

				// Update the label with the file path
				openFilePathLabel.setText("File Path:  " + inputFile);

				// Add an entry to the audit log
				audit.add("File to open was chosen.  " + openFilePathLabel.getText());

				// Create an object of the file manipulation class
				parseFullDoc = new ParseFullDoc();

				// Set the audit log and the input file for the file manipulation object
				parseFullDoc.setAudit(audit);
				parseFullDoc.setNewInputFile(inputFile);

				// Parse the data in the input file and create a list of job objects
				parseFullDoc.parseData();

				// Enable a few buttons
				chooseSaveDirectory.setEnabled(true);
				filterForCheckedBoxes.setEnabled(true);
				addPricing.setEnabled(true);
				updateBidders.setEnabled(true);

				// set header
				dataHeaderLabel.setText("Job List for " + parseFullDoc.getBidFileType());

				// Display the data
				displayData();
				currentDisplay = displayPages.JOB_FILTERING;

				// Add an entry to the audit log
				audit.add("	Function chooseOpenFile completed.");

			}
		});

		// open file button
		updateBidders.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("updateBidders Button was pressed");
				File bidderFile = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, null);

				if (bidderFile == null) {
					showWarning("Warning", "Error", "No file selected");
					audit.add("No file was selected");
					return;
				}

				parseFullDoc.updateBidders(bidderFile);
				audit.add("	Function updateBidders completed.");

			}
		});

//		 Add an action listener to the choose save file button
		chooseSaveDirectory.addActionListener(new ActionListener() {
			// When the button is pressed, perform the following actions
			public void actionPerformed(ActionEvent e) {

				// Add a log message
				audit.add("chooseSaveFile Button was pressed.");
				switch (currentDisplay) {

				case JOB_FILTERING:
					break;
				case PRICING:
					if (!checkPricingPageTextValidity())
						return;
				}

				lettingMonthDirectory = fileManager.chooseDirectory(null);
				// Get the selected file
				File formattedOutput = fileManager.chooseFile(lettingMonthDirectory + "\\Program Ouput.txt", null, FileManager.fileChooserOptions.SAVE, null);
				File userFriendlyOutput = fileManager.chooseFile(lettingMonthDirectory + "\\Program Output (User Friendly).txt", null, FileManager.fileChooserOptions.SAVE, null);
				File emailList =  fileManager.chooseFile(lettingMonthDirectory + "\\Email List.txt", null, FileManager.fileChooserOptions.SAVE, null);

//				// Set the prices for the current job
//				setPrices();
				// Set the file path label to show the chosen file
				saveFilePathLabel.setText("Directory Path:  " + lettingMonthDirectory);

				// Enable the save button
				save.setEnabled(true);

				// Add a log message with the chosen file's path
				audit.add("File to save to was chosen. Directory Path:  " + lettingMonthDirectory);

				parseFullDoc.exportDataFiles(formattedOutput, userFriendlyOutput, emailList);

				// Disable the updateBidders button
				updateBidders.setEnabled(false);

				// Add a log message
				audit.add("	Function chooseSaveFile completed.");
			}
		});



		// save button
		save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("save Button was pressed.");

				File excelInputFile = fileManager.chooseFile("BidProposalProject\\src\\main\\resources\\Test Template.xlsm",
						null, FileManager.fileChooserOptions.OPEN, xslmFileFilter);

				int startingEstimateNo = 1600;
				ExcelManager excelManager;

				for (int jobIndex = 0; jobIndex < parseFullDoc.getJobList().size(); jobIndex++) {

					excelManager = new ExcelManager();
					excelManager.createWorkBook(excelInputFile.getAbsolutePath());

					populateExcel(excelManager, parseFullDoc.getJobList().get(jobIndex), startingEstimateNo);
					startingEstimateNo += 10;
					excelManager.saveWorkbook(String.format("%s\\%S %s%s", lettingMonthDirectory,
							parseFullDoc.getJobList().get(jobIndex).getCounty(),
							parseFullDoc.getJobList().get(jobIndex).getCsj(), ".xlsm"));
				}

				showWarning("Success", "Success", "Excel files were created");
				audit.add("save	Function save completed.");
			}
		});

		// filter for selection button
		filterForCheckedBoxes.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("filterForCheckedBoxes Button was pressed.");

				ArrayList<Job> selectedJobList = new ArrayList<Job>(); // create a buffer list of jobs

				// for every check box, add job to buffer job list
				for (int currentJobCheckBox = 0; currentJobCheckBox < jobCheckBoxes.size(); currentJobCheckBox++) {

					// if the check box is selected, add the corresponding job to the buffer
					if (jobCheckBoxes.get(currentJobCheckBox).isSelected()) {

						selectedJobList.add(parseFullDoc.getJobList().get(currentJobCheckBox)); // add job to buffer
					}
				}

				parseFullDoc.setJobList(selectedJobList); // set the job list to the selected jobs

				// for every job, print the info
//				for (Job job : parseFullDoc.getJobList())
//
//					job.printJobInfo(); // print the job info

				displayData(); // display the new data
				filterForCheckedBoxes.setEnabled(false);
				audit.add("	Function filterForCheckedBoxes completed.");
			}
		});

		// add pricing button. sets up data panel to allow input from user
		addPricing.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("addPricing Button was pressed.");

				displayPricingInput(); // display the pricing window
				currentDisplay = displayPages.PRICING;

				if (parseFullDoc.getJobList().size() > 1) {

					nextJob.setEnabled(true);
				}
				addPricing.setEnabled(false);
				audit.add("	Function addPricing completed.");
			}
		});

		// Add an action listener to the previous job button
		previousJob.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("previousJob Button was pressed.");

				if (checkPricingPageTextValidity()) {

					setPrices();
					jobIndex--;
					displayPricingInput();

					nextJob.setEnabled(true);
					ifFirstJob = (jobIndex == 0);
					if (ifFirstJob) {

						previousJob.setEnabled(false);
					}
					audit.add("	Function previousJob completed.");
				}
			}
		});

		// Add an action listener to the next job button
		nextJob.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("nextJob Button was pressed.");
				if (checkPricingPageTextValidity()) {

					setPrices();
					jobIndex++;
					displayPricingInput();
					previousJob.setEnabled(true);

					ifLastJob = (jobIndex == parseFullDoc.getJobList().size() - 1);
					if (ifLastJob) {

						nextJob.setEnabled(false);
					}
				}
				audit.add("	Function nextJob completed.");
			}
		});

		// Add a log message
		audit.add("All button functions added.");
	}

	// ====================================================================================================
	// Button Functions
	// ====================================================================================================


	public void displayData() {

		dataScrollPane.remove(viewportContainer);
		dataScrollPane.remove(rowHeaderContainer);

		viewportPanel.removeAll();
		rowHeaderContainer.removeAll();

		viewportContainer.remove(viewportPanel);
		rowHeaderContainer.remove(rowHeaderPanel);

		GridBagConstraints constraints = new GridBagConstraints();

		jobLabels = new ArrayList<JLabel>();
		jobCheckBoxes = new ArrayList<JCheckBox>();

		dataHeaderLabel = new JLabel(" CSJ                 County              Highway             Total Quantities");
		dataHeaderLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

		rowHeaderPanel = new JPanel();
		rowHeaderPanel.setLayout(new GridBagLayout());

		rowHeaderContainer.add(rowHeaderPanel);

		dataScrollPane.setRowHeaderView(rowHeaderContainer);

		viewportPanel = new JPanel();
		viewportPanel.setLayout(new GridBagLayout());
		viewportPanel.setBackground(Color.LIGHT_GRAY);

		viewportContainer.add(viewportPanel, BorderLayout.NORTH);

		dataScrollPane.setViewportView(viewportContainer);
		viewportPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

		constraints.gridx = 1;
		constraints.gridy = 0;
		viewportPanel.add(dataHeaderLabel, constraints);

		for (int index = 0; index < parseFullDoc.getJobList().size(); index++) {

			// sets all job labels into list
			jobLabels.add(new JLabel(String.format("%n%-20s%-20s%-20s     %,10.2f", //
					parseFullDoc.getJobList().get(index).getCsj(), //
					parseFullDoc.getJobList().get(index).getCounty(), //
					parseFullDoc.getJobList().get(index).getHighway(), //
					parseFullDoc.getJobList().get(index).getSumOfQuantities())));

			// sets all job check boxes into list
			jobCheckBoxes.add(new JCheckBox(String.format("%2d:", index + 1)));
//			System.out.println(jobCheckBoxes.get(index).getText());

			jobLabels.get(index).setFont(new Font("Monospaced", Font.PLAIN, 14));
			jobCheckBoxes.get(index).setFont(new Font("Monospaced", Font.PLAIN, 14));

			constraints.anchor = GridBagConstraints.NORTHEAST;
			constraints.gridx = 0;
			constraints.gridy = index + 1;
			constraints.ipady = 0;
			viewportPanel.add(jobCheckBoxes.get(index), constraints);

			constraints.anchor = GridBagConstraints.NORTHEAST;
			constraints.gridx = 1;
			constraints.gridy = index + 1;
			constraints.ipady = 8;
			viewportPanel.add(jobLabels.get(index), constraints);
		}
		audit.add("	Data has been displayed.");
	}

	public void displayPricingInput() {

		GridBagConstraints constraints = new GridBagConstraints();

		dataHeaderLabel = new JLabel();
		dataHeaderLabel.setText(String.format("%-20s%-20s%-20s%-20s", parseFullDoc.getJobList().get(jobIndex).getCsj(),
				parseFullDoc.getJobList().get(jobIndex).getCounty(),
				parseFullDoc.getJobList().get(jobIndex).getHighway(), "county's largest city:  "
						+ cityFinder.getLargestCity(parseFullDoc.getJobList().get(jobIndex).getCounty())));
		dataScrollPane.setColumnHeaderView(dataHeaderLabel);

		rowHeaderPanel = new JPanel();
		rowHeaderContainer.add(rowHeaderPanel);
		dataScrollPane.setRowHeaderView(rowHeaderPanel);

		viewportPanel = new JPanel();
		viewportContainer.add(viewportPanel);
		dataScrollPane.setViewportView(viewportPanel);
		viewportPanel.setLayout(new GridBagLayout());

		constraints.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
		constraints.ipadx = 10;
		constraints.ipady = 10;

//		constraints.gridx = 0;
//		constraints.gridy = 0;
//		viewportPanel.add(new JLabel("Up to how many mobilizations?  "), constraints);
//		constraints.gridx = 1;
//		constraints.gridy = 0;
//		addUpTo_MobsToPricingPage(constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		viewportPanel.add(new JLabel("Total Mobilizations Price?  "), constraints);
		constraints.gridx = 1;
		constraints.gridy = 1;
		addTotalMobsToPricingPage(constraints);

//		constraints.gridx = 0;
//		constraints.gridy = 2;
//		viewportPanel.add(new JLabel("Additional Mobilizations Price   "), constraints);
//		constraints.gridx = 1;
//		constraints.gridy = 2;
//		addAdditionalMobsToPricingPage(constraints);

		addLineItemsToPricingPage(constraints);

		totalMobsTextField.requestFocus();
		audit.add("	Job:  " + parseFullDoc.getJobList().get(jobIndex).getCsj() + "	Pricing page has been displayed.");
	}

	public void addUpTo_MobsToPricingPage(GridBagConstraints constraints) {

		upToMobsTextField = new JTextField();
		upToMobsTextField.setText(String.format("%d", parseFullDoc.getJobList().get(jobIndex).getUpTo_Mobs()));
		upToMobsTextField.setPreferredSize(new Dimension(50, 20));
		upToMobsTextField.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				upToMobsTextField.selectAll();
			}

			public void focusLost(FocusEvent e) {
			}
		});
		viewportPanel.add(upToMobsTextField, constraints);
	}

	public void addAdditionalMobsToPricingPage(GridBagConstraints constraints) {

		additionalMobsTextField = new JTextField();
		additionalMobsTextField
				.setText(String.format("%.0f", parseFullDoc.getJobList().get(jobIndex).getAdditionalMobs()));
		additionalMobsTextField.setPreferredSize(new Dimension(50, 20));
		additionalMobsTextField.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				additionalMobsTextField.selectAll();
			}

			public void focusLost(FocusEvent e) {
			}
		});
		viewportPanel.add(additionalMobsTextField, constraints);
	}

	public void addTotalMobsToPricingPage(GridBagConstraints constraints) {

		totalMobsTextField = new JTextField();
		totalMobsTextField.setText(String.format("%.0f", parseFullDoc.getJobList().get(jobIndex).getTotalMobs()));
		totalMobsTextField.setPreferredSize(new Dimension(50, 20));
		totalMobsTextField.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				totalMobsTextField.selectAll();
			}

			public void focusLost(FocusEvent e) {
			}

		});
		viewportPanel.add(totalMobsTextField, constraints);
	}

	public void addLineItemsToPricingPage(GridBagConstraints constraints) {

		for (int index = 0; index < parseFullDoc.getJobList().get(jobIndex).getLineItems().size(); index++) {

			final int indexForActionListener = index;
			constraints.gridx = 0;
			constraints.gridy = index + 4;
			viewportPanel.add(
					new JLabel(String.format("%s%s%,2.2f",
							parseFullDoc.getJobList().get(jobIndex).getLineItems().get(index).getDescription(),
							"        Quantity: ",
							parseFullDoc.getJobList().get(jobIndex).getLineItems().get(index).getQuantity())),
					constraints);
			lineItemPrices.add(new JTextField());
			lineItemPrices.get(index).setPreferredSize(new Dimension(50, 20));
			lineItemPrices.get(index).setText(String.format("%1.2f",
					parseFullDoc.getJobList().get(jobIndex).getLineItems().get(index).getPrice()));

			lineItemPrices.get(index).addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent e) {

					lineItemPrices.get(indexForActionListener).selectAll();
				}

				public void focusLost(FocusEvent e) {

				}

			});

			constraints.gridx = 1;
			constraints.gridy = index + 4;
			viewportPanel.add(lineItemPrices.get(index), constraints);
		}
	}

	public void setPrices() {

//		pFD.getJobList().get(jobIndex).setUpTo_Mobs(Integer.valueOf(upToMobsTextField.getText()));
		parseFullDoc.getJobList().get(jobIndex).setTotalMobs(Float.valueOf(totalMobsTextField.getText()));
//		pFD.getJobList().get(jobIndex).setAdditionalMobs(Float.valueOf(additionalMobsTextField.getText()));

		// use this if additional mobs is the same as total mobs (add in check boxes to
		// allow the choice
		parseFullDoc.getJobList().get(jobIndex).setAdditionalMobs(Float.valueOf(totalMobsTextField.getText()));

		for (int index = 0; index < lineItemPrices.size(); index++) {

			parseFullDoc.getJobList().get(jobIndex).getLineItems().get(index)
					.setPrice(Float.valueOf(lineItemPrices.get(index).getText()));
		}
		lineItemPrices.clear();
	}

	public void populateExcel(ExcelManager excelManager, Job job, int estimateNumber) {

		String sheetName;

		Contractor contractor;
		LineItem lineItem;
		float lineItemAmount;
		float totalAmount = 0;

		for (int contractorIndex = 0; contractorIndex < job.getContractorList().size(); contractorIndex++) {
			totalAmount = 0;

			sheetName = String.valueOf(contractorIndex + 1);
			contractor = job.getContractorList().get(contractorIndex);

			excelManager.setCellValue(sheetName, 6, 3, job.getCsj());
			excelManager.setCellValue(sheetName, 0, 30, job.getHighway());
			excelManager.setCellValue(sheetName, 0, 33, job.getCounty());
			excelManager.setCellValue(sheetName, 3, 23, job.getAdditionalMobs());
			excelManager.setCellValue(sheetName, 6, 20, job.getTotalMobs());
			excelManager.setCellValue(sheetName, 4, 20, job.getUpTo_Mobs());
			excelManager.setCellValue(sheetName, 0, 27,
					String.format("%s%d", "WR-2023-", estimateNumber + contractorIndex));
			
			audit.add(String.format("%s%d", "WR-2023-", estimateNumber + contractorIndex));

			totalAmount += job.getTotalMobs();
			excelManager.setCellValue(sheetName, 0, 18, contractor.getContractorName());
			excelManager.setCellValue(sheetName, 0, 24, contractor.getContractorEmail());
			excelManager.setCellValue(sheetName, 4, 3, contractor.getContractorEmail());
			excelManager.setCellValue(sheetName, 0, 22, contractor.getContractorPhoneNumber());

			for (int lineItemIndex = 0; lineItemIndex < job.getLineItems().size(); lineItemIndex++) {

				lineItem = job.getLineItems().get(lineItemIndex);
				lineItemAmount = lineItem.getQuantity() * lineItem.getPrice();
				totalAmount += lineItemAmount;

				excelManager.setCellValue(sheetName, 3, 7 + lineItemIndex, lineItem.getQuantity());
				excelManager.setCellValue(sheetName, 4, 7 + lineItemIndex, lineItem.getDescription());
				excelManager.setCellValue(sheetName, 5, 7 + lineItemIndex, lineItem.getPrice());
				excelManager.setCellValue(sheetName, 6, 7 + lineItemIndex, String.format("$%,1.2f", lineItemAmount));
			}
			excelManager.setCellValue(sheetName, 6, 36, String.format("$%,1.2f", totalAmount));
		}
	}

	public void showWarning(String header, String warningMessage, String argument) {
		JOptionPane.showMessageDialog(null, warningMessage + ": " + argument, header, JOptionPane.WARNING_MESSAGE);
	}

	public boolean checkPricingPageTextValidity() {
		boolean valid = true;
		String invalidInput = checkTextFields(totalMobsTextField);
		if (invalidInput != null) {
			showWarning("Warning", "Invalid input", invalidInput + " is not a valid number for total mobilizations");
			valid = false;
		}
		invalidInput = checkTextFields(lineItemPrices);
		if (invalidInput != null) {
			showWarning("Warning", "Invalid input", invalidInput + " is not a valid number for line item price");
			valid = false;
		}
		return valid;
	}

	public String checkTextFields(ArrayList<JTextField> textField) {
		for (int i = 0; i < textField.size(); i++) {
			try {
				Float.parseFloat(textField.get(i).getText());
			} catch (NumberFormatException e) {
				return textField.get(i).getText();
			}
		}
		return null;
	}

	public String checkTextFields(JTextField textField) {

		try {
			Float.parseFloat(textField.getText());
		} catch (NumberFormatException e) {
			return textField.getText();
		}
		return null;
	}
}
