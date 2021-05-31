package threadcoreknowledge.createthreads;

/**
 * @author zzxstart
 * @date 2021/2/24 - 0:15
 */
public class RunnableStyle implements Runnable{
    public static void main(String[] args) {
    Thread thread =new Thread(new RunnableStyle());
    thread.start();
    
    }
    @Override
    public void run() {
        System.out.println("用Runnable方法实现线程");
    }
}
