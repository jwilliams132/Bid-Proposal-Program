package gearworks;

import java.util.Objects;

public class TxDotLineItem {

	private String description;
	private String itemNumber = "";
	private String descriptionCode = "";
	private String unit = "SY";

	public TxDotLineItem() {

	}

	public TxDotLineItem(String itemNumber, String descriptionCode, String description, String unit) {

		this.itemNumber = itemNumber;
		this.descriptionCode = descriptionCode;
		this.description = description;
		this.unit = unit;
	}

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

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {

			return false;
		}
		TxDotLineItem other = (TxDotLineItem) obj;
		return Objects.equals(itemNumber, other.itemNumber) &&
				Objects.equals(descriptionCode, other.descriptionCode) &&
				Objects.equals(description, other.description) &&
				Objects.equals(unit, other.unit);
	}

	@Override
	public int hashCode() {

		return Objects.hash(itemNumber, descriptionCode, description, unit);
	}
}
