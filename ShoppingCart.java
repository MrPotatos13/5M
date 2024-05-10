
public class ShoppingCart extends ItemHandling {

	public ShoppingCart() {
		items = new Product[30];
		itemCount = 0;
	}

	public double calculateTotal() {
		double total = 0;
		for (int i = 0; i < itemCount; i++) {
			total += items[i].priceWithVAT();
		}
		return total;
	}

}
