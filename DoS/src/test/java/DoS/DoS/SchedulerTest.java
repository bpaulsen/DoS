package DoS.DoS;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class SchedulerTest {
    @Rule
    public TestName testName = new TestName();
    
	@Test
	public void testScheduling() {
		String site_name = testName.getMethodName();
		
		LocalDateTime now = LocalDateTime.now();
		
		Job job1 = new Job(site_name + "_1", "1", 1, now.plusNanos(0));
		Job job2 = new Job(site_name + "_1", "2", 1, now.plusNanos(1));
		Job job3 = new Job(site_name + "_2", "3", 1, now.plusNanos(2));
		Job job4 = new Job(site_name + "_2", "4", 1, now.plusNanos(3));
		
		Scheduler scheduler = new Scheduler();
		assertTrue("Able to add job1 to scheduler", scheduler.add(job1));
		assertEquals("Only one site has jobs", 1, scheduler.site_size());
		assertTrue("Able to add job2 to scheduler", scheduler.add(job2));	
		assertEquals("Only one site still has jobs", 1, scheduler.site_size());
		
		assertTrue("Able to add job3 to scheduler", scheduler.add(job3));
		assertEquals("Two sites have jobs", 2, scheduler.site_size());
		assertTrue("Able to add job4 to scheduler", scheduler.add(job4));
		assertEquals("Two sites still have jobs", 2, scheduler.site_size());
		
		assertNotNull("Schedule first job", scheduler.run_next_job());
		assertTrue("Job 1 is now running", job1.get_is_running());
		assertNotNull("Schedule second job", scheduler.run_next_job());
		assertTrue("Job 3 is now running", job3.get_is_running());
		
		assertTrue("Job 3 is completed", scheduler.remove(job3));
		assertNotNull("Schedule third job",scheduler.run_next_job());
		assertTrue("Job 4 is now running", job4.get_is_running());
		assertTrue("Job 4 is completed", scheduler.remove(job4));
		assertEquals("Only one site still has jobs", 1, scheduler.site_size());
	}

	@Test
	public void testPerformance() {
		long start_time = System.nanoTime();
		Scheduler scheduler = new Scheduler();
		
		for (int i=1; i<=1000; i++) {
			for (int j=1; j<=1000; j++) {
				Job job = new Job(String.valueOf(i), String.valueOf(j), 1);
				assertTrue("Verify that job can be added", scheduler.add(job));
			}
		}
		
		long end_time = System.nanoTime();
		assertTrue("Run time " + (end_time - start_time) / 1000000 + " to add 1 million jobs is less than 6 seconds", (end_time - start_time) / 1000000 < 6000);

		start_time = end_time;
		for (int i=1; i<=1000; i++) {
			for (int j=1; j<=1000; j++) {
				assertNotNull("Verify that job can be scheduled", scheduler.run_next_job());
			}
		}
		end_time = System.nanoTime();
		assertTrue("Run time " + (end_time - start_time) / 1000000 + " to schedule 1 million jobs is less than 14 seconds", (end_time - start_time) / 1000000 < 14000);
		
		start_time = end_time;		
		for (int i=1; i<=1000; i++) {
			for (int j=1; j<=1000; j++) {
				Job job = new Job(String.valueOf(i), String.valueOf(j), 1);
				assertTrue("Verify that job (" + i + ", " + j + ") can be removed", scheduler.remove(job));
			}
		}
		
		end_time = System.nanoTime();
		assertTrue("Run time " + (end_time - start_time) / 1000000 + " to remove 1 million jobs is less than 3 seconds", (end_time - start_time) / 1000000 < 3000);
	}
	
	@Test
	public void testNullOfFirstPendingJob() {
		Scheduler scheduler = new Scheduler();
		scheduler.clear();
		assertEquals("Empty scheduler has no first_pending_job", null, scheduler.first_pending_job());
	}
}

