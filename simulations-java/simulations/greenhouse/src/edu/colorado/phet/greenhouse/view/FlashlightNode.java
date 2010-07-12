/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;


import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PNode that represents a flashlight in the view.  This node is set up such
 * that setting its offset based on the photon emission point in the model
 * should position it correctly.  This makes some assumptions about the
 * direction of photon emission.
 * 
 * @author John Blanco
 */
public class FlashlightNode extends PNode {
	
	private static final Font LABEL_FONT = new PhetFont(24);
	
	private PImage flashlightImage;
	private ModelViewTransform2D mvt;
	private PhotonAbsorptionModel model;

    private JRadioButton infraredPhotonRadioButton;
    private JRadioButton visiblePhotonRadioButton;
	
	/**
	 * Constructor.
	 * 
	 * @param flashlightWidth - Width of the flashlight in screen coords.  The
	 * height will be based on the aspect ratio of the image.  The size of
	 * the control box is independent of this - its size is based on the
	 * strings that define the user selections.
	 * @param mvt - Model-view transform for translating between model and
	 * view coordinate systems.
	 * @param model TODO
	 */
	public FlashlightNode(double flashlightWidth, ModelViewTransform2D mvt, final PhotonAbsorptionModel model) {
		
		this.mvt = mvt;
		this.model = model;
		
		// Create the flashlight image node, setting the offset such that the
		// center right side of the image is the origin.  This assumes that
		// photons will be emitted horizontally to the right.
		flashlightImage = new PImage(GreenhouseResources.getImage("flashlight.png"));
		flashlightImage.scale(flashlightWidth / flashlightImage.getFullBoundsReference().width);
		flashlightImage.setOffset(-flashlightWidth, -flashlightImage.getFullBoundsReference().height / 2);
		
		// Create the button that the user presses to fire a photon.  This
		// is comprised of two images, one over the other, and the one becomes
		// invisible when pressed.
		double buttonDiameter = flashlightImage.getFullBoundsReference().height * 0.3; // Note: Adjust multiplier as needed.
		final PImage unpressedButtonImage = new PImage(GreenhouseResources.getImage("button_unpressed.png"));
		unpressedButtonImage.scale(buttonDiameter / unpressedButtonImage.getFullBoundsReference().width);
		Point2D buttonOffset = new Point2D.Double(
				flashlightImage.getFullBoundsReference().getCenterX() - buttonDiameter / 2, 
				flashlightImage.getFullBoundsReference().getCenterY() + flashlightImage.getFullBoundsReference().getHeight() * 0.05 - buttonDiameter / 2);
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
		PImage pressedButtonImage = new PImage(GreenhouseResources.getImage("button_pressed.png"));
		pressedButtonImage.scale(buttonDiameter / pressedButtonImage.getFullBoundsReference().width);
		pressedButtonImage.setOffset(buttonOffset);
        pressedButtonImage.addInputEventListener(new CursorHandler());
		
		// Calculate the vertical distance between the center of the
		// flashlight image and the control box.  This is a function of the
		// flashlight width.  The multiplier is arbitrary and can be adjusted
		// as desired.
		double distanceBetweeImageAndControl = flashlightWidth * 0.8;
		
		// Create the control box for selecting the type of photon that will
		// be emitted.
		JPanel emissionTypeSelectionPanel = new VerticalLayoutPanel();
		emissionTypeSelectionPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		// TODO: i18n
		visiblePhotonRadioButton = new JRadioButton("Visible");
		visiblePhotonRadioButton.setFont(LABEL_FONT);
		visiblePhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setPhotonWavelength( GreenhouseConfig.sunlightWavelength );
                updateFrequencySelectButtons();
            }
        });
        // TODO: i18n
        infraredPhotonRadioButton = new JRadioButton("Infrared");
        infraredPhotonRadioButton.setFont(LABEL_FONT);
        infraredPhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setPhotonWavelength( GreenhouseConfig.irWavelength );
                updateFrequencySelectButtons();
            }
        });
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(visiblePhotonRadioButton);
		buttonGroup.add(infraredPhotonRadioButton);
		emissionTypeSelectionPanel.add(visiblePhotonRadioButton);
		emissionTypeSelectionPanel.add(infraredPhotonRadioButton);
		PSwing selectionPanelPSwing = new PSwing(emissionTypeSelectionPanel);
		selectionPanelPSwing.setOffset(
				flashlightImage.getFullBoundsReference().getCenterX() - selectionPanelPSwing.getFullBoundsReference().width / 2,
				flashlightImage.getFullBoundsReference().getCenterY() + distanceBetweeImageAndControl - selectionPanelPSwing.getFullBoundsReference().height / 2);
		
		// Create the "connecting rod" that will visually connect the
		// selection panel to the flashlight image.
		Rectangle2D connectingRodShape = new Rectangle2D.Double(0, 0, flashlightWidth * 0.1, distanceBetweeImageAndControl);
		PNode connectingRod = new PhetPPath(connectingRodShape);
		connectingRod.setPaint(new GradientPaint(0f, 0f, Color.WHITE, (float)connectingRodShape.getWidth(), 0f, Color.DARK_GRAY));
		connectingRod.setOffset(
				flashlightImage.getFullBoundsReference().getCenterX() - connectingRodShape.getWidth() / 2,
				flashlightImage.getFullBoundsReference().getCenterY());

		// Add all the nodes in the order needed to achieve the desired
		// layering.
		addChild(connectingRod);
		addChild(flashlightImage);
		addChild(pressedButtonImage);
		addChild(unpressedButtonImage);
		addChild(selectionPanelPSwing);
		
		// Perform any initialization that is dependent upon the model state.
		updateFrequencySelectButtons();
	}
	
	private void updateFrequencySelectButtons(){
	    if (model.getPhotonWavelength() == GreenhouseConfig.irWavelength){
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
}
