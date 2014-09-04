package DoS.DoS;

import static org.junit.Assert.*;

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
		
		Job job1 = new Job( site_name, "foo1");
		Job job2 = new Job( site_name, "foo2");

		assertTrue("Verify that a job can be added to an empty queue", sb1.add(job1));		
		assertEquals("Verify that pending counts are equal on job queues", sb1.pending_size(), sb2.pending_size());
		
		assertTrue("Verify that a job can be added to an non-empty queue", sb1.add(job2));		
		assertEquals("Verify that pending counts are equal on job queues", sb1.pending_size(), sb2.pending_size());
		
		assertTrue("Verify that a non-empty pending queue can call run_next_job()", sb1.run_next_job());
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
		
		Job job1 = new Job(site_name, "foo1");
		Job job2 = new Job(site_name, "foo2");
		Job job3 = new Job(site_name, "foo3", true);
		
		assertTrue("Verify that a job can be added to an empty queue", sb.add(job1));
		assertEquals("Verify that pending count equals 1 after adding one job", sb.pending_size(), 1);
		
		assertTrue("Verify that a job can be added to a non-empty queue", sb.add(job2));
		assertEquals("Verify that pending count equals 2 after adding second job", sb.pending_size(), 2);
		
		assertEquals("Verify that running count equals 0", sb.running_size(), 0);
		assertTrue("Verify that a non-empty pending queue can call run_next_job()", sb.run_next_job());
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidSite() {
		String site_name = testName.getMethodName();
		SiteBucket sb = new SiteBucket(site_name);
		Job job = new Job("foo", "foo");
		
		assertTrue("Try to add a job with the wrong site name to a site bucket", sb.add(job));
	}
}
