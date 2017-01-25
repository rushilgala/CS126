/**
* The WeetStore is implemented by using a HashMap which uses a Key-Value Pair. The key value being the unique weet ID and the value being the Weet Object.
* My implementation of the HashMap does not contain the remove operation, since its not required for it, but does have a put and get operation.
* Using an initial capacity of 128 means that the hashmap will be balanced if the weet ids added are only incremementing by 1 each time.
* Inserting a Weet into the hashMap has the best case O(1) and worst case O(n) which is the same for searching for a weet
* Hashing the unique Id means that there is a reduced chance of collisions
* The reason a hash map was chosen was because there's no fixed size, therefore it is scalable
* @author_name: Rushil Gala-Shah
* @author: 1515140
*/

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;
import uk.ac.warwick.java.cs126.models.Weet;

import java.io.BufferedReader;
import java.util.Date;
import java.io.FileReader;
import java.text.ParseException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class WeetStore implements IWeetStore {
	private HashMap<Integer, Weet> weetHashMap; // Store the variable weetHashMap to type HashMap which takes Integers and Weets
	
	class HashMap<K,V> { // Inner class HashMap for ADT implementation
		private weetEntry<K,V>[] table; // Array of Nodes which will contain the key (unique id) and value (the weet object)
		private int capacity = 256; // Initial Capacity of HashMap
		private int count; // Variable for counting how many weets we currently have
		
		class weetEntry<K,V> { // Each Key-Value Pair object inner class
			K key; // Key - the hashed value of the unique id
			V value; // The Weet object
			weetEntry<K,V> next; // The next Key-Value Pair in the list
			
			// Constructor for when a new Key-Value Pair is initialized
			public weetEntry(K key, V value, weetEntry<K,V> next){
				this.key = key;
				this.value = value;
				this.next = next;
			}
		}
		
		@SuppressWarnings("unchecked")
		public HashMap() { // Constructor for HashMap
			this.table = new weetEntry[capacity]; // Create the initial array of nodes using the capacity given
			this.count = 0; // Set the number of weets to 0
		}
		
		// Adding a weet object to the data store
		public boolean put(K key, V weet) { // Worse case O(n), best case O(1)
			if (key == null) {
				return false; // No Key was supplied so return false
			}
			
			int location = hash(key); // Hash the key so we know the location to store the Key-Value Pair
			
			// Construct a new Key-Value Pair Object consisting of the key and value where the next object points to null
			weetEntry<K,V> newWeet = new weetEntry<K,V>(key, weet, null);

			if(table[location] == null) { //There are no weets currently who's hashed value locates here
				table[location] = newWeet; // Add the Key-Value Pair Object to the first slot
				count++; // Increase the counter for number of weets
				return true; // Return that the weet was added successfully
			} else {
				weetEntry<K,V> current = table[location]; // Get the first Key-Value Pair from the current pointer in the array
				while(current.next != null){ // Traverse through the keys
					if(current.key.equals(key)) { // If the key we are trying to store is found in the data structure
						return false; // Return false as this probably means that the weet id is not unique						
					}
					current = current.next; // Get the next key-value pair
				}
				// Keep going until we reach the end of the list of keys
				current.next = newWeet; // We are at the end of the list so assign the next space to this weet (key-value pair)
				count++; // Increase the counter for number of weets
				return true; // Return that the weet was added successfully
			}
		}
		
		// Retrieving a weet object from the data store
		public V get(K key) { // Worse case O(n), best case O(1)
			int location = hash(key); // Hash the key value
			if (table[location] == null) { // If the location in the array returns null, then there are no weet objects here
				return null; // Return null as there are no weet objects here
			} else {
				weetEntry<K,V> temp = table[location]; // Get the first key-value pair object and assign it to a temporary object
				while(temp != null) { // Traverse through the keys
					if (temp.key.equals(key)) { // If the key is the one we are looking for, then
						return temp.value; // Return the weet object
					}
					temp = temp.next; // Get the next key-value pair
				} // Keep traversring until we get to a null object
				return null; // Reached the end of the list, the weet is not here so return null
			}
		}
		
		// Get the current number of weets
		public int getSize() {
			return count; // Return the count variable as that keeps count of the number of weets
		}
		
		// Get all the weets
		public Weet[] getAll() {
			int maxSize = getSize(); // The number of weets we have is kept count of
			Weet[] weets = new Weet[maxSize]; // Use size to set our array
			int counter = 0; // Set a counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of weetEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					weetEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					while(temp != null) { // Traverse through the list
						weets[counter++] = (Weet) temp.value; // Add each non null values as weets to the array we created and increase the counter
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return weets; // Return the array
		}
		
		// Get all the weets by a specific user
		public Weet[] weetByUser(int uid) {
			int maxSize = getSize(); // Get the maximum possible size we could have
			Weet[] weets = new Weet[maxSize]; // Create a new array with this fixed size
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of weetEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					weetEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					while(temp != null) { // Traverse through the list
						Weet tempWeet = (Weet) temp.value; // Grab the value and assign it to a temp Weet
						int user_id = tempWeet.getUserId(); // Get the userID of the Weet from the Weet JavaDocs
						if (user_id == uid) { // If the user_id matches the one given in the parameters, then we can add it to the array
							weets[counter++] = (Weet) temp.value; // Add it to the array that we created and increase the counter
						}
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return weets; // Return the array
		}
		
		// Get all the weets who contain the query
		public Weet[] weetContaining(String query) {
			int maxSize = getSize(); // Get the maximum possible size we could have
			Weet[] weets = new Weet[maxSize]; // Create a new array with this fixed size
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of weetEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					weetEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					
					while(temp != null) { // Traverse through the list
						Weet tempWeet = (Weet) temp.value; // Grab the value and assign it to a temp Weet
						String message = tempWeet.getMessage(); // Get the contents of the Weet from the Weet JavaDocs
						if (message.contains(query)) { // If the contents contains the query, then we can add it to the array
							weets[counter++] = (Weet) temp.value; // Add it to the array that we created and increase the counter
						}
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return weets; // Return the array
		}
		
		// Get all the weets which happened on a certain date
		public Weet[] weetDateOn(Date dateOn) {
			int maxSize = getSize(); // Get the maximum possible size we could have
			Weet[] weets = new Weet[maxSize]; // Create a new array with this fixed size
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of weetEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					weetEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					
					while(temp != null) { // Traverse through the list
						Weet tempWeet = (Weet) temp.value; // Grab the value and assign it to a temp Weet
						Date dateWeeted = tempWeet.getDateWeeted(); // Get the date of when the Weet was made from the Weet JavaDocs
						if (dateWeeted.equals(dateOn)) { // If the dates are equal, then we can add it to the array
							weets[counter++] = (Weet) temp.value; // Add it to the array that we created and increase the counter
						}
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return weets; // Return the array
		}
		
		// Get all the weets which happened before a certain date
		public Weet[] weetDateBefore(Date dateBefore) {
			int maxSize = getSize(); // Get the maximum possible size we could have
			Weet[] weets = new Weet[maxSize]; // Create a new array with this fixed size
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < capacity; i++) { // For each number in the array of weetEntry, go through
				if (table[i] != null) { // If the first element is null, then we can skip this bit
					weetEntry<K,V> temp = table[i]; // Assign the head object to a temp variable
					
					while(temp != null) { // Traverse through the list
						Weet tempWeet = (Weet) temp.value; // Grab the value and assign it to a temp Weet
						Date dateWeeted = tempWeet.getDateWeeted(); // Get the date of when the Weet was made from the Weet JavaDocs
						if (dateWeeted.before(dateBefore)) { // If the date of the weet is before the specified date, then we can add it to the array
							weets[counter++] = (Weet) temp.value; // Add it to the array that we created and increase the counter
						}
						temp = temp.next; // Get the next object
					}
				} // Move on to the next i value
			}
			return weets; // Return the array
		}
		
		// Get the current trending weets
		public String[] weetGetTrending() {
			
			Weet[] weetArray = weetContaining("#"); // Get the array containing a '#' and assign it to the array
			String[] getTrend = new String[10]; // Create a new array which will contain the top 10
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < weetArray.length; i++) { // Go through the array containing '#' and nulls
				if (weetArray[i] != null) {
					counter++; // Count the number of non null elements
				}
			}
			Weet[] trendArray = new Weet[counter]; // Create a new array with the number of elements
			Object trending[][] = new Object[trendArray.length][2]; // Create a trending 2d array, which will contain the #, as well as the number of times it appears
			for (int i = 0; i < counter; i++) {
				trendArray[i] = weetArray[i]; // Put all the non null elements into the trendArray
			}
			
			for(int i = 0; i < trending.length; i++) {
				trending[i][0] = null; // Set all the strings to null
				trending[i][1] = 0; // Set all the counters to 0
			}
			// Put all the topics in trending array, increasing the count if its already in there...
			int k = 0; // Set k to 0
			boolean found = false; // Set found to false
			for (int i = 0; i < trendArray.length; i++) { // For every message in trending,
				String[] temp = trendArray[i].getMessage().split("\\s+"); // Split the message into words depending on the spaces
				for (int j = 0; j < temp.length; j++) { // For every word in the message
					k = 0; // Reset k to 0
					found = false; // Reset found to false
					if (temp[j].startsWith("#")) { // If the word begins with '#', then we have a trending option
						while (trending[k][0]!=null) { //Go through the trending array to see if its already stored
							if (trending[k][0].equals(temp[j])) {
								trending[k][1] = (int)trending[k][1] + 1; // If its already stored, increase the number ny 1
								found = true; // Change found to true
								break; // We found what we want, so lets break out
							}
							k++; // Increase k and keep going until we hit a null
						}
						if (!found) { // If we cannot find the trending word, then its a new word
							trending[k][0] = (String) temp[j]; // Add the word to the array
							trending[k][1] = (int) trending[k][1] + 1; // Increase the number of times it appears by 1
						}
					}
				}
			}
			
			sort(trending, 0, trending.length - 1);// Sort the trending array, so the larger the count, the closer to the 0 index...
			
			for (int i = 0; i < getTrend.length; i++) {
				getTrend[i] = null; // Initially set it to null
				getTrend[i] = (String) trending[i][0]; // Copy the 0th to 9th element to getTrend
			}
			return getTrend; // Return getTrend array which will have the top 10 trending topics
		}
		
		// quick sort a 2d array - used for trending weets
		public void sort(Object[][] trending, int low, int n) {
			int lo = low; // Set the lowest index from the parameters
			int hi = n; // Set the number of elements to the highet index
			int middle = low + (n - low) / 2; // Get the middle element
			if (trending == null || trending.length == 0) { // If the array is null, or the length is 0
				return; // Return as there is nothing to sort now
			}
			if (low >= n) { // If the lowest index is greater than the number of elements, then
				return; // Return as there is nothing to sort
			}
			int pivot = (int) trending[middle][1]; // Place the pivot as the middle element of trending's number value
			while (lo <= hi) { // Whilst, lo is less than or equal to high
				while ((int)trending[lo][1] > pivot) { // While lower element is more than the pivot, then
					lo++; // Increase the value && move to the next element
				} // Do this until we get to an element which is less than the pivot
				while ((int)trending[hi][1] < pivot) { // While higher element is less than the pivot, then
					hi--; // Decrease the hi value && go down to the next element
				} // Do this until we get to an element which is more than the pivot
				if (lo <= hi) { // If lo is still less than hi
					// Swapping elements
					Object tempName = trending[lo][0]; // Assign the element at index lo to a temp value
					Object tempVal = trending[lo][1]; // Assign the element at index lo to a temp value
					trending[lo][0] = trending[hi][0]; // Move the element at index hi to the lo index
					trending[lo][1] = trending[hi][1]; // Move the element at index hi to the lo index
					trending[hi][0] = tempName; // Move the temp value to the high index
					trending[hi][1] = tempVal; // Move the temp value to the high index
					lo++; // Increase the lo index
					hi--; // Decrease the hi index
				}
			} // Keep doing this until lo > hi
			// Recursively sort the two parts
			if (low < hi) { // If the initial low index is still less than the hi index, then
				sort(trending, low, hi); // Sort the lower side of the pivot
			}
			if (n > lo) { // If the initial high value is still greater than lo index, then
				sort(trending, lo, n); // Sort the higher side of the pivot
			}
		}
		
		// Method to hash a key value
		private int hash(K key){ // The hash is modded by the capacity so we can be certain there won't be an array out of bounds exception
			return Math.abs(key.hashCode()) % capacity; // Return the positive value of the hash
		}
	}
	
	// Constructor for WeetStore
	public WeetStore() {
		weetHashMap = new HashMap<Integer, Weet>(); // Create a new instance of the HashMap, the key being an Integer, and the value the Weet object
	}
	
	// Quick sort algorithm which is used to sort the weets by the date weeted
	public static void quickSort(Weet[] weetsArray, int low, int n) {
		int lo = low; // Set the lowest index from the parameters
		int hi = n; // Set the number of elements to the highet index
		int middle = low + (n - low) / 2; // Get the middle element
		if (weetsArray == null || weetsArray.length == 0) { // If the array is null, or the length is 0
			return; // Return as there is nothing to sort now
		}
		if (low >= n) { // If the lowest index is greater than the number of elements, then
			return; // Return as there is nothing to sort
		}
		Weet pivot = weetsArray[middle]; // Place the pivot as the middle element of weets
		while (lo <= hi) { // Whilst, lo is less than or equal to high
			while (weetsArray[lo].getDateWeeted().after(pivot.getDateWeeted())) { // While lower element weeted more recently than the pivot, then
				lo++; // Increase the lo value && move to the next weet
			} // Do this until we come across a weet that was weeted before the pivot weet
			while (weetsArray[hi].getDateWeeted().before(pivot.getDateWeeted())) { // While the weet with the highest index weeted before the pivot
				hi--; // Decrease the hi value && go down to the next weet
			} // Do this until we come across a weet before the pivot weet
			if (lo <= hi) { // If lo is still less than hi
				// Swapping Weets
				Weet temp = weetsArray[lo]; // Assign the weet at index lo to a temp weet
				weetsArray[lo] = weetsArray[hi]; // Move the weet at index hi to the lo index
				weetsArray[hi] = temp; // Move the temp weet to the high index
				lo++; // Increase the lo index
				hi--; // Decrease the hi index
			}
		} // Keep doing this until lo > hi
		// Recursively sort the two parts
		if (low < hi) { // If the initial low index is still less than the hi index, then
			quickSort(weetsArray, low, hi); // Sort the lower side of the pivot
		}
		if (n > lo) { // If the initial high value is still greater than lo index, then
			quickSort(weetsArray, lo, n); // Sort the higher side of the pivot
		}
	}

	// Method to count the number of non null objects
	public int countNotNull(Weet[] array) {
		int counter = 0; // Set the counter to 0
		for (int i = 0; i < array.length; i++) { // Traverse through the specified array
			if (array[i] != null) { // If the element is not null, then
				counter++; // Increase the counter
			}
		}
		return counter; // Return the number of non-null objects
	}

	// Method to add a weet to the data store
	public boolean addWeet(Weet weet) {
		return weetHashMap.put(weet.getId(), weet); // Add a weet object to the data store using the unique id as the key and the weet object as the value
	}
	
	// Method to get a specified Weet Object by their unique ID
	public Weet getWeet(int wid) {
		return weetHashMap.get(wid); // Return the weet object using their unique ID as the key
	}
	
	// Method to get the array of weets
	public Weet[] getWeets() {
		Weet[] weetsArray = weetHashMap.getAll().clone(); // Use the HashMap to get all the weets, as there is a method in there already
		quickSort(weetsArray, 0, weetsArray.length-1); // Sort the array so that the weets are ordered - most recent first
		return weetsArray; // Return the sorted array
	}
	
	// Method to get an array of weets by a certain user
	public Weet[] getWeetsByUser(User usr) {
		int user_id = usr.getId(); // Get the unique id of the User
		Weet[] weetsArray = weetHashMap.weetByUser(user_id); // Use the HashMap to generate all the weets made by a certain user
		int length = countNotNull(weetsArray); // Count the number of elements in the array which aren't null
		Weet[] userWeets = new Weet[length]; // Create a new array which takes the number of non null elements
		for (int i = 0; i < length; i++) {
			userWeets[i] = weetsArray[i]; // Copy the weets which aren't null into the new array
		}
		quickSort(userWeets, 0, length-1); // Sort the array so that the weets are ordered - most recent first
		return userWeets; // Return the sorted array
	}

	// Method to get an array of weets which contain a query
	public Weet[] getWeetsContaining(String query) {
		Weet[] weetsArray = weetHashMap.weetContaining(query); // Use the HashMap to generate all the weets which contain the query string
		int length = countNotNull(weetsArray); // Count the number of elements in the array which aren't null
		Weet[] containWeets = new Weet[length]; // Create a new array which takes the number of non null elements
		for (int i = 0; i < length; i++) {
			containWeets[i] = weetsArray[i]; // Copy the weets which aren't null into the new array
		}
		quickSort(containWeets, 0, length-1); // Sort the array so that the weets are ordered - most recent first
		return containWeets; // Return the sorted array
	}
	
	// Method to get an array of weets made on a certain date
	public Weet[] getWeetsOn(Date dateOn) {
		Weet[] weetsArray = weetHashMap.weetDateOn(dateOn); // Use the HashMap to generate all the weets made on a certain date
		int length = countNotNull(weetsArray); // Count the number of elements in the array which aren't null
		Weet[] onWeets = new Weet[length]; // Create a new array which takes the number of non null elements
		for (int i = 0; i < length; i++) {
			onWeets[i] = weetsArray[i]; // Copy the weets which aren't null into the new array
		}
		quickSort(onWeets, 0, length-1); // Sort the array so that the weets are ordered - most recent first
		return onWeets; // Return the sorted array
	}
	
	// Method to get an array of weets before a certain date
	public Weet[] getWeetsBefore(Date dateBefore) {
		Weet[] weetsArray = weetHashMap.weetDateBefore(dateBefore); // Use the HashMap to generate all the weets made before a certain date
		int length = countNotNull(weetsArray); // Count the number of elements in the array which aren't null
		Weet[] beforeWeets = new Weet[length]; // Create a new array which takes the number of non null elements
		for (int i = 0; i < length; i++) {
			beforeWeets[i] = weetsArray[i]; // Copy the weets which aren't null into the new array
		}
		quickSort(beforeWeets, 0, length-1); // Sort the array so that the weets are ordered - most recent first
		return beforeWeets; // Return the sorted array
	}
	
	// Method to get all trending topics in weets
	public String[] getTrending() {
		return weetHashMap.weetGetTrending(); // Get the trending weets using a method defined in the hashmap
	}
	
}
