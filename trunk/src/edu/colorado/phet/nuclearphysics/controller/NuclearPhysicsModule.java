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
import edu.colorado.phet.nuclearphysics.view.AlphaDecayPhysicalPanel;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import javax.swing.*;
import java.awt.*;

public class NuclearPhysicsModule extends Module {
    private ApparatusPanel apparatusPanel;
    private PotentialProfile potentialProfile;
    private PotentialProfilePanel potentialProfilePanel;
//    private PotentialProfilePanelOld potentialProfilePanel;
    private AlphaDecayPhysicalPanel physicalPanel;

    public NuclearPhysicsModule( String name, AbstractClock clock ) {
        super( name );
        potentialProfile = defaultProfile;
        physicalPanel = new AlphaDecayPhysicalPanel();
        potentialProfilePanel = new PotentialProfilePanel();
//        potentialProfilePanel = new PotentialProfilePanelOld( potentialProfile );
        apparatusPanel = new ApparatusPanel();
        super.setApparatusPanel( apparatusPanel );

        apparatusPanel.setLayout( new GridLayout( 2, 1 ) );
        apparatusPanel.add( physicalPanel );
        apparatusPanel.add( potentialProfilePanel );

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
        potentialProfilePanel.addNucleus( nucleus );
//        potentialProfilePanel.setNucleus( nucleus );
//        PotentialProfilePanel ppp = new PotentialProfilePanel();
//        PotentialProfilePanelOld ppp = new PotentialProfilePanelOld( nucleus.getPotentialProfile() );
    }

//    protected void addAlphaParticle( AlphaParticle alphaParticle ) {
//        this.getModel().addModelElement( alphaParticle );
//        potentialProfilePanel.addAlphaParticle( alphaParticle );
//        physicalPanel.addAlphaParticle( alphaParticle );
//    }

    protected void addAlphaParticle( AlphaParticle alphaParticle, Nucleus nucleus ) {
        this.getModel().addModelElement( alphaParticle );
        ( (PotentialProfilePanel)potentialProfilePanel ).addAlphaParticle( alphaParticle, nucleus );
        physicalPanel.addAlphaParticle( alphaParticle );
    }

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

    public void setProfileMaxHeight( double modelValue ) {
        potentialProfile.setMaxPotential( modelValue );
        potentialProfilePanel.repaint();
    }

    public void setProfileWellDepth( double wellDepth ) {
        potentialProfile.setWellDepth( wellDepth );
        potentialProfilePanel.repaint();
    }

    public void setProfileWidth( double profileWidth ) {
        potentialProfile.setWidth( profileWidth );
        potentialProfilePanel.repaint();
    }

//    public PotentialProfile getPotentialProfile() {
//        return this.potentialProfilePanel.getPotentialProfile();
//    }

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

    protected AlphaDecayPhysicalPanel getPhysicalPanel() {
        return physicalPanel;
    }

    protected PotentialProfilePanel getPotentialProfilePanel() {
//    protected PotentialProfilePanelOld getPotentialProfilePanel() {
        return potentialProfilePanel;
    }


    //
    // Statics
    //
    private static PotentialProfile defaultProfile = new PotentialProfile( 300, 400, 75 );

}
