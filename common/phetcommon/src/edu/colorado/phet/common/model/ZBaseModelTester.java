/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.model;

import junit.framework.TestCase;

public class ZBaseModelTester extends TestCase {
    private volatile BaseModel model;

    public void setUp() {
        model = new BaseModel();
    }

    public void tearDown() {
    }

    public void testThatSelectForSelectsProperClass() {
        TestModelElement1 element = new TestModelElement1();

        model.addModelElement( element );

        assertTrue(model.selectFor( TestModelElement1.class).contains(element));
    }

    public void testThatSelectForDoesNotSelectWrongClass() {
        TestModelElement1 element1 = new TestModelElement1();
        TestModelElement2 element2 = new TestModelElement2();

        model.addModelElement( element1 );
        model.addModelElement( element2 );

        assertFalse(model.selectFor( TestModelElement1.class).contains(element2));
        assertFalse(model.selectFor( TestModelElement2.class).contains(element1));
    }

    public void testCanSelectForInterface() {
        TestModelElement1 element1 = new TestModelElement1();
        TestModelElement2 element2 = new TestModelElement2();

        model.addModelElement( element1 );
        model.addModelElement( element2 );

        assertTrue(model.selectFor(ModelElement.class).contains(element1));
        assertTrue(model.selectFor(ModelElement.class).contains(element2));
    }

    public static class TestModelElement1 implements ModelElement {
        public void stepInTime( double dt ) {
        }
    }

    public static class TestModelElement2 implements ModelElement {
        public void stepInTime( double dt ) {
        }
    }
}
