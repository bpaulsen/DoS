package DoS.DoS;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class SiteBucketTest {
    @Rule
    public TestName testName = new TestName();
	
	@Test
	public void testDuplicateSites() {
		String site_name = testName.getMethodName();
		SiteBucket sb1 = new SiteBucket(site_name);
		SiteBucket sb2 = new SiteBucket(site_name);
		
		Job job1 = new Job( site_name, "foo1", 1);
		Job job2 = new Job( site_name, "foo2", 1);

		assertTrue("Verify that a job can be added to an empty queue", sb1.add(job1));		
		assertEquals("Verify that pending counts are equal on job queues", sb1.pending_size(), sb2.pending_size());
		
		assertTrue("Verify that a job can be added to an non-empty queue", sb1.add(job2));		
		assertEquals("Verify that pending counts are equal on job queues", sb1.pending_size(), sb2.pending_size());
		
		assertNotNull("Verify that a non-empty pending queue can call run_next_job()", sb1.run_next_job());
		assertEquals("Verify that pending counts are equal on job queues after running job", sb1.pending_size(), sb2.pending_size());
		assertEquals("Verify that running counts are equal on job queues after running job", sb1.running_size(), sb2.running_size()); 
		
		assertTrue("Try to remove running job", sb1.remove(job1));
		assertEquals("Verify that pending counts are equal on job queues after removing running job", sb1.pending_size(), sb2.pending_size());
		assertEquals("Verify that running counts are equal on job queues after removing running job", sb1.running_size(), sb2.running_size()); 
		
		assertTrue("Try to remove pending job", sb1.remove(job2));
		assertEquals("Verify that pending counts are equal on job queues after removing pending job", sb1.pending_size(), sb2.pending_size());
		assertEquals("Verify that running counts are equal on job queues after removing pending job", sb1.running_size(), sb2.running_size()); 
	}
	
	@Test
	public void testCount() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);
		
		Job job1 = new Job(site_name, "foo1", 1);
		Job job2 = new Job(site_name, "foo2", 1);
		Job job3 = new Job(site_name, "foo3", 1, true);
		
		assertTrue("Verify that a job can be added to an empty queue", sb.add(job1));
		assertEquals("Verify that pending count equals 1 after adding one job", sb.pending_size(), 1);
		
		assertTrue("Verify that a job can be added to a non-empty queue", sb.add(job2));
		assertEquals("Verify that pending count equals 2 after adding second job", sb.pending_size(), 2);
		
		assertEquals("Verify that running count equals 0", sb.running_size(), 0);
		assertNotNull("Verify that a non-empty pending queue can call run_next_job()", sb.run_next_job());
		assertEquals("Verify that running count equals 1", sb.running_size(), 1);
		
		assertTrue("Try to remove running job", sb.remove(job1));
		assertEquals("Verify that running count equals 0 after removing running job", sb.running_size(), 0);
		
		assertTrue("Try to add already running job", sb.add(job3));
		assertEquals("Verify that running count equals 1 after adding a running job", sb.running_size(), 1);
		
		assertTrue("Try to remove pending job", sb.remove(job2));
		assertEquals("Verify that pending count equals 0 after removing running job", sb.pending_size(), 0); 
		
		assertFalse("Try to remove already removed running job", sb.remove(job1));
		assertFalse("Try to remove already removed pending job", sb.remove(job2));
	}
	
	@Test
	public void testRemovingUnknownJobs() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);
		
		Job job = new Job(site_name, "foo", 1);
		assertFalse("Try to remove non-existant job", sb.remove(job));
	}
	
	@Test
	public void testRemovingRunningJob() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);
		
		Job job1 = new Job(site_name, "foo", 1, true);
		Job job2 = new Job(site_name, "foo", 1);
		assertTrue("Add running job", sb.add(job1));
		assertTrue("Remove job with same key as running job", sb.remove(job2));
		
		assertTrue("Add pending job", sb.add(job2));
		assertTrue("Remove job with same key as pending job", sb.remove(job1));
	}
	
	@Test
	public void testAddingJobWithSameId() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);
		
		Job job1 = new Job(site_name, "foo", 1);
		Job job2 = new Job(site_name, "foo", 2, true);
		assertTrue("Add job", sb.add(job1));
		assertEquals("Size of queue equals 1", 1, sb.pending_size());
		assertFalse("Add same job", sb.add(job1));
		assertEquals("Size of queue equals 1", 1, sb.pending_size());	
		
		assertFalse("Add job with same id", sb.add(job2));
		assertEquals("Size of queue equals 1", 1, sb.pending_size());	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidSite() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);
		Job job = new Job("foo", "foo", 1);
		
		assertTrue("Try to add a job with the wrong site name to a site bucket", sb.add(job));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullJobAdditions() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);
		
		assertTrue("Try to add a null job to a site bucket", sb.add(null));	
	}
	
	@Test
	public void testPriorityOrder() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);		

		Job[] jobs = new Job[11];
		for (int priority=0; priority<=10; priority++) {
			jobs[priority] = new Job(site_name, String.valueOf(priority), priority);
			assertTrue("Add job of priority " + priority, sb.add(jobs[priority]));
		}
		
		// now verify that running the next job pulls them in priority order
		for (int priority=10; priority>0; --priority) {
			assertFalse("Job of priority " + priority + " is not currently running", jobs[priority].get_is_running());
			assertNotNull("Run next job", sb.run_next_job());
			assertTrue("Job of priority " + priority + " is now currently running", jobs[priority].get_is_running());
		}
	}
	
	@Test
	public void testDateTimeOrder() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);		

		LocalDateTime now = LocalDateTime.now();
		
		Job[] jobs = new Job[11];
		for (int order=0; order<=10; order++) {
			jobs[order] = new Job(site_name, String.valueOf(order), 1, now.minusNanos(order));
			assertTrue("Add job created with order " + order, sb.add(jobs[order]));		
		}
		
		// now verify that running the next job pulls them in timestamp order
		for (int order=10; order>=0; --order) {
			assertFalse("Job of order " + order + " is not currently running", jobs[order].get_is_running());
			assertNotNull("Run next job", sb.run_next_job());
			assertTrue("Job of order " + order + " is now currently running", jobs[order].get_is_running());
		}
	}
	
	@Test
	public void testCompareTo() {
		String site_name = testName.getMethodName();
		String site_name_1 = site_name + "_1";
		String site_name_2 = site_name + "_2";
		SiteBucket sb1 = new SiteBucket(site_name_1);
		SiteBucket sb2 = new SiteBucket(site_name_2);
		
		LocalDateTime now = LocalDateTime.now();
		
		Job job1 = new Job(site_name_1, "1", 1, now.plusNanos(3));
		Job job2 = new Job(site_name_1, "2", 2, now.plusNanos(2));
		Job job3 = new Job(site_name_2, "3", 1, now.plusNanos(1));
		Job job4 = new Job(site_name_2, "4", 1, now.plusNanos(0));
		
		assertTrue("Able to add job1 to site_bucket 1", sb1.add(job1));
		assertEquals("site_bucket_1 has 0 running jobs", 0, sb1.running_size());
		assertTrue("first site_bucket comes first in sort order because it has pending jobs and second site_bucket does not", sb1.compareTo(sb2) < 0);

		assertTrue("Able to add job3 to site_bucket 2", sb2.add(job3));
		assertEquals("site_bucket_2 has 0 running jobs", 0, sb2.running_size());
		assertTrue("second site_bucket comes first in sort order because it has same number of running_jobs as first site_bucket, pending jobs at same priority and earliest pending job", sb1.compareTo(sb2) > 0);	

		assertTrue("Able to add job4 to site_bucket 2", sb2.add(job4));
		assertNotNull("Able to schedule for site_bucket 2", sb2.run_next_job());
		assertTrue("first site_bucket comes first in sort order because it has no running jobs and second site_bucket does", sb1.compareTo(sb2) < 0);

		assertTrue("Verify that job4 is the running job", job4.get_is_running());
		assertTrue("Remove job 3", sb2.remove(job4));
		assertTrue("second site_bucket comes first in sort order because it has same number of running_jobs as first site_bucket, pending jobs at same priority and earliest pending job", sb1.compareTo(sb2) > 0);	

		assertTrue("Able to add job2 to site_bucket 1", sb1.add(job2));
		assertTrue("first site_bucket comes first in sort order because it has same number of running_jobs as first site_bucket, but a pending jobs at higher priority", sb1.compareTo(sb2) < 0);
	}
	
	@Test
	public void testEquals() {
		String site_name = testName.getMethodName();
		SiteBucket sb1 = new SiteBucket(site_name);
		SiteBucket sb2 = new SiteBucket(site_name);
		
		assertEquals("two site buckets with same name .equals each other", sb1, sb2);
	
		Job job = new Job(site_name, "1", 1);
		assertTrue("Add job to site bucket", sb1.add(job));
		assertEquals("two site buckets with same name .equals each other after a job has been added to one", sb1, sb2);
		
		assertNotNull("Run job", sb1.run_next_job());
		assertEquals("two site buckets with same name .equals each other after a job has been run", sb1, sb2);

		assertTrue("Remove job", sb1.remove(job));
		assertEquals("two site buckets with same name .equals each other after a job has been removed", sb1, sb2);
	}
	
	@Test
	public void testClear() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);

		Job job1 = new Job(site_name, "1", 1);
		assertTrue("Add job to site bucket", sb.add(job1));
		
		Job job2 = new Job(site_name, "2", 1);
		assertTrue("Add job to site bucket", sb.add(job2));
		
		assertEquals("Count of jobs in site", 2, sb.size());
		
		assertNotNull("Can run next job", sb.run_next_job());
		
		assertEquals("Count of jobs in site", 2, sb.size());
		assertEquals("Count of running jobs in site", 1, sb.running_size());
		assertEquals("Count of running jobs in site", 1, sb.pending_size());

		sb.clear();
		
		assertEquals("Count of jobs in site", 0, sb.size());
		assertEquals("Count of running jobs in site", 0, sb.running_size());
		assertEquals("Count of pending jobs in site", 0, sb.pending_size());	
	
		assertNull("Can not schedule another job", sb.run_next_job());
	}
}
