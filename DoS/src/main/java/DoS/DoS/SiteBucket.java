package DoS.DoS;

import java.util.Comparator;
import java.util.TreeSet;

public class SiteBucket {
	private final String site_name;
	private final TreeSet<Job> jobs = new TreeSet<Job>(new Comparator<Job>()
			{
				public int compare(Job o1, Job o2) {
					if ( o1.get_is_running() != o2.get_is_running() ) {
						return o1.get_is_running() ? -1 : 1;
					}
					
					int timestamp_compare = o1.get_create_timestamp().compareTo(o2.get_create_timestamp());
					if ( timestamp_compare != 0) {
						return timestamp_compare;
					}

					return o1.get_id().compareTo(o2.get_id());
				} 
			});
	
	public SiteBucket(String site_name) {
		this.site_name = site_name;  // really need to make sure that I do not create two SiteBuckets with same site_name
	}
	
	public boolean add(Job job) {
		return jobs.add(job);
	}
	
	public boolean remove(Job job) {
		return jobs.remove(job);
	}
	
	public Job first() {
		return jobs.first();
	}
	
	public int size() {
		return jobs.size();
	}
	
}
