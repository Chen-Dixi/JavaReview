package dixi.bupt;

public class HeapSort {

    public static void main(String[] args){
        int [] a =  {98,83,71,61,51,41,31,21,11,1};
        int res = findKth(a,10,4);

        System.out.println("结果");
        System.out.println(res);
    }

    static public int findKth(int[] a, int n, int K) {
        // write code here

        int[] minHeap = new int[K];

        if(n<K)
            return -1;

        for(int i=0;i<K;i++){
            minHeap[i] = a[i];
        }
        int k = K-1;
        for(int i=(k-1)/2;i>=0;i--){
            sank(minHeap,i,k);
        }
        for(int i=0;i<K;i++)
            System.out.println(minHeap[i]);
        //MINHEAP为最小堆
        for(int i=K;i<n;i++){
            if(a[i]>minHeap[0]){
                minHeap[0]=a[i];
                sank(minHeap,0,k);
            }
        }

        return minHeap[0];
    }

    static  private void sank(int[] a, int k, int n){

        while(2*k+1 <= n ){
            int j = 2*k+1;

            if(j<n && a[j+1]<a[j]){
                j++;
            }

            if(a[j]>a[k])
                break;
            swap(a, k,j);
            k = j;
        }
    }
    static private void swap(int[] a, int i, int j){
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}
