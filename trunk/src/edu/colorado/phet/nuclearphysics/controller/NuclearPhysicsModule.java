/**
 * Class: NuclearPhysicsModule
 * Class: edu.colorado.phet.nuclearphysics.controller
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 12:07:12 PM
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.PhysicalPanel;

import javax.swing.*;
import java.awt.*;

public class NuclearPhysicsModule extends Module {
    private ApparatusPanel apparatusPanel;
//    private AlphaDecayPhysicalPanel physicalPanel;
    private PhysicalPanel physicalPanel;

    public NuclearPhysicsModule( String name, AbstractClock clock ) {
        super( name );
        physicalPanel = new PhysicalPanel();
        apparatusPanel = new ApparatusPanel();
        super.setApparatusPanel( apparatusPanel );
        apparatusPanel.setLayout( new GridLayout( 1, 1 ) );
        apparatusPanel.add( physicalPanel );

        // Start the model
        this.setModel( new FissionModel( clock ) );
        this.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                apparatusPanel.repaint();
            }
        } );

        JPanel controlPanel = new NuclearPhysicsControlPanel( this );
        super.setControlPanel( controlPanel );
    }

    public void setPhysicalPanel( PhysicalPanel physicalPanel ) {
        this.physicalPanel = physicalPanel;
    }

    protected void addControlPanelElement( JPanel panel ) {
        ( (NuclearPhysicsControlPanel)getControlPanel() ).addPanelElement( panel );
    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    protected void addNeucleus( Nucleus nucleus ) {
        this.getModel().addModelElement( nucleus );
        physicalPanel.addNucleus( nucleus );
    }

//    protected void addAlphaParticle( AlphaParticle alphaParticle, Nucleus nucleus ) {
//        this.getModel().addModelElement( alphaParticle );
//        physicalPanel.addAlphaParticle( alphaParticle );
//    }
//
    protected void addNeutron( NuclearParticle particle ) {
        this.getModel().addModelElement( particle );
        NeutronGraphic ng = new NeutronGraphic( particle );
        physicalPanel.addGraphic( ng );
    }

    protected void addNeutron( NuclearParticle particle, Nucleus nucleus ) {
        this.getModel().addModelElement( particle );
        NeutronGraphic ng = new NeutronGraphic( particle );
        physicalPanel.addGraphic( ng );
    }

    protected void setUraniumNucleus( Uranium235 uraniumNucleus ) {
        addNeucleus( uraniumNucleus );
    }

    protected void handleDecay( DecayProducts decayProducts ) {
        // Remove the old nucleus
        getModel().removeModelElement( decayProducts.getN0() );
        physicalPanel.removeNucleus( decayProducts.getN0() );

        // Add the new nuclei
        getModel().addModelElement( decayProducts.getN1() );
        getModel().addModelElement( decayProducts.getN2() );
    }

    protected PhysicalPanel getPhysicalPanel() {
//    protected AlphaDecayPhysicalPanel getPhysicalPanel() {
        return physicalPanel;
    }

//    protected PotentialProfilePanel getPotentialProfilePanel() {
//        return potentialProfilePanel;
//    }
}
