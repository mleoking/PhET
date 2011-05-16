// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.test;

/**
 * @author Sam Reid
 */
public class TestFieldDispatch {
    static class Animal {
        public final String name = "Animal";
    }

    static class Dog extends Animal {
        public final String name = "Dog";
    }

    public static void main( String[] args ) {
        Animal dog = new Dog();
        System.out.println( "dog.name = " + dog.name );

        Dog ollie = new Dog();
        System.out.println( "ollie.name = " + ollie.name );

        new MyClass<Dog>().print( ollie );
    }

    public static class MyClass<T extends Animal> {
        public void print( T t ) {
            System.out.println( "my class printed t = " + t.name );
        }
    }
}

