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
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.Animation;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasApplication;
import edu.colorado.phet.idealgas.IdealGasConfig;
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

    private PumpHandleGraphic handleGraphicImage;
    //    private MovableImageGraphic handleGraphicImage;
    private PhetImageGraphic flamesGraphicImage;
    private PhetImageGraphic iceGraphicImage;
    protected DefaultInteractiveGraphic doorGraphicImage;
    private PhetGraphic boxGraphic;

    // Array of images for the pusher animation
    private Animation pusher;
    private Image currPusherFrame;

    // Array of images for the leaner animation
    private Animation leaner;
    private Image currLeanerFrame;

    private Vector2D[] opening = new Vector2D[2];


    /**
     *
     */
    public BaseIdealGasApparatusPanel( Module module ) {
        init( module );
    }

    protected BaseIdealGasApparatusPanel( Module module, String name ) {
        init( module );
    }

    public void repaint( int x, int y, int width, int height ) {
        super.repaint( x, y, width, height );
    }

    /**
     *
     */
    public void init( final Module module ) {

        try {
            // Load the pusher animation
            pusher = new Animation( IdealGasConfig.PUSHER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_PUSHER_ANIMATION_FRAMES );
            currPusherFrame = pusher.getCurrFrame();

            // Load the leaner animation
            leaner = new Animation( IdealGasConfig.LEANER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_LEANER_ANIMATION_FRAMES );
            currLeanerFrame = leaner.getCurrFrame();

            // Set up the graphics for the pump
            BufferedImage pumpImg = ImageLoader.loadBufferedImage( IdealGasConfig.PUMP_IMAGE_FILE );
            BufferedImage handleImg = ImageLoader.loadBufferedImage( IdealGasConfig.HANDLE_IMAGE_FILE );
            PhetImageGraphic handleGraphic = new PhetImageGraphic( this, handleImg );

            Pump pump = new Pump( (IdealGasModel)module.getModel() );
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
                                                                        IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227 );
            this.addGraphic( interactiveDoorGraphic, -6 );
            //            doorGraphicImage = new BoxDoorGraphic( doorImg,
            //                                                   IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227,
            //                                                   IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 227,
            //                                                   IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227 );
            //            this.addGraphic( doorGraphicImage, -6 );
        }
        catch( IOException ioe ) {
            throw new RuntimeException( ioe.getMessage() );
        }

        // Set the size of the panel
        this.setPreferredSize( new Dimension( 600, 520 ) );
    }


    protected IdealGasApplication getIdealGasApplication() {
        return (IdealGasApplication)PhetApplication.instance();
    }

    /**
     *
     */
    //    public void clear() {
    //        super.clear();
    //        this.boxGraphic = null;
    //    }

    //    protected  getPhysicalSystem() {
    //        return PhetApplication.instance().getPhysicalSystem();
    //    }

    //    protected IdealGasSystem getIdealGasSystem() {
    //        return (IdealGasSystem)getPhysicalSystem();
    //    }

    /**
     * Moves door to its closed position
     */
    //    public void closeDoor() {
    //        doorGraphicImage.setPosition( IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227 );
    //    }

    /**
     * Get a reference to the box graphic. This is a bit of a hack. If we haven't
     * gotten it yet, we search through all the layers for it
     */
    //    private PhetGraphic getBoxGraphic() {
    //
    //        // If we have not established the reference to the box graphic yet, then
    //        // search all the graphic layers for it.
    //        if( boxGraphic == null ) {
    //            boxGraphic = super.getGraphicOfType( Box2DGraphic.class );
    //        }
    //        return boxGraphic;
    //    }

    /**
     *
     */
    protected void paintComponent( Graphics graphics ) {

        super.paintComponent( graphics );
        //        Graphics2D g2 = (Graphics2D)graphics;
        //        Box2DGraphic box = (Box2DGraphic)this.getBoxGraphic();
        //        float boxLeftEdge = (float)( (Shape)box.getRep() ).getBounds2D().getMinX();
        //        float boxLowerEdge = (float)( (Shape)box.getRep() ).getBounds2D().getMaxY();
        //
        //        // If we're doing constant pressure, paint a pusher frame
        //        if( getIdealGasSystem().isConstantPressure() || box.isDragging() ) {
        //            g2.drawImage( currPusherFrame, (int)boxLeftEdge - 107, 400 + IdealGasConfig.Y_BASE_OFFSET, this );
        //        }
        //
        //        // If we're doing constant volume, paint a leaner frame
        //        if( getIdealGasSystem().isConstantVolume() && !box.isDragging() ) {
        //            g2.drawImage( currLeanerFrame, (int)boxLeftEdge - 107, 400 + IdealGasConfig.Y_BASE_OFFSET, this );
        //        }
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
        //        flamesGraphicImage.setPosition( (float)flamesGraphicImage.getLocationPoint2D().getX(),
        flamesGraphicImage.setPosition( (int)flamesGraphicImage.getTransform().getTranslateX(),
                                        (int)Math.min( (float)flameHeight, baseFlameHeight ) );
        //        iceGraphicImage.setPosition( (float)iceGraphicImage.getLocationPoint2D().getX(),
        iceGraphicImage.setPosition( (int)iceGraphicImage.getTransform().getTranslateX(),
                                     (int)Math.min( (float)iceHeight, baseFlameHeight ) );
        this.repaint();
    }

    //    public void setOpening( float doorStartPointMaxX, float x, float y ) {
    //        // TODO: do something about the + 2s in here. I had to put them in to fudge the thing
    //        // to draw properly, but don't know why
    //        opening[0] = new Vector2D( x + ( (Image)doorGraphicImage.getRep() ).getWidth( this ),
    //                                   y + ( (Image)doorGraphicImage.getRep() ).getHeight( this ) + 2 );
    //        opening[1] = new Vector2D( doorStartPointMaxX,
    //                                   y + ( (Image)doorGraphicImage.getRep() ).getHeight( this ) + 2 );
    //        getIdealGasSystem().getBox().setOpening( opening );
    ////        getIdealGasSystem().getBox().setOpening( opening );
    //    }

    /**
     * Creates a gas molecule of the proper species
     */
    //    protected GasMolecule pumpGasMolecule() {
    //
    //        // Add a new gas molecule to the system
    //        PumpMoleculeCmd pumpCmd = new PumpMoleculeCmd( getIdealGasApplication(),
    //                                                       this.getIdealGasSystem().getAverageMoleculeEnergy() );
    //        GasMolecule newMolecule = (GasMolecule)pumpCmd.doIt();
    //
    //        // Constrain the molecule to be inside the box
    //        Box2D box = getIdealGasSystem().getBox();
    //        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule );
    //        newMolecule.addConstraint( constraintSpec );
    //        return newMolecule;
    //    }

    /**
     *
     */
    public void movePusher( int dir ) {
        if( dir > 0 ) {
            currPusherFrame = pusher.getNextFrame();
        }
        else if( dir < 0 ) {
            currPusherFrame = pusher.getPrevFrame();
        }
    }

    /**
     *
     */
    public void moveLeaner( int dir ) {
        if( dir > 0 && leaner.getCurrFrameNum() + 1 < leaner.getNumFrames() ) {
            currLeanerFrame = leaner.getNextFrame();
        }
        else if( dir < 0 && leaner.getCurrFrameNum() > 0 ) {
            currLeanerFrame = leaner.getPrevFrame();
        }
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

    /**
     * Starts the application running for the specific apparatus panel
     */
    //    public void activate() {
    //        super.activate();
    //
    //        // Turn off gravity
    //        getIdealGasApplication().setGravityEnabled( false );
    //        getIdealGasApplication().setGravity( 0 );
    //
    //        setToolTips();
    //    }

    /**
     * Stops the application running for the specific apparatus panel
     */
    //    public void deactivate() {
    //        super.deactivate();
    //        getIdealGasApplication().clear();
    //    }

    private int cnt = 0;
    public boolean init;

    public void update( Observable observable, Object o ) {
        //        super.update( observable, o );

        // 8/2/04 to fix startup paint problem
        if( !init ) {
            this.repaint();
            init = true;
        }

        // fast paint
        if( IdealGasConfig.fastPaint ) {
            if( boxGraphic != null ) {
                //                this.repaint( ( (Rectangle2D.Float)( boxGraphic.getRep() ) ).getBounds() );
            }
        }
        else {
            //            this.repaint();
        }
    }

    //
    // Static fields and methods
    //

    // Coordinates of the intake port on the box
    private static boolean toolTipsSet = false;

}
