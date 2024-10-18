package dixi.bupt.thread;


public class ReviewThreadPoool {
    private static final int DEFAULT_IO_WORKER_COUNT = Math.max(Runtime.getRuntime().availableProcessors(), 8);

    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        for (int i = 0; i < 5; i++) {
//            executorService.execute(new MyRunnable());
//        }
//        executorService.shutdown();
//        System.out.println("Main Method!");
        System.out.println(DEFAULT_IO_WORKER_COUNT);
        String cpuNumStr = "4.0";
        int cpuCores = 0;
        try {
            cpuCores = Integer.parseInt(cpuNumStr);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException");
        }
        System.out.println(cpuCores);
    }
}
