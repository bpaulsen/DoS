package DoS.DoS;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class SiteBucket implements Comparable<Object> {
	private static final Map<String,SiteBucket> site_map = new ConcurrentHashMap<String,SiteBucket>();
	private final String site_name;
	private Set<Job> running_jobs;
	private PriorityBlockingQueue<Job> pending_jobs;
	private Set<Job> known_jobs;
	
	public SiteBucket(String site_name) {
		if (site_name == null) {
			throw new IllegalArgumentException("site name is not allowed to be null");
		}
		this.site_name = site_name;
		
		SiteBucket sb = site_map.get(site_name);
		if (sb == null) {
			this.running_jobs = Collections.newSetFromMap(new ConcurrentHashMap<Job, Boolean>());
			this.known_jobs = Collections.newSetFromMap(new ConcurrentHashMap<Job, Boolean>());
			this.pending_jobs = new PriorityBlockingQueue<Job>();
			sb = site_map.putIfAbsent(site_name, this);
		}

		if (sb != null) {
			this.running_jobs = sb.running_jobs;
			this.pending_jobs = sb.pending_jobs;
			this.known_jobs = sb.known_jobs;
		}
	}

	public String get_site_name() {
		return site_name;
	}
	
	public boolean add(Job job) {
		if (job == null) {
			throw new IllegalArgumentException("Job can not be null");
		}
		if (!site_name.equals(job.get_site())) {
			throw new IllegalArgumentException("Site name (" + site_name + ") must match site name of job (" + job.get_site() + ")");
		}
		
		if ( !known_jobs.add(job) ) {
			return false;
		}
		
		return job.get_is_running() ? running_jobs.add(job) : pending_jobs.add(job);
	}
	
	public boolean remove(Job job) {
		if (!known_jobs.remove(job)) {
			return false;
		}
		return running_jobs.contains(job) ? running_jobs.remove(job) : pending_jobs.remove(job);
	}
	
	public int running_size() {
		return running_jobs.size();
	}

	public int pending_size() {
		return pending_jobs.size();
	}
	
	public Job first_pending_job() {
		return pending_jobs.peek();
	}
	
	public LocalDateTime earliest_pending_job() {
		Job job = pending_jobs.peek();
		if ( job == null) {
			return null;
		}
		
		return job.get_create_timestamp();
	}
	
	public boolean run_next_job() {
		Job job = pending_jobs.poll();
		if ( job == null ) {
			return false;
		}
		
		job.set_is_running(true);
		return running_jobs.add(job);
	}

	@Override
	public int compareTo(Object obj) {
		if (obj == null) {
			return -1;
		}

		SiteBucket other = (SiteBucket) obj;
		if (get_site_name().equals(other.get_site_name())) {
			return 0;
		}
		
    	int comparison = running_size() - other.running_size();    	
    	if ( comparison != 0 ) {
    		return comparison;
    	}
    	
    	Job first_job_1 = first_pending_job();
    	Job first_job_2 = other.first_pending_job();
    	
     	if (first_job_1 != null || first_job_2 != null) {
     		if (first_job_1 == null && first_job_2 != null) {
     			return 1;
     		}
     		else if (first_job_1 != null && first_job_2 == null) {
     			return -1;
     		}
   	
     		comparison = first_job_1.compareTo(first_job_2);
     		if ( comparison != 0 ) {
     			return comparison;
     		}
     	}
    	
    	return get_site_name().compareTo(other.get_site_name());
	}
}