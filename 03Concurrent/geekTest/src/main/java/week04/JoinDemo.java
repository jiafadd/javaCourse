package week04;

/**
 * Created by jiafa
 * on 2021/11/28 18:06
 */
public class JoinDemo {
    public static void main(String[] args) {
        
    long start=System.currentTimeMillis();

    int[] result = new int[1];
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            result[0] = sum();
        }
    };
    Thread thread = new Thread(runnable);
    thread.start();
    try {
        thread.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
        
    
    // 确保  拿到result 并输出
    System.out.println("异步计算结果为："+ result[0]);

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
