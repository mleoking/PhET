/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class is the "view" for the particle container.  This is where the
 * information about the nature of the image that is used to depict the
 * container is encapsulated.
 *
 * @author John Blanco
 */
public class ParticleContainerNode3 extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final MultipleParticleModel m_model;
    private PPath m_containerNode;
    private LiquidExpansionThermometerNode m_thermometerNode;

    private double m_containmentAreaWidth;
    private double m_containmentAreaHeight;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode3(PhetPCanvas canvas, MultipleParticleModel model) throws IOException {
        
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
        
        // Create the node that will represent the rectangular containment vessel.
        m_containerNode = new PPath( new Rectangle2D.Double(0, 0, m_containmentAreaWidth, m_containmentAreaHeight));
        m_containerNode.setStroke( new BasicStroke(100) );
        m_containerNode.setStrokePaint( Color.YELLOW );
        addChild(m_containerNode);

        // Position this node so that the origin of the canvas, i.e. position
        // x=0, y=0, is at the lower left corner of the container.
        double xPos = 0;
        double yPos = -m_containmentAreaHeight;
        setOffset( xPos, yPos );
        
        // Add a thermometer for displaying temperature.
        
        m_thermometerNode = new LiquidExpansionThermometerNode( 
                new PDimension( m_containerNode.getFullBoundsReference().width * 0.075, 
                        m_containerNode.getFullBoundsReference().height * 0.20 ) );
        
        m_thermometerNode.setTicks( m_thermometerNode.getFullBoundsReference().height / 12, Color.BLACK, 4 );
        
        addChild(m_thermometerNode);
        
        m_thermometerNode.setOffset( 
                m_containerNode.getFullBoundsReference().x + m_containerNode.getFullBoundsReference().width * 0.25, 
                m_containerNode.getFullBoundsReference().y - m_containerNode.getFullBoundsReference().height * 0.12 );
          
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
        m_thermometerNode.setLiquidHeight( m_model.getNormalizedTemperature() );
    }

    // TODO: JPB TBD - Is this needed?
    private void update() {
    }
}