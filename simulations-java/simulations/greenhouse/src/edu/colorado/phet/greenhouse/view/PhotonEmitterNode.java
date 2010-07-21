/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;


import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
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
	
	enum ImageType { FLASHLIGHT, HEAT_LAMP };
	
	// ------------------------------------------------------------------------
	// Instance Data
	// ------------------------------------------------------------------------
	
	private PImage photonEmitterImage;
	private PhotonAbsorptionModel model;
	private PNode emitterImageLayer;
	private double emitterImageWidth;

    private JRadioButton infraredPhotonRadioButton;
    private JRadioButton visiblePhotonRadioButton;
	
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
            }
		});
		
		// Create the layers on which the other nodes will be placed.
		PNode everythingElseLayer = new PNode();
		addChild( everythingElseLayer );
		emitterImageLayer = new PNode();
		addChild( emitterImageLayer );
		
		// Add the initial image.
		updateImage( emitterImageWidth );
		
		
		// Calculate the vertical distance between the center of the
		// emitter image and the control box.  This is a function of the
		// flashlight width.  The multiplier is arbitrary and can be adjusted
		// as desired.
		double distanceBetweeImageAndControl = emitterImageWidth * 0.6;
		
		// Create the control box for selecting the type of photon that will
		// be emitted.
		JPanel emissionTypeSelectionPanel = new VerticalLayoutPanel();
		emissionTypeSelectionPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        // TODO: i18n
        infraredPhotonRadioButton = new JRadioButton("Infrared");
        infraredPhotonRadioButton.setFont(LABEL_FONT);
        infraredPhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
                updateFrequencySelectButtons();
            }
        });
        emissionTypeSelectionPanel.add(infraredPhotonRadioButton);
        // TODO: i18n
        visiblePhotonRadioButton = new JRadioButton("Visible");
        visiblePhotonRadioButton.setFont(LABEL_FONT);
        visiblePhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.sunlightWavelength );
                updateFrequencySelectButtons();
            }
        });
        emissionTypeSelectionPanel.add(visiblePhotonRadioButton);
        
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(infraredPhotonRadioButton);
		buttonGroup.add(visiblePhotonRadioButton);
		
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
        
        // Create the button that turns photon emission on/off.
        double buttonDiameter = photonEmitterImage.getFullBoundsReference().height * 0.3; // Note: Adjust multiplier as needed.
        final PImage unpressedButtonImage = new PImage(GreenhouseResources.getImage("emitterOffButton.png"));
        double scalingFactor = buttonDiameter / unpressedButtonImage.getFullBoundsReference().width;
        unpressedButtonImage.scale(scalingFactor);
        Point2D buttonOffset = new Point2D.Double(
                photonEmitterImage.getFullBoundsReference().getCenterX() - buttonDiameter / 2, 
                photonEmitterImage.getFullBoundsReference().getCenterY() - buttonDiameter / 2);
        unpressedButtonImage.setOffset(buttonOffset);
        unpressedButtonImage.addInputEventListener(new CursorHandler());
        unpressedButtonImage.addInputEventListener(new PBasicInputEventHandler(){
            @Override
            public void mousePressed( PInputEvent event ) {
                unpressedButtonImage.setVisible(false);
                model.emitPhoton();
            }
            
            @Override
            public void mouseReleased( PInputEvent event ) {
                unpressedButtonImage.setVisible(true);
            }
        });
        PImage pressedButtonImage = new PImage(GreenhouseResources.getImage("emitterOnButton.png"));
        pressedButtonImage.scale(scalingFactor);
        pressedButtonImage.setOffset(buttonOffset);
        pressedButtonImage.addInputEventListener(new CursorHandler());

        emitterImageLayer.addChild(photonEmitterImage);
        emitterImageLayer.addChild(pressedButtonImage);
        emitterImageLayer.addChild(unpressedButtonImage);
	}
}
