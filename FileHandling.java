import java.io.*;

public class FileHandling {

    // for knowing the number of customers after restart of program
    public static int customerCount = 0;

    // Method to save inventory to a file using try-with-resources to ensure proper closure of resources
    public static void saveInventory(Inventory inventory) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("inventory.dat"))) {
            out.writeObject(inventory);
        } catch (IOException e) {
            System.out.println("Failed to save inventory data.");
        }
    }

    // Method to load inventory from a file
    public static Inventory loadInventory() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("inventory.dat"))) {
            return (Inventory) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Failed to load inventory data.");
            return new Inventory(); // return an empty inventory if failed to load
        }
    }

    // Method to save customer data to a file
    public static void saveCustomers(Customer[] customers, int customerCount) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("customers.dat"))) {
            out.writeObject(customers);
            out.writeInt(customerCount);
        } catch (IOException e) {
            System.out.println("Failed to save customer data.");
        }
    }

    // Method to load customer data from a file
    public static Customer[] loadCustomers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("customers.dat"))) {
            Object list = in.readObject();
            customerCount = in.readInt();
            return (Customer[]) list;
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Failed to load customer data.");
            return new Customer[50]; // return an empty customer array if failed to load
        }
    }
}
