import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Contractor {

	private String contractorName;
	private String contractorPhoneNumber;
	private String contractorEmail;

	public Contractor(String infoLine) {

//		System.out.println(infoLine);
		setContractorName(findContractorName(infoLine)); // sets name from a trimmed substring of infoLine
		setContractorPhoneNumber(findContractorPhoneNumber(infoLine)); // sets phone number from a regex of infoLine
		setContractorEmail(findContractorEmail(infoLine)); // sets email from a regex search of infoLine
	}
	
	public Contractor(String contractorName, String contractorPhoneNumber, String contractorEmail) {
		this.contractorName = contractorName;
		this.contractorPhoneNumber = contractorPhoneNumber;
		this.contractorEmail = contractorEmail;
	}

	// ====================================================================================================
	// Parsing Data
	// ====================================================================================================

	// finds the name of the contractor, by taking a substring and gets rid of the trailing '.'s
	public String findContractorName(String infoLine) {

		String nameLine = infoLine.substring(0, 35); // creates the substring from the first 34 characters
		String name = nameLine.replaceAll("\\.{2,}", ""); // deletes the trailing '.'s
		return name;
	}

	// uses regex to find email from infoLine
	private String findContractorEmail(String infoLine) {

		String email = ""; // buffer
		Matcher emailMatcher = Pattern.compile("[A-Za-z0-9\\.]+@[A-Za-z-]+\\.[A-Za-z]+").matcher(infoLine); // uses Pattern/Matcher to search through the string using regex.
		
		// if regex finds the email, assign it to the buffer
		if (emailMatcher.find()) {
			email = emailMatcher.group(); // assign found email to the buffer
			return email;
		}
		return "=============No Email Found============="; // returns a message if no email is found
	}

	// uses regex to find phone number from infoLine
	private String findContractorPhoneNumber(String infoLine) {

		String phone = ""; // buffer
		Matcher phoneNumberMatcher = Pattern.compile("\\(*\\d{3}\\)*.{8}").matcher(infoLine); // uses Pattern/Matcher to search through the string using regex.
		
		// if regex finds the phone number, assign it to the buffer
		if (phoneNumberMatcher.find()) {
			
			phone = phoneNumberMatcher.group(); // assign found phone number to the buffer
			return phone;
		}
		return "=============No Phone Found============="; // returns a message if no phone number is found
	}
	
	// ====================================================================================================
	// Outputting Data
	// ====================================================================================================

	public void printContractorInfo() {
		
		System.out.printf("%-40s%-45s%40s%n", getContractorName(), getContractorPhoneNumber(), getContractorEmail());
	}

	public String formatContractorInfo() {

		return String.format("|%s|%s|%s", getContractorName(), getContractorPhoneNumber(), getContractorEmail());
	}
	
	// ====================================================================================================
	// Getter Setters
	// ====================================================================================================

	public String getContractorName() {
		
		return contractorName;
	}

	public void setContractorName(String contractorName) {
		
		this.contractorName = contractorName;
	}

	public String getContractorPhoneNumber() {
		
		return contractorPhoneNumber;
	}

	public void setContractorPhoneNumber(String contractorPhoneNumber) {
		
		this.contractorPhoneNumber = contractorPhoneNumber;
	}

	public String getContractorEmail() {
		
		return contractorEmail;
	}

	public void setContractorEmail(String contractorEmail) {
		
		this.contractorEmail = contractorEmail;
	}

}