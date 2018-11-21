package com.example.demo.component.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 并发请求测试
 */
public class Test {

    public static void main(String[] args) throws Exception{
        latchTest();
    }



    /**
     * 并发测试
     * @throws InterruptedException
     */
    private static void latchTest() throws InterruptedException {
        int poolSize = 10;
        final CountDownLatch start = new CountDownLatch(1);
        ExecutorService exce = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        start.await();
                        getResult();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            };
            exce.submit(run);
        }
        start.countDown();
        exce.shutdown();
    }

    public static String getResult(){
        String result = HttpUtil.doGet("http://localhost/getStudent3?id=2");
        return result;
    }

    /**
     * 没并发的测试
     * @throws InterruptedException
     */
    private static void latchTest2() throws InterruptedException {
        int poolSize = 100;
        ExecutorService exce = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        getResult();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            };
            exce.submit(run);
        }
        exce.shutdown();
    }


}
