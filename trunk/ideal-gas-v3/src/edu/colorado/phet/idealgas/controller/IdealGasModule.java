/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.command.RemoveMoleculeCmd;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.PressureSensingBox;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.view.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.view.Box2DGraphic;
import edu.colorado.phet.idealgas.view.Mannequin;
import edu.colorado.phet.idealgas.view.monitors.CmLines;
import edu.colorado.phet.idealgas.view.monitors.IdealGasMonitorPanel;

import java.awt.geom.Point2D;
import java.util.ResourceBundle;

public class IdealGasModule extends Module {

    private static ResourceBundle localizedStrings;

    static {
        localizedStrings = ResourceBundle.getBundle( "localization/IdealGasModule" );
    }

    private IdealGasModel idealGasModel;
    private PressureSensingBox box;
    private Gravity gravity;
    private Pump pump;
    private CmLines cmLines;
    //    private DefaultInteractiveGraphic rulerGraphic;


    public IdealGasModule( AbstractClock clock ) {
        this( clock, localizedStrings.getString( "Ideal_Gas" ) );
    }

    protected IdealGasModel getIdealGasModel() {
        return (IdealGasModel)getModel();
    }

    public IdealGasModule( AbstractClock clock, String name ) {
        super( name );

        // Create the model
        idealGasModel = new IdealGasModel( clock.getDt() );
        setModel( idealGasModel );

        gravity = new Gravity( idealGasModel );
        idealGasModel.addModelElement( gravity );

        // Add collision experts
        idealGasModel.addCollisionExpert( new SphereSphereExpert( idealGasModel, clock.getDt() ) );
        idealGasModel.addCollisionExpert( new SphereBoxExpert( idealGasModel ) );


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
        Mannequin pusher = new Mannequin( getApparatusPanel(), idealGasModel, box, boxGraphic );
        addGraphic( pusher, 10 );

        // Set up the control panel
        setControlPanel( new IdealGasControlPanel( this ) );
    }

    public void setCurrentSpecies( Class moleculeClass ) {
        pump.setCurrentGasSpecies( moleculeClass );
    }

    public void setCmLinesOn( boolean cmLinesOn ) {
        if( cmLines == null ) {
            cmLines = new CmLines( getApparatusPanel(), idealGasModel );
        }
        if( cmLinesOn ) {
            addGraphic( cmLines, 20 );
        }
        else {
            getApparatusPanel().removeGraphic( cmLines );
        }

    }

    public void setStove( int value ) {
        idealGasModel.setHeatSource( (double)value );
        ( (BaseIdealGasApparatusPanel)getApparatusPanel() ).setStove( value );
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

    protected PressureSensingBox getBox() {
        return box;
    }
}
