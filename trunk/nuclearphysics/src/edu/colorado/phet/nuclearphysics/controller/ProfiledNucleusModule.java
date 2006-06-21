/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsModel;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.view.EnergyProfilePanelGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A module that presents a PhysicalPanel at the top and PotentialProfilePanel below.
 * <p.
 * The apparatus panel has a physical representation, and overlaid on it is a graphic showing the
 * energy profile for the nucleus.
 * <p>
 * The code to get things to lay out in the right places is, unfortunately, spread out and obtuse. The basic origin
 * is in the middel of the apparatus panel, but calling setOrigin() on the PhysicalPanel translates the origin from
 * there.
 */
public abstract class ProfiledNucleusModule extends NuclearPhysicsModule {

    private Nucleus nucleus;
    private EnergyProfilePanelGraphic energyProfilePanel;
//    private EnergyProfilePanel energyProfilePanel;
    private GridBagConstraints physicalPanelGBC;

    public ProfiledNucleusModule( String name, IClock clock ) {
        super( name, clock );
    }

    protected void init() {
        getApparatusPanel().setLayout( new GridBagLayout() );
        physicalPanelGBC = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                   GridBagConstraints.CENTER,
                                                   GridBagConstraints.BOTH,
                                                   new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints profilePanelGBC = new GridBagConstraints( 0, 1, 1, 1, 1, .75,
                                                                     GridBagConstraints.CENTER,
                                                                     GridBagConstraints.BOTH,
                                                                     new Insets( 0, 0, 0, 0 ), 0, 0 );

        energyProfilePanel = new EnergyProfilePanelGraphic( getApparatusPanel() );
        energyProfilePanel.setVisible( true );
//        energyProfilePanel = new EnergyProfilePanel( getClock() );

//        final JDialog profilePanelDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
//        profilePanelDlg.getContentPane().setLayout( new BorderLayout( ));
//        profilePanelDlg.getContentPane().add( energyProfilePanel );
//        profilePanelDlg.pack();
//        final Component apparatusPanel = getApparatusPanel();
//        profilePanelDlg.setLocationRelativeTo( apparatusPanel );
//        getApparatusPanel().addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                final JDialog profilePanelDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
//                profilePanelDlg.getContentPane().setLayout( new BorderLayout( ));
//                profilePanelDlg.getContentPane().add( energyProfilePanel );
//                profilePanelDlg.pack();
//                final Component apparatusPanel = getApparatusPanel();
//                profilePanelDlg.setLocationRelativeTo( apparatusPanel );
//                profilePanelDlg.setLocation( (apparatusPanel.getWidth() - profilePanelDlg.getWidth()) / 2,
//                                             (apparatusPanel.getHeight() - profilePanelDlg.getHeight()) / 2);
//                profilePanelDlg.setVisible( true );
//            }
//        } );
//        profilePanelDlg.setLocation( (apparatusPanel.getWidth() - profilePanelDlg.getWidth()) / 2,
//                                     (apparatusPanel.getHeight() - profilePanelDlg.getHeight()) / 2);
//        profilePanelDlg.setVisible( true );
//        getApparatusPanel().add( energyProfilePanel, profilePanelGBC );

        NuclearPhysicsModel model = (NuclearPhysicsModel)getModel();
        model.addNucleusListener( new NuclearPhysicsModel.NucleusListener() {
            public void nucleusAdded( NuclearPhysicsModel.ChangeEvent event ) {
                Nucleus nucleus = event.getNucleus();
                if( !( nucleus instanceof AlphaParticle ) ) {
                    energyProfilePanel.addEnergyProfile( event.getNucleus() );
                }
            }

            public void nucleusRemoved( NuclearPhysicsModel.ChangeEvent event ) {
                if( !( nucleus instanceof AlphaParticle ) ) {
                    energyProfilePanel.removeEnergyProfile( event.getNucleus().getEnergylProfile() );
                }
            }
        } );
    }

    protected void addPhysicalPanel( Component component ) {
        physicalPanelGBC.gridy = 0;

        // Add the energy profile panel
        AffineTransform atx = new AffineTransform( getPhysicalPanel().getNucleonTx() );
        atx.translate( -energyProfilePanel.getWidth()/ 2 , 150 );
        TxGraphic txg = new TxGraphic( energyProfilePanel, atx ) ;
        getPhysicalPanel().addOriginCenteredGraphic( txg, 1E12 );
        getApparatusPanel().add( component, physicalPanelGBC );
    }

    public void setNucleus( Nucleus nucleus ) {
        this.nucleus = nucleus;
    }

    protected Nucleus getNucleus() {
        return nucleus;
    }

    protected void addNucleus( Nucleus nucleus ) {
        getModel().addModelElement( nucleus );
//        super.addNucleus( nucleus );
//        energyProfilePanel.addEnergyProfile( nucleus );
    }

    protected void addNucleus( Nucleus nucleus, Color color ) {
        super.addNucleus( nucleus );
        energyProfilePanel.addNucleus( nucleus, color );
    }

    protected EnergyProfilePanelGraphic getEnergyProfilePanel() {
//    protected EnergyProfilePanel getEnergyProfilePanel() {
        return energyProfilePanel;
    }

    public void clear() {
        getEnergyProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }
}
