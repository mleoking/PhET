/*
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.view.PhetFrame;
//import edu.colorado.phet.idealgas.graphics.IdealGasAboutDialog;
import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.idealgas.physics.collision.CollisionGod;

import javax.swing.*;

/**
 *
 */
public class IdealGasApplication extends PhetApplication {

//    private Class currentGasSpecies = HeavySpecies.class;
//    private boolean cmLinesOn;
//    private PressureSensingBox box;
//    private CollisionGod collisionGod;


//    public void clear() {
////        super.clear();
//        IdealGasSystem.instance().clear();
//        getIdealGasSystem().setGravity( null );
//
//        // Here, we need to be able to tell the current module to clear(). If the current
//        // module has a box with a door, the module should then close it.
//        ( (BaseIdealGasApparatusPanel)getPhetMainPanel().getApparatusPanel() ).closeDoor();
//    }

    public IdealGasApplication( ApplicationModel appModel ) {
        super( appModel );
//        super( new IdealGasSystem() );
//        init();

        // Start the application running
//        this.run();
    }

    /**
     *
     */
//    public void init() {
//
//        // This work should be done by the module
//        XXXXX
//
////        super.init();
//
//        // Add a pressure box
//        float xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
//        float yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
//        float xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
//        float yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
//        box = new PressureSensingBox( new Vector2D.Double( xOrigin, yOrigin ),
//                                      new Vector2D.Double( xDiag, yDiag ) );
//        this.addBox( box, 1 );
//
//        // Add a collision collisionGod
//        collisionGod = new CollisionGod( new Rectangle2D.Double( 0, 0,
//                                                                 600,
//                                                                 600 ),
//                                                                                10, 10 );
////                                         20, 20 );
//        this.addLaw( collisionGod );
//        //                                       12, 4 ) );
//        //        this.addLaw( CollisionLaw.instance() );
//
//        // Set up collision classes
//        new SphereHotAirBalloonContactDetector();
//        new SphereSphereContactDetector();
//        new SphereWallContactDetector();
//        new SphereBoxContactDetector();
//
//        BalloonSphereCollision.register();
//        SphereSphereCollision.register();
//        SphereWallCollision.register();
//        SphereBoxCollision.register();
//
//        // Set the default clock
////        this.setClockParams( 0.1f, 50, 0.0f );
//    }

    /**
     *
     */
//    public GraphicFactory getGraphicFactory() {
//        return IdealGasGraphicFactory.instance();
//    }

    /**
     *
     */
//    protected PhetMainPanel createMainPanel() {
//        return new IdealGasMainPanel( this );
//    }

    /**
     *
     */
//    public IdealGasSystem getIdealGasSystem() {
//        return IdealGasSystem.instance();
////        return (IdealGasSystem)getPhysicalSystem();
//    }

    /**
     *
     */

//    // Move to module
//    public void addBox( Box2D box, int level ) {
//        this.getIdealGasSystem().addBox( box );
//        this.addBody( box, level );
//    }
//
//    /**
//     *
//     */
//    public void setGravity( Gravity gravity ) {
//        getIdealGasSystem().setGravity( gravity );
//        ( (IdealGasMonitorPanel)getPhetMainPanel().getMonitorPanel() ).setGravity( gravity );
//    }
//
//    /**
//     *
//     */
//    public void setGravity( double amt ) {
//        if( getIdealGasMainPanel() != null ) {
//            ( (IdealGasControlPanel)getIdealGasMainPanel().getControlPanel() ).
//                    setGravity( amt );
//        }
//    }
//
//    /**
//     *
//     */
//    public void setGravityEnabled( boolean enabled ) {
//        if( getIdealGasMainPanel() != null ) {
//            ( (IdealGasControlPanel)getIdealGasMainPanel().getControlPanel() ).
//                    setGravityEnabled( enabled );
//        }
//    }
//
//    /**
//     * Sets the species of gas that will be used when gas is introducted into the system
//     */
//    public void setCurrentSpecies( Class gasSpecies ) {
//
//        // Sanity check on the parameter
//        if( !GasMolecule.class.isAssignableFrom( gasSpecies ) ) {
//            throw new RuntimeException( "Parameter of incompatible type. Required: " + GasMolecule.class
//                                        + "  Received: " + gasSpecies );
//        }
//        currentGasSpecies = gasSpecies;
//    }
//
//    /**
//     *
//     */
//    public Class getCurrentGasSpecies() {
//        return currentGasSpecies;
//    }
//
//    /**
//     * Sets an attribute to tell whether to display CM lines
//     */
//    public void setCmLinesOn( boolean b ) {
//        cmLinesOn = b;
//    }
//
//    /**
//     *
//     */
//    public boolean isCmLinesOn() {
//        return cmLinesOn;
//    }
//
//    /**
//     *
//     */
//    public void setStove( int value ) {
//        this.getIdealGasMainPanel().setStove( value );
//        this.getIdealGasSystem().setHeatSource( (float)value );
//    }
//
//    /**
//     *
//     */
//    public void toggleSounds() {
//
//    }
//
//    /**
//     * @return
//     */
//    private IdealGasMainPanel getIdealGasMainPanel() {
//        return (IdealGasMainPanel)getApplicationView().getBasicPhetPanel();
////        return (IdealGasMainPanel)getPhetMainPanel();
//    }

//    protected JMenu createControlsMenu( PhetFrame phetFrame ) {
//        return new IdealGasControlsMenu( phetFrame, this );
//    }
//
//    protected JMenu createTestMenu() {
//        return new IdealGasTestMenu( this );
//    }

//    protected PhetAboutDialog getAboutDialog( PhetFrame phetFrame ) {
//        return new IdealGasAboutDialog( phetFrame );
//    }

    protected IdealGasConfig getConfig() {
//    protected Config getConfig() {
//    protected Config getConfig() {
        return new IdealGasConfig();
    }

//    private boolean pressureSliceEnabled = false;
//
//    public void togglePressureSlice() {
//        pressureSliceEnabled = !pressureSliceEnabled;
//        setPressureSliceEnabled( pressureSliceEnabled );
//    }
//
//    PressureSlice pressureSlice;
//    PressureSliceGraphic pressureSliceGraphic;
//
//    public void setPressureSliceEnabled( boolean pressureSliceEnabled ) {
//        if( pressureSlice == null ) {
//            pressureSlice = new PressureSlice( box );
//        }
//        this.pressureSliceEnabled = pressureSliceEnabled;
//        if( pressureSliceEnabled ) {
//            PhysicalSystem.instance().addPrepCmd( new AddBodyCmd( pressureSlice ) );
//            pressureSliceGraphic = new PressureSliceGraphic( pressureSlice,
//                                                             box );
//            getPhetMainPanel().getApparatusPanel().addGraphic( pressureSliceGraphic, 20 );
//        }
//        else {
//            getPhetMainPanel().getApparatusPanel().removeBody( pressureSlice );
//            PhysicalSystem.instance().addPrepCmd( new RemoveBodyCmd( pressureSlice ) );
//        }
//    }
//
//    private MovableImageGraphic rulerGraphic;
//
//    public void setRulerEnabed( boolean rulerEnabled ) {
//        if( rulerGraphic == null ) {
//            ResourceLoader loader = new ResourceLoader();
//            Image rulerImage = loader.loadImage( "images/meter-stick.gif" ).getImage();
//            rulerGraphic = new MovableImageGraphic( rulerImage,
//                                                    100f, 500f, 0f, 0f, 600f, 600f );
//        }
//        if( rulerEnabled ) {
//            getPhetMainPanel().getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
//        }
//        else {
//            getPhetMainPanel().getApparatusPanel().removeGraphic( rulerGraphic );
//        }
//    }

    //
    // Static fields and methods
    //
    public static void main( String[] args ) {


        ApplicationModel appModel = new ApplicationModel( IdealGasConfig.TITLE,
                                                          IdealGasConfig.DESCRIPTION,
                                                          "2.0" );

        // ??? is the first param in seconds or milliseconds?                                                          IdealGasConfig.VERSION );
        AbstractClock clock = new ThreadedClock( 0.1, 50, true );
        appModel.setClock( clock );

        IdealGasApplication application = new IdealGasApplication( appModel );
        application.startApplication();

//        application.start();
//        application.getPhetFrame().setVisible( true );
//        application.getPhetFrame().setSize( application.getPhetFrame().getSize() );
    }

//    public CollisionGod getCollisionGod() {
//        return collisionGod;
//    }
}
