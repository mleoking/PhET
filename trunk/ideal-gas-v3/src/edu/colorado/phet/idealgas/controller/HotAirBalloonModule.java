/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 3:35:40 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereHotAirBalloonExpert;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.BoxMustContainParticle;
import edu.colorado.phet.idealgas.model.Constraint;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.view.HotAirBalloonGraphic;
import edu.colorado.phet.idealgas.IdealGasConfig;

import java.awt.geom.Point2D;

public class HotAirBalloonModule extends IdealGasModule {

    private static final double initialVelocity = 35;

    private HotAirBalloon balloon;
    private double initRadius;
    private double initX;
    private double initY;
    private int defaultGravity = IdealGasConfig.MAX_GRAVITY / 4;

    public HotAirBalloonModule( AbstractClock clock ) {
        super( clock, SimStrings.get( "ModuleTitle.HotAirBalloon" ) );

        // Add collision experts to the model
        getIdealGasModel().addCollisionExpert( new SphereHotAirBalloonExpert( getIdealGasModel(), clock.getDt() ) );

        // Set the size of the box
        Box2D box = getIdealGasModel().getBox();

        // Add the hot air balloon to the model
        initRadius = 50;
        initX = box.getMinX() + box.getWidth() / 2;
        initY = box.getMaxY() - initRadius;
        balloon = new HotAirBalloon( new Point2D.Double( initX, initY ),
                                     new Vector2D.Double( 0, 0 ),
                                     new Vector2D.Double( 0, 0 ),
                                     200, initRadius,
                                     60,
                                     getIdealGasModel() );
        box.setMinimumWidth( balloon.getRadius() * 3 );

        // Give the balloon a high layer number so it always is over whatever
        // particles are pumped into the box
        getIdealGasModel().addModelElement( balloon );
        Constraint constraintSpec = new BoxMustContainParticle( box, balloon, getIdealGasModel() );
        balloon.addConstraint( constraintSpec );
        HotAirBalloonGraphic graphic = new HotAirBalloonGraphic( getApparatusPanel(), balloon );
        addGraphic( graphic, 20 );

        // Add the specific controls we need for the hot air balloon
        IdealGasControlPanel controlPanel = new IdealGasControlPanel( this );
        controlPanel.addParticleControl( new HotAirBalloonControlPanel( balloon ) );
        this.setControlPanel( new ControlPanel( this ) );
        getControlPanel().add( controlPanel );

        // Turn on gravity
        setGravity( defaultGravity );
    }

    protected Pump.PumpingEnergyStrategy getPumpingEnergyStrategy() {
        return new Pump.FixedEnergyStrategy();
    }

    /**
     * Places the balloon back at its initial location
     */
    public void reset() {
        super.reset();
        balloon.setPosition( new Point2D.Double( initX, initY ) );
        balloon.setHeatSource( 0 );
    }
}
