/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.model;

import junit.framework.TestCase;

import java.util.List;
import java.io.Serializable;

public class ZBaseModelTester extends TestCase {
    private volatile BaseModel model;
    private volatile ModelElement element1 = new TestModelElement1();
    private volatile ModelElement element2 = new TestModelElement2();
    private volatile ModelElement element3 = new TestModelElement3();

    public void setUp() {
        model = new BaseModel();

        element1 = new TestModelElement1();
        element2 = new TestModelElement2();
        element3 = new TestModelElement3();

        model.addModelElement( element1 );
        model.addModelElement( element2 );
        model.addModelElement( element3 );
    }

    public void tearDown() {
    }

    public void testThatSelectForSelectsProperClass() {
        assertTrue( model.selectFor( TestModelElement1.class ).contains( element1 ) );
    }

    public void testThatSelectForDoesNotSelectWrongClass() {
        assertFalse( model.selectFor( TestModelElement1.class ).contains( element2 ) );
        assertFalse( model.selectFor( TestModelElement2.class ).contains( element1 ) );
    }

    public void testCanSelectForInterface() {
        List selection = model.selectFor( ModelElement.class );

        assertTrue( selection.contains( element1 ) );
        assertTrue( selection.contains( element2 ) );
    }

    public void testCanSelectForTwoClasses() {
        List selection = model.selectFor( new Class[]{Serializable.class, TestModelElement2.class} );

        assertTrue( selection.contains( element2 ) );
    }

    public void testCanSelectForJustTwoClasses() {
        List selection = model.selectFor( new Class[]{Serializable.class, TestModelElement2.class} );

        assertFalse( selection.contains( element1 ) );
        assertFalse( selection.contains( element3 ) );
    }

    public static class TestModelElement1 implements ModelElement {
        public void stepInTime( double dt ) {
        }
    }

    public static class TestModelElement2 implements ModelElement, Serializable {
        public void stepInTime( double dt ) {
        }
    }

    public static class TestModelElement3 implements ModelElement {
        public void stepInTime( double dt ) {
        }
    }
}
