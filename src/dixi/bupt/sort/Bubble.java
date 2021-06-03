package dixi.bupt.sort;

/**
 * 算法分析：
 * 时间复杂度，O(n^2)，最坏情况是数组倒序。所有轮次必须执行完，比较次数是n-1 + n-2 +...+ 1 = n(n-1)/2。交换次数与比较次数相同，所以时间复杂度为O(N^2)
 * 额外空间复杂度：没有用其他辅助空间，O(1)
 * 稳定性：每次都和相邻元素比较，稳定
 * @param <T>
 */
public class Bubble<T extends Comparable<T>> extends Sort<T> {
    /**
     * 从左到右不断交换相邻元素，循环之后，让未排序的最大元素上浮到右侧
     * 注意，一轮循环中，如果没有发生交换，说明数组已经是有序的了
     * @param nums
     */
    @Override
    public void sort(T[] nums) {
        int N = nums.length;
        boolean isSorted = false;
        for( int i = N-1; i > 0 && !isSorted; i--){
            isSorted = true;
            for(int j=0;j<i;j++){
                if(less(nums[j+1],nums[j])){
                    swap(nums,j+1,j);
                    isSorted = false;
                }
            }
        }
    }
    /**
     * 蒋迪的代码
     * @param nums
     */
    public void bubbleSort(T[] nums){
        if(nums==null||nums.length==0)
            return;
        //这个代码比较可以改进，如果没有发生交换，说明数组已经是有序的了，应该可以直接结束排序
        for(int i=nums.length-1;i>0;i--){
            for(int j=0;j<i;j++){
                if(less(nums[j+1],nums[j]))
                    swap(nums,j+1,j);
            }
        }
    }
}
