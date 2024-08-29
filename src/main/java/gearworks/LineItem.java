package gearworks;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LineItem extends TxDotLineItem {

	// private String description,
	// itemNumber = "",
	// descriptionCode = "",
	// // specialNumber,
	// unit = "SY";

	private BigDecimal quantity;
	private BigDecimal price = new BigDecimal(0);

	public LineItem() {

		super();
	}

	@Deprecated
	public LineItem(String description, BigDecimal quantity, BigDecimal price) {

		super("", "", description, "SY");
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
		super(itemNumber, descriptionCode, description, unit);
		this.quantity = quantity;
		this.price = price;
	}

	@Deprecated
	public LineItem(String specNumber, String description, String unit, BigDecimal quantity, BigDecimal price) {

		super("", "", description, unit);
		String[] tokens = specNumber.split(" ");
		setItemNumber(tokens[0]);
		setDescriptionCode(tokens[1]);
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

	@JsonIgnore
	public String getSpecNumber() {

		return getItemNumber() + " " + getDescriptionCode();
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

		return super.equals(obj) &&
				Objects.equals(quantity, otherLineItem.quantity) &&
				Objects.equals(price, otherLineItem.price);
	}

	@Override
	public int hashCode() {

		return Objects.hash(super.hashCode(), quantity, price);
	}
}