/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class is the "view" for the particle container.  This is where the
 * information about the nature of the image that is used to depict the
 * container is encapsulated.
 *
 * @author John Blanco
 */
public class ParticleContainerNode2 extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Only a portion of the image will correspond to the location of the
    // particle container within the model.  The rest of the image is "fluff",
    // meaning that it provides visual cues to the user.  These constants
    // define where within the image the particle container should be mapped.
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT   = 0.0;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT  = 0.0;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM = 0.05;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_TOP    = 0.00;
    public static final String IMAGE_NAME = "cup_3D_front_70_back_line.png";
    
    public static final double MAX_TEMP_IN_KELVIN = 1000;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final MultipleParticleModel m_model;
    private final ModelViewTransform m_mvt;
    private PImage m_containerImageNode;
    private LiquidExpansionThermometerNode m_thermometerNode;

    private double m_containmentAreaWidth;
    private double m_containmentAreaHeight;
    private double m_containmentAreaOffsetX;
    private double m_containmentAreaOffsetY;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode2(MultipleParticleModel model, ModelViewTransform mvt) {
        
        super();

        m_model = model;
        m_mvt = mvt;
        m_containmentAreaWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();
        
        // Set ourself up as a listener to the model.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void temperatureChanged(){
                updateThermometerTemperature();
            }
        });

        // Internal initialization.
        m_containmentAreaWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();

        // Load the image that will be used.
        m_containerImageNode = StatesOfMatterResources.getImageNode(IMAGE_NAME);
        
        // Scale the container image based on the size of the container.
        double neededImageWidth = 
            m_containmentAreaWidth / (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT - NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT);
        m_containerImageNode.setScale( neededImageWidth / m_containerImageNode.getWidth() );
        
        // Add the image to this node.
        addChild(m_containerImageNode);
        
        // Calculate the offset for the area within the overall image where
        // the particles will be contained.
        m_containmentAreaOffsetX = neededImageWidth * NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT;
        m_containmentAreaOffsetY = m_containerImageNode.getFullBounds().height * NON_CONTAINER_IMAGE_FRACTION_FROM_TOP;
        
        // Position this node so that the origin of the canvas, i.e. position
        // x=0, y=0, is at the lower left corner of the container.
        double xPos = -m_containerImageNode.getFullBoundsReference().width * NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT;
        double yPos = -m_containerImageNode.getFullBoundsReference().height * (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM);
        setOffset( xPos, yPos );
        
        // Add a thermometer for displaying temperature.
        
        m_thermometerNode = new LiquidExpansionThermometerNode( 
                new PDimension( m_containerImageNode.getFullBoundsReference().width * 0.075, 
                        m_containerImageNode.getFullBoundsReference().height * 0.20 ) );
        
        m_thermometerNode.setTicks( m_thermometerNode.getFullBoundsReference().height / 12, Color.BLACK, 4 );
        
        addChild(m_thermometerNode);
        
        m_thermometerNode.setOffset( 
                m_containerImageNode.getFullBoundsReference().x + m_containerImageNode.getFullBoundsReference().width * 0.25, 
                m_containerImageNode.getFullBoundsReference().y - m_containerImageNode.getFullBoundsReference().height * 0.12 );
          
        updateThermometerTemperature();
        
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
        double temperature = Math.min(  m_model.getTemperatureInKelvin() / MAX_TEMP_IN_KELVIN, 1 ); 
    }

    // TODO: JPB TBD - Is this needed?
    private void update() {
    }
}