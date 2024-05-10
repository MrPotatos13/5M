
public class Food extends Product{
	private int calories;

	public Food(String name, double price, String category, int calories) {
		super(name, price, category);
		this.calories = calories;
	}
	
	// VAT for food items is 5%
	public double priceWithVAT() {
		return price * 1.05;
	}
	
	public String toString() {
		return super.toString()
				+"Calories: " + calories + "\n";
	}
}
