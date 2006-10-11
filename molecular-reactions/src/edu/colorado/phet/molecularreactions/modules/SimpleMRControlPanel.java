/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.molecularreactions.model.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * TestControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class TestControlPanel extends ControlPanel {
public class SimpleMRControlPanel extends JPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;

    public SimpleMRControlPanel( final SimpleModule module ) {
        super( new GridBagLayout() );

        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( new OptionsPanel( module ), gbc );

        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        gbc.anchor = GridBagConstraints.CENTER;
        add( resetBtn, gbc );
    }

    private class OptionsPanel extends JPanel {
        private SimpleModule module;
        private JRadioButton oneDRB;
        private JRadioButton twoDRB;

        public OptionsPanel( final SimpleModule module ) {
            this.module = module;

            ButtonGroup numDimensionsBG = new ButtonGroup();
            oneDRB = new JRadioButton( SimStrings.get( "Control.oneDimension" ) );
            twoDRB = new JRadioButton( SimStrings.get( "Control.twoDimensions" ) );
            numDimensionsBG.add( oneDRB );
            numDimensionsBG.add( twoDRB );
            oneDRB.setSelected( true );

            // Add a listener to the launcher that will set the proper radio button if
            // the launcher's state is programatically changed
            module.getLauncher().addChangeListener( new Launcher.ChangeListener() {
                public void stateChanged( Launcher launcher ) {
                    if( launcher.getMovementType() == Launcher.ONE_DIMENSIONAL
                        && !oneDRB.isSelected() ) {
                        oneDRB.setSelected( true );
                    }
                    if( launcher.getMovementType() == Launcher.TWO_DIMENSIONAL
                        && !twoDRB.isSelected() ) {
                        twoDRB.setSelected( true );
                    }
                }
            } );

            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.options" ) ) );
            setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 20, 0, 0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             insets, 0, 0 );
            add( oneDRB, gbc );
            add( twoDRB, gbc );

            // Assign behaviors
            oneDRB.addActionListener( new LauncherRBActionListener() );
            twoDRB.addActionListener( new LauncherRBActionListener() );
        }

        private class LauncherRBActionListener implements ActionListener {

            public void actionPerformed( ActionEvent e ) {
                if( oneDRB.isSelected() ) {
                    module.getLauncher().setMovementType( Launcher.ONE_DIMENSIONAL );
                }
                if( twoDRB.isSelected() ) {
                    module.getLauncher().setMovementType( Launcher.TWO_DIMENSIONAL );
                }
            }
        }
    }
}
