import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CombinedFormatTest {

    @Test
    void testJobFromFormat() {

        List<LineItem> expectedLineItems = new ArrayList<>(
                List.of(new LineItem("PLANE ASPH CONC PAV (2\")", new BigDecimal("17702.000"), new BigDecimal(0)),
                        new LineItem("PLANE ASPH CONC PAV (3\")", new BigDecimal("39818.000"), new BigDecimal(0))));

        List<Contractor> expectedContractors = new ArrayList<>(
                List.of(new Contractor("BIG CREEK CONSTRUCTION, LTD", "(254)662-3100",
                        "QUOTES@BIGCREEKCONSTRUCTION.COM"),
                        new Contractor("FNH CONSTRUCTION, LLC", "(469)248-0301",
                                "CANDACE@FNHCONSTRUCTION.COM"),
                        new Contractor("DREWERY CONSTRUCTION COMPANY, INCOR", "(936)560-1534",
                                "DREWERYWHEATON@GMAIL.COM"),
                        new Contractor("MADDEN CONTRACTING COMPANY, LLC", "(318)377-0928",
                                "PGULLEY@MADDENCONTRACTING.COM")));

        Job expectedJob = new Job("ANDERSON", "US0079", "6444-56-001", 21, new Date(1698948000000L), expectedLineItems,
                expectedContractors);

        CombinedFormat combinedFormat = new CombinedFormat();
        String input = "COUNTY: ANDERSON                HIGHWAY: US0079            LENGTH:     0.01|CONTROL NUMBER: 6444-56-001            *                 SBE GOAL:      0.0 %COUNTY|PROJECT NUMBER: RMC - 644456001|TYPE: MILL AND INLAY|TIME FOR COMPLETION: 21 WORKING DAYS                 GUARANTY:      45,000.00|BIDS RECEIVED UNTIL:  1:00 PM NOVEMBER 02, 2023      EST.COST:   2,269,976.00|BIDS WILL BE OPENED:  1:00 PM NOVEMBER 02, 2023|  MAIL OR DELIVER BIDS TO:                 FOR QUESTIONS CALL:      <-changed |  TEXAS DEPARTMENT OF TRANSPORTATION       EDUARDO CASTANEDA|  ATTN: CONSTRUCTION DIVISION - M1C3.02    2709 W FRONT ST                       <-changed |  6230 E. STASSNEY LANE                    TYLER, TX 75702|  AUSTIN TX 78744-3147                     903/510-9241|  FOR QUESTIONS REGARDING A PROPOSAL CALL 512/416-2498|  TO REQUEST A PROPOSAL GO TO HTTP://WWW.DOT.STATE.TX.US/BUSINESS/PR.HTM|  FOR ELECTRONIC BIDDING VISIT THE WEBSITE BELOW:|       HTTP://WWW.TXDOT.GOV/BUSINESS/LETTING-BIDS/EBS.HTML|LIMITS FROM: VARIOUS ROADWAYS IN|  LIMITS TO: ANDERSON & CHEROKEE COUNTIES|DATE FIRST ADVERTISED:6/29/2023  ADDED TO LETTING:9/26/2023                     |------------------------------------------------------------------------------|     ITEM DES  S.P                                                APPROXIMATE | ALT NO.  CD.  NO. ITEM DESCRIPTION                       UNIT    QUANTITIES  |------------------------------------------------------------------------------|      354 6045     PLANE ASPH CONC PAV (2\")                SY       17,702.000|      354 6048     PLANE ASPH CONC PAV (3\")                SY       39,818.000||PLANHOLDERS|--------------------------------------------------------------------------------|BIG CREEK CONSTRUCTION, LTD........ WACO, TX        (254)662-3100 (254)857-3289 <-FAX|                                                    EMAIL:QUOTES@BIGCREEKCONSTRUCTION.COM|FNH CONSTRUCTION, LLC.............. PLANO           (469)248-0301 (469)248-2720 <-FAX|                                                    EMAIL:CANDACE@FNHCONSTRUCTION.COM|***** SUPPLEMENTAL LIST OF BIDDERS|DREWERY CONSTRUCTION COMPANY, INCOR NACOGDOCHES     (936)560-1534 (936)560-6542 <-FAX|                                                    EMAIL:DREWERYWHEATON@GMAIL.COM|MADDEN CONTRACTING COMPANY, LLC.... MINDEN, LA      (318)377-0928 (318)377-9065 <-FAX|                                                    EMAIL:PGULLEY@MADDENCONTRACTING.COM|  |EMAIL ALL:QUOTES@BIGCREEKCONSTRUCTION.COM;CANDACE@FNHCONSTRUCTION.COM;DREWERYWHEATON@GMAIL.COM;PGULLEY@MADDENCONTRACTING.COM;||================================================================================";
        Job actualJob = combinedFormat.jobFromFormat(input);
    }

    @Test
    void testJobToFormat() {

        CombinedFormat combinedFormat = new CombinedFormat();
        assertThrows(UnsupportedOperationException.class, () -> {
            combinedFormat.jobToFormat(null);
        });
    }

    @Test
    void testJobsFromFormat() {

        List<LineItem> expectedLineItems = new ArrayList<>(
                List.of(new LineItem("PLANE ASPH CONC PAV (2\")", new BigDecimal("17702.000"), new BigDecimal(0)),
                        new LineItem("PLANE ASPH CONC PAV (3\")", new BigDecimal("39818.000"), new BigDecimal(0))));

        List<Contractor> expectedContractors = new ArrayList<>(
                List.of(new Contractor("BIG CREEK CONSTRUCTION, LTD", "(254)662-3100",
                        "QUOTES@BIGCREEKCONSTRUCTION.COM"),
                        new Contractor("FNH CONSTRUCTION, LLC", "(469)248-0301",
                                "CANDACE@FNHCONSTRUCTION.COM"),
                        new Contractor("DREWERY CONSTRUCTION COMPANY, INCOR", "(936)560-1534",
                                "DREWERYWHEATON@GMAIL.COM"),
                        new Contractor("MADDEN CONTRACTING COMPANY, LLC", "(318)377-0928",
                                "PGULLEY@MADDENCONTRACTING.COM")));

        List<Job> expectedJobs = new ArrayList<>(
                List.of(new Job("ANDERSON", "US0079", "6444-56-001", 21, new Date(1698948000000L), expectedLineItems,
                        expectedContractors)));

        FileManager fileManager = new FileManager();
        List<String> fileContents = fileManager.readFile(
                "C:\\Users\\Jacob\\Documents\\GitHub\\Bid-Proposal-Program\\BidProposalProject\\src\\test\\resources\\TestJobs.txt");
        CombinedFormat combinedFormat = new CombinedFormat();
        List<Job> actualJobs = combinedFormat.jobsFromFormat(fileContents);

        assertEquals(expectedJobs, actualJobs);
    }
}
