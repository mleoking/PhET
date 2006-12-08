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
import edu.colorado.phet.molecularreactions.model.Launcher;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.molecularreactions.view.ReactionChooserPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * SimpleMRControlPanel
 * <p>
 * Control panel for the Simple module
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMRControlPanel extends MRControlPanel {
    private LauncherOptionsPanel launcherOptionsPanel;

    public SimpleMRControlPanel( final SimpleModule module ) {
        super( new GridBagLayout() );

        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 10, 0, 0, 0 ), 0, 0 );
        launcherOptionsPanel = new LauncherOptionsPanel( module );
        add( launcherOptionsPanel, gbc );

        add( new ReactionChooserPanel( module ), gbc );

        gbc.fill = GridBagConstraints.NONE;
        JButton reloadBtn = new JButton( SimStrings.get("Control.reload"));
        reloadBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reload();
            }
        } );
        gbc.anchor = GridBagConstraints.CENTER;
        add( reloadBtn, gbc );

        JButton resetAllBtn = new JButton( SimStrings.get( "Control.reset") );
        resetAllBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        gbc.anchor = GridBagConstraints.CENTER;
        add( resetAllBtn, gbc );
    }

    public void reset() {
        launcherOptionsPanel.reset();
    }


    private class LauncherOptionsPanel extends JPanel implements  Resetable {
        private SimpleModule module;
        private JRadioButton oneDRB;
        private JRadioButton twoDRB;

        public LauncherOptionsPanel( final SimpleModule module ) {
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

            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.launcherOptions" ) ) );
            setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 10, 0, 0 );
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

            module.getLauncher().addChangeListener( new Launcher.ChangeListener() {
                public void stateChanged( Launcher launcher ) {
                    oneDRB.setSelected( module.getLauncher().getMovementType() == Launcher.ONE_DIMENSIONAL );
                    twoDRB.setSelected( module.getLauncher().getMovementType() == Launcher.TWO_DIMENSIONAL );
                }
            } );
        }

        private class LauncherRBActionListener implements ActionListener {

            public void actionPerformed( ActionEvent e ) {
                Launcher.MovementType movementType = null;
                if( oneDRB.isSelected() ) {
                    movementType=Launcher.ONE_DIMENSIONAL;
                }
                if( twoDRB.isSelected() ) {
                    movementType = Launcher.TWO_DIMENSIONAL;
                }
                module.resetMolecules( module.getMRModel() );
//                module.reset();
                module.getLauncher().setMovementType( movementType );
            }
        }

        public void reset() {
            oneDRB.setSelected( true );
            module.getLauncher().setMovementType( Launcher.ONE_DIMENSIONAL );
        }
    }
}
