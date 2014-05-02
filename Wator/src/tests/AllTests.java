package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DirectionTest.class, WatorModelTest.class,
		FishTest.class, LocTest.class, SharkTest.class })
public class AllTests {

}
