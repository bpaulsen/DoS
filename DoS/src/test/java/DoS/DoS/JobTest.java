package DoS.DoS;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class JobTest {
    @Rule
    public TestName testName = new TestName();
    
	@Test
	public void testJobCreation() {
		String site_name = testName.getMethodName();
		Job job = new Job(site_name, "foo", 1);
		assertFalse("job created without specifying is_running defaults to an is_running of false", job.get_is_running());
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullSiteWithIdPriority() {
		Job job = new Job(null, "foo", 1);
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullSiteWithIdPriorityCreatetimestamp() {
		Job job = new Job(null, "foo", 1, LocalDateTime.now());
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullSiteWithIdPriorityIsrunning() {
		Job job = new Job(null, "foo", 1, true);
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullSiteWithIdPriorityCreatetimestampIsrunning() {
		Job job = new Job(null, "foo", 1, LocalDateTime.now(), true);
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullIdWithIdPriority() {
		Job job = new Job("foo", null, 1);
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullIdWithSitePriorityCreatetimestamp() {
		Job job = new Job("foo", null, 1, LocalDateTime.now());
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullIdWithSitePriorityIsrunning() {
		Job job = new Job("foo", null, 1, true);
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullIdWithSitePriorityCreatetimestampIsrunning() {
		Job job = new Job("foo", null, 1, LocalDateTime.now(), true);
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullCreateTimestampWithSiteIdPriority() {
		Job job = new Job("foo", "foo", 1, null);
		assertTrue("Job is not null", job != null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullCreatetimestampWithSitePriorityIdIsrunning() {
		Job job = new Job("foo", "foo", 1, null, true);
		assertTrue("Job is not null", job != null);
	}
	
	@Test
	public void testCompareTo() {
		String site_name = testName.getMethodName();
		Job job = new Job(site_name, "foo", 1 );
		
		assertTrue("Verify that job with the same id is treated as equal", job.compareTo(new Job(site_name, "foo", 2)) == 0 );
		assertTrue("Verify that higher priority jobs come first in sort order", job.compareTo(new Job(site_name, "foo2", 2)) > 0 );
		assertTrue("Verify that earlier timestamp jobs come first in sort order", job.compareTo(new Job(site_name, "foo2", 1)) < 0);
	}
}
