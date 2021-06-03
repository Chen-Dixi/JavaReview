package dixi.bupt.sort;

import java.util.ArrayList;
import java.util.Collections;

public class BucketSort {
    public static void bucketSort(int[] arr){
        
        if(arr == null || arr.length < 2)
            return;

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for(int value: arr){
            max = Math.max(max, value);
            min = Math.min(min, value);
        }

        int bucketNum = (max - min)/arr.length + 1;
        ArrayList<ArrayList<Integer> > bucketArr = new ArrayList<>(bucketNum);

        for(int i=0; i < bucketNum; i++){
            bucketArr.add(new ArrayList<>());
        }

        for(int value : arr){
            int num = (value - min) / (arr.length);
            bucketArr.get(num).add(value);
        }

        for(ArrayList<Integer> integers: bucketArr){
            Collections.sort(integers);
        }

        int index = 0;
        for(ArrayList<Integer> integers: bucketArr)
            for(int integer: integers){
                arr[index++] = integer;
            }


    }
}
