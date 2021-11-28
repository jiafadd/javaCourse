package week04.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jiafa
 * on 2021/11/28 20:26
 */
public class ReentrantLockDemo {
    
    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        int[] result = new int[1];
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    result[0] = sum();
                    condition.signal();
                } finally {
                    lock.unlock();
                }
                
            }
        };
        Thread thread = new Thread(runnable);
        lock.lock();
        try {
            thread.start();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        // 确保  拿到result 并输出
        System.out.println("异步计算结果为："+result[0]);

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

