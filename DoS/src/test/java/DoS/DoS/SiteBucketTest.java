package DoS.DoS;

import static org.junit.Assert.*;

import org.junit.Test;

public class SiteBucketTest {
	@Test
	public void testDuplicateSites() {
		SiteBucket sb1 = new SiteBucket("foo");
		SiteBucket sb2 = new SiteBucket("foo");
		
		assertSame( "Duplicate objects of the same site are using the same reference", sb1, sb2 );
	}
}
