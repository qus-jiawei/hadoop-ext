package cn.uc.hadoop;

import static org.junit.Assert.*;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import cn.uc.hadoop.utils.conf.AllConfig;

public class TestJobUtils {

	public void test() {

		try {
			Configuration conf = new Configuration();
			JobUtils.getJobStatusFromRM("job_1384840443657_0005",conf);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	static public void main(String[] args){
		try {
			Configuration conf = new Configuration();
			System.out.println( JobUtils.getJobStatusFromRM(args[0],conf) );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
