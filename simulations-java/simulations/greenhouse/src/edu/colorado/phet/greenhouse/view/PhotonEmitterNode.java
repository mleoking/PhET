/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.IntensitySlider;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel.PhotonTarget;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
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
	private PNode emissionControlSliderLayer;
	private double emitterImageWidth;

    private JRadioButton infraredPhotonRadioButton;
    private JRadioButton visiblePhotonRadioButton;
    private IntensitySlider emissionRateControlSlider;
    private PSwing selectionPanelPSwing;

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
                updateEmissionControlSlider();
                // Workaround for piccolo bug.
                selectionPanelPSwing.repaint();
            }

            @Override
            public void photonEmissionPeriodChanged() {
                updateEmissionControlSlider();
            }
		});
		
		// Create the layers on which the other nodes will be placed.
		PNode everythingElseLayer = new PNode();
		addChild( everythingElseLayer );
		emitterImageLayer = new PNode();
		addChild( emitterImageLayer );
		emissionControlSliderLayer = new PNode();
		addChild( emissionControlSliderLayer );
		
		// Add the initial image.
		updateImage( emitterImageWidth );
		
		// Add the slider that will control the rate of photon emission.
		Dimension emissionControlSliderSize = new Dimension(100, 30);  // This may be adjusted as needed for best look.
		emissionRateControlSlider = new IntensitySlider( Color.RED, IntensitySlider.HORIZONTAL, emissionControlSliderSize );
		emissionRateControlSlider.setMinimum( 0 );
		emissionRateControlSlider.setMaximum( SLIDER_RANGE );
		emissionRateControlSlider.addChangeListener( new ChangeListener() {
		    public void stateChanged( ChangeEvent e ) {
		        double sliderProportion = (double)emissionRateControlSlider.getValue() / (double)SLIDER_RANGE;
		        if (model.getPhotonTarget() == PhotonTarget.CONFIGURABLE_ATMOSPHERE){
		            // Note the implicit conversion from frequency to period in the following line.
		            model.setPhotonEmissionPeriod(
		                    PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_MULTIPLE_TARGET / sliderProportion );
		        }
		        else{
		            // Note the implicit conversion from frequency to period in the following line.
		            model.setPhotonEmissionPeriod(
		                    PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_SINGLE_TARGET / sliderProportion );
		        }
		    }
		});
		
		PSwing emissionRateControlSliderPSwing = new PSwing( emissionRateControlSlider );
		PBounds emitterImageBounds = photonEmitterImage.getFullBoundsReference();
		emissionRateControlSliderPSwing.setOffset(
		        emitterImageBounds.getCenterX() - emissionRateControlSliderPSwing.getFullBoundsReference().getWidth() / 2,
		        emitterImageBounds.getCenterY() - emissionRateControlSliderPSwing.getFullBoundsReference().getHeight() / 2);

        emissionControlSliderLayer.addChild( emissionRateControlSliderPSwing );
        
        // Do the initial update of the emission control slider.
        updateEmissionControlSlider();
		
		// Calculate the vertical distance between the center of the
		// emitter image and the control box.  This is a function of the
		// flashlight width.  The multiplier is arbitrary and can be adjusted
		// as desired.
		double distanceBetweeImageAndControl = emitterImageWidth * 0.6;
		
		JPanel infraredButtonPanel = new JPanel();
        infraredPhotonRadioButton = new JRadioButton( GreenhouseResources.getString( "PhotonEmitterNode.Infrared" ) );
        infraredPhotonRadioButton.setFont(LABEL_FONT);
        infraredPhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
                updateFrequencySelectButtons();
            }
        });
        infraredButtonPanel.add( infraredPhotonRadioButton );
        infraredButtonPanel.add( new JLabel(new ImageIcon( GreenhouseResources.getImage( "photon-660.png" ) ) ) );
        
        JPanel visibleButtonPanel = new JPanel();
        visiblePhotonRadioButton = new JRadioButton( GreenhouseResources.getString( "PhotonEmitterNode.Visible" ) );
        visiblePhotonRadioButton.setFont(LABEL_FONT);
        visiblePhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.sunlightWavelength );
                updateFrequencySelectButtons();
            }
        });
        visibleButtonPanel.add( visiblePhotonRadioButton );
        visibleButtonPanel.add( new JLabel(new ImageIcon( GreenhouseResources.getImage( "thin2.png" ) ) ) );
        
		ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( infraredPhotonRadioButton );
        buttonGroup.add( visiblePhotonRadioButton );

		// main panel, control on in a vertical column, left justified
        JPanel emissionTypeSelectionPanel = new JPanel();
        emissionTypeSelectionPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        EasyGridBagLayout layout = new EasyGridBagLayout( emissionTypeSelectionPanel );
        layout.setAnchor( GridBagConstraints.WEST ); // left justify
        emissionTypeSelectionPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( infraredButtonPanel, row++, column );
        layout.addComponent( visibleButtonPanel, row++, column );
        layout.addComponent( emissionRateControlSlider, row++, column );
		
		selectionPanelPSwing = new PSwing(emissionTypeSelectionPanel);
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
		updateEmissionControlSlider();
	}
		
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	
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
	
	private void updateEmissionControlSlider(){

	    // Adjust the position of the slider.  Note that we do a conversion
	    // between period and frequency and map it into the slider's range.
	    int mappedFrequency;
	    if (model.getPhotonTarget() == PhotonTarget.CONFIGURABLE_ATMOSPHERE){
	        mappedFrequency = (int)Math.round( PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_MULTIPLE_TARGET / 
	                model.getPhotonEmissionPeriod() * (double) SLIDER_RANGE);
	    }
	    else{
            mappedFrequency = (int)Math.round( PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_SINGLE_TARGET / 
                    model.getPhotonEmissionPeriod() * (double) SLIDER_RANGE);
	    }
	    
	    emissionRateControlSlider.setValue( mappedFrequency );
	    
	    // Set the color of the slider.
	    if (model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength){
	        emissionRateControlSlider.setColor( Color.RED );
	    }
	    else if (model.getEmittedPhotonWavelength() == GreenhouseConfig.sunlightWavelength){
	        emissionRateControlSlider.setColor( Color.YELLOW );
	    }
	    else{
	        System.err.println(getClass().getName() + "- Error: Unrecognized photon.");
	    }
	}
}
