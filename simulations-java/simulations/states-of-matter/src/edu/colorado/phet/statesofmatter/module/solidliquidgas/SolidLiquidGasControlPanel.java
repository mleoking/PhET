/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationLegendPanel;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;


public class SolidLiquidGasControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public SolidLiquidGasControlPanel( SolidLiquidGasModule solidLiquidGasModule, Frame parentFrame ) {
        
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the panel that allows the user to select molecule type.
        addControlFullWidth( new MoleculeSelectionPanel() );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( solidLiquidGasModule );

    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------
    private class MoleculeSelectionPanel extends JPanel {
        
        JRadioButton m_oxygenRadioButton;
        JRadioButton m_nitrogenRadioButton;
        JRadioButton m_carbonDioxideRadioButton;
        
        MoleculeSelectionPanel(){
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.MOLECULE_TYPE_SELECT_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_oxygenRadioButton = new JRadioButton( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL );
            m_oxygenRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_nitrogenRadioButton = new JRadioButton( StatesOfMatterStrings.NITROGEN_SELECTION_LABEL );
            m_nitrogenRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_carbonDioxideRadioButton = new JRadioButton( StatesOfMatterStrings.CARBON_DIOXIDE_SELECTION_LABEL );
            m_carbonDioxideRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_oxygenRadioButton );
            buttonGroup.add( m_nitrogenRadioButton );
            buttonGroup.add( m_carbonDioxideRadioButton );
            m_oxygenRadioButton.setSelected( true );
            
            add( m_oxygenRadioButton );
            add( m_nitrogenRadioButton );
            add( m_carbonDioxideRadioButton );
        }
    }
}
