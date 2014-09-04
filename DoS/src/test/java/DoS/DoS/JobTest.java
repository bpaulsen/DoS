package DoS.DoS;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class JobTest {
    @Rule
    public TestName testName = new TestName();
    
	@Test
	public void testJobCreation() {
		String site_name = testName.getMethodName();
		Job job = new Job(site_name, "foo");
		site_name = "foo2";		
		assertEquals("verify that site name did not change", job.get_site(), testName.getMethodName());
	}	
}
