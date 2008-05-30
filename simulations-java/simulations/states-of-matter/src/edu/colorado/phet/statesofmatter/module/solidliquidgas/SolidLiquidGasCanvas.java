package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode;
import edu.colorado.phet.statesofmatter.view.ParticleNode;
import edu.umd.cs.piccolo.util.PDimension;


public class SolidLiquidGasCanvas extends PhetPCanvas {
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 20; // TODO: JPB TBD - This is not correct yet, and is just set to a value that empirically looks good.
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 4.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private MultipleParticleModel m_model;
    private HashMap m_mapParticlesToNodes = new HashMap();
    private ParticleContainerNode m_particleContainer;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public SolidLiquidGasCanvas(MultipleParticleModel multipleParticleModel) {
        
        m_model = multipleParticleModel;
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/WIDTH_TRANSLATION_FACTOR, 
                        getHeight()/HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Create the particle container.
        try {
            m_particleContainer = new ParticleContainerNode(this, m_model.getParticleContainer());
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        addWorldChild(m_particleContainer);
        
        // Add the individual particles to the particle container.
        for ( int i = 0; i < m_model.getParticles().size(); i++ ) {
            m_particleContainer.addParticleNode(new ParticleNode(m_model.getParticle(i)));
        }
        
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
        // TODO: JPB TBD - Need to fill this in.
    }
}
