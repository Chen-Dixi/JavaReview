package dixi.bupt.microsoft;

public class Solution {

    public static void main(String[] args){

        int rows = 4;
        int cols = 4;

        int ans = findPath(rows, cols);
        System.out.println(ans); //20

        boolean[][] grid = new boolean[2][2];
        for(int i=0;i<2;i++)
            for(int j=0;j<2;j++){
                grid[i][j]=true;
            }
        //1 grid[0][1]=false;
//        grid[0][1]=false;
//        grid[1][0]=false;
        int ans2 = findPath(grid, 2,2);
        System.out.println(ans2); //20

//        dp[0] = true;
//        for(int i=1;i<=n;i++)
//            for(int j=i; j>=1; j--) {
//                if (isInDict(word(j,i)))  //isInDict用
//                    dp[i] = dp[j-1]; //状态转移
//            }
//        return dp[n];
    }

    public static int findPath(int rows, int cols){
        //
        int[][] dp = new int[rows][cols];

        for(int i=0;i<rows; i++){
            dp[i][0]=1;
        }

        for(int i=0;i<cols;i++)
            dp[0][i]=1;

        // State transition
        // dp[i][j] = dp[i-1][j]+dp[i][j-1]
        for(int i=1;i<rows;i++)
            for(int j=1;j<cols;j++){
                dp[i][j] = dp[i-1][j]+dp[i][j-1];
            }

        return dp[rows-1][cols-1];
    }

    public static int findPath(boolean[][] grid, int rows, int cols){
        //
        int[][] dp = new int[rows][cols];

        dp[0][0]= grid[0][0] ? 1 : 0;
        for(int i=1;i<rows; i++){
//            if(dp[i-1][0]==0) dp[i][0]=0;
            if(grid[i][0]==false)// stone
                dp[i][0]=0;
            else
                dp[i][0] = dp[i-1][0];
        }

        for(int i=1;i<cols; i++){
//            if(dp[i-1][0]==0) dp[i][0]=0;
            if(grid[0][i]==false)// stone
                dp[0][i]=0;
            else
                dp[0][i] = dp[0][i-1];
        }

        // State transition
        // dp[i][j] = dp[i-1][j]+dp[i][j-1]
        for(int i=1;i<rows;i++)
            for(int j=1;j<cols;j++){
                if(!grid[i][j])
                    dp[i][j] = 0;
                else
                    dp[i][j] = dp[i-1][j]+dp[i][j-1];
            }

        return dp[rows-1][cols-1];
    }

    public static int back_pack(int[] weights, int[] values, int S){
        //
        int N = weights.length;//

        int[][] dp = new int[N][S+1];

        // 1选 i: dp[i][j] = dp[i-1][j-weights[i]] + values[i];
        // 2不选i: dp[i][j] = dp[i-1][j];

        //初始状态 initial state
        for(int j=0;j<=S;j++){
            if(j<weights[0]) dp[0][j] = 0; //放不进去
            else dp[0][j] = values[0];
        }

        for(int i=1;i<N; i++){
            for(int j=0;j<=S;j++){
                if(j<weights[i]){ //j 容量 放不下这个i
                    dp[i][j] = dp[i-1][j];
                }else{
                    dp[i][j] = Math.max(dp[i-1][j-weights[i]] + values[i], dp[i-1][j]);
                }
            }
        }
        return dp[N-1][S];
    }
}
