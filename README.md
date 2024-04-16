This program is used for the production of bid proposals for the company that uses it. It lets you choose files that either have TxDot formatting or the program's outputted formats. You then can use the GUI to filter contracts and add company chosen prices to any line items for each contract. You can then save any work you have done using the program's formats. Once the pricing is confirmed, you can then export that data to an excel file template where the company can then create pdf's, using the template's macro, of each contractor that bid on each contract. This is currently set up to use my current employer's bid proposal format that was given to me in excel. Currently, the TxDot input files are obtained by a third party program called "Easy Link Pro", by Whitley Siddons website, which obtains the files from the TxDot website. This third party program allows filtering of individual line items for construction companies that specialize in certain kinds of work (i.e., Milling, Asphalt, Concrete). 

Here is an outline of the classes used, separated by their relative functions

Main Logic + Main FXML Controller
	App.java

FXML Controllers
	StartupDisplayController.java
	UnfilteredDisplayController.java
	UpdateInfoDisplayController.java
	FilteredDisplayController.java
	PricingDisplayController.java

Input/Output File Formats
	InputFileProcessor.java
	FormatInterface.java
	Format.java
	CombinedFormat.java
	V1Format.java
	V2Format.java
	ClearTextFormat.java
	EmailFormat.java
	
I/O Managers	
	JSON_Manager.java
	FileManager.java

Export File Formats
	ExcelFormatInterface.java
	ExcelFormat.java
	V1ExcelFormat.java
	V2ExcelFormat.java

Data Formats
	Job.java
	Contractor.java
	LineItem.java
	Themes.java

Data Storage
	ContractorStorage.java
	Preferences.java

Utility Classes
	BigDecimalToWordsConverter.java
	TexasCityFinder.java