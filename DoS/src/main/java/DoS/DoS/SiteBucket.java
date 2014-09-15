package DoS.DoS;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class SiteBucket implements Comparable<Object> {
	private class Key {
		private final Integer priority;
		private final String  site_name;	

		public Key( Integer priority, String site_name ) {
			this.priority = priority;
			this.site_name = site_name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((priority == null) ? 0 : priority.hashCode());
			result = prime * result
					+ ((site_name == null) ? 0 : site_name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (priority == null) {
				if (other.priority != null)
					return false;
			} else if (!priority.equals(other.priority))
				return false;
			if (site_name == null) {
				if (other.site_name != null)
					return false;
			} else if (!site_name.equals(other.site_name))
				return false;
			return true;
		}	  
	}
		
	private static final Map<Key,SiteBucket> site_map = new ConcurrentHashMap<Key,SiteBucket>();
	
	private final String site_name;
	private Set<Job> known_jobs;
	private Set<Job> running_jobs;
	private Queue<Job> pending_jobs;

	public SiteBucket(String site_name) {
		if (site_name == null) {
			throw new IllegalArgumentException("site name is not allowed to be null");
		}
		this.site_name = site_name;
		this.initialize_private_collections(null, site_name);
	}
	
	public SiteBucket(int priority, String site_name) {
		if (site_name == null) {
			throw new IllegalArgumentException("site name is not allowed to be null");
		}
		this.site_name = site_name;
		this.initialize_private_collections(priority, site_name);
	}
	
	private void initialize_private_collections(Integer priority, String site_name) {
		Key key = new Key(priority, site_name);
		SiteBucket sb = site_map.get(key);
		if (sb == null) {
			this.running_jobs = Collections.newSetFromMap(new ConcurrentHashMap<Job, Boolean>());
			this.known_jobs = Collections.newSetFromMap(new ConcurrentHashMap<Job, Boolean>());
			this.pending_jobs = new PriorityBlockingQueue<Job>();
			sb = site_map.putIfAbsent(key, this);
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
	
	public int size() {
		return known_jobs.size();
	}
	
	public int running_size() {
		return running_jobs.size();
	}

	public int pending_size() {
		return pending_jobs.size();
	}
	
	public Job peek() {
		return pending_jobs.peek();
	}
	
	public Job poll() {
		Job job = pending_jobs.poll();
		if ( job != null ) {
			if (running_jobs.add(job)) {
				job.set_is_running(true);
			}
			else {
				// need to handle the case where we are unable to add to the map
			}
		}
		return job;
	}
	
	public boolean get_is_running(Job job) {
		return running_jobs.contains(job);
	}
	
	public void clear() {
		known_jobs.clear();
		running_jobs.clear();
		pending_jobs.clear();
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
    	
    	Job first_job_1 = peek();
    	Job first_job_2 = other.peek();
    	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result
				+ ((known_jobs == null) ? 0 : known_jobs.hashCode());
		result = prime * result
				+ ((pending_jobs == null) ? 0 : pending_jobs.hashCode());
		result = prime * result
				+ ((running_jobs == null) ? 0 : running_jobs.hashCode());
		result = prime * result
				+ ((site_name == null) ? 0 : site_name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SiteBucket other = (SiteBucket) obj;
		if (known_jobs == null) {
			if (other.known_jobs != null)
				return false;
		} else if (!known_jobs.equals(other.known_jobs))
			return false;
		if (pending_jobs == null) {
			if (other.pending_jobs != null)
				return false;
		} else if (!pending_jobs.equals(other.pending_jobs))
			return false;
		if (running_jobs == null) {
			if (other.running_jobs != null)
				return false;
		} else if (!running_jobs.equals(other.running_jobs))
			return false;
		if (site_name == null) {
			if (other.site_name != null)
				return false;
		} else if (!site_name.equals(other.site_name))
			return false;
		return true;
	}
}