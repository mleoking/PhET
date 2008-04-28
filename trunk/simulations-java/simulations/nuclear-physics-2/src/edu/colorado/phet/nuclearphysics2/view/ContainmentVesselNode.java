/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.nuclearphysics2.model.ContainmentVessel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A node that represents a containment vessel in the view.  Each instance
 * must be coupled with a containment vessel in the model portion of the sim.
 *
 * @author John Blanco
 */
public class ContainmentVesselNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final float   CONTAINMENT_VESSEL_THICKNESS = 8.0f; 
    private static final double  HANDLE_HEIGHT = 30; 
    private static final float   HANDLE_WIDTH = 15; 
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the containment vessel in the model that this node represents.
    private ContainmentVessel _containmentVessel;
    
    // The shapes and the node that represent the main vessel.  The shapes
    // make up the vessel shape in sections that go clockwise around it.
    private Ellipse2D _mainVesselOuterEllipse;
    private Ellipse2D _mainVesselInnerEllipse;
    private PPath _mainVesselNode;
    
    // A nodes that represent visual handles that the user can grab to change
    // the size of the containment vessel.
    private HandleNode _lowerRightHandle;
    private HandleNode _upperRightHandle;
    
    // Variables used to control the resizing process.
    boolean _resizing;
    
    // Reference to the canvas upon which this node resides.  This is needed
    // for scaling model coordinates into canvas coordinates.
    PhetPCanvas _canvas;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public ContainmentVesselNode(ContainmentVessel containmentVessel, PhetPCanvas canvas){
        
        _containmentVessel = containmentVessel;
        _canvas = canvas;
        
        // Initialize member variables.
        _resizing = false;
        
        // Register as a listener to the containment vessel.
        _containmentVessel.addListener( new ContainmentVessel.Listener(){
            public void radiusChanged(double newRadius){
                setVesselNodeSizeAndPosition();
            }
            public void enableStateChanged(boolean isEnabled){
                _mainVesselNode.setVisible( isEnabled );
                _lowerRightHandle.setVisible( isEnabled );
                _upperRightHandle.setVisible( isEnabled );
            }
        });
        
        // Create a handle for sizing the containment vessel.
        _upperRightHandle = new HandleNode(HANDLE_WIDTH, HANDLE_HEIGHT, Color.GRAY);
        _upperRightHandle.setVisible( _containmentVessel.getIsEnabled() );
        _upperRightHandle.rotate( Math.PI * 0.79 );
        _upperRightHandle.addInputEventListener( new CursorHandler() );
        _upperRightHandle.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                
                PDimension unitDimension = new PDimension( 1, 1 );
                
                _canvas.getCamera().getTransform().getTranslateX();
                System.out.println(_canvas.getCamera().getTransform().getTranslateX());
                
                // Convert the radius to canvas coordinates so that we have
                // the right units for setting the new size of the containment vessel.
                _canvas.getPhetRootNode().screenToWorld( unitDimension );
                double unitOfChange = unitDimension.getWidth();
                
                PDimension delta = event.getCanvasDelta();
                
                // TODO: JPB TBD - Just to get something happening.
                _containmentVessel.setRadius( _containmentVessel.getRadius() + delta.width * unitOfChange );
            }
        } );

        addChild(_upperRightHandle);
        
        // Create a handle for sizing the containment vessel.
        _lowerRightHandle = new HandleNode(HANDLE_WIDTH, HANDLE_HEIGHT, Color.GRAY);
        _lowerRightHandle.setVisible( _containmentVessel.getIsEnabled() );
        _lowerRightHandle.rotate( Math.PI * 1.22 );
        _lowerRightHandle.addInputEventListener( new CursorHandler() );
        _lowerRightHandle.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                System.out.println("mouseDragged, canvas pos = " + event.getCanvasPosition());
                System.out.println("mouseDragged, position = " + event.getPosition());
                System.out.println("mouseDragged, delta = " + event.getDelta());
                System.out.println("+++++++++++++");
            }
        } );

        addChild(_lowerRightHandle);
        
        // Create the shapes and the node that will represent the containment
        // vessel.
        _mainVesselOuterEllipse = new Ellipse2D.Double();
        _mainVesselInnerEllipse = new Ellipse2D.Double();
        _mainVesselNode = new PPath(_mainVesselOuterEllipse);
        setVesselNodeSizeAndPosition();
        _mainVesselNode.setVisible( _containmentVessel.getIsEnabled() );
        _mainVesselNode.setPickable( true );
        addChild(_mainVesselNode);        
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    private void setVesselNodeSizeAndPosition(){
        
        double radius = _containmentVessel.getRadius();
        Rectangle2D apertureRect = _containmentVessel.getAperatureRectReference();

        _mainVesselInnerEllipse.setFrameFromDiagonal( -radius, -radius, radius, radius );
        _mainVesselOuterEllipse.setFrameFromDiagonal( -radius - CONTAINMENT_VESSEL_THICKNESS, 
                -radius - CONTAINMENT_VESSEL_THICKNESS, radius + CONTAINMENT_VESSEL_THICKNESS,
                radius + CONTAINMENT_VESSEL_THICKNESS);
        Area mainVesselArea = new Area(_mainVesselOuterEllipse);
        mainVesselArea.subtract( new Area(_mainVesselInnerEllipse) );
        mainVesselArea.subtract( new Area(apertureRect) );
        
        _mainVesselNode.setPathTo( mainVesselArea );
        _mainVesselNode.setPaint( Color.BLACK );
        
        // Set the position of the handles.
      _lowerRightHandle.setOffset( radius * 0.82,  radius * 0.92 );
      _upperRightHandle.setOffset( radius * 1.05, -radius * 0.60 );
      
    }
}
