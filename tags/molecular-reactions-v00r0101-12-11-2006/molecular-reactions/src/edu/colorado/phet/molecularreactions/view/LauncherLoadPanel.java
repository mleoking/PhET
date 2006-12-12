/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.modules.SimpleModule;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.view.icons.MoleculeIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LauncherLoadPanel
 * <p>
 * Provides options for which type of molecule is loaded in the launcher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LauncherLoadPanel extends JPanel {
    private JRadioButton aRB;
    private JRadioButton cRB;
    private SimpleModule module;
    private JLabel moleculeCLabel;
    private JLabel moleculeALabel;

    public LauncherLoadPanel( SimpleModule module ) {
        this.module = module;

        moleculeALabel = new JLabel();
        moleculeALabel.setHorizontalAlignment( JLabel.CENTER );
        moleculeCLabel = new JLabel();
        moleculeCLabel.setHorizontalAlignment( JLabel.CENTER );
        // Add a listener to the model that will update the icons if the energy profile changes
        module.getMRModel().addListener( new MRModel.ModelListener() {
            public void energyProfileChanged( EnergyProfile profile ) {
                updateIcons();
            }
        } );
        updateIcons();

        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.launcherType" ) ) );
        setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );

        ButtonGroup bg = new ButtonGroup();
        aRB = new JRadioButton();
        cRB = new JRadioButton();
        bg.add( aRB );
        bg.add( cRB );

        aRB.addActionListener( new MoleculeSelectorRBAction() );
        cRB.addActionListener( new MoleculeSelectorRBAction() );

        aRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );
        cRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );

        setLayout( new GridBagLayout() );
        int rbAnchor = GridBagConstraints.CENTER;
        int iconAnchor = GridBagConstraints.CENTER;
        Insets leftInsets = new Insets( 3, 25, 3, 5 );
        Insets rightInsets = new Insets( 3, 5, 3, 25 );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         rbAnchor,
                                                         GridBagConstraints.HORIZONTAL,
                                                         leftInsets, 0, 0 );
        add( aRB, gbc );
        add( cRB, gbc );
        gbc.gridy = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.anchor = iconAnchor;
        gbc.insets = rightInsets;
        add( moleculeALabel, gbc );
        add( moleculeCLabel, gbc );

        // Fix the horizontal size to make room for the border title
        gbc.gridwidth = 2;
        add( Box.createHorizontalStrut( 30 ), gbc );

        cRB.setSelected( true );
    }

    private void updateIcons() {
        moleculeALabel.setIcon( new MoleculeIcon( MoleculeA.class, module.getMRModel().getEnergyProfile() ) );
        moleculeCLabel.setIcon(new MoleculeIcon( MoleculeC.class, module.getMRModel().getEnergyProfile() ) );
    }

    public void setMolecule( SimpleMolecule launcherMolecule ) {
        aRB.setSelected( launcherMolecule instanceof MoleculeA );
        cRB.setSelected( launcherMolecule instanceof MoleculeC );
    }

    private class MoleculeSelectorRBAction extends AbstractAction {

        public void actionPerformed( ActionEvent e ) {
            if( aRB.isSelected() ) {
                module.setMolecules( module.getMRModel(), new MoleculeA() );
            }
            if( cRB.isSelected() ) {
                module.setMolecules( module.getMRModel(), new MoleculeC() );
            }
        }
    }
}
