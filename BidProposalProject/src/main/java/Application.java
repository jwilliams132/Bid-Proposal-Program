import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
// import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

public class Application {

    private FileManager fileManager = new FileManager();
    private InputFileProcessor jobStorage = new InputFileProcessor();
    private String lettingMonthDirectory;

    private final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 50);
    private final Font FONT = new Font("Monospaced", Font.PLAIN, 16);

    // Dark Theme Colors
    // private final Color PRIMARY_FONT_COLOR = new Color(0, 179, 189);
    // private final Color SECONDARY_FONT_COLOR = new Color(55, 136, 186);
    // private final Color TERTIARY_FONT_COLOR = new Color(192, 155, 82);
    // private final Color QUATERNARY_FONT_COLOR = new Color(201, 62, 113);
    // private final Color PRIMARY_BACKGROUND = new Color(13, 21, 33);
    // private final Color SECONDARY_BACKGROUND = new Color(16, 26, 41);

    // Light Theme Colors
    private final Color PRIMARY_FONT_COLOR = Color.BLACK;
    private final Color SECONDARY_FONT_COLOR = Color.BLACK;
    private final Color TERTIARY_FONT_COLOR = Color.BLACK;
    private final Color QUATERNARY_FONT_COLOR = Color.BLACK;
    private final Color PRIMARY_BACKGROUND = Color.WHITE;
    private final Color SECONDARY_BACKGROUND = Color.LIGHT_GRAY;

    private final Color TITLE_FONT_COLOR = TERTIARY_FONT_COLOR;

    private ArrayList<JCheckBox> jobCheckBoxes = new ArrayList<JCheckBox>();
    private ArrayList<Integer> currentJobFilterIndexes = new ArrayList<Integer>();
    private ArrayList<JTextField> lineItemPrices = new ArrayList<JTextField>();
    private int jobIndex = 0;

    private List<Job> currentJobList, filteredJobList;

    private enum Display {
        STARTUP, UNFILTERED, FILTERED, PRICING
    };

    // private enum JOBSET {
    // OLD, NEW
    // };

    private enum TEST {
        TEST, REAL
    };

    private enum ExcelFormat {
        V1, V2
    }

    private Display currentDisplay = null;
    private TEST ifTest = TEST.REAL;
    private ExcelFormat preferredExcelFormat = ExcelFormat.V2;

    private FileFilter txtFileFilter = new FileFilter() {
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
        }

        public String getDescription() {
            return "Text files (*.txt)";
        }
    };

    
    // =====Open File Panel=====
    /**/private JPanel openFilePanel;

    /* -- */private JButton chooseOpenFile;
    /* -- */private JLabel openFilePathLabel;
    /* -- */private JButton updateBidders;

    // =====Data Panel==========
    /* ---- */private JPanel displayPanel;
    /* ------------------ */private JTextField upToMobsTextField;
    /* ------------------ */private JTextField totalMobsTextField;
    /* ------------------ */private JTextField additionalMobsTextField;

    // ==========Data Manipulation Panel
    /* -- */private JPanel dataManipulationPanel;
    /* ------ */private JPanel jobFilterPanel;
    /* ---------- */private JButton filterJobs;
    /* ---------- */private JButton undoFiltering;
    /* ---------- */private JButton addPricing;
    /* ------ */private JPanel jobSelectionPanel;
    /* ---------- */private JButton previousJob;
    /* ---------- */private JLabel currentJob;
    /* ---------- */private JButton nextJob;

    // =====Save File Panel=====
    /**/private JPanel saveFilePanel;
    /* -- */private JButton chooseSaveFolder;
    /* -- */private JLabel saveFilePathLabel;
    /* -- */private JButton saveExcel;

    private JPanel startupDisplay = new JPanel();
    private JScrollPane legendDisplay = new JScrollPane();
    private JPanel bottomPanel;
    private ArrayList<JButton> jobButtons;

    /**
     * Launch the application. TODO
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            public void run() {

                new Application();
            }
        });
    }

    /**
     * Create the application.
     */
    public Application() {

        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        // ===========================================================================
        // Frame
        // ===========================================================================

        String path_to_Cropped_WR_LLC_logo = "BidProposalProject\\src\\main\\resources\\Cropped WR LLC logo.jpg";

        initializeButtons();
        initializePanesAndPanels();
        addBackgrounds();
        addButtonListeners();

        new JFrame() {
            {
                setIconImage(Toolkit.getDefaultToolkit().getImage(path_to_Cropped_WR_LLC_logo));
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("Williams Road LLC Bid Form Program");
                getContentPane().setLayout(new BorderLayout());
                setSize(1300, 600);
                setBackground(PRIMARY_BACKGROUND);
                getContentPane().add(openFilePanel, BorderLayout.NORTH);
                getContentPane().add(displayPanel, BorderLayout.CENTER);
                getContentPane().add(bottomPanel, BorderLayout.SOUTH);
                setLocationRelativeTo(null);
                setVisible(true);
            }
        };

        // ===========================================================================
        // Main Panel (North) Open File
        // ===========================================================================

        openFilePathLabel = new JLabel("File Path:  ");
        openFilePathLabel.setFont(FONT);
        openFilePathLabel.setForeground(QUATERNARY_FONT_COLOR);
        openFilePanel.add(chooseOpenFile, BorderLayout.WEST);
        openFilePanel.add(openFilePathLabel, BorderLayout.CENTER);
        openFilePanel.add(updateBidders, BorderLayout.EAST);

        // ===========================================================================
        // Main Panel (South) Save File
        // ===========================================================================

        saveFilePathLabel = new JLabel("Directory Path:  ");
        saveFilePathLabel.setFont(FONT);
        saveFilePathLabel.setForeground(QUATERNARY_FONT_COLOR);
        saveFilePanel.add(chooseSaveFolder, BorderLayout.WEST);
        saveFilePanel.add(saveFilePathLabel, BorderLayout.CENTER);
        saveFilePanel.add(saveExcel, BorderLayout.EAST);

        // ===========================================================================
        // Main Panel (Center) Display Data
        // ===========================================================================

        startupDisplay = getStartupDisplay();
        changeDisplay(startupDisplay, Display.STARTUP);

        bottomPanel.add(dataManipulationPanel, BorderLayout.NORTH);
        bottomPanel.add(saveFilePanel, BorderLayout.SOUTH);

        dataManipulationPanel.add(jobFilterPanel, BorderLayout.WEST);
        dataManipulationPanel.add(jobSelectionPanel, BorderLayout.CENTER);

        jobFilterPanel.add(filterJobs);
        jobFilterPanel.add(addPricing);

        jobSelectionPanel.add(previousJob);
        currentJob = new JLabel("(00/00)");
        currentJob.setFont(FONT);
        currentJob.setForeground(SECONDARY_FONT_COLOR);
        jobSelectionPanel.add(currentJob);
        jobSelectionPanel.add(nextJob);

        createTestKeyStroke();
    }

    // ====================================================================================================
    // Initialization Methods
    // ====================================================================================================

    private void initializeButtons() {

        chooseOpenFile = new JButton("Open Bidding File...");
        updateBidders = new JButton("Add Updated Bidders");
        chooseSaveFolder = new JButton("Choose a folder to save to...");
        saveExcel = new JButton("Export Excel Files");
        filterJobs = new JButton("Confirm Selected Jobs");
        undoFiltering = new JButton("Undo Filtering");
        addPricing = new JButton("Add Pricing to Jobs");
        previousJob = new JButton("<< Previous Job");
        nextJob = new JButton("Next Job >>");

        updateBidders.setEnabled(false);
        chooseSaveFolder.setEnabled(false);
        saveExcel.setEnabled(false);
        filterJobs.setEnabled(false);
        addPricing.setEnabled(false);
        previousJob.setEnabled(false);
        nextJob.setEnabled(false);
    }

    private void initializePanesAndPanels() {

        openFilePanel = new JPanel();
        openFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        openFilePanel.setLayout(new BorderLayout(10, 10));

        saveFilePanel = new JPanel();
        saveFilePanel.setLayout(new BorderLayout(10, 10));
        saveFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        dataManipulationPanel = new JPanel();
        dataManipulationPanel.setLayout(new BorderLayout(0, 0));

        jobFilterPanel = new JPanel();
        jobFilterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        jobSelectionPanel = new JPanel();
        jobSelectionPanel.setLayout(new FlowLayout());
    }

    private void addBackgrounds() {
        ArrayList<JComponent> backgroundColorPanesAndPanels = new ArrayList<JComponent>(
                Arrays.asList(openFilePanel, saveFilePanel, dataManipulationPanel,
                        jobFilterPanel, jobSelectionPanel, displayPanel, bottomPanel));

        for (JComponent component : backgroundColorPanesAndPanels) {
            component.setBackground(PRIMARY_BACKGROUND);
        }
    }

    private void addButtonListeners() {

        chooseOpenFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        openChosenStartFile();

                    }
                });
            }
        });

        updateBidders.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        getUpdatedDoc(); //TODO use this as test button for now
                    }
                });
            }
        });

        filterJobs.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        
                        filterJobSelection();
                    }
                });
            }
        });

        undoFiltering.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        removeFilter();
                    }
                });
            }
        });

        chooseSaveFolder.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        createOutputFiles();
                    }
                });
            }
        });

        addPricing.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        switchToPricingPage();
                    }
                });
            }
        });

        previousJob.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        showPreviousJob();
                    }
                });
            }
        });

        nextJob.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        showNextJob();
                    }
                });
            }
        });

        saveExcel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        createExcelFiles();
                    }
                });
            }
        });
    }

    private void createTestKeyStroke() {

        Action myAction = new AbstractAction("My Action") {

            @Override
            public void actionPerformed(ActionEvent e) {

                ifTest = TEST.TEST;
                openChosenStartFile();
                ifTest = TEST.REAL;
            }
        };

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK);
        chooseOpenFile.getInputMap(JComponent.WHEN_FOCUSED).put(keyStroke, "myAction");
        chooseOpenFile.getActionMap().put("myAction", myAction);
    }

    // ====================================================================================================
    // Button Methods
    // ====================================================================================================

    private void openChosenStartFile() {

        currentJob.setText("(00/00)");
        currentJobFilterIndexes.clear();
        jobIndex = 0;

        // ====CHOOSE FILE====

        File inputFile = fileManager.chooseFile(
                ifTest == TEST.TEST ? "BidProposalProject\\src\\main\\resources\\Testing\\CombinedOld.txt" : null,
                null, FileManager.fileChooserOptions.OPEN, txtFileFilter);

        if (inputFile == null) {

            showWarning("Warning", "Error", "No file selected");
            return;
        }

        // ====PARSE FILE CONTENTS====

        currentJobList = jobStorage.parseFile(inputFile.getAbsolutePath());

        // SET OPENED FILE PATH

        lettingMonthDirectory = Paths.get(inputFile.getAbsolutePath()).getParent().toString();

        openFilePathLabel.setText("File Path:  " + inputFile);

        chooseSaveFolder.setEnabled(true);
        filterJobs.setEnabled(true);
        undoFiltering.setEnabled(true);
        addPricing.setEnabled(false);
        updateBidders.setEnabled(true);
        previousJob.setEnabled(false);
        nextJob.setEnabled(false);

        jobFilterPanel.remove(0); // replaces removeFilter button with Filter button
        jobFilterPanel.add(filterJobs, 0);

        changeDisplay(getUnfilteredDisplay(), Display.UNFILTERED);
    }

    private void getUpdatedDoc() { // TODO

        // File updatedFile = fileManager.chooseFile(null, null,
        // FileManager.fileChooserOptions.OPEN, null);

        // if (updatedFile == null) {

        // showWarning("Warning", "Error", "No file selected");
        // return;
        // }

        // ParseFullDoc updatedDoc;
        // updatedDoc = new ParseFullDoc();
        // updatedDoc.setNewInputFile(updatedFile);
        // updatedDoc.parseData();
        // updatedDoc.setFullJobList(updatedDoc.getJobList());

        // displayUpdateInfoFrame(updatedDoc);
    }

    private void filterJobSelection() {

        ArrayList<Job> selectedJobList = new ArrayList<Job>(); // create a buffer list of jobs
        ArrayList<Integer> selectedJobIndexes = new ArrayList<Integer>(); // create a buffer list of jobs
        boolean hasSelectedCheckBox = false;

        // for every check box, add job to buffer job list
        for (int currentJobCheckBox = 0; currentJobCheckBox < jobCheckBoxes
                .size(); currentJobCheckBox++) {

            // if the check box is selected, add the corresponding job to the buffer
            if (jobCheckBoxes.get(currentJobCheckBox).isSelected()) {

                hasSelectedCheckBox = true;
                selectedJobList.add(currentJobList.get(currentJobCheckBox));// add job to
                                                                            // buffer
                selectedJobIndexes.add(currentJobCheckBox); // save job indexes of filter
            }
        }

        if (!hasSelectedCheckBox) {
            selectedJobList.addAll(currentJobList);
        }

        filteredJobList = selectedJobList; // set the job list to the selected jobs

        changeDisplay(getFilteredDisplay(), Display.FILTERED);

        filterJobs.setEnabled(false);
        addPricing.setEnabled(true);

        currentJobFilterIndexes = selectedJobIndexes;
        jobFilterPanel.remove(0); // replaces filter button with removeFilter button
        jobFilterPanel.add(undoFiltering, 0);
    }

    private void removeFilter() {

        filteredJobList = currentJobList;
        changeDisplay(getUnfilteredDisplay(), Display.UNFILTERED);

        filterJobs.setEnabled(true);
        addPricing.setEnabled(false);

        jobFilterPanel.remove(0); // replaces removeFilter button with Filter button
        jobFilterPanel.add(filterJobs, 0);
    }

    private void createOutputFiles() {

        switch (currentDisplay) {

            case FILTERED:
                break;
            case PRICING:
                if (!isPricingValid())
                    return;
            default:
        }
        setPrices();

        lettingMonthDirectory = fileManager.chooseDirectory(lettingMonthDirectory);
        // Get the selected file
        File userFriendlyOutput = fileManager.chooseFile(
                lettingMonthDirectory + "\\Program Output (User Friendly).txt", null,
                FileManager.fileChooserOptions.SAVE, null);
        File emailList = fileManager.chooseFile(lettingMonthDirectory + "\\Email List.txt", null,
                FileManager.fileChooserOptions.SAVE, null);

        ArrayList<String> userFriendlyOutputBuffer = new ArrayList<String>();
        ArrayList<String> emailListBuffer = new ArrayList<String>();

        jobStorage.saveFileFormat(filteredJobList, lettingMonthDirectory + "\\Program Output.txt",
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
        saveFilePathLabel.setText("Directory Path:  " + lettingMonthDirectory);

        // Enable the save button
        saveExcel.setEnabled(true);

        // Disable the updateBidders button
        updateBidders.setEnabled(false);
    }

    private void switchToPricingPage() {

        // legendDisplay = getLegendPane();
        resetLegendAndPricingPanel();
        enableIterationButtons();
        addPricing.setEnabled(false);
        undoFiltering.setEnabled(false);
    }

    private void showPreviousJob() {

        if (isPricingValid()) {

            setPrices();
            jobIndex--;
            legendAutoFollow(legendDisplay);
            resetLegendAndPricingPanel();
            enableIterationButtons();
        }
    }

    private void showNextJob() {

        if (isPricingValid()) {

            setPrices();
            jobIndex++;
            legendAutoFollow(legendDisplay);
            resetLegendAndPricingPanel();
            enableIterationButtons();
        }
    }

    private void createExcelFiles() {

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

    // ====================================================================================================
    // Methods
    // ====================================================================================================

    private void changeDisplay(JComponent panel, Display currentDisplay) {

        displayPanel.removeAll();
        displayPanel.revalidate();
        displayPanel.repaint();
        displayPanel.add(panel);
        this.currentDisplay = currentDisplay;
    }

    private void resetLegendAndPricingPanel() {

        Point legendScrollPosition = legendDisplay.getViewport().getViewPosition();
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane pricingDisplay = getPricingDisplay();

        legendDisplay = getLegendPane();
        panel.add(legendDisplay, BorderLayout.WEST);
        panel.add(pricingDisplay, BorderLayout.CENTER);
        changeDisplay(panel, Display.PRICING);
        totalMobsTextField.requestFocus();
        legendDisplay.getViewport().setViewPosition(legendScrollPosition);
    }

    private void enableIterationButtons() {
        if (filteredJobList.size() == 1) {

            previousJob.setEnabled(false);
            nextJob.setEnabled(false);
        }

        if (jobIndex == 0) {

            previousJob.setEnabled(false);
            nextJob.setEnabled(true);
        }
        if (jobIndex == filteredJobList.size() - 1) {

            previousJob.setEnabled(true);
            nextJob.setEnabled(false);
        }
        if (jobIndex > 0 && jobIndex < filteredJobList.size() - 1) {

            previousJob.setEnabled(true);
            nextJob.setEnabled(true);
        }
    }

    // ===========================================================================
    // Startup Display
    // ===========================================================================

    private JPanel getStartupDisplay() {

        return new JPanel() {
            {
                add(new JLabel("BIDDING PROGRAM") {
                    {
                        setFont(TITLE_FONT);
                        setForeground(TITLE_FONT_COLOR);
                        setHorizontalAlignment(JLabel.CENTER);
                        setVerticalAlignment(JLabel.BOTTOM);
                    }
                });
                add(new JLabel("Click \"Open Bidding File\" To Get Started") {
                    {
                        setFont(FONT);
                        setForeground(SECONDARY_FONT_COLOR);
                        setHorizontalAlignment(JLabel.CENTER);
                        setVerticalAlignment(JLabel.TOP);
                    }
                });
                setLayout(new GridLayout(2, 0));
                setBackground(SECONDARY_BACKGROUND);
            }
        };
    }

    // ===========================================================================
    // Unfiltered Display
    // ===========================================================================

    public JScrollPane getUnfilteredDisplay() {

        JScrollPane unfilteredDisplayPane = new JScrollPane();
        unfilteredDisplayPane.getVerticalScrollBar().setUnitIncrement(16);

        GridBagConstraints displayConstraints = new GridBagConstraints();
        JPanel unfilteredDisplay = new JPanel(new GridBagLayout());

        JLabel label = new JLabel(String.format("(00 selected) %-68s", "")) {
            {
                setHorizontalAlignment(0);
                setFont(FONT);
                setForeground(QUATERNARY_FONT_COLOR);
            }
        };

        jobCheckBoxes = new ArrayList<JCheckBox>();

        final ItemListener checkAllListener = new ItemListener() {

            public void itemStateChanged(ItemEvent event) {

                if (event.getStateChange() == ItemEvent.SELECTED) {

                    jobCheckBoxes.forEach(checkBox -> checkBox.setSelected(true));
                } else {

                    jobCheckBoxes.forEach(checkBox -> checkBox.setSelected(false));
                }
            }
        };

        final JCheckBox checkAll = new JCheckBox(" ALL ") {
            {
                setFont(FONT);
                setBackground(SECONDARY_BACKGROUND);
                setForeground(QUATERNARY_FONT_COLOR);
                addItemListener(checkAllListener);
            }
        };

        final ItemListener jobCheckBoxListener = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateScrollPaneHeader(label);
                }
                if (e.getStateChange() == ItemEvent.DESELECTED) {

                    updateScrollPaneHeader(label);
                    checkAll.removeItemListener(checkAllListener);
                    checkAll.setSelected(false);
                    checkAll.addItemListener(checkAllListener);
                }
            }
        };

        int lineItemCount = 0;

        unfilteredDisplay = new JPanel(new GridBagLayout());
        unfilteredDisplay.setBackground(SECONDARY_BACKGROUND);
        // viewportContainer.add(unfilteredDisplay, BorderLayout.NORTH);

        unfilteredDisplayPane.setViewportView(unfilteredDisplay);

        // Top Heading =============
        displayConstraints.gridx = 1;
        displayConstraints.gridy = 0;
        unfilteredDisplay.add(new JLabel() {
            {
                setText(String.format("%-20s%-20s%-20s%16s", "CSJ", "County", "Highway", "Total Quantities"));
                setFont(FONT);
                setBackground(SECONDARY_BACKGROUND);
                setForeground(TERTIARY_FONT_COLOR);
            }
        }, displayConstraints);

        // for each job
        for (int index = 0; index < currentJobList.size(); index++) {

            Job currentJob = currentJobList.get(index);

            // adds job check boxes into list here
            jobCheckBoxes.add(new JCheckBox(String.format("  %2d:", index + 1)) {
                {
                    setFont(FONT);
                    setBackground(SECONDARY_BACKGROUND);
                    setForeground(QUATERNARY_FONT_COLOR);
                    addItemListener(jobCheckBoxListener);
                }
            });
            displayConstraints.gridx = 0;
            displayConstraints.gridy = 0;
            unfilteredDisplay.add(checkAll, displayConstraints);
            displayConstraints.gridx = 0;
            displayConstraints.gridy = index + lineItemCount + 1;

            // puts checkbox on display
            unfilteredDisplay.add(jobCheckBoxes.get(index), displayConstraints);

            // displays job heading
            displayConstraints.gridx = 1;
            displayConstraints.gridy = index + lineItemCount + 1;
            unfilteredDisplay.add(new JLabel(String.format("%n%-20s%-20s%-20s     %,11.2f",
                    currentJob.getCsj(),
                    currentJob.getCounty(),
                    currentJob.getHighway(),
                    currentJob.getSumOfQuantities())) {
                {
                    setFont(new Font("Monospaced", Font.BOLD, 16));
                    setForeground(SECONDARY_FONT_COLOR);
                }
            }, displayConstraints);

            // displays line items
            for (LineItem lineItem : currentJob.getLineItems()) {
                lineItemCount++;
                displayConstraints.gridx = 1;
                displayConstraints.gridy = index + lineItemCount + 1;
                unfilteredDisplay.add(new JLabel(
                        String.format("%-40s     %,10.2f%19s", lineItem.getDescription(), lineItem.getQuantity(), "")) {
                    {
                        setFont(FONT);
                        setForeground(PRIMARY_FONT_COLOR);
                    }
                }, displayConstraints);

            }
        }

        JPanel columnHeader = new JPanel();
        columnHeader.setBackground(SECONDARY_BACKGROUND);
        columnHeader.add(label);
        unfilteredDisplayPane.setColumnHeaderView(columnHeader);

        if (currentJobFilterIndexes.size() != 0) { // if there were previous filters used, match the checkboxes to that
                                                   // filter

            for (Integer integer : currentJobFilterIndexes) {

                jobCheckBoxes.get(integer).setSelected(true);
            }
        }
        return unfilteredDisplayPane;
    }

    private void updateScrollPaneHeader(JLabel label) {

        StringBuilder labelString = new StringBuilder();
        Job bufferJob;
        String bufferCounty, bufferCsj;
        Integer count = 0;

        for (int index = 0; index < jobCheckBoxes.size(); index++) {

            if (jobCheckBoxes.get(index).isSelected()) {

                count++;
                bufferJob = currentJobList.get(index);
                bufferCounty = bufferJob.getCounty();
                bufferCsj = bufferJob.getCsj();

                labelString.append(bufferCounty.substring(0, 3));
                labelString.append(" ");
                labelString.append(bufferCsj.substring(bufferCsj.length() - 3));
                labelString.append("|");

                if (count % 10 == 0) {
                    labelString.append("<br>");
                }
            }
        }

        labelString.insert(0, String.format("<html>(%02d)", count));
        labelString.append("</html>");
        label.setText(labelString.toString());
    }

    // ===========================================================================
    // Filtered Display
    // ===========================================================================

    private JScrollPane getFilteredDisplay() {

        JScrollPane filteredDisplayPane = new JScrollPane();
        filteredDisplayPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel filteredDisplay = new JPanel(new GridBagLayout());
        GridBagConstraints filteredDisplayConstraints = new GridBagConstraints();

        filteredDisplay.setBackground(SECONDARY_BACKGROUND);
        filteredDisplayPane.setViewportView(filteredDisplay);

        filteredDisplayConstraints.gridx = 0;
        filteredDisplayConstraints.gridy = 0;

        filteredDisplay.add(new JLabel() {
            {
                setText(String.format("%-20s%-20s%-20s%16s", "CSJ", "County", "Highway", "Total Quantities"));
                setFont(FONT);
                setBackground(SECONDARY_BACKGROUND);
                setForeground(TERTIARY_FONT_COLOR);
            }
        }, filteredDisplayConstraints);

        int filteredLineItemCount = 0;
        for (int jobCount = 0; jobCount < filteredJobList.size(); jobCount++) {

            Job currentJob = filteredJobList.get(jobCount);

            filteredDisplayConstraints.gridy = jobCount + filteredLineItemCount + 1;
            filteredDisplay.add(new JLabel(String.format("%n%-20s%-20s%-20s     %,11.2f",
                    currentJob.getCsj(),
                    currentJob.getCounty(),
                    currentJob.getHighway(),
                    currentJob.getSumOfQuantities())) {
                {
                    setFont(new Font("Monospaced", Font.BOLD, 16));
                    setForeground(SECONDARY_FONT_COLOR);
                }
            }, filteredDisplayConstraints);

            for (LineItem lineItem : currentJob.getLineItems()) {
                filteredLineItemCount++;
                filteredDisplayConstraints.gridy = jobCount + filteredLineItemCount + 1;
                filteredDisplay.add(new JLabel(
                        String.format("%-40s     %,10.2f%19s", lineItem.getDescription(), lineItem.getQuantity(), "")) {
                    {
                        setFont(FONT);
                        setForeground(PRIMARY_FONT_COLOR);
                    }
                }, filteredDisplayConstraints);
            }
        }
        return filteredDisplayPane;
    }

    // ===========================================================================
    // Pricing Display
    // ===========================================================================

    public JScrollPane getLegendPane() {

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new GridBagLayout());
        legendPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        legendPanel.setBackground(PRIMARY_BACKGROUND);

        legendDisplay = new JScrollPane();
        legendDisplay.setBorder(new EmptyBorder(0, 0, 0, 0));
        legendDisplay.getVerticalScrollBar().setUnitIncrement(16);

        JLabel rightLocatorLabel = new JLabel("<") {
            {

                setForeground(SECONDARY_FONT_COLOR);
            }
        };
        JLabel leftLocatorLabel = new JLabel(">") {
            {

                setForeground(SECONDARY_FONT_COLOR);
            }
        };

        jobButtons = new ArrayList<JButton>();
        for (int jobIndexForLegendButtons = 0; jobIndexForLegendButtons < filteredJobList
                .size(); jobIndexForLegendButtons++) {

            // create Job and Index object for button creation
            Job job = filteredJobList.get(jobIndexForLegendButtons);
            final int JOB_INDEX = jobIndexForLegendButtons;

            // create county buffer and take off possible ", ETC"
            String bufferCounty = job.getCounty();
            if (bufferCounty.length() > 5 && bufferCounty.substring(bufferCounty.length() - 5).equals(", ETC"))
                bufferCounty = bufferCounty.substring(0, bufferCounty.length() - 5);

            final String CSJ = job.getCsj().substring(8, 11);
            final String county = bufferCounty;

            jobButtons.add(new JButton() {
                {
                    setText(String.format("%13s %s", county, CSJ));
                    setFont(FONT);
                    setForeground(TERTIARY_FONT_COLOR);

                    addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {

                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {

                                    setPrices();
                                    jobIndex = JOB_INDEX;
                                    legendAutoFollow(legendDisplay);
                                    resetLegendAndPricingPanel();

                                    enableIterationButtons();
                                }
                            });
                        }
                    });
                }
            });

            GridBagConstraints legendConstraints = new GridBagConstraints();

            legendPanel.remove(leftLocatorLabel);
            legendConstraints.gridx = 0;
            legendConstraints.gridy = jobIndex;
            legendPanel.add(leftLocatorLabel, legendConstraints);

            legendPanel.remove(rightLocatorLabel);
            legendConstraints.gridx = 2;
            legendConstraints.gridy = jobIndex;
            legendPanel.add(rightLocatorLabel, legendConstraints);

            legendConstraints.gridx = 1;

            for (int buttonIndex = 0; buttonIndex < jobButtons.size(); buttonIndex++) {

                legendConstraints.gridy = buttonIndex;
                legendPanel.add(jobButtons.get(buttonIndex), legendConstraints);
            }
        }
        legendDisplay.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        legendDisplay.setViewportView(legendPanel);
        Dimension preferredSize = legendPanel.getPreferredSize();
        JViewport viewport = legendDisplay.getViewport();
        viewport.setPreferredSize(new Dimension(preferredSize.width, viewport.getHeight()));

        return legendDisplay;
    }

    public JScrollPane getPricingDisplay() {

        JScrollPane pricingDisplayPane = new JScrollPane();
        TexasCityFinder cityFinder = new TexasCityFinder();

        GridBagConstraints displayPricingConstraints = new GridBagConstraints();
        currentJob.setText(String.format("(%02d/%02d)", jobIndex + 1,
                filteredJobList.size()));
        final Job currentJob = filteredJobList.get(jobIndex);
        pricingDisplayPane.setColumnHeaderView(new JPanel() {
            {
                setLayout(new FlowLayout(FlowLayout.LEFT));
                setBackground(SECONDARY_BACKGROUND);
                add(new JLabel() {
                    {
                        setText(String.format("%-20s%-20s%-20s%-20s", currentJob.getCsj(),
                                currentJob.getCounty(),
                                currentJob.getHighway(),
                                "county's largest city:  ".concat(cityFinder.getLargestCity(currentJob.getCounty()))));
                        setFont(FONT);
                        setForeground(TERTIARY_FONT_COLOR);
                    }
                });
            }
        });

        JPanel pricingDisplay = new JPanel();
        pricingDisplayPane.setViewportView(pricingDisplay);
        pricingDisplay.setLayout(new GridBagLayout());
        pricingDisplay.setBackground(SECONDARY_BACKGROUND);

        displayPricingConstraints.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
        displayPricingConstraints.ipadx = 10;
        displayPricingConstraints.ipady = 10;

        addTotalMobsToPricingPage(pricingDisplay, displayPricingConstraints);
        addUpTo_MobsToPricingPage(pricingDisplay, displayPricingConstraints);
        addAdditionalMobsToPricingPage(pricingDisplay, displayPricingConstraints);
        addLineItemsToPricingPage(pricingDisplay, displayPricingConstraints);

        totalMobsTextField.requestFocus();
        return pricingDisplayPane;
    }

    public void addTotalMobsToPricingPage(JPanel pricingDisplay, GridBagConstraints displayPricingConstraints) {

        displayPricingConstraints.gridy = 1;

        displayPricingConstraints.gridx = 0;
        pricingDisplay.add(new JLabel("Total Mobilizations Price?  ") {
            {
                setFont(FONT);
                setForeground(PRIMARY_FONT_COLOR);
            }
        }, displayPricingConstraints);

        displayPricingConstraints.gridx = 1;
        pricingDisplay.add(new JLabel("$") {
            {
                setFont(FONT);
                setForeground(QUATERNARY_FONT_COLOR);
            }
        }, displayPricingConstraints);

        displayPricingConstraints.gridx = 2;
        totalMobsTextField = new JTextField();
        totalMobsTextField.setText(String.format("%.0f",
                filteredJobList.get(jobIndex).getTotalMobs()));

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

    public void addUpTo_MobsToPricingPage(JPanel pricingDisplay, GridBagConstraints displayPricingConstraints) {

        displayPricingConstraints.gridy = 2;

        displayPricingConstraints.gridx = 0;
        pricingDisplay.add(new JLabel("Amount of mobilizations included?  ") {
            {
                setFont(FONT);
                setForeground(PRIMARY_FONT_COLOR);
            }
        }, displayPricingConstraints);

        displayPricingConstraints.gridx = 2;
        upToMobsTextField = new JTextField();
        upToMobsTextField.setText(String.format("%d",
                filteredJobList.get(jobIndex).getUpTo_Mobs()));

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

        displayPricingConstraints.gridy = 3;

        displayPricingConstraints.gridx = 0;
        pricingDisplay.add(new JLabel("Additional Mobilization Price?  ") {
            {
                setFont(FONT);
                setForeground(PRIMARY_FONT_COLOR);
            }
        }, displayPricingConstraints);

        displayPricingConstraints.gridx = 1;
        pricingDisplay.add(new JLabel("$") {
            {
                setFont(FONT);
                setForeground(QUATERNARY_FONT_COLOR);
            }
        }, displayPricingConstraints);

        displayPricingConstraints.gridx = 2;
        additionalMobsTextField = new JTextField();
        additionalMobsTextField.setText(String.format("%.0f",
                filteredJobList.get(jobIndex).getAdditionalMobs()));

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

    public void addLineItemsToPricingPage(JPanel pricingDisplay, GridBagConstraints displayPricingConstraints) {

        lineItemPrices.clear();
        for (int index = 0; index < filteredJobList.get(jobIndex).getLineItems().size(); index++) {

            final int indexForActionListener = index;
            displayPricingConstraints.gridx = 0;
            displayPricingConstraints.gridy = index + 4;

            pricingDisplay.add(new JLabel(String.format("%-40s%s%,12.2f%s",
                    filteredJobList.get(jobIndex).getLineItems().get(index).getDescription(),
                    "    Quantity: ",
                    filteredJobList.get(jobIndex).getLineItems().get(index).getQuantity(),
                    " (Sq. Yds.)")) {
                {
                    setFont(FONT);
                    setForeground(PRIMARY_FONT_COLOR);
                }
            },
                    displayPricingConstraints);
            lineItemPrices.add(new JTextField());
            lineItemPrices.get(index).setPreferredSize(new Dimension(50, 20));
            lineItemPrices.get(index).setText(String.format("%1.2f",
                    filteredJobList.get(jobIndex).getLineItems().get(index).getPrice()));

            lineItemPrices.get(index).addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent e) {

                    lineItemPrices.get(indexForActionListener).selectAll();
                }

                public void focusLost(FocusEvent e) {

                }

            });

            displayPricingConstraints.gridx = 1;
            displayPricingConstraints.gridy = index + 4;

            pricingDisplay.add(new JLabel("$") {
                {
                    setFont(FONT);
                    setForeground(QUATERNARY_FONT_COLOR);
                }
            }, displayPricingConstraints);
            displayPricingConstraints.gridx = 2;

            pricingDisplay.add(lineItemPrices.get(index), displayPricingConstraints);

            displayPricingConstraints.gridx = 3;
            pricingDisplay.add(new JLabel(" (per Sq. Yd.)") {
                {
                    setFont(FONT);
                    setForeground(QUATERNARY_FONT_COLOR);
                }
            }, displayPricingConstraints);
        }
    }

    public void setPrices() {

        filteredJobList.get(jobIndex).setTotalMobs(new BigDecimal(totalMobsTextField.getText()));
        filteredJobList.get(jobIndex).setUpTo_Mobs(Integer.valueOf(upToMobsTextField.getText()));
        filteredJobList.get(jobIndex).setAdditionalMobs(new BigDecimal(additionalMobsTextField.getText()));

        for (int index = 0; index < lineItemPrices.size(); index++) {
            filteredJobList
                    .get(jobIndex)
                    .getLineItems()
                    .get(index)
                    .setPrice(new BigDecimal(lineItemPrices.get(index).getText()));
        }
        lineItemPrices.clear();
    }

    public void legendAutoFollow(JScrollPane scrollPane) {

        int heightOfButton = jobButtons.get(jobIndex).getHeight();
        int currentButton = jobIndex + 1;
        int currentPosition = (int) scrollPane.getViewport().getViewRect().getY();
        int heightOfSlider = (int) scrollPane.getViewport().getViewRect().getHeight();

        if (currentButton * heightOfButton > currentPosition + heightOfSlider)

            scrollPane.getViewport().setViewPosition(new Point(0, currentButton * heightOfButton - heightOfSlider));

        if (currentButton * heightOfButton - heightOfButton < currentPosition)

            scrollPane.getViewport().setViewPosition(new Point(0, currentButton * heightOfButton - heightOfButton));

    }

    // ===========================================================================
    // "Update Job Data" Display
    // ===========================================================================

    // private ArrayList<Job> filterUpdatedJobs(ParseFullDoc updatedDoc) {

    // ArrayList<Job> filteredUpdatedJobs = new ArrayList<Job>();

    // // for each updated job...
    // for (Job updatedJob : updatedDoc.getJobList()) {

    // // check each old job
    // for (Job oldJob : currentJobList) {

    // // and if the CSJ's match
    // if (oldJob.getCsj().equals(updatedJob.getCsj())) {

    // // add the updated job to the list
    // filteredUpdatedJobs.add(updatedJob);
    // break;
    // }
    // }

    // // stop once the count of jobs is correct
    // if (filteredUpdatedJobs.size() == currentJobList.size())
    // break;
    // }
    // return filteredUpdatedJobs;
    // }

    // private void displayUpdateInfoFrame(ParseFullDoc updatedDoc) { TODO

    // // set up the frame
    // JFrame updateInfoFrame = new JFrame() {
    // {
    // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    // setTitle("Update Information");
    // getContentPane().setLayout(new GridBagLayout());
    // setSize(1250, 600);

    // }
    // };
    // List<Job> oldJobs = currentJobList;// TODO
    // List<Job> newJobs = filterUpdatedJobs(updatedDoc);

    // JList<String> oldJobList = new JList<String>(
    // getInfoList(JOBSET.OLD, oldJobs, newJobs).toArray(new String[] {}));
    // oldJobList.setBorder(new EmptyBorder(20, 50, 10, 0));
    // oldJobList.setFont(FONT);
    // oldJobList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    // JList<String> newJobList = new JList<String>(
    // getInfoList(JOBSET.NEW, oldJobs, newJobs).toArray(new String[] {}));
    // newJobList.setBorder(new EmptyBorder(20, 0, 10, 50));
    // newJobList.setFont(FONT);
    // newJobList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    // JScrollPane infoScrollPane = new JScrollPane();
    // infoScrollPane.getVerticalScrollBar().setUnitIncrement(16);

    // JPanel infoPanel = new JPanel(new GridLayout(0, 2));

    // infoPanel.add(oldJobList);
    // infoPanel.add(newJobList);

    // GridBagConstraints infoFrameConstraints = new GridBagConstraints();
    // infoFrameConstraints.gridx = 0;
    // infoFrameConstraints.gridy = 0;
    // infoFrameConstraints.fill = GridBagConstraints.BOTH; // Fill both
    // horizontally and vertically
    // infoFrameConstraints.weightx = 1.0; // Expand horizontally
    // infoFrameConstraints.weighty = 1.0; // Expand vertically

    // infoScrollPane.setViewportView(infoPanel);
    // updateInfoFrame.add(infoScrollPane, infoFrameConstraints);
    // updateInfoFrame.setVisible(true);
    // }

    // private List<String> getInfoList(JOBSET whichJobset, List<Job> oldJobs,
    // List<Job> newJobs) { TODO

    // List<Job> chosenJobSet = whichJobset == JOBSET.OLD ? oldJobs : newJobs;
    // List<String> outputList = new ArrayList<String>();

    // StringBuilder buffer = new StringBuilder();
    // int maxContractors, contractorCount;

    // // for every Job
    // for (int jobIndex = 0; jobIndex < chosenJobSet.size(); jobIndex++) {

    // // find the count of the larger contractor count between old and new
    // maxContractors = Math.max(oldJobs.get(jobIndex).getContractorList().size(),
    // newJobs.get(jobIndex).getContractorList().size());

    // contractorCount = chosenJobSet.get(jobIndex).getContractorList().size();

    // // add job info to buffer
    // buffer.append("<html>");
    // buffer.append(String.format("%-20s%-20s%s",
    // chosenJobSet.get(jobIndex).getCsj(),
    // chosenJobSet.get(jobIndex).getCounty(), "<br>"));

    // // for each contractor
    // for (int contractorIndex = 0; contractorIndex < maxContractors;
    // contractorIndex++) {

    // // add each contractor,
    // buffer.append(String.format("%s<br>",
    // contractorCount > contractorIndex ? chosenJobSet
    // .get(jobIndex).getContractorList().get(contractorIndex).getContractorName()
    // : "-----"));
    // }
    // buffer.append("=".repeat(58));
    // buffer.append("</html>");
    // outputList.add(buffer.toString());
    // buffer.setLength(0);
    // }
    // return outputList;
    // }

    // ====================================================================================================
    // Other Methods
    // ====================================================================================================

    public void populateExcel(ExcelManager excelManager, Job job, int estimateNumber) {

        String sheetName;
        Contractor contractor;
        LineItem lineItem;
        BigDecimal lineItemAmount;
        BigDecimal totalAmount = new BigDecimal(0);

        for (int contractorIndex = 0; contractorIndex < job.getContractorList().size(); contractorIndex++) {

            totalAmount = new BigDecimal(0);

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

            totalAmount.add(job.getTotalMobs());
            excelManager.setCellValue(sheetName, 0, 18, contractor.getContractorName());
            excelManager.setCellValue(sheetName, 0, 24, contractor.getContractorEmail());
            excelManager.setCellValue(sheetName, 4, 3, contractor.getContractorEmail());
            excelManager.setCellValue(sheetName, 0, 22, contractor.getContractorPhoneNumber());

            for (int lineItemIndex = 0; lineItemIndex < job.getLineItems().size(); lineItemIndex++) {

                lineItem = job.getLineItems().get(lineItemIndex);
                lineItemAmount = lineItem.getQuantity().multiply(lineItem.getPrice());
                totalAmount.add(lineItemAmount);

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

    public boolean isPricingValid() {

        String invalidInput = checkTextField(totalMobsTextField);
        boolean valid = true;

        if (invalidInput != null) {

            showWarning("Warning", "Invalid input", invalidInput + " is not a valid number for total mobilizations");
            valid = false;
        }

        invalidInput = checkTextFields(lineItemPrices);

        if (invalidInput != null) {

            showWarning("Warning", "Invalid input",
                    String.format("\"%s\" is not a valid number for line item price", invalidInput));
            valid = false;
        }
        return valid;
    }

    public String checkTextFields(ArrayList<JTextField> textFields) {

        for (JTextField textField : textFields) {

            try {

                new BigDecimal(textField.getText());
            } catch (NumberFormatException | NullPointerException e) {

                return textField.getText();
            }
        }
        return null;
    }

    public String checkTextField(JTextField textField) {

        try {

            new BigDecimal(textField.getText());
        } catch (NumberFormatException | NullPointerException e) {

            return textField.getText();
        }
        return null;
    }
}
