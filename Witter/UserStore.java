/**
* The UserStore is implemented by using a HashMap which uses a Key-Value Pair. The key value being the unique user ID and the value being the User Object.
* My implementation of the HashMap does not contain the remove operation, since its not required for it, but does have a put and get operation.
* Using an initial capacity of 128 means that the hashmap will be balanced if the user ids added are only incremementing by 1 each time.
* Inserting a User into the hashMap has the best case O(1) and worst case O(n) which is the same for searching for a user
* Hashing the unique Id means that there is a reduced chance of collisions
* The reason a hash map was chosen was because there's no fixed size, therefore it is scalable
* @author_name: Rushil Gala-Shah
* @author: 1515140
*/

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;

import java.util.Date;

public class UserStore implements IUserStore {
	private HashMap<Integer,User> userList; // Store the variable userList to type HashMap which takes Integers and Users
	
	class HashMap<K,V> { // Inner class HashMap for ADT implementation
		private userEntry<K,V>[] table; // Array of Nodes which will contain the key (unique id) and value (the user object)
		private int capacity = 128; // Initial Capacity of HashMap
		private int count; // Variable for counting how many users we currently have
		
		class userEntry<K,V> { // Each Key-Value Pair object inner class
			K key; // Key - the hashed value of the unique id
			V value; // The user object
			userEntry<K,V> next; // The next Key-Value Pair in the list
			
			// Constructor for when a new Key-Value Pair is initialized
			public userEntry(K key, V value, userEntry<K,V> next){
				this.key = key;
				this.value = value;
				this.next = next;
			}
			
		}
		
		@SuppressWarnings("unchecked")
		public HashMap() { // Constructor for HashMap
			this.table = new userEntry[capacity]; // Create the initial array of nodes using the capacity given
			this.count = 0; // Set the number of users to 0
		}
		
		// Adding a user object to the data store
		public boolean put(K key, V usr) { // Worse case O(n), best case O(1)
			if (key == null) {
				return false; // No Key was supplied so return false
			}
			
			int location = hash(key); // Hash the key so we know the location to store the Key-Value Pair
			
			// Construct a new Key-Value Pair Object consisting of the key and value where the next object points to null
			userEntry<K,V> newUser = new userEntry<K,V>(key, usr, null);

			if(table[location] == null) { // There are no users currently who's hashed value locates here
				table[location] = newUser; // Add the Key-Value Pair Object to the first slot
				count++; // Increase the counter for number of users
				return true; // Return that the user was added successfully
			} else {
				userEntry<K,V> current = table[location]; // Get the first Key-Value Pair from the current pointer in the array
				while(current.next != null){ // Traverse through the keys
					if(current.key.equals(key)) { // If the key we are trying to store is found in the data structure
						return false; // Return false as this probably means that the user id is not unique				
					}
					current = current.next; // Get the next key-value pair
				}
				// Keep going until we reach the end of the list of keys
				current.next = newUser; // We are at the end of the list so assign the next space to this user (key-value pair)
				count++; // Increase the counter for number of users
				return true; // Return that the user was added successfully
			}
		}
		
		// Retrieving a user object from the data store
		public V get(K key) { // Worse case O(n), best case O(1)
			int location = hash(key); // Hash the key value
			if (table[location] == null) { // If the location in the array returns null, then there are no user objects here
				return null; // Return null as there are no user objects here
			} else {
				userEntry<K,V> temp = table[location]; // Get the first key-value pair object and assign it to a temporary object
				while(temp != null) { // Traverse through the keys
					if (temp.key.equals(key)) { // If the key is the one we are looking for, then
						return temp.value; // Return the user object
					}
					temp = temp.next; // Get the next key-value pair
				} // Keep traversring until we get to a null object
				return null; // Reached the end of the list, the user is not here so return null
			}
		}
		
		// Get the current number of users
		public int getSize() {
			return count; // Return the count variable as that keeps count of the number of users
		}
		
		// Get all the users
		public User[] searchThrough() {
			int maxSize = getSize(); // The number of users we have is kept count of
			User[] users = new User[maxSize]; // Use size to set our array
			int counter = 0; // Set a counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of userEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					userEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					
					while(temp != null) { // Traverse through the list
						users[counter++] = (User) temp.value; // Add each non null values as users to the array we created and increase the counter
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return users; // Return the array
		}
		
		// Get all the users who contain the query
		public User[] name(String query) {
			int maxSize = getSize(); // Get the maximum possible size we could have
			User[] users = new User[maxSize]; // Create a new array with this fixed size
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of userEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					userEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					
					while(temp != null) { // Traverse through the list
						User tempUser = (User) temp.value; // Grab the value and assign it to a temp User
						String name = tempUser.getName(); // Get the name of the User from the User JavaDocs
						if (name.contains(query)) { // If the name contains the query, then we can add it to the array
							users[counter++] = (User) temp.value; // Add it to the array that we created and increase the counter
						}
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return users; // Return the array
		}
		
		// Get all the users who joined before the date specified
		public User[] dateBefore(Date dateBefore) {
			int maxSize = getSize(); // Get the maximum possible size we could have
			User[] users = new User[maxSize]; // Create a new array with this fixed size
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of userEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					userEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					
					while(temp != null) { // Traverse through the list
						User tempUser = (User) temp.value; // Grab the value and assign it to a temp User
						Date dateJoined = tempUser.getDateJoined(); // Get the date the User joined using the User JavaDocs
						if (dateJoined.before(dateBefore)) { // If the date they joined is before the date specified, then we can add it to the array
							users[counter++] = (User) temp.value; // Add it to the array that we created and increase the counter
						}
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return users; // Return the array
		}
		
		// Method to hash a key value
		private int hash(K key){ // The hash is modded by the capacity so we can be certain there won't be an array out of bounds exception
			return Math.abs(key.hashCode()) % capacity; // Return the positive value of the hash
		}
		
	}
	
	// Constructor for UserStore
	public UserStore() {
		userList = new HashMap<Integer, User>(); // Create a new instance of the HashMap, the key being an Integer, and the value the User object
	}
	
	// Quick sort algorithm which is used to sort the users by date joined
	public static void quickSort(User[] usersArray, int low, int n) {
		int lo = low; // Set the lowest index from the parameters
		int hi = n; // Set the number of elements to the highet index
		int middle = low + (n - low) / 2; // Get the middle element
		if (usersArray == null || usersArray.length == 0) { // If the array is null, or the length is 0
			return; // Return as there is nothing to sort now
		}
		if (low >= n) { // If the lowest index is greater than the number of elements, then
			return; // Return as there is nothing to sort
		}
		User pivot = usersArray[middle]; // Place the pivot as the middle element of users
		while (lo <= hi) { // Whilst, lo is less than or equal to high
			while (usersArray[lo].getDateJoined().after(pivot.getDateJoined())) { // While lower element joined more recently than the pivot, then
				lo++; // Increase the lo value && move to the next user
			} // Do this until we get to user who joined before the pivot user
			while (usersArray[hi].getDateJoined().before(pivot.getDateJoined())) { // While the user with the highest index joined before the pivot
				hi--; // Decrease the hi value && go down to the next user
			} // Do this until we come across a user who joined before the pivot user
			if (lo <= hi) { // If lo is still less than hi
				// Swapping Users
				User temp = usersArray[lo]; // Assign the user at index lo to a temp user
				usersArray[lo] = usersArray[hi]; // Move the user at index hi to the lo index
				usersArray[hi] = temp; // Move the temp user to the high index
				lo++; // Increase the lo index
				hi--; // Decrease the hi index
			}
		} // Keep doing this until lo > hi
		// Recursively sort the two parts
		if (low < hi) { // If the initial low index is still less than the hi index, then
			quickSort(usersArray, low, hi); // Sort the lower side of the pivot
		}
		if (n > lo) { // If the initial high value is still greater than lo index, then
			quickSort(usersArray, lo, n); // Sort the higher side of the pivot
		}
	}
	
	// Method to count the number of non null objects
	public int countNotNull(User[] array) {
		int counter = 0; // Set the counter to 0
		for (int i = 0; i < array.length; i++) { // Traverse through the specified array
			if (array[i] != null) { // If the element is not null, then
				counter++; // Increase the counter
			}
		}
		return counter; // Return the number of non-null objects
	}
	
	// Method to add a user to the data store
	public boolean addUser(User usr) {
		return userList.put(usr.getId(), usr); // Add a user object to the data store using the unique id as the key and the user object as the value
	}

	// Method to get a specified User Object by their unique ID
	public User getUser(int uid) {
		return userList.get(uid); // Return the user object using their unique ID as the key
	}
	
	// Method to get the array of users
	public User[] getUsers() {
		User[] usersArray = userList.searchThrough().clone(); // Use the HashMap to get all the users, as there is a method in there already
		quickSort(usersArray, 0, usersArray.length-1); // Sort the array so that the users are ordered - most recent first
		return usersArray; // Return the sorted array
	}
	
	// Method to get the array of users containing a certain string
	public User[] getUsersContaining(String query) {
		User[] usersArray = userList.name(query).clone(); // Use the HashMap to generate all the users who contain the query string
		int length = countNotNull(usersArray); // Count the number of elements in the array which aren't null
		User[] containUsers = new User[length]; // Create a new array which takes the number of non null elements
		for (int i = 0; i < length; i++) {
			containUsers[i] = usersArray[i]; // Copy the users which aren't null into the new array
		}
		quickSort(containUsers, 0, length-1); // Sort the array so that the users are ordered - most recent first
		return containUsers; // Return the sorted array
	}
	
	// Method to get the array of users before a certain date
	public User[] getUsersJoinedBefore(Date dateBefore) {
		User[] usersArray = userList.dateBefore(dateBefore).clone(); // Use the HashMap to generate all the users who joined before the date
		int length = countNotNull(usersArray); // Count the number of elements in the array which aren't null
		User[] beforeUsers = new User[length]; // Create a new array which takes the number of non null elements
		for (int i = 0; i < length; i++) {
			beforeUsers[i] = usersArray[i]; // Copy the users which aren't null into the new array
		}
		quickSort(beforeUsers, 0, length-1); // Sort the array so that the users are ordered - most recent first
		return beforeUsers; // Return the sorted array
	}

}
