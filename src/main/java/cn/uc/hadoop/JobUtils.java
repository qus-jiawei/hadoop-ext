package cn.uc.hadoop;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.ResourceMgrDelegate;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

/***
 * see {@link cn.uc.hadoop.JobUtils}
 */
public class JobUtils {

	static final String BYTES_PER_REDUCER = "uc.exec.reducers.bytes.per.reducer";
	static final String MAX_REDUCERS = "uc.exec.reducers.max";
	static final long DEF_BYTES_PER_REDUCER = 1000L * 1000 * 1000;
	static final int DEF_MAX_REDUCERS = 999;

	/**
	 * 估算Job需要的Reducers数量，基于Job输入数据量、配置参数和集群中Reduce槽数。
	 * 
	 * @return Reducers数量
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	static int estimateNumberOfReducers(Job job) throws IOException{
		// 0.95或者1.75 ×（节点数 ×mapred.tasktracker.tasks.maximum参数值）
		try{
			Configuration conf = job.getConfiguration();
	
			long bytesPerReducer = conf.getLong(BYTES_PER_REDUCER,
					DEF_BYTES_PER_REDUCER);
			int maxReducers = conf.getInt(MAX_REDUCERS, DEF_MAX_REDUCERS);
	
			long totalInputFileSize = getInputLength(job);
	
			// 按数据量计算得到的reducer数量
			int reducers = (int) ((totalInputFileSize + bytesPerReducer - 1) / bytesPerReducer);
			reducers = Math.max(1, reducers);
	
			JobClient client = new JobClient(new JobConf(conf));
			int maxReduceTasks = client.getClusterStatus().getMaxReduceTasks();
	
			// 如果按输入数据计算得到的reducer数远大于reduce的槽数，使用1.75，否则使用0.95
			// 按系统槽数计算得到的reducer数量
			int reducersOnStatus = maxReduceTasks * 95 / 100;
			if (reducers >= maxReduceTasks * 3) {// *3 -> 远大于
				reducersOnStatus = (maxReduceTasks * 175 + 100 - 1) / 100;// 向上取整
			}
			reducersOnStatus = Math.max(1, reducersOnStatus);
	
			reducers = Math.min(reducersOnStatus, reducers);
			reducers = Math.min(maxReducers, reducers);
			return reducers;
		}
		catch(Exception e){
			return 1;
		}
	}

	/**
	 * 计算Job输入文件的大小
	 * 
	 * @param job
	 *            hadoop job
	 * @return 所有输入路径的汇总
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws ClassNotFoundException 
	 */
	private static long getInputLength(Job job) throws IOException, InterruptedException, ClassNotFoundException {

		InputFormat<?, ?> input =
			      ReflectionUtils.newInstance(job.getInputFormatClass(), job.getConfiguration());
		List<InputSplit> array = input.getSplits(job);
		long sum = 0 ;
		for(InputSplit is:array){
			sum += is.getLength();
		}
		return sum;
	}

	/**
	 * 自动计算Job的Reducer数量并设置给定Job。注意，要在设置Input Path后才能调用此函数。
	 * 
	 * @param job
	 *            Hadoop Job
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 * @throws IllegalStateException 
	 */
	public static void setNumberOfReducers(Job job) throws IOException {
		job.setNumReduceTasks(estimateNumberOfReducers(job));
	}

	/**
	 * 根据给出的applicationid或者jobid查询对应的job的状态
	 * 自动进行RM和AM之间切换的容错
	 * 返回的FinalApplicationStatus有4种状态:
	 *  UNDEFINED: app未完成
	 *  SUCCEEDED: app成功
	 *  FAILED: app失败
	 *  KILLED: app被kill
	 * @param name
	 * @throws IOException
	 */
	public static FinalApplicationStatus getJobStatusFromRM(String name,Configuration conf) throws IOException {
		if( name.startsWith("application")){
			name = name.replace("application", "job");
		}
		JobID oldJobID = JobID.forName(name);
		ApplicationId appId = TypeConverter.toYarn(oldJobID).getAppId();
		ResourceMgrDelegate rmd = new ResourceMgrDelegate(new YarnConfiguration(conf));
		ApplicationReport report = rmd.getApplicationReport(appId);
		return report.getFinalApplicationStatus();
	}
	
	/**
	 * get the splits for a job . it could be call only when the inputformat and the input file is already setted.
	 * @param job the job to sumbit, the 
	 * @return the number of input splits
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static int getSplitsFromJob(Job job) throws IOException, InterruptedException, ClassNotFoundException{
		InputFormat<?, ?> input =
			      ReflectionUtils.newInstance(job.getInputFormatClass(), job.getConfiguration());
		List<InputSplit> array = input.getSplits(job);
		return array.size();
	}
}
