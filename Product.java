import java.io.Serializable;

public abstract class Product implements Serializable {
	protected String name;
	protected double price;
	protected String category;

	// constructer to be used by children of this class
	public Product(String name, double price, String category) {
		this.name = name;
		this.price = price;
		this.category = category;
	}

	// abstract method to calculate price for product with VAT
	// note that VAT is not the same for all children
	public abstract double priceWithVAT();

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public String getCategory() {
		return category;
	}

	public String toString() {
		return "Name: " + name + "\n"
				+ "Price: " + price + "\n"
				+ "Category: " + category + "\n";
	}

}
