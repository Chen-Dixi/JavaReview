package dixi.bupt.sort;

//选择排序

/**
 * 选择排序，时间复杂度是O(n^2)，空间复杂度是O(1)。没有借助额外空间。时间复杂度没有最好最坏情况，因为他都要比较这么多次，
 * 稳定性 键值相同的两个元素，排序后他们的相对位置不发生改变
 * 5 4 5 3 6 7 ,选择排序的第一个5和3交换，变成3 4 5 5 6 7，两个5的相对位置发生改变
 * @param <T>
 */
public class SelectSort<T extends Comparable<T>> extends Sort<T> {
    /**
     * CS-Notes的代码
     * @param nums
     */
    @Override
    public void sort(T[] nums) {
        //选择最小的放在里面
        int N = nums.length;
        for(int i=0;i<N-1;i++){
            int min = i;
            for(int j=i+1;j<N;j++)
            {
                if(less(nums[j],nums[min]));
                    min=j;
            }
            swap(nums,i,min);
        }
    }

    /**
     * 蒋迪的代码
     * @param nums
     */
    public void selectionSort(T[] nums){
        if(nums==null || nums.length<2) return;

        for(int i=0;i<nums.length-1;i++){ //选择排序，i从[0～n-2]这样来。
            int min=i;
            for(int j=i+1;j<nums.length;j++)
            {
                min = less(nums[j],nums[min]) ? j:min;
            }
            swap(nums, i,min);
        }
    }
}
