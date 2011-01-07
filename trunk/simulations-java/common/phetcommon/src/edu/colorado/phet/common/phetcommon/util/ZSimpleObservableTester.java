// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

import junit.framework.TestCase;

public class ZSimpleObservableTester extends TestCase {
    private volatile SimpleObservable observable;

    public void setUp() {
        observable = new SimpleObservable();
    }

    public void tearDown() {
    }

    public void testControllerPropagatesNotification() {
        MockObserver mockObserver = new MockObserver();

        observable.addObserver( mockObserver );

        SimpleObserver controllingObserver = observable.getController();

        controllingObserver.update();

        assertEquals( 1, mockObserver.getUpdateCount() );
    }

    private static class MockObserver implements SimpleObserver {
        private volatile int updateCount = 0;

        public void update() {
            ++updateCount;
        }

        public int getUpdateCount() {
            return updateCount;
        }
    }
}
