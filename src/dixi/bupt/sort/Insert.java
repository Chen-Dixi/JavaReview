package dixi.bupt.sort;

/**
 * 时间复杂度。 最坏情况完全逆序，每次都要和左边的所有元素进行比较，结果为O(n^2)。最好情况就是已经排好序了，只需要和前面一个元素比较。最好结果为O(n)
 * 最坏情况，每次插入一个元素，需要和左边所有元素比较，
 * 空间复杂度，O(1)
 * 稳定性，稳定的，其他东西不受影响
 * @param <T>
 */
public class Insert<T extends Comparable<T>> extends  Sort<T> {
    /**
     * 插入排序，每次交换相邻元素，插入排序需要交换的次数，就是逆序对的数量
     * @param nums
     */
    public void sort(T[] nums){
        int N = nums.length;
        //扑克牌整理顺序
        for(int i=1;i<N;i++){//从第二张牌开始，和前面的牌交换
            for(int j=1; j > 0 && less(nums[j],nums[j-1]); j--){ //左边已经排好序了，如果遇到左边的牌比当前牌小，就不用继续了
                swap(nums, j,j-1);
            }
        }
    }
}
