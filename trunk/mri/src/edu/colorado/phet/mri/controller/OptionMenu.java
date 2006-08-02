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

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.mri.MriConfig;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * OptionMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class OptionMenu extends JMenu {

    public OptionMenu() {
        super( "Options" );
        add( new FlipDelayMI() );


        final PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        JMenuItem backgroundColorMI = new JMenuItem( "Background color" );
        add( backgroundColorMI );
        backgroundColorMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color newColor = JColorChooser.showDialog( PhetUtilities.getPhetFrame(),
                                                           "Background Color",
                                                           phetLookAndFeel.getBackgroundColor() );
                phetLookAndFeel.setBackgroundColor( newColor );
                phetLookAndFeel.apply();
                SwingUtilities.updateComponentTreeUI( PhetUtilities.getPhetFrame() );
            }
        } );

        JMenuItem foregroundColorMI = new JMenuItem( "Foreground color" );
        add( foregroundColorMI );
        foregroundColorMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color newColor = JColorChooser.showDialog( PhetUtilities.getPhetFrame(),
                                                           "Foreground Color",
                                                           phetLookAndFeel.getForegroundColor() );
                phetLookAndFeel.setForegroundColor( newColor );
                phetLookAndFeel.apply();
            }
        } );
    }

    //----------------------------------------------------------------
    // Menu Items
    //----------------------------------------------------------------

    /**
     *
     */
    class FlipDelayMI extends JMenuItem {
        public FlipDelayMI() {
            super( "Dipole reset delay..." );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setResetDelay();
                }
            } );
        }

        private void setResetDelay() {
            final JDialog dlg = new JDialog( PhetUtilities.getPhetFrame(), "Adjust Reset Delay", false );
            dlg.getContentPane().setLayout( new BorderLayout() );
            final ModelSlider sldr = new ModelSlider( "",
                                                      "simulation time",
                                                      0,
                                                      1000,
                                                      MriConfig.SPIN_DOWN_TIMEOUT,
                                                      new DecimalFormat( "#" ) );
            sldr.setMajorTickSpacing( 200 );
            sldr.setNumMajorTicks( 3 );
            sldr.setPaintTicks( true );
            sldr.setPaintLabels( true );
            sldr.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    MriConfig.SPIN_DOWN_TIMEOUT = (long)sldr.getValue();
                }
            } );
            dlg.getContentPane().add( sldr );
            JButton btn = new JButton( "Close" );
            btn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dlg.setVisible( false );
                }
            } );
            JPanel btnPnl = new JPanel();
            btnPnl.add( btn );
            dlg.getContentPane().add( btnPnl, BorderLayout.SOUTH );
            dlg.pack();
            dlg.setVisible( true );
        }
    }
}
