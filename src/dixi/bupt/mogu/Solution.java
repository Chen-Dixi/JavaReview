package dixi.bupt.mogu;

import java.util.HashMap;

public class Solution {

    public static void main(String[] args){

    }

//    private static int maxDepth(TreeNode root){
//        if(root==null)
//            return 0;
//
//        int left = maxDepth(root.left);
//        int right = maxDepth(root.right);
//
//        return Math.max(left, right) + 1;
//    }=

    private static int[] twoSum(int[] nums, int target){
        HashMap<Integer, Integer> map = new HashMap<>();

        int length = nums.length;
        if(length<=1) return new int[0];

        map.put(nums[0],0);
        int[] ans = new int[2];
        for(int i=1;i<length;i++){
            int num = nums[i];
            if(map.containsKey(target-num)){
                ans[0]=num;
                ans[1]=nums[map.get(target-num)];
                return ans;
            }
            map.put(num,i);
        }

        return new int[0];
    }

}
