package mergesort;

import java.util.Arrays;

/**
 * Class provides functionality to sort array of {@link Comparable} objects
 * using "Merge sort" method.
 */
public class ArraySorter {

	// Value of the list that is not an array index
	private static final int NIL = -1;
	
	// Copy of input array to use data from
	private Comparable[] array;
	
	/*
	 *  Instances of "data-list", "tail-list" and queue array.
	 *  This class uses array implementation of queue with fixed size.
	 */
	private int[] data, next, queue;
	
	/*
	 * Pointers used in the queue implementation.
	 * "qPush" points to the next empty element of the queue (where to add)
	 * "qPoll" points to the first element of the queue (next to poll).
	 */
	private int qPush, qPoll;
	
	/*
	 * Flags used in the queue implementation.
	 * "canPush" flags if element can be added to the queue.
	 * "canPoll" flags if element can be polled from the queue.
	 */
	private boolean canPush, canPoll;

	/**
	 * Sorts specified array, using "Merge sort" method.
	 * @param input - array to sort
	 */
	public void mergeSort(Comparable[] input) {
		
		// check if input array is null
		if (input == null)
			throw new IllegalArgumentException("Input array cannot be null!");
		
		// if input array is empty or contains only 1 element - there's no need to sort
		if (input.length < 2)
			return;
		
		// set up environment
		setUp(input);
		
		// variable to store final result list
		int list;
		while (true) {
			
			// poll two lists from queue
			int list1 = poll();
			int list2 = poll();
			
			// merge them and save result into created variable
			list = merge(list1, list2);
			
			// if queue is empty - there's nothing else to merge - break loop
			if (isEmpty())
				break;
			
			// if queue is not empty - add merged list to it
			add(list);
		}
		
		// write result
		writeBack(input, list);
	}

	/*
	 * Set up environment for merging.
	 */
	void setUp(Comparable[] input) {

		// Clone input array, to get data from
		array = Arrays.copyOf(input, input.length);
		
		/*
		 * Initialize arrays:
		 * "data" is the array of lists
		 * "next" is the array of list tails
		 * "queue" is the array for the queue implementation
		 */
		data = new int[input.length];
		next = new int[input.length];
		queue = new int[input.length];
		
		// fill array of tail with NIL values
		Arrays.fill(next, NIL);
		
		// fill array of lists and queue with indexes
		for (int i = 0; i < input.length; i++)
			data[i] = queue[i] = i;
		
		// set queue pointers to the first cell
		qPush = qPoll = 0;

		/*
		 * At this point queue is full.
		 * So set flags: can poll, but cannot push
		 */
		canPush = false;
		canPoll = true;
	}

	/*
	 * Write merge result to the specified array.
	 * And release resources.
	 */
	void writeBack(Comparable[] arr, int list) {

		// Iterate array and put sorted elements in it 
		for (int i = 0; i < arr.length; i++) {
			
			arr[i] = this.array[list];
			list = next[list];
		}
		
		/*
		 * Release resources.
		 */
		
		this.array = null;
		this.data = null;
		this.next = null;
		this.queue = null;
	}
	
	/*
	 * Merge specified lists
	 */
	int merge(int list1, int list2) {
		
		// Get values of the lists
		
		Comparable c1 = array[list1];
		Comparable c2 = array[list2];

		// compare values
		
		int small = list1, big = list2;
		if (c1 != c2 && c1 != null && c1.compareTo(c2) > 0) {

			small = list2;
			big = list1;
		}

		/*
		 * If small list has no tail - set big list to be small list tail
		 * If small list has tail - merge it with the big list and set result to be small list tail
		 */
		
		if (next[small] == NIL)
			next[small] = big;
		else
			next[small] = merge(next[small], big);
		
		// return small list with new tail
		
		return small;
	}
	
	/*
	 * Checks if queue is empty
	 */
	boolean isEmpty() {
		
		// array is empty if no elements can be polled from it
		// "canPoll" flag shows if any elements can be polled from queue
		// NOT "canPoll" means - empty
		
		return !canPoll;
	}
	
	/*
	 * Polls next elements from the queue
	 */
	int poll() {

		// check if element can be polled from the queue
		if (!canPoll)
			throw new IllegalStateException("Queue is Empty!");

		// poll element into variable and shift pointer
		int result = queue[qPoll++];
		
		// set canPush flag, cuz there's now at least one empty space in the queue
		canPush = true;
		
		/*
		 * If poll pointer went beyond array bounds - return it to the first position
		 */
		
		if (qPoll == queue.length)
			qPoll = 0;
		
		/*
		 * If poll pointer now points to the push pointer - then queue is empty
		 * Set flag
		 */
		
		if (qPoll == qPush)
			canPoll = false;
		
		// return previously saved element
		
		return result;
	}
	
	/*
	 * Adds element to the queue.
	 */
	void add(int list) {
		
		// check if element can be added to the queue
		if (!canPush)
			throw new IllegalStateException("Queue capacity is excided!");
		
		// add element into the cell push-pointer points to and shift pointer
		queue[qPush++] = list;
		
		// set poll-flag, cuz there's now at least one element in the queue
		canPoll = true;
		
		/*
		 * If poll pointer went beyond array bounds - return it to the first position
		 */
		
		if (qPush == queue.length)
			qPush = 0;
		
		/*
		 * If push pointer now points to the poll pointer - then queue is full
		 * Set flag
		 */
		
		if (qPush == qPoll)
			canPush = false;
	}
	
	public static void main(String[] args) {
		
		ArraySorter as = new ArraySorter();
		Integer[] arr = new Integer[100];
		for (int i = 0; i < arr.length; i++)
			arr[i] = (int) (Math.random() * 100);
		
		as.mergeSort(arr);
		System.out.println(Arrays.toString(arr));
	}
}