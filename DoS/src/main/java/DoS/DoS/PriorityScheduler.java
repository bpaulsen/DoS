package DoS.DoS;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class PriorityScheduler {
	private static double percentages[] = { 0, 5, 10, 10, 10, 10, 10, 12, 13, 15, 0 };
	private static final Map<Integer,Scheduler> scheduler_map = new ConcurrentHashMap<Integer, Scheduler>();
	
	private static int running_count = 0;
	
	private static Comparator<Scheduler> comparator = new Comparator<Scheduler>() {
		@Override
		public int compare(Scheduler s1, Scheduler s2) {
			Job job_s1 = s1.peek();
			Job job_s2 = s2.peek();
			
			// handle the cases where one has pending and the other does not
			if (job_s1 == null) return 1;
			if (job_s2 == null) return -1;	
			
			int running_size_s1 = s1.running_size();
			int running_size_s2 = s2.running_size();
			boolean within_allotment_s1 = running_count == 0 || (100.0 * running_size_s1 / running_count < percentages[s1.get_priority()] ) ? true : false;
			boolean within_allotment_s2 = running_count == 0 || (100.0 * running_size_s2 / running_count < percentages[s2.get_priority()] ) ? true : false;
			
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
	};

	private static final TreeSet<Scheduler> schedulers = new TreeSet<Scheduler>(comparator);
	
	public boolean add(Job job) {
		int priority = job.get_priority();
		Scheduler scheduler = get_scheduler(priority);
		
		boolean return_value = scheduler.add(job);
		
		if (job.get_is_running()) {
			running_count++;
		}
		
		return schedulers.add(scheduler) && return_value;
	}
	
	public boolean remove(Job job) {
		int priority = job.get_priority();
		Scheduler scheduler = get_scheduler(priority);
		
		boolean is_running = scheduler.get_is_running(job) ? true : false;
		boolean return_value = scheduler.remove(job);
		
		if ( is_running ) {
			running_count--;
		}
		
		return schedulers.add(scheduler) && return_value;
	}
	
	public Job poll() {
		TreeSet<Scheduler> schedulers = new TreeSet<Scheduler>(comparator);
		
		schedulers.addAll(scheduler_map.values());				
		Scheduler scheduler = schedulers.first();
		
		if ( scheduler == null ) {
			return null;
		}
		
		Job job = scheduler.poll();
		if (job != null) {
			running_count++;
		}
		
		return job;
	}
	
	public void clear() {
		// cheap and easy because we have a small finite set of priorities
		for (Integer priority : scheduler_map.keySet()) {
			Scheduler scheduler = scheduler_map.get(priority);
			if (scheduler != null) {
				scheduler.clear();
			}
		}

		schedulers.clear();
		running_count = 0;
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
