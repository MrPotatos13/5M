
public class NonFood extends Product {
	// in months
	private int warrantyPeriod;

	public NonFood(String name, double price, String category, int warrantyPeriod) {
		super(name, price, category);
		this.warrantyPeriod = warrantyPeriod;
	}

	// VAT for nonfood items is 15%
	public double priceWithVAT() {
		return price * 1.15;
	}

	public String toString() {
		return super.toString()
				+ "Warranty Period: " + warrantyPeriod + "\n";
	}
}
