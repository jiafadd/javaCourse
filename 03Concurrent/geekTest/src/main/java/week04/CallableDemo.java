package week04;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by jiafa
 * on 2021/11/28 21:19
 */
public class CallableDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long start=System.currentTimeMillis();

        int[] result = new int[1];
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                result[0] = sum();
                return result[0];
            }
        };
        FutureTask futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();
        
        // 确保  拿到result 并输出
        System.out.println("异步计算结果为："+futureTask.get());

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
