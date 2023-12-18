package gearworks;

import java.math.BigDecimal;
import java.util.Objects;

public class LineItem {

	private String description, specNumber, unit;

	private BigDecimal quantity;
	private BigDecimal price = new BigDecimal(0);

	public LineItem(String description, BigDecimal quantity, BigDecimal price) {

		this.description = description;
		this.quantity = quantity;
		this.price = price;
	}

	public LineItem(String specNumber, String description, String unit, BigDecimal quantity, BigDecimal price) {

		this.specNumber = specNumber;
		this.description = description;
		this.unit = unit;
		this.quantity = quantity;
		this.price = price;
	}

	// ====================================================================================================
	// Outputting Data
	// ====================================================================================================

	public String returnLineItems() {

		return String.format("%-10n%-40s%,11.0f    %1.2f%n", getSpecNumber(), getDescription(), getQuantity(),
				getPrice());
	}

	public String returnLabelFormattedString() {

		return String.format("%12s  %-39s  %,14.2f %-4s", getSpecNumber(), getDescription(), getQuantity(), getUnit());
	}

	// ====================================================================================================
	// Getter Setters
	// ====================================================================================================

	public String getSpecNumber() {
		return specNumber;
	}

	public void setSpecNumber(String specNumber) {
		this.specNumber = specNumber;
	}

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

		return Objects.equals(specNumber, otherLineItem.specNumber) &&
				Objects.equals(description, otherLineItem.description) &&
				Objects.equals(quantity, otherLineItem.quantity) &&
				Objects.equals(price, otherLineItem.price);
	}

	@Override
	public int hashCode() {

		return Objects.hash(specNumber, description, quantity, price);
	}
}