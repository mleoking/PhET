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
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;
import edu.colorado.phet.nuclearphysics.view.PhysicalPanel;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;

public class NuclearPhysicsModule extends Module {
    private ApparatusPanel apparatusPanel;
    private PotentialProfile potentialProfile;
    private PotentialProfilePanel potentialProfilePanel;
    private PhysicalPanel physicalPanel;
    private Uranium235 uraniumNucleus;

    public NuclearPhysicsModule( String name, AbstractClock clock ) {
        super( name );
        potentialProfile = defaultProfile;
        potentialProfilePanel = new PotentialProfilePanel( potentialProfile );
        physicalPanel = new PhysicalPanel();
        apparatusPanel = new ApparatusPanel();
        super.setApparatusPanel( apparatusPanel );

        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Potential Energy Profile" );
        potentialProfilePanel.setBorder( titledBorder );

        BevelBorder baseBorder2 = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder2 = BorderFactory.createTitledBorder( baseBorder2, "Physical System" );
        physicalPanel.setBorder( titledBorder2 );

        apparatusPanel.setLayout( new GridLayout( 1, 2 ) );
        apparatusPanel.add( potentialProfilePanel );
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
        potentialProfilePanel.setNucleus( nucleus );
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

    public PotentialProfile getPotentialProfile() {
        return this.potentialProfilePanel.getPotentialProfile();
    }


    protected Uranium235 getUraniumNucleus() {
        return uraniumNucleus;
    }

    protected void setUraniumNucleus( Uranium235 uraniumNucleus ) {
        this.uraniumNucleus = uraniumNucleus;
        addNeucleus( getUraniumNucleus() );
    }

    protected void handleDecay( DecayProducts decayProducts ) {
        // Remove the old nucleus
        getModel().removeModelElement( decayProducts.getN0() );
        physicalPanel.removeNucleus( decayProducts.getN0() );
        potentialProfilePanel.removeNucleus();

        // Add the new nuclei
        getModel().addModelElement( decayProducts.getN1() );
        getModel().addModelElement( decayProducts.getN2() );
        NucleusGraphic n1g = new NucleusGraphic( decayProducts.getN1() );
        physicalPanel.addGraphic( n1g );
        NucleusGraphic n2g = new NucleusGraphic( decayProducts.getN2() );
        physicalPanel.addGraphic( n2g );
    }

    protected PotentialProfilePanel getPotentialProfilePanel() {
        return potentialProfilePanel;
    }

    public void testDecay() {
        DecayProducts dp = uraniumNucleus.alphaDecay();
        getModel().removeModelElement( dp.getN0() );
        getModel().addModelElement( dp.getN1() );
        getModel().addModelElement( dp.getN2() );
        physicalPanel.removeNucleus( dp.getN0() );
        potentialProfilePanel.removeNucleus();
        physicalPanel.addNucleus( dp.getN1() );
        physicalPanel.addNucleus( dp.getN1() );
    }

    //
    // Statics
    //
    private static PotentialProfile defaultProfile = new PotentialProfile( 300, 400, 75 );

}
