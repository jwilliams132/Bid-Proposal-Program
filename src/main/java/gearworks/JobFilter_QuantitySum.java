package gearworks;

import java.math.BigDecimal;

public class JobFilter_QuantitySum implements JobFilterInterface {

	private BigDecimal limit;

    public JobFilter_QuantitySum(BigDecimal limit) {

        this.limit = limit;
    }

    @Override
    public boolean apply(Job job) {
		
        BigDecimal sum = job.getLineItems().stream()
                            .map(LineItem::getQuantity) // Replace with the actual field
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.compareTo(limit) >= 0;
    }
}
