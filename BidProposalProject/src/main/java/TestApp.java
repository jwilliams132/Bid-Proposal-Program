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
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

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
	private final Font TITLEFONT = new Font("Monospaced", Font.BOLD, 50);
	private final Font FONT = new Font("Monospaced", Font.PLAIN, 16);
	private final Color BACKGROUND = Color.WHITE;
	private final Color FOREGROUND = Color.BLACK;
	private final Color SCROLLPANECOLOR = Color.LIGHT_GRAY;

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
	/* -- */private JScrollPane legendScrollPane;
	/* ------ */private JPanel legendPanel;
	/* ---------- */private JLabel locatorLabel;
	/* -- */private JScrollPane dataScrollPane;
	/* ------ */private JPanel columnHeaderPanel;
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

	JLabel startupLabel1;
	JLabel startupLabel2;

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
		chooseOpenFile.doClick(); // testing purposes
		filterForCheckedBoxes.doClick(); // testing purposes
		addPricing.doClick();
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
		frmWilliamsRoadLlc.setBackground(BACKGROUND);

		// ===========================================================================
		// Main Panel (North) Open File
		// ===========================================================================

		openFilePanel = new JPanel();
		openFilePanel.setBackground(BACKGROUND);
		openFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		openFilePanel.setLayout(new BorderLayout(10, 10));
		frmWilliamsRoadLlc.getContentPane().add(openFilePanel, BorderLayout.NORTH);

		chooseOpenFile = new JButton("Open Bidding File...");
		openFilePanel.add(chooseOpenFile, BorderLayout.WEST);

		updateBidders = new JButton("Add Updated Bidders");
		updateBidders.setHorizontalAlignment(SwingConstants.LEFT);
		updateBidders.setEnabled(false);
		openFilePanel.add(updateBidders, BorderLayout.EAST);

		openFilePathLabel = new JLabel("File Path:  ");
		openFilePanel.add(openFilePathLabel);

		// ===========================================================================
		// Main Panel (South) Save File
		// ===========================================================================

		saveFilePanel = new JPanel();
		frmWilliamsRoadLlc.getContentPane().add(saveFilePanel, BorderLayout.SOUTH);
		saveFilePanel.setLayout(new BorderLayout(10, 10));
		saveFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		saveFilePanel.setBackground(BACKGROUND);
		chooseSaveDirectory = new JButton("Choose a folder to save to...");
		chooseSaveDirectory.setEnabled(false);
		saveFilePanel.add(chooseSaveDirectory, BorderLayout.WEST);

		save = new JButton("Export Excel Files");
		save.setEnabled(false);
		saveFilePanel.add(save, BorderLayout.EAST);

		saveFilePathLabel = new JLabel("Directory Path:  ");
		saveFilePanel.add(saveFilePathLabel);

		// ===========================================================================
		// Main Panel (Center) Display Data
		// ===========================================================================

		dataPanel = new JPanel();
		dataPanel.setLayout(new BorderLayout(0, 0));
		dataPanel.setBackground(SCROLLPANECOLOR);

		legendScrollPane = new JScrollPane();
		legendScrollPane.setBorder(new EmptyBorder(0, 10, 0, 10));
		legendScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		legendScrollPane.setBackground(BACKGROUND);

		legendPanel = new JPanel() {
			{
				setLayout(new GridBagLayout());
				setBackground(BACKGROUND);
			}
		};
		locatorLabel = new JLabel(">");

		dataScrollPane = new JScrollPane();
		dataScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		dataScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		dataScrollPane.setBackground(SCROLLPANECOLOR);

		columnHeaderPanel = new JPanel();
		columnHeaderPanel.setBackground(SCROLLPANECOLOR);

		viewportPanel = new JPanel() {
			{
				setLayout(new GridLayout(2, 0));
				setBackground(SCROLLPANECOLOR);
			}
		};
		startupLabel1 = new JLabel("BIDDING PROGRAM") {
			{
				setFont(TITLEFONT);
				setHorizontalAlignment(JLabel.CENTER);
				setVerticalAlignment(JLabel.BOTTOM);
			}
		};

		startupLabel2 = new JLabel("Click \"Open Bidding File\" To Get Started") {
			{
				setFont(FONT);
				setHorizontalAlignment(JLabel.CENTER);
				setVerticalAlignment(JLabel.TOP);
			}
		};
		viewportPanel.add(startupLabel1);
		viewportPanel.add(startupLabel2);

		viewportContainer = new JPanel();
		viewportContainer.setLayout(new BorderLayout(0, 0));
		viewportContainer.setBackground(SCROLLPANECOLOR);

		frmWilliamsRoadLlc.getContentPane().add(dataPanel, BorderLayout.CENTER);

		dataPanel.add(legendScrollPane, BorderLayout.WEST);

		dataPanel.add(dataScrollPane, BorderLayout.CENTER);
		dataScrollPane.setColumnHeaderView(columnHeaderPanel);

		viewportContainer.add(viewportPanel);
		dataScrollPane.setViewportView(viewportContainer);

		// ===========================================================================
		// Sub Panel (MP (Center)) (South) Manipulation Buttons
		// ===========================================================================

		dataManipulationPanel = new JPanel();
		dataManipulationPanel.setBackground(BACKGROUND);
		dataPanel.add(dataManipulationPanel, BorderLayout.SOUTH);
		dataManipulationPanel.setLayout(new BorderLayout(0, 0));

		jobFilterPanel = new JPanel();
		jobFilterPanel.setBackground(BACKGROUND);
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
		jobSelectionPanel.setBackground(BACKGROUND);
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

		/*
		 * popup for a filechooser
		 * if null, shows a warning popup
		 * 
		 * updates file path label
		 * 
		 * creates ParseFullDoc instance
		 * 
		 * -inside PFD
		 * sets Audit
		 * sets input file
		 * parses the Data
		 * 
		 * enables buttons
		 * sets data window label
		 * displayData()
		 */
		chooseOpenFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("chooseOpenFile Button was pressed.");

				// The "chooseOpenFileButton" was pressed, so show the file chooser
				// File inputFile = fileManager.chooseFile(null, null,
				// FileManager.fileChooserOptions.OPEN, txtFileFilter);

				// testing purposes
				File inputFile = fileManager.chooseFile(
						"C:\\Users\\School laptop(Jacob)\\Desktop\\Letting\\Test\\Combined.txt", null,
						FileManager.fileChooserOptions.OPEN, txtFileFilter);

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
				addPricing.setEnabled(false);
				updateBidders.setEnabled(true);

				// set header
				columnHeaderPanel.add(new JLabel() {
					{
						setText("Job List for ".concat(parseFullDoc.getBidFileType()));
						setFont(FONT);
						setForeground(FOREGROUND);
					}
				}, BorderLayout.WEST);

				// Display the data
				displayFirst();
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

		// Add an action listener to the choose save file button
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
				File formattedOutput = fileManager.chooseFile(lettingMonthDirectory + "\\Program Ouput.txt", null,
						FileManager.fileChooserOptions.SAVE, null);
				File userFriendlyOutput = fileManager.chooseFile(
						lettingMonthDirectory + "\\Program Output (User Friendly).txt", null,
						FileManager.fileChooserOptions.SAVE, null);
				File emailList = fileManager.chooseFile(lettingMonthDirectory + "\\Email List.txt", null,
						FileManager.fileChooserOptions.SAVE, null);

				// // Set the prices for the current job
				// setPrices();
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

				File excelInputFile = fileManager.chooseFile(
						"BidProposalProject\\src\\main\\resources\\Test Template.xlsm",
						null, FileManager.fileChooserOptions.OPEN, xslmFileFilter);

				int startingEstimateNo = 1780;
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

				boolean hasSeletedCheckBox = false;

				audit.add("filterForCheckedBoxes Button was pressed.");

				ArrayList<Job> selectedJobList = new ArrayList<Job>(); // create a buffer list of jobs

				// for every check box, add job to buffer job list
				for (int currentJobCheckBox = 0; currentJobCheckBox < jobCheckBoxes.size(); currentJobCheckBox++) {

					// if the check box is selected, add the corresponding job to the buffer
					if (jobCheckBoxes.get(currentJobCheckBox).isSelected()) {

						hasSeletedCheckBox = true;
						selectedJobList.add(parseFullDoc.getJobList().get(currentJobCheckBox)); // add job to buffer
					}
				}

				if (!hasSeletedCheckBox) {
					selectedJobList.addAll(parseFullDoc.getJobList());
				}
				parseFullDoc.setJobList(selectedJobList); // set the job list to the selected jobs

				// for every job, print the info
				// for (Job job : parseFullDoc.getJobList())
				//
				// job.printJobInfo(); // print the job info

				displayFiltered(); // display the new data

				filterForCheckedBoxes.setEnabled(false);
				addPricing.setEnabled(true);
				audit.add("	Function filterForCheckedBoxes completed.");
			}
		});

		// add pricing button. sets up data panel to allow input from user
		addPricing.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				audit.add("addPricing Button was pressed.");

				// displaySideLegend(); TODO
				displayPricing(); // display the pricing window
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
					displayPricing();

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
					displayPricing();
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
	// Methods
	// ====================================================================================================

	private void clearScrollPanel() {
		viewportContainer.removeAll();
	}

	// ===========================================================================
	// First Display
	// ===========================================================================

	public void displayFirst() {

		GridBagConstraints displayConstraints = new GridBagConstraints();
		JPanel firstDisplay = new JPanel(new GridBagLayout());

		jobCheckBoxes = new ArrayList<JCheckBox>();
		final ItemListener checkAllListener = new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {

					selectAllCheckBoxes(jobCheckBoxes);
				} else {

					deselectAllCheckBoxes(jobCheckBoxes);
				}
			}
		};

		final JCheckBox checkAll = new JCheckBox(" ALL ") {
			{
				setFont(FONT);
				setBackground(SCROLLPANECOLOR);
				setForeground(FOREGROUND);
				addItemListener(checkAllListener);
			}
		};

		final ItemListener jobCheckBoxListener = new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {

					checkAll.removeItemListener(checkAllListener);
					checkAll.setSelected(false);
					checkAll.addItemListener(checkAllListener);
				}
			}
		};
		int lineItemCount = 0;

		clearScrollPanel();

		firstDisplay = new JPanel(new GridBagLayout());
		firstDisplay.setBackground(SCROLLPANECOLOR);
		viewportContainer.add(firstDisplay, BorderLayout.NORTH);
		dataScrollPane.setViewportView(viewportContainer);

		displayConstraints.gridx = 1;
		displayConstraints.gridy = 0;
		firstDisplay.add(new JLabel() {
			{
				setText(String.format("%-20s%-20s%-20s%16s", "CSJ", "County", "Highway", "Total Quantities"));
				setFont(FONT);
				setBackground(SCROLLPANECOLOR);
				setForeground(FOREGROUND);
			}
		}, displayConstraints);

		for (int index = 0; index < parseFullDoc.getJobList().size(); index++) {
			Job currentJob = parseFullDoc.getJobList().get(index);

			// sets all job check boxes into list here
			jobCheckBoxes.add(new JCheckBox(String.format("  %2d:", index + 1)) {
				{
					setFont(FONT);
					setBackground(SCROLLPANECOLOR);
					setForeground(FOREGROUND);
					addItemListener(jobCheckBoxListener);
				}
			});
			displayConstraints.gridx = 0;
			displayConstraints.gridy = 0;
			firstDisplay.add(checkAll, displayConstraints);
			displayConstraints.gridx = 0;
			displayConstraints.gridy = index + lineItemCount + 1;

			firstDisplay.add(jobCheckBoxes.get(index), displayConstraints);

			displayConstraints.gridx = 1;
			displayConstraints.gridy = index + lineItemCount + 1;
			firstDisplay.add(new JLabel(String.format("%n%-20s%-20s%-20s     %,11.2f",
					currentJob.getCsj(),
					currentJob.getCounty(),
					currentJob.getHighway(),
					currentJob.getSumOfQuantities())) {
				{
					setFont(new Font("Monospaced", Font.BOLD, 16));
					setForeground(FOREGROUND);
				}
			}, displayConstraints);

			for (LineItem lineItem : currentJob.getLineItems()) {
				lineItemCount++;
				displayConstraints.gridx = 1;
				displayConstraints.gridy = index + lineItemCount + 1;
				firstDisplay.add(new JLabel(
						String.format("%-40s     %,10.2f%19s", lineItem.getDescription(), lineItem.getQuantity(), "")) {
					{
						setFont(FONT);
						setForeground(FOREGROUND);
					}
				}, displayConstraints);

			}
		}
		audit.add("	Data has been displayed.");
	}

	private void selectAllCheckBoxes(ArrayList<JCheckBox> checkBoxes) {
		for (JCheckBox checkBox : checkBoxes) {
			checkBox.setSelected(true);
		}
	}

	private void deselectAllCheckBoxes(ArrayList<JCheckBox> checkBoxes) {
		for (JCheckBox checkBox : checkBoxes) {
			checkBox.setSelected(false);
		}
	}

	// ===========================================================================
	// Filtered Display
	// ===========================================================================

	private void displayFiltered() {

		clearScrollPanel();

		JPanel filteredDisplay = new JPanel(new GridBagLayout());
		GridBagConstraints filteredDisplayConstraints = new GridBagConstraints();

		filteredDisplay.setBackground(SCROLLPANECOLOR);
		viewportContainer.add(filteredDisplay, BorderLayout.NORTH);
		dataScrollPane.setViewportView(viewportContainer);

		filteredDisplayConstraints.gridx = 0;
		filteredDisplayConstraints.gridy = 0;

		filteredDisplay.add(new JLabel() {
			{
				setText(String.format("%-20s%-20s%-20s%16s", "CSJ", "County", "Highway", "Total Quantities"));
				setFont(FONT);
				setBackground(SCROLLPANECOLOR);
				setForeground(FOREGROUND);
			}
		}, filteredDisplayConstraints);

		int filteredLineItemCount = 0;
		for (int jobCount = 0; jobCount < parseFullDoc.getJobList().size(); jobCount++) {

			Job currentJob = parseFullDoc.getJobList().get(jobCount);

			filteredDisplayConstraints.gridy = jobCount + filteredLineItemCount + 1;
			filteredDisplay.add(new JLabel(String.format("%n%-20s%-20s%-20s     %,11.2f",
					currentJob.getCsj(),
					currentJob.getCounty(),
					currentJob.getHighway(),
					currentJob.getSumOfQuantities())) {
				{
					setFont(new Font("Monospaced", Font.BOLD, 16));
					setForeground(FOREGROUND);
				}
			}, filteredDisplayConstraints);

			for (LineItem lineItem : currentJob.getLineItems()) {
				filteredLineItemCount++;
				filteredDisplayConstraints.gridy = jobCount + filteredLineItemCount + 1;
				filteredDisplay.add(new JLabel(
						String.format("%-40s     %,10.2f%19s", lineItem.getDescription(), lineItem.getQuantity(), "")) {
					{
						setFont(FONT);
						setForeground(FOREGROUND);
					}
				}, filteredDisplayConstraints);
			}
		}
	}

	// ===========================================================================
	// Pricing Display
	// ===========================================================================

	public void displayPricing() {

		// displaySideLegend(); TODO
		GridBagConstraints displayPricingConstraints = new GridBagConstraints();
		final Job currentJob = parseFullDoc.getJobList().get(jobIndex);
		dataScrollPane.setColumnHeaderView(new JLabel() {
			{
				setText(String.format("%-20s%-20s%-20s%-20s", currentJob.getCsj(),
						currentJob.getCounty(),
						currentJob.getHighway(),
						"county's largest city:  ".concat(cityFinder.getLargestCity(currentJob.getCounty()))));
				setFont(FONT);
				setForeground(FOREGROUND);
			}
		});

		JPanel pricingDisplay = new JPanel();
		viewportContainer.add(pricingDisplay);
		dataScrollPane.setViewportView(pricingDisplay);
		pricingDisplay.setLayout(new GridBagLayout());

		displayPricingConstraints.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
		displayPricingConstraints.ipadx = 10;
		displayPricingConstraints.ipady = 10;

		displayPricingConstraints.gridx = 0;
		displayPricingConstraints.gridy = 1;
		pricingDisplay.add(new JLabel("Total Mobilizations Price?  ") {
			{
				setFont(FONT);
				setForeground(FOREGROUND);
			}
		}, displayPricingConstraints);
		displayPricingConstraints.gridx = 1;
		displayPricingConstraints.gridy = 1;
		addTotalMobsToPricingPage(pricingDisplay, displayPricingConstraints);
		addLineItemsToPricingPage(pricingDisplay, displayPricingConstraints);

		totalMobsTextField.requestFocus();
		audit.add("	Job:  " + parseFullDoc.getJobList().get(jobIndex).getCsj() + "	Pricing page has been displayed.");
	}

	public void addUpTo_MobsToPricingPage(JPanel pricingDisplay, GridBagConstraints displayPricingConstraints) {

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
		pricingDisplay.add(upToMobsTextField, displayPricingConstraints);
	}

	public void addAdditionalMobsToPricingPage(JPanel pricingDisplay, GridBagConstraints displayPricingConstraints) {

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
		pricingDisplay.add(additionalMobsTextField, displayPricingConstraints);
	}

	public void addTotalMobsToPricingPage(JPanel pricingDisplay, GridBagConstraints displayPricingConstraints) {

		pricingDisplay.add(new JLabel("$"), displayPricingConstraints);
		displayPricingConstraints.gridx = 2;
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
		pricingDisplay.add(totalMobsTextField, displayPricingConstraints);
	}

	public void addLineItemsToPricingPage(JPanel pricingDisplay, GridBagConstraints displayPricingConstraints) {

		for (int index = 0; index < parseFullDoc.getJobList().get(jobIndex).getLineItems().size(); index++) {

			final int indexForActionListener = index;
			displayPricingConstraints.gridx = 0;
			displayPricingConstraints.gridy = index + 4;
			pricingDisplay.add(new JLabel(String.format("%-40s%s%,12.2f%s",
					parseFullDoc.getJobList().get(jobIndex).getLineItems().get(index).getDescription(),
					"    Quantity: ",
					parseFullDoc.getJobList().get(jobIndex).getLineItems().get(index).getQuantity(),
					" (Sq. Yds.)")) {
				{
					setFont(FONT);
					setForeground(FOREGROUND);
				}
			},
					displayPricingConstraints);
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

			displayPricingConstraints.gridx = 1;
			displayPricingConstraints.gridy = index + 4;

			pricingDisplay.add(new JLabel("$"), displayPricingConstraints);
			displayPricingConstraints.gridx = 2;

			pricingDisplay.add(lineItemPrices.get(index), displayPricingConstraints);

			displayPricingConstraints.gridx = 3;
			pricingDisplay.add(new JLabel("(per Sq. Yd.)"), displayPricingConstraints);
		}
	}

	public void setPrices() {

		// pFD.getJobList().get(jobIndex).setUpTo_Mobs(Integer.valueOf(upToMobsTextField.getText()));
		parseFullDoc.getJobList().get(jobIndex).setTotalMobs(Float.valueOf(totalMobsTextField.getText()));
		// pFD.getJobList().get(jobIndex).setAdditionalMobs(Float.valueOf(additionalMobsTextField.getText()));

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

	public Color randomColor() {

		Random random = new Random();

		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	public void displaySideLegend() {
		ArrayList<JButton> jobButtons = new ArrayList<JButton>();
		for (int jobIndexForLegendButtons = 0; jobIndexForLegendButtons < parseFullDoc.getJobList()
				.size(); jobIndexForLegendButtons++) {

			Job job = parseFullDoc.getJobList().get(jobIndexForLegendButtons);
			final int JOB_INDEX = jobIndexForLegendButtons;

			String bufferCounty = job.getCounty();
			if (bufferCounty.length() > 5 && bufferCounty.substring(bufferCounty.length() - 5).equals(", ETC"))
				bufferCounty = bufferCounty.substring(0, bufferCounty.length() - 5);

			final String CSJ = job.getCsj().substring(8, 11);
			final String county = bufferCounty;

			jobButtons.add(new JButton() {
				{
					setText(String.format("%13s %s", county, CSJ));
					setFont(FONT);

					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

							jobIndex = JOB_INDEX;
							displayPricing();
						}
					});
				}
			});

			GridBagConstraints legendConstraints = new GridBagConstraints();

			legendPanel.remove(locatorLabel);
			legendConstraints.gridx = 0;
			legendConstraints.gridy = jobIndex;
			legendPanel.add(locatorLabel, legendConstraints);

			legendConstraints.gridx = 1;

				for (int buttonIndex = 0; buttonIndex < jobButtons.size(); buttonIndex++) {
				
				legendConstraints.gridy = buttonIndex;
				legendPanel.add(jobButtons.get(buttonIndex), legendConstraints);
			}
		}
		legendScrollPane.setViewportView(legendPanel);
	}
}
