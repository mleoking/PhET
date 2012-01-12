// Copyright 2002-2011, University of Colorado

/**
 * Class: BoxDoorGraphic
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

public class BoxDoorGraphic extends CompositePhetGraphic
        implements SimpleObserver,
                   Box2D.ChangeListener,
                   PressureSensingBox.ChangeListener,
                   IdealGasModule.ResetListener {
    private int x;
    private int y;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private PressureSensingBox box;
    private PhetImageGraphic imageGraphic;
    private PhetShapeGraphic highlightGraphic;
    private double openingMaxX;
    private Point2D[] opening = new Point2D[2];
    private PhetShapeGraphic doorShapeGraphic;
    // Used to control the rotation of the door when it blows off the box
    private double blowOffRotation = 0;
    private boolean isAnimating = false;

    /**
     * @param component
     * @param x
     * @param y
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @param box
     * @param color
     */
    public BoxDoorGraphic( Component component,
                           int x, int y, int minX, int minY, int maxX, int maxY,
                           PressureSensingBox box, Color color, IdealGasModule module ) {
        super( component );
        BufferedImage doorImg = null;
        doorImg = IdealGasResources.getImage( IdealGasConfig.DOOR_IMAGE_FILE );
        imageGraphic = new PhetImageGraphic( component, doorImg );
        this.addGraphic( imageGraphic );

        Rectangle door = new Rectangle( imageGraphic.getWidth(), 12 );
        doorShapeGraphic = new PhetShapeGraphic( component, door, color );
        this.addGraphic( doorShapeGraphic );

        highlightGraphic = new PhetShapeGraphic( component, new Rectangle( imageGraphic.getBounds() ),
                                                 new BasicStroke( 1 ), Color.red );
        highlightGraphic.setVisible( false );
        this.addGraphic( highlightGraphic );

        this.x = x;
        this.y = y;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.box = box;
        this.openingMaxX = x + imageGraphic.getBounds().getWidth();

        box.addObserver( this );
        box.addChangeListener( (Box2D.ChangeListener) this );
        box.addChangeListener( (PressureSensingBox.ChangeListener) this );

        setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
        addTranslationListener( new DoorTranslator() );

        // Listen for resets
        module.addResetListener( this );

        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                stepAnimation();
            }
        } );
    }

    public void fireMouseEntered( MouseEvent e ) {
        super.fireMouseEntered( e );
        highlightGraphic.setVisible( true );
        setBoundsDirty();
        repaint();
    }

    public void fireMouseExited( MouseEvent e ) {
        super.fireMouseExited( e );
        highlightGraphic.setVisible( false );
        setBoundsDirty();
        repaint();
    }

    public void translateDoor( double dx, double dy ) {
        minX = (int) ( box.getMinX() - imageGraphic.getBounds().getWidth() );
        // Update the position of the image on the screen
        x = (int) Math.min( maxX, Math.max( minX, x + dx ) );
        y = (int) Math.min( maxY, Math.max( minY, y + dy ) );
        imageGraphic.setLocation( x, y - (int) imageGraphic.getBounds().getHeight() );

        // Update the box's openinng
        opening[0] = new Point2D.Double( x + imageGraphic.getBounds().getWidth(),
                                         box.getMinY() );
        opening[1] = new Point2D.Double( openingMaxX,
                                         box.getMinY() );
        setBoundsDirty();
        repaint();
    }

    public void update() {
        if ( minY != (int) box.getMinY() || minX != (int) box.getMinX() ) {
            translateDoor( 0, 0 );
            minX = (int) box.getMinX();
            minY = (int) box.getMinY();
            maxY = (int) box.getMinY();
            // For some reason, -1 is needed here to line this up properly with the box
            imageGraphic.setLocation( (int) imageGraphic.getBounds().getMinX(),
                                      minY - (int) imageGraphic.getBounds().getHeight() - 1 );

            doorShapeGraphic.setLocation( (int) imageGraphic.getLocation().getX(),
                                          (int) imageGraphic.getLocation().getY() + 13 );
            highlightGraphic.setLocation( imageGraphic.getLocation() );
            setBoundsDirty();
            repaint();
        }
    }

    protected PhetGraphic getHandler( Point p ) {
        if ( isVisible() && imageGraphic.contains( p.x, p.y ) ) {
            return this;
        }
        else {
            return null;
        }
    }

    public void setColor( Color color ) {
        doorShapeGraphic.setPaint( color );
    }

    //----------------------------------------------------------------
    // Listener implementations
    //----------------------------------------------------------------

    /**
     * Box2D.ChangeListener implementation
     *
     * @param event
     */
    public void boundsChanged( Box2D.ChangeEvent event ) {
        double newMinX = (int) ( box.getMinX() - imageGraphic.getBounds().getWidth() );
        if ( newMinX > minX ) {
            translateDoor( newMinX - minX, 0 );
        }
    }

    public void isVolumeFixedChanged( Box2D.ChangeEvent event ) {
        //noop
    }

    /**
     * IdealGasModule.ResetListener implementation
     *
     * @param event
     */
    public void resetOccurred( IdealGasModule.ResetEvent event ) {
        closeDoor();
    }

    // Moves the door to the "closed" position.
    public void closeDoor() {
        stopAnimation();
        this.setTransform( new AffineTransform() );
        this.setLocation( 0, 0 );
        translateDoor( Double.MAX_VALUE, 0 );
    }

    /*
     * PressureSensingBox.ChangeListener implementation
     * When the safe pressure is exceeded, start the animation of the lid blowing off.
     */
    public void stateChanged( PressureSensingBox.ChangeEvent event ) {
        if ( !event.getPressureSensingBox().isPressureSafe() && !isAnimating ) {
            openBox();
            startAnimation();
        }
    }

    // Opens the top of the box
    private void openBox() {
        opening[0] = new Point2D.Double( x, box.getMinY() );
        opening[1] = new Point2D.Double( openingMaxX, box.getMinY() );
        box.setOpening( opening );
    }

    // Starts animation of the lid blowing off.
    private void startAnimation() {
        isAnimating = true;
        blowOffRotation = 0;
    }

    private void stopAnimation() {
        isAnimating = false;
    }

    // Performs one step of the animation for the lid blowing off.
    private void stepAnimation() {
        if ( isAnimating ) {
            blowOffRotation -= 10;
            this.setTransform( AffineTransform.getRotateInstance( Math.toRadians( blowOffRotation ),
                                                                  doorShapeGraphic.getLocation().getX(),
                                                                  doorShapeGraphic.getLocation().getY() ) );
            this.setLocation( (int) getLocation().getX() - 1, (int) getLocation().getY() - 8 );
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class DoorTranslator implements TranslationListener {
        public void translationOccurred( TranslationEvent event ) {
            translateDoor( event.getDx(), event.getDy() );
            box.setOpening( opening );
        }

        public DoorTranslator() {
            translate( 0, 0 );
        }
    }

}
