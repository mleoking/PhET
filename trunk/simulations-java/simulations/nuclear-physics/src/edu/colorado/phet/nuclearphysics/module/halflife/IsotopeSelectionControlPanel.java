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
import java.util.ArrayList;

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
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusNode;
import edu.colorado.phet.nuclearphysics.view.NucleusImageFactory;
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
        _selectionPanel = new NucleusSelectionPanel( true );
        
        // Add the selection panel.
        addControlFullWidth( _selectionPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( piccoloModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class NucleusSelectionPanel extends VerticalLayoutPanel {
    	
        //------------------------------------------------------------------------
        // Class Data
        //------------------------------------------------------------------------
        
        //------------------------------------------------------------------------
        // Instance Data
        //------------------------------------------------------------------------
        
        private NucleusTypeControl _alphaDecayModel;
        private JRadioButton _carbon14RadioButton;
        private JRadioButton _uranium238RadioButton;
        private JRadioButton _customNucleusRadioButton;

        //------------------------------------------------------------------------
        // Constructor
        //------------------------------------------------------------------------
        
        public NucleusSelectionPanel( boolean customNucleusSelectionEnabled ) {
            
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
        	
            // Add the border around the panel.
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    NuclearPhysicsStrings.ISOTOPE_SELECTION_BORDER_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );
            
            // Create the radio buttons.
            _carbon14RadioButton = new JRadioButton();
            _uranium238RadioButton = new JRadioButton();
            _customNucleusRadioButton = new JRadioButton();
            
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
            _customNucleusRadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                	_alphaDecayModel.setNucleusType(NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM);
                }
            });

            
            // Group the radio buttons together logically and set initial state.
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _carbon14RadioButton );
            buttonGroup.add( _uranium238RadioButton );
            buttonGroup.add( _customNucleusRadioButton );
            _carbon14RadioButton.setSelected( true );
            
            //--------------------------------------------------------------------
            // Add the isotope selection options to the panel.
            //--------------------------------------------------------------------
            
            // Add the first isotope selection option.
            
            NucleusSelectionDescriptor carbon14Descriptor = new NucleusSelectionDescriptor(
            		AtomicNucleusImageType.GRADIENT_SPHERE,
            		NuclearPhysicsStrings.CARBON_14_ISOTOPE_NUMBER,
            		NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL,
            		NuclearPhysicsConstants.CARBON_14_LABEL_COLOR,
            		NuclearPhysicsConstants.CARBON_COLOR,
            		NuclearPhysicsStrings.CARBON_14_LEGEND_LABEL );

            NucleusSelectionDescriptor nitrogen14Descriptor = new NucleusSelectionDescriptor(
            		AtomicNucleusImageType.GRADIENT_SPHERE,
            		NuclearPhysicsStrings.NITROGEN_14_ISOTOPE_NUMBER,
            		NuclearPhysicsStrings.NITROGEN_14_CHEMICAL_SYMBOL,
            		NuclearPhysicsConstants.NITROGEN_14_LABEL_COLOR,
            		NuclearPhysicsConstants.NITROGEN_COLOR,
            		NuclearPhysicsStrings.NITROGEN_14_LEGEND_LABEL );
            
            add( createIsotopeSelection( _carbon14RadioButton, carbon14Descriptor, nitrogen14Descriptor ) );
                        
            // Create spacing between selection panels.
            add( createVerticalSpacingPanel( 20 ) );

            // Add the selection for U238->Lead 206
            
            NucleusSelectionDescriptor uranium238Descriptor = new NucleusSelectionDescriptor(
            		AtomicNucleusImageType.GRADIENT_SPHERE,
            		NuclearPhysicsStrings.URANIUM_238_ISOTOPE_NUMBER,
            		NuclearPhysicsStrings.URANIUM_238_CHEMICAL_SYMBOL,
            		NuclearPhysicsConstants.URANIUM_238_LABEL_COLOR,
            		NuclearPhysicsConstants.URANIUM_238_COLOR,
            		NuclearPhysicsStrings.URANIUM_238_LEGEND_LABEL );

            NucleusSelectionDescriptor lead206Descriptor = new NucleusSelectionDescriptor(
            		AtomicNucleusImageType.GRADIENT_SPHERE,
            		NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER,
            		NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL,
            		NuclearPhysicsConstants.LEAD_LABEL_COLOR,
            		NuclearPhysicsConstants.LEAD_206_COLOR,
            		NuclearPhysicsStrings.LEAD_206_LEGEND_LABEL );
            
            add( createIsotopeSelection( _uranium238RadioButton, uranium238Descriptor, lead206Descriptor ) );
            
            // Add the custom nucleus selection, but only if it is enabled.
            if ( customNucleusSelectionEnabled ){

            	// Create spacing between selection panels.
                add( createVerticalSpacingPanel( 20 ) );

                // Add the selection for U238->Lead 206
                
                NucleusSelectionDescriptor preDecayCustomNucleusDescriptor = new NucleusSelectionDescriptor(
                		AtomicNucleusImageType.GRADIENT_SPHERE,
                		"",  // Custom nucleus has no isotope number.
                		NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL,
                		NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR,
                		NuclearPhysicsConstants.CUSTOM_NUCLEUS_PRE_DECAY_COLOR,
                		NuclearPhysicsStrings.CUSTOM_NUCLEUS_LEGEND_LABEL );

                NucleusSelectionDescriptor postDecayCustomNucleusDescriptor = new NucleusSelectionDescriptor(
                		AtomicNucleusImageType.GRADIENT_SPHERE,
                		"",  // Custom nucleus has no isotope number.
                		NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL,
                		NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR,
                		NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR,
                		NuclearPhysicsStrings.CUSTOM_NUCLEUS_LEGEND_LABEL );
                
                add( createIsotopeSelection( _customNucleusRadioButton, preDecayCustomNucleusDescriptor, 
                		postDecayCustomNucleusDescriptor ) );                        

            }
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
        
        private JPanel createIsotopeSelection( JRadioButton button, NucleusSelectionDescriptor preDecayNucleus,
        		NucleusSelectionDescriptor postDecayNucleus ){
        	
        	JPanel isotopePanel = new JPanel();
            isotopePanel.setLayout( new GridBagLayout() );
            GridBagConstraints constraints = new GridBagConstraints();
        	
            // Add the radio button.
            constraints.anchor = GridBagConstraints.EAST;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.ipadx = 25;
            isotopePanel.add( button, constraints );
            constraints.ipadx = 0; // Remove padding.
            
            // Create and add the pre-decay nucleus.  It is created as a
            // sphere and not a nucleus image.
            PNode labeledPreDecayNucleus = new LabeledNucleusNode(
            		preDecayNucleus.getSphereColor(),
                    preDecayNucleus.getIsotopeNumberString(), 
                    preDecayNucleus.getChemicalSymbol(), 
                    preDecayNucleus.getLabelColor() );
            Image preDecayImage = labeledPreDecayNucleus.toImage();
            ImageIcon predecayIconImage = new ImageIcon(preDecayImage);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = 0;
            isotopePanel.add( new JLabel(predecayIconImage), constraints );
            
            // Create and add the textual label for the pre-decay nucleus.
            JLabel preDecayNucleusLabel = new JLabel( preDecayNucleus.getLegendLabel() ) ;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = 0;
            isotopePanel.add( preDecayNucleusLabel, constraints );
            
            // Create and add the arrow that signifies decay.
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridx = 1;
            constraints.gridy = 1;
            isotopePanel.add( new JLabel( createArrowIcon( Color.BLACK ) ), constraints );
            
            // Create and add post-decay nucleus.
            PNode labeledPostDecayNucleus = new LabeledNucleusNode(
            		postDecayNucleus.getSphereColor(),
            		postDecayNucleus.getIsotopeNumberString(), 
            		postDecayNucleus.getChemicalSymbol(), 
            		postDecayNucleus.getLabelColor() );
            Image postDecayNucleusImage = labeledPostDecayNucleus.toImage();
            ImageIcon postDecayIconImage = new ImageIcon( postDecayNucleusImage );
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = 2;
            isotopePanel.add( new JLabel(postDecayIconImage), constraints );
            
            // Create and add the textual label for the Nitrogen nucleus.
            JLabel postDecayNucleusLabel = new JLabel( postDecayNucleus.getLegendLabel() ) ;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = 2;
            isotopePanel.add( postDecayNucleusLabel, constraints );
            
        	return isotopePanel;
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
