/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.module.nuclearreactor.NuclearReactorModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A node that represents a nuclear reactor in the view.
 *
 * @author John Blanco
 */
public class NuclearReactorNode extends PNode{

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final Color        REACTOR_WALL_COLOR = Color.BLACK;
    private static final Color        REACTOR_CHAMBER_COLOR = new Color(0xf5f5d6);
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the nuclear reactor in the model that this node represents.
    private NuclearReactorModel _nuclearReactorModel;
    
    // Reference to the canvas upon which this node resides.  This is needed
    // for scaling model coordinates into canvas coordinates.
    PhetPCanvas _canvas;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public NuclearReactorNode(NuclearReactorModel nuclearReactorModel, PhetPCanvas canvas){
        
        _nuclearReactorModel = nuclearReactorModel;
        _canvas = canvas;
        
        // Register as a listener for notifications from the model about model
        // elements coming and going.
        _nuclearReactorModel.addListener( new NuclearReactorModel.Adapter(){
            public void modelElementAdded(Object modelElement){
//                handleModelElementAdded(modelElement);
            }
            public void modelElementRemoved(Object modelElement){
//                handleModelElementRemoved(modelElement);
            }
        });

        // Create the shapes and the node that will represent the outer wall
        // of the reactor.
        Rectangle2D reactorRect = _nuclearReactorModel.getReactorRect();
        double reactorWallWidth = _nuclearReactorModel.getReactorWallWidth();
        Rectangle2D reactorWallShape = new Rectangle2D.Double(reactorRect.getX() + (reactorWallWidth / 2),
                reactorRect.getY() + (reactorWallWidth / 2), reactorRect.getWidth() - reactorWallWidth,
                reactorRect.getHeight() - reactorWallWidth);
        PPath reactorWall = new PPath(reactorWallShape);
        reactorWall.setStroke( new BasicStroke((float)reactorWallWidth) );
        reactorWall.setStrokePaint( REACTOR_WALL_COLOR );
        reactorWall.setPaint( REACTOR_CHAMBER_COLOR );
        addChild(reactorWall);
        
        // Add nodes for each of the nuclei in the reactor.
        ArrayList nuclei = _nuclearReactorModel.getNuclei();
        for ( int i = 0; i < nuclei.size(); i++ ) {
            AtomicNucleus nucleus = (AtomicNucleus) nuclei.get( i );
            addChild(new AtomicNucleusImageNode(nucleus));
        }
        
        // Add nodes for each of the reactor chambers in the reactor.
        ArrayList chamberRects = _nuclearReactorModel.getChamberRectsReference();
        for (int i = 0; i < chamberRects.size(); i++){
            PPath chamberNode = new PPath((Rectangle2D)chamberRects.get( i ));
            addChild(chamberNode);
        }
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
//    /**
//     * Handle the addition of a new model element by adding a corresponding
//     * node to the canvas (i.e. the view).
//     */
//    public void handleModelElementAdded(Object modelElement){
//        
//        if ((modelElement instanceof AtomicNucleus)){
//            // Add an atom node for this guy.
//            PNode atomNode = new AtomicNucleusImageNode((AtomicNucleus)modelElement);
//            _nucleusLayer.addChild( atomNode );
//            _modelElementToNodeMap.put( modelElement, atomNode );
//        }
//        else if (modelElement instanceof Neutron){
//            // Add a corresponding neutron node for this guy.
//            PNode neutronNode = new NeutronNode((Neutron)modelElement);
//            _nucleusLayer.addChild( neutronNode );
//            _modelElementToNodeMap.put( modelElement, neutronNode );            
//        }
//        else{
//            System.err.println("Error: Unable to find appropriate node for model element.");
//        }
//    }
//    
//    /**
//     * Remove the node or nodes that corresponds with the given model element
//     * from the canvas.
//     */
//    public void handleModelElementRemoved(Object modelElement){
//        
//        Object nucleusNode = _modelElementToNodeMap.get( modelElement );
//        if ((nucleusNode != null) || (nucleusNode instanceof PNode)){
//            
//            if (modelElement instanceof AtomicNucleus){
//                // Remove the nucleus node.
//                _nucleusLayer.removeChild( (PNode )nucleusNode );
//                _modelElementToNodeMap.remove( modelElement );
//            }
//            else {
//                // This is not a composite model element, so just remove the
//                // corresponding PNode.
//                _nucleusLayer.removeChild( (PNode )nucleusNode );
//                _modelElementToNodeMap.remove( modelElement );
//            }
//        }
//        else{
//            System.err.println("Error: Problem encountered removing node from canvas.");
//        }
//    }
//
}
