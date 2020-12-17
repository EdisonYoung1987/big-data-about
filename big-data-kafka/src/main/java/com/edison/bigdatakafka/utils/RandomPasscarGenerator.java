package com.edison.bigdatakafka.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

/**随机生成pass相关数据*/
public class RandomPasscarGenerator {
    private static final String[] CITIES={"渝","京","沪","湘","贵","川"};
    private static final String[] LETTERS={"A","B","C","D","E","H"};

    //生成随机车牌号
    public static String genPlateNum(){
        Random random=new Random(System.currentTimeMillis());
        return CITIES[random.nextInt(CITIES.length)]+LETTERS[random.nextInt(LETTERS.length)]+genRandomNums(5);
    }

    //生成一串随机数字
    public static String genRandomNums(int length){
        StringBuilder sb=new StringBuilder();
        Random random=new Random(System.currentTimeMillis());
        for(int i=0;i<length;i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
