package net.sourceforge.retroweaver.tests;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests the annotations implementation. Specifically, - Do annotations with
 * RetentionPolicy.RUNTIME get retained - Are they available through the
 * standard reflection APIs - Do their defaults behave correctly - Do package
 * annotations load correctly
 */

@Retention(RetentionPolicy.RUNTIME)
@interface TestClassAnnotation {
	public String value() default "Hello";

	public int value1() default 10;

	public float value2() default 12.3f;

	public Class value3() default String.class;

	public double value4();

	public Class value5();

	public String[] array1() default { "a", "b", "c" };
	
	public int[] intArray() default { 1, 2};
	public int[] intArray2() default {};
	public int[] intArray3();
}

enum EnumAnnotation {
	A, B, C;
}

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@interface EnumAnnotationTest {
	public EnumAnnotation value();
}

@Retention(RetentionPolicy.RUNTIME)
@interface TestMethodAnnotation {
	public String value();
}

@Retention(RetentionPolicy.RUNTIME)
@interface TestFieldAnnotation {
	public String value();
}

@Retention(RetentionPolicy.RUNTIME)
@interface Name {
	String first();

	String last();
}

@Retention(RetentionPolicy.RUNTIME)
@interface Author {
	Name value();

	Name[] reviewers();
}

@TestClassAnnotation(value4 = 3.2f, value5 = Method.class, array1 = { "a", "b", "c", "d" }
		, intArray = {1, 2, 3}, intArray3 = {}
		)
@EnumAnnotationTest(EnumAnnotation.A)
@Author(value = @Name(first = "John", last = "Doe"), reviewers = {
		@Name(first = "John", last = "Doe"),
		@Name(first = "Jane", last = "Doe") })
public class AnnotationTest extends AbstractTest {

	public void testClassAnnotations() {
		try {
			Class<AnnotationTest> c = AnnotationTest.class;

			Annotation[] annotations = c.getAnnotations();
			assertEquals("length", 3, annotations.length);

			TestClassAnnotation a = c.getAnnotation(TestClassAnnotation.class);
			assertEquals("value", "Hello", a.value());
			assertEquals("value1", 10, a.value1());
			assertEquals("value2", 12.3f, a.value2(), 0.000001f);
			assertEquals("value3", String.class, a.value3());
			assertEquals("value4", 3.2d, a.value4(), 0.000001f);
			assertEquals("value5", Method.class, a.value5());
			assertTrue("array1", Arrays.equals(new String[] { "a", "b", "c", "d" }, a.array1()));
			assertTrue("intArray", Arrays.equals(new int[] { 1, 2, 3 }, a.intArray()));
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testIsAnnotationPresent() {
		assertTrue("isAnnotationPresent", AnnotationTest.class.isAnnotationPresent(TestClassAnnotation.class));
		assertFalse("isAnnotationPresent", AnnotationTest.class.isAnnotationPresent(ParameterAnnotationTest.class));
		assertFalse("isAnnotationPresent", Class.class.isAnnotationPresent(TestClassAnnotation.class));
	}
	
	@TestMethodAnnotation("bar")
	@EnumAnnotationTest(EnumAnnotation.C)
	public void testMethodAnnotations() throws Exception {
		Method m = AnnotationTest.class.getMethod("testMethodAnnotations");

		Annotation[] annotations = m.getAnnotations();
		assertEquals("length", 2, annotations.length);

		TestMethodAnnotation a1 = m.getAnnotation(TestMethodAnnotation.class);
		assertEquals("value", "bar", a1.value());

		EnumAnnotationTest a2 = m.getAnnotation(EnumAnnotationTest.class);
		assertEquals("value", EnumAnnotation.C, a2.value());
		
		assertNull("getDefaultValue", m.getDefaultValue());
		
		m = TestMethodAnnotation.class.getMethod("value");
		assertNull("getDefaultValue", m.getDefaultValue());
	
		m = TestClassAnnotation.class.getMethod("value");
		assertEquals("getDefaultValue", "Hello", m.getDefaultValue());

		m = TestClassAnnotation.class.getMethod("value1");
		assertEquals("getDefaultValue", 10, m.getDefaultValue());
		
		m = AnnotatedInnerClass.class.getMethod("innerMethod");
		a1 = m.getAnnotation(TestMethodAnnotation.class);
		assertEquals("value", "inner", a1.value());
	}

	@TestFieldAnnotation("foo")
	@EnumAnnotationTest(EnumAnnotation.B)
	public String testField;

	public void testFieldAnnotations() throws Exception {
		Field f = AnnotationTest.class.getField("testField");

		Annotation[] annotations = f.getAnnotations();
		assertEquals("length", 2, annotations.length);

		TestFieldAnnotation a1 = f.getAnnotation(TestFieldAnnotation.class);
		assertEquals("value", "foo", a1.value());

		EnumAnnotationTest a2 = f.getAnnotation(EnumAnnotationTest.class);
		assertEquals("value", EnumAnnotation.B, a2.value());
	}


	@Retention(RetentionPolicy.RUNTIME)
	@interface ParameterAnnotationTest {
		public String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface ParameterAnnotationIntTest {
		public int value();
	}

	public void methodWithParameterAnnotations(
			@ParameterAnnotationTest("p1") @ParameterAnnotationIntTest(1) String p1,
			@ParameterAnnotationTest("p2") @ParameterAnnotationIntTest(2) int p2) {
	}

	public void testParameterAnnotations() throws Exception {
		Method m = AnnotationTest.class.getMethod("methodWithParameterAnnotations", String.class, int.class);

		Annotation[][] annotations = m.getParameterAnnotations();
		assertEquals("length", 2, annotations.length);

		assertEquals("length", 2, annotations[0].length);
		assertEquals("length", 2, annotations[1].length);

		for(int i = 0; i < annotations.length; i++)
			for(int j = 0; j < annotations[i].length; j++) {
				Annotation a = annotations[i][j];
				if (a instanceof ParameterAnnotationTest)
					assertEquals("value", "p"+(i+1), ((ParameterAnnotationTest)a).value());
				else
					assertEquals("value", (i+1), ((ParameterAnnotationIntTest)a).value());
			}
	}

	public void testAnnotationArrays() throws Exception {
		try {
			// make sure arrays are copied so that local modifications
			// do not have any impact on future calls
			Class<AnnotationTest> c = AnnotationTest.class;
			TestClassAnnotation a = c.getAnnotation(TestClassAnnotation.class);
			int[] intArray = a.intArray();
			assertTrue("intArray", Arrays.equals(new int[] { 1, 2, 3 }, intArray));

			intArray[0] = 0;
			intArray = a.intArray();
			assertTrue("intArray", Arrays.equals(new int[] { 1, 2, 3 }, intArray));
			
			Method m = TestClassAnnotation.class.getMethod("array1");
			String stringArray[] = (String[])m.getDefaultValue();
			assertTrue("array1", Arrays.equals(new String[] { "a", "b", "c" }, stringArray));

			stringArray[0] = "d";
			stringArray = (String[])m.getDefaultValue();
			assertTrue("array1", Arrays.equals(new String[] { "a", "b", "c" }, stringArray));
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	class AnnotatedInnerClass {
		@TestMethodAnnotation("inner")
		public void innerMethod() {
		}
	}

	@EnumAnnotationTest(EnumAnnotation.C)
	public class Parent {
		@EnumAnnotationTest(EnumAnnotation.A) public void foo() {}
		@EnumAnnotationTest(EnumAnnotation.B) public String bar;
	}

	public class Child extends Parent {
		public void foo() {}
		public String bar;
	}

	@EnumAnnotationTest(EnumAnnotation.A)
	public class Child2 extends Parent {
		public void foo() {}
		public String bar;
	}

	public void testInheritedAssertions() {
		assertEquals("declared", 0, Child.class.getDeclaredAnnotations().length);

		assertEquals("inherited size", 1, Child.class.getAnnotations().length);

		EnumAnnotationTest a = Child.class.getAnnotation(EnumAnnotationTest.class);
		assertEquals("inherited", EnumAnnotation.C, a.value());

		assertEquals("inherited size", 1, Child2.class.getAnnotations().length);

		a = Child2.class.getAnnotation(EnumAnnotationTest.class);
		assertEquals("inherited", EnumAnnotation.A, a.value());
	}

	public void testInheritedMethodAssertions() throws Exception {
		// no inheritance
		Method m = Child.class.getMethod("foo", new Class[0]);

		assertEquals("declared method", 0, m.getDeclaredAnnotations().length);
		assertEquals("inherited method", 0, m.getAnnotations().length);
	}

	public void testInheritedFieldAssertions() throws Exception {
		// no inheritance
		Field f = Child.class.getField("bar");

		assertEquals("declared field", 0, f.getDeclaredAnnotations().length);
		assertEquals("inherited field", 0, f.getAnnotations().length);
	}

	class Super {
		@EnumAnnotationTest(EnumAnnotation.A) public void a() {}
	}

	public class Sub extends Super {		
	}

	public void testNonPublicClass() {
		Class<? extends Annotation> annotationClass = EnumAnnotationTest.class;

		List<Method> results= new ArrayList<Method>();

		// get super classes
		List<Class<?>> l = new ArrayList<Class<?>>();
		Class<?> current= Sub.class;
		while (current != null) {
			l.add(current);
			current= current.getSuperclass();
		}

 		for (Class<?> eachClass : l) {
			Method[] methods= eachClass.getDeclaredMethods();
			for (Method eachMethod : methods) {
				Annotation annotation= eachMethod.getAnnotation(annotationClass);
				if (annotation != null) {
					results.add(eachMethod);
				}
			}
		}

		assertEquals("testNonPublicClass", 1, results.size());
	}

	public static void main(String[] args) {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(AnnotationTest.class);
		TestRunner.run(suite);
	}

}
