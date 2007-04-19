package net.sourceforge.retroweaver.tests;

/**
 * Ensures that Retroweaver translates LDC/LDC_W instructions for class literals
 * correctly.
 */

public class ClassLiteralTest extends AbstractTest {

	public void testClassLiteral() {
		String name = ClassLiteralTest.class.getName();
		assertEquals("testClassLiteral1", name,
				"net.sourceforge.retroweaver.tests.ClassLiteralTest");

		name = String.class.getName();
		assertEquals("testClassLiteral2", name, "java.lang.String");
	}

	public void testArrays() {
		String name = ClassLiteralTest[].class.getName();
		assertEquals("ClassLiteralTestArray", name,
				"[Lnet.sourceforge.retroweaver.tests.ClassLiteralTest;");
		name = ClassLiteralTest[][].class.getName();
		assertEquals("ClassLiteralTestArray2", name,
				"[[Lnet.sourceforge.retroweaver.tests.ClassLiteralTest;");

		name = int[].class.getName();
		assertEquals("intArray", name, "[I");
	}

	public void testInterface() {
		Class clazz1 = String.class;
		Class clazz2 = Integer.class;

		assertEquals("testInterface class 1", clazz1, ClassLiteralTest1.clazz1);
		assertEquals("testInterface class 2", clazz2, ClassLiteralTest1.clazz2);

		assertEquals("testInterface interface 1", clazz1, ClassLiteralTest2.clazz1);
		assertEquals("testInterface interface 2", clazz2, ClassLiteralTest2.clazz2);
	}

	public void testRetroweaverRuntime() {
		assertTrue("testRetroweaverRuntime Enum", Enum.class.isAssignableFrom(LetterEnum.class));

		assertEquals("testRetroweaverRuntime StringBuilder", StringBuilder.class, new StringBuilder().getClass());
	}

	public void testMissingClass() {
		try {
			Class clazz = MissingClass.class;
			fail("Class should not be defined: " + clazz.getName());
		} catch (NoClassDefFoundError e) {
			success("testMissingClass");
		}
	}
}

class MissingClass {
	// make sure class is not part of the weaved classes
}

class ClassLiteralTest1 {
	static Class clazz1 = String.class;
	static Class clazz2 = Integer.class;
}

interface ClassLiteralTest2 {
	Class clazz1 = String.class;
	Class clazz2 = Integer.class;
}
