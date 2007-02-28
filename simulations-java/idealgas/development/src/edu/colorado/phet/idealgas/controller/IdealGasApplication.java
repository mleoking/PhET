/*
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.controller.*;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.idealgas.physics.collision.SphereBoxContactDetector;
import edu.colorado.phet.idealgas.physics.collision.SphereHotAirBalloonContactDetector;
import edu.colorado.phet.idealgas.physics.collision.CollisionGod;
import edu.colorado.phet.idealgas.graphics.*;
import edu.colorado.phet.physics.*;
import edu.colorado.phet.physics.collision.*;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.graphics.GraphicFactory;
import edu.colorado.phet.graphics.MovableImageGraphic;
import edu.colorado.phet.graphics.util.GraphicsUtil;
import edu.colorado.phet.graphics.util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;

/**
 *
 */
public class IdealGasApplication extends PhetApplication {

    private Class currentGasSpecies = HeavySpecies.class;
    private boolean cmLinesOn;
    private PressureSensingBox box;


    public void clear() {
        super.clear();
        getIdealGasSystem().setGravity( null );
        ( (BaseIdealGasApparatusPanel)getPhetMainPanel().getApparatusPanel() ).closeDoor();
    }

    public IdealGasApplication() {
        super( new IdealGasSystem() );
        init();

        // Start the application running
        this.run();
    }

    /**
     *
     */
    public void init() {
        super.init();

        // Add a pressure box
        float xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        float yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        float xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        float yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new PressureSensingBox(
                new Vector2D( xOrigin, yOrigin ),
                new Vector2D( xDiag, yDiag ) );
        this.addBox( box, 1 );

        // Add a collision law
        this.addLaw( new CollisionGod( new Rectangle2D.Double( 0, 0,
                                                             600,
                                                             600 ),
                                     12, 4 ) );
//        this.addLaw( CollisionLaw.instance() );

        // Set up collision classes
        new SphereHotAirBalloonContactDetector();
        new SphereSphereContactDetector();
        new SphereWallContactDetector();
        new SphereBoxContactDetector();

        SphereSphereCollision.register();
        SphereWallCollision.register();
        SphereBoxCollision.register();

        // Set the default clock
        this.setClockParams( 0.1f, 50, 0.0f );
    }

    /**
     *
     */
    public GraphicFactory getGraphicFactory() {
        return IdealGasGraphicFactory.instance();
    }

    /**
     *
     */
    protected PhetMainPanel createMainPanel() {
        return new IdealGasMainPanel( this );
    }

    /**
     *
     */
    public IdealGasSystem getIdealGasSystem() {
        return (IdealGasSystem)getPhysicalSystem();
    }

    /**
     *
     */
    public void addBox( Box2D box, int level ) {
        this.getIdealGasSystem().addBox( box );
        this.addBody( box, level );
    }

    /**
     *
     */
    public void setGravity( Gravity gravity ) {
        getIdealGasSystem().setGravity( gravity );
        Object o = getPhetMainPanel().getMonitorPanel();
        ( (IdealGasMonitorPanel)getPhetMainPanel().getMonitorPanel() ).setGravity( gravity );
    }

    /**
     *
     */
    public void setGravity( double amt ) {
        if( getIdealGasMainPanel() != null ) {
            ( (IdealGasControlPanel)getIdealGasMainPanel().getControlPanel() ).
                    setGravity( amt );
        }
    }

    /**
     *
     */
    public void setGravityEnabled( boolean enabled ) {
        if( getIdealGasMainPanel() != null ) {
            ( (IdealGasControlPanel)getIdealGasMainPanel().getControlPanel() ).
                    setGravityEnabled( enabled );
        }
    }

    /**
     * Sets the species of gas that will be used when gas is introducted into the system
     */
    public void setCurrentSpecies( Class gasSpecies ) {

        // Sanity check on the parameter
        if( !GasMolecule.class.isAssignableFrom( gasSpecies ) ) {
            throw new RuntimeException( "Parameter of incompatible type. Required: " + GasMolecule.class
                                        + "  Received: " + gasSpecies );
        }
        currentGasSpecies = gasSpecies;
    }

    /**
     *
     */
    public Class getCurrentGasSpecies() {
        return currentGasSpecies;
    }

    /**
     * Sets an attribute to tell whether to display CM lines
     */
    public void setCmLinesOn( boolean b ) {
        cmLinesOn = b;
    }

    /**
     *
     */
    public boolean isCmLinesOn() {
        return cmLinesOn;
    }

    /**
     *
     */
    public void setStove( int value ) {
        this.getIdealGasMainPanel().setStove( value );
        this.getIdealGasSystem().setHeatSource( (float)value );
    }

    /**
     *
     */
    public void toggleSounds() {

    }

    /**
     *
     * @return
     */
    private IdealGasMainPanel getIdealGasMainPanel() {
        return (IdealGasMainPanel)getPhetMainPanel();
    }

    protected JMenu createControlsMenu( PhetFrame phetFrame ) {
        return new IdealGasControlsMenu( phetFrame, this );
    }

    protected JMenu createTestMenu() {
        return new IdealGasTestMenu( this );
    }

    protected PhetAboutDialog getAboutDialog( PhetFrame phetFrame ) {
        return new IdealGasAboutDialog( phetFrame );
    }

    protected Config getConfig() {
        return new IdealGasConfig();
    }

    private boolean pressureSliceEnabled = false;

    public void togglePressureSlice() {
        pressureSliceEnabled = !pressureSliceEnabled;
        setPressureSliceEnabled( pressureSliceEnabled );
    }

    PressureSlice pressureSlice;
    PressureSliceGraphic pressureSliceGraphic;

    public void setPressureSliceEnabled( boolean pressureSliceEnabled ) {
        if( pressureSlice == null ) {
            pressureSlice = new PressureSlice( box );
        }
        this.pressureSliceEnabled = pressureSliceEnabled;
        this.pressureSliceEnabled = pressureSliceEnabled;
        if( pressureSliceEnabled ) {
            PhysicalSystem.instance().addPrepCmd( new AddBodyCmd( pressureSlice ) );
            pressureSliceGraphic = new PressureSliceGraphic(
                    pressureSlice,
                    box );
            getPhetMainPanel().getApparatusPanel().addGraphic( pressureSliceGraphic, 20 );
        }
        else {
            getPhetMainPanel().getApparatusPanel().removeBody( pressureSlice );
            PhysicalSystem.instance().addPrepCmd( new RemoveBodyCmd( pressureSlice ) );
        }
    }

    private MovableImageGraphic rulerGraphic;

    public void setRulerEnabed( boolean rulerEnabled ) {
        if( rulerGraphic == null ) {
            ResourceLoader loader = new ResourceLoader();
            Image rulerImage = loader.loadImage( "images/meter-stick.gif" ).getImage();
            rulerGraphic = new MovableImageGraphic(
                    rulerImage,
                    100f, 500f, 0f, 0f, 600f, 600f );
        }
        if( rulerEnabled ) {
            getPhetMainPanel().getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
        }
        else {
            getPhetMainPanel().getApparatusPanel().removeGraphic( rulerGraphic );
        }
    }

    //
    // Static fields and methods
    //
    public static void main( String[] args ) {

        IdealGasApplication application = new IdealGasApplication();
        application.start();
    }
}
