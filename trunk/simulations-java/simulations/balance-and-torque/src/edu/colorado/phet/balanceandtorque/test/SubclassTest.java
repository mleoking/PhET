// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.test;

//REVIEW Consider deleting this, I usually keep only the tests that have some long-term value.

/**
 * Created to test a question that I had about subclassing and overrides.  Not
 * specific to the enclosing sim.
 *
 * @author John Blanco
 */
public class SubclassTest {
    private static class A {
        public void test() {
            System.out.println( "Invoked in class A" );
        }
    }

    private static class B extends A {
        @Override public void test() {
            System.out.println( "Invoked in class B" );
        }
    }

    public static void main( String[] args ) {
        A testClass = new B();
        testClass.test();
    }
}
