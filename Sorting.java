package finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry; // You may (or may not) need it to implement fastSort

public class Sorting {

	/*
	 * This method takes as input an HashMap with values that are Comparable.
	 * It returns an ArrayList containing all the keys from the map, ordered
	 * in descending order based on the values they mapped to.
	 *
	 * The time complexity for this method is O(n^2) as it uses bubble sort, where n is the number
	 * of pairs in the map.
	 */
	public static <K, V extends Comparable<V>> ArrayList<K> slowSort (HashMap<K, V> results) {
		ArrayList<K> sortedUrls = new ArrayList<K>();
		sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls

		int N = sortedUrls.size();
		for(int i=0; i<N-1; i++){
			for(int j=0; j<N-i-1; j++){
				if(results.get(sortedUrls.get(j)).compareTo(results.get(sortedUrls.get(j+1))) < 0){
					K temp = sortedUrls.get(j);
					sortedUrls.set(j, sortedUrls.get(j+1));
					sortedUrls.set(j+1, temp);
				}
			}
		}
		return sortedUrls;
	}

	private static <K, V extends Comparable<V>> ArrayList<K> merge(ArrayList<K> list1, ArrayList<K> list2, HashMap<K, V> results) {
		K[] temp1 = (K[]) list1.toArray();
		K[] temp2 = (K[]) list2.toArray();
		ArrayList<K> list = new ArrayList<K>();

		int counter1 = 0;
		int counter2 = 0;

		while (counter1 < temp1.length && counter2 < temp2.length) {
			V element1 = results.get(temp1[counter1]);
			V element2 = results.get(temp2[counter2]);
			if (element1.compareTo(element2) > 0) {
				list.add(temp1[counter1]);
				counter1++;
			} else {
				list.add(temp2[counter2]);
				counter2++;
			}
		}
		while (counter1 < temp1.length) {
			list.add(temp1[counter1]);
			counter1++;
		}
		while (counter2 < temp2.length) {
			list.add(temp2[counter2]);
			counter2++;
		}
		return list;
	}

	private static <K, V extends Comparable<V>> ArrayList<K> mergeSort(ArrayList<K> keys, HashMap<K, V> results) {
		if (keys.size() <= 1) {
			return keys;
		}
		else {
			int mid = (keys.size() - 1) / 2;
			ArrayList<K> list1 = new ArrayList<K>();
			ArrayList<K> list2 = new ArrayList<K>();

			for (int i = 0; i <= mid; i++) {
				list1.add(keys.get(i));
			}

			for (int i = mid + 1; i < keys.size(); i++) {
				list2.add(keys.get(i));
			}
			ArrayList<K> newList1 = mergeSort(list1, results);
			ArrayList<K> newList2 = mergeSort(list2, results);
			return merge(newList1, newList2, results);
		}
	}


	/*
	 * This method takes as input an HashMap with values that are Comparable.
	 * It returns an ArrayList containing all the keys from the map, ordered
	 * in descending order based on the values they mapped to.
	 *
	 * The time complexity for this method is O(n*log(n)), where n is the number
	 * of pairs in the map.
	 */
	public static <K, V extends Comparable<V>> ArrayList<K> fastSort(HashMap<K, V> results) {
		ArrayList<K> unsortedUrls = new ArrayList<K>(results.keySet());
		return mergeSort(unsortedUrls, results) ;
	}
}