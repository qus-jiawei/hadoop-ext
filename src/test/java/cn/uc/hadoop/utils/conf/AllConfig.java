package cn.uc.hadoop.utils.conf;

import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.MRConfig;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.v2.jobhistory.JHAdminConfig;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

public class AllConfig implements MRConfig,MRJobConfig{
	
	static public JobConf jobConf = new JobConf(); 
	static public DFSConfigKeys dFSConfigKeys = new DFSConfigKeys(); 
	static public YarnConfiguration yarnConfiguration = new YarnConfiguration();
	static public JHAdminConfig jHAdminConfig = new JHAdminConfig();
	
}
