package DoS.DoS;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SiteBucket {
	private static final Map<String,SiteBucket> site_map = new ConcurrentHashMap<String,SiteBucket>();
	private final String site_name;
	private Set<Job> running_jobs;
	
	/* I think we may need to turn pending_jobs into a TreeSet to handle the times where we need to initialize
	 * the SiteBucket from scratch.  Alternatively, may need to create an addAll method
	 */
	private Queue<Job> pending_jobs;
	
	public SiteBucket(String site_name) {
		this.site_name = site_name;
		
		SiteBucket sb = site_map.get(site_name);
		if (sb == null) {
			this.running_jobs = new HashSet<Job>();
			this.pending_jobs = new ConcurrentLinkedQueue<Job>();
			sb = site_map.putIfAbsent(site_name, this);
		}

		if (sb != null) {
			this.running_jobs = sb.running_jobs;
			this.pending_jobs = sb.pending_jobs;
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
		
		return job.get_is_running() ? running_jobs.add(job) : pending_jobs.add(job);
	}
	
	public boolean remove(Job job) {
		return job.get_is_running() ? running_jobs.remove(job) : pending_jobs.remove(job);
	}
	
	public int running_size() {
		return running_jobs.size();
	}

	public int pending_size() {
		return pending_jobs.size();
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
		add(job);
		return true;
	}
}