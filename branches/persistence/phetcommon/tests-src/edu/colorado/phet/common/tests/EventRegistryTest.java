/**
 * Class: EventRegistryTest
 * Package: edu.colorado.phet.common.tests
 * Original Author: Ron LeMaster
 * Creation Date: Dec 12, 2004
 * Creation Time: 11:27:51 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.util.EventRegistry;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Tests to see if EventRegistry can be used with anonymous inner classes
 */
public class EventRegistryTest {

    static interface FooListener extends EventListener {
        void fooHappened( FooEvent event );
    }

    static class FooListenerImpl implements FooListener {
        public void fooHappened( FooEvent event ) {
            System.out.println( "$$$" );
        }
    }

    static class FooEvent extends EventObject {
        public FooEvent( Object source ) {
            super( source );
        }
    }

    static class Foo {
        EventRegistry eventResigstry = new EventRegistry();

        void addListener( EventListener l ) {
            eventResigstry.addListener( l );
        }

        void fireEvent() {
            eventResigstry.fireEvent( new FooEvent( this ) );
        }
    }

    public static void main( String[] args ) {
        Foo foo = new Foo();

        //Tests to see if EventRegistry can be used with anonymous inner classes
        foo.addListener( new FooListener() {
            public void fooHappened( FooEvent event ) {
                System.out.println( "!!!" );
            }
        } );

        // Test a top-level listener implementations
        foo.addListener( new FooListenerImpl() );

        foo.fireEvent();
    }
}
