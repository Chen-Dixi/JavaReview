package dixi.bupt.sort;

public class HeapSort <T extends Comparable<T>> extends Sort<T>{


    @Override
    public void sort(T[] nums) {
        int N = nums.length - 1;
        //叶子节点不需要进行下沉，
        for (int k = (N-1) / 2; k >= 0; k--)
            sink(nums, k, N);

        while (N > 0) {
            swap(nums, 0, N--);
            sink(nums, 0, N);

        }
    }

    private void sink(T[] nums, int k, int N) {
        //注：N  = nums.length - 1; k 是当前节点索引
        while(2*k+1<=N){
            int j = 2*k + 1;
            if(j < N && less(nums, j,j+1))
                j++;

            if(!less(nums, k, j))
                break;
            swap(nums, k,j);
            k = j;
        }
    }

    // 小顶堆
    private void sinkForMinHeap(T[] nums, int k, int N)
    {
        while(2*k + 1 <= N)
        {
            int j = 2*k + 1;
            if(more(nums, j, j+1)){
                j++;
            }
            if(!more(nums, k, j)) break;
            swap(nums, k, j);
            k=j;
        }
    }
    // sink 最小堆

    private boolean less(T[] nums, int i, int j) {
        return nums[i].compareTo(nums[j]) < 0;
    }

    private boolean more(T[] nums, int i, int j) { return nums[i].compareTo(nums[j]) > 0; }
}
