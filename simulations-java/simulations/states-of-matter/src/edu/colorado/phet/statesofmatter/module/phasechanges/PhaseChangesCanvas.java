/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.view.BicyclePumpNode;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode2;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode3;
import edu.colorado.phet.statesofmatter.view.ParticleNode;
import edu.colorado.phet.statesofmatter.view.StoveNode;
import edu.colorado.phet.statesofmatter.view.instruments.DialGaugeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


public class PhaseChangesCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 23000;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 5.5;
    private final double HEIGHT_TRANSLATION_FACTOR = 1.3;
    
    // Sizes, in terms of overall canvas size, of the nodes on the canvas.
    private final double BURNER_NODE_WIDTH = CANVAS_WIDTH / 2.5;
    private final double PRESSURE_GAUGE_WIDTH = CANVAS_WIDTH / 6;
    private final double PUMP_HEIGHT = CANVAS_HEIGHT / 2;
    private final double PUMP_WIDTH = CANVAS_WIDTH / 3;
    
    // Maximum value expected for pressure.  JPB TBD - Should probably get
    // this from the model or somewhere, though I'm not sure where yet.
    private final double MAX_PRESSURE = 1;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private MultipleParticleModel m_model;
    private ParticleContainerNode3 m_particleContainer;
    private ModelViewTransform m_mvt;
    private DialGaugeNode m_pressureMeter;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public PhaseChangesCanvas(MultipleParticleModel multipleParticleModel) {
        
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
            public void pressureChanged(){
                m_pressureMeter.setValue(m_model.getPressure());
            }
        });
        
        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );
        
        // Create the particle container.
        m_particleContainer = new ParticleContainerNode3(m_model, m_mvt, true);
        
        // Add the pressure meter.
        m_pressureMeter = new DialGaugeNode(PRESSURE_GAUGE_WIDTH, "Pressure", 0, MAX_PRESSURE, "");
        m_pressureMeter.setOffset( m_particleContainer.getFullBoundsReference().x + (0.97 * m_particleContainer.getFullBoundsReference().width), 
                -m_particleContainer.getFullBoundsReference().height * 0.75 );
        addWorldChild( m_pressureMeter );
        
        // Add the pump.
        BicyclePumpNode pump = new BicyclePumpNode(PUMP_WIDTH, PUMP_HEIGHT, m_model);
        pump.setOffset( m_particleContainer.getFullBoundsReference().x + (0.97 * m_particleContainer.getFullBoundsReference().width),
                -PUMP_HEIGHT);
        addWorldChild( pump );
        
        // Add the particle container after the pressure meter so it can be
        // on top of it.
        addWorldChild(m_particleContainer);
        
        // Add a burner that the user can use to add or remove heat from the
        // particle container.
        StoveNode stoveNode = new StoveNode( m_model );
        stoveNode.setScale( BURNER_NODE_WIDTH / stoveNode.getFullBoundsReference().width );
        stoveNode.setOffset(m_particleContainer.getFullBoundsReference().getMinX() + 
                m_particleContainer.getFullBoundsReference().width/2,
                m_particleContainer.getFullBoundsReference().getMaxY());
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
