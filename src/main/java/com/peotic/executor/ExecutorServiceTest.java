package com.peotic.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ExecutorServiceTest {
    int overNum = 1;
    
    public static void main(String[] args) {
        work2();
    }

    private static void work2() {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        List<FutureTask<String>> result2List = new ArrayList<FutureTask<String>>();
        final ExecutorServiceTest t = new ExecutorServiceTest();
        
        for (int i = 0; i < 10; i++) {
            final int id = i;
            FutureTask<String> futureTask = new FutureTask<String>(new TaskWithResult(id)){
                //测试结论：done方法执行的时间与futureTask.get()返回的时间先后不能确定
                protected void done() {
                    synchronized (ExecutorServiceTest.class) {
                        System.out.println(id + ":线程执行结果数量" + t.overNum);
                        if (t.overNum == 6) {//如果有6个执行成功就可以关掉所有运行线程
                            List<Runnable> list = executorService.shutdownNow();
                            System.out.println(id + ":shutdownNow, 返回等待执行的任务列表size=" + list.size());
                        }
                        t.overNum ++;
                    }
                }
            };
            executorService.submit(futureTask);
            result2List.add(futureTask);
        }
        
     // 遍历任务的结果
        for (int i = 0; i < result2List.size(); i++){
            final FutureTask<String> futureTask = result2List.get(i);
            final int id = i;
            executorService.execute(new Runnable() {
                public void run() {
                    long time1 = System.currentTimeMillis();
                    try {
                        System.out.println(id + ":调用返回成功:" + futureTask.get());
                    } catch (Exception e) {
                        System.out.println(id + ":调用返回失败:" + e.getLocalizedMessage());
                    }                    
                    System.out.println(id + ":调用等待用时：" + (System.currentTimeMillis() - time1));
                }
            });
        }

        if (!executorService.isShutdown()) {
            executorService.shutdown();
            System.out.println("shutdown");
        }
    }

}

class TaskWithResult implements Callable<String> {
    private int id;

    public TaskWithResult(int id) {
        this.id = id;
    }

    
    public String call() throws Exception {
        System.out.println("call()方法被自动调用,干活！！！ " + Thread.currentThread().getName());
        Thread.sleep(Math.round(Math.random() * 5555));
        if (id > 4) {
            System.out.println(id + "：干活失败");
            throw new TaskException("干活失败");
        }
        System.out.println(id + "：干活完成");
        return id + "干活完成:" + Thread.currentThread().getName();
    }
}

class TaskException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public TaskException(String message) {
        super(message);
    }
}