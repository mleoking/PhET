/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.Uranium235Nucleus;
import edu.colorado.phet.nuclearphysics2.model.Uranium238Nucleus;
import edu.colorado.phet.nuclearphysics2.module.nuclearreactor.ControlRod;
import edu.colorado.phet.nuclearphysics2.module.nuclearreactor.NuclearReactorModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A node that represents a nuclear reactor in the view.
 *
 * @author John Blanco
 */
public class NuclearReactorNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Constants the control the look of the reactor.
    private static final Color        REACTOR_WALL_COLOR = Color.BLACK;
    private static final Color        COOL_REACTOR_CHAMBER_COLOR = new Color(0xbbbbbb);
    private static final Color        HOT_REACTOR_CHAMBER_COLOR = new Color(0xff3333);
    
    // Max temperature, with used when setting up the thermometer and in
    // in controlling the internal color of the reactor.
    private static final double       MAX_TEMPERATURE = 75;  // Unitless value.
    
    // Constants that control the position and size of the thermometer.
    private static final double       THERMOMETER_PROPORTION_FROM_LEFT_SIDE = 0.08;
    private static final double       THERMOMETER_PROPORTION_ABOVE = 0.18;
    private static final double       THERMOMETER_WIDTH_PROPORTION = 0.05;
    private static final double       THERMOMETER_HEIGHT_PROPORTION = 0.40;
    
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the nuclear reactor in the model that this node represents.
    private NuclearReactorModel _nuclearReactorModel;
    
    // Reference to the canvas upon which this node resides.  This is needed
    // for scaling model coordinates into canvas coordinates.
    PhetPCanvas _canvas;
    
    // A node representing the outside of the reactor.
    PPath _reactorWall;
    
    // A child PNode where all the atomic nuclei and free particles are
    // maintained.
    PNode _nucleiAndFreeParticleNode;
    
    // A map for tracking model element to node relationships.
    HashMap _modelElementToNodeMap;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public NuclearReactorNode(NuclearReactorModel nuclearReactorModel, PhetPCanvas canvas){
        
        _nuclearReactorModel = nuclearReactorModel;
        _canvas = canvas;
        
        // Initialize local data.
        _modelElementToNodeMap = new HashMap();
        
        // Register as a listener for notifications from the model about model
        // elements coming and going.
        _nuclearReactorModel.addListener( new NuclearReactorModel.Adapter(){
            public void modelElementAdded(Object modelElement){
                handleModelElementAdded(modelElement);
            }
            public void modelElementRemoved(Object modelElement){
                handleModelElementRemoved(modelElement);
            }
            public void resetOccurred(){
                handleReactorReset();
            }
            public void temperatureChanged(){
                setInternalReactorColor();
            }
        });

        // Create the shapes and the node that will represent the outer wall
        // of the reactor.
        Rectangle2D reactorRect = _nuclearReactorModel.getReactorRect();
        double reactorWallWidth = _nuclearReactorModel.getReactorWallWidth();
        Rectangle2D reactorWallShape = new Rectangle2D.Double(reactorRect.getX() + (reactorWallWidth / 2),
                reactorRect.getY() + (reactorWallWidth / 2), reactorRect.getWidth() - reactorWallWidth,
                reactorRect.getHeight() - reactorWallWidth);
        _reactorWall = new PPath(reactorWallShape);
        _reactorWall.setStroke( new BasicStroke((float)reactorWallWidth) );
        _reactorWall.setStrokePaint( REACTOR_WALL_COLOR );
        addChild(_reactorWall);
        setInternalReactorColor();
        
        // Add nodes for each of the reactor chambers in the reactor.
        ArrayList chamberRects = _nuclearReactorModel.getChamberRectsReference();
        for (int i = 0; i < chamberRects.size(); i++){
            PPath chamberNode = new PPath((Rectangle2D)chamberRects.get( i ));
            addChild(chamberNode);
        }
        
        // Add the nodes for each of the control rods.
        ArrayList controlRods = _nuclearReactorModel.getControlRodsReference();
        for (int i = 0; i < controlRods.size(); i++){
            ControlRodNode controlRodNode = new ControlRodNode((ControlRod)controlRods.get( i ));
            addChild(controlRodNode);
        }
        
        // Add the control rod adjuster node.
        addChild(new ControlRodAdjusterNode(_nuclearReactorModel));
        
        // Add the node into which the nuclei and free nucleons will be placed.
        _nucleiAndFreeParticleNode = new PNode();
        addChild(_nucleiAndFreeParticleNode);
        
        // Add the initial nuclei (if there are any).
        addNucleusNodes();
        
        // Add the thermometer.
        PNode thermometerNode = new ThermometerNode(_nuclearReactorModel,
                new PDimension(THERMOMETER_WIDTH_PROPORTION * reactorRect.getWidth(), 
                        THERMOMETER_HEIGHT_PROPORTION * reactorRect.getHeight()), MAX_TEMPERATURE);
        addChild(thermometerNode);
        thermometerNode.setOffset( 
                reactorRect.getX() + (reactorRect.getWidth() * THERMOMETER_PROPORTION_FROM_LEFT_SIDE),
                reactorRect.getY() - (reactorRect.getHeight() * THERMOMETER_PROPORTION_ABOVE));
        
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    private void handleReactorReset(){
        
        // The reactor should have already removed all nuclei and free
        // particles as part of the reset process, but we double check here
        // anyway.
        if ( _nucleiAndFreeParticleNode.getChildrenCount() > 0 ){
            System.err.println("Error: Particle and/or nuclei nodes still exist after reset.");
            assert false;  // Force an assert if running in debug mode so problem won't go unnoticed.
            _nucleiAndFreeParticleNode.removeAllChildren();
        }
        
        // Add the nuclei to the model.  It is assumed after reset that there
        // are no free particles.
        addNucleusNodes();
    }
    
    /**
     * Set the internal reactor color, which is a function of the internal
     * temperature.
     */
    private void setInternalReactorColor(){
        double reactorTemperature = _nuclearReactorModel.getTemperature();
        if (reactorTemperature > MAX_TEMPERATURE){
            reactorTemperature = MAX_TEMPERATURE;
        }
        // Blend the hot and cold colors together based on the current temp.
        double weighting = (MAX_TEMPERATURE - reactorTemperature) / MAX_TEMPERATURE;
        
        int red = (int)(Math.round( (COOL_REACTOR_CHAMBER_COLOR.getRed() * weighting) + 
                (HOT_REACTOR_CHAMBER_COLOR.getRed() * (1 - weighting)) )); 
        int green = (int)(Math.round( (COOL_REACTOR_CHAMBER_COLOR.getGreen() * weighting) + 
                (HOT_REACTOR_CHAMBER_COLOR.getGreen() * (1 - weighting)) )); 
        int blue = (int)(Math.round( (COOL_REACTOR_CHAMBER_COLOR.getBlue() * weighting) + 
                (HOT_REACTOR_CHAMBER_COLOR.getBlue() * (1 - weighting)) ));
        
        if (blue > 255){
            System.err.println("Holy jeez, what's going on?");
        }
        
        _reactorWall.setPaint( new Color(red, green, blue) );
    }
    
    /**
     * Add a node to represent a nucleus for each of the nuclei in the model.
     */
    private void addNucleusNodes(){
        
        ArrayList nuclei = _nuclearReactorModel.getNuclei();
        for ( int i = 0; i < nuclei.size(); i++ ) {
            AtomicNucleus nucleus = (AtomicNucleus) nuclei.get( i );
            
            // Only show U235 nuclei in the view.  There are likely to be
            // other nuclei present in the model (generally U238) to moderate
            // the reaction, but the educators have specified that these not
            // be shown since it makes the view too cluttered.
            if (nucleus instanceof Uranium235Nucleus){
                AtomicNucleusImageNode atomNode = new AtomicNucleusImageNode(nucleus);
                _nucleiAndFreeParticleNode.addChild( atomNode );
                _modelElementToNodeMap.put( nucleus, atomNode );
            }
        }
    }
    
    /**
     * Handle the addition of a new model element by adding a corresponding
     * node to the canvas (i.e. the view).
     */
    public void handleModelElementAdded(Object modelElement){
        
        if ((modelElement instanceof AtomicNucleus)){
            // Add an atom node for this guy.
            PNode atomNode = new AtomicNucleusImageNode((AtomicNucleus)modelElement);
            _nucleiAndFreeParticleNode.addChild( atomNode );
            _modelElementToNodeMap.put( modelElement, atomNode );
        }
        else if (modelElement instanceof Neutron){
            // Add a corresponding neutron node for this guy.
            PNode neutronNode = new NeutronNode((Neutron)modelElement);
            _nucleiAndFreeParticleNode.addChild( neutronNode );
            _modelElementToNodeMap.put( modelElement, neutronNode );            
        }
        else{
            System.err.println("Error: Unable to find appropriate node for model element.");
            assert false; // Assert when running in debug mode so that the problem doesn't go unnoticed.
        }
    }
    
    /**
     * Remove the node or nodes that corresponds with the given model element
     * from the canvas.
     */
    public void handleModelElementRemoved(Object modelElement){

        Object nucleusNode = _modelElementToNodeMap.get( modelElement );
        if ((nucleusNode != null) || (nucleusNode instanceof PNode)){
            
            if (modelElement instanceof AtomicNucleus){
                // Remove the nucleus node.
                _nucleiAndFreeParticleNode.removeChild( (PNode )nucleusNode );
                AtomicNucleusNode node = (AtomicNucleusNode)_modelElementToNodeMap.remove( modelElement );
                node.cleanup();
            }
            else {
                // This is not a composite model element, so just remove the
                // corresponding PNode.
                _nucleiAndFreeParticleNode.removeChild( (PNode )nucleusNode );
                _modelElementToNodeMap.remove( modelElement );
            }
        }
        else{
            if (!(modelElement instanceof Uranium238Nucleus)){
                System.err.println("Error: Problem encountered removing node from canvas.");
            }
        }
    }
}
