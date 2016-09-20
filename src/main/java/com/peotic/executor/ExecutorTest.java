package com.peotic.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorTest {
    
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Future<String> submit = executor.submit(new Callable<String>() {
            public String call() throws Exception {
                System.out.println("ssss");
                if (1/0 == 1)
                    return "false";
                return "true";
            }
        });
        try {
            Thread.sleep(5555);
            executor.shutdownNow();  
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            System.out.println("除0");
            String obj = submit.get();
            System.out.println(obj);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("error");
            e.printStackTrace();
        }
        
        work1();
   }

    private static void work1() {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<String>> resultList = new ArrayList<Future<String>>();

        // 创建10个任务并执行
        for (int i = 0; i < 10; i++) {
            // 使用ExecutorService执行Callable类型的任务，并将结果保存在future变量中
            Future<String> future = executorService.submit(new TaskWithResult(i));
            // 将任务执行结果存储到List中
            resultList.add(future);
        }

        executorService.shutdown();
        System.out.println("shutdown");

        // 遍历任务的结果
        for (Future<String> fs : resultList) {
            System.out.println("===========================");
            long time1 = System.currentTimeMillis();
            try {
                System.out.println("调用返回成功:" + fs.get());

            } catch (Exception e) {
                if (!executorService.isTerminated()) {
                    List<Runnable> list = executorService.shutdownNow();
                    System.out.println("返回等待执行的任务列表size=" + list.size());
                }
                System.out.println("调用返回失败:" + e.getLocalizedMessage());
            }
            System.out.println("调用等待用时：" + (System.currentTimeMillis() - time1));
        }
    }
}
