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

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.molecularreactions.controller.ResetAllAction;
import edu.colorado.phet.molecularreactions.view.Legend;
import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

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
        private MRModule module;

        public OptionsPanel( MRModule module ) {
            this.module = module;

            ButtonGroup numDimensionsBG = new ButtonGroup();
            final JRadioButton oneDRB = new JRadioButton( SimStrings.get( "Control.oneDimension" ) );
            final JRadioButton twoDRB = new JRadioButton( SimStrings.get( "Control.twoDimensions" ) );
            numDimensionsBG.add( oneDRB );
            numDimensionsBG.add( twoDRB );
            oneDRB.setSelected( true );

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
        }
    }
}
