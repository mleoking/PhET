/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusImageType;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;

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
        
        // Create and add the title for the control panel.
        JPanel titlePanel = new JPanel();
        JLabel controlPanelTitle = new JLabel("Choose an Object");
        controlPanelTitle.setFont(new PhetFont(20, true));
        titlePanel.add(controlPanelTitle);
        addControlFullWidth(titlePanel);
        
        // Insert some spacing.
        addControlFullWidth(createVerticalSpacingPanel(20));
        
        // Create the tree selection.
        RadioButtonWithIcon treeSelector = new RadioButtonWithIcon( "Tree", "tree_1.png" );
        addControlFullWidth( treeSelector );

        // Insert some spacing.
        addControlFullWidth(createVerticalSpacingPanel(10));
        
        // Create the rock selection.
        RadioButtonWithIcon rockSelector = new RadioButtonWithIcon( "Rock", "rock_1.png" );
        addControlFullWidth( rockSelector );
        
        // Put the radio buttons in a group together.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( treeSelector.getButton() );
        buttonGroup.add( rockSelector.getButton() );
        treeSelector.getButton().setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class DatableItemSelectionPanel extends VerticalLayoutPanel {
    	
        //------------------------------------------------------------------------
        // Class Data
        //------------------------------------------------------------------------
    	
    	// Constant that controls the size (height) of the image icons used to
    	// represent the various selections.
    	private static final int IMAGE_ICON_HEIGHT = 70;  // In pixels.
        
        //------------------------------------------------------------------------
        // Instance Data
        //------------------------------------------------------------------------
        
        private JRadioButton _treeRadioButton;
        private JRadioButton _rockRadioButton;

        //------------------------------------------------------------------------
        // Constructor
        //------------------------------------------------------------------------
        
        public DatableItemSelectionPanel() {
        	
        	// Set the layout to a grid with 2 columns, one for the button and
        	// one for the image.
            
            // Create the radio buttons.
        	
//            _treeRadioButton = new JRadioButton("Tree", createIconFromImageName("tree_1.png"));
            _treeRadioButton = new JRadioButton("Tree");
            _treeRadioButton.setFont(LABEL_FONT);
            _treeRadioButton.setVerticalTextPosition(SwingConstants.CENTER);
            _treeRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);

            _rockRadioButton = new JRadioButton("Rock", createIconFromImageName("rock_1.png"));
            _rockRadioButton.setFont(LABEL_FONT);
            
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
            
            // Add the buttons to the panel.
            add(_treeRadioButton);
            add(_rockRadioButton);
            
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
        
        private ImageIcon createIconFromImageName( String imageName ){
            BufferedImage image = NuclearPhysicsResources.getImage( imageName );
            double scaleFactor = (double)((double)IMAGE_ICON_HEIGHT / (double)(image.getHeight()));
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            return new ImageIcon( image );
        }
    }
    
    
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
    
    /**
     * This class combines an icon and a radio button on to a panel in the way
     * that is needed for this simulation.
     */
    private static class RadioButtonWithIcon extends HorizontalLayoutPanel {
    	
    	// Constant that controls the size (height) of the image icons used to
    	// represent the various selections.
    	private static final int IMAGE_ICON_HEIGHT = 90;  // In pixels.

    	// Font to use for labels.
    	private static final Font LABEL_FONT = new PhetFont(18);
    	
    	private JRadioButton _button;

    	/**
    	 * Constructor.
    	 * 
    	 * @param text
    	 * @param imageName
    	 */
    	public RadioButtonWithIcon(String text, String imageName){
    		
    		// Create and add the button.
    		_button = new JRadioButton(text);
    		_button.setFont(LABEL_FONT);
    		add(_button);
    		
    		// Create and add the image.
            BufferedImage image = NuclearPhysicsResources.getImage( imageName );
            double scaleFactor = (double)((double)IMAGE_ICON_HEIGHT / (double)(image.getHeight()));
            image = BufferedImageUtils.rescaleFractional( image, scaleFactor, scaleFactor );
            ImageIcon imageIcon = new ImageIcon( image );
            JLabel iconImageLabel = new JLabel( imageIcon );
            add( iconImageLabel );
    	}
    	
    	public JRadioButton getButton(){
    		return _button;
    	}
    }
}