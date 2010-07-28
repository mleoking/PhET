/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.IntensitySlider;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel.PhotonTarget;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PNode that represents a flashlight in the view.  This node is set up such
 * that setting its offset based on the photon emission point in the model
 * should position it correctly.  This makes some assumptions about the
 * direction of photon emission.
 * 
 * @author John Blanco
 */
public class PhotonEmitterNode extends PNode {
	
    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

	private static final Font LABEL_FONT = new PhetFont(24);
	
	private static int SLIDER_RANGE = 100;
	
	enum ImageType { FLASHLIGHT, HEAT_LAMP };
	
	// ------------------------------------------------------------------------
	// Instance Data
	// ------------------------------------------------------------------------
	
	private PImage photonEmitterImage;
	private PhotonAbsorptionModel model;
	private PNode emitterImageLayer;
	private PNode emissionControlButtonLayer;
	private double emitterImageWidth;

    private JRadioButton infraredPhotonRadioButton;
    private JRadioButton visiblePhotonRadioButton;
    private IntensitySlider intensitySlider;
    
    // These two images are laid atop one another to form the button that is
    // used to turn photon emission on and off.
    private PImage unpressedButtonImage;
    private PImage pressedButtonImage;
	
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
	/**
	 * Constructor.
	 * 
	 * @param imageWidth - Width of the emitter image in screen coords.  The
	 * height will be based on the aspect ratio of the image.  The size of
	 * the control box is independent of this - its size is based on the
	 * strings that define the user selections.
	 * @param mvt - Model-view transform for translating between model and
	 * view coordinate systems.
	 * @param model
	 */
	public PhotonEmitterNode(double imageWidth, ModelViewTransform2D mvt, final PhotonAbsorptionModel model) {
		
		this.model = model;
		this.emitterImageWidth = imageWidth;
		
		// Listen to the model for events that may cause this node to change
		// state.
		model.addListener( new PhotonAbsorptionModel.Adapter() {

            @Override
            public void emittedPhotonWavelengthChanged() {
                updateFrequencySelectButtons();
                updateImage( emitterImageWidth );
                updateIntensitySlider();
            }

            @Override
            public void periodicPhotonEmissionEnabledChanged() {
                updatePhotonEmissionControlButton();
                updateIntensitySlider();
            }

            @Override
            public void photonEmissionPeriodChanged() {
                updateIntensitySlider();
            }
		});
		
		// Create the layers on which the other nodes will be placed.
		PNode everythingElseLayer = new PNode();
		addChild( everythingElseLayer );
		emitterImageLayer = new PNode();
		addChild( emitterImageLayer );
		emissionControlButtonLayer = new PNode();
		addChild( emissionControlButtonLayer );
		
		// Add the initial image.
		updateImage( emitterImageWidth );
		
		// Add the images that comprise the button that will turn photon
		// emission on and off.  These will be positioned by the corresponding
		// update method.
        pressedButtonImage = new PImage(GreenhouseResources.getImage("emitterOnButton.png"));
        pressedButtonImage.addInputEventListener(new CursorHandler());
        pressedButtonImage.addInputEventListener(new PBasicInputEventHandler(){
            @Override
            public void mousePressed( PInputEvent event ) {
                model.setPeriodicPhotonEmissionEnabled( false );
            }
        });
        emissionControlButtonLayer.addChild(pressedButtonImage);
        
        unpressedButtonImage = new PImage(GreenhouseResources.getImage("emitterOffButton.png"));
        unpressedButtonImage.addInputEventListener(new CursorHandler());
        unpressedButtonImage.addInputEventListener(new PBasicInputEventHandler(){
            @Override
            public void mousePressed( PInputEvent event ) {
                model.setPeriodicPhotonEmissionEnabled( true );
            }
        });
        emissionControlButtonLayer.addChild(unpressedButtonImage);
        
        // Do the initial update of the emission control button's position and
        // state.
        updatePhotonEmissionControlButton();
		
		// Calculate the vertical distance between the center of the
		// emitter image and the control box.  This is a function of the
		// flashlight width.  The multiplier is arbitrary and can be adjusted
		// as desired.
		double distanceBetweeImageAndControl = emitterImageWidth * 0.6;
		
		// Create the control box for selecting the type of photon that will
		// be emitted.
		JPanel emissionTypeSelectionPanel = new VerticalLayoutPanel();
		emissionTypeSelectionPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		JPanel infraredButtonPanel = new HorizontalLayoutPanel();
		// TODO: i18n
        infraredPhotonRadioButton = new JRadioButton("Infrared");
        infraredPhotonRadioButton.setFont(LABEL_FONT);
        infraredPhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
                updateFrequencySelectButtons();
            }
        });
        infraredButtonPanel.add( infraredPhotonRadioButton );
        infraredButtonPanel.add( new JLabel(new ImageIcon( GreenhouseResources.getImage( "photon-660.png" ) ) ) );
        emissionTypeSelectionPanel.add(infraredButtonPanel);
        
        JPanel visibleButtonPanel = new HorizontalLayoutPanel();
        // TODO: i18n
        visiblePhotonRadioButton = new JRadioButton("Visible");
        visiblePhotonRadioButton.setFont(LABEL_FONT);
        visiblePhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.sunlightWavelength );
                updateFrequencySelectButtons();
            }
        });
        visibleButtonPanel.add( visiblePhotonRadioButton );
        visibleButtonPanel.add( new JLabel(new ImageIcon( GreenhouseResources.getImage( "thin2.png" ) ) ) );
        emissionTypeSelectionPanel.add(visibleButtonPanel);
        
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(infraredPhotonRadioButton);
		buttonGroup.add(visiblePhotonRadioButton);
		
		// Create and add the slider that will allow the user to control the
		// rate of photon emission.
		int edgeMargin = 5; // Adjust as desired for best look.
		int intensitySliderWidth = Math.max( infraredButtonPanel.getPreferredSize().width - 2 * edgeMargin,
		        visibleButtonPanel.getPreferredSize().width - 2 * edgeMargin );
		intensitySlider = new IntensitySlider( Color.RED, IntensitySlider.HORIZONTAL, new Dimension(intensitySliderWidth, 26) );
		intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println("TBD......");
            }
        });
		emissionTypeSelectionPanel.add( intensitySlider );
		
		PSwing selectionPanelPSwing = new PSwing(emissionTypeSelectionPanel);
		selectionPanelPSwing.setOffset(
				photonEmitterImage.getFullBoundsReference().getCenterX() - selectionPanelPSwing.getFullBoundsReference().width / 2,
				photonEmitterImage.getFullBoundsReference().getCenterY() + distanceBetweeImageAndControl - selectionPanelPSwing.getFullBoundsReference().height / 2);
		
		// Create the "connecting rod" that will visually connect the
		// selection panel to the flashlight image.
		Rectangle2D connectingRodShape = new Rectangle2D.Double(0, 0, emitterImageWidth * 0.1, distanceBetweeImageAndControl);
		PNode connectingRod = new PhetPPath(connectingRodShape);
		connectingRod.setPaint(new GradientPaint(0f, 0f, Color.WHITE, (float)connectingRodShape.getWidth(), 0f, Color.DARK_GRAY));
		connectingRod.setOffset(
				photonEmitterImage.getFullBoundsReference().getCenterX() - connectingRodShape.getWidth() / 2,
				photonEmitterImage.getFullBoundsReference().getCenterY());

		// Add all the nodes in the order needed to achieve the desired
		// layering.
		everythingElseLayer.addChild(connectingRod);
		everythingElseLayer.addChild(selectionPanelPSwing);
		
		// Perform any initialization that is dependent upon the model state.
		updateFrequencySelectButtons();
		updateIntensitySlider();
	}
		
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	// Inner Classes and Interfaces
	//------------------------------------------------------------------------
	
	private void updateFrequencySelectButtons(){
	    if (model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength){
	        if (!infraredPhotonRadioButton.isSelected()){
	            infraredPhotonRadioButton.setSelected( true );
	        }
	    }
	    else{
	        if (!visiblePhotonRadioButton.isSelected()){
	            visiblePhotonRadioButton.setSelected( true );
	        }
	    }
	}
	
	/**
	 * Set the appropriate image based on the current setting for the
	 * wavelength of the emitted photons.
	 */
	private void updateImage(double flashlightWidth){
	    
	    // Clear any existing image.
	    emitterImageLayer.removeAllChildren();
	    
	    // Create the flashlight image node, setting the offset such that the
        // center right side of the image is the origin.  This assumes that
        // photons will be emitted horizontally to the right.
	    if (model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength){
	        photonEmitterImage = new PImage(GreenhouseResources.getImage("heat_lamp.png"));
	    }
	    else if (model.getEmittedPhotonWavelength() == GreenhouseConfig.sunlightWavelength){
	        photonEmitterImage = new PImage(GreenhouseResources.getImage("flashlight2.png"));
	    }
        photonEmitterImage.scale(flashlightWidth / photonEmitterImage.getFullBoundsReference().width);
        photonEmitterImage.setOffset(-flashlightWidth, -photonEmitterImage.getFullBoundsReference().height / 2);
        
        emitterImageLayer.addChild(photonEmitterImage);
	}
	
    /**
     * Update the visibility and position of the button that is used to turn
     * photon emission on and off.
     */
	private void updatePhotonEmissionControlButton(){
        double buttonDiameter = photonEmitterImage.getFullBoundsReference().height * 0.3; // Note: Adjust multiplier as needed.
        double scalingFactor = buttonDiameter / unpressedButtonImage.getFullBoundsReference().width;
        Point2D buttonOffset = new Point2D.Double(
                photonEmitterImage.getFullBoundsReference().getCenterX() - buttonDiameter / 2, 
                photonEmitterImage.getFullBoundsReference().getCenterY() - buttonDiameter / 2);
        unpressedButtonImage.scale(scalingFactor);
        unpressedButtonImage.setOffset(buttonOffset);
        pressedButtonImage.scale(scalingFactor);
        pressedButtonImage.setOffset(buttonOffset);
        
        // If photons are being emitted, the top button should be invisible.
        unpressedButtonImage.setVisible( !model.isPeriodicPhotonEmissionEnabled() );
	}
	
	private void updateIntensitySlider(){

	    // Set the overall enable/disable state.
	    intensitySlider.setEnabled( model.isPeriodicPhotonEmissionEnabled() );
	    
	    // Adjust the position of the slider.  Note that we do a conversion
	    // between period and frequency and map it into the slider's range.
	    
	    
	    
	    // Adjust the color of the slider.
	    if (model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength){
	        intensitySlider.setColor( Color.RED );
	    }
	    else if (model.getEmittedPhotonWavelength() == GreenhouseConfig.sunlightWavelength){
	        intensitySlider.setColor( Color.YELLOW );
	    }
	    else{
	        System.err.println(getClass().getName() + "- Error: Unrecognized photon.");
	    }
	}
	
	
}
