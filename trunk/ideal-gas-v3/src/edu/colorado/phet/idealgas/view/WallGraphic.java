/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.util.Translatable;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * WallGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class WallGraphic extends PhetShapeGraphic implements Wall.ChangeListener {
    public static final int EAST_WEST = 1, NORTH_SOUTH = 2;
    private Wall wall;
    private int translationDirection;
    private double min;
    private double max;


    public WallGraphic( Wall wall, Component component, Paint fill, Paint borderPaint,
                        int translationDirection, double min, double max ) {
        this( wall, component, fill, translationDirection, min, max );
        setStroke( new BasicStroke( 1f ));
        setBorderPaint( borderPaint );
    }
    
    public WallGraphic( Wall wall, Component component, Paint fill,
                    int translationDirection, double min, double max ) {
        super( component, wall.getBounds(), fill );
        this.wall = wall;
        wall.addChangeListener( this );
        this.translationDirection = translationDirection;
        this.min = min;
        this.max = max;

        // Add mouseable behavior
        // Add listeners
        if( translationDirection == EAST_WEST ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
            addTranslationListener( new EastWestTranslator( wall ) );
        }
        if( translationDirection == NORTH_SOUTH ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
            addTranslationListener( new NorthSouthTranslator( wall ) );
        }
    }

    public void wallChanged( Wall.ChangeEvent event ) {
        setBounds( new Rectangle( (int)wall.getBounds().getX(), (int)wall.getBounds().getY(),
                                  (int)wall.getBounds().getWidth(), (int)wall.getBounds().getHeight() ) );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // Translation listeners
    //----------------------------------------------------------------

    private class EastWestTranslator implements TranslationListener {
        private Translatable translatable;

        public EastWestTranslator( Translatable translatable ) {
            this.translatable = translatable;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            double dx = translationEvent.getDx();
            // Keep the wall from moving too far to the left
            if( wall.getBounds().getMinX() + dx < min ) {
                dx = min - wall.getBounds().getMinX();
            }
            // Keep the wall from moving too far to the right
            if( wall.getBounds().getMaxX() + dx > max ) {
                dx = max - wall.getBounds().getMaxX();
            }
            translatable.translate( dx, 0 );
        }
    }

    private class NorthSouthTranslator implements TranslationListener {
        private Translatable translatable;

        public NorthSouthTranslator( Translatable translatable ) {
            this.translatable = translatable;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            double dy = translationEvent.getDy();
            // Keep the wall from moving too far up
            if( wall.getBounds().getMinY() + dy < min ) {
                dy = min - wall.getBounds().getMinY();
            }
            // Keep the wall from going too far down
            if( wall.getBounds().getMaxY() + dy > max ) {
                dy = max - wall.getBounds().getMaxY();
            }
            translatable.translate( 0, dy );
        }
    }
}
