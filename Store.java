import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Store {
	private JFrame frame;

	private static Scanner read = new Scanner(System.in);
	private static Customer[] customerList = FileHandling.loadCustomers();
	private static int customerCount = FileHandling.customerCount;
	private static Inventory inventory = FileHandling.loadInventory();

	public static void main(String[] args) {
		Store store = new Store();
		store.FMainFrame();
		boolean wantsExit = false;

		while (!wantsExit) {
			displayMainMenu();

			int choice = 0;
			try {
				System.out.print("your choice is: ");
				choice = read.nextInt();
				read.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input! Try again");
				read.nextLine(); // to catch buffer
				continue;
			}

			switch (choice) {
				case 1:
					customerHandling();
					break;
				case 2:
					cartOperations();
					break;
				case 3:
					inventoryHandling();
					break;
				case 4:
					wantsExit = true;
					break;
				default:
					System.out.println("Invalid Input!");
					break;
			}
		}
	}

	public static void customerHandling() {
		boolean wantsBack = false;

		while (!wantsBack) {

			displayCustomerMenu();

			int choice;
			try {
				System.out.print("your choice is: ");
				choice = read.nextInt();
				read.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input! Try again");
				read.nextLine();
				continue;
			}

			if (choice == 1) {
				if (customerCount < customerList.length) {
					System.out.print("Enter Name: ");
					String customerName = read.nextLine();

					System.out.print("Enter Address: ");
					String customerAddress = read.nextLine();

					customerList[customerCount] = new Customer(customerName, customerAddress);
					customerCount++;

					System.out.println("Customer Added!");
					FileHandling.saveCustomers(customerList, customerCount);
				} else {
					System.out.println("Can't add customer (Full)");
				}
			} else if (choice == 2) {
				System.out.print("Enter the name of the customer you want to remove: ");
				String customerName = read.nextLine();

				int index = findCustomer(customerName);

				if (index == -1) {
					System.out.println("Customer not found");
				} else {
					for (int i = index; i < customerCount - 1; i++) {
						customerList[i] = customerList[i + 1];
					}
					customerCount--;
					System.out.println("Customer removed!");
					FileHandling.saveCustomers(customerList, customerCount);
				}

			} else if (choice == 3) {
				if (customerCount == 0)
					System.out.println("There are no customers");
				for (int i = 0; i < customerCount; i++) {
					System.out.println(customerList[i]);
				}
			} else if (choice == 4) {
				wantsBack = true;
			} else {
				System.out.println("Invalid Choice!");
			}
		}
	}

	public static void cartOperations() {
		boolean wantsBack = false;
		boolean customerSelected = false;
		String customerName = "";

		while (!wantsBack) {
			if (!customerSelected) {
				System.out.println("Enter the customer name (or 'B' to go back): ");
				customerName = read.nextLine();
			}

			if (customerName.equalsIgnoreCase("B")) {
				wantsBack = true;
				continue;
			}

			int index = findCustomer(customerName);

			if (index == -1)
				System.out.println("Customer Not Found!");
			else {
				if (!customerSelected) {
					customerSelected = true;
					System.out.println("Customer Selected!");
				}

				displayCartMenu();

				Customer customer = customerList[index];
				ShoppingCart shoppingCart = customer.getShoppingCart();

				int choice;
				try {
					System.out.print("your choice is: ");
					choice = read.nextInt();
					read.nextLine();
				} catch (InputMismatchException e) {
					System.out.println("Invalid Input! Try again");
					read.nextLine();
					continue;
				}

				if (choice == 1) {
					inventory.viewNames();

					System.out.print("Enter the name of the product you want to add to cart: ");
					String productName = read.nextLine();

					try {
						inventory.findItem(productName);
					} catch (ItemNotFoundException e) {
						System.out.println(e.getMessage());
						continue;
					}

					int quantityToAdd;
					try {
						System.out.print("Enter how many you want to add: ");
						quantityToAdd = read.nextInt();
						read.nextLine();
					} catch (InputMismatchException e) {
						System.out.println("Invalid Input! Try again");
						read.nextLine();
						continue;
					}

					Product addedItem = null;
					try {
						addedItem = inventory.items[inventory.findItem(productName)];
					} catch (ItemNotFoundException e) {
						System.out.println(e.getMessage());
					}

					for (int i = 0; i < quantityToAdd; i++) {
						shoppingCart.addItem(addedItem);
					}
					System.out.println("Item\'s added!");
					FileHandling.saveCustomers(customerList, customerCount);
				} else if (choice == 2) {

					String productName = "";
					try {
						System.out.print("Enter the name of the product you want to remove from cart: ");
						productName = read.nextLine();
						shoppingCart.findItem(productName);
					} catch (ItemNotFoundException e) {
						System.out.println(e.getMessage());
						continue;
					}

					int quantityToRemove;
					try {
						System.out.print("Enter how many you want to remove: ");
						quantityToRemove = read.nextInt();
						read.nextLine();
					} catch (InputMismatchException e) {
						System.out.println("Invalid Input! Try again");
						read.nextLine();
						continue;
					}

					int availableQuantity = shoppingCart.countItem(productName);

					if (quantityToRemove <= availableQuantity) {
						try {
							for (int i = 0; i < quantityToRemove; i++) {
								shoppingCart.removeItem(productName);
							}
						} catch (ItemNotFoundException e) {
							System.out.println(e.getMessage());
						}

						System.out.println("Removed Successfully!");
						FileHandling.saveCustomers(customerList, customerCount);
					} else {
						System.out.println("Trying to remove more than there is");
					}

				} else if (choice == 3) {

					if (shoppingCart.isEmpty())
						System.out.println("Shopping Cart is Empty!");
					else
						shoppingCart.viewItems();

				} else if (choice == 4) {

					System.out.println("Shopping cart total is " + shoppingCart.calculateTotal() + " SR");

				} else if (choice == 5) {
					wantsBack = true;
					continue;
				}
			}
		}
	}

	public static void inventoryHandling() {
		boolean wantsBack = false;

		while (!wantsBack) {
			displayInventoryMenu();

			int choice;
			try {
				System.out.print("your choice is: ");
				choice = read.nextInt();
				read.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input! Try again");
				read.nextLine();
				continue;
			}

			if (choice == 1) {
				if (inventory.spaceAvailable()) {
					System.out.println("Is it food or nonfood: ");
					String type = read.nextLine();
					boolean isFood = false;

					if (type.equalsIgnoreCase("food")) {
						isFood = true;
					} else if (type.equalsIgnoreCase("nonfood")) {
						isFood = false;
					} else {
						System.out.println("Invalid type!");
						continue;
					}

					System.out.print("Enter Name: ");
					String itemName = read.nextLine();

					double itemPrice;
					try {
						System.out.print("Enter Price: ");
						itemPrice = read.nextDouble();
						read.nextLine();
					} catch (InputMismatchException e) {
						System.out.println("Invalid Input! Try again");
						read.nextLine();
						continue;
					}

					System.out.print("Enter Category: ");
					String itemCategory = read.nextLine();

					Product item;

					if (isFood) {
						System.out.print("Enter Calories: ");
						int foodCalories = read.nextInt();
						item = new Food(itemName, itemPrice, itemCategory, foodCalories);
					} else {
						try {
							System.out.print("Enter Warranty Period (in months): ");
							int nonFoodWarranty = read.nextInt();
							read.nextLine();
							item = new NonFood(itemName, itemPrice, itemCategory, nonFoodWarranty);
						} catch (InputMismatchException e) {
							System.out.println("Invalid Input! Try again");
							read.nextLine();
							continue;
						}
					}

					inventory.addItem(item);
					FileHandling.saveInventory(inventory);

				} else
					System.out.println("Inventory Full!");

			} else if (choice == 2) {

				System.out.print("Enter the name of the product you want to remove: ");
				String productName = read.nextLine();

				try {
					inventory.removeItem(productName);
				} catch (ItemNotFoundException e) {
					System.out.println(e.getMessage());
				}

				FileHandling.saveInventory(inventory);
			} else if (choice == 3) {

				if (inventory.isEmpty())
					System.out.println("Inventory is empty!");
				else
					inventory.viewItems();

			} else if (choice == 4) {

				wantsBack = true;

			} else {
				System.out.println("Invalid input!");
			}

		}
	}

	public static int findCustomer(String name) {
		for (int i = 0; i < customerCount; i++) {
			if (customerList[i].getName().equalsIgnoreCase(name))
				return i;
		}
		return -1;
	}

	public static void displayMainMenu() {
		System.out.println("**************************************");
		System.out.println("*   Welcome to your Store Manager    *");
		System.out.println("**************************************");
		System.out.println("*  choose one of the options below:  *");
		System.out.println("*  1- Customer Information           *");
		System.out.println("*  2- Customer Shopping Carts        *");
		System.out.println("*  3- Manage Inventory               *");
		System.out.println("*  4- Exit                           *");
		System.out.println("**************************************");
	}

	public static void displayCustomerMenu() {
		System.out.println("**************************************");
		System.out.println("*        Customer Information        *");
		System.out.println("**************************************");
		System.out.println("*  choose one of the options below:  *");
		System.out.println("*  1- Add a customer                 *");
		System.out.println("*  2- Remove a customer              *");
		System.out.println("*  3- View customers                 *");
		System.out.println("*  4- Back to main menu              *");
		System.out.println("**************************************");
	}

	public static void displayCartMenu() {
		System.out.println("**************************************");
		System.out.println("*      Customer Shopping Carts       *");
		System.out.println("**************************************");
		System.out.println("*  choose one of the options below:  *");
		System.out.println("*  1- Add an item                    *");
		System.out.println("*  2- Remove an item                 *");
		System.out.println("*  3- View items in cart             *");
		System.out.println("*  4- Calculate total                *");
		System.out.println("*  5- Back to main menu              *");
		System.out.println("**************************************");
	}

	public static void displayInventoryMenu() {
		System.out.println("**************************************");
		System.out.println("*         Inventory Handling         *");
		System.out.println("**************************************");
		System.out.println("*  choose one of the options below:  *");
		System.out.println("*  1- Add an item                    *");
		System.out.println("*  2- Remove an item                 *");
		System.out.println("*  3- View items in inventory        *");
		System.out.println("*  4- Back to main menu              *");
		System.out.println("**************************************");
	}

	/////////////////////////////////////////////////////// GUI
	public void FManageInvView() {
		frame = new JFrame("Inventory View");
		JPanel contentPaneFManageInvView = new JPanel();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFManageInvView.setBackground(new Color(128, 128, 128));
		contentPaneFManageInvView.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPaneFManageInvView.setLayout(null);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBackground(new Color(192, 192, 192));
		textArea.setBounds(27, 55, 378, 169);
		contentPaneFManageInvView.add(textArea);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(27, 55, 378, 169);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPaneFManageInvView.add(scrollPane);

		JButton btnView = new JButton("View");
		btnView.setBounds(149, 230, 113, 27);
		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				if (inventory.isEmpty()) {
					textArea.append("Inventory is empty!");
				} else {
					textArea.append(inventory.viewItemsString());
				}
			}
		});
		contentPaneFManageInvView.add(btnView);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvMain();
			}
		});
		contentPaneFManageInvView.add(btnBack);

		frame.setContentPane(contentPaneFManageInvView);
		frame.setVisible(true);
	}

	public void FManageInvRemove() {
		frame = new JFrame("Manage Remove Item");
		JPanel contentPaneFManageInvRemove;
		JTextField txtRemoveFManageInvRemove;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFManageInvRemove = new JPanel();
		contentPaneFManageInvRemove.setBackground(new Color(128, 128, 128));
		contentPaneFManageInvRemove.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFManageInvRemove);
		contentPaneFManageInvRemove.setLayout(null);

		JLabel lblManageRemoveItem = new JLabel("Manage Remove Item ");
		lblManageRemoveItem.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblManageRemoveItem.setBounds(110, 11, 179, 35);
		contentPaneFManageInvRemove.add(lblManageRemoveItem);

		JLabel lblNewLabel = new JLabel("Enter the name of the product you want to remove:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel.setBounds(54, 57, 324, 14);
		contentPaneFManageInvRemove.add(lblNewLabel);

		txtRemoveFManageInvRemove = new JTextField();
		txtRemoveFManageInvRemove.setBounds(133, 82, 156, 20);
		contentPaneFManageInvRemove.add(txtRemoveFManageInvRemove);
		txtRemoveFManageInvRemove.setColumns(10);

		JButton btnRemove = new JButton("Remove");
		btnRemove.setBounds(145, 145, 160, 30);
		btnRemove.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtRemoveFManageInvRemove.getText();
				try {
					inventory.removeItem(name);
				} catch (ItemNotFoundException eee) {
					System.out.println(eee.getMessage());
				}
				FileHandling.saveInventory(inventory);
				JOptionPane.showMessageDialog(frame, "Successfully Removed!");
				frame.setVisible(false);
				FManageInvMain();
			}
		});
		contentPaneFManageInvRemove.add(btnRemove);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvMain();
			}
		});
		contentPaneFManageInvRemove.add(btnBack);

		frame.setVisible(true);
	}

	public void FManageInvMain() {
		frame = new JFrame("Manage Inventory");
		JPanel contentPaneFManageInvMain;
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 516, 306);
		contentPaneFManageInvMain = new JPanel();
		contentPaneFManageInvMain.setBackground(new Color(128, 128, 128));
		contentPaneFManageInvMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPaneFManageInvMain);
		contentPaneFManageInvMain.setLayout(null);

		JLabel lblWelcome = new JLabel("Inventory Handling");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblWelcome.setBounds(104, 11, 287, 32);
		contentPaneFManageInvMain.add(lblWelcome);

		JButton btnAddItem = new JButton("Add Item");
		btnAddItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvAdd_Choose();

			}
		});
		btnAddItem.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAddItem.setBounds(10, 108, 159, 60);
		contentPaneFManageInvMain.add(btnAddItem);

		JButton btnRemoveItem = new JButton("Remove Item");
		btnRemoveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvRemove();

			}

		});
		btnRemoveItem.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnRemoveItem.setBounds(179, 108, 146, 60);
		contentPaneFManageInvMain.add(btnRemoveItem);

		JButton btnItemView = new JButton("View All Items");
		btnItemView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvView();

			}
		});
		btnItemView.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnItemView.setBounds(335, 108, 155, 60);
		contentPaneFManageInvMain.add(btnItemView);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FMainFrame();

			}
		});
		frame.setVisible(true);
		contentPaneFManageInvMain.add(btnBack);
	}

	public void FManageInvAdd_NonFood() {
		frame = new JFrame("Add NonFood");
		JPanel contentPaneFManageInvAdd_NonFood;
		JTextField textFieldFManageInvAdd_NonFood;
		JTextField textField_1FManageInvAdd_NonFood;
		JTextField textField_2FManageInvAdd_NonFood;
		JTextField txtWarrantyPeriodInFManageInvAdd_NonFood;

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFManageInvAdd_NonFood = new JPanel();
		contentPaneFManageInvAdd_NonFood.setBackground(new Color(128, 128, 128));
		contentPaneFManageInvAdd_NonFood.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFManageInvAdd_NonFood);
		contentPaneFManageInvAdd_NonFood.setLayout(null);

		JLabel lblNewLabel = new JLabel("Manage Item - NonFood");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(118, 11, 199, 35);
		contentPaneFManageInvAdd_NonFood.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Enter The Name:");
		lblNewLabel_1.setBounds(76, 57, 101, 26);
		contentPaneFManageInvAdd_NonFood.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("Enter The Price:");
		lblNewLabel_1_1.setBounds(76, 94, 101, 26);
		contentPaneFManageInvAdd_NonFood.add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_1_1 = new JLabel("Enter The Category:");
		lblNewLabel_1_1_1.setBounds(76, 129, 111, 26);
		contentPaneFManageInvAdd_NonFood.add(lblNewLabel_1_1_1);

		JLabel lblNewLabel_1_1_1_1 = new JLabel("Enter The Warranty Period :");
		lblNewLabel_1_1_1_1.setBounds(30, 166, 199, 26);
		contentPaneFManageInvAdd_NonFood.add(lblNewLabel_1_1_1_1);

		JLabel lblNewLabel_1_1_1_1_1 = new JLabel("  (in months)");
		lblNewLabel_1_1_1_1_1.setBounds(60, 186, 199, 26);
		contentPaneFManageInvAdd_NonFood.add(lblNewLabel_1_1_1_1_1);

		textFieldFManageInvAdd_NonFood = new JTextField();
		textFieldFManageInvAdd_NonFood.setColumns(10);
		textFieldFManageInvAdd_NonFood.setBounds(208, 60, 96, 20);
		contentPaneFManageInvAdd_NonFood.add(textFieldFManageInvAdd_NonFood);

		textField_1FManageInvAdd_NonFood = new JTextField();
		textField_1FManageInvAdd_NonFood.setColumns(10);
		textField_1FManageInvAdd_NonFood.setBounds(208, 97, 96, 20);
		contentPaneFManageInvAdd_NonFood.add(textField_1FManageInvAdd_NonFood);

		textField_2FManageInvAdd_NonFood = new JTextField();
		textField_2FManageInvAdd_NonFood.setColumns(10);
		textField_2FManageInvAdd_NonFood.setBounds(208, 132, 96, 20);
		contentPaneFManageInvAdd_NonFood.add(textField_2FManageInvAdd_NonFood);

		txtWarrantyPeriodInFManageInvAdd_NonFood = new JTextField();
		txtWarrantyPeriodInFManageInvAdd_NonFood.setColumns(10);
		txtWarrantyPeriodInFManageInvAdd_NonFood.setBounds(208, 169, 144, 20);
		contentPaneFManageInvAdd_NonFood.add(txtWarrantyPeriodInFManageInvAdd_NonFood);

		JButton btnNewButton = new JButton("Add");
		btnNewButton.setBounds(152, 216, 89, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String itemName = textFieldFManageInvAdd_NonFood.getText();
				double itemPrice = Double.valueOf(textField_1FManageInvAdd_NonFood.getText());
				String itemCategory = textField_2FManageInvAdd_NonFood.getText();
				int nonFoodWarranty = Integer.valueOf(txtWarrantyPeriodInFManageInvAdd_NonFood.getText());
				if (inventory.spaceAvailable()) {
					if (itemPrice > 0) {
						if (nonFoodWarranty > 0) {
							Product item = new Food(itemName, itemPrice, itemCategory, nonFoodWarranty);
							JOptionPane.showMessageDialog(frame, "Successfully Added!");
							frame.setVisible(false);
							FManageInvMain();
							inventory.addItem(item);
							FileHandling.saveInventory(inventory);
						} else {
							// wrong calories
							JOptionPane.showMessageDialog(frame, "Wrong Month Number");
						}
					} else {

						JOptionPane.showMessageDialog(frame, "Wrong Price");

					}
				} else {
					// full
					JOptionPane.showMessageDialog(frame, "Inventory fully");

				}

			}
		});

		contentPaneFManageInvAdd_NonFood.add(btnNewButton);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		;
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvAdd_Choose();
			}
		});
		contentPaneFManageInvAdd_NonFood.add(btnBack);
		frame.setVisible(true);
	}

	public void FManageInvAdd_Food() {
		frame = new JFrame("Add Food");
		JPanel contentPaneFManageInvAdd_Food;
		JTextField txtNameFManageInvAdd_Food;
		JTextField txtPriceFManageInvAdd_Food;
		JTextField txtCategoryFManageInvAdd_Food;
		JTextField txtCaloriesFManageInvAdd_Food;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFManageInvAdd_Food = new JPanel();
		contentPaneFManageInvAdd_Food.setBackground(new Color(128, 128, 128));
		contentPaneFManageInvAdd_Food.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFManageInvAdd_Food);
		contentPaneFManageInvAdd_Food.setLayout(null);

		JLabel lblNewLabel = new JLabel("Manage Item - Food");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(117, 11, 161, 35);
		contentPaneFManageInvAdd_Food.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Enter The Name:");
		lblNewLabel_1.setBounds(117, 57, 101, 26);
		contentPaneFManageInvAdd_Food.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("Enter The Price:");
		lblNewLabel_1_1.setBounds(117, 97, 101, 26);
		contentPaneFManageInvAdd_Food.add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_1_1 = new JLabel("Enter The Category:");
		lblNewLabel_1_1_1.setBounds(117, 134, 101, 26);
		contentPaneFManageInvAdd_Food.add(lblNewLabel_1_1_1);

		JLabel lblNewLabel_1_1_1_1 = new JLabel("Enter The Calories:");
		lblNewLabel_1_1_1_1.setBounds(117, 171, 101, 26);
		contentPaneFManageInvAdd_Food.add(lblNewLabel_1_1_1_1);

		txtNameFManageInvAdd_Food = new JTextField();
		txtNameFManageInvAdd_Food.setBounds(240, 60, 96, 20);
		contentPaneFManageInvAdd_Food.add(txtNameFManageInvAdd_Food);
		txtNameFManageInvAdd_Food.setColumns(10);

		txtPriceFManageInvAdd_Food = new JTextField();
		txtPriceFManageInvAdd_Food.setBounds(240, 100, 96, 20);
		contentPaneFManageInvAdd_Food.add(txtPriceFManageInvAdd_Food);

		txtCategoryFManageInvAdd_Food = new JTextField();
		txtCategoryFManageInvAdd_Food.setColumns(10);
		txtCategoryFManageInvAdd_Food.setBounds(238, 137, 96, 20);
		contentPaneFManageInvAdd_Food.add(txtCategoryFManageInvAdd_Food);

		txtCaloriesFManageInvAdd_Food = new JTextField();
		txtCaloriesFManageInvAdd_Food.setColumns(10);
		txtCaloriesFManageInvAdd_Food.setBounds(240, 174, 96, 20);
		contentPaneFManageInvAdd_Food.add(txtCaloriesFManageInvAdd_Food);

		JButton btnNewButton = new JButton("Add");
		btnNewButton.setBounds(156, 213, 89, 23);
		contentPaneFManageInvAdd_Food.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String itemName = txtNameFManageInvAdd_Food.getText();
				double itemPrice = Double.valueOf(txtPriceFManageInvAdd_Food.getText());
				String itemCategory = txtCategoryFManageInvAdd_Food.getText();
				int foodCalories = Integer.valueOf(txtCaloriesFManageInvAdd_Food.getText());
				if (inventory.spaceAvailable()) {
					if (itemPrice > 0) {
						if (foodCalories > 0) {
							Product item = new Food(itemName, itemPrice, itemCategory, foodCalories);
							JOptionPane.showMessageDialog(frame, "Successfully Added!");
							frame.setVisible(false);
							FManageInvMain();
							inventory.addItem(item);
							FileHandling.saveInventory(inventory);
						} else {
							// wrong calories
							JOptionPane.showMessageDialog(frame, "Wrong Calories");
						}
					} else {
						// wrong num
						JOptionPane.showMessageDialog(frame, "Wrong Price number");

					}
				} else {
					// full
					JOptionPane.showMessageDialog(frame, "Inventory fully");

				}

			}
		});

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		;
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvAdd_Choose();

			}
		});
		contentPaneFManageInvAdd_Food.add(btnBack);
		frame.setVisible(true);
	}

	public void FManageInvAdd_Choose() {
		frame = new JFrame("Add Item");
		JPanel contentPaneFManageInvAdd_Choose;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFManageInvAdd_Choose = new JPanel();
		contentPaneFManageInvAdd_Choose.setBackground(new Color(128, 128, 128));
		contentPaneFManageInvAdd_Choose.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPaneFManageInvAdd_Choose);
		contentPaneFManageInvAdd_Choose.setLayout(null);

		JLabel lblNewLabel = new JLabel("Inventory Handling ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(126, 11, 148, 28);
		contentPaneFManageInvAdd_Choose.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Is it food or nonfood:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(136, 50, 117, 14);
		contentPaneFManageInvAdd_Choose.add(lblNewLabel_1);

		JButton btnNewButton = new JButton("Food");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvAdd_Food();
			}
		});
		btnNewButton.setBounds(79, 77, 89, 23);
		contentPaneFManageInvAdd_Choose.add(btnNewButton);

		JButton btnNonfood = new JButton("NonFood");
		btnNonfood.setBounds(207, 77, 89, 23);
		btnNonfood.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvAdd_NonFood();

			}
		});
		contentPaneFManageInvAdd_Choose.add(btnNonfood);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		;
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FManageInvMain();

			}
		});
		contentPaneFManageInvAdd_Choose.add(btnBack);
		frame.setVisible(true);
	}

	public void FMainFrame() {
		frame = new JFrame("Main Frame");
		JPanel contentPaneFMainFrame;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 516, 306);
		contentPaneFMainFrame = new JPanel();
		contentPaneFMainFrame.setBackground(new Color(128, 128, 128));
		contentPaneFMainFrame.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPaneFMainFrame);
		contentPaneFMainFrame.setLayout(null);

		JLabel lblWelcome = new JLabel("Welcome To The Store.");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblWelcome.setBounds(104, 11, 287, 32);
		contentPaneFMainFrame.add(lblWelcome);

		JButton btnCustomerInformation = new JButton("Customer Information");
		btnCustomerInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				FCustomerInfo_Main();
			}
		});
		btnCustomerInformation.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCustomerInformation.setBounds(10, 108, 159, 60);
		contentPaneFMainFrame.add(btnCustomerInformation);

		JButton btnManageInventory = new JButton("Manage Inventory");
		btnManageInventory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				FManageInvMain();
			}
		});
		btnManageInventory.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnManageInventory.setBounds(179, 108, 146, 60);
		contentPaneFMainFrame.add(btnManageInventory);

		JButton btnCustomerShoppingCarts = new JButton("Customer Shopping Carts");
		btnCustomerShoppingCarts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				FCustomerShopping_SelectCustomer();
			}
		});
		btnCustomerShoppingCarts.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCustomerShoppingCarts.setBounds(335, 108, 155, 60);
		contentPaneFMainFrame.add(btnCustomerShoppingCarts);

		frame.setVisible(true);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private Customer customer = null;
	private ShoppingCart shoppingCart = null;
	private String productNameFoundAdd = "";
	private String productNameFoundRemove = "";

	public void FCustomerShopping_SelectItemRemove() {
		frame = new JFrame("Item Remove");
		JPanel contentPaneFCustomerShopping_SelectItemRemove;
		JTextField textFieldFCustomerShopping_SelectItemRemove;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerShopping_SelectItemRemove = new JPanel();
		contentPaneFCustomerShopping_SelectItemRemove.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerShopping_SelectItemRemove.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFCustomerShopping_SelectItemRemove);
		contentPaneFCustomerShopping_SelectItemRemove.setLayout(null);

		JLabel lblNewLabel = new JLabel("Please Enter The Name Of Product");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(81, 11, 260, 36);
		contentPaneFCustomerShopping_SelectItemRemove.add(lblNewLabel);

		textFieldFCustomerShopping_SelectItemRemove = new JTextField();
		textFieldFCustomerShopping_SelectItemRemove.setBounds(91, 47, 223, 28);
		contentPaneFCustomerShopping_SelectItemRemove.add(textFieldFCustomerShopping_SelectItemRemove);
		textFieldFCustomerShopping_SelectItemRemove.setColumns(10);

		JButton btnNewButton = new JButton("Done");
		btnNewButton.setBounds(146, 98, 95, 28);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String productName = textFieldFCustomerShopping_SelectItemRemove.getText();
				int index = -99;
				try {
					index = shoppingCart.findItem(productName);
				} catch (ItemNotFoundException eas) {
					System.out.println(eas.getMessage());
				}

				if (index == -99) {
					JOptionPane.showMessageDialog(frame, "Producet not found");
				} else {
					productNameFoundRemove = productName;
					frame.setVisible(false);
					FCustomerShopping_HowManyRemove();
				}
			}
		});
		contentPaneFCustomerShopping_SelectItemRemove.add(btnNewButton);
		JButton btnBack = new JButton("Back");
		///////// action listner
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerShMain();
			}
		});
		contentPaneFCustomerShopping_SelectItemRemove.add(btnBack);
		frame.setVisible(true);

	}

	public void FCustomerShopping_SelectItemAdd() {
		frame = new JFrame("Add Item");
		JPanel contentPaneFCustomerShopping_SelectItemAdd;
		JTextField textFieldFCustomerShopping_SelectItemAdd;

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerShopping_SelectItemAdd = new JPanel();
		contentPaneFCustomerShopping_SelectItemAdd.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerShopping_SelectItemAdd.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFCustomerShopping_SelectItemAdd);
		contentPaneFCustomerShopping_SelectItemAdd.setLayout(null);

		JLabel lblNewLabel = new JLabel("Please Enter The Name Of Product");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(81, 11, 260, 36);
		contentPaneFCustomerShopping_SelectItemAdd.add(lblNewLabel);

		textFieldFCustomerShopping_SelectItemAdd = new JTextField();
		textFieldFCustomerShopping_SelectItemAdd.setBounds(91, 47, 223, 28);
		contentPaneFCustomerShopping_SelectItemAdd.add(textFieldFCustomerShopping_SelectItemAdd);
		textFieldFCustomerShopping_SelectItemAdd.setColumns(10);

		JButton btnNewButton = new JButton("Done");
		btnNewButton.setBounds(146, 98, 95, 28);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = -1;

				String productName = textFieldFCustomerShopping_SelectItemAdd.getText();
				try {
					index = inventory.findItem(productName);
				} catch (ItemNotFoundException ae) {
					System.out.println(ae.getMessage());

				}
				if (index == -1) {

					JOptionPane.showMessageDialog(frame, "Producet not found");
				} else {
					productNameFoundAdd = productName;
					frame.setVisible(false);
					FCustomerShopping_HowManyAdd();

				}

			}
		});
		contentPaneFCustomerShopping_SelectItemAdd.add(btnNewButton);
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerShMain();

			}
		});
		contentPaneFCustomerShopping_SelectItemAdd.add(btnBack);
		frame.setVisible(true);
	}

	public void FCustomerShopping_SelectCustomer() {

		frame = new JFrame("Select Customer");
		JPanel contentPaneFCustomerShopping_SelectCustomer;
		JTextField textFieldFCustomerShopping_SelectCustomer;

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerShopping_SelectCustomer = new JPanel();
		contentPaneFCustomerShopping_SelectCustomer.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerShopping_SelectCustomer.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFCustomerShopping_SelectCustomer);
		contentPaneFCustomerShopping_SelectCustomer.setLayout(null);

		JLabel lblNewLabel = new JLabel("Enter the customer name:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(123, 11, 192, 34);
		contentPaneFCustomerShopping_SelectCustomer.add(lblNewLabel);

		textFieldFCustomerShopping_SelectCustomer = new JTextField();
		textFieldFCustomerShopping_SelectCustomer.setBounds(123, 56, 182, 20);
		contentPaneFCustomerShopping_SelectCustomer.add(textFieldFCustomerShopping_SelectCustomer);
		textFieldFCustomerShopping_SelectCustomer.setColumns(10);

		JButton btnNewButton = new JButton("Done");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnNewButton.setBounds(163, 97, 95, 34);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String customerNameFCustomerShopping_SelectCustomer = textFieldFCustomerShopping_SelectCustomer
						.getText();

				int index = findCustomer(customerNameFCustomerShopping_SelectCustomer);

				if (index == -1) {
					JOptionPane.showMessageDialog(frame, "Customer not found");
				} else {
					JOptionPane.showMessageDialog(frame, "Customer Selected!");
					customer = customerList[index];
					shoppingCart = customer.getShoppingCart();
					frame.setVisible(false);
					FCustomerShMain();

				}
			}
		});
		contentPaneFCustomerShopping_SelectCustomer.add(btnNewButton);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FMainFrame();

			}
		});
		contentPaneFCustomerShopping_SelectCustomer.add(btnBack);
		frame.setVisible(true);
	}

	public void FCustomerShopping_HowManyRemove() {
		frame = new JFrame("Remove Item");
		JPanel contentPaneFCustomerShopping_HowManyRemove;
		JTextField textFieldFCustomerShopping_HowManyRemove;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerShopping_HowManyRemove = new JPanel();
		contentPaneFCustomerShopping_HowManyRemove.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerShopping_HowManyRemove.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFCustomerShopping_HowManyRemove);
		contentPaneFCustomerShopping_HowManyRemove.setLayout(null);

		JLabel lblPleaseEnterHow = new JLabel("Please Enter How Many:");
		lblPleaseEnterHow.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPleaseEnterHow.setBounds(104, 11, 260, 36);
		contentPaneFCustomerShopping_HowManyRemove.add(lblPleaseEnterHow);

		textFieldFCustomerShopping_HowManyRemove = new JTextField();
		textFieldFCustomerShopping_HowManyRemove.setColumns(10);
		textFieldFCustomerShopping_HowManyRemove.setBounds(91, 58, 223, 28);
		contentPaneFCustomerShopping_HowManyRemove.add(textFieldFCustomerShopping_HowManyRemove);

		JButton btnNewButton = new JButton("Done");
		btnNewButton.setBounds(151, 97, 95, 28);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int quantityToRemove = Integer.valueOf(textFieldFCustomerShopping_HowManyRemove.getText());
				int availableQuantity = shoppingCart.countItem(productNameFoundRemove);
				if (quantityToRemove < 0) {
					JOptionPane.showMessageDialog(frame, "Wrong inputs");
				} else {
					if (quantityToRemove <= availableQuantity) {
						try {
							for (int i = 0; i < quantityToRemove; i++) {
								shoppingCart.removeItem(productNameFoundRemove);
							}
						} catch (ItemNotFoundException ee) {
							System.out.println(ee.getMessage());
						}

						JOptionPane.showMessageDialog(frame, "Removed Successfully!");
						FileHandling.saveCustomers(customerList, customerCount);
						frame.setVisible(false);
						FCustomerShMain();
					} else {
						JOptionPane.showMessageDialog(frame, "Trying to remove more than there is");
					}
				}
			}
		});
		contentPaneFCustomerShopping_HowManyRemove.add(btnNewButton);
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerShopping_SelectItemRemove();

			}
		});
		contentPaneFCustomerShopping_HowManyRemove.add(btnBack);

		frame.setVisible(true);
		////////////////////////////////////////// ;
	}

	public void FCustomerShopping_HowManyAdd() {
		frame = new JFrame("How Many Item");
		JPanel contentPaneFCustomerShopping_HowManyAdd;
		JTextField textFieldFCustomerShopping_HowManyAdd;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerShopping_HowManyAdd = new JPanel();
		contentPaneFCustomerShopping_HowManyAdd.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerShopping_HowManyAdd.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFCustomerShopping_HowManyAdd);
		contentPaneFCustomerShopping_HowManyAdd.setLayout(null);

		JLabel lblPleaseEnterHow = new JLabel("Please Enter How Many:");
		lblPleaseEnterHow.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPleaseEnterHow.setBounds(104, 11, 260, 36);
		contentPaneFCustomerShopping_HowManyAdd.add(lblPleaseEnterHow);

		textFieldFCustomerShopping_HowManyAdd = new JTextField();
		textFieldFCustomerShopping_HowManyAdd.setColumns(10);
		textFieldFCustomerShopping_HowManyAdd.setBounds(91, 58, 223, 28);
		contentPaneFCustomerShopping_HowManyAdd.add(textFieldFCustomerShopping_HowManyAdd);

		JButton btnNewButton = new JButton("Done");
		btnNewButton.setBounds(151, 97, 95, 28);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int quantityToAdd = Integer.valueOf(textFieldFCustomerShopping_HowManyAdd.getText());
				if (quantityToAdd < 0) {
					JOptionPane.showMessageDialog(frame, "Wrong inputs");
				} else {
					Product addedItem = null;
					try {
						addedItem = inventory.items[inventory.findItem(productNameFoundAdd)];
					} catch (ItemNotFoundException ea) {
						System.out.println(ea.getMessage());
					}
					for (int i = 0; i < quantityToAdd; i++) {
						shoppingCart.addItem(addedItem);
					}
					JOptionPane.showMessageDialog(frame, "Item\'s added!");
					FileHandling.saveCustomers(customerList, customerCount);
					frame.setVisible(false);
					FCustomerShMain();
				}
			}
		});
		contentPaneFCustomerShopping_HowManyAdd.add(btnNewButton);
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerShopping_SelectItemAdd();

			}
		});
		contentPaneFCustomerShopping_HowManyAdd.add(btnBack);
		frame.setVisible(true);
		////////////////////////////////////////// ;
	}

	public void FCustomerShMain() {
		frame = new JFrame("Customer Shopping");
		JPanel contentPaneFCustomerShMain;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerShMain = new JPanel();
		contentPaneFCustomerShMain.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerShMain.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFCustomerShMain);
		contentPaneFCustomerShMain.setLayout(null);

		JLabel lblNewLabel = new JLabel("Customer Shopping Cart");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(112, 11, 220, 34);
		contentPaneFCustomerShMain.add(lblNewLabel);

		JButton btnNewButton = new JButton("Add an item");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				FCustomerShopping_SelectItemAdd();

			}
		});
		btnNewButton.setBounds(71, 86, 116, 43);
		contentPaneFCustomerShMain.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Remove an item");
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNewButton_1.setBounds(215, 86, 117, 43);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				FCustomerShopping_SelectItemRemove();

			}
		});
		contentPaneFCustomerShMain.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("View items in cart");
		btnNewButton_2.setHorizontalAlignment(SwingConstants.LEADING);
		btnNewButton_2.setBounds(68, 151, 119, 43);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				FCustomerShopping_ViewItems();

			}
		});
		contentPaneFCustomerShMain.add(btnNewButton_2);

		JButton btnNewButton_2_1 = new JButton("Calculate total");

		btnNewButton_2_1.setBounds(215, 151, 117, 43);
		contentPaneFCustomerShMain.add(btnNewButton_2_1);
		btnNewButton_2_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				Calculate();

			}
		});
		JButton btnBack = new JButton("Back");
		///////// action listner
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerShopping_SelectCustomer();

			}
		});
		contentPaneFCustomerShMain.add(btnBack);
		frame.setVisible(true);

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void FCustomerInfo_View() {
		frame = new JFrame("View Customers");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 120, 531, 417);

		JPanel contentPaneFCustomerInfo_View = new JPanel(null);
		contentPaneFCustomerInfo_View.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerInfo_View.setBorder(new EmptyBorder(8, 8, 8, 8));
		frame.setContentPane(contentPaneFCustomerInfo_View);

		JTextArea textAreaFCustomerInfo_View = new JTextArea();
		textAreaFCustomerInfo_View.setEditable(false);
		textAreaFCustomerInfo_View.setBackground(new Color(219, 219, 219));

		JScrollPane scrollPaneFCustomerInfo_View = new JScrollPane(textAreaFCustomerInfo_View);
		scrollPaneFCustomerInfo_View.setBounds(10, 50, 480, 250);
		scrollPaneFCustomerInfo_View.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneFCustomerInfo_View.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPaneFCustomerInfo_View.add(scrollPaneFCustomerInfo_View);

		JButton btnViewAll = new JButton("View All Customers");
		btnViewAll.setBounds((frame.getWidth() - 160) / 2, 320, 160, 40);
		btnViewAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaFCustomerInfo_View.setText("");
				if (customerCount == 0) {
					textAreaFCustomerInfo_View.append("There are no customers\n");
				} else {
					for (int i = 0; i < customerCount; i++) {
						textAreaFCustomerInfo_View.append(customerList[i].toString() + "\n");
					}
				}
			}
		});
		contentPaneFCustomerInfo_View.add(btnViewAll);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerInfo_Main();
			}
		});
		contentPaneFCustomerInfo_View.add(btnBack);

		frame.setVisible(true);
	}

	public void FCustomerInfo_Remove() {
		frame = new JFrame("Remove Customer");
		JPanel contentPaneFCustomerInfo_Remove;
		JTextField textFieldFCustomerInfo_Remove;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerInfo_Remove = new JPanel();
		contentPaneFCustomerInfo_Remove.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPaneFCustomerInfo_Remove.setBackground(new Color(128, 128, 128));

		frame.setContentPane(contentPaneFCustomerInfo_Remove);
		contentPaneFCustomerInfo_Remove.setLayout(null);

		JLabel lblNewLabel = new JLabel("Enter The Name:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(149, 11, 108, 32);
		contentPaneFCustomerInfo_Remove.add(lblNewLabel);

		textFieldFCustomerInfo_Remove = new JTextField();
		textFieldFCustomerInfo_Remove.setBounds(119, 43, 148, 29);
		contentPaneFCustomerInfo_Remove.add(textFieldFCustomerInfo_Remove);
		textFieldFCustomerInfo_Remove.setColumns(10);

		JButton btnNewButton = new JButton("Remove Customer");
		btnNewButton.setBounds(136, 83, 121, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String customerName = textFieldFCustomerInfo_Remove.getText();
				int index;
				index = findCustomer(customerName);

				if (index == -1) {
					JOptionPane.showMessageDialog(frame, "Customer not found");
				} else {
					for (int i = index; i < customerCount - 1; i++) {
						customerList[i] = customerList[i + 1];
					}
					customerCount--;
					JOptionPane.showMessageDialog(frame, "Customer removed");
					FileHandling.saveCustomers(customerList, customerCount);
					frame.setVisible(false);
					FCustomerInfo_Main();
				}
			}
		});
		contentPaneFCustomerInfo_Remove.add(btnNewButton);

		JButton btnBack = new JButton("Back");
		///////// action listner
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerInfo_Main();

			}
		});

		contentPaneFCustomerInfo_Remove.add(btnBack);
		frame.setVisible(true);
	}

	public void FCustomerInfo_Main() {
		frame = new JFrame("Customer Information");
		JPanel contentPaneFCustomerInfo_Main;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 516, 306);
		contentPaneFCustomerInfo_Main = new JPanel();
		contentPaneFCustomerInfo_Main.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerInfo_Main.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPaneFCustomerInfo_Main);
		contentPaneFCustomerInfo_Main.setLayout(null);

		JLabel lblWelcome = new JLabel("Customer Information");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblWelcome.setBounds(104, 11, 287, 32);
		contentPaneFCustomerInfo_Main.add(lblWelcome);

		JButton btnCustomerInformation = new JButton("Add Customer");
		btnCustomerInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerInfo_Add();
			}
		});
		btnCustomerInformation.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCustomerInformation.setBounds(10, 108, 159, 60);
		contentPaneFCustomerInfo_Main.add(btnCustomerInformation);

		JButton btnManageInventory = new JButton("Remove Customer");
		btnManageInventory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerInfo_Remove();

			}
		});
		btnManageInventory.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnManageInventory.setBounds(179, 108, 146, 60);
		contentPaneFCustomerInfo_Main.add(btnManageInventory);

		JButton btnCustomerShoppingCarts = new JButton("View All Customers");
		btnCustomerShoppingCarts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerInfo_View();
			}
		});
		btnCustomerShoppingCarts.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCustomerShoppingCarts.setBounds(335, 108, 155, 60);
		contentPaneFCustomerInfo_Main.add(btnCustomerShoppingCarts);

		JButton btnBack = new JButton("Back");
		///////// action listner
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FMainFrame();

			}
		});
		contentPaneFCustomerInfo_Main.add(btnBack);

		frame.setVisible(true);
	}

	public void FCustomerInfo_Add() {
		frame = new JFrame("Add Customer");
		JPanel contentPaneFCustomerInfo_Add;
		JTextField textFieldFCustomerInfo_Add;
		JTextField textField_1FCustomerInfo_Add;

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPaneFCustomerInfo_Add = new JPanel();
		contentPaneFCustomerInfo_Add.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerInfo_Add.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPaneFCustomerInfo_Add);
		contentPaneFCustomerInfo_Add.setLayout(null);

		JLabel lblNewLabel = new JLabel("Enter The Name");
		lblNewLabel.setBounds(170, 11, 77, 14);
		contentPaneFCustomerInfo_Add.add(lblNewLabel);

		JLabel lblEnterTheAdress = new JLabel("Enter The Address");
		lblEnterTheAdress.setBounds(170, 103, 97, 14);
		contentPaneFCustomerInfo_Add.add(lblEnterTheAdress);

		textFieldFCustomerInfo_Add = new JTextField();
		textFieldFCustomerInfo_Add.setBounds(143, 41, 135, 20);
		contentPaneFCustomerInfo_Add.add(textFieldFCustomerInfo_Add);
		textFieldFCustomerInfo_Add.setColumns(10);

		textField_1FCustomerInfo_Add = new JTextField();
		textField_1FCustomerInfo_Add.setColumns(10);
		textField_1FCustomerInfo_Add.setBounds(143, 134, 135, 20);
		contentPaneFCustomerInfo_Add.add(textField_1FCustomerInfo_Add);

		JButton btnBack = new JButton("Back");
		///////// action listner
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		;
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerInfo_Main();
			}
		});
		contentPaneFCustomerInfo_Add.add(btnBack);

		JButton btnAdd = new JButton("Add");

		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAdd.setBounds(150, 180, 100, 40);
		contentPaneFCustomerInfo_Add.add(btnAdd);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (customerCount < customerList.length) {

					String customerName = textFieldFCustomerInfo_Add.getText();

					String customerAddress = textField_1FCustomerInfo_Add.getText();

					customerList[customerCount] = new Customer(customerName, customerAddress);
					customerCount++;
					JOptionPane.showMessageDialog(frame, "Customer Added!");
					FileHandling.saveCustomers(customerList, customerCount);
					frame.setVisible(false);
					FCustomerInfo_Main();
				} else {
					JOptionPane.showMessageDialog(frame, "Can't add customer (Full)");
				}
			}
		});
		frame.setVisible(true);
	}

	public void FCustomerShopping_ViewItems() {
		frame = new JFrame("View Items in Cart");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 120, 531, 417);

		JPanel contentPaneFCustomerInfo_View = new JPanel(null);
		contentPaneFCustomerInfo_View.setBackground(new Color(128, 128, 128));
		contentPaneFCustomerInfo_View.setBorder(new EmptyBorder(8, 8, 8, 8));
		frame.setContentPane(contentPaneFCustomerInfo_View);

		JTextArea textAreaFCustomerInfo_View = new JTextArea();
		textAreaFCustomerInfo_View.setEditable(false);
		textAreaFCustomerInfo_View.setBackground(new Color(219, 219, 219));

		JScrollPane scrollPaneFCustomerInfo_View = new JScrollPane(textAreaFCustomerInfo_View);
		scrollPaneFCustomerInfo_View.setBounds(10, 50, 480, 250);
		scrollPaneFCustomerInfo_View.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneFCustomerInfo_View.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPaneFCustomerInfo_View.add(scrollPaneFCustomerInfo_View);

		JButton btnViewAll = new JButton("View Items");
		btnViewAll.setBounds((frame.getWidth() - 160) / 2, 320, 160, 40);
		btnViewAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaFCustomerInfo_View.setText("");
				if (shoppingCart.isEmpty())
					textAreaFCustomerInfo_View.append(("Shopping Cart is Empty!"));
				else
					textAreaFCustomerInfo_View.append(shoppingCart.viewItemsString());
			}
		});
		contentPaneFCustomerInfo_View.add(btnViewAll);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerShMain();
			}
		});
		contentPaneFCustomerInfo_View.add(btnBack);

		frame.setVisible(true);
	}

	public void Calculate() {
		JPanel contentPane;
		frame = new JFrame("Calculate");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(128, 128, 128));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Shopping cart total is " + shoppingCart.calculateTotal() + " SR");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setBounds(81, 91, 252, 48);
		contentPane.add(lblNewLabel);
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBack.setBounds(5, 5, 60, 40);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FCustomerShMain();
			}
		});
		contentPane.add(btnBack);
		frame.setVisible(true);
	}

}