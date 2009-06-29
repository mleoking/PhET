/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;

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
    
    private RadiometricMeasurementModel _model;
    
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
        JLabel controlPanelTitle = new JLabel(NuclearPhysicsStrings.MEASUREMENT_CONTROL_PANEL_TITLE);
        controlPanelTitle.setFont(new PhetFont(20, true));
        titlePanel.add(controlPanelTitle);
        addControlFullWidth(titlePanel);
        
        // Insert some spacing.
        addControlFullWidth(createVerticalSpacingPanel(20));
        
        // Create the tree selection.
        RadioButtonWithIcon treeSelector = new RadioButtonWithIcon( NuclearPhysicsStrings.TREE_LABEL, "tree_1.png" );
        addControlFullWidth( treeSelector );
        treeSelector.getButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				_model.setSimulationMode(RadiometricMeasurementModel.SIMULATION_MODE.TREE);
			}
        });

        // Insert some spacing.
        addControlFullWidth(createVerticalSpacingPanel(10));
        
        // Create the rock selection.
        RadioButtonWithIcon rockSelector = new RadioButtonWithIcon( NuclearPhysicsStrings.ROCK_LABEL, "rock_1.png" );
        addControlFullWidth( rockSelector );
        rockSelector.getButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				_model.setSimulationMode(RadiometricMeasurementModel.SIMULATION_MODE.ROCK);
			}
        });
        
        // Put the radio buttons in a group together.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( treeSelector.getButton() );
        buttonGroup.add( rockSelector.getButton() );
        treeSelector.getButton().setSelected( true );
    }
    
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
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
            
            // Add a listener to the image that essentially makes it so that
            // clicking on the image is the same as clicking on the button.
            iconImageLabel.addMouseListener( new MouseAdapter(){
				public void mouseReleased(MouseEvent e) {
					_button.setSelected(true);
				}
            });
    	}
    	
    	public JRadioButton getButton(){
    		return _button;
    	}
    }
}