/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.BicyclePumpNode;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode;
import edu.colorado.phet.statesofmatter.view.StoveNode;
import edu.colorado.phet.statesofmatter.view.instruments.CompositeThermometerNode;
import edu.colorado.phet.statesofmatter.view.instruments.DialGaugeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas where the visual objects for the phase changes tab are placed.
 *
 * @author John Blanco
 */
public class PhaseChangesCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 24000;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private static final double WIDTH_TRANSLATION_FACTOR = 0.29;  // 0 puts the vertical origin all the way left, 1
                                                                  // is all the way to the right.
    private static final double HEIGHT_TRANSLATION_FACTOR = 0.74; // 0 puts the horizontal origin at the top of the 
                                                                  // window, 1 puts it at the bottom.
    
    // Sizes, in terms of overall canvas size, of the nodes on the canvas.
    private final double BURNER_NODE_WIDTH = CANVAS_WIDTH / 2.5;
    private final double PUMP_HEIGHT = CANVAS_HEIGHT / 2;
    private final double PUMP_WIDTH = CANVAS_WIDTH / 4;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private MultipleParticleModel m_model;
    private ParticleContainerNode m_particleContainer;
    private ModelViewTransform m_mvt;
    private CompositeThermometerNode m_thermometerNode;
    private Random m_rand;
    private double m_rotationAngle;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public PhaseChangesCanvas(MultipleParticleModel multipleParticleModel) {
        
        m_model = multipleParticleModel;
        m_rand = new Random();
        
        // Create the Model-View transform that we will be using.
        m_mvt = new ModelViewTransform(1.0, 1.0, 0.0, 0.0, false, true);
        
        // Set the transform strategy so that the particle container is in a
        // reasonable place given that point (0,0) on the canvas represents
        // the lower left corner of the particle container.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR, 
                        getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set ourself up as a listener to the model.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void temperatureChanged(){
                updateThermometerTemperature();
            }
            public void containerSizeChanged(){
                updateThermometerPosition();
            }
            public void containerExploded() {
                m_rotationAngle = -(Math.PI/100 + (m_rand.nextDouble() * Math.PI/50));
            }
        });

        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );
        
        // Create the particle container.
        m_particleContainer = new ParticleContainerNode(m_model, m_mvt, true, true);

        // Get the rectangle that describes the position of the particle
        // container within the model, since the various nodes below will
        // all be positioned relative to it.
        Rectangle2D containerRect = m_model.getParticleContainerRect();

        // Add the pump.
        BicyclePumpNode pump = new BicyclePumpNode(PUMP_WIDTH, PUMP_HEIGHT, m_model);
        pump.setOffset( containerRect.getX() + containerRect.getWidth(), 
                containerRect.getY() - pump.getFullBoundsReference().height - 
                containerRect.getHeight() * 0.2);
        addWorldChild( pump );
        
        // Add the particle container after the pressure meter so it can be
        // on top of it.
        addWorldChild(m_particleContainer);
        
        // Add a thermometer for displaying temperature.
        m_thermometerNode = new CompositeThermometerNode(containerRect.getWidth() * 0.25, 
        		containerRect.getHeight() * 0.35, StatesOfMatterConstants.MAX_DISPLAYED_TEMPERATURE);
        addWorldChild(m_thermometerNode);
        updateThermometerTemperature();
        updateThermometerPosition();
        
        // Add a burner that the user can use to add or remove heat from the
        // particle container.
        StoveNode stoveNode = new StoveNode( m_model, this.getBackground() );
        stoveNode.setScale( BURNER_NODE_WIDTH / stoveNode.getFullBoundsReference().width );
        stoveNode.setOffset(containerRect.getX() + containerRect.getWidth() * 0.3,
                containerRect.getY() + containerRect.getHeight() * 0.1);
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
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    /**
     * Update the value displayed in the thermometer.
     */
    private void updateThermometerTemperature(){
        m_thermometerNode.setTemperatureInDegreesKelvin( m_model.getTemperatureInKelvin() );
    }

    /**
     * Update the position of the thermometer so that it stays on the lid.
     */
    private void updateThermometerPosition(){
        Rectangle2D containerRect = m_model.getParticleContainerRect();

        if (!m_model.getContainerExploded()){
            if (m_thermometerNode.getRotation() != 0){
            	m_thermometerNode.setRotation(0);
            }
        }
        else{
        	// The container is exploding, so spin the thermometer.
            m_thermometerNode.rotateInPlace(m_rotationAngle);
        }
        
        m_thermometerNode.setOffset( 
                containerRect.getX() + containerRect.getWidth() * 0.31, 
                containerRect.getY() - containerRect.getHeight() - 
                (m_thermometerNode.getFullBoundsReference().height * 0.5) );
    }
}
