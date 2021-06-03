package dixi.bupt.sort;

/**
 * 归并排序的思想是将数组分成两部分，分别进行排序，然后归并起来。
 * 时间复杂度：
 * 合并操作是O(N),完全二叉树，深度log2(N)，最好最坏平均复杂度都是O(nlogn)。
 * 空间复杂度，使用了额外的空间，O(N)。
 * 稳定性，稳定，只需要归并时，同样大小的数字，第一个数列的数字先入列。
 * @param <T>
 */
public class MergeSort<T extends Comparable<T>> extends  Sort<T>  {
    protected  T[] aux;//这是归并排序使用的额外空间

    //归并操作
    protected void merge(T[] nums, int l, int m, int r)
    {
        int p=l, q=m+1,i=l;
        //拷贝
        for(int k=l; k<=r; k++){
            aux[k]=nums[k];
        }

        while(p<=m || q<=r)
        {
            if(q>r || (p<=m && less(aux[p],aux[q])))
                nums[i++]=aux[p++];
            else
                nums[i++]=aux[q++];
        }
    }

    public void sort(T[] nums){
        aux = (T[]) new Comparable[nums.length];
        sort(nums,0,nums.length-1);
    }

    private void sort(T[] nums, int l, int r){
        if(r<=l)return ;
        int m = (l+r)/2;
        sort(nums,l,m );
        sort(nums,m+1,r);
        merge(nums, l,m,r);
    }
}
