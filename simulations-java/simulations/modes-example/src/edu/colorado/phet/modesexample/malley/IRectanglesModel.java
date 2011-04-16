// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.awt.*;
import java.util.ArrayList;

/**
 * Interface implemented by all models that involve a collection of rectangles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IRectanglesModel {

    // Adds a rectangle to the model
    public void addRectangle( Rectangle rectangle );

    // Removes a rectangle to the model
    public void removeRectangle( Rectangle rectangle );

    // Returns the set of rectangles (copy of the list)
    public ArrayList<Rectangle> getRectangles();

    // Resets the model and all of its rectangles
    public void reset();

    /**
     * Base class for all models involving a collection of rectangles.
     */
    public class AbstractRectanglesModel implements IRectanglesModel {

        private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();

        public void addRectangle( Rectangle rectangle ) {
            rectangles.add( rectangle );
        }

        public void removeRectangle( Rectangle rectangle ) {
            rectangles.remove( rectangle );
        }

        public ArrayList<Rectangle> getRectangles() {
            return new ArrayList<Rectangle>( rectangles );
        }

        public void reset() {
            for ( Rectangle rectangle : rectangles ) {
                rectangle.reset();
            }
        }
    }

    /**
     * Model that has 2 rectangles.
     */
    public class TwoRectanglesModel extends AbstractRectanglesModel {
        public TwoRectanglesModel() {
            addRectangle( new Rectangle( "Red rectangle", 10, 10, 300, 150, Color.RED, Color.BLACK ) );
            addRectangle( new Rectangle( "Green rectangle", 10, 400, 400, 200, Color.GREEN, Color.BLACK ) );
        }
    }

    /**
     * Model that has 3 rectangles.
     */
    public class ThreeRectanglesModel extends AbstractRectanglesModel {
        public ThreeRectanglesModel() {
            addRectangle( new Rectangle( "Yellow rectangle", 10, 10, 100, 150, Color.YELLOW, Color.BLACK ) );
            addRectangle( new Rectangle( "Orange rectangle", 10, 200, 200, 200, Color.ORANGE, Color.BLACK ) );
            addRectangle( new Rectangle( "Blue rectangle", 10, 500, 300, 200, Color.BLUE, Color.BLACK ) );
        }
    }
}
