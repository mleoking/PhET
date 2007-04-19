package net.sourceforge.retroweaver.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Tests support for the new 1.5 enum language feature.
 */

public final class EnumTest extends AbstractTest {

	public void testConcatenation() {
		String s = "";
		for (LetterEnum t : LetterEnum.values()) {
			s = s + t + ':';
		}
		assertEquals("values", s, "A:B:C:");
	}

	public void testValueOf() {
		LetterEnum et = Enum.valueOf(LetterEnum.class, "A");
		assertSame("valueOf", et, LetterEnum.A);

		CMYK color = Enum.valueOf(CMYK.class, "Magenta");
		assertSame("valueOf", color, CMYK.Magenta);
		assertFalse("isCyan", color.isCyan());

		// test with inner class CMYK$1
		color = Enum.valueOf(CMYK.class, "Cyan");
		assertSame("valueOf", color, CMYK.Cyan);
		assertTrue("isCyan", color.isCyan());
	}

	public void testNullValueOf() {
		try {
			Class<Color> c = null;
			Enum.valueOf(c, "B");
			fail("Should raise a NullPointerException");
		} catch (NullPointerException e) { // NOPMD by xlv
			success("null valueOf");
		}
	}

	public void testIsEnum() {		
		assertTrue("Color is enum", Color.class.isEnum());
		assertFalse("Object is not enum", Object.class.isEnum());
	}

	public void testGetEnumConstants() {
		Color v1[] = Color.values();
		Color v2[] = Color.class.getEnumConstants();
		assertArraySameItems("getEnumConstants", v1, v2);
		
		assertNull("null getEnumConstants", Object.class.getEnumConstants());
	}

	public void testInvalidValueOf() {
		try {
			Class clazz	= Object.class;
			Enum.valueOf(clazz, "foo");
			fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			success("IllegalArgumentException");			
		}
		try {
			Class clazz	= this.getClass();
			Enum.valueOf(clazz, "foo");
			fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			success("IllegalArgumentException");			
		}
	}

	public void testInvalidName() {
		try {
			// Should fail with IllegalArgumentException, because
			// D isn't part of the enum.
			Enum.valueOf(LetterEnum.class, "D");

			fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			success("testInvalidName");
		}
	}

	public void testNullClass() {
		try {
			Class<LetterEnum> clazz = null;
			Enum.valueOf(clazz, "A");

			fail("Should raise a NullPointerException");
		} catch (NullPointerException e) { // NOPMD by xlv
			success("testNullClass");
		}
	}

	public void testNullName() {
		try {
			Enum.valueOf(LetterEnum.class, null);

			fail("Should raise an NullPointerException");
		} catch (NullPointerException e) { // NOPMD by xlv
			success("testNullName");
		}
	}

	public void testBasicSerializable() throws Exception {
		// Store "Red" object
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bytes);

		EnumExample ex = new EnumExample(Color.Red);

		os.writeObject(ex);
		os.close();

		// read "Red" from stream
		EnumExample resolved = (EnumExample) new ObjectInputStream(
				new ByteArrayInputStream(bytes.toByteArray())).readObject();

		// test retrieved object
		assertSame("serialization", resolved.color, Color.Red);
	}

	public void testSerializableVersion() throws Exception {
		// test retrieved object from different LetterEnum version

		// check that A can be retrieved
		ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("data/LetterEnum_A.ser"));
		LetterEnum l = (LetterEnum) ois.readObject();
		ois.close();
		
		assertSame("serialization LetterEnum_A.ser", l, LetterEnum.A);

		// check that D raises an InvalidObjectException
		try {
	        ois = new ObjectInputStream(getClass().getResourceAsStream("data/LetterEnum_D.ser"));
			ois.readObject();
			ois.close();
			
			fail("serialization LetterEnum_D.ser: should raise InvalidObjectException");
		} catch (InvalidObjectException ioe) {
			success("serialization LetterEnum_D.ser");
		}
	}
	
	public void testInnerClassSerialization() throws Exception {
		// test enum inner class serialization
		ByteArrayOutputStream bytes;
		ObjectOutputStream os;

		CMYK color;
		color = CMYK.Magenta;
		bytes = new ByteArrayOutputStream();
		os = new ObjectOutputStream(bytes);
		os.writeObject(color);
		os.close();

		color = (CMYK) new ObjectInputStream(
				new ByteArrayInputStream(bytes.toByteArray())).readObject();

		// test retrieved object
		assertSame("serialization", color, CMYK.Magenta);
		assertFalse("isCyan", color.isCyan());

		color = CMYK.Cyan;
		bytes = new ByteArrayOutputStream();
		os = new ObjectOutputStream(bytes);
		os.writeObject(color);
		os.close();

		color = (CMYK) new ObjectInputStream(
				new ByteArrayInputStream(bytes.toByteArray())).readObject();

		// test retrieved object
		assertSame("serialization", color, CMYK.Cyan);
		assertTrue("isCyan", color.isCyan());
	}

	public void testDeclaringClass() {
		assertEquals("Color", Color.Red.getDeclaringClass().getName(),
				"net.sourceforge.retroweaver.tests.Color");
		assertEquals("CMYK", CMYK.Cyan.getDeclaringClass().getName(),
				"net.sourceforge.retroweaver.tests.CMYK");
		assertEquals("declaringClass", LetterEnum.A.getDeclaringClass()
				.getName(), "net.sourceforge.retroweaver.tests.LetterEnum");
	}

	public void testCompareTo() {
		LetterEnum l1 = LetterEnum.A;
		LetterEnum l2 = LetterEnum.B;
		LetterEnum l3 = LetterEnum.C;
		LetterEnum l4 = LetterEnum.C;

		assertTrue("compareTo negative", l1.compareTo(l2) < 0);
		assertEquals("compareTo null", l3.compareTo(l4), 0);
		assertTrue("compareTo positive", l3.compareTo(l2) > 0);

		try {
			Comparable c1 = l1;
			Comparable c2 = Color.Red;
			c1.compareTo(c2);
			fail("Should raise a ClassCastException");
		} catch (ClassCastException e) {
			success("compareTo ClassCastException");
		}
	}

	public void testFields() {
		LetterEnum e = LetterEnum.A;

		assertEquals("name", e.name(), "A");
		assertEquals("ordinal", e.ordinal(), 0);
	}

	public void testMethodCalls() {
		CMYK c1 = CMYK.Cyan;
		assertTrue("Cyan", c1.isCyan());
		CMYK c2 = CMYK.Magenta;
		assertFalse("Magenta", c2.isCyan());
	}

	public void testClassLoader() {
		try {
			String className = "net.sourceforge.retroweaver.tests.Number";
			String fieldName = "Two";

			ClassLoader cl = EnumTest.class.getClassLoader();
			
			Class enumClass = Enum.class;
			Class numberClass = cl.loadClass(className);

			Class[] paramTypes = { Class.class, String.class };
			Object[] args = { numberClass, fieldName };
			Method m = enumClass.getMethod("valueOf", paramTypes);
			Enum two = (Enum)m.invoke(null, args);
			assertEquals("testClassLoader valueOf", two.name(), fieldName);

			Field field = numberClass.getField(fieldName);
			two = (Enum) field.get(null);
			assertEquals("testClassLoader getField", two.name(), fieldName);

		} catch (Exception e) {
			fail("testClassLoader exception: " + e.getMessage());
		}
	}

	public void testCoverage() {
		assertNotSame("hashcode", Color.Red.hashCode(), Color.Black.hashCode());

		EmptyEnum values[] = EmptyEnum.values();
		assertEquals("emty values", values.length, 0);

		// coverage of "equals(Object): boolean"
		LetterEnum et = Enum.valueOf(LetterEnum.class, "A");
		assertEquals("valueOf", et, LetterEnum.A);

		try {
			Enum.valueOf(EmptyEnum.class, "foo");
			fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			success("missing values array in Enum_");
		}

		Class c = CMYK.Cyan.getDeclaringClass();
		assertNotNull("getDeclaringClass", c);

		try {
			CMYK.Cyan.testClone();
			fail("Should raise a CloneNotSupportedException");
		} catch (CloneNotSupportedException e) {
			success("clone");
		}

		LetterEnum l = LetterEnum.A;
		assertFalse("equals", l.equals(null)); // NOPMD by xlv
		assertFalse("equals", l.equals(Color.Red));
		
		Comparable c1 = LetterEnum.A;
		Comparable c2 = LetterEnum.A;
		assertEquals("compareTo", c1.compareTo(c2), 0);
	}

	public static void main(String[] args) {
		/*
		 * Serialize "A" and "D" into LetterEnum_A.set and LetterEnum_D.ser
		 * 
		 * Before running this code, uncomment "D" in LetterEnum so that a
		 * missing enum value can be tested in readResolve().
		 */
		LetterEnum l1 = LetterEnum.A;
		LetterEnum l2 = null;
		try {
			l2 = LetterEnum.valueOf("D");
		} catch (IllegalArgumentException iae) {
			System.err.println("Uncomment D in LetterEnum to generate the correct files."); // NOPMD by xlv
			System.exit(1);
		}

		try {
			FileOutputStream fos = new FileOutputStream("LetterEnum_A.ser");
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(l1);
			oos.close();

			fos = new FileOutputStream("LetterEnum_D.ser");
	        oos = new ObjectOutputStream(fos);
			oos.writeObject(l2);
			oos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

}

enum LetterEnum {
	A, B, C
	//,D	// uncomment only when generating .ser files
}

enum Color {
	Black, Red, Green, Blue, White;
}

enum CMYK {
	Cyan {
		public boolean isCyan() {
			return true;
		}
	},
	Magenta, Yellow, Black;

	public boolean isCyan() {
		return false;
	}

	public CMYK testClone() throws CloneNotSupportedException {
		return (CMYK) (this.clone());
	}
}

enum Number {
	One, Two
}

enum EmptyEnum {
}

class EnumExample implements Serializable {

	protected Color color;

	EnumExample(Color c) {
		color = c;
	}

}
