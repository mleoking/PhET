package edu.colorado.phet.statesofmatter.module.exp2;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode3;
import edu.colorado.phet.statesofmatter.view.ParticleNode;
import edu.colorado.phet.statesofmatter.view.StoveNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This is the canvas that represents the play area for the "Solid, Liquid,
 * Gas" tab of this simulation.
 *
 * @author John Blanco
 */
public class Exp2SolidLiquidGasCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 22000;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 3.2; // Larger means x = 0 further left.
    private final double HEIGHT_TRANSLATION_FACTOR = 1.35; // Larger means y = 0 further up.
    
    // Sizes, in terms of overall canvas size, of the nodes on the canvas.
    private final double BURNER_NODE_WIDTH = CANVAS_WIDTH / 2.75;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private MultipleParticleModel m_model;
    private ParticleContainerNode3 m_particleContainer;
    private PNode m_lowerParticleLayer;
    private PNode m_upperParticleLayer;
    private ModelViewTransform m_mvt;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public Exp2SolidLiquidGasCanvas(MultipleParticleModel multipleParticleModel) {
        
        m_model = multipleParticleModel;
        
        // Create the Model-View transform that we will be using.
        m_mvt = new ModelViewTransform(1.0, 1.0, 0.0, 0.0, false, true);
        
        // Set the transform strategy so that the particle container is in a
        // reasonable place given that point (0,0) on the canvas represents
        // the lower left corner of the particle container.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/WIDTH_TRANSLATION_FACTOR, 
                        getHeight()/HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
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
        });
        
        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );
        
        // Create and add the lower particle layer node.  We create two so
        // that we can control which particles go on top of each other.
        m_lowerParticleLayer = new PNode();
        m_lowerParticleLayer.setPickable( false );
        m_lowerParticleLayer.setChildrenPickable( false );
        addWorldChild( m_lowerParticleLayer );
        
        // Create and add the upper particle layer node.
        m_upperParticleLayer = new PNode();
        m_upperParticleLayer.setPickable( false );
        m_upperParticleLayer.setChildrenPickable( false );
        addWorldChild( m_upperParticleLayer );
        
        // Create and add the particle container.
        try {
            m_particleContainer = new ParticleContainerNode3(this, m_model);
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        
        addWorldChild(m_particleContainer);
        
        /*
        // TODO: JPB TBD - Add a rectangle that represents the containment box
        // so that I can calibrate the size of the cup.
        ParticleContainer container = m_model.getParticleContainer();
        Shape containerShape = container.getShape();
        if (containerShape instanceof Rectangle2D){
            containerShape = m_mvt.modelToView( (Rectangle2D)containerShape );
        }
        else{
            System.err.println("Unexpected type for container shape.");
        }
        PPath tempContainerNode = new PPath(containerShape);
        tempContainerNode.setStrokePaint( Color.MAGENTA );
        tempContainerNode.setStroke( new BasicStroke(100) );
        addWorldChild( tempContainerNode );
        */
        
        // Add a burner that the user can use to add or remove heat from the
        // particle container.
        StoveNode stoveNode = new StoveNode( m_model );
        stoveNode.setScale( BURNER_NODE_WIDTH / stoveNode.getFullBoundsReference().width );
        stoveNode.setOffset(m_particleContainer.getFullBoundsReference().getMinX() + 
                m_particleContainer.getFullBoundsReference().width/3,
                m_particleContainer.getFullBoundsReference().getMaxY() + stoveNode.getFullBoundsReference().height / 10);
        addWorldChild( stoveNode );
        
        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            
            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                // TODO: JPB TBD - Do I need this?
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    public void reset(){
        m_particleContainer.reset();
    }
}
