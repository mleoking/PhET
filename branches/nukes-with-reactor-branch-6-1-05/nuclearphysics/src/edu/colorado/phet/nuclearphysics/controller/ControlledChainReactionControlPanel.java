/**
 * Class: MultipleNucleusFissionControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 17, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;

public class ControlledChainReactionControlPanel extends JPanel {

    //
    // Static fields and methods
    //
    private static Random random = new Random();
    private static final int U235 = 1;
    private static final int U238 = 2;

    //
    // Instance fields and methods
    //
    private ChainReactionModule module;
    private JTextField percentDecayTF;

    public ControlledChainReactionControlPanel( final ControlledFissionModule module ) {
        super();
        this.module = module;

        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "MultipleNucleusFissionControlPanel.ControlBorder" ) );
        this.setBorder( titledBorder );

        //
        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                SwingUtilities.getWindowAncestor( ControlledChainReactionControlPanel.this ).validate();
            }
        } );

        // Create the controls
        JButton fireNeutronBtn = new JButton( SimStrings.get( "MultipleNucleusFissionControlPanel.FireButton" ) );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
                percentDecayTF.setText( "0" );
                percentDecayTF.setEditable( false );
                percentDecayTF.setBackground( Color.white );
            }
        } );

        // The reset button
        JButton resetBtn = new JButton( SimStrings.get( "MultipleNucleusFissionControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
                percentDecayTF.setText( "0" );
            }
        } );

        percentDecayTF = new JTextField( 4 );
        percentDecayTF.setHorizontalAlignment( JTextField.RIGHT );
        percentDecayTF.setText( "0" );
        percentDecayTF.setEditable( false );
        percentDecayTF.setBackground( Color.white );

        // Layout the panel
        setLayout( new GridBagLayout() );
        GridBagConstraints gbcLeft = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 5, 5, 5, 5 ), 5, 5 );
        GridBagConstraints gbcRight = new GridBagConstraints( 1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 5, 5, 5, 5 ), 5, 5 );
        GridBagConstraints gbcCenter = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                               2, 1, 1, 1, GridBagConstraints.CENTER,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 5, 5, 5, 5 ), 5, 5 );
        add( fireNeutronBtn, gbcCenter );
        add( resetBtn, gbcCenter );

        // A check box to bring up the debug panel
        // create the dialog with the developers' controls
//        final JCheckBox debugPanelCB = new JCheckBox( SimStrings.get( "AdvancedControls.ShowControls" ) );
//        debugPanelCB.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                module.setDevelopmentControlDialog( debugPanelCB.isSelected() );
//            }
//        } );
//        add( debugPanelCB, gbcCenter );
    }
}
