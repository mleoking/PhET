// BaseIdealGasApparatusPanel

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 8:52:24 AM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasApplication;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
public class BaseIdealGasApparatusPanel extends ApparatusPanel2 {
    //public class BaseIdealGasApparatusPanel extends ApparatusPanel {

    // Coordinates of the intake port on the box
    private static boolean toolTipsSet = false;

    //    private PumpHandleGraphic handleGraphicImage;
    private PhetImageGraphic flamesGraphicImage;
    private PhetImageGraphic iceGraphicImage;
    protected PhetImageGraphic doorGraphicImage;


    /**
     *
     */
    public BaseIdealGasApparatusPanel( Module module, AbstractClock clock, Box2D box ) {
        super( module.getModel(), clock );
        init( module, box );

        setUseOffscreenBuffer( true );
    }

    /**
     *
     */
    public void init( final Module module, Box2D box ) {
        // Set the background color
        this.setBackground( Color.white );

        try {
            // Set up the stove, flames, and ice
            BufferedImage stoveImg = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_IMAGE_FILE );
            PhetImageGraphic stoveGraphic = new PhetImageGraphic( this, stoveImg, IdealGasConfig.X_BASE_OFFSET + IdealGasConfig.X_STOVE_OFFSET,
                                                                  IdealGasConfig.Y_BASE_OFFSET + IdealGasConfig.Y_STOVE_OFFSET );
            this.addGraphic( stoveGraphic, -4 );
            BufferedImage flamesImg = ImageLoader.loadBufferedImage( IdealGasConfig.FLAMES_IMAGE_FILE );
            flamesGraphicImage = new PhetImageGraphic( this, flamesImg, IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545 );
            this.addGraphic( flamesGraphicImage, -6 );
            BufferedImage iceImg = ImageLoader.loadBufferedImage( IdealGasConfig.ICE_IMAGE_FILE );
            iceGraphicImage = new PhetImageGraphic( this, iceImg, IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545 );
            this.addGraphic( iceGraphicImage, -6 );

            // Set up the door for the box
            BoxDoorGraphic interactiveDoorGraphic = new BoxDoorGraphic( this,
                                                                        IdealGasConfig.X_BASE_OFFSET + 230, IdealGasConfig.Y_BASE_OFFSET + 227,
                                                                        IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 227,
                                                                        IdealGasConfig.X_BASE_OFFSET + 230, IdealGasConfig.Y_BASE_OFFSET + 227,
                                                                        box );
            this.addGraphic( interactiveDoorGraphic, -6 );
        }
        catch( IOException ioe ) {
            throw new RuntimeException( ioe.getMessage() );
        }
    }

    /**
     * @return
     */
    protected IdealGasApplication getIdealGasApplication() {
        return (IdealGasApplication)PhetApplication.instance();
    }

    /**
     *
     */
    protected void paintComponent( Graphics graphics ) {

        super.paintComponent( graphics );
    }

    //
    // Stove-related methods
    //
    public void setStove( int value ) {
        int baseFlameHeight = IdealGasConfig.Y_BASE_OFFSET + 545;
        int flameHeight = baseFlameHeight - value;
        int iceHeight = baseFlameHeight + value;
        flamesGraphicImage.setLocation( (int)flamesGraphicImage.getLocation().getX(),
                                        (int)Math.min( (float)flameHeight, baseFlameHeight ) );
        iceGraphicImage.setLocation( (int)iceGraphicImage.getLocation().getX(),
                                     (int)Math.min( (float)iceHeight, baseFlameHeight ) );
//        flamesGraphicImage.setPosition( (int)flamesGraphicImage.getPosition().getX(),
//                                        (int)Math.min( (float)flameHeight, baseFlameHeight ) );
//        iceGraphicImage.setPosition( (int)iceGraphicImage.getPosition().getX(),
//                                     (int)Math.min( (float)iceHeight, baseFlameHeight ) );
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
