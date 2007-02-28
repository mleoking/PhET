/**
 * Class: BoxDoorGraphic
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

    /**
     *
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
        try {
            doorImg = ImageLoader.loadBufferedImage( IdealGasConfig.DOOR_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
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
        box.addChangeListener( (Box2D.ChangeListener)this );
        box.addChangeListener( (PressureSensingBox.ChangeListener)this );

        setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
        addTranslationListener( new DoorTranslator() );

        // Listen for resets
        module.addResetListener( this );
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
        minX = (int)( box.getMinX() - imageGraphic.getBounds().getWidth() );
        // Update the position of the image on the screen
        x = (int)Math.min( maxX, Math.max( minX, x + dx ) );
        y = (int)Math.min( maxY, Math.max( minY, y + dy ) );
        imageGraphic.setLocation( x, y - (int)imageGraphic.getBounds().getHeight() );

        // Update the box's openinng
        opening[0] = new Point2D.Double( x + imageGraphic.getBounds().getWidth(),
                                         box.getMinY() );
        opening[1] = new Point2D.Double( openingMaxX,
                                         box.getMinY() );
        setBoundsDirty();
        repaint();
    }

    public void update() {
        if( minY != (int)box.getMinY() || minX != (int)box.getMinX() ) {
            translateDoor( 0, 0 );
            minX = (int)box.getMinX();
            minY = (int)box.getMinY();
            maxY = (int)box.getMinY();
            // For some reason, -1 is needed here to line this up properly with the box
            imageGraphic.setLocation( (int)imageGraphic.getBounds().getMinX(),
                                      minY - (int)imageGraphic.getBounds().getHeight() - 1 );

            doorShapeGraphic.setLocation( (int)imageGraphic.getLocation().getX(),
                                          (int)imageGraphic.getLocation().getY() + 13 );
            highlightGraphic.setLocation( imageGraphic.getLocation() );
            setBoundsDirty();
            repaint();
        }
    }

    protected PhetGraphic getHandler( Point p ) {
        if( isVisible() && imageGraphic.contains( p.x, p.y ) ) {
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
        double newMinX = (int)( box.getMinX() - imageGraphic.getBounds().getWidth() );
        if( newMinX > minX ) {
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
        blowOffRotation = 0;
        this.setTransform( new AffineTransform());
        this.setLocation( 0,0 );
        // Close the door
        translateDoor( Double.MAX_VALUE, 0 );
    }

    /**
     * PressureSensingBox.ChangeListener implementation
     *
     * @param event
     */
    public void stateChanged( PressureSensingBox.ChangeEvent event ) {
        double pressure = event.getPressureSensingBox().getPressure();
        if( pressure > IdealGasConfig.MAX_SAFE_PRESSURE ) {
            blowOffRotation -= 10;
            this.setTransform( AffineTransform.getRotateInstance( Math.toRadians( blowOffRotation ),
                                                                  doorShapeGraphic.getLocation().getX(),
                                                                  doorShapeGraphic.getLocation().getY() ) );
            this.setLocation( (int)getLocation().getX() - 1, (int)getLocation().getY() - 8 );

            opening[0] = new Point2D.Double( x, box.getMinY() );
            opening[1] = new Point2D.Double( openingMaxX, box.getMinY() );
            box.setOpening( opening );
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
