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

		ps.clear();
	}
	
	@Test
	public void testPerformance() {
		long start_time = System.nanoTime();
		PriorityScheduler scheduler = new PriorityScheduler();
		
		for (int priority=1; priority<=9; priority++) {
			for (int i=1; i<=100; i++) {
				for (int j=1; j<=100; j++) {
					Job job = new Job(String.valueOf(i), String.valueOf(j), priority);
					assertTrue("Verify that job can be added", scheduler.add(job));
				}
			}
		}
		
		long end_time = System.nanoTime();
		assertTrue("Run time " + (end_time - start_time) / 1000000 + " to add 90000 jobs is less than 2 seconds", (end_time - start_time) / 1000000 < 2000);

		start_time = end_time;
		for (int priority=1; priority<=9; priority++) {
			for (int i=1; i<=100; i++) {
				for (int j=1; j<=100; j++) {
					assertNotNull("Verify that job can be scheduled", scheduler.poll());
				}
			}
		}
		end_time = System.nanoTime();
		assertTrue("Run time " + (end_time - start_time) / 1000000 + " to schedule 90000 jobs is less than 6 seconds", (end_time - start_time) / 1000000 < 6000);
		
		start_time = end_time;	
		for (int priority=1; priority<=9; priority++) {
			for (int i=1; i<=100; i++) {
				for (int j=1; j<=100; j++) {
					Job job = new Job(String.valueOf(i), String.valueOf(j), priority);
					assertTrue("Verify that job (" + i + ", " + j + ") can be removed", scheduler.remove(job));
				}
			}
		}
		
		end_time = System.nanoTime();
		assertTrue("Run time " + (end_time - start_time) / 1000000 + " to remove 90000 jobs is less than 2 second", (end_time - start_time) / 1000000 < 2000);
	}

}
