import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

public class JobTest {

    @Test
    void testGetSumOfQuantities() {
        Job testJob = new Job();
        List<LineItem> expectedLineItems = new ArrayList<>(
                List.of(new LineItem("PLANE ASPH CONC PAV (2\")", new BigDecimal("17702.000"), new BigDecimal(0)),
                        new LineItem("PLANE ASPH CONC PAV (3\")", new BigDecimal("39818.000"), new BigDecimal(0))));
        testJob.setLineItems(expectedLineItems);

        BigDecimal expected = new BigDecimal("57520.000");
        BigDecimal actual = testJob.getSumOfQuantities();

        assertEquals(expected, actual);
    }

    @Test
    void testRemoveBlacklistedContractors() {
        Job testJob = new Job();
        List<Contractor> testContractors = new ArrayList<>(
                List.of(new Contractor("LONE STAR PAVING COMPANY", "(254)662-3100",
                        "QUOTES@BIGCREEKCONSTRUCTION.COM"),
                        new Contractor("TEXAS MATERIALS GROUP, INC", "(469)248-0301",
                                "CANDACE@FNHCONSTRUCTION.COM"),
                        new Contractor("DREWERY CONSTRUCTION COMPANY, INCOR", "(936)560-1534",
                                "DREWERYWHEATON@GMAIL.COM"),
                        new Contractor("MADDEN CONTRACTING COMPANY, LLC", "(318)377-0928",
                                "PGULLEY@MADDENCONTRACTING.COM")));
        testJob.setContractorList(testContractors);
        testJob.removeBlacklistedContractors();

        List<Contractor> expectedContractors = new ArrayList<>(
                List.of(new Contractor("DREWERY CONSTRUCTION COMPANY, INCOR", "(936)560-1534",
                        "DREWERYWHEATON@GMAIL.COM"),
                        new Contractor("MADDEN CONTRACTING COMPANY, LLC", "(318)377-0928",
                                "PGULLEY@MADDENCONTRACTING.COM")));
        assertEquals(expectedContractors, testJob.getContractorList());

    }

    @Test
    void testSetBiddingDateFromString() {

        Job testJob = new Job();
        String testDate = "1999-03-11 00:00:00";

        testJob.setBiddingDateFromString(testDate);

        Date actual = testJob.getBiddingDate();
        Date expected = new Date(921132000000L);

        assertEquals(expected, actual);
    }
}
