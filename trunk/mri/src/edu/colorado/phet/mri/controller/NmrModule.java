/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.util.*;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.NmrControlPanel;
import edu.colorado.phet.mri.model.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.geom.Rectangle2D;
import java.awt.*;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NmrModule extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = SimStrings.get( "Module.NmrTitle" );

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    static SampleChamber sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                                                                                    MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
                                                                                    MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                                    MriConfig.SAMPLE_CHAMBER_HEIGHT ) );

    /**
     * Constructor
     */
    public NmrModule() {
        super( name, new SwingClock( delay, dt ), sampleChamber );
    }

    protected void init() {
        super.init();

        System.out.println( "NmrModule.init" );

        // Control panel
        setControlPanel( new NmrControlPanel( this ) );

        // Sample Chamber
        sampleChamber.createDipoles( (MriModel)getModel(), 20 );

        // Set the initial view
        setEmRep( NmrModule.WAVE_VIEW );

//        SwingUtilities.invokeLater( new Runnable() {
//            public void run() {
//
//                Magnifier magnifierB = new Magnifier( getSimulationPanel(),
//                                                      getClock(),
//                                                      new Dimension( 150, 150 ),
//                                                      2.0 );
//                getControlPanel().addControl( new MagnifierPanel( magnifierB ));
//            }
//        } );


//        JFrame magFrame = new JFrame( "Magnifier");
//        Magnifier2 mag2 = new Magnifier2( ( MySimPanel)getSimulationPanel(), new Dimension( 200, 200), 2 );
//        magFrame.getContentPane().add( mag2 );
//        magFrame.pack();
//        magFrame.setVisible( true );
//
//        MagnifierPNode mpn = new MagnifierPNode( ( MySimPanel)getSimulationPanel(), new Dimension( 200, 200), 2 );
//        mpn.setOffset( 400, 400 );
//        mpn.update();
//        (( MySimPanel)getSimulationPanel()).addScreenChild( mpn );

    }

    class MagnifierPanel extends JPanel {
        public MagnifierPanel( final Magnifier magnifier ) {
            super( new GridBagLayout() );
            setBorder( ControlBorderFactory.createBorder( "Magnifier "));

            final JSpinner magSpinner = new JSpinner( new SpinnerNumberModel( 2, 0.5, 5, 0.1 ));
            magSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double mag = ((Double)magSpinner.getValue()).doubleValue();
                    magnifier.setZoom( mag );
                }
            } );

            GridBagConstraints gbc = new GridBagConstraints( 0,0,1,1,1,1,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0,10,0,10),0,0 );
            gbc.gridheight = 2;
            add( magnifier, gbc );
            gbc.gridheight = 1;
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            add( new JLabel( "Magnification"), gbc);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy = 1;
            add( magSpinner, gbc );
        }
    }

}
