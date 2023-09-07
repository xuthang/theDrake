package testRuns;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	testRuns.suite01.TestSuite.class,
	testRuns.suite02.TestSuite.class,
	testRuns.suite03.TestSuite.class,
	testRuns.suite04.TestSuite.class,
	testRuns.suite05.TestSuite.class
})

public class MainSuite {

}
