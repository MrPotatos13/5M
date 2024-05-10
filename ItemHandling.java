import java.io.Serializable;

public class ItemHandling implements Serializable {
	protected Product[] items;
	protected int itemCount;

	public boolean addItem(Product product) {
		if (spaceAvailable()) {
			items[itemCount] = product;
			itemCount++;
			return true;
		} else
			return false;
	}

	public int findItem(String productName) throws ItemNotFoundException {
		for (int i = 0; i < itemCount; i++) {
			if (items[i].getName().equalsIgnoreCase(productName))
				return i;
		}
		throw new ItemNotFoundException("Item " + productName + " Not Found");
	}

	public boolean removeItem(String productName) throws ItemNotFoundException {
		int index = findItem(productName);
		for (int i = index; i < itemCount - 1; i++)
			items[i] = items[i + 1];
		itemCount--;
		return true;
	}

	public void viewItems() {

		for (int i = 0; i < itemCount; i++)
			System.out.println("Item #" + (i + 1) + ": \n" + items[i]);
	}

	public String viewItemsString() {
		String str = "";
		for (int i = 0; i < itemCount; i++)
			str += "Item #" + (i + 1) + ": \n" + items[i];
		return str;
	}

	public int countItem(String productName) {
		int counter = 0;
		for (int i = 0; i < itemCount; i++) {
			if (items[i].getName().equals(productName))
				counter++;
		}
		return counter;
	}

	public boolean spaceAvailable() {
		return itemCount < items.length;
	}

	public boolean isEmpty() {
		return itemCount == 0;
	}
}
