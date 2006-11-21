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

import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.SimpleModule;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeC;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LauncherLoadPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LauncherLoadPanel extends JPanel {
    private JRadioButton aRB;
    private JRadioButton cRB;
    private Class currentMoleculeType;
    private SimpleModule module;

    public LauncherLoadPanel( SimpleModule module ) {
        this.module = module;
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
        add( new JLabel( new MoleculeIcon( MoleculeA.class, module.getMRModel().getEnergyProfile() ) ), gbc );
        add( new JLabel( new MoleculeIcon( MoleculeC.class, module.getMRModel().getEnergyProfile() ) ), gbc );

        cRB.setSelected( true );
        currentMoleculeType = MoleculeA.class;
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
