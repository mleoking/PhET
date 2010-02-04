package edu.colorado.phet.reactantsproductsandleftovers.test;

/**
 * Investigate basic questions related to Java typing and generics.
 * Randomly instantiates an object of a specific class using reflection.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestTyping {

    public static abstract class Widget {
        // some base class functionality goes here
    }

    public static class A extends Widget {
        // some specialized functionality goes here
    }

    public static class B extends Widget {
        // some specialized functionality goes here
    }

    public static void main( String[] args ) throws InstantiationException, IllegalAccessException {
        Class<?>[] widgets = { A.class, B.class }; // Q: how to prevent non-Widget classes from being put in this array?
        int index = (int) ( Math.random() * widgets.length );
        Widget widget = (Widget) widgets[index].newInstance(); // Q: how to eliminate this cast?
        System.out.println( "you created a widget of type " + widget.getClass().getName() );
    }
}
