package dixi.bupt.javaContainer;

public class Main {
    static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n+1;
    }

    public static void main(String[] args){
        int ans = tableSizeFor(18);
        System.out.println(ans);
    }
}
