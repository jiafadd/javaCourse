package week04.pool;

import java.util.concurrent.*;

/**
 * Created by jiafa
 * on 2021/11/28 22:51
 */
public class NewScheduledThreadPool {
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long start=System.currentTimeMillis();
        ExecutorService executorService = Executors.newScheduledThreadPool(1);
        int[] result = new int[1];
        result[0] = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return sum();
            }
        }).get();
        executorService.shutdown();
        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result[0]);

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

        // 然后退出main线程
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}


