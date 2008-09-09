package edu.colorado.phet.forces1d.common;


/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 29, 2004
 * Time: 2:17:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutUtil {
    double min;
    double max;
    double separatorWidth;

    public LayoutUtil( double min, double max, double separatorWidth ) {//separator width could be rewritten to be fractional.
        this.min = min;
        this.max = max;
        this.separatorWidth = separatorWidth;
    }

    public abstract static class LayoutElement {
        protected double size;
        protected double min;

        public double getMin() {
            return min;
        }

        public double getSize() {
            return size;
        }

        public String toString() {
            return "min=" + min + ", size=" + size;
        }
    }

    public static class Fixed extends LayoutElement {

        public Fixed( double size ) {
            this.size = size;
        }
    }

    public static class Dynamic extends LayoutElement {


        public Dynamic() {
        }

        public void set( double min, double size ) {
            this.min = min;
            this.size = size;
        }

    }

    public void layout( LayoutElement[] elements ) {
        double range = max - min;
        int numElements = elements.length;
        int numSeparators = numElements - 1;
        double sepSpace = numSeparators * separatorWidth;

        int numVariable = 0;
        double fixedSpace = 0;
        for ( int i = 0; i < elements.length; i++ ) {
            LayoutElement element = elements[i];
            if ( element instanceof Fixed ) {
                fixedSpace += element.getSize();
            }
            else {
                numVariable++;
            }
        }

        double remainingSpace = range - sepSpace - fixedSpace;
        double spacePerElement = remainingSpace / numVariable;
        double minVal = min;
        for ( int i = 0; i < elements.length; i++ ) {
            LayoutElement element = elements[i];
            if ( element instanceof Dynamic ) {
                Dynamic d = (Dynamic) element;
                d.set( minVal, spacePerElement );
                minVal += spacePerElement + separatorWidth;
            }
            else {
                Fixed f = (Fixed) element;
                f.min = minVal;
                minVal += f.size + separatorWidth;

            }
        }
    }

}
