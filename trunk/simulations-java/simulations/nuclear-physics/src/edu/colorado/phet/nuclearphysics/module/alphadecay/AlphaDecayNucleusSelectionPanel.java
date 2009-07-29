/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay;

import java.awt.Color;
import java.awt.Font;
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

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusImageNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class displays a panel that allows the user to select between
 * different types of atomic nuclei for demonstrating Alpha Decay.
 *
 * @author John Blanco
 */
public class AlphaDecayNucleusSelectionPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private NucleusTypeControl _alphaDecayModel;
    private JRadioButton _poloniumRadioButton;
    private JRadioButton _customNucleusRadioButton;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaDecayNucleusSelectionPanel(NucleusTypeControl alphaDecayModel) {
        
    	_alphaDecayModel = alphaDecayModel;
    	
    	// Register for notifications of nucleus type changes.
    	alphaDecayModel.addListener(new NuclearDecayListenerAdapter(){
    		public void nucleusTypeChanged() {
    			if (_alphaDecayModel.getNucleusType() == NucleusType.HEAVY_CUSTOM){
    				_customNucleusRadioButton.setSelected(true);
    			}
    			else{
    				_poloniumRadioButton.setSelected(true);
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
        _poloniumRadioButton = new JRadioButton();
        _customNucleusRadioButton = new JRadioButton();
        
        // Register for button presses.
        _poloniumRadioButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	_alphaDecayModel.setNucleusType(NucleusType.POLONIUM_211);
            }
        });
        _customNucleusRadioButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	_alphaDecayModel.setNucleusType(NucleusType.HEAVY_CUSTOM);
            }
        });

        
        // Group the radio buttons together logically and set initial state.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _poloniumRadioButton );
        buttonGroup.add( _customNucleusRadioButton );
        _poloniumRadioButton.setSelected( true );
        
        //--------------------------------------------------------------------
        // Add the various components to the panel.
        //--------------------------------------------------------------------
        
        // Add the Polonium radio button.
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 25;
        add( _poloniumRadioButton, constraints );
        constraints.ipadx = 0; // Remove padding.
        
        // Create and add the Polonium image.
        PNode labeledPoloniumNucleus = new LabeledNucleusImageNode("Polonium Nucleus Small.png",
                NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
                NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
        Image poloniumImage = labeledPoloniumNucleus.toImage();
        ImageIcon poloniumIconImage = new ImageIcon(poloniumImage);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 0;
        add( new JLabel(poloniumIconImage), constraints );
        
        // Create and add the textual label for the Polonium nucleus.
        JLabel poloniumLabel = new JLabel( NuclearPhysicsStrings.POLONIUM_211_LEGEND_LABEL ) ;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 2;
        constraints.gridy = 0;
        add( poloniumLabel, constraints );
        
        // Create and add the arrow that signifies decay.
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 1;
        constraints.gridy = 1;
        add( new JLabel(createArrowIcon(Color.BLACK)), constraints );
        
        // Create and add Lead image.
        PNode labeledLeadNucleus = new LabeledNucleusImageNode("Polonium Nucleus Small.png",
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
        JLabel leadLabel = new JLabel( NuclearPhysicsStrings.LEAD_207_LEGEND_LABEL ) ;
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
        add( _customNucleusRadioButton, constraints  );
        
        // Create and add the icon for the non-decayed custom nucleus.
        PNode labeledCustomNucleus = new LabeledNucleusImageNode("Polonium Nucleus Small.png", "",
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
        PNode labeledDecayedCustomNucleus = new LabeledNucleusImageNode("Polonium Nucleus Small.png", "",
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
    	if (_alphaDecayModel.getNucleusType() == NucleusType.POLONIUM_211){
    		_poloniumRadioButton.setSelected(true);
    	}
    	else if (_alphaDecayModel.getNucleusType() == NucleusType.HEAVY_CUSTOM){
    		_customNucleusRadioButton.setSelected(true);
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
