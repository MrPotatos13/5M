import java.io.Serializable;

public class Customer implements Serializable {
	private String name;
	private String address;
	private ShoppingCart shoppingCart;
	
	public Customer(String name, String address) {
		this.name = name;
		this.address = address;
		this.shoppingCart = new ShoppingCart();
	}

	public String getName() {
		return name;
	}

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}
	
	public String toString() {
		return "Name: " + name + ", Address: " + address;
	}
	
}
