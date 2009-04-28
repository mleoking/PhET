/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.halflife;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class represents the control panel that allows the user to select the
 * isotope that will be decaying on the canvas.
 *
 * @author John Blanco
 */
public class IsotopeSelectionControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private NucleusSelectionPanel _selectionPanel;
    private NucleusTypeControl _model;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param piccoloModule
     * @param parentFrame parent frame, for creating dialogs
     * @param alphaDecayModel 
     */
    public IsotopeSelectionControlPanel( PiccoloModule piccoloModule, Frame parentFrame, 
    		NucleusTypeControl model ) {
        
    	_model = model;
    	
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panel
        _selectionPanel = new NucleusSelectionPanel();
        
        // Add the selection panel.
        addControlFullWidth( _selectionPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( piccoloModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class NucleusSelectionPanel extends JPanel {
    	
        //------------------------------------------------------------------------
        // Class Data
        //------------------------------------------------------------------------
        
        //------------------------------------------------------------------------
        // Instance Data
        //------------------------------------------------------------------------
        
        private NucleusTypeControl _alphaDecayModel;
        private JRadioButton _carbon14RadioButton;
        private JRadioButton _uranium238RadioButton;
        private JRadioButton _customRadioButton;

        //------------------------------------------------------------------------
        // Constructor
        //------------------------------------------------------------------------
        
        public NucleusSelectionPanel() {
            
        	// Register for notifications of nucleus type changes.
        	_model.addListener(new AlphaDecayAdapter(){
        		public void nucleusTypeChanged() {
        			if (_model.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
        				_uranium238RadioButton.setSelected(true);
        			}
        			else{
        				_carbon14RadioButton.setSelected(true);
        			}
        		}
        	});
        	
            // Add the border around the legend.
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    NuclearPhysicsStrings.NUCLEUS_SELECTION_BORDER_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );
            
            // Set the layout.
            setLayout( new GridBagLayout() );
            GridBagConstraints constraints = new GridBagConstraints();

            // Create the radio buttons.
            _carbon14RadioButton = new JRadioButton();
            _uranium238RadioButton = new JRadioButton();
            
            // Register for button presses.
            _carbon14RadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                	_alphaDecayModel.setNucleusType(NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM);
                }
            });
            _uranium238RadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                	_alphaDecayModel.setNucleusType(NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM);
                }
            });

            
            // Group the radio buttons together logically and set initial state.
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _carbon14RadioButton );
            buttonGroup.add( _uranium238RadioButton );
            _carbon14RadioButton.setSelected( true );
            
            //--------------------------------------------------------------------
            // Add the various components to the panel.
            //--------------------------------------------------------------------
            
            // Add the Polonium radio button.
            constraints.anchor = GridBagConstraints.EAST;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.ipadx = 25;
            add( _carbon14RadioButton, constraints );
            constraints.ipadx = 0; // Remove padding.
            
            // Create and add the Carbon 14 image.
            PNode labeledCarbonNucleus = new LabeledNucleusNode(NuclearPhysicsConstants.CARBON_COLOR,
                    NuclearPhysicsStrings.CARBON_14_ISOTOPE_NUMBER, 
                    NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.CARBON_14_LABEL_COLOR );
            Image carbonImage = labeledCarbonNucleus.toImage();
            ImageIcon carbonIconImage = new ImageIcon(carbonImage);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = 0;
            add( new JLabel(carbonIconImage), constraints );
            
            // Create and add the textual label for the Carbon nucleus.
            JLabel carbonLabel = new JLabel( NuclearPhysicsStrings.CARBON_14_LEGEND_LABEL ) ;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = 0;
            add( carbonLabel, constraints );
            
            // Create and add the arrow that signifies decay.
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridx = 1;
            constraints.gridy = 1;
            add( new JLabel( createArrowIcon( Color.BLACK ) ), constraints );
            
            // Create and add Lead image.
            PNode labeledLeadNucleus = new LabeledNucleusNode("Polonium Nucleus Small.png",
                    NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER, 
                    NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL,
                    NuclearPhysicsConstants.LEAD_LABEL_COLOR );
            Image leadImage = labeledLeadNucleus.toImage();
            ImageIcon leadIconImage = new ImageIcon( leadImage );
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = 2;
            add( new JLabel(leadIconImage), constraints );
            
            // Create and add the textual label for the Lead nucleus.
            JLabel leadLabel = new JLabel( NuclearPhysicsStrings.LEAD_LEGEND_LABEL ) ;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = 2;
            add( leadLabel, constraints );
            
            // Create spacing between the two main selections.
            constraints.gridx = 1;
            constraints.gridy = 3;
            add( createVerticalSpacingPanel( 20 ), constraints );
            
            // Add the custom nucleus radio button.
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 0;
            constraints.gridy = 4;
            add( _uranium238RadioButton, constraints  );
            
            // Create and add the icon for the non-decayed custom nucleus.
            PNode labeledCustomNucleus = new LabeledNucleusNode("Polonium Nucleus Small.png", "",
                    NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR );
            Image customNucleusImage = labeledCustomNucleus.toImage();
            ImageIcon customNucleusIconImage = new ImageIcon(customNucleusImage);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = 4;
            add( new JLabel(customNucleusIconImage), constraints );
            
            // Create and add the textual label for the non-decayed custom nucleus.
            JLabel customNucleusLabel = new JLabel( NuclearPhysicsStrings.CUSTOM_NUCLEUS_LEGEND_LABEL ) ;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = 4;
            add( customNucleusLabel, constraints );
            
            // Create and add the arrow that signifies decay.
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridx = 1;
            constraints.gridy = 5;
            add( new JLabel(createArrowIcon(Color.BLACK)), constraints );
            
            // Create and add the icon for the decayed custom nucleus.
            PNode labeledDecayedCustomNucleus = new LabeledNucleusNode("Polonium Nucleus Small.png", "",
                    NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL, 
                    NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR );
            Image decayedCustomNucleusImage = labeledDecayedCustomNucleus.toImage();
            ImageIcon decayedCustomNucleusIconImage = new ImageIcon(decayedCustomNucleusImage);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = 6;
            add( new JLabel(decayedCustomNucleusIconImage), constraints );
            
            // Create and add the textual label for the decayed custom nucleus.
            JLabel decayedCustomNucleusLabel = new JLabel( NuclearPhysicsStrings.DECAYED_CUSTOM_NUCLEUS_LEGEND_LABEL ) ;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = 6;
            add( decayedCustomNucleusLabel, constraints );
        }
        
        /**
         * Update the state of the buttons based on the values in the model.  This
         * is generally used when something other than this panel has caused a
         * change in the model.
         */
        public void updateButtonState(){
        	if (_alphaDecayModel.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM){
        		_carbon14RadioButton.setSelected(true);
        	}
        	else if (_alphaDecayModel.getNucleusType() == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
        		_uranium238RadioButton.setSelected(true);
        	}
        	else{
        		System.err.println("Error: Unrecognized nucleus type.");
        		
        	}
        }
        
        private ImageIcon createArrowIcon( Color color ) {
            ArrowNode arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(0, 15), 8, 14, 6 );
            arrowNode.setPaint( color );
            Image arrowImage = arrowNode.toImage();
            return( new ImageIcon(arrowImage)) ;
        }
        
        private JPanel createVerticalSpacingPanel(int space){
            JPanel spacePanel = new JPanel();
            spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
            spacePanel.add( Box.createVerticalStrut( space ) );
            return spacePanel;
        }
    }
}
