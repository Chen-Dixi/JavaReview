package dixi.bupt.microsoft;

import dixi.bupt.sort.HeapSort;
import dixi.bupt.sort.QuickSort;

public class Test {
    public static void main(String[] args){
        Integer[] nums = {3,2,1,2,3};
        findNext(nums);
        int lenth = nums.length;
        for(int i=0;i<lenth;i++){
            System.out.print(nums[i]);
        }
    }

    public static int findNext(Integer[] num){



        int flag = 1;
        int radix = 10;

        int length = num.length;
        QuickSort<Integer> sort = new QuickSort<>();
        int max =length-1;

        for(int i=length-2; i>=0; i--){
            if(num[i]<num[max]){
                swap(num, max, i);
                sort.quicksort(num, i+1, length-1);
                break;
            }else{
                max = i;
            }
        }
        return -1;


    }

    public static void swap(Integer []a, int i, int j){
        Integer t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
