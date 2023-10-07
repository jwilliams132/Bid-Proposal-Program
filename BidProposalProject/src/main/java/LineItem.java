import java.math.BigDecimal;
import java.util.Objects;

public class LineItem {

    private String description;
    private BigDecimal quantity;
    private BigDecimal price = new BigDecimal(0);

    public LineItem(String description, BigDecimal quantity, BigDecimal price) {

        setDescription(description);
        setQuantity(quantity);
        setPrice(price);
    }

    // ====================================================================================================
    // Outputting Data
    // ====================================================================================================

    public String returnLineItems() {

        return String.format("%-40s%,11.0f    %1.2f%n", getDescription(), getQuantity(), getPrice());
    }

    public String formatLineItems() {

        return String.format("|%s|%s|%1.2f", getDescription(), getQuantity(), getPrice());
    }

    // ====================================================================================================
    // Getter Setters
    // ====================================================================================================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {

            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {

            return false;
        }

        LineItem otherLineItem = (LineItem) obj;

        return Objects.equals(description, otherLineItem.description) &&
                quantity == otherLineItem.quantity &&
                price.compareTo(otherLineItem.price) == 0;
    }
}