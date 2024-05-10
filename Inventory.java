
public class Inventory extends ItemHandling {
	
	public Inventory() {
		items = new Product[250];
		itemCount = 0;
	}
	
	public void viewNames() {
		if(this.isEmpty()) {
			System.out.println("There are no items in inventory!");
		}
		else {
			System.out.println("Items in inventory: ");
			for (int i = 0; i < itemCount; i++) {
				System.out.println("Item #" + (i+1) + ": \n"+ items[i].getName());
			}
		}
	}
}
