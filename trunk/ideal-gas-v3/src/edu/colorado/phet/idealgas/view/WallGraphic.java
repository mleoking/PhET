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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

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
    private boolean isResizable = false;
    private boolean isResizingEast = false;
    private boolean isResizingWest = false;
    private boolean isResizingNorth = false;
    private boolean isResizingSouth = false;
    private double hotSpotRadius = 4;
    private int strokeWidth = 1;
    private Paint normalBorderPaint;

    /**
     * @param wall
     * @param component
     * @param fill
     * @param borderPaint
     * @param translationDirection
     */
    public WallGraphic( Wall wall, Component component, Paint fill, Paint borderPaint,
                        int translationDirection ) {
        this( wall, component, fill, translationDirection );
        setStroke( new BasicStroke( strokeWidth ) );
        setBorderPaint( borderPaint );
        normalBorderPaint = borderPaint;
    }

    /**
     * @param wall
     * @param component
     * @param fill
     * @param translationDirection
     */
    public WallGraphic( final Wall wall, Component component, Paint fill,
                        int translationDirection ) {
        super( component, wall.getBounds(), fill );
        this.wall = wall;
        wall.addChangeListener( this );

        // Add a listener for resize events
        addTranslationListener( new Resizer() );

        // Add mouseable behavior
        if( translationDirection == EAST_WEST ) {
            addTranslationListener( new EastWestTranslator( wall ) );
        }
        if( translationDirection == NORTH_SOUTH ) {
            addTranslationListener( new NorthSouthTranslator( wall ) );
        }
        if( translationDirection == ALL ) {
            addTranslationListener( new NorthSouthTranslator( wall ) );
            addTranslationListener( new EastWestTranslator( wall ) );
        }

        // Add a listener that will manage the type of cursor shown
        component.addMouseMotionListener( new CursorManager() );

        // Add a listener that will detect if the user wants to resize the wall
        component.addMouseListener( new ResizingDetector( wall ) );
    }

    /**
     * Sets the wall to be resizable in a specified direction.
     *
     * @param isResizable
     */
    public void setIsResizable( boolean isResizable ) {
        this.isResizable = isResizable;
    }

    private void setHighlightWall( boolean highlightWall ) {
        if( highlightWall ) {
            setBorderPaint( Color.red );
        }
        else {
            setBorderPaint( normalBorderPaint );
        }
    }

    /**
     * For debugging
     *
     * @param g2
     */
    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
//        GraphicsUtil.setAlpha( g2, 0.5 );
        super.paint( g2 );
        restoreGraphicsState();
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------
    boolean testFlag = false;

    public void wallChanged( Wall.ChangeEvent event ) {
        Wall wall = event.getWall();
        setShape( new Rectangle( (int)wall.getBounds().getX(), (int)wall.getBounds().getY(),
                                 (int)wall.getBounds().getWidth(), (int)wall.getBounds().getHeight() ) );
        testFlag = true;
        setBoundsDirty();
        repaint();
    }

    protected Rectangle determineBounds() {
        return super.determineBounds();
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
            if( !( isResizingNorth || isResizingSouth || isResizingEast || isResizingWest ) ) {
                translatable.translate( translationEvent.getDx(), 0 );
            }
        }
    }

    private class NorthSouthTranslator implements TranslationListener {
        private Translatable translatable;

        public NorthSouthTranslator( Translatable translatable ) {
            this.translatable = translatable;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            if( !( isResizingNorth || isResizingSouth || isResizingEast || isResizingWest ) ) {
                translatable.translate( 0, translationEvent.getDy() );
            }
        }
    }

    /**
     * Resizes the wall
     */
    private class Resizer implements TranslationListener {

        public void translationOccurred( TranslationEvent translationEvent ) {
            if( isResizable /* && translationEvent.getMouseEvent().isControlDown()*/ ) {
                double minX = wall.getBounds().getMinX();
                double maxX = wall.getBounds().getMaxX();
                double minY = wall.getBounds().getMinY();
                double maxY = wall.getBounds().getMaxY();
                Point mouseLoc = translationEvent.getMouseEvent().getPoint();

                if( isResizingNorth ) {
                    minY = mouseLoc.y;
                }
                if( isResizingSouth ) {
                    maxY = mouseLoc.y;
                }
                if( isResizingWest ) {
                    minX = mouseLoc.x;
                }
                if( isResizingEast ) {
                    maxX = mouseLoc.x;
                }

                wall.setBounds( new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY ) );
            }
        }
    }

    /**
     * Detects that the user wishes to resize the wall.
     * <p/>
     * Sets the cursor and an internal flag
     */
    private class ResizingDetector extends MouseAdapter {
        private final Wall wall;

        public ResizingDetector( Wall wall ) {
            this.wall = wall;
        }

        public void mousePressed( MouseEvent e ) {
            if( isResizable ) {
                double minX = wall.getBounds().getMinX();
                double maxX = wall.getBounds().getMaxX();
                double minY = wall.getBounds().getMinY();
                double maxY = wall.getBounds().getMaxY();
                Point mouseLoc = e.getPoint();

                if( Math.abs( mouseLoc.y - minY ) <= hotSpotRadius ) {
                    isResizingNorth = true;
                }
                else if( Math.abs( mouseLoc.y - maxY ) <= hotSpotRadius ) {
                    isResizingSouth = true;
                }
                else if( Math.abs( mouseLoc.x - minX ) <= hotSpotRadius ) {
                    isResizingWest = true;
                }
                else if( Math.abs( mouseLoc.x - maxX ) <= hotSpotRadius ) {
                    isResizingEast = true;
                }
            }
        }

        /**
         * Clear all resizing flags when the mouse is released, and reset the cursor
         *
         * @param e
         */
        public void mouseReleased( MouseEvent e ) {
            isResizingEast = false;
            isResizingWest = false;
            isResizingNorth = false;
            isResizingSouth = false;
        }
    }

    /**
     * Sets the cursor to the proper one depending on where the mouse is
     */
    private class CursorManager implements MouseMotionListener {
        private Cursor currentCursor = Cursor.getDefaultCursor();
        private Cursor origCursor = null;

        public void mouseDragged( MouseEvent e ) {
            // noop
        }

        public void mouseMoved( MouseEvent e ) {
            Point mouseLoc = e.getPoint();
            if( contains( mouseLoc.x, mouseLoc.y ) ) {

                // If the mouse jsut entered the wall, save the cursor so we can restore it later
                if( origCursor == null ) {
                    origCursor = getComponent().getCursor();
                }

                // Highlight the wall when it's painted
                setHighlightWall( true );

                // If the wall is resizable and the cursor is on or near its border, give it the
                // correct double-arrow cursor. Otherwise, make the cursor a hand.
                if( isResizable ) {
                    double minX = getBounds().getMinX();
                    double maxX = getBounds().getMaxX();
                    double minY = getBounds().getMinY();
                    double maxY = getBounds().getMaxY();

                    if( Math.abs( mouseLoc.y - minY ) <= hotSpotRadius ) {
                        currentCursor = Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR );
                    }
                    else if( Math.abs( mouseLoc.y - maxY ) <= hotSpotRadius ) {
                        currentCursor = Cursor.getPredefinedCursor( Cursor.S_RESIZE_CURSOR );
                    }
                    else if( Math.abs( mouseLoc.x - minX ) <= hotSpotRadius ) {
                        currentCursor = Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR );
                    }
                    else if( Math.abs( mouseLoc.x - maxX ) <= hotSpotRadius ) {
                        currentCursor = Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR );
                    }
                    else {
                        currentCursor = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
                    }
                }
                else {
                    currentCursor = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
                }
                getComponent().setCursor( currentCursor );
            } // if( contains( mouseLoc.x, mouseLoc.y ) ) {

            // If the mouse isn't in the bounds of the wall, don't highlight the wall, and
            // make sure the cursor is correct
            else {
                if( origCursor != null ) {
                    getComponent().setCursor( origCursor );
                    origCursor = null;
                }
                setHighlightWall( false );
            }
        }
    }
}
