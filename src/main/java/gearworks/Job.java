package gearworks;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Job {

    private List<Contractor> contractorList = new ArrayList<Contractor>();
    private List<LineItem> lineItems = new ArrayList<LineItem>();
    private String county, highway, csj;
    private Date biddingDate = new Date(946684800000L); // January 1, 2000, 00:00:00 UTC == default case
    private int workingDays = 0, upTo_Mobs = 1;
    private BigDecimal totalMobs = new BigDecimal(0),
            additionalMobs = new BigDecimal(0),
            standbyPrice = new BigDecimal(0),
            minimumDayCharge = new BigDecimal(0);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Adjust the format as needed

    // used by CombinedFormat
    public Job(String county, String highway, String csj, int workingDays, Date biddingDate, List<LineItem> lineItems,
            List<Contractor> contractorList) {

        this.county = county;
        this.highway = highway;
        this.csj = csj;
        this.workingDays = workingDays;
        this.biddingDate = biddingDate;
        this.lineItems = lineItems;
        this.contractorList = contractorList;
        removeBlacklistedContractors();
    }

    // used by V1Format
    public Job(String county, String highway, String csj, int workingDays, int upTo_Mobs, BigDecimal totalMobs,
            BigDecimal additionalMobs, List<LineItem> lineItems, List<Contractor> contractorList) {

        this.county = county;
        this.highway = highway;
        this.csj = csj;
        this.workingDays = workingDays;
        this.upTo_Mobs = upTo_Mobs;
        this.totalMobs = totalMobs;
        this.additionalMobs = additionalMobs;
        this.lineItems = lineItems;
        this.contractorList = contractorList;
    }

    // used by V2Format
    public Job(String county, String highway, String csj, int workingDays, int upTo_Mobs, BigDecimal totalMobs,
            BigDecimal additionalMobs, Date biddingDate, List<LineItem> lineItems, List<Contractor> contractorList) {

        this.county = county;
        this.highway = highway;
        this.csj = csj;
        this.workingDays = workingDays;
        this.upTo_Mobs = upTo_Mobs;
        this.totalMobs = totalMobs;
        this.additionalMobs = additionalMobs;
        this.biddingDate = biddingDate;
        this.lineItems = lineItems;
        this.contractorList = contractorList;
    }

    // testing purposes only
    public Job() {

    }

    // ====================================================================================================
    // Blacklisting
    // ====================================================================================================

    // removes blacklisted contractors i.e. Lone Star, Texas Materials
    public void removeBlacklistedContractors() {

        List<String> blacklist = new ArrayList<String>();

        // populate the blacklist ArrayList with names
        blacklist.add("Angel Brothers");
        blacklist.add("AUSTIN BRIDGE & ROAD SERVICES, LP");
        blacklist.add("LONE STAR PAVING COMPANY");
        blacklist.add("TEXAS MATERIALS GROUP, INC");

        // for every contractor in the ArrayList, compare the name to the blacklisted
        // names
        List<Contractor> contractorsToRemove = contractorList.stream()
                .filter(contractor -> blacklist.stream()
                        .anyMatch(blacklistName -> contractor.getContractorName().equalsIgnoreCase(blacklistName)))
                .collect(Collectors.toList());

        // removes blacklisted contractors from contractorList
        contractorsToRemove.forEach(contractor -> contractorList.remove(contractor));
    }

    // ====================================================================================================
    // Outputting Data
    // ====================================================================================================

    public void printJobInfo() {

        System.out.println("County:		      " + getCounty());
        System.out.println("Highway:	      " + getHighway());
        System.out.println("CSJ:		      " + getCsj());
        System.out.println("Working Days:     " + getWorkingDays());
        System.out.printf("Up to %d Mobs%n", getUpTo_Mobs());
        System.out.println("Total Mobs:       " + getTotalMobs());
        System.out.println("Additional Mobs:  " + getAdditionalMobs());

        if (!getLineItems().isEmpty()) {
            System.out.printf("%n%-40s%-12s%8s%n", "Item Description:", "Quantities:", "Price:");
            System.out.println("=".repeat(60));
            for (LineItem lineItem : getLineItems()) {

                System.out.println(lineItem.returnLineItems());
            }
        }

        System.out.printf("%n%-40s%-45s%40s%n", "Contractor Name", "Phone Number", "Email");
        System.out.println("=".repeat(125));
        for (Contractor contractor : getContractorList()) {

            System.out.printf("%-40s%-45s%40s%n", contractor.getContractorName(), contractor.getContractorPhoneNumber(),
                    contractor.getContractorEmail());
        }
        System.out.println("=".repeat(125));
        System.out.println("=".repeat(125));
    }

    public List<String> formatUserFriendlyJobInfo() {

        List<String> job = new ArrayList<String>();

        job.add("County:		      " + getCounty());
        job.add("Highway:	      " + getHighway());
        job.add("CSJ:		      " + getCsj());
        job.add("Working Days:     " + getWorkingDays());

        if (!getLineItems().isEmpty()) {
            job.add(String.format("%n%-40s%-12s%8s%n", "Item Description:", "Quantities:", "Price:"));
            job.add("=".repeat(60));
            for (LineItem lineItem : getLineItems()) {

                job.add(lineItem.returnLineItems());
            }
        }
        return job;
    }

    public List<String> formatEmailList() {

        List<String> emails = new ArrayList<String>();

        emails.add(getCounty() + "   " + getCsj());
        emails.add(String.format("Bid Proposal for %s - %s (%s)", getCounty(), getHighway(), getCsj()));
        emails.add("-".repeat(80));
        for (Contractor contractor : getContractorList()) {
            if (!contractor.getContractorEmail().equalsIgnoreCase("==No Email Found==")) {
                emails.add(contractor.getContractorEmail());
            } else {
                emails.add(contractor.getContractorEmail() + "   " + contractor.getContractorName());
            }
        }
        emails.add("");
        return emails;
    }

    // ====================================================================================================
    // Getter Setters
    // ====================================================================================================

    public Date getBiddingDate() {

        return biddingDate;
    }

    public void setBiddingDate(Date biddingDate) {

        this.biddingDate = biddingDate;
    }

    public List<Contractor> getContractorList() {

        return contractorList;
    }

    public void setContractorList(List<Contractor> contractorList) {

        this.contractorList = contractorList;
    }

    public List<LineItem> getLineItems() {

        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {

        this.lineItems = lineItems;
    }

    public String getCounty() {

        return county;
    }

    public void setCounty(String county) {

        this.county = county;
    }

    public String getHighway() {

        return highway;
    }

    public void setHighway(String highway) {

        this.highway = highway;
    }

    public String getCsj() {

        return csj;
    }

    public void setCsj(String csj) {

        this.csj = csj;
    }

    public int getWorkingDays() {

        return workingDays;

    }

    public void setWorkingDays(int workingDays) {

        this.workingDays = workingDays;
    }

    public int getUpTo_Mobs() {

        return upTo_Mobs;
    }

    public void setUpTo_Mobs(int upTo_Mobs) {

        this.upTo_Mobs = upTo_Mobs;
    }

    public BigDecimal getTotalMobs() {

        return totalMobs;
    }

    public void setTotalMobs(BigDecimal totalMobs) {

        this.totalMobs = totalMobs;
    }

    public BigDecimal getAdditionalMobs() {

        return additionalMobs;
    }

    public void setAdditionalMobs(BigDecimal additionalMobs) {

        this.additionalMobs = additionalMobs;
    }

    public BigDecimal getStandbyPrice() {

        return standbyPrice;
    }

    public void setStandbyPrice(BigDecimal standbyPrice) {

        this.standbyPrice = standbyPrice;
    }

    public BigDecimal getMinimumDayCharge() {

        return minimumDayCharge;
    }

    public void setMinimumDayCharge(BigDecimal minimumDayCharge) {

        this.minimumDayCharge = minimumDayCharge;
    }

    public String getBiddingDateString() {

        return dateFormat.format(biddingDate);
    }

    public void setBiddingDateFromString(String biddingDateString) {

        try {

            biddingDate = dateFormat.parse(biddingDateString);
        } catch (ParseException e) {

            // Handle parsing errors if the string is not in the expected format
            e.printStackTrace();
        }
    }

    public BigDecimal getSumOfQuantities() {

        BigDecimal sum = new BigDecimal(0);
        for (LineItem lineItem : getLineItems()) {

            sum = sum.add(lineItem.getQuantity());
        }
        return sum;
    }

    // ====================================================================================================
    // Comparing
    // ====================================================================================================

    @Override
    public boolean equals(Object o) {

        if (this == o)

            return true;

        if (o == null || getClass() != o.getClass())

            return false;

        Job job = (Job) o;

        return Objects.equals(county, job.county) &&
                Objects.equals(highway, job.highway) &&
                Objects.equals(csj, job.csj) &&
                Objects.equals(biddingDate, job.biddingDate) &&
                workingDays == job.workingDays &&
                upTo_Mobs == job.upTo_Mobs &&
                Objects.equals(totalMobs, job.totalMobs) &&
                Objects.equals(additionalMobs, job.additionalMobs) &&
                Objects.equals(contractorList, job.contractorList) &&
                Objects.equals(lineItems, job.lineItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(county, highway, csj, biddingDate, workingDays, upTo_Mobs, totalMobs, additionalMobs,
                contractorList, lineItems);
    }
}