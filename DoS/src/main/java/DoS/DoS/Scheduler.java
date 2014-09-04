package DoS.DoS;

import java.util.TreeSet;

public class Scheduler {
	private static TreeSet<SiteBucket> site_buckets = new TreeSet<SiteBucket>();
	private SiteBucket get_site_bucket(String site_name) {
		SiteBucket site_bucket = new SiteBucket(site_name);
		site_buckets.remove(site_bucket);
		return site_bucket;
	}
	
	// need to make this thread safe
	public boolean add(Job job) {
		SiteBucket site_bucket = get_site_bucket(job.get_site());
		site_bucket.add(job);	
		return site_buckets.add(site_bucket);
	}
	
	public boolean remove(Job job) {
		SiteBucket site_bucket = get_site_bucket(job.get_site());
		
		if ( !site_bucket.remove(job) ) {
			return false;
		}
		if ( site_bucket.pending_size() != 0 || site_bucket.running_size() != 0 ) {
			return site_buckets.add(site_bucket);
		}
		return true;
	}
	
	public boolean run_next_job() {
		SiteBucket site_bucket = site_buckets.first();
		
		if ( site_bucket == null ) {
			return false;
		}
		
		if ( !site_buckets.remove(site_bucket) ) {
			return false;
		}
		
		if ( !site_bucket.run_next_job() ) {
			return false;
		}
		
		return site_buckets.add(site_bucket);
	}
	
	public int size() {
		return site_buckets.size();
	}
}
