// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

import junit.framework.TestCase;

import java.util.Stack;

public class ZDynamicListenerControllerFactoryTester extends TestCase {
    public void setUp() {
    }

    public void tearDown() {
    }

    public void testCannotCreateDynamicControllerForClass() {
        try {
            DynamicListenerControllerFactory.newController( ZDynamicListenerControllerFactoryTester.class );

            fail();
        }
        catch ( IllegalStateException e ) {
        }
    }

    public void testCanCreateDynamicControllerForInterface() {
        DynamicListenerControllerFactory.newController( TestListener.class );
    }

    public void testCanCastDynamicControllerToListenerInterface() {
        TestListener listener = (TestListener) DynamicListenerControllerFactory.newController( TestListener.class );

        assertNotNull( listener );
    }

    public void testThatControllerNotifiesListeners() {
        MockListener mockListener = new MockListener();

        DynamicListenerController c = DynamicListenerControllerFactory.newController( TestListener.class );

        c.addListener( mockListener );

        TestListener listenerController = (TestListener) c;

        listenerController.notifySumChanged( 4.0 );

        assertEquals( 4.0, mockListener.getLastNotification(), 0.00001 );
    }

    public void testThatAddingWrongListenerFails() {
        Object wrongClassMockListener = new Object();

        DynamicListenerController c = DynamicListenerControllerFactory.newController( TestListener.class );

        try {
            c.addListener( wrongClassMockListener );

            fail();
        }
        catch ( IllegalStateException e ) {
        }
    }

    public void testThatControllerObjectMethodsWork() {
        DynamicListenerController c = DynamicListenerControllerFactory.newController( TestListener.class );

        c.hashCode();
        c.toString();
        c.equals( new Object() );
    }

    public void testCanRetrieveListeners() {
        MockListener mockListener = new MockListener();

        DynamicListenerController c = DynamicListenerControllerFactory.newController( TestListener.class );

        c.addListener( mockListener );

        assertTrue( c.getAllListeners().contains( mockListener ) );
    }

    public void testCannotAddDuplicateListener() {
        MockListener mockListener = new MockListener();

        DynamicListenerController c = DynamicListenerControllerFactory.newController( TestListener.class );

        c.addListener( mockListener );
        c.addListener( mockListener );

        assertEquals( 1, c.getAllListeners().size() );
    }

    public interface TestListener {
        void notifySumChanged( double newSum );
    }

    public static class MockListener implements TestListener {
        Stack stack = new Stack();

        public void notifySumChanged( double newSum ) {
            stack.push( new Double( newSum ) );
        }

        public double getLastNotification() {
            return ( (Double) stack.pop() ).doubleValue();
        }
    }
}
