package DoS.DoS;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class PriorityScheduler {
	private static double percentages[] = { 0, 5, 10, 10, 10, 10, 10, 12, 13, 15, 0 };
	private static final Map<Integer,Scheduler> scheduler_map = new ConcurrentHashMap<Integer, Scheduler>();
	
	private static int running_count = 0;
	private static final TreeSet<Scheduler> schedulers = new TreeSet<Scheduler>(new Comparator<Scheduler>() {
		@Override
		public int compare(Scheduler s1, Scheduler s2) {
			Job job_s1 = s1.first_pending_job();
			Job job_s2 = s2.first_pending_job();
			
			// handle the cases where one has pending and the other does not
			if (job_s1 == null) return 1;
			if (job_s2 == null) return -1;	
			
			int running_size_s1 = s1.running_size();
			int running_size_s2 = s2.running_size();
			boolean within_allotment_s1 = running_count == 0 || (100.0 * running_size_s1 / running_count < percentages[s1.get_priority()] ) ? true : false;
			boolean within_allotment_s2 = running_count == 0 || (100.0 * running_size_s2 / running_count < percentages[s1.get_priority()] ) ? true : false;
			
			// handle cases where one or both sets of schedulers are running within their allotment
			if (within_allotment_s1 && !within_allotment_s2) return -1;
			if (!within_allotment_s1 && within_allotment_s2) return 1;
			if (within_allotment_s1 && within_allotment_s2) return s2.get_priority() - s1.get_priority(); 
			
			// both are over the allotment, now try to divide evenly
			double over_allotment_pct_s1 = 100.0 * running_size_s1 / running_count - percentages[s1.get_priority()];
			double over_allotment_pct_s2 = 100.0 * running_size_s2 / running_count - percentages[s2.get_priority()];
			
			if ( over_allotment_pct_s1 < over_allotment_pct_s2) return -1;
			if ( over_allotment_pct_s1 > over_allotment_pct_s2) return 1;
			
			return s2.get_priority() - s1.get_priority(); 
		}
	});
	
	public boolean add(Job job) {
		int priority = job.get_priority();
		Scheduler scheduler = get_scheduler(priority);
		if ( !scheduler.add(job)) {
			return false;
		}
		
		if (job.get_is_running()) {
			running_count++;
		}
		return true;
	}
	
	public boolean remove(Job job) {
		int priority = job.get_priority();
		Scheduler scheduler = get_scheduler(priority);
		
		boolean is_running = scheduler.get_is_running(job) ? true : false;
		if ( !scheduler.remove(job) ) {
			return false;
		}
		
		if ( is_running ) {
			running_count--;
		}
		
		return true;
	}
	
	public boolean run_next_job() {
		Scheduler scheduler = schedulers.first();
		
		if ( scheduler == null ) {
			return false;
		}
		
		if ( !schedulers.remove(scheduler) ) {
			return false;
		}
		
		boolean return_value = scheduler.run_next_job();
		if (return_value) {
			running_count++;
		}
		
		return schedulers.add(scheduler) && return_value;
	}
	
	private static Scheduler get_scheduler(int priority) {
		Scheduler scheduler = scheduler_map.get(priority);
		if (scheduler == null) {
			scheduler = new Scheduler(priority);
			Scheduler new_scheduler = scheduler_map.putIfAbsent(priority, scheduler);
			if ( new_scheduler != null) {
				scheduler = new_scheduler;
			}
		}
		
		schedulers.remove(scheduler);
		return scheduler;
	}
}
