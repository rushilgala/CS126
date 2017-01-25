/**
* The FollowerStore is implemented by using two HashMaps which uses a Key-Value Pair. One Map contains the followers of each user and the other map contains the users each user follows
* Each HashMap contains an Integer as a key which is the user's unique id, and the value is a linked list where each node in the linked list contains a user and the date they followed.
* The reason behind this is so that instead of traversing the whole map for either followers or follows, each one can be done respectively.
* My implementation of the HashMap sdoes not contain the remove operation, since its not required for it, but does have a put and get operation.
* The put and get methods are just there to put and get the linked lists.
* Using an initial capacity of 128 means that both hashmaps will be balanced as the user id's are modded after they are hashed.
* Inserting a follower/follows requires one to check if there is a LinkedList which has the best case O(1) and worst case O(n) 
* After which, inserting the data about the user, requires comparing therefore best case O(1) and worst case O(n).
* This means the total worse case will be O(n) and best case would be O(1)
* Hashing the unique Id means that there is a reduced chance of collisions
* The reason hash maps were chosen were because there's no fixed size, therefore they are scalable
* @author_name: Rushil Gala-Shah
* @author: 1515140
*/

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.Weet;
import uk.ac.warwick.java.cs126.models.User;

import java.util.Date;

public class FollowerStore implements IFollowerStore {
	private HashMap<Integer,LinkedList> followersList; // HashMap containing the users following a user
	private HashMap<Integer,LinkedList> followsList; // HashMap containing the users a user follows
	
	class UserFollow { // Inner class UserFollow object containing the user's ID, the Date the follow happens and who they followed/are following.
		private int uid; // The user's ID who is being stored
		private Date dateFollowed; // The date on which the follow happened
		private int owner; // The owner of the linked list of which this object resides in
		
		// Constructor for when a new instance of UserFollow is made
		public UserFollow(int uid, Date dateFollowed, int owner) {
			this.uid = uid;
			this.dateFollowed = dateFollowed;
			this.owner = owner;
		}
		
		// Method to retrieve the user's id
		public int getUid() {
			return this.uid;
		}
		
		// Method to retrieve the owner's unique id
		public int getOwner() {
			return this.owner;
		}
		
		// Method to retrieve the date the follow happened
		public Date getDateFollowed() {
			return this.dateFollowed;
		}
		
	}

	class LinkedList { // Inner class LinkedList to contain a sorted list of all the users a user follows or all the users that follow a user
		private Node head; // head variable - the first element in the linked list object
		private int count; // Count keeps track of the number of users in the list
		
		@SuppressWarnings("unchecked")
		public LinkedList() { // Constructor
			this.head = null; // Give head a null value since there's no users in here
			this.count = 0; // Reset the number of follow(er)s to 0
		}
		
		class Node { // Each UserFollow object stored within an inner class
			UserFollow userFollow; // The UserFollow object
			Node next; // The next Node in the list
			
			// Constructors
			public Node(UserFollow userFollow, Node next) { // When we know which the next node is going to be
				this.userFollow = userFollow;
				this.next = next;
			}
			
			public Node(UserFollow userFollow) { // When we know the node is going to be at the end of a list
				this.userFollow = userFollow;
				this.next = null;
			}
			
			// Method to get the UserFollow object
			public UserFollow getUserFollow() {
				return this.userFollow;
			}
			
			// Method to set the UserFollow object
			public void setUserFollow(UserFollow userFollow) {
				this.userFollow = userFollow;
			}
			
			// Method to get the next Node
			public Node getNext() {
				return this.next;
			}
			
			// Method to set the next Node
			public void setNext(Node e) {
				this.next = e;
			}
			
		}
		
		// Method to check whether the list is empty
		public boolean isEmpty() {
			return (head == null); // Returns true if the list is empty, false if not
		}
		
		// Method to get the owner of the sorted linked list
		public int getOwner() {
			if (!isEmpty()) { // If the list is not empty
				return head.getUserFollow().getOwner(); // Get the first elements userfollow object's owner
			}
			return 0; // Return 0 if we call this method and there is no owner/list is empty
		}
		
		// Method to add a new userFollow Object the linked list
		public boolean add(UserFollow newUserFollow) {
			Node newFollow = new Node(newUserFollow); // Create a new node, with next set to null
			
			if (isEmpty()) { // If the list is currently empty...
				this.head = newFollow; // Set the head to this userFollow object
				increaseCount(); // Increase the count as we have added a new follow
				return true; // Return true as we have successfully added to the data store
			} else {
				// Head element contains something, therefore the list is not empty
				// Perform a check to see if we already have this uid...
				Node user = this.head;
				while (user != null) { // Traverse through the elements
					if (newUserFollow.getUid() == user.getUserFollow().getUid()) { // If we find a match
						return false; // Then return false, as we already added this user follow
					}
					user = user.getNext(); // Get the next one, until we hit the end of the list
				}
				
				// Loop through the userFollow objects until there is a date that is before than the one being added
				Node before = null;
				Node current = this.head;
				for (; newUserFollow.getDateFollowed().before(current.getUserFollow().getDateFollowed()); before = current, current = current.getNext()) {
					if (current.getNext() == null) { // If we reached the end before finding one before, this means this userfollow was one of the first to follow
						current.setNext(newFollow); // Add the userfollow to the end of the list
						increaseCount(); // Increase the count
						return true; // Return true to indicate we have successfully added to the data store
					}
				}
				if (before != null) { // If before isn't null, then we have are in the middle of the list somewhere
					before.setNext(newFollow); // Set before's link as the userFollow
					newFollow.setNext(current); // Set userfollow's link to current so we get before -> newFollow -> current
				} else { // Before is null therefore the date is more recent than the current head
					newFollow.setNext(this.head); // Set the current head as this object's next node
					this.head = newFollow; // This userfollow is now the new head element
				}
				increaseCount(); // We've successfully added it & sorted the newFollow, so increase the count
				return true; // Return true as we have successfully added to the data store
			}
		}
		
		// Method to increase the count, as count variable is private
		private void increaseCount() {
			count++; // Increases the count
		}
		
		// Method to get the UserFollow object based on user id
		public UserFollow get(int uid) {
			Node current = head; // Set the head as a temp variable
			while (current.getNext() != null) { // Traverse through the list
				if (current.getUserFollow().getUid() == uid) { // If we find a match
					return current.getUserFollow(); // Return the userfollow object
				}
				current = current.getNext(); // Get the next element in the list
			}
			return null; // If we didn't find any matches, return null
		}
		
		// Method to get the size of the list i.e. The number of follow(er)s someome has
		public int size() {
			return count; // Return the count variable as its been keeping track everytime we added someone
		}
		
		// Method to return all the follow(er)s' unique id's
		public int[] getFollow() {
			int counter = 0; // Set counter to 0
			int[] list = new int[size()]; // Set the size to the number of followers
			Node current = head; // Set the head to a temp variable
			
			while (current != null) { // Until we hit a null point,
				list[counter++] = current.getUserFollow().getUid(); // Add each user id to a list
				current = current.getNext(); // Get the next element
			}
			return list; // Return the array - we don't need to sort as we sorted on insertion
		}
		
		// Method to check if we have a follower
		public boolean checkFollow(int uid) { 
			return (get(uid)!=null); // Checks to see if we can use a get method and return a userfollow object which isn't null
		}
		
	}
	
	class HashMap<K,V> { // Inner class HashMap for ADT implmentation
		private followEntry<K,V>[] table; // Array of Nodes which will contain the key (unique id) and value (the linkedlist object)
		private int capacity = 128; // Initial Capacity of HashMap
		private int count; // Variable for counting how many linkedlists we currently have
		
		class followEntry<K,V> { // Each Key-Value Pair object inner class
			K key; // Key - the hashed value of the unique id
			V value; // The linked list object
			followEntry<K,V> next; // The next Key-Value Pair in the list
			
			// Constructor for when a new Key-Value Pair is initialized
			public followEntry(K key, V value, followEntry next) {
				this.key = key;
				this.value = value;
				this.next = next;
			}
			
		}
		
		@SuppressWarnings("unchecked")
		public HashMap() { // Constructor for HashMap
			this.table = new followEntry[capacity]; // Create the initial array of nodes using the capacity given
			this.count = 0; // Set the number of linked lists to 0
		}
		
		// Method to hash a key value
		private int hash(K key){ // The hash is modded by the capacity so we can be certain there won't be an array out of bounds exception
			return Math.abs(key.hashCode()) % capacity; // Return the positive value of the hash
		}
		
		// Method to increase the counter
		private void increaseCount() {
			count++; // Increases the variable count by 1
		}
		
		// Method to get the number linked lists
		public int getSize() {
			return count; // Returns the number of linked lists in the hash map
		}
		
		// Method to put a new linked list in the hash map
		public boolean put(K key, V list, UserFollow newFollower) { // Worse case O(n), best case O(1)
			if (key == null) {
				return false; // No Key was supplied so return false
			}
			
			int location = hash(key); // Hash the key so we know the location to store the Key-Value Pair
			
			// Construct a new Key-Value Pair Object consisting of the key and value where the next object points to null
			followEntry<K,V> newFollow = new followEntry<K,V>(key, list, null);

			if (table[location] == null) { // If there are no linked lists who's hashed value locates here
				table[location] = newFollow; // Add the Key-Value Pair Object to the first slot
				increaseCount(); // Increase the counter for number of linked lists
				return add(key,newFollower); // Add the user who follow into this linked list, and return true if successful
			} else {
				followEntry<K,V> current = table[location]; // Get the first Key-Value Pair from the current pointer in the array
				while(current.next != null){ // Traverse through the keys
					if(current.key.equals(key)) { // If the key we are trying to store is found in the data structure
						return add(key,newFollower); // We already have the linked list here, so let's attempt to add the new user follow(er) to this list					
					}
					current = current.next; // Get the next element
				}
				// Keep going until we reach the end of the list of keys
				current.next = newFollow; // We are at the end of the list so assign the next space to this linked list (key-value pair)
				increaseCount(); // Increase the counter for number of linked lists
				return add(key,newFollower); // Add the user who follow into this linked list, and return true if successful
			}
		}
		
		// Method to get the linked list from the data store
		public V get(K key) { // Worse case O(n), best case O(1)
			int location = hash(key); // Hash the key value
			if (table[location] == null) { // If the location in the array returns null, then there are no linked lists objects here
				return null; // Return null as there are no linked lists objects here
			} else {
				followEntry<K,V> temp = table[location]; // Get the first key-value pair object and assign it to a temporary object
				while(temp != null) { // Traverse through the keys
					if (temp.key.equals(key)) { // If the key is the one we are looking for, then
						return temp.value; // Return the linked list object
					}
					temp = temp.next; // Get the next key-value pair
				} // Keep traversring until we get to a null object
				return null; // Reached the end of the list, the linked list is not here so return null
			}
		}
		
		// Method to add user follow object to the linked list
		private boolean add(K key, UserFollow userFollow) {
			LinkedList temp = (LinkedList) get(key); // Get the linked list object from the key
			return temp.add(userFollow); // Attempt to use the add method in linkedlist class to add the user follow
		}
		
		// Method to get the size of a list
		public int listSize(K key) {
			LinkedList temp = (LinkedList) get(key); // Get the linked list object from the key
			if (temp == null) { // If the list cannot be found
				return 0; // Return 0 as the size
			}
			return temp.size(); // Return the size of the list using the size method in the linked list class
		}
		
		// Method to get the users of a specific user
		public int[] getFollow(K key) {
			LinkedList temp = (LinkedList) get(key); // Get the linked list object from the key
			if (temp == null) { // If the list cannot be found
				return null; // Return null
			}
			return temp.getFollow(); // Return the array containing the user id's using the getFollow method in the linked list class
		}
		
		// Method to check if a specific user is in a list
		public boolean checkFollow(K key, int uid) {
			LinkedList temp = (LinkedList) get(key); // Get the linked list object from the key
			if (temp == null) { // If there's no list
				return false; // Return false
			}
			return temp.checkFollow(uid); // Return whether a user id is found in the list or not using checkFollow method in the linked list class
		}
		
		// Method to get the top followers
		public int[][] getTop() {
			int maxSize = getSize(); // Get the total number of linked lists
			int[][] topUsers = new int[maxSize][2]; // Create a new 2d array which will contain the user's id and the number of followers they have
			int counter = 0; // Set the counter to 0
			for (int i = 0; i < capacity; i++) { // Traverse through each bucket
				if (table[i] != null) { // If the head element is null, then move to the next value of i
					followEntry<K,V> temp = table[i]; // Get the first key-value pair object and assign it to a temporary object
					while(temp != null) { // Traverse through the keys
						LinkedList tempList = (LinkedList) temp.value; // Set the value as a tempory linked list
						if (!tempList.isEmpty()) { // If the list is not empty, then 
							topUsers[counter][0] = tempList.getOwner(); // Get the owner of the linked list
							topUsers[counter][1] = tempList.size(); // Get the size of the linked list as that is the number of followers
							counter++; // Increase the counter
						}
						temp = temp.next; // Get the next pair
					}
				}
			}
			return topUsers; // Return the unsorted array
		}
		
	}
	
	// Sort method for the top followers method
	public static void sort(int[][] usersArray, int low, int n) {
		int lo = low; // Set the lowest index from the parameters
		int hi = n; // Set the number of elements to the highet index
		int middle = low + (n - low) / 2; // Get the middle element
		if (usersArray == null || usersArray.length == 0) { // If the array is null, or the length is 0
			return; // Return as there is nothing to sort now
		}
		if (low >= n) { // If the lowest index is greater than the number of elements, then
			return; // Return as there is nothing to sort
		}
		int pivot = usersArray[middle][1]; // Place the pivot as the middle element of the array
		while (lo <= hi) { // Whilst, lo is less than or equal to high
			while (usersArray[lo][1] > pivot) { // While lower element is more than the pivot, then
				lo++; // Increase the value && move to the next element
			} // Do this until we get to an element which is less than the pivot
			while (usersArray[hi][1] < pivot) { // While higher element is less than the pivot, then
				hi--; // Decrease the hi value && go down to the next element
			} // Do this until we get to an element which is more than the pivot
			if (lo <= hi) { // If lo is still less than hi
				// Swapping elements
				int tempId = usersArray[lo][0]; // Assign the element at index lo to a temp value
				int tempVal = usersArray[lo][1]; // Assign the element at index lo to a temp value
				usersArray[lo][0] = usersArray[hi][0]; // Move the element at index hi to the lo index
				usersArray[lo][1] = usersArray[hi][1]; // Move the element at index hi to the lo index
				usersArray[hi][0] = tempId; // Move the temp value to the high index
				usersArray[hi][1] = tempVal; // Move the temp value to the high index
				lo++; // Increase the lo index
				hi--; // Decrease the hi index
			}
		} // Keep doing this until lo > hi
		// Recursively sort the two parts
		if (low < hi) { // If the initial low index is still less than the hi index, then
			sort(usersArray, low, hi); // Sort the lower side of the pivot
		}
		if (n > lo) { // If the initial high value is still greater than lo index, then
			sort(usersArray, lo, n); // Sort the higher side of the pivot
		}
	}
	
	// Constructor
	public FollowerStore() {
		followersList = new HashMap<Integer, LinkedList>(); // Create a new instance of the HashMap, the key being an Integer, and the value the LinkedList object
		followsList = new HashMap<Integer, LinkedList>(); // Create a new instance of the HashMap, the key being an Integer, and the value the LinkedList object
	}
	
	// Method to count the number of non 0 objects
	public int countNotNull(int[] array) {
		int counter = 0; // Set the counter to 0
		for (int i = 0; i < array.length; i++) { // Traverse through the specified array
			if (array[i] != -1) { // If the element is found, then
				counter++; // Increase the counter
			}
		}
		return counter; // Return the number of non-0 objects
	}
	
	// Method to add a new Follower to followersList and a new Follows to followsList
	public boolean addFollower(int uid1, int uid2, Date followDate) {
		UserFollow newFollows = new UserFollow(uid2, followDate, uid1); // Create a new UserFollow object for followsList
		UserFollow newFollower = new UserFollow(uid1, followDate, uid2); // Create a new UserFollow object for followersList
		boolean checkFollowers = false; // Set the boolean check to false
		boolean checkFollows = false; // Set the boolean check to false
		LinkedList newFollowerList = new LinkedList(); // Create a new instance of the linked list which may be used in followers
		LinkedList newFollowsList = new LinkedList(); // Create a new instance of the linked list which may be used in follows
		checkFollowers = followersList.put(uid2,newFollowerList,newFollower); // Check if a linked list already exists && attempt to add a new follower
		checkFollows = followsList.put(uid1,newFollowsList,newFollows); // Check if a linked list already exists && attempt to add a new follows
		return (checkFollows && checkFollowers); // Return whether adding a new Follow/Follower was successful or not
	}
	
	// Method to get the followers of a specific user
	public int[] getFollowers(int uid) {
		int[] followers = followersList.getFollow(uid); // Call the method in the hash map to get the number of followers
		if (followers == null) { // If the array is null,
			return null; // Return null to avoid any errors
		}
		return followers; // Return the array
	}
	
	// Method to get the users a specific user follows
	public int[] getFollows(int uid) {
		int[] follows = followsList.getFollow(uid); // Call the method in the hash map to get the number of follows
		if (follows == null) { // If the array is null,
			return null; // Return null to avoid any errors
		}
		return follows; // Return the array
	}
	
	// Method to check whether a user is a follower of another user
	public boolean isAFollower(int uidFollower, int uidFollows) {
		return followersList.checkFollow(uidFollows,uidFollower); // Call a method in the hash map to check if one user follows another - returns true or a false
	}

	// Method to get the number of followers of a specific user
	public int getNumFollowers(int uid) {
		return followersList.listSize(uid); // Calls a method in the hashmap to return the size of a list
	}
	
	// Method that takes two arrays and gets the common elements into another array
	public int[] mutual(int[] uid1, int[] uid2) {
		int length = Math.min(uid1.length, uid2.length); // Takes the smaller length, as that will be the greatest size
		int[] mutual = new int[length]; // Create a new array using the length
		int count = 0; // Set the counter to 0
		for (int i = 0; i < uid1.length; i++) { // Go through the entire array
			for (int j = 0; j < uid2.length; j++) { // Go through the entire array
				if (uid2[j] == uid1[i]) { // Compare the two user ids,
					mutual[count++] = uid2[i]; // If they match, add the user id to the new array and increase the counter
				}
			}
		}
		return mutual; // Return the array
	}
	
	// Method to see the same followers between two users
	public int[] getMutualFollowers(int uid1, int uid2) {
		int firstSize = followersList.listSize(uid1); // Get the size of the list
		int secondSize = followersList.listSize(uid2); // Get the size of the list
		int[] firstUser = followersList.getFollow(uid1); // Get the followers of the user
		int[] secondUser = followersList.getFollow(uid2); // Get the followers of the user
		int[] mutualFollowers = mutual(firstUser, secondUser); // Use the mutual method to return the list of mutal user ids
		int count = countNotNull(mutualFollowers); // Count the number of non null elements
		int[] mutual = new int[count]; // Create a new array
		for (int i = 0; i < count; i++) {
			mutual[i] = mutualFollowers[i]; // Copy the non null elements to the new array
		}
		return mutual; // Return the array
	}
	
	// Method to see a list of users that two users both follow
	public int[] getMutualFollows(int uid1, int uid2) {		
		int firstSize = followsList.listSize(uid1); // Get the size of the list
		int secondSize = followsList.listSize(uid1); // Get the size of the list
		int[] firstUser = followsList.getFollow(uid1); // Get the follows of the user
		int[] secondUser = followsList.getFollow(uid2); // Get the follows of the user
		int[] mutualFollows = mutual(firstUser, secondUser); // Use the mutual method to return the list of mutal user ids
		int count = countNotNull(mutualFollows); // Count the number of non null elements
		int[] mutual = new int[count]; // Create a new array
		for (int i = 0; i < count; i++) {
			mutual[i] = mutualFollows[i]; // Copy the non null elements to the new array
		}
		return mutual; // Return the array
	}
	
	// Method to get a list of users sorted by those who have the most followers at the top
	public int[] getTopUsers() {
		int[][] topUsers = followersList.getTop(); // Use the method in the hash map class to get the array of all users and the number of followers they have
		sort(topUsers, 0, topUsers.length - 1); // Sorting by number of followers - biggest number first
		int[] sortedTopUsers = new int[topUsers.length]; // Create a new array to hold the sorted data
		for (int i = 0; i < sortedTopUsers.length; i++) {
			sortedTopUsers[i] = topUsers[i][0]; // Copy the sorted user id's into the new array
		}
		return sortedTopUsers; // Return the array
	}
	
}
