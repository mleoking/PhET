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
import edu.colorado.phet.nuclearphysics.view.PotentialProfileGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;

public class NuclearPhysicsModule extends Module {
    protected ApparatusPanel apparatusPanel;
    protected PotentialProfile potentialProfile;
    protected PotentialProfilePanel potentialProfilePanel;
    protected ApparatusPanel physicalPanel;
    protected Uranium2235 uraniumNucleus;

    public NuclearPhysicsModule( String name, AbstractClock clock ) {
        super( name );
        potentialProfile = new PotentialProfile( 250, 400, 75 );
        potentialProfilePanel = new PotentialProfilePanel();
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
        this.setModel( new FisionModel( clock ) );
        this.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                apparatusPanel.repaint();
            }
        } );
        boolean b = clock.hasStarted();
        b = clock.isRunning();

        JPanel controlPanel = new NuclearPhysicsControlPanel( this );
        super.setControlPanel( controlPanel );


        PotentialProfileGraphic ppg = new PotentialProfileGraphic( potentialProfile );
        potentialProfilePanel.addGraphic( ppg );

//
//        uraniumNucleus = new Uranium2235(new Point2D.Double(200, 400));
//        addNeucleus(uraniumNucleus);
//

    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    protected void addNeucleus( Nucleus nucleus ) {
        this.getModel().addModelElement( nucleus );
        NucleusGraphic ng = new NucleusGraphic( nucleus );
        physicalPanel.addGraphic( ng );
        potentialProfilePanel.addGraphic( ng );
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
        return this.potentialProfile;
    }

    public void testDecay() {
        DecayProducts dp = uraniumNucleus.decay();
        getModel().removeModelElement( dp.getN0() );
        getModel().addModelElement( dp.getN1() );
        getModel().addModelElement( dp.getN2() );
        physicalPanel.removeGraphic( NucleusGraphic.getGraphic( dp.getN0() ) );
        potentialProfilePanel.removeGraphic( NucleusGraphic.getGraphic( dp.getN0() ) );
        NucleusGraphic n1g = new NucleusGraphic( dp.getN1() );
        physicalPanel.addGraphic( n1g );
        NucleusGraphic n2g = new NucleusGraphic( dp.getN2() );
        physicalPanel.addGraphic( n2g );
    }


    //
    // Interfaces implemented
    //
    
    //
    // Static fields and methods
    //
    
    //
    // Inner classes
    //
}
