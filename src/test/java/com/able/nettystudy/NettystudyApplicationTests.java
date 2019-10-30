package com.able.nettystudy;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class NettystudyApplicationTests {
    public static final ThreadLocal<Integer> a=new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return 3;
        }
    };

    @Test
    public void contextLoads() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {

                try {
                    countDownLatch.await();
                    log.info("random={},threadName={}", random.nextLong(),Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



            });
        }
        countDownLatch.countDown();
        TimeUnit.SECONDS.sleep(1);

    }
    
    @Test
    public void test1() throws Exception{
        List<String> zzpttjd = decrypt("4b4a49444251564d4e404c4151564a494945415c5f4f4a434d47595f4a4a4a444250564a4144424751504e4e40474d5154", "zzpttjd");
        System.out.println(zzpttjd);
    }
    private static List<String> decrypt(String ev, String pXorKeys) throws IllegalAccessException {
        StringBuilder output = new StringBuilder();
        String defaultKey = "zhihuishu";
        if (!StringUtils.isEmpty(pXorKeys)) {
            defaultKey = pXorKeys;
        }
        for (int i = 0; i < ev.length(); i += 2) {
            String str = ev.substring(i, i + 2);
            char c = (char) (Integer.parseInt(str, 16) ^ defaultKey.charAt(i / 2 % defaultKey.length()));
            if (c >= '0' && c <= ';') {
                output.append(c);
            } else {
                throw new IllegalAccessException("Illegal character: " + c);
            }
        }
        try {
            String decrypt = output.toString();
            String[] split = decrypt.split(";");
            List<String> list = new ArrayList<String>(Arrays.asList(split));
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalAccessException("Illegal exception: " + e.getMessage());
        }




    }



    @Test
    public void test2(){

    }

}
