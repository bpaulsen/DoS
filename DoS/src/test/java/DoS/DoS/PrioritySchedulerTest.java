package DoS.DoS;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class PrioritySchedulerTest {
    @Rule
    public TestName testName = new TestName();
	
	@Test
	public void testHighestPriorityWinsWhenNoJobsAreRunning() {
		String site_name = testName.getMethodName();
		
		// Create PriorityScheduler
		PriorityScheduler ps = new PriorityScheduler();
		
		// create jobs from priority 1 through priority 9
		Job jobs[] = new Job[10];
		for (int priority=1; priority<=9; priority++) {
			jobs[priority] = new Job(site_name, String.valueOf(priority), priority);
			assertTrue( "Able to add job of priority " + priority, ps.add(jobs[priority]));
		}
		
		Job job = new Job(site_name, "extra priority 9", 9);
		assertTrue( "Able to add extra job of priority 9", ps.add(job));

		Job runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 9 job is now running", "9", runningJob.get_id());
	
		runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 8 job is now running", "8", runningJob.get_id());	
		
		runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 7 job is now running", "7", runningJob.get_id());	
		
		runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 6 job is now running", "6", runningJob.get_id());
		
		runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 5 job is now running", "5", runningJob.get_id());
		
		runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 4 job is now running", "4", runningJob.get_id());
		
		runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 3 job is now running", "3", runningJob.get_id());
		
		runningJob = ps.poll();
		assertNotNull("Can run next job", runningJob);
		assertEquals("Priority 9 job is now running", "extra priority 9", runningJob.get_id());
	}

}
