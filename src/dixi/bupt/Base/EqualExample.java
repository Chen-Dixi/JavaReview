package dixi.bupt.Base;

public class EqualExample {
    private int x;
    private int y;
    private int z;

    public EqualExample(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        EqualExample that = (EqualExample) o;
        if( that.x!=x || that.y!=this.y || that.z!=this.z ) return false;
        return true;
    }

    // 散列值相同的两个对象不一定等价，因为计算哈希具有随机性，两个值不同的对象可能计算出相同的哈希值。哈希值不同，对象一定不等价
    @Override
    public int hashCode() {
        int result = 17;
        // 不想等的对象应该均匀分布在所有可能哈希值上，要求哈希值把所有域的值都考虑进来。可以将每个域都当作R进制的某一位，然后组成R进制的整数
        // R取31
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}