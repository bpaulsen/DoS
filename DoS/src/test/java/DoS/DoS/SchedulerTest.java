package DoS.DoS;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class SchedulerTest {
    @Rule
    public TestName testName = new TestName();
    
	@Test
	public void testScheduling() {
		String site_name = testName.getMethodName();
		
		Job job1 = new Job(site_name + "_1", "1");
		Job job2 = new Job(site_name + "_1", "2");
		Job job3 = new Job(site_name + "_2", "3");
		Job job4 = new Job(site_name + "_2", "4");
		
		Scheduler scheduler = new Scheduler();
		assertTrue("Able to add job1 to scheduler", scheduler.add(job1));
		assertTrue("Able to add job2 to scheduler", scheduler.add(job2));
		assertTrue("Able to add job3 to scheduler", scheduler.add(job3));
		assertTrue("Able to add job4 to scheduler", scheduler.add(job4));
		
		assertTrue("Schedule first job", scheduler.run_next_job());
		assertTrue("Job 1 is now running", job1.get_is_running());
		assertTrue("Schedule second job", scheduler.run_next_job());
		assertTrue("Job 3 is now running", job3.get_is_running());
		
		assertTrue("Job 3 is completed", scheduler.remove(job3));
		assertTrue("Schedule third job",scheduler.run_next_job());
		assertTrue("Job 4 is now running", job4.get_is_running());
	}

}

