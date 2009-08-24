/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusImageNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class displays a panel that allows the user to select between
 * different types of atomic nuclei for demonstrating Beta Decay.
 *
 * @author John Blanco
 */
public class BetaDecayNucleusSelectionPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private NucleusTypeControl _betaDecayModel;
    private JRadioButton _hydrogenRadioButton;
    private JRadioButton _carbonRadioButton;
    private JRadioButton _customNucleusRadioButton;
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public BetaDecayNucleusSelectionPanel(NucleusTypeControl betaDecayModel) {
        
    	_betaDecayModel = betaDecayModel;
    	
    	// Register for notifications of nucleus type changes.
    	betaDecayModel.addListener(new NuclearDecayListenerAdapter(){
    		public void nucleusTypeChanged() {
    			updateButtonState();
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
        _hydrogenRadioButton = new JRadioButton();
        _carbonRadioButton = new JRadioButton();
        _customNucleusRadioButton = new JRadioButton();
        
        // Register for button presses.
        _hydrogenRadioButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	_betaDecayModel.setNucleusType(NucleusType.HYDROGEN_3);
            }
        });
        _carbonRadioButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	_betaDecayModel.setNucleusType(NucleusType.CARBON_14);
            }
        });

        _customNucleusRadioButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            	_betaDecayModel.setNucleusType(NucleusType.LIGHT_CUSTOM);
            }
        });

        // Group the radio buttons together logically and set initial state.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _hydrogenRadioButton );
        buttonGroup.add( _carbonRadioButton );
        buttonGroup.add( _customNucleusRadioButton );
        _hydrogenRadioButton.setSelected( true );
        
        //--------------------------------------------------------------------
        // Add the various components to the panel.
        //--------------------------------------------------------------------
        NucleusDisplayInfo nucleusDisplayInfo;
        
        // Add the Hydrogen radio button.
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 25;
        add( _hydrogenRadioButton, constraints );
        constraints.ipadx = 0; // Remove padding.
        
        // Create and add the Hydrogen image.
        PNode labeledHydrogen3Nucleus = new LabeledNucleusImageNode( NucleusType.HYDROGEN_3 );
        labeledHydrogen3Nucleus.scale(0.75); // Reduce the size a bit so it looks okay with other atoms.
        Image hydrogenImage = labeledHydrogen3Nucleus.toImage();
        ImageIcon hydrogen3IconImage = new ImageIcon(hydrogenImage);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 0;
        JLabel hydrogen3IconJLabel = new JLabel(hydrogen3IconImage);
        add( hydrogen3IconJLabel, constraints );
        
        // Add a handler so that if the user clicks on this nucleus image,
        // the corresponding nucleus type will be set.
        hydrogen3IconJLabel.addMouseListener( new MouseAdapter(){
        	public void mouseClicked(java.awt.event.MouseEvent arg0){
        		_hydrogenRadioButton.doClick();
        	}
        });
        
        // Create and add the textual label for the Hydrogen nucleus.
        nucleusDisplayInfo = NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.HYDROGEN_3);
        JLabel hydrogen3Label = new JLabel( nucleusDisplayInfo.getName(), JLabel.CENTER ) ;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.ipadx = 25;
        add( hydrogen3Label, constraints );
        constraints.ipadx = 0;
        
        // Create and add the arrow that signifies decay.
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 1;
        constraints.gridy = 1;
        add( new JLabel(createArrowIcon(Color.BLACK)), constraints );
        
        // Create and add Helium image.
        PNode labeledHeliumNucleus = new LabeledNucleusImageNode( NucleusType.HELIUM_3 );
        labeledHeliumNucleus.scale(0.75); // Reduce the size a bit so it looks okay with other atoms.
        Image heliumImage = labeledHeliumNucleus.toImage();
        ImageIcon heliumIconImage = new ImageIcon( heliumImage );
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 2;
        JLabel helium3IconJLabel = new JLabel(heliumIconImage);
        add( helium3IconJLabel, constraints );
        
        // Add a handler so that if the user clicks on this nucleus image,
        // the corresponding nucleus type will be set.
        helium3IconJLabel.addMouseListener( new MouseAdapter(){
        	public void mouseClicked(java.awt.event.MouseEvent arg0){
        		_hydrogenRadioButton.doClick();
        	}
        });
        
        // Create and add the textual label for the helium nucleus.
        nucleusDisplayInfo = NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.HELIUM_3);
        JLabel heliumLabel = new JLabel( nucleusDisplayInfo.getName() ) ;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 2;
        add( heliumLabel, constraints );
        
        // Create spacing between this and the next selection.
        constraints.gridx = 1;
        constraints.gridy = 3;
        add( createVerticalSpacingPanel( 20 ), constraints );
        
        // Add the carbon-14 radio button.
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.ipadx = 25;
        add( _carbonRadioButton, constraints );
        constraints.ipadx = 0; // Remove padding.
        
        // Create and add the carbon-14 image.
        PNode labeledCarbon14Nucleus = new LabeledNucleusImageNode( NucleusType.CARBON_14 );
        Image carbonImage = labeledCarbon14Nucleus.toImage();
        ImageIcon carbon14IconImage = new ImageIcon(carbonImage);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 4;
        JLabel carbon14IconJLabel = new JLabel(carbon14IconImage);
        add( carbon14IconJLabel, constraints );
        
        // Add a handler so that if the user clicks on this nucleus image,
        // the corresponding nucleus type will be set.
        carbon14IconJLabel.addMouseListener( new MouseAdapter(){
        	public void mouseClicked(java.awt.event.MouseEvent arg0){
        		_carbonRadioButton.doClick();
        	}
        });
        
        // Create and add the textual label for the carbon-14 nucleus.
        nucleusDisplayInfo = NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.CARBON_14);
        JLabel carbon14Label = new JLabel( nucleusDisplayInfo.getName() ) ;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 4;
        add( carbon14Label, constraints );
        
        // Create and add the arrow that signifies decay.
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 1;
        constraints.gridy = 5;
        add( new JLabel(createArrowIcon(Color.BLACK)), constraints );
        
        // Create and add nitrogen-14 image.
        PNode labeledNitrogenNucleus = new LabeledNucleusImageNode( NucleusType.NITROGEN_14 );
        Image nitrogenImage = labeledNitrogenNucleus.toImage();
        ImageIcon nitrogenIconImage = new ImageIcon( nitrogenImage );
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 6;
        JLabel nitrogen14IconJLabel = new JLabel(nitrogenIconImage);
        add( nitrogen14IconJLabel, constraints );
        
        // Add a handler so that if the user clicks on this nucleus image,
        // the corresponding nucleus type will be set.
        nitrogen14IconJLabel.addMouseListener( new MouseAdapter(){
        	public void mouseClicked(java.awt.event.MouseEvent arg0){
        		_carbonRadioButton.doClick();
        	}
        });
        
        // Create and add the textual label for the nitrogen nucleus.
        nucleusDisplayInfo = NucleusDisplayInfo.getDisplayInfoForNucleusType(NucleusType.NITROGEN_14);
        JLabel nitrogenLabel = new JLabel( nucleusDisplayInfo.getName() ) ;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 6;
        add( nitrogenLabel, constraints );
        
        // Create spacing between this and the next selection.
        constraints.gridx = 1;
        constraints.gridy = 7;
        add( createVerticalSpacingPanel( 20 ), constraints );
        
        // Add the custom nucleus radio button.
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 8;
        add( _customNucleusRadioButton, constraints  );
        
        // Create and add the icon for the non-decayed custom nucleus.
        PNode labeledCustomNucleus = new LabeledNucleusImageNode( NucleusType.LIGHT_CUSTOM );
        Image customNucleusImage = labeledCustomNucleus.toImage();
        ImageIcon customNucleusIconImage = new ImageIcon(customNucleusImage);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 8;
        JLabel customNucleusIconJLabel = new JLabel(customNucleusIconImage);
        add( customNucleusIconJLabel, constraints );
        
        // Add a handler so that if the user clicks on this nucleus image,
        // the corresponding nucleus type will be set.
        customNucleusIconJLabel.addMouseListener( new MouseAdapter(){
        	public void mouseClicked(java.awt.event.MouseEvent arg0){
        		_customNucleusRadioButton.doClick();
        	}
        });
        
        // Create and add the textual label for the non-decayed custom nucleus.
        JLabel customNucleusLabel = new JLabel( NuclearPhysicsStrings.CUSTOM_NUCLEUS_LEGEND_LABEL ) ;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 8;
        add( customNucleusLabel, constraints );
        
        // Create and add the arrow that signifies decay.
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 1;
        constraints.gridy = 9;
        add( new JLabel(createArrowIcon(Color.BLACK)), constraints );
        
        // Create and add the icon for the decayed custom nucleus.
        PNode labeledDecayedCustomNucleus = new LabeledNucleusImageNode( NucleusType.LIGHT_CUSTOM_POST_DECAY );
        Image decayedCustomNucleusImage = labeledDecayedCustomNucleus.toImage();
        ImageIcon decayedCustomNucleusIconImage = new ImageIcon(decayedCustomNucleusImage);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 10;
        JLabel decayedCustomNucleusIconJLabel = new JLabel(decayedCustomNucleusIconImage);
        add( decayedCustomNucleusIconJLabel, constraints );
        
        // Add a handler so that if the user clicks on this nucleus image,
        // the corresponding nucleus type will be set.
        decayedCustomNucleusIconJLabel.addMouseListener( new MouseAdapter(){
        	public void mouseClicked(java.awt.event.MouseEvent arg0){
        		_customNucleusRadioButton.doClick();
        	}
        });
        
        // Create and add the textual label for the decayed custom nucleus.
        JLabel decayedCustomNucleusLabel = new JLabel( NuclearPhysicsStrings.DECAYED_CUSTOM_NUCLEUS_LEGEND_LABEL ) ;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 10;
        add( decayedCustomNucleusLabel, constraints );
        
    }
    
    /**
     * Update the state of the buttons based on the values in the model.  This
     * is generally used when something other than this panel has caused a
     * change in the model.
     */
    public void updateButtonState(){
    	if (_betaDecayModel.getNucleusType() == NucleusType.HYDROGEN_3){
    		_hydrogenRadioButton.setSelected(true);
    	}
    	else if (_betaDecayModel.getNucleusType() == NucleusType.CARBON_14){
    		_carbonRadioButton.setSelected(true);
    	}
    	else if (_betaDecayModel.getNucleusType() == NucleusType.LIGHT_CUSTOM){
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
