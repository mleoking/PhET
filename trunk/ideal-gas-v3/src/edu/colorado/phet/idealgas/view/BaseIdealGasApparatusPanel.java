// BaseIdealGasApparatusPanel

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 8:52:24 AM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasApplication;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.Pump;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;

/**
 *
 */
public class BaseIdealGasApparatusPanel extends ApparatusPanel {

    // Coordinates of the intake port on the box
    private static boolean toolTipsSet = false;

    private PumpHandleGraphic handleGraphicImage;
    private PhetImageGraphic flamesGraphicImage;
    private PhetImageGraphic iceGraphicImage;
    protected DefaultInteractiveGraphic doorGraphicImage;


    /**
     *
     */
    public BaseIdealGasApparatusPanel( Module module, Box2D box, Pump pump ) {
        init( module, box, pump );
    }

    protected BaseIdealGasApparatusPanel( Module module, Box2D box, Pump pump, String name ) {
        init( module, box, pump );
    }

    /**
     *
     */
    public void init( final Module module, Box2D box, Pump pump ) {

        // Set the background color
        this.setBackground( Color.white );        

        try {

            // Set up the graphics for the pump
            BufferedImage pumpImg = ImageLoader.loadBufferedImage( IdealGasConfig.PUMP_IMAGE_FILE );
            BufferedImage handleImg = ImageLoader.loadBufferedImage( IdealGasConfig.HANDLE_IMAGE_FILE );
            PhetImageGraphic handleGraphic = new PhetImageGraphic( this, handleImg );
            handleGraphicImage = new PumpHandleGraphic( pump, handleGraphic,
                                                        IdealGasConfig.X_BASE_OFFSET + 549, IdealGasConfig.Y_BASE_OFFSET + 238,
                                                        IdealGasConfig.X_BASE_OFFSET + 549, IdealGasConfig.Y_BASE_OFFSET + 100,
                                                        IdealGasConfig.X_BASE_OFFSET + 549, IdealGasConfig.Y_BASE_OFFSET + 238 );
            this.addGraphic( handleGraphicImage, -6 );
            PhetImageGraphic pumpGraphic = new PhetImageGraphic( this, pumpImg, IdealGasConfig.X_BASE_OFFSET + 436, IdealGasConfig.Y_BASE_OFFSET + 253 );
            this.addGraphic( pumpGraphic, -4 );

            // Set up the stove, flames, and ice
            BufferedImage stoveImg = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_IMAGE_FILE );
            PhetImageGraphic stoveGraphic = new PhetImageGraphic( this, stoveImg, IdealGasConfig.X_BASE_OFFSET + 247,
                                                                  IdealGasConfig.Y_BASE_OFFSET + 545 );
            this.addGraphic( stoveGraphic, -4 );
            BufferedImage flamesImg = ImageLoader.loadBufferedImage( IdealGasConfig.FLAMES_IMAGE_FILE );
            flamesGraphicImage = new PhetImageGraphic( this, flamesImg, IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545 );
            this.addGraphic( flamesGraphicImage, -6 );
            BufferedImage iceImg = ImageLoader.loadBufferedImage( IdealGasConfig.ICE_IMAGE_FILE );
            iceGraphicImage = new PhetImageGraphic( this, iceImg, IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545 );
            this.addGraphic( iceGraphicImage, -6 );

            // Set up the door for the box
            BufferedImage doorImg = ImageLoader.loadBufferedImage( IdealGasConfig.DOOR_IMAGE_FILE );
            PhetImageGraphic doorGraphic = new PhetImageGraphic( this, doorImg );
            BoxDoorGraphic interactiveDoorGraphic = new BoxDoorGraphic( doorGraphic,
                                                                        IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227,
                                                                        IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 227,
                                                                        IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227,
                                                                        box );
            this.addGraphic( interactiveDoorGraphic, -6 );
        }
        catch( IOException ioe ) {
            throw new RuntimeException( ioe.getMessage() );
        }

        // Set the size of the panel
        this.setPreferredSize( new Dimension( 600, 800 ) );
//        this.setPreferredSize( new Dimension( 600, 520 ) );
    }


    protected IdealGasApplication getIdealGasApplication() {
        return (IdealGasApplication)PhetApplication.instance();
    }

    /**
     *
     */
    protected void paintComponent( Graphics graphics ) {

        super.paintComponent( graphics );
//                Graphics2D g2 = (Graphics2D)graphics;
        //        Box2DGraphic box = (Box2DGraphic)this.getBoxGraphic();
        //        float boxLeftEdge = (float)( (Shape)box.getRep() ).getBounds2D().getMinX();
        //        float boxLowerEdge = (float)( (Shape)box.getRep() ).getBounds2D().getMaxY();
        //
//                // If we're doing constant pressure, paint a pusher frame
//                if( model.isConstantPressure() || box.isDragging() ) {
//                    g2.drawImage( currPusherFrame, (int)boxLeftEdge - 107, 400 + IdealGasConfig.Y_BASE_OFFSET, this );
//                }
//
//                // If we're doing constant volume, paint a leaner frame
//                if( model.isConstantVolume() && !box.isDragging() ) {
//                    g2.drawImage( currLeanerFrame, (int)boxLeftEdge - 107, 400 + IdealGasConfig.Y_BASE_OFFSET, this );
//                }
        //
        //        // Compute and draw lines for coordinates of each species' CM
        //        if( getIdealGasApplication().isCmLinesOn() ) {
        //            Vector2D heavyCm = HeavySpecies.getCm();
        //            Vector2D lightCm = LightSpecies.getCm();
        //            if( lightCm.getY() != 0 ) {
        //                Color oldColor = g2.getColor();
        //                g2.setColor( Color.red );
        //                g2.drawLine( (int)boxLeftEdge - 20, (int)( lightCm.getY() ),
        //                             (int)boxLeftEdge + 18, (int)( lightCm.getY() ) );
        //
        //                g2.drawLine( (int)( lightCm.getX() ), (int)boxLowerEdge - 20,
        //                             (int)( lightCm.getX() ), (int)boxLowerEdge + 18 );
        //
        //                g2.setColor( oldColor );
        //            }
        //            if( heavyCm.getY() != 0 ) {
        //                Color oldColor = g2.getColor();
        //                g2.setColor( Color.blue );
        //                g2.drawLine( (int)boxLeftEdge - 20, (int)( heavyCm.getY() ),
        //                             (int)boxLeftEdge + 18, (int)( heavyCm.getY() ) );
        //
        //                g2.drawLine( (int)( heavyCm.getX() ), (int)boxLowerEdge - 20,
        //                             (int)( heavyCm.getX() ), (int)boxLowerEdge + 18 );
        //
        //                g2.setColor( oldColor );
        //            }
        //        }
    }

    //    protected boolean isVisible( PhetGraphic graphic ) {
    //        PhysicalEntity body = graphic.getBody();
    //        if( body != null && body instanceof IdealGasParticle && !( body instanceof HollowSphere ) ) {
    //            IdealGasParticle p = (IdealGasParticle)body;
    //            Box2D box = getIdealGasSystem().getBox();
    //            return ( !box.isOutsideBox( p ) || box.isInOpening( p ) );
    //        }
    //        else {
    //            return true;
    //        }
    //    }

    //
    // Stove-related methods
    //
    public void setStove( int value ) {
        int baseFlameHeight = IdealGasConfig.Y_BASE_OFFSET + 545;
        int flameHeight = baseFlameHeight - value;
        int iceHeight = baseFlameHeight + value;
        flamesGraphicImage.setPosition( (int)flamesGraphicImage.getTransform().getTranslateX(),
                                        (int)Math.min( (float)flameHeight, baseFlameHeight ) );
        iceGraphicImage.setPosition( (int)iceGraphicImage.getTransform().getTranslateX(),
                                     (int)Math.min( (float)iceHeight, baseFlameHeight ) );
        this.repaint();
    }

    /**
     *
     */
    //    public void setToolTips() {
    //
    //        if( !toolTipsSet ) {
    //
    //            toolTipsSet = true;
    //            int x = 0;
    //            int y = 0;
    //            int yOffset = 225 + IdealGasConfig.Y_BASE_OFFSET;
    //            x = (int)handleGraphicImage.getLocationPoint2D().getX() + this.getX() + 110;
    //            y = (int)handleGraphicImage.getLocationPoint2D().getY() + this.getY() + 0;
    //            Point2D.Float p = new Point2D.Float( x - 15, y + yOffset );
    //            HelpItem helpText1 = new HelpItem( "You can pump gas into the box by" +
    //                                               "\nmoving the handle up and down", p );
    //            new AddHelpItemCmd( helpText1 ).doIt();
    //
    //            x = (int)doorGraphicImage.getLocationPoint2D().getX() + this.getX() + 30;
    //            y = (int)doorGraphicImage.getLocationPoint2D().getY() + this.getY() - 40;
    //            p = new Point2D.Float( x, y + yOffset );
    //            HelpItem helpText4 = new HelpItem( "You can let gas out of the box" +
    //                                               "\nby sliding the door to the left", p );
    //            new AddHelpItemCmd( helpText4 ).doIt();
    //
    //            x = (int)flamesGraphicImage.getLocationPoint2D().getX() + this.getX() + 100;
    //            y = (int)flamesGraphicImage.getLocationPoint2D().getY() + this.getY() + 15;
    //            p = new Point2D.Float( x, y + yOffset );
    //            HelpItem helpText3 = new HelpItem( "Heats or cools gas in the box. You can" +
    //                                               "\ncontrol it from the panel on the right.", p );
    //            new AddHelpItemCmd( helpText3 ).doIt();
    //        }
    //    }
}
