/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.Strings;
import edu.colorado.phet.idealgas.controller.command.RemoveMoleculeCmd;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.PressureSensingBox;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.view.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.view.Box2DGraphic;
import edu.colorado.phet.idealgas.view.Mannequin;
import edu.colorado.phet.idealgas.view.monitors.IdealGasMonitorPanel;

import java.awt.geom.Point2D;

public class IdealGasModule extends Module {
    private IdealGasModel idealGasModel;
    private PressureSensingBox box;
    private Gravity gravity;
    private Pump pump;


    public IdealGasModule( AbstractClock clock ) {
        this( clock, Strings.idealGasModuleName );
    }

    public IdealGasModule( AbstractClock clock, String name  ) {        
        super( name );

        // Create the model
        idealGasModel = new IdealGasModel( clock.getDt() );
        setModel( idealGasModel );

        gravity = new Gravity( idealGasModel );
        idealGasModel.addModelElement( gravity );

        // Create the box
        double xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        double yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        double xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        double yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new PressureSensingBox( new Point2D.Double( xOrigin, yOrigin ),
                                      new Point2D.Double( xDiag, yDiag ), idealGasModel, clock );
        idealGasModel.addBox( box );

        // Create the pump
        pump = new Pump( this, box );

        setApparatusPanel( new BaseIdealGasApparatusPanel( this, box, pump ) );
        IdealGasMonitorPanel monitorPanel = new IdealGasMonitorPanel( idealGasModel );
        setMonitorPanel( monitorPanel );

        // Set up the box
        Box2DGraphic boxGraphic = new Box2DGraphic( getApparatusPanel(), box );
        addGraphic( boxGraphic, 10 );

        // Add the animated mannequin
        Mannequin pusher = new Mannequin( getApparatusPanel(), idealGasModel, box );
        addGraphic( pusher, 10 );

        // Set up the control panel
        setControlPanel( new IdealGasControlPanel( this ) );
    }

    public void setCurrentSpecies( Class moleculeClass ) {
        pump.setCurrentGasSpecies( moleculeClass );
    }

    public void setCmLinesOn( boolean selected ) {
        System.out.println( "not implemented" );
    }

    public void setStove( int value ) {
        idealGasModel.setHeatSource( (double)value );
        ( (BaseIdealGasApparatusPanel)getApparatusPanel() ).setStove( value );
    }

    //    public void setGravity( Gravity gravity ) {
    ////        idealGasModel.setGravityEnabled( false );
    //        idealGasModel.setGravity( gravity );
    //    }

    public void setPressureSliceEnabled( boolean selected ) {
        System.out.println( "not implemented" );
    }

    public void setRulerEnabed( boolean selected ) {
        System.out.println( "not implemented" );
    }

    public void setGravity( double value ) {
        gravity.setAmt( value );
    }

    public void pumpGasMolecules( int numMolecules ) {
        pump.pump( numMolecules );
    }

    public void removeGasMolecule() {
        Command cmd = new RemoveMoleculeCmd( idealGasModel, pump.getCurrentGasSpecies() );
        cmd.doIt();
    }
}
