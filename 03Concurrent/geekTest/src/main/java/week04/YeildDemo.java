package week04;

/**
 * Created by jiafa
 * on 2021/11/28 17:13
 */
public class YeildDemo {
    public static void main(String[] args) throws InterruptedException {

        long start=System.currentTimeMillis();
        
        int[] result = new int[1];
        
        new Thread(()->{ 
            result[0] = sum(); //这是得到的返回值
        }).start();
        
        while(Thread.activeCount() > 2){
            Thread.yield();
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
