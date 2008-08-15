/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.io.IOException;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.view.instruments.CompositeThermometerNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class is the "view" for the particle container.  This is where the
 * information about the nature of the image that is used to depict the
 * container is encapsulated.
 *
 * @author John Blanco
 */
public class ParticleContainerNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Only a portion of the image will correspond to the location of the
    // particle container within the model.  The rest of the image is "fluff",
    // meaning that it provides visual cues to the user.  These constants
    // define where within the image the particle container should be mapped.
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT   = 0.35;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT  = 0.05;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM = 0.2;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_TOP    = 0.05;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final MultipleParticleModel m_model;
    private PImage m_cupImageNode;
    private CompositeThermometerNode m_thermometerNode;

    private double m_containmentAreaWidth;
    private double m_containmentAreaHeight;
    private double m_containmentAreaOffsetX;
    private double m_containmentAreaOffsetY;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode(PhetPCanvas canvas, MultipleParticleModel model) throws IOException {
        
        super();

        m_model               = model;
        
        // Register as a listener for temperature changes.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void temperatureChanged(){
                updateThermometerTemperature();
            }
        });
        
        // Internal initialization.
        m_containmentAreaWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();

        // Load the image that will be used.
        m_cupImageNode = StatesOfMatterResources.getImageNode(StatesOfMatterConstants.COFFEE_CUP_IMAGE);
        
        // Scale the cup image based on the size of the container.
        double neededImageWidth = 
            m_containmentAreaWidth / (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT - NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT);
        m_cupImageNode.setScale( neededImageWidth / m_cupImageNode.getWidth() );
        
        // Add the cup image to this node.
        addChild(m_cupImageNode);
        
        // Calculate the offset for the area within the overall image where
        // the particles will be contained.
        m_containmentAreaOffsetX = neededImageWidth * NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT;
        m_containmentAreaOffsetY = m_cupImageNode.getFullBounds().height * NON_CONTAINER_IMAGE_FRACTION_FROM_TOP;
        
        // Position this node so that the origin of the canvas, i.e. position
        // x=0, y=0, is at the lower left corner of the container.
        double xPos = -m_cupImageNode.getFullBoundsReference().width * NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT;
        double yPos = -m_cupImageNode.getFullBoundsReference().height * (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM);
        setOffset( xPos, yPos );
        
        // Add a thermometer for displaying temperature.
        
        m_thermometerNode = new CompositeThermometerNode(m_cupImageNode.getFullBoundsReference().width * 0.2, 
                m_cupImageNode.getFullBoundsReference().height * 0.3, 
                StatesOfMatterConstants.MAX_DISPLAYED_TEMPERATURE);
        m_thermometerNode.setOffset( 
                m_cupImageNode.getFullBoundsReference().x + m_cupImageNode.getFullBoundsReference().width * 0.4, 
                m_cupImageNode.getFullBoundsReference().y - m_cupImageNode.getFullBoundsReference().height * 0.1 );
        addChild(m_thermometerNode);
        
        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        update();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public void reset(){
        // TODO: JPB TBD.
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    /**
     * Update the value displayed in the thermometer.
     */
    private void updateThermometerTemperature(){
        m_thermometerNode.setTemperatureInDegreesKelvin( m_model.getTemperatureInKelvin() );
    }

    // TODO: JPB TBD - Is this needed?
    private void update() {
    }
}