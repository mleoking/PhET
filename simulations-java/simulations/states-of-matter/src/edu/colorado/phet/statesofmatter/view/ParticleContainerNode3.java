/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.view.instruments.CompositeThermometerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

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
    
    // Constants that control the appearance of the drawn container.
    private static final Color CONTAINER_EDGE_COLOR = Color.YELLOW;
    private static final float CONTAINER_LINE_WIDTH = 100;
    private static final Stroke CONTAINER_EDGE_STROKE = new BasicStroke(CONTAINER_LINE_WIDTH);
    private static final Stroke HIDDEN_CONTAINER_EDGE_STROKE = new BasicStroke(CONTAINER_LINE_WIDTH / 4,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,0, new float[] {CONTAINER_LINE_WIDTH, CONTAINER_LINE_WIDTH}, 0);
    
    // Constants that control the appearance of the image.
    private static final String IMAGE_NAME = StatesOfMatterConstants.CYLINDRICAL_CONTAINER_IMAGE;
    private static final double CONTAINER_VERTICAL_OFFSET_FRACTION   = 0.05;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final MultipleParticleModel m_model;
    private ModelViewTransform m_mvt;
    private double m_containmentAreaWidth;
    private double m_containmentAreaHeight;
    private PNode m_lowerParticleLayer;
    private PNode m_upperParticleLayer;
    private PNode m_topContainerLayer;
    private PNode m_bottomContainerLayer;
    private PNode m_containerTop;
    private CompositeThermometerNode m_thermometerNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode3(MultipleParticleModel model, ModelViewTransform mvt, boolean useImage) {
        
        super();

        m_model = model;
        m_mvt = mvt;
        m_containmentAreaWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();
        
        // Set ourself up as a listener to the model.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void particleAdded(StatesOfMatterAtom particle){
                if (particle instanceof HydrogenAtom){
                    m_lowerParticleLayer.addChild( new ParticleNode(particle, m_mvt));
                }
                else{
                    m_upperParticleLayer.addChild( new ParticleNode(particle, m_mvt));
                }
            }
            public void temperatureChanged(){
                updateThermometerTemperature();
            }
        });
        
        // Create the visual representation of the container.
        if (useImage){
            loadContainerImage();
        }
        else{
            drawContainer();
        }
        
        // Add a thermometer for displaying temperature.
        
        m_thermometerNode = new CompositeThermometerNode(0, 400, 
                m_topContainerLayer.getFullBoundsReference().width * 0.25, 
                m_topContainerLayer.getFullBoundsReference().height * 0.30);
        m_thermometerNode.setOffset( 
                m_topContainerLayer.getFullBoundsReference().x + m_topContainerLayer.getFullBoundsReference().width * 0.85, 
                m_topContainerLayer.getFullBoundsReference().y - m_topContainerLayer.getFullBoundsReference().height * 0.05 );
        addChild(m_thermometerNode);
        
        updateThermometerTemperature();
        
        // Position this node so that the origin of the canvas, i.e. position
        // x=0, y=0, is at the lower left corner of the container.
        double xPos = 0;
        double yPos = -m_containmentAreaHeight;
        setOffset( xPos, yPos );

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
        // TODO: JPB TBD - The multiplier below is bogus, and I'm waiting on better
        // information from the physicists.
        m_thermometerNode.setTemperatureInKelvin( m_model.getNormalizedTemperature() * 160 );
    }

    // TODO: JPB TBD - Is this needed?
    private void update() {
    }
    
    private void drawContainer() {
        // Create the bottom of the container, which will appear below (or
        // behind) the particles in the Z-order.
        
        double ellipseHeight = m_containmentAreaHeight * 0.15; // TODO: JPB TBD - Make this a constant?
        
        // Add the node that will be at the bottom of the Z-order, and will
        // contain the portion of the container that should appear behind the
        // particles.
        m_bottomContainerLayer = new PNode();
        m_bottomContainerLayer.setPickable( false );
        m_bottomContainerLayer.setChildrenPickable( false );
        addChild( m_bottomContainerLayer );

        // Create the bottom of the container.
        PPath hiddenContainerEdge = new PPath(new Ellipse2D.Double(0, 0, m_containmentAreaWidth, ellipseHeight));
        hiddenContainerEdge.setStroke( HIDDEN_CONTAINER_EDGE_STROKE );
        hiddenContainerEdge.setStrokePaint( CONTAINER_EDGE_COLOR );
        m_bottomContainerLayer.addChild( hiddenContainerEdge );

        Ellipse2D outerEllipse = new Ellipse2D.Double( -CONTAINER_LINE_WIDTH / 2, -CONTAINER_LINE_WIDTH / 2, 
                m_containmentAreaWidth + CONTAINER_LINE_WIDTH, ellipseHeight + CONTAINER_LINE_WIDTH );
        Ellipse2D innerEllipse = new Ellipse2D.Double( CONTAINER_LINE_WIDTH / 2, CONTAINER_LINE_WIDTH / 2, 
                m_containmentAreaWidth - CONTAINER_LINE_WIDTH, ellipseHeight - CONTAINER_LINE_WIDTH );
        Rectangle2D topHalfRect = new Rectangle2D.Double(-CONTAINER_LINE_WIDTH / 2, -CONTAINER_LINE_WIDTH / 2,
                m_containmentAreaWidth + CONTAINER_LINE_WIDTH, ellipseHeight / 2);
        Area bottomEdgeArea = new Area(outerEllipse);
        bottomEdgeArea.subtract( new Area(innerEllipse) );
        bottomEdgeArea.subtract( new Area(topHalfRect) );
        PPath bottomFrontContainerEdge = new PPath(bottomEdgeArea);
        bottomFrontContainerEdge.setPaint( CONTAINER_EDGE_COLOR );
        m_bottomContainerLayer.addChild( bottomFrontContainerEdge );

        m_bottomContainerLayer.setOffset( 0, m_containmentAreaHeight - (ellipseHeight / 2) );

        // Add the layers where the particles will appear.
        addParticleLayers();
        
        // Create the top portion of the container, which will appear above
        // the particles in the Z-order.

        m_topContainerLayer = new PNode();
        m_topContainerLayer.setPickable( false );
        m_topContainerLayer.setChildrenPickable( false );
        addChild( m_topContainerLayer );
        
        PPath containerTop = new PPath( new Ellipse2D.Double(0, 0, m_containmentAreaWidth, ellipseHeight));
        containerTop.setStroke( CONTAINER_EDGE_STROKE );
        containerTop.setStrokePaint( CONTAINER_EDGE_COLOR );
        m_containerTop = new PNode();
        m_containerTop.setPickable( true );
        m_containerTop.setChildrenPickable( false );
        m_containerTop.addChild( containerTop );
        m_topContainerLayer.addChild( m_containerTop );
        m_containerTop.setOffset( 0, -ellipseHeight / 2 );
        
        // Create the left and right edges of the container.
        
        PPath containerLeftSide = new PPath( new Line2D.Double(0, 0, 0, m_containmentAreaHeight) );
        containerLeftSide.setStroke( CONTAINER_EDGE_STROKE );
        containerLeftSide.setStrokePaint( CONTAINER_EDGE_COLOR );
        m_topContainerLayer.addChild( containerLeftSide );
        
        PPath containerRightSide = 
            new PPath( new Line2D.Double(m_containmentAreaWidth, 0, m_containmentAreaWidth, m_containmentAreaHeight) );
        containerRightSide.setStroke( CONTAINER_EDGE_STROKE );
        containerRightSide.setStrokePaint( CONTAINER_EDGE_COLOR );
        m_topContainerLayer.addChild( containerRightSide );
    }

    private void loadContainerImage(){
        
        // Load the image that will be used.
        PImage containerImageNode = StatesOfMatterResources.getImageNode(IMAGE_NAME);
        
        // Scale the container image based on the size of the container.
        containerImageNode.setScale( m_containmentAreaWidth / containerImageNode.getWidth() );
        
        // Add the layers where the particles will appear.  Note that by
        // virtue of being added at this point, the will be behind the image,
        // which requires that the image be at least partially transparent.
        addParticleLayers();
        
        // Add the image to the top layer node.
        m_topContainerLayer = new PNode();
        m_topContainerLayer.setPickable( false );
        m_topContainerLayer.setChildrenPickable( false );
        m_topContainerLayer.addChild(containerImageNode);
        
        // Shift the image so that the particles only move around where we
        // want them to.
        containerImageNode.setOffset( 0, -m_containmentAreaHeight * CONTAINER_VERTICAL_OFFSET_FRACTION );
        
        // Add the top layer node to this node.
        addChild( m_topContainerLayer );
    }

    private void addParticleLayers() {
        // Create and add the lower particle layer node.  We create two so
        // that we can control which particles go on top of each other.
        
        m_lowerParticleLayer = new PNode();
        m_lowerParticleLayer.setPickable( false );
        m_lowerParticleLayer.setChildrenPickable( false );
        addChild( m_lowerParticleLayer );
        m_lowerParticleLayer.setOffset( 0, m_containmentAreaHeight );  // Compensate for inverted Y axis.
        
        // Create and add the upper particle layer node.
        m_upperParticleLayer = new PNode();
        m_upperParticleLayer.setPickable( false );
        m_upperParticleLayer.setChildrenPickable( false );
        addChild( m_upperParticleLayer );
        m_upperParticleLayer.setOffset( 0, m_containmentAreaHeight );  // Compensate for inverted Y axis.
    }
}