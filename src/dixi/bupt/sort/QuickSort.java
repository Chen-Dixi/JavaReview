package dixi.bupt.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 最好时间复杂度，O(logN)。每次选的基数正好等分当前数列
 * @param <T>
 */
public class QuickSort<T extends Comparable<T>> extends  Sort<T> {
    public void sort(T[] nums)
    {
        shuffle(nums);
        quicksort(nums, 0, nums.length - 1);
    }

    public void quicksort(T[] nums, int l, int r)
    {
        if(l<r){
            int p = partition(nums, l,r);
            quicksort(nums, l,p-1);
            quicksort(nums, p+1,r);
        }
    }

    private void shuffle(T[] nums) {
        List<Comparable> list = Arrays.asList(nums);
        Collections.shuffle(list);
        list.toArray(nums);
    }

    private int partition(T[] nums, int l, int r)
    {
        T x = nums[r];
        int i = l-1;
        for(int j=l;j<r;j++){
            if(less(nums[j],x)){
                i++;
                swap(nums, i, j);
            }
        }
        swap(nums, i+1, r);
        return i+1;
    }

}
