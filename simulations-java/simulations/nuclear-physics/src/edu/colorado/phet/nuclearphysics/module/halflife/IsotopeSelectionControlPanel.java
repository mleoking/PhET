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
import java.awt.event.MouseAdapter;
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
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.NucleusTypeControl;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusSphereNode;
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
    
    private IsotopeSelectionPanel _selectionPanel;
    private NucleusTypeControl _model;
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
    public IsotopeSelectionControlPanel( PiccoloModule piccoloModule, Frame parentFrame, 
    		NucleusTypeControl model, boolean customNucleusEnabled ) {
        
    	_model = model;
    	
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panel
        _selectionPanel = new IsotopeSelectionPanel( customNucleusEnabled );
        
        // Add the selection panel.
        addControlFullWidth( _selectionPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( piccoloModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class IsotopeSelectionPanel extends JPanel {
    	
        //------------------------------------------------------------------------
        // Class Data
        //------------------------------------------------------------------------
        
        //------------------------------------------------------------------------
        // Instance Data
        //------------------------------------------------------------------------
        
        private JRadioButton _carbon14RadioButton;
        private JRadioButton _uranium238RadioButton;
        private JRadioButton _customNucleusRadioButton;

        //------------------------------------------------------------------------
        // Constructor
        //------------------------------------------------------------------------
        
        public IsotopeSelectionPanel( boolean customNucleusSelectionEnabled ) {
            
        	// Register for notifications of nucleus type changes.
        	_model.addListener(new NuclearDecayListenerAdapter(){
        		public void nucleusTypeChanged() {
        			if (_model.getNucleusType() == NucleusType.CARBON_14){
        				_carbon14RadioButton.setSelected(true);
        			}
        			else if (_model.getNucleusType() == NucleusType.URANIUM_238){
        				_uranium238RadioButton.setSelected(true);
        			}
        			else {
        				_customNucleusRadioButton.setSelected(true);
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
            
            // Set the layout.
            setLayout( new GridBagLayout() );

            // Create the radio buttons.
            _carbon14RadioButton = new JRadioButton();
            _uranium238RadioButton = new JRadioButton();
            _customNucleusRadioButton = new JRadioButton();
            
            // Register for button presses.
            _carbon14RadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                	_model.setNucleusType(NucleusType.CARBON_14);
                }
            });
            _uranium238RadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                	_model.setNucleusType(NucleusType.URANIUM_238);
                }
            });
            _customNucleusRadioButton.addActionListener( new ActionListener(){
                public void actionPerformed(ActionEvent event){
                	_model.setNucleusType(NucleusType.HEAVY_CUSTOM);
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
            
            addIsotopeSelection( _carbon14RadioButton, 
            		NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.CARBON_14), 
            		NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.NITROGEN_14) );
                        
            // Add the selection for U238->Lead 206
            
            addIsotopeSelection( _uranium238RadioButton, 
            		NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.URANIUM_238), 
            		NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.LEAD_206) );
            
            // Add the custom nucleus selection, but only if it is enabled.
            if ( customNucleusSelectionEnabled ){

                addIsotopeSelection( _customNucleusRadioButton, 
                		NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.HEAVY_CUSTOM), 
                		NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.HEAVY_CUSTOM_POST_DECAY) );
            }
        }
        
        /**
         * Update the state of the buttons based on the values in the model.  This
         * is generally used when something other than this panel has caused a
         * change in the model.
         */
        public void updateButtonState(){
        	if (_model.getNucleusType() == NucleusType.CARBON_14){
        		_carbon14RadioButton.setSelected(true);
        	}
        	else if (_model.getNucleusType() == NucleusType.URANIUM_238){
        		_uranium238RadioButton.setSelected(true);
        	}
        	else{
        		System.err.println("Error: Unrecognized nucleus type.");
        		
        	}
        }
        
        /**
         * Create a panel that shows a pre-decay and a post decay nucleus,
         * labels for each, and a radio button that allows the user to select
         * this type of decay.
         *  
         * @param button
         * @param preDecayNucleus
         * @param postDecayNucleus
         * @return
         */
        private int _isotopeSelectorCount = 0;
        private void addIsotopeSelection( final JRadioButton button, NucleusDisplayInfo preDecayNucleus,
        		NucleusDisplayInfo postDecayNucleus ){
        	
            GridBagConstraints constraints = new GridBagConstraints();
        	
            // Add the radio button.
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 0;
            constraints.gridy = _isotopeSelectorCount * 4;
            constraints.ipadx = 25;
            add( button, constraints );
            constraints.ipadx = 0; // Remove padding.
            
            // Create and add the pre-decay nucleus.  It is created as a
            // sphere and not a nucleus image.
            PNode labeledPreDecayNucleus = new LabeledNucleusSphereNode( preDecayNucleus );
            Image preDecayImage = labeledPreDecayNucleus.toImage();
            ImageIcon predecayIconImage = new ImageIcon(preDecayImage);
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = _isotopeSelectorCount * 4;
            constraints.ipadx = 10;
            JLabel predecayIcon = new JLabel(predecayIconImage);
            add( predecayIcon, constraints );
            constraints.ipadx = 0; // Remove the padding.
            
            // Add a handler so that if the user clicks on this nucleus image,
            // the corresponding nucleus type will be set.
            predecayIcon.addMouseListener( new MouseAdapter(){
            	public void mouseClicked(java.awt.event.MouseEvent arg0){
            		button.doClick();
            	}
            });
            
            // Create and add the textual label for the pre-decay nucleus.
            JLabel preDecayNucleusLabel = new JLabel( preDecayNucleus.getName() ) ;
            preDecayNucleusLabel.setFont( LABEL_FONT );
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = _isotopeSelectorCount * 4;
            add( preDecayNucleusLabel, constraints );
            
            // Create and add the arrow that signifies decay.
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridx = 1;
            constraints.gridy = _isotopeSelectorCount * 4 + 1;
            add( new JLabel( createArrowIcon( Color.BLACK ) ), constraints );
            
            // Create and add post-decay nucleus.
            PNode labeledPostDecayNucleus = new LabeledNucleusSphereNode( postDecayNucleus );
            Image postDecayNucleusImage = labeledPostDecayNucleus.toImage();
            ImageIcon postDecayIconImage = new ImageIcon( postDecayNucleusImage );
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 1;
            constraints.gridy = _isotopeSelectorCount * 4 + 2;
            constraints.ipadx = 10;
            JLabel postDecayIcon = new JLabel(postDecayIconImage); 
            add( postDecayIcon, constraints );
            constraints.ipadx = 0; // Remove the padding.
            
            // Add a handler so that if the user clicks on this nucleus image,
            // the corresponding nucleus type will be set.
            postDecayIcon.addMouseListener( new MouseAdapter(){
            	public void mouseClicked(java.awt.event.MouseEvent arg0){
            		button.doClick();
            	}
            });
            
            // Create and add the textual label for the post-decay nucleus.
            JLabel postDecayNucleusLabel = new JLabel( postDecayNucleus.getName() ) ;
            postDecayNucleusLabel.setFont( LABEL_FONT );
            constraints.anchor = GridBagConstraints.WEST;
            constraints.gridx = 2;
            constraints.gridy = _isotopeSelectorCount * 4 + 2;
            add( postDecayNucleusLabel, constraints );
            
            // Create spacing between the two main selections.
            constraints.gridx = 1;
            constraints.gridy = _isotopeSelectorCount * 4 + 3;
            add( createVerticalSpacingPanel( 20 ), constraints );
            
            _isotopeSelectorCount++;
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
