/**
 * Class: FisionModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Particle;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;
import edu.colorado.phet.nuclearphysics.model.Uranium2235;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfileGraphic;
import edu.colorado.phet.nuclearphysics.view.ProtonGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Point2D;

public class FisionModule extends Module {
    private ApparatusPanel apparatusPanel;
    private PotentialProfile potentialProfile;
    private ApparatusPanel potentialProfilePanel;
    private ApparatusPanel physicalPanel;
    private Uranium2235 uraniumNucleus;

    public FisionModule() {
        super( "Fision" );

        apparatusPanel = new ApparatusPanel();
        super.setApparatusPanel( apparatusPanel );

        potentialProfilePanel = new ApparatusPanel();
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Potential Profile" );
        potentialProfilePanel.setBorder( titledBorder );

        physicalPanel = new ApparatusPanel();
        BevelBorder baseBorder2 = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder2 = BorderFactory.createTitledBorder( baseBorder2, "Physical System" );
        physicalPanel.setBorder( titledBorder2 );


        apparatusPanel.setLayout( new GridLayout( 1, 2 ) );
        apparatusPanel.add( potentialProfilePanel );
        apparatusPanel.add( physicalPanel );

        potentialProfile = new PotentialProfile( 250, 400, 75 );
        PotentialProfileGraphic ppg = new PotentialProfileGraphic( potentialProfile,
                                                                   new Point2D.Double( 250, 600 ) );
        potentialProfilePanel.addGraphic( ppg );

        System.out.println( potentialProfilePanel.getWidth() );
        uraniumNucleus = new Uranium2235( new Point2D.Double( 300, 400 ) );
        addNeucleus( uraniumNucleus );
//        addNeucleus( new Nucleus( new Point2D.Double( 400, 400 ), 5, 3, potentialProfile ));


        JPanel controlPanel = new FisionControlPanel( this );
        super.setControlPanel( controlPanel );
    }

    private void addNeucleus( Nucleus nucleus ) {
        NucleusGraphic ng = new NucleusGraphic( nucleus );
        physicalPanel.addGraphic( ng );
    }

    private void addProton( double x, double y ) {
        Particle p = new Particle( new Point2D.Double( x, y ) );
        ProtonGraphic pg = new ProtonGraphic( p );
        physicalPanel.addGraphic( pg );
    }

    private void addNeutron( double x, double y ) {
        Particle p = new Particle( new Point2D.Double( x, y ) );
        NeutronGraphic ng = new NeutronGraphic( p );
        physicalPanel.addGraphic( ng );
    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
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
//        uraniumNucleus.decay();
    }
}
