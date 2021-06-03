package dixi.bupt.lazada;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Solution {

    public static class TreeNode{
        TreeNode left;
        TreeNode right;
        int val;
        public TreeNode(int val){
            this.val = val;
            this.left = null;
            this.right = null;
        }

        public TreeNode(int val, TreeNode left, TreeNode right){
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static void main(String[] args){
        TreeNode left = new TreeNode(2);
        TreeNode right = new TreeNode(3);
        TreeNode root = new TreeNode(1, left,right);
        TreeNode left_right = new TreeNode(4);
        left.right = left_right;
        List<TreeNode> res = inorder(root);
        for(TreeNode node : res){
            System.out.println(node.val);
        }
    }

    private static List<TreeNode> inorder(TreeNode root){
        Stack<TreeNode> stack = new Stack<TreeNode>();
        // root == null
        if(root==null){
            return null;
        }
        List<TreeNode> inorderTraversal = new ArrayList<>();
        TreeNode node=root;
        while(!stack.empty() || node!=null){
            while(node!=null){
                stack.push(node);
                node = node.left;
            }
            if(!stack.empty()){
                node = stack.pop();
                inorderTraversal.add(node);
                node = node.right;
            }
        }
        return inorderTraversal;
    }
}
