package net.sourceforge.retroweaver.tests;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedList;

public class TypeTest extends AbstractTest {

	public void testEnclosingMethod() {
		class Local1 {}

		Method m = Local1.class.getEnclosingMethod();

		assertEquals("testEnclosingMethod", "testEnclosingMethod", m.getName());
	}

	public void testEnclosingConstructor() {
		TopLevel o = new TopLevel("blah");

		Class[] actual = o.c.getParameterTypes();

		assertTrue("testEnclosingConstructor", actual.length == 1 && actual[0].equals(String.class));
	}

	public void testEnclosingClass() {
		class Local2 {}

		Class local = Local2.class.getEnclosingClass();
		Class an = new Object() {}.getClass();

		assertNull("testEnclosingClass top", TypeTest.class.getEnclosingClass());	
		assertSame("testEnclosingClass local", TypeTest.class, local);
		assertSame("testEnclosingClass anonymous", an, an);
		assertSame("testEnclosingClass inner", TopLevel.class, TopLevel.Inner.class.getEnclosingClass());
		assertSame("testEnclosingClass nested", TopLevel.class, TopLevel.Nested.class.getEnclosingClass());
	}

	public void testLocal() {
		class Local3 {}

		Class an = new Object() {}.getClass();

		assertTrue("testLocal Local3", Local3.class.isLocalClass());
		assertFalse("testLocal anonymous", an.isLocalClass());
		assertFalse("testLocal top level", TypeTest.class.isLocalClass());
		assertFalse("testLocal inner class", TopLevel.Inner.class.isLocalClass());
	}

	public void testMemberClass() {
		class Local4 {}
		
		Class an = new Object() {}.getClass();

		assertFalse("testMemberClass Local4", Local4.class.isMemberClass());
		assertFalse("testMemberClass anonymous", an.isMemberClass());
		assertFalse("testMemberClass top level", TypeTest.class.isMemberClass());
		assertTrue("testMemberClass inner class", TopLevel.Inner.class.isMemberClass());		
		assertTrue("testMemberClass nested class", TopLevel.Nested.class.isMemberClass());		
	}

	public void testCanonicalName() {
		class Local4 {}
		
		Class an = new Object() {}.getClass();

		Class[] a1 = new Class[0];
		Local4[] a2 = new Local4[0];
		Class[][] a3 = new Class[0][];

		assertEquals("canonicalName", "net.sourceforge.retroweaver.tests.TopLevel", TopLevel.class.getCanonicalName());
		assertNull("canonicalName", Local4.class.getCanonicalName());
		assertNull("canonicalName", an.getCanonicalName());
		assertEquals("canonicalName array 1", "java.lang.Class[]", a1.getClass().getCanonicalName());
		assertNull("canonicalName array 2", a2.getClass().getCanonicalName());
		assertEquals("canonicalName array 3", "java.lang.Class[][]", a3.getClass().getCanonicalName());
		assertEquals("testMemberClass inner class", "net.sourceforge.retroweaver.tests.TopLevel.Inner", TopLevel.Inner.class.getCanonicalName());	
		assertEquals("testMemberClass nested class", "net.sourceforge.retroweaver.tests.TopLevel.Nested", TopLevel.Nested.class.getCanonicalName());
	}

	public void testGenericInterfaces() {
		TopLevel<String> t = new TopLevel<String>("blah");
		Class c = t.getClass();
		
		genericInterfaceTest(c,
			"java.util.LinkedList<E> owner: null raw: class java.util.LinkedList types: (E)",
			2,
			"1 interface java.io.Serializable 2 java.lang.Comparable<E> owner: null raw: interface java.lang.Comparable types: (E) ",
			1,
			"1 E name: E bounds: (1 class java.lang.String ) generic: class net.sourceforge.retroweaver.tests.TopLevel ");

		genericInterfaceTest(this.getClass(),
				"class net.sourceforge.retroweaver.tests.AbstractTest",
				0, "", 0, "");
		
		genericInterfaceTest(TopLevel2.class,
			"java.util.LinkedList<java.lang.String> owner: null raw: class java.util.LinkedList types: (class java.lang.String)",
			2,
			"1 interface java.io.Serializable 2 java.lang.Comparable<java.lang.String> owner: null raw: interface java.lang.Comparable types: (class java.lang.String) ",
			0,
			"");
	}

	private void genericInterfaceTest(Class c, String es, int i, String ei, int p, String ep) {
		Type superclass = c.getGenericSuperclass();
		Type[] types = c.getGenericInterfaces();
		TypeVariable[] typeVariables = c.getTypeParameters();

		/*
		System.out.println("Superclass: " + printType(superclass));
		System.out.println("generic interfaces Length: " + types.length);
		System.out.println("\t#" + printTypes(types) + '#');
		System.out.println("parameters Length: " + typeVariables.length);
		System.out.println("\t#" + printTypes(typeVariables) + '#');
		/**/

		assertEquals("getGenericSuperclass", es, printType(superclass));

		assertEquals("getGenericInterfaces length", i, types.length);
		assertEquals("getGenericInterfaces", ei, printTypes(types));

		assertEquals("getTypeParameters length", p, typeVariables.length);
		assertEquals("getTypeParameters", ep, printTypes(typeVariables));
	}

	private static String printType(Type t) {
		if (t == null) {
			return "null";
		}

		String s = t.toString();
		
		if (t instanceof GenericArrayType) {
			GenericArrayType a = (GenericArrayType) t;
			s += " component: " + printType(a.getGenericComponentType());
		} else if (t instanceof ParameterizedType) {
			ParameterizedType p = (ParameterizedType) t;
			s += " owner: " + printType(p.getOwnerType()) +
				" raw: " + printType(p.getRawType()) +
				" types: (";
			for(Type a: p.getActualTypeArguments()) {
				// printTypes creates infinite loop
				s += a.toString();
			}
			s += ')';
		} else if (t instanceof TypeVariable) {
			TypeVariable v = (TypeVariable) t;
			s += " name: " + v.getName() +
				" bounds: (" + printTypes(v.getBounds()) +
				") generic: " + v.getGenericDeclaration();
		} else if (t instanceof WildcardType) {
			WildcardType w = (WildcardType) t;
			s += " lower: (" + printTypes(w.getLowerBounds()) +
				") upper: (" + printTypes(w.getUpperBounds()) + ')';
		}

		return s;
	}

	private static String printTypes(Type types[]) {
		if (types == null)
			return "null";

		String s = "";
		int i = 1;
		for(Type t: types) {
			s += "" + (i++) + ' ' + printType(t) + ' ';
		}
		return s;
	}

}

class TopLevel2 extends LinkedList<String> implements Serializable, Comparable<String>{
	public int compareTo(String o) { return 0; }
}

class TopLevel<E extends String> extends LinkedList<E> implements Serializable, Comparable<E>{

	static class Nested {}

	class Inner {}

	Constructor c;

	public TopLevel(String s) {
		class Local4 {}
		
		c = Local4.class.getEnclosingConstructor();
	}

	public int compareTo(E o) { return 0; }

}