package dixi.bupt.huawei;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("checkstyle:VisibilityModifier")
// 简单的位置类
class Pos {
    int x;
    int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

/**
 * @author chendixi
 * Created on 2024-09-17
 */
public class Maze {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int n = in.nextInt();
            int m = in.nextInt();
            // 构造迷宫
            int[][] map = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    map[i][j] = in.nextInt();
                }
            }

            // 路径存储的数组
            List<Pos> path = new ArrayList<>();
            // DFS 搜索路径
            dfs(map, 0, 0, path);
            // 输出
            for (Pos p : path) {
                System.out.println("(" + p.x + "," + p.y + ")");
            }
        }
    }

    /**
     * 返回的boolean 代表这条路能不能走通。
     */
    public static boolean dfs(int[][] map, int x, int y, List<Pos> path) {
        path.add(new Pos(x, y));
        map[x][y] = 1;
        // 结束标记
        if (x == map.length - 1 && y == map[0].length - 1) {
            return true;
        }
        if (x + 1 < map.length && map[x + 1][y] == 0) {
            if (dfs(map, x + 1, y, path)) {
                return true;
            }
        }

        if (x - 1 >= 0 && map[x - 1][y] == 0) {
            if (dfs(map, x - 1, y, path)) {
                return true;
            }
        }

        if (y + 1 < map[0].length && map[x][y + 1] == 0) {
            if (dfs(map, x, y + 1, path)) {
                return true;
            }
        }
        // 向左能走时
        if (y - 1 > -1 && map[x][y - 1] == 0) {
            if (dfs(map, x, y - 1, path)) {
                return true;
            }
        }
        // 回溯
        path.remove(path.size() - 1);
        map[x][y] = 0;
        return false;
    }
}
