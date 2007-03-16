/**
 * Class: NuclearPhysicsModule
 * Class: edu.colorado.phet.nuclearphysics.controller
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 12:07:12 PM
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NuclearPhysicsApparatusPanel;
import edu.colorado.phet.nuclearphysics.view.PhysicalPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * An abstract class that has base behaviors for all modules in this simulation.
 * <p>
 * Abstract methods:
 * <ul>
 * <li>init()   This is called on the first activation of the module, so that initialization can be defered
 * <li>getLegendClasses() - Provides the classes of model elements for
 * which there should be graphics in the control panel's legend
 * </ul>
 *
 */
abstract public class NuclearPhysicsModule extends PhetGraphicsModule {
    private ApparatusPanel apparatusPanel;
    private PhysicalPanel physicalPanel;
    private IClock clock;
    private NuclearPhysicsControlPanel nuclearPysicslControlPanel;
    private boolean init;

    public NuclearPhysicsModule( String name, IClock clock ) {
        super( name, clock );
        this.clock = clock;

        // Start the model
        NuclearPhysicsModel model = new NuclearPhysicsModel();
        this.setModel( model );
        this.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                // todo: get rid of this, and make the graphics self-painting
                apparatusPanel.repaint();
            }
        } );

        apparatusPanel = new NuclearPhysicsApparatusPanel( clock );
        super.setApparatusPanel( apparatusPanel );

        physicalPanel = new PhysicalPanel( clock, model );
        apparatusPanel.setLayout( new GridLayout( 1, 1 ) );
        setPhysicalPanel( physicalPanel );
        apparatusPanel.add( physicalPanel );

        ControlPanel controlPanel = new ControlPanel();
        setControlPanel( controlPanel );
        nuclearPysicslControlPanel = new NuclearPhysicsControlPanel( this, getLegendClasses() );
        getControlPanel().addControl( nuclearPysicslControlPanel );
    }

    /**
     *
     */
    protected abstract void init();

    /**
     * Returns a list of LegendPanel.LegendItem instances that indicate which items should be in the module's
     * legend in the control panel
     * @return a list
     */
    protected abstract List getLegendClasses();

    /**
     * Call init() the first time the module is activated
     */
    public void activate() {
        if( !init ) {
            init();
            init = true;
        }
        super.activate();
    }

    public IClock getClock() {
        return clock;
    }

    public void setPhysicalPanel( PhysicalPanel physicalPanel ) {
        this.physicalPanel = physicalPanel;
    }

    protected void addControlPanelElement( JPanel panel ) {
        nuclearPysicslControlPanel.addPanelElement( panel );
    }

    protected void addNucleus( Nucleus nucleus ) {
        this.getModel().addModelElement( nucleus );
    }

    protected void addNeutron( final NuclearParticle particle ) {
        this.getModel().addModelElement( particle );
        final NeutronGraphic ng = new NeutronGraphic( particle );
        physicalPanel.addGraphic( ng );

        particle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                physicalPanel.removeGraphic( ng );
                particle.removeListener( this );
            }
        } );
    }

    protected void addNeutron( final NuclearParticle particle, Nucleus nucleus ) {
        this.getModel().addModelElement( particle );
        final NeutronGraphic ng = new NeutronGraphic( particle );
        physicalPanel.addGraphic( ng );

        particle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                physicalPanel.removeGraphic( ng );
                particle.removeListener( this );
            }
        } );
    }

    protected void setUraniumNucleus( Uranium235 uraniumNucleus ) {
        addNucleus( uraniumNucleus );
    }

    public PhysicalPanel getPhysicalPanel() {
        return physicalPanel;
    }



}
