/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.modules;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.Launcher;
import edu.colorado.phet.reactionsandrates.util.ControlBorderFactory;
import edu.colorado.phet.reactionsandrates.util.Resetable;
import edu.colorado.phet.reactionsandrates.view.ReactionChooserPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * SimpleMRControlPanel
 * <p/>
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
        JButton reloadBtn = new JButton( MRConfig.RESOURCES.getLocalizedString( "Control.reload" ) );
        reloadBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reload();
            }
        } );
        gbc.anchor = GridBagConstraints.CENTER;
        add( reloadBtn, gbc );

        ResetAllButton resetAllBtn = new ResetAllButton( this );
        resetAllBtn.addResettable( new Resettable() {
            public void reset() {
                module.reset();
            }
        });
        gbc.anchor = GridBagConstraints.CENTER;
        add( resetAllBtn, gbc );
    }

    public void reset() {
        launcherOptionsPanel.reset();
    }


    private class LauncherOptionsPanel extends JPanel implements Resetable {
        private SimpleModule module;
        private JRadioButton oneDRB;
        private JRadioButton twoDRB;

        public LauncherOptionsPanel( final SimpleModule module ) {
            this.module = module;

            ButtonGroup numDimensionsBG = new ButtonGroup();
            oneDRB = new JRadioButton( MRConfig.RESOURCES.getLocalizedString( "Control.oneDimension" ) );
            twoDRB = new JRadioButton( MRConfig.RESOURCES.getLocalizedString( "Control.twoDimensions" ) );
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

            setBorder( ControlBorderFactory.createPrimaryBorder( MRConfig.RESOURCES.getLocalizedString( "Control.launcherOptions" ) ) );
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
                    movementType = Launcher.ONE_DIMENSIONAL;
                }
                if( twoDRB.isSelected() ) {
                    movementType = Launcher.TWO_DIMENSIONAL;
                }
                module.resetMolecules();
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
