package DoS.DoS;

import java.util.TreeSet;

public class PriorityScheduler {
	private static final TreeSet<SiteBucket> null_priority_treeset = new TreeSet<SiteBucket>();

	public boolean add(Job job) {
		int priority = job.get_priority();
		return true;
	}
}
