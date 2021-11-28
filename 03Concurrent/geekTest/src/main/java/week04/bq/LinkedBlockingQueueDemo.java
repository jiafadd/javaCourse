package week04.bq;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by jiafa
 * on 2021/11/28 22:13
 */
public class LinkedBlockingQueueDemo {
    
    public static void main(String[] args) throws InterruptedException {

    long start=System.currentTimeMillis();
    BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(1);

    int[] result = new int[1];
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            result[0] = sum();
            try {
                blockingQueue.put(result[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    Thread thread = new Thread(runnable);
    thread.start();
    // 确保  拿到result 并输出
    System.out.println("异步计算结果为："+blockingQueue.take());

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
