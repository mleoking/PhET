package net.sourceforge.retroweaver.tests;

/**
 * Test that calls to 1.5-only Class methods work correctly. Only a subset of
 * these are actually handled!
 */

public class ClassMethodsTest extends AbstractTest {

	public void testCast() {
		Object obj = mystery();
		assertEquals("testCast", B.class.cast(obj).f(), "B");
	}

	public void testInvalidCast() {
		Object obj2 = mystery2();
		try {
			B.class.cast(obj2).f();
			fail("testInvalidCast should raise a ClassCastException");
		} catch (ClassCastException e) {
			success("testInvalidCast");
		}
	}

	public void testAsSubclass() throws Exception {
		Class<? extends A> bAsA = getBAsA();
		Class<? extends B> b = bAsA.asSubclass(B.class);

		B bInstance = b.newInstance();
		assertEquals("testAsSubclass", bInstance.f(), "B");
	}

	public void testInvalidAsSubclass() {
		Class<? extends A> cAsA = getCAsA();
		try {
			cAsA.asSubclass(B.class);
			fail("testInvalidAsSubclass should raise a ClassCastException");
		} catch (ClassCastException e) {
			success("testInvalidAsSubclass");
		}
	}

	static class Nested {}

	public void testGetSimpleName() {
		Class cit = (new int[0]).getClass();
		assertEquals("testGetSimpleName", "int[]", cit.getSimpleName());

		Class cst = (new String[0]).getClass();
		assertEquals("testGetSimpleName", "String[]", cst.getSimpleName());

		Class cs = String.class;
		assertEquals("testGetSimpleName", "String", cs.getSimpleName());

		Class ct = ClassMethodsTest.class;
		assertEquals("testGetSimpleName", "ClassMethodsTest", ct
				.getSimpleName());

		assertEquals("testGetSimpleName", "Nested", Nested.class.getSimpleName());

		// tests for local, inner and anonymous classes done in InnerClassTest
		// to avoid JVM 1.2 and 1.3 errors
	}


	public void testIsAnonymousClass() {
		Class cit = (new int[0]).getClass();
		assertFalse("testIsAnonymousClass", cit.isAnonymousClass());

		Class cst = (new String[0]).getClass();
		assertFalse("testIsAnonymousClass", cst.isAnonymousClass());

		assertFalse("testIsAnonymousClass", String.class.isAnonymousClass());

		assertFalse("testIsAnonymousClass", Nested.class.isAnonymousClass());

		// tests for local, inner and anonymous classes done in InnerClassTest
		// to avoid JVM 1.2 and 1.3 errors
	}

	static abstract class A {
		public abstract String f();
	}

	static class B extends A {
		public String f() {
			return "B";
		}
	}

	static class C extends A {
		public String f() {
			return "C";
		}
	}

	public Object mystery() {
		return new B();
	}

	public Object mystery2() {
		return new C();
	}

	public Class<? extends A> getBAsA() {
		return B.class;
	}

	public Class<? extends A> getCAsA() {
		return C.class;
	}

}
