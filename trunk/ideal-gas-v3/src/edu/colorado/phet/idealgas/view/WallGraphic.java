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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * WallGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class WallGraphic extends PhetShapeGraphic implements Wall.ChangeListener {
    public static final int ALL = 0, EAST_WEST = 1, NORTH_SOUTH = 2;
    public static final Object NORTH = new Object();
    private Wall wall;
    private int translationDirection;
    private double min;
    private double max;
    private List resizableDirections = new ArrayList();


    public WallGraphic( Wall wall, Component component, Paint fill, Paint borderPaint,
                        int translationDirection, double min, double max ) {
        this( wall, component, fill, translationDirection, min, max );
        setStroke( new BasicStroke( 1f ) );
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

        // Catch control key presses
        addTranslationListener( new Resizer() );

        // Add mouseable behavior
        if( translationDirection == EAST_WEST ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
            addTranslationListener( new EastWestTranslator( wall ) );
        }
        if( translationDirection == NORTH_SOUTH ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
            addTranslationListener( new NorthSouthTranslator( wall ) );
        }
        if( translationDirection == ALL ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
            addTranslationListener( new NorthSouthTranslator( wall ) );
            addTranslationListener( new EastWestTranslator( wall ) );
        }
    }

    public void wallChanged( Wall.ChangeEvent event ) {
        setBounds( new Rectangle( (int)wall.getBounds().getX(), (int)wall.getBounds().getY(),
                                  (int)wall.getBounds().getWidth(), (int)wall.getBounds().getHeight() ) );
        setBoundsDirty();
        repaint();
    }

    public void setResizable( Object direction ) {
        resizableDirections.add( direction );
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

            // If the control key is down, it means to resize
            if( !translationEvent.getMouseEvent().isControlDown() ) {
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
    }

    private class NorthSouthTranslator implements TranslationListener {
        private Translatable translatable;

        public NorthSouthTranslator( Translatable translatable ) {
            this.translatable = translatable;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            double dy = translationEvent.getDy();

            // If the control key is down, it means to resize
            if( !translationEvent.getMouseEvent().isControlDown() ) {
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

    private class Resizer implements TranslationListener {
        private double hotSpotRadius = 5;

        public void translationOccurred( TranslationEvent translationEvent ) {
            if( translationEvent.getMouseEvent().isControlDown() ) {
                double minX = wall.getBounds().getMinX();
                double maxX = wall.getBounds().getMaxX();
                double minY = wall.getBounds().getMinY();
                double maxY = wall.getBounds().getMaxY();

//                if( Math.abs( translationEvent.getMouseEvent().getX() - minX )
//                < hotSpotRadius ) {
                if( Math.abs( translationEvent.getMouseEvent().getX() - minX )
                    < Math.abs( translationEvent.getMouseEvent().getX() - maxX ) ) {
                    minX = translationEvent.getMouseEvent().getX();
                }
//                if( Math.abs( translationEvent.getMouseEvent().getX() - maxX ) < hotSpotRadius ) {
                else {
                    maxX = translationEvent.getMouseEvent().getX();
                }
//                if( Math.abs( translationEvent.getMouseEvent().getY() - minY )
//                    < Math.abs( translationEvent.getMouseEvent().getY() - maxY ) ) {
//                    minY = translationEvent.getMouseEvent().getY();
//                }
//                else {
//                    maxY = translationEvent.getMouseEvent().getY();
//                }
                wall.setBounds( new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY ) );
            }
        }
    }
}
