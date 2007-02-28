// BaseIdealGasApparatusPanel

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 8:52:24 AM
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.command.AddHelpItemCmd;
import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.graphics.util.HelpItem;
import edu.colorado.phet.graphics.*;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.*;
import edu.colorado.phet.physics.body.PhysicalEntity;
import edu.colorado.phet.idealgas.physics.body.*;

import javax.swing.event.MouseInputListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.Observable;

/**
 *
 */
public class BaseIdealGasApparatusPanel extends ApparatusPanel implements MouseInputListener {

    private MovableImageGraphic handleGraphicImage;
    private MovableImageGraphic flamesGraphicImage;
    private MovableImageGraphic iceGraphicImage;
    protected MovableImageGraphic doorGraphicImage;
    private PhetGraphic boxGraphic;

    // Array of images for the pusher animation
    private Animation pusher;
    private Image currPusherFrame;

    // Array of images for the leaner animation
    private Animation leaner;
    private Image currLeanerFrame;

    private Rectangle2D.Float pumpHotSpot;
    private Rectangle2D.Float doorHotSpot;


    /**
     *
     */
    public BaseIdealGasApparatusPanel( PhetApplication application ) {
        super( application, "Ideal Gas" );
        init( application );
    }

    protected BaseIdealGasApparatusPanel( PhetApplication application, String name ) {
        super( application, name );
        init( application );
    }

    /**
     *
     */
    public void init( PhetApplication application ) {

        // Load the pusher animation
        pusher = new Animation( IdealGasConfig.PUSHER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_PUSHER_ANIMATION_FRAMES );
        currPusherFrame = pusher.getCurrFrame();

        // Load the leaner animation
        leaner = new Animation( IdealGasConfig.LEANER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_LEANER_ANIMATION_FRAMES );
        currLeanerFrame = leaner.getCurrFrame();

        // Set up the graphics for the pump
        ResourceLoader loader = new ResourceLoader();
        Image pumpImg = loader.loadImage( IdealGasConfig.PUMP_IMAGE_FILE ).getImage();
        Image handleImg = loader.loadImage( IdealGasConfig.HANDLE_IMAGE_FILE ).getImage();
        handleGraphicImage = new PumpHandleGraphic(
                handleImg,
                IdealGasConfig.X_BASE_OFFSET + 549, IdealGasConfig.Y_BASE_OFFSET + 238,
                IdealGasConfig.X_BASE_OFFSET + 549, IdealGasConfig.Y_BASE_OFFSET + 100,
                IdealGasConfig.X_BASE_OFFSET + 549, IdealGasConfig.Y_BASE_OFFSET + 238 );
        this.addGraphic( handleGraphicImage, -6 );
        this.addGraphic( new ImageGraphic( pumpImg, IdealGasConfig.X_BASE_OFFSET + 436, IdealGasConfig.Y_BASE_OFFSET + 253 ), -4 );

        // Set up the stove, flames, and ice
        Image stoveImg = loader.loadImage( IdealGasConfig.STOVE_IMAGE_FILE ).getImage();
        this.addGraphic( new ImageGraphic( stoveImg,
                                           IdealGasConfig.X_BASE_OFFSET + 247,
                                           IdealGasConfig.Y_BASE_OFFSET + 545 ), -4 );
        Image flamesImg = loader.loadImage( IdealGasConfig.FLAMES_IMAGE_FILE ).getImage();
        flamesGraphicImage = new MovableImageGraphic(
                flamesImg,
                IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545,
                IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 500,
                IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545 );
        this.addGraphic( flamesGraphicImage, -6 );
        Image iceImg = loader.loadImage( IdealGasConfig.ICE_IMAGE_FILE ).getImage();
        iceGraphicImage = new MovableImageGraphic(
                iceImg,
                IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545,
                IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 500,
                IdealGasConfig.X_BASE_OFFSET + 260, IdealGasConfig.Y_BASE_OFFSET + 545 );
        this.addGraphic( iceGraphicImage, -6 );

        // Set up the door for the box
        Image doorImg = loader.loadImage( IdealGasConfig.DOOR_IMAGE_FILE ).getImage();
        doorGraphicImage = new BoxDoorGraphic(
                doorImg,
                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227,
                IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 227,
                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227 );
        this.addGraphic( doorGraphicImage, -6 );


        // Set the size of the panel
        this.setPreferredSize( new Dimension( 600, 520 ) );

        // Set up mouse stuff
        this.addMouseListener( this );
        this.addMouseMotionListener( this );
    }


    protected IdealGasApplication getIdealGasApplication() {
        return (IdealGasApplication)PhetApplication.instance();
    }

    /**
     *
     */
    public void clear() {
        super.clear();
        this.boxGraphic = null;
    }

    protected PhysicalSystem getPhysicalSystem() {
        return PhetApplication.instance().getPhysicalSystem();
    }

    protected IdealGasSystem getIdealGasSystem() {
        return (IdealGasSystem)getPhysicalSystem();
    }

    /**
     * Moves door to its closed position
     */
    public void closeDoor() {
        doorGraphicImage.setPosition( IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 227 );
    }

    /**
     * Get a reference to the box graphic. This is a bit of a hack. If we haven't
     * gotten it yet, we search through all the layers for it
     */
    private PhetGraphic getBoxGraphic() {

        // If we have not established the reference to the box graphic yet, then
        // search all the graphic layers for it.
        if( boxGraphic == null ) {
            boxGraphic = super.getGraphicOfType( Box2DGraphic.class );
        }
        return boxGraphic;
    }

    /**
     *
     */
    protected void paintComponent( Graphics graphics ) {

        super.paintComponent( graphics );
        Graphics2D g2 = (Graphics2D)graphics;
        Box2DGraphic box = (Box2DGraphic)this.getBoxGraphic();
        float boxLeftEdge = (float)( (Shape)box.getRep() ).getBounds2D().getMinX();
        float boxLowerEdge = (float)( (Shape)box.getRep() ).getBounds2D().getMaxY();

        // If we're doing constant pressure, paint a pusher frame
        if( getIdealGasSystem().isConstantPressure() || box.isDragging() ) {
            g2.drawImage( currPusherFrame, (int)boxLeftEdge - 107, 400 + IdealGasConfig.Y_BASE_OFFSET, this );
        }

        // If we're doing constant volume, paint a leaner frame
        if( getIdealGasSystem().isConstantVolume() && !box.isDragging() ) {
            g2.drawImage( currLeanerFrame, (int)boxLeftEdge - 107, 400 + IdealGasConfig.Y_BASE_OFFSET, this );
        }

        // Compute and draw lines for coordinates of each species' CM
        if( getIdealGasApplication().isCmLinesOn() ) {
            Vector2D heavyCm = HeavySpecies.getCm();
            Vector2D lightCm = LightSpecies.getCm();
            if( lightCm.getY() != 0 ) {
                Color oldColor = g2.getColor();
                g2.setColor( Color.red );
                g2.drawLine( (int)boxLeftEdge - 20, (int)( lightCm.getY() ),
                             (int)boxLeftEdge + 18, (int)( lightCm.getY() ) );

                g2.drawLine( (int)( lightCm.getX() ), (int)boxLowerEdge - 20,
                             (int)( lightCm.getX() ), (int)boxLowerEdge + 18 );

                g2.setColor( oldColor );
            }
            if( heavyCm.getY() != 0 ) {
                Color oldColor = g2.getColor();
                g2.setColor( Color.blue );
                g2.drawLine( (int)boxLeftEdge - 20, (int)( heavyCm.getY() ),
                             (int)boxLeftEdge + 18, (int)( heavyCm.getY() ) );

                g2.drawLine( (int)( heavyCm.getX() ), (int)boxLowerEdge - 20,
                             (int)( heavyCm.getX() ), (int)boxLowerEdge + 18 );

                g2.setColor( oldColor );
            }
        }
    }

    protected boolean isVisible( PhetGraphic graphic ) {
        PhysicalEntity body = graphic.getBody();
        if( body != null && body instanceof Particle && !( body instanceof HollowSphere ) ) {
            Particle p = (Particle)body;
            Box2D box = getIdealGasSystem().getBox();
            return ( !box.isOutsideBox( p ) || box.isInOpening( p ) );
        }
        else {
            return true;
        }
    }

    //
    // Stove-related methods
    //
    public void setStove( int value ) {
        int baseFlameHeight = IdealGasConfig.Y_BASE_OFFSET + 545;
        int flameHeight = baseFlameHeight - value;
        int iceHeight = baseFlameHeight + value;
        flamesGraphicImage.setPosition( (float)flamesGraphicImage.getLocationPoint2D().getX(), (float)flameHeight );
        iceGraphicImage.setPosition( (float)iceGraphicImage.getLocationPoint2D().getX(), (float)iceHeight );
        this.repaint();
    }

    //
    // Mouse-related methods
    //
    private boolean draggingPump = false;
    private boolean draggingDoor = false;
    private Point draggingRefPos;
    private float handleStartPointY;
    private float handleMinY;
    private boolean isInPumpHotSpot = false;
    private boolean isInDoorHotSpot = false;

    private float doorStartPointX;
    private float minDraggedY;
    private float lastYPumped;
    private float lastYTracked;
    private float doorStartPointMaxX = -1;

    public void mousePressed( MouseEvent event ) {

        // If the click is over the handle, start draggingPump
        if( isInPumpHotSpot ) {
            this.setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
            handleStartPointY = (float)handleGraphicImage.getLocationPoint2D().getY();
            draggingPump = true;
            draggingRefPos = event.getPoint();
            minDraggedY = (float)draggingRefPos.getY();
            lastYPumped = minDraggedY;
            lastYTracked = minDraggedY;
        }

        // If the click is over the door handle, start draggingPump it
        if( isInDoorHotSpot ) {
            this.setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
            doorStartPointX = (float)doorGraphicImage.getLocationPoint2D().getX();
            draggingDoor = true;
            draggingRefPos = event.getPoint();
        }
    }

    public void mouseReleased( MouseEvent event ) {

        if( draggingPump ) {
            draggingPump = false;
        }

        if( draggingDoor ) {
            draggingDoor = false;
        }
    }

    /**
     * Pump gas molecules into the box, if the mouse is down
     */
//    private ArrayList addList = new ArrayList( 100 );

    public void mouseDragged( MouseEvent event ) {

        // If we're dragging the pump handle...
        if( draggingPump && getIdealGasApplication().isRunning() ) {
            handleMinY = (float)event.getPoint().getY() < handleMinY ? (float)event.getPoint().getY() : handleMinY;
            float yDiff = (float)event.getPoint().getY() - (float)draggingRefPos.getY();
            float xCurr = (float)handleGraphicImage.getLocationPoint2D().getX();
            float yNew = handleStartPointY + yDiff;
            handleGraphicImage.setPosition( xCurr, yNew );

            // Determine if we should pump now. We do it if the mouse is moving down
            minDraggedY = yNew < minDraggedY ? yNew : minDraggedY;
            if( yNew > minDraggedY && yNew > lastYTracked && event.getPoint().getY() <= handleGraphicImage.getMaxY() ) {
                int numMolecules = (int)( yNew - lastYPumped ) / 2;
//                addList.clear();
                for( int i = 0; i < numMolecules; i++ ) {
                    this.pumpGasMolecule();
                }
                lastYPumped = yNew;
                lastYTracked = yNew;
            }
            else {
                lastYTracked = yNew;
            }
            this.repaint();
        }

        // If we're dragging the door handle...
        // TODO: All this stuff shouldn't be necessary, but without it the door doesn't
        // slide properly for any but the first apparatus panels
        if( draggingDoor ) {

            // HACK: Terrible! This should be done differently
            if( doorStartPointMaxX == -1 ) {
                doorStartPointMaxX = doorStartPointX + ( (Image)doorGraphicImage.getRep() ).getWidth( this );
            }
            float xDiff = (float)event.getPoint().getX() - (float)draggingRefPos.getX();

            float yCurr = (float)doorGraphicImage.getLocationPoint2D().getY();
            float xNew = doorStartPointX + xDiff;
            xNew = Math.max( xNew, doorGraphicImage.getMinX() );
            doorGraphicImage.setPosition( xNew, yCurr );
            this.repaint();

            // Tell the box the size and location of the opening
            setOpening( doorStartPointMaxX, xNew, yCurr );
        }
    }

    public void setOpening( float doorStartPointMaxX, float x, float y ) {
        // TODO: do something about the + 2s in here. I had to put them in to fudge the thing
        // to draw properly, but don't know why
        Vector2D[] opening = new Vector2D[2];
        opening[0] = new Vector2D( x + ( (Image)doorGraphicImage.getRep() ).getWidth( this ),
                                   y + ( (Image)doorGraphicImage.getRep() ).getHeight( this ) + 2 );
        opening[1] = new Vector2D( doorStartPointMaxX,
                                   y + ( (Image)doorGraphicImage.getRep() ).getHeight( this ) + 2 );
        getIdealGasSystem().getBox().setOpening( opening );
    }

    public void mouseClicked( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
    }

    public void mouseExited( MouseEvent event ) {
    }

    /**
     *
     * @param event
     */
    public void mouseMoved( MouseEvent event ) {

        Point mousePos = event.getPoint();

        // Determine if the cursor is in the pump handle hot spot
        getPumpHotSpot();
        isInPumpHotSpot = pumpHotSpot.contains( mousePos );

        // Determine if the cursor is in the door handle hot spot
        getDoorHotSpot();
        isInDoorHotSpot = doorHotSpot.contains( mousePos );

        // Set the cursor
        if( isInDoorHotSpot || isInPumpHotSpot ) {
            this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        else {
            this.setCursor( Cursor.getDefaultCursor() );
        }
    }

    private void getDoorHotSpot() {
        doorHotSpot = new Rectangle2D.Float(
                (float)( doorGraphicImage.getLocationPoint2D().getX() + ( (Image)doorGraphicImage.getRep() ).getWidth( this ) / 2 - 10 ),
                (float)doorGraphicImage.getLocationPoint2D().getY(),
                20, 20 );
    }

    private void getPumpHotSpot() {
        pumpHotSpot = new Rectangle2D.Float(
                (float)handleGraphicImage.getLocationPoint2D().getX(),
                (float)handleGraphicImage.getLocationPoint2D().getY(),
                ( (Image)handleGraphicImage.getRep() ).getWidth( this ),
                12 );
    }

    /**
     * Creates a gas molecule of the proper species
     */
    protected GasMolecule pumpGasMolecule() {

        // Add a new gas molecule to the system
        PumpMoleculeCmd pumpCmd = new PumpMoleculeCmd( getIdealGasApplication() );
        GasMolecule newMolecule = (GasMolecule)pumpCmd.doIt();

        // Constrain the molecule to be inside the box
        Box2D box = getIdealGasSystem().getBox();
        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule );
        newMolecule.addConstraint( constraintSpec );
        return newMolecule;
    }

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
    public void setToolTips() {

        if( !toolTipsSet ) {

            toolTipsSet = true;
            int x = 0;
            int y = 0;
            int yOffset = 225 + IdealGasConfig.Y_BASE_OFFSET;
            x = (int)handleGraphicImage.getLocationPoint2D().getX() + this.getX() + 110;
            y = (int)handleGraphicImage.getLocationPoint2D().getY() + this.getY() + 0;
            Point2D.Float p = new Point2D.Float( x - 15, y + yOffset );
            HelpItem helpText1 = new HelpItem( "You can pump gas into the box by" +
                                               "\nmoving the handle up and down", p );
            new AddHelpItemCmd( helpText1 ).doIt();

            x = (int)doorGraphicImage.getLocationPoint2D().getX() + this.getX() + 30;
            y = (int)doorGraphicImage.getLocationPoint2D().getY() + this.getY() - 40;
            p = new Point2D.Float( x, y + yOffset );
            HelpItem helpText4 = new HelpItem( "You can let gas out of the box" +
                                               "\nby sliding the door to the left", p );
            new AddHelpItemCmd( helpText4 ).doIt();

            x = (int)flamesGraphicImage.getLocationPoint2D().getX() + this.getX() + 100;
            y = (int)flamesGraphicImage.getLocationPoint2D().getY() + this.getY() + 15;
            p = new Point2D.Float( x, y + yOffset );
            HelpItem helpText3 = new HelpItem( "Heats or cools gas in the box. You can" +
                                               "\ncontrol it from the panel on the right.", p );
            new AddHelpItemCmd( helpText3 ).doIt();
        }
    }

    /**
     * Starts the application running for the specific apparatus panel
     */
    public void activate() {
        super.activate();

        // Turn off gravity
        getIdealGasApplication().setGravityEnabled( false );
        getIdealGasApplication().setGravity( 0 );

        setToolTips();
    }

    /**
     * Stops the application running for the specific apparatus panel
     */
    public void deactivate() {
        super.deactivate();
        getIdealGasApplication().clear();
    }

    public void update( Observable observable, Object o ) {
        super.update( observable, o );
    }

    //
    // Static fields and methods
    //

    // Coordinates of the intake port on the box
    private static boolean toolTipsSet = false;

}
