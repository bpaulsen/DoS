package DoS.DoS;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.TreeSet;

public class Scheduler {
	private static TreeSet<SiteBucket> site_buckets = new TreeSet<SiteBucket>( new Comparator<SiteBucket>() {
        @Override
        public int compare(SiteBucket o1, SiteBucket o2) {
        	int comparison = o1.running_size() - o2.running_size();
        	
        	if ( comparison != 0 ) {
        		return comparison;
        	}
        	
         	LocalDateTime earliest_job_1 = o1.earliest_pending_job();
         	LocalDateTime earliest_job_2 = o2.earliest_pending_job();
         	if (earliest_job_1 == null && earliest_job_2 != null) {
         		return 1;
         	}
       	
        	comparison = earliest_job_1.compareTo(earliest_job_2);
        	if ( comparison != 0 ) {
        		return comparison;
        	}
        	
        	return o1.get_site_name().compareTo(o2.get_site_name());
        }
    });

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
		site_bucket.remove(job);
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
}
