/**
 * Class: IdealGasBaseModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Jul 20, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.graphics.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.graphics.IdealGasMonitorPanel;
import edu.colorado.phet.idealgas.graphics.PressureSliceGraphic;
import edu.colorado.phet.idealgas.physics.*;
//import edu.colorado.phet.idealgas.physics.collision.BalloonSphereCollision;
import edu.colorado.phet.idealgas.physics.collision.CollisionGod;
import edu.colorado.phet.idealgas.physics.collision.SphereBoxContactDetector;
//import edu.colorado.phet.idealgas.physics.collision.SphereHotAirBalloonContactDetector;
import edu.colorado.phet.physics.collision.*;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class IdealGasBaseModule extends Module {

    private IdealGasSystem model = new IdealGasSystem();
    private Class currentGasSpecies = HeavySpecies.class;
    private boolean cmLinesOn;
    private PressureSensingBox box;
    private CollisionGod collisionGod;
    private BaseIdealGasApparatusPanel apparatusPanel;
    private IdealGasMonitorPanel monitorPanel;
    private IdealGasControlPanel controlPanel;

    protected IdealGasBaseModule( String name, BaseIdealGasApparatusPanel apparatusPanel ) {
        super( name );
        this.apparatusPanel = apparatusPanel;
        this.monitorPanel = new IdealGasMonitorPanel( );
        this.controlPanel = new IdealGasControlPanel( );
    }

    /**
     *
     */
    public void init() {

        // Add a pressure box
        float xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        float yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        float xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        float yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new PressureSensingBox( new Vector2D.Double( xOrigin, yOrigin ),
                                      new Vector2D.Double( xDiag, yDiag ) );
        this.addBox( box, 1 );

        // Add a collision collisionGod
        collisionGod = new CollisionGod( new Rectangle2D.Double( 0, 0,
                                                                 600,
                                                                 600 ),
                                                                                10, 10 );
//                                         20, 20 );
        model.addLaw( collisionGod );
//        this.addLaw( collisionGod );
        //                                       12, 4 ) );
        //        this.addLaw( CollisionLaw.instance() );

        // Set up collision classes
        new SphereHotAirBalloonContactDetector();
        new SphereSphereContactDetector();
        new SphereWallContactDetector();
        new SphereBoxContactDetector();

        BalloonSphereCollision.register();
        SphereSphereCollision.register();
        SphereWallCollision.register();
        SphereBoxCollision.register();

        // Set the default clock
//        this.setClockParams( 0.1f, 50, 0.0f );
    }


    public void clear() {
//        super.clear();
        model.clear();
        model.setGravity( null );
//        IdealGasSystem.instance().clear();
//        getIdealGasSystem().setGravity( null );

        // Here, we need to be able to tell the current module to clear(). If the current
        // module has a box with a door, the module should then close it.
        apparatusPanel.closeDoor();
//        ( (BaseIdealGasApparatusPanel)getPhetMainPanel().getApparatusPanel() ).closeDoor();
    }

    // Move to module
    public void addBox( Box2D box, int level ) {
        model.addBox( box );
        model.addBody( box, level );
//        this.getIdealGasSystem().addBox( box );
//        this.addBody( box, level );
    }

    /**
     *
     */
    public void setGravity( Gravity gravity ) {
        model.setGravity( gravity );
//        getIdealGasSystem().setGravity( gravity );
        monitorPanel.setGravity( gravity );
//        ( (IdealGasMonitorPanel)getPhetMainPanel().getMonitorPanel() ).setGravity( gravity );
    }

    /**
     *
     */
    public void setGravity( double amt ) {
        controlPanel.setGravity( amt );
//        if( getIdealGasMainPanel() != null ) {
//            ( (IdealGasControlPanel)getIdealGasMainPanel().getControlPanel() ).
//                    setGravity( amt );
//        }
    }

    /**
     *
     */
    public void setGravityEnabled( boolean enabled ) {
        controlPanel.setGravityEnabled( enabled );
//        if( getIdealGasMainPanel() != null ) {
//            ( (IdealGasControlPanel)getIdealGasMainPanel().getControlPanel() ).
//                    setGravityEnabled( enabled );
//        }
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
        apparatusPanel.setStove( value );
//        this.getIdealGasMainPanel().setStove( value );
        model.setHeatSource( (float)value );
//        this.getIdealGasSystem().setHeatSource( (float)value );
    }

    /**
     *
     */
    public void toggleSounds() {

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
        if( pressureSliceEnabled ) {
            model.addModelElement( pressureSlice );
//            PhysicalSystem.instance().addPrepCmd( new AddBodyCmd( pressureSlice ) );
            pressureSliceGraphic = new PressureSliceGraphic( pressureSlice, box, apparatusPanel);
            apparatusPanel.addGraphic( pressureSliceGraphic, 20 );
//            getPhetMainPanel().getApparatusPanel().addGraphic( pressureSliceGraphic, 20 );
        }
        else {
            apparatusPanel.removeGraphic( pressureSliceGraphic );
//            getPhetMainPanel().getApparatusPanel().removeBody( pressureSlice );
            model.removeModelElement( pressureSlice );
//            PhysicalSystem.instance().addPrepCmd( new RemoveBodyCmd( pressureSlice ) );
        }
    }

    private PhetImageGraphic rulerGraphic;
//    private MovableImageGraphic rulerGraphic;

    public void setRulerEnabed( boolean rulerEnabled ) {
        if( rulerGraphic == null ) {
            BufferedImage rulerImage = null;
            try {
                rulerImage = ImageLoader.loadBufferedImage( "images/meter-stick.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            //            ResourceLoader loader = new ResourceLoader();
//            Image rulerImage = loader.loadImage( "images/meter-stick.gif" ).getImage();
            rulerGraphic = new PhetImageGraphic( apparatusPanel, rulerImage);
//            rulerGraphic = new MovableImageGraphic( rulerImage,
//                                                    100f, 500f, 0f, 0f, 600f, 600f );
        }
        if( rulerEnabled ) {
            apparatusPanel.addGraphic( rulerGraphic, Integer.MAX_VALUE );
//            getPhetMainPanel().getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
        }
        else {
            apparatusPanel.removeGraphic( rulerGraphic );
//            getPhetMainPanel().getApparatusPanel().removeGraphic( rulerGraphic );
        }
    }


    /**
     * Adds a physical body to the apparatus panel at a specified
     * level
     */
    public void addBody( Particle body, int level ) {

        GraphicFactory graphicFactory = PhetApplication.instance().getGraphicFactory();

        PhetGraphic graphic = graphicFactory.createGraphic( body, apparatusPanel );
        apparatusPanel.addGraphic( graphic, level );
        apparatusPanel.repaint();

//        // If it's a particle, only add it to the current apparatus panel
//        if( body instanceof edu.colorado.phet.idealgas.physics.body.IdealGasParticle ) {
//            ApparatusPanel apparatusPanel = getApparatusPanel();
//            PhetGraphic graphic = graphicFactory.createGraphic( body, apparatusPanel );
//            apparatusPanel.addGraphic( graphic, level );
//            apparatusPanel.repaint();
//        } else {
//            for( int i = 0; i < getApparatusPanels().size(); i++ ) {
//                ApparatusPanel apparatusPanel = (ApparatusPanel)getApparatusPanels().get( i );
//                PhetGraphic graphic = graphicFactory.createGraphic( body, apparatusPanel );
//                apparatusPanel.addGraphic( graphic, level );
//                apparatusPanel.repaint();
//            }
//        }
    }
}
