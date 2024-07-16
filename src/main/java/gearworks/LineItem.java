package gearworks;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LineItem {

	private String description,
			itemNumber = "",
			descriptionCode = "",
			// specialNumber,
			unit = "SY";

	private BigDecimal quantity;
	private BigDecimal price = new BigDecimal(0);

	public LineItem() {

	}

	@Deprecated
	public LineItem(String description, BigDecimal quantity, BigDecimal price) {

		this.description = description;
		this.quantity = quantity;
		this.price = price;
	}

	@JsonCreator
    public LineItem(
            @JsonProperty("itemNumber") String itemNumber,
            @JsonProperty("descriptionCode") String descriptionCode,
            // @JsonProperty("specialNumber") String specialNumber,
            @JsonProperty("description") String description,
            @JsonProperty("unit") String unit,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("price") BigDecimal price) {
        this.itemNumber = itemNumber;
        this.descriptionCode = descriptionCode;
        // this.specialNumber = specialNumber;
        this.description = description;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
    }

	@Deprecated
	public LineItem(String specNumber,
			String description,
			String unit,
			BigDecimal quantity,
			BigDecimal price) {

		String[] tokens = specNumber.split(" ");
		this.itemNumber = tokens[0];
		this.descriptionCode = tokens[1];

		this.description = description;
		this.unit = unit;
		this.quantity = quantity;
		this.price = price;
	}

	// ====================================================================================================
	// Outputting Data
	// ====================================================================================================

	public String returnLineItems() {

		return String.format("%-10s%-40s%,11.0f    %1.2f%n", getSpecNumber(), getDescription(), getQuantity(),
				getPrice());
	}

	public String returnLabelFormattedString() {

		return String.format("%7s  %-39s  %,14.2f %-4s", getSpecNumber(), getDescription(), getQuantity(), getUnit());
	}

	// ====================================================================================================
	// Getter Setters
	// ====================================================================================================

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getDescriptionCode() {
		return descriptionCode;
	}

	public void setDescriptionCode(String descriptionCode) {
		this.descriptionCode = descriptionCode;
	}

	@JsonIgnore
	public String getSpecNumber() {
		return itemNumber +
				" " + descriptionCode
		// " " + specialNumber
		;
	}

	// public String getSpecialNumber() {
	// return specialNumber;
	// }

	// public void setSpecialNumber(String specialNumber) {
	// this.specialNumber = specialNumber;
	// }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
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

	// ====================================================================================================
	// Comparing
	// ====================================================================================================

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {

			return false;
		}

		LineItem otherLineItem = (LineItem) obj;

		return Objects.equals(itemNumber, otherLineItem.itemNumber) &&
				Objects.equals(descriptionCode, otherLineItem.descriptionCode) &&
				Objects.equals(description, otherLineItem.description) &&
				Objects.equals(quantity, otherLineItem.quantity) &&
				Objects.equals(price, otherLineItem.price);
	}

	@Override
	public int hashCode() {

		return Objects.hash(itemNumber, descriptionCode, description, quantity, price);
	}
}