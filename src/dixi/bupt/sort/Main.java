package dixi.bupt.sort;


public class Main {

    public static void main(String[] args) {
        // write your code here
        HeapSort<Integer> heap = new HeapSort<>();

        Integer[] nums = {2,3,4,5,1,5,7,3,5,4,2,10};
        heap.sort(nums);
        for(Integer i : nums){
            System.out.println(i);
        }
    }
}