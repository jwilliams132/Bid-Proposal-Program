
public class LineItem {
	
	private String description;
	private float quantity;
	private float price = 0;
	
	public LineItem(String description, float quantity, float price) {
		
		setDescription(description);
		setQuantity(quantity);
		setPrice(price);
	}
	
	// ====================================================================================================
	// Outputting Data
	// ====================================================================================================
	
	public String returnLineItems() {
		
		return String.format("%-40s%,11.0f    %1.2f%n", getDescription(), Float.valueOf(getQuantity()), getPrice());
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

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
}