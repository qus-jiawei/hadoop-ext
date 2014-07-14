package cn.uc.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.collections.map.LinkedMap;
import org.junit.Test;

public class MapSpeedTest {
	private Random random = new Random();
	private void printTime(Map map){
		for(int i=0;i<=100000;i++){
			
		}
	}
	private void printPut(String name,Class map){
		
//		System.out.println(name+"\t\t\t"+
//				printPut(map,10)+"\t"+
//				printPut(map,50)+"\t"+
//				printPut(map,100)+"\t"+
//				printPut(map,500)+"\t"+
//				printPut(map,1000));
	}
	private long printPut(Map<Integer,Integer> map, int size){
		//
		long begin = System.nanoTime();
		for(int i=0;i<size;i++){
			map.put(random.nextInt(), random.nextInt());
		}
		return (System.nanoTime() - begin);
	}
	@Test
	public void testMapSpeed(){
		//测试Map的几种实现，在不同的场景下的速度
		
		Map map=null;
		//put
//		testPut("HashMap",HashMap.class);
//		printPut("LinkedM",LinkedMap.class);
//		printPut("TreeMap",TreeMap.class);
		//创建遍历
		//get
		//
	}
}
