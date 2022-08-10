package dixi.bupt.lazada;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class WarOfSpecies {

    static int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    static int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);

        char[][] grid = new char[3][8];
        char[][] res = new char[3][8];
        for(int i=0; i < 3; i++)
        {
            String str = in.nextLine();
            grid[i] = str.toCharArray();
        }

        solve(grid, res);
        for(int i=0; i < 3; i++)
        {
           System.out.println(new String(res[i]));
        }

    }

    private static void solve(char[][] grid, char[][] res)
    {
        for(int i=0; i<3; i++)
            for(int j=0; j<8; j++)
            {
                if(grid[i][j]=='.')
                {
                    emptyCell(grid, res, i, j);
                }else{
                    nonEmptyCell(grid, res, i ,j);
                }
            }
    }

    private static void emptyCell(char[][] grid, char[][] res, int i, int j)
    {
        int X=0,O=0;
        for(int k = 0; k < 8; k++)
        {
            int nx = i + dx[k];
            int ny = j + dy[k];

            if(nx>=0 && nx < 3 && ny >=0 && ny < 8){
                if(grid[nx][ny]=='X')
                    X++;
                else if(grid[nx][ny]=='O')
                    O++;
            }
        }
        if(X>=2 || O>=2)
        {
            res[i][j] = (X > O) ? 'X' : ((X == O) ? '.':'O');
        }else{
            res[i][j] = '.';
        }
    }
    private static void nonEmptyCell(char[][] grid, char[][] res, int i, int j)
    {
        int X = 0, O=0;
        char self = grid[i][j];
        res[i][j] = grid[i][j];
        for(int k = 0; k < 8; k++)
        {
            int nx = i + dx[k];
            int ny = j + dy[k];
            if(nx>=0 && nx < 3 && ny >=0 && ny < 8){
                if(grid[nx][ny]=='X')
                    X++;
                else if(grid[nx][ny]=='O')
                    O++;
            }
        }
        int same = (self=='X') ? X : O;
        int other = (self=='X') ? O : X;

        if(same + other > 6 || same < 3 || other > same){
            res[i][j] = '.';
        }
    }

}
