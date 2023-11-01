import java.util.regex.Pattern;
import java.util.Objects;
import java.util.regex.Matcher;

public class Contractor implements Comparable<Contractor> {

    private String contractorName;
    private String contractorPhoneNumber;
    private String contractorEmail;
    private String contractorEstimateNumber;

    // used by CombinedFormat
    public Contractor(String infoLine) {

        setContractorName(findContractorName(infoLine)); // sets name from a trimmed substring of infoLine
        setContractorPhoneNumber(findContractorPhoneNumber(infoLine)); // sets phone number from a regex of infoLine
        setContractorEmail(findContractorEmail(infoLine)); // sets email from a regex search of infoLine
    }

    // used by ContractorStorage and V1Format
    public Contractor(String contractorName, String contractorPhoneNumber, String contractorEmail) {

        this.contractorName = contractorName;
        this.contractorPhoneNumber = contractorPhoneNumber;
        this.contractorEmail = contractorEmail;
    }

    // used by V2Format
    public Contractor(String contractorName, String contractorPhoneNumber, String contractorEmail,
            String contractorEstimateNumber) {

        this.contractorName = contractorName;
        this.contractorPhoneNumber = contractorPhoneNumber;
        this.contractorEmail = contractorEmail;
        // System.out.println(contractorEmailLookup(contractorEmail));
        // setContractorEmail(); // sets email from a regex search of infoLine
        this.contractorEstimateNumber = contractorEstimateNumber;
    }

    // ====================================================================================================
    // Parsing Data
    // ====================================================================================================

    // finds the name of the contractor, by taking a substring and gets rid of the
    // trailing '.'s
    public String findContractorName(String infoLine) {

        String nameLine = infoLine.substring(0, 35); // creates the substring from the first 34 characters
        String name = nameLine.replaceAll("\\.{2,}", ""); // deletes the trailing '.'s
        return name;
    }

    // uses regex to find email from infoLine
    private String findContractorEmail(String infoLine) {

        String email = ""; // buffer
        Matcher emailMatcher = Pattern.compile("[A-Za-z0-9\\.]+@[A-Za-z-]+\\.[A-Za-z]+").matcher(infoLine); // uses
                                                                                                            // Pattern/Matcher
                                                                                                            // to search
                                                                                                            // through
                                                                                                            // the
                                                                                                            // string
                                                                                                            // using
                                                                                                            // regex.

        // if regex finds the email, assign it to the buffer
        if (emailMatcher.find()) {
            email = emailMatcher.group(); // assign found email to the buffer
            return email;
        }
        ContractorStorage storage = new ContractorStorage();
        return storage.getEmail(contractorName); // returns a message if no email is found
    }

    // uses regex to find phone number from infoLine
    private String findContractorPhoneNumber(String infoLine) {

        String phone = ""; // buffer
        Matcher phoneNumberMatcher = Pattern.compile("\\(*\\d{3}\\)*.{8}").matcher(infoLine); // uses Pattern/Matcher to
                                                                                              // search through the
                                                                                              // string using regex.

        // if regex finds the phone number, assign it to the buffer
        if (phoneNumberMatcher.find()) {

            phone = phoneNumberMatcher.group(); // assign found phone number to the buffer
            return phone;
        }
        return "==No Phone Found=="; // returns a message if no phone number is found
    }

    // ====================================================================================================
    // Outputting Data
    // ====================================================================================================

    public String toString() {

        return String.format("%-40s%-45s%40s%n", getContractorName(), getContractorPhoneNumber(), getContractorEmail());
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

    public void setContractorEstimateNo(String contractorEstimateNumber) {

        this.contractorEstimateNumber = contractorEstimateNumber;
    }

    public String getContractorEstimateNo() {

        return contractorEstimateNumber;
    }

    @Override
    public int compareTo(Contractor other) {

        return this.contractorName.compareTo(other.contractorName);
    }

    public boolean equals(Object obj) {

        if (this == obj) {

            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {

            return false;
        }

        Contractor otherContractor = (Contractor) obj;

        return Objects.equals(contractorName, otherContractor.contractorName) &&
                Objects.equals(contractorPhoneNumber, otherContractor.contractorPhoneNumber) &&
                Objects.equals(contractorEmail, otherContractor.contractorEmail) &&
                Objects.equals(contractorEstimateNumber, otherContractor.contractorEstimateNumber);
    }
}