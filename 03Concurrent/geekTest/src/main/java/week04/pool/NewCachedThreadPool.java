package week04.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jiafa
 * on 2021/11/28 23:00
 */
public class NewCachedThreadPool {
    public static void main(String[] args) {

        long start=System.currentTimeMillis();
        ExecutorService executorService = Executors.newCachedThreadPool();
        int[] result = new int[1];
        try {
            Future<Integer> future = executorService.submit(() -> {
                return sum();
            });
            result[0] = future.get();
        } catch (Exception ex) {
            System.out.println("catch submit");
            ex.printStackTrace();
        }
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


