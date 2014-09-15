package DoS.DoS;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class Scheduler {
	private static final Map<Integer,TreeSet<SiteBucket>> tree_map = new ConcurrentHashMap<Integer,TreeSet<SiteBucket>>();
	private static final TreeSet<SiteBucket> null_priority_treeset = new TreeSet<SiteBucket>();
	
	private Integer priority = null;
	private TreeSet<SiteBucket> site_buckets;
	
	public Scheduler() {
		site_buckets = null_priority_treeset;
	}
	
	public Scheduler(int priority) {
		if (priority < 0 || priority > 10) {
			throw new IllegalArgumentException("priority must be between 0 and 10 inclusive");
		}
		
		this.priority = priority;
		site_buckets = tree_map.get(priority);
		if (site_buckets == null) {
			site_buckets = new TreeSet<SiteBucket>();
			TreeSet<SiteBucket> new_treeset = tree_map.putIfAbsent(priority, site_buckets);
			if ( new_treeset != null ) {
				site_buckets = new_treeset;
			}
		}
	}
	
	private SiteBucket get_site_bucket(String site_name) {
		SiteBucket site_bucket = priority == null ? new SiteBucket(site_name) : new SiteBucket(priority, site_name);
		site_buckets.remove(site_bucket);
		return site_bucket;
	}
	
	// need to make this thread safe
	public boolean add(Job job) {
		SiteBucket site_bucket = get_site_bucket(job.get_site());
		
		boolean return_value = site_bucket.add(job);
		return site_buckets.add(site_bucket) && return_value;
	}
	
	public boolean remove(Job job) {
		SiteBucket site_bucket = get_site_bucket(job.get_site());
		
		boolean return_value = site_bucket.remove(job);
		if ( site_bucket.pending_size() != 0 || site_bucket.running_size() != 0 ) {
			return site_buckets.add(site_bucket) && return_value;
		}
		return return_value;
	}
	
	public Job poll() {
		SiteBucket site_bucket = site_buckets.pollFirst();
		
		if ( site_bucket == null ) {
			return null;
		}
		
		Job job = site_bucket.poll();		
		site_buckets.add(site_bucket); // need to handle case where we are unable to add the site_bucket back to the tree
		return job;
	}
	
	public int site_size() {
		return site_buckets.size();
	}
	
	public int size() {
	    int size = 0;
	    Iterator<SiteBucket> iterator = site_buckets.iterator();

	    while (iterator.hasNext()) {
	    	size += iterator.next().size();
	    }	

		return size;
	}
	
	public int pending_size() {
	    int size = 0;
	    Iterator<SiteBucket> iterator = site_buckets.iterator();

	    while (iterator.hasNext()) {
	    	size += iterator.next().pending_size();
	    }	

		return size;
	}
	
	public int running_size() {
	    int size = 0;
	    Iterator<SiteBucket> iterator = site_buckets.iterator();

	    while (iterator.hasNext()) {
	    	size += iterator.next().running_size();
	    }	

		return size;	
	}
	
	public void clear() {
	    Iterator<SiteBucket> iterator = site_buckets.iterator();

	    while (iterator.hasNext()) {
	    	iterator.next().clear();
	    }	

		site_buckets.clear();
		tree_map.clear();
	}
	
	public Integer get_priority() {
		return priority;
	}
	
	public Job peek() {
		if (site_buckets.size() == 0) return null;
		
		return site_buckets.first().peek();
	}
	
	public boolean get_is_running(Job job) {
		SiteBucket site_bucket = priority == null ? new SiteBucket(job.get_site()) : new SiteBucket(priority, job.get_site());
		return site_bucket.get_is_running(job);
	}
}
