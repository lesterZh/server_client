import java.util.List;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {

	static int[] arr = {22,18,9,8,7,1,5,4,3,2,6};
	public static void main(String[] args) {
		for (char a='a';a<='z';a++) {
			System.out.print(a);
		}
		for (char a='A';a<='Z';a++) {
			System.out.print(a);
		}
	}
		
	static void bubbleSort(int[] arr) {
		int len = arr.length;
		for (int i=len-2; i>=0; i--) {
			for (int j=0; j<=i; j++) {
				if (arr[j] > arr[j+1]) {
					swap(arr, j, j+1);
				}
			}
		}
	}

	static void selectSort(int[] arr) {
		int len = arr.length;
		for (int i=0; i<len-1; i++) {
			int min = i;
			for (int j=i+1; j<len; j++) {
				if (arr[j] < arr[min]) {
					min = j;
				}
			}
			if (min != i) 
				swap(arr, min, i);
		}
	}
	
	static void InsertSort(int[] arr) {
		int len = arr.length;
		for (int i=0; i<len; i++) {
			int k = i;
			while(k >= 1) {
				if (arr[k-1] > arr[k]) {
					swap(arr, k, k-1);
				} else {
					break;
				}
				k--;
			}
			
//		for (int j=i; j>=1; j--) {
//			if (arr[j-1] > arr[j]) {
//				swap(arr, j, j-1);
//			} else 
//				break;
//		}
			
		}
	}
	
	
	static int partion(int[] arr, int left, int right) {
		int mid = left - 1;
//		int id = left;
		while (left <= right) {
			if (arr[left] <= arr[right]) {
				swap(arr, left, ++mid);
			}
			left++;
		}
		return mid;
	}
	
	static void qSort(int[] arr, int left, int right) {
		if (left >= right) return;
		int mid = partion(arr, left, right);		
		qSort(arr, left, mid-1);
		qSort(arr, mid+1, right);
	}
	
	static void mergeSort(int[] arr, int left, int right) {
		if (left >= right) return;
		int mid = (left + right) / 2;
		mergeSort(arr, left, mid);
		mergeSort(arr, mid+1, right);
		merge(arr, left, mid, right);
	}
	
	static void mergeSort2(int[] arr) {
		int len = arr.length;
		int sl = 1, dl = 1;
		while (sl < len) {
			dl = sl * 2;
			int i = 0;
			while (i + dl < len) {
				merge(arr, i, i+sl-1, i+dl-1);
				i += dl;
			}
			if (i + sl < len) {
				merge(arr, i, i+sl-1, len-1);
			}
			sl = dl;
		}
	}
	
	private static void merge(int[] arr, int left, int mid, int right) {
		int[] temp = new int[right-left+1];
		int id = 0;
		int l = left, r = mid+1;
		while (l <= mid && r <= right) {
			if (arr[l] < arr[r]) {
				temp[id++] = arr[l++];
			} else {
				temp[id++] = arr[r++];
			}
		}
		
		while (l <= mid) {
			temp[id++] = arr[l++];
		}
		
		while (r <= right) {
			temp[id++] = arr[r++];
		}
		
		for (int i=0; i<temp.length; i++) {
			arr[i+left] = temp[i];
		}
	}
	
	static void heapSort(int[] arr) {
		int len = arr.length;
		for (int i=len/2; i>=0; i--) {
			heapAdjust(arr, i, len);
		}
		
		for (int i=len-1; i>0; i--) {
			swap(arr, i, 0);
			heapAdjust(arr, 0, i);
		}
	}

	private static void heapAdjust(int[] arr, int i, int len) {
		int child;
		while ((child = i*2 + 1) < len) {
			if (child + 1 < len && arr[child+1] > arr[child]) {
				child++;
			}
			
			if (arr[child] > arr[i]) {
				swap(arr, i, child);
				i = child;
			} else {
				break;
			}
		}
	}

	static void shellSort(int[] arr) {
		int len = arr.length;
		int feet = len / 2;
		while (feet > 0) {
			for (int i=feet; i<len; i++) {
				int insert = i;
				while (insert >= feet) {
					if (arr[insert-feet] > arr[insert]) {
						swap(arr, insert, insert-feet);
						insert -= feet;
					} else {
						break;
					}
				}
			}
			feet /= 2;
		}
	}
	
	private static void swap(int[] arr, int a, int b) {
		// TODO Auto-generated method stub
//		arr[a] = arr[a] ^ arr[b];
//		arr[b] = arr[b] ^ arr[a];
//		arr[a] = arr[a] ^ arr[b];
		
		if (arr[a] == arr[b]) return;
		arr[a] ^= arr[b];
		arr[b] ^= arr[a];
		arr[a] ^= arr[b];
		
//		int t = arr[a];
//		arr[a] = arr[b];
//		arr[b] = t;
	}
	
	
	static int getLcs(String s1, String s2) {
		int len1 = s1.length();
		int len2 = s2.length();
		
		int[][] dp = new int[len1+1][len2+1];
		for (int i=1; i<=len1; i++) {
			for (int j=1; j<=len2; j++) {
				if (s1.charAt(i-1) == s2.charAt(j-1)) {
					dp[i][j] = dp[i-1][j-1] + 1;
				} else {
					dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
				}
			}
		}
		getLcsStr(dp, s1, s2);
		return dp[len1][len2];
	}
	
	static void getLcsStr(int[][] dp, String s1, String s2) {
		int len1 = s1.length();
		int len2 = s2.length();
		StringBuilder sBuilder = new StringBuilder();
		while (len1 > 0 && len2 > 0) {
			if (s1.charAt(len1-1) == s2.charAt(len2-1)) {
				sBuilder.insert(0, s1.charAt(len1-1));
				len1--;
				len2--;
			} else if (dp[len1][len2] == dp[len1-1][len2]) {
				len1--;
			} else {
				len2--;
			}
		}
		System.out.println(sBuilder.toString());
	}
}
