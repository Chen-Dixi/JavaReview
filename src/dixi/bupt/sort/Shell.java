package dixi.bupt.sort;

/**
 * 希尔排序
 * 时间复杂度
 * 最坏情况O(N^2)，有效改善大数组，无序程度高，向前插入一次导致移动操作大。希尔排序对于增量序列选择十分钟重要。
 * @param <T>
 */
public class Shell<T extends Comparable<T>> extends Sort<T>  {
    /**
     * CS-Notes的代码
     * @param nums
     */
    @Override
    public void sort(T[] nums) {
        int N = nums.length;
        int h = 1;

        while (h < N / 3) {
            h = 3 * h + 1; // 1, 4, 13, 40, ...
        }

        while (h >= 1) {
            for (int i = h; i < N; i++) {
                for (int j = i; j >= h && less(nums[j], nums[j - h]); j -= h) {
                    swap(nums, j, j - h);
                }
            }
            h = h / 3;
        }
    }


    public void sort1(T[] nums) {
        if(nums==null || nums.length < 2) return;

        for(int D = nums.length / 2; D >= 1; D /= 2)//希尔增量，
        {
            for(int i = D; i < nums.length; i++){
                int j = i;
                T temp = nums[j];//要准备插入的值
                while(j-D >= 0 && less(nums[j], nums[j-D])){
                    swap(nums, j-D, j);
                    j = j-D;
                }
            }
        }
    }

}
