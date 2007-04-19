package net.sourceforge.retroweaver.tests;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class RetroweaverTestSuite extends TestSuite {

	public RetroweaverTestSuite() {
		super();

		// tests valid for all JVM versions

		addTestSuite(EnumTest.class);
		addTestSuite(ClassLiteralTest.class);
		addTestSuite(StringBuilderTest.class);
		addTestSuite(ClassMethodsTest.class);
		addTestSuite(AutoboxTest.class);
		addTestSuite(FullCoverageTest.class);
		addTestSuite(IterableTest.class);
		addTestSuite(CollectionsTest.class);
		addTestSuite(BigDecimalTest.class);
		addTestSuite(SystemTest.class);
		addTestSuite(BooleanTest.class);
		addTestSuite(WriterTest.class);

		// tests below are only supposed to be run for some JVM versions

		String version = System.getProperty("java.version");
		if (version.length() < 3) {
			throw new UnsupportedOperationException("Unsupported Java version: " + version);
		}

		int v = version.charAt(2) - '0';

		if (v < 2 || v > 6) {
			throw new UnsupportedOperationException("Unsupported Java version: " + version);
		}

		if (v < 5) {
			// test missing classes and methods. Test for 1.4 and below only
			addTestSuite(VerifierTest.class);

			if (v > 2) {
				// EMPTY_MAP field is only in 1.3+
				addTestSuite(MapTest.class);
			}
		}

		if (v > 3) {
			// Concurrent backport is only for 1.4+
			addTestSuite(ConcurrentTest.class);

			// CharSequence class was defined in 1.4
			addTestSuite(StringBufferTest.class);
			addTestSuite(WriterAppendTest.class);
			addTestSuite(AppendableTest.class);
			addTestSuite(StringTest.class);

			// StackTraceElement class was defined in 1.4
			addTestSuite(ThreadTest.class);
			
			addTestSuite(AnnotationTest.class);
		}

		// tests below should probably work, with some byte code changes...
		if (v > 2) {
			// FIXME: error JVM 1.2 for NAMES field in interface
			addTestSuite(InterfaceFieldTest.class);
		}

		if (v > 3) {
			// FIXME: problem in JVM 1.2 and 1.3 with inner classes
			addTestSuite(InnerClassTest.class);
			addTestSuite(TypeTest.class);
		}
	}

	public static TestSuite suite() {
		return new RetroweaverTestSuite();
	}

	public static void main(String args[]) {
		TestRunner.run(suite());
	}

}
