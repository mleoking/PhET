/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;

/**
 * This class represents the control panel that allows the user to select the
 * item that can be radiometrically dated.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DatableItemSelectionPanel _selectionPanel;
    private RadiometricMeasurementModel _model;
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, 14 ); 
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param piccoloModule
     * @param parentFrame parent frame, for creating dialogs
     * @param customNucleusEnabled TODO
     * @param alphaDecayModel 
     */
    public RadiometricMeasurementControlPanel( PiccoloModule piccoloModule, Frame parentFrame, 
    		RadiometricMeasurementModel model ) {
        
    	_model = model;
    	
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panel
        _selectionPanel = new DatableItemSelectionPanel();
        
        // Add the selection panel.
        addControlFullWidth( _selectionPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( piccoloModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class DatableItemSelectionPanel extends JPanel {
    	
        //------------------------------------------------------------------------
        // Class Data
        //------------------------------------------------------------------------
        
        //------------------------------------------------------------------------
        // Instance Data
        //------------------------------------------------------------------------
        
        private JRadioButton _treeRadioButton;
        private JRadioButton _rockRadioButton;

        //------------------------------------------------------------------------
        // Constructor
        //------------------------------------------------------------------------
        
        public DatableItemSelectionPanel() {
            
            // Create the radio buttons.
        	
        	ImageIcon treeImageIcon = new ImageIcon(NuclearPhysicsResources.getImage("tree_1.png"));
            _treeRadioButton = new JRadioButton("Tree", treeImageIcon);
            _rockRadioButton = new JRadioButton();
            
            // Register for button presses.
            _treeRadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                }
            });
            _rockRadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                }
            });
            
            // Group the radio buttons together logically and set initial state.
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _treeRadioButton );
            buttonGroup.add( _rockRadioButton );
            _treeRadioButton.setSelected( true );
            
        }
        
        /**
         * Update the state of the buttons based on the values in the model.
         */
        public void updateButtonState(){
        	// TODO
        }
        
        private JPanel createVerticalSpacingPanel(int space){
            JPanel spacePanel = new JPanel();
            spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
            spacePanel.add( Box.createVerticalStrut( space ) );
            return spacePanel;
        }
    }
    
    /**
     * This class is used to describe a nucleus that needs to be added to a
     * control panel.  The description has little to do with the nature of the
     * nucleus itself and everything to do with how it is presented to the user.
     * 
     */
    private class NucleusSelectionDescriptor {
    	private final AtomicNucleusImageType imageType;
    	private final String isotopeNumberString;
    	private final String chemicalSymbol;
    	private final Color labelColor;
    	private final Color sphereColor;
    	private final String legendLabel;
    	
		public NucleusSelectionDescriptor(AtomicNucleusImageType imageType,
				String isotopeNumberString, String chemicalSymbol, Color labelColor,
				Color sphereColor, String legendLabel) {
			this.imageType = imageType;
			this.isotopeNumberString = isotopeNumberString;
			this.chemicalSymbol = chemicalSymbol;
			this.labelColor = labelColor;
			this.sphereColor = sphereColor;
			this.legendLabel = legendLabel;
		}

		public AtomicNucleusImageType getImageType() {
			return imageType;
		}

		public String getIsotopeNumberString() {
			return isotopeNumberString;
		}

		public String getChemicalSymbol() {
			return chemicalSymbol;
		}

		public Color getLabelColor() {
			return labelColor;
		}

		public Color getSphereColor() {
			return sphereColor;
		}

		public String getLegendLabel() {
			return legendLabel;
		}
    }
}
