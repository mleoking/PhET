// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.nuclearphysics.model.ContainmentVessel;
import edu.colorado.phet.nuclearphysics.util.MovingImageNode;
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
    
    private static final float        CONTAINMENT_VESSEL_THICKNESS = 8.0f; 
    private static final double       HANDLE_HEIGHT = 15; 
    private static final float        HANDLE_WIDTH = 12;
    private static final float        HANDLE_THICKNESS = 3;
    private static final BasicStroke  HANDLE_STROKE = new BasicStroke(1.2f);
    private static final float        HANDLE_CORNER_WIDTH = 4;
    private static final Color        HANDLE_COLOR = Color.LIGHT_GRAY;
    private static final double       HANDLE_ANGLE = Math.PI / 4;
    private static final double       MAX_FRAGMENT_VELOCITY = 5;
    private static final double       MAX_FRAGMENT_SPIN_RATE = 0.02;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the containment vessel in the model that this node represents.
    private ContainmentVessel _containmentVessel;
    
    // The shapes and the node that represent the main vessel.
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
    
    // Reference to the clock that is driving the simulation, which is needed
    // for handling the explosion of the vessel.
    SwingClock _clock;
    
    // Random number generator, used during explosion.
    Random _rand;
    
    // List of fragments produced by explosion.
    ArrayList _fragments;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public ContainmentVesselNode(ContainmentVessel containmentVessel, PhetPCanvas canvas, SwingClock clock){
        
        _containmentVessel = containmentVessel;
        _canvas = canvas;
        _clock = clock;
        
        // Initialize member variables.
        _resizing = false;
        _rand = new Random();
        _fragments = new ArrayList();
        
        // Register as a listener to the containment vessel.
        _containmentVessel.addListener( new ContainmentVessel.Adapter(){
            public void radiusChanged(double newRadius){
                setVesselNodeSizeAndPosition();
                _canvas.repaint();
            }
            public void enableStateChanged(boolean isEnabled){
                _mainVesselNode.setVisible( isEnabled );
                _lowerRightHandle.setVisible( isEnabled );
                _upperRightHandle.setVisible( isEnabled );
            }
            public void explosionOccurred(){
                handleContainmentVesselExplosion();
            }
            public void resetOccurred(){
                handleResetOccurred();
            }
        });
        
        // Create a handle for sizing the containment vessel.
        _upperRightHandle = new HandleNode(HANDLE_WIDTH, HANDLE_HEIGHT, HANDLE_THICKNESS, HANDLE_CORNER_WIDTH,
                HANDLE_COLOR, Color.BLACK, HANDLE_STROKE);
        _upperRightHandle.setVisible( _containmentVessel.getIsEnabled() );
        _upperRightHandle.rotate( Math.PI - HANDLE_ANGLE );
        _upperRightHandle.addInputEventListener( new CursorHandler() );
        _upperRightHandle.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                handleMouseDraggedEvent( event );
            }
        } );

        addChild(_upperRightHandle);
        
        // Create a handle for sizing the containment vessel.
        _lowerRightHandle = new HandleNode(HANDLE_WIDTH, HANDLE_HEIGHT, HANDLE_THICKNESS, HANDLE_CORNER_WIDTH,
                HANDLE_COLOR, Color.BLACK, HANDLE_STROKE);
        _lowerRightHandle.setVisible( _containmentVessel.getIsEnabled() );
        _lowerRightHandle.rotate( Math.PI + HANDLE_ANGLE );
        _lowerRightHandle.addInputEventListener( new CursorHandler() );
        _lowerRightHandle.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                handleMouseDraggedEvent( event );
            }
        } );

        addChild(_lowerRightHandle);
        
        // Create the shapes and the node that will represent the containment
        // vessel.
        _mainVesselOuterEllipse = new Ellipse2D.Double();
        _mainVesselInnerEllipse = new Ellipse2D.Double();
        _mainVesselNode = new PPath(_mainVesselOuterEllipse);
        _mainVesselNode.addInputEventListener( new CursorHandler() );
        _mainVesselNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                handleMouseDraggedEvent( event );
            }
        } );
        _mainVesselNode.setVisible( _containmentVessel.getIsEnabled() );
        _mainVesselNode.setPickable( true );
        addChild(_mainVesselNode);
        
        // Set the initial size and position for the container.
        setVesselNodeSizeAndPosition();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Set the size and position of the nodes and the various other pieces
     * that ultimately depict the containment vessel.
     * 
     */
    private void setVesselNodeSizeAndPosition(){
        
        double radius = _containmentVessel.getRadius();
        Rectangle2D apertureRect = _containmentVessel.getAperatureRectReference();
        
        // Create the shape that represents the containment vessel based on
        // the radius of the containment vessel in the model.

        _mainVesselInnerEllipse.setFrameFromDiagonal( -radius, -radius, radius, radius );
        _mainVesselOuterEllipse.setFrameFromDiagonal( -radius - CONTAINMENT_VESSEL_THICKNESS, 
                -radius - CONTAINMENT_VESSEL_THICKNESS, radius + CONTAINMENT_VESSEL_THICKNESS,
                radius + CONTAINMENT_VESSEL_THICKNESS);
        Area mainVesselArea = new Area(_mainVesselOuterEllipse);
        mainVesselArea.subtract( new Area(_mainVesselInnerEllipse) );
        mainVesselArea.subtract( new Area(apertureRect) );
        
        _mainVesselNode.setPathTo( mainVesselArea );
        _mainVesselNode.setPaint( Color.BLACK );
        
        // Set the positions for the handles.  The calculations are necessary
        // to account for the fact that the locator point for the handle node
        // is on the corner of it, not the center.
        
        double handleRadius = radius + HANDLE_WIDTH + (CONTAINMENT_VESSEL_THICKNESS / 2);
        double handleAngle = HANDLE_ANGLE - Math.atan( (HANDLE_HEIGHT / 2 )/(radius + HANDLE_WIDTH) );
        double xPos = handleRadius * Math.cos( handleAngle );
        double yPos = -handleRadius * Math.sin( handleAngle );
        _upperRightHandle.setOffset( xPos, yPos );
      
        handleAngle = -HANDLE_ANGLE - Math.atan( (HANDLE_HEIGHT / 2 )/(radius + HANDLE_WIDTH) );
        xPos = handleRadius * Math.cos( handleAngle );
        yPos = -handleRadius * Math.sin( handleAngle );
        _lowerRightHandle.setOffset( xPos,  yPos );
    }

    /**
     * Handle the explosion of the containment vessel.
     */
    private void handleContainmentVesselExplosion(){

        // Take a snapshot of the current representation of the containment vessel.
        BufferedImage vesselImage = BufferedImageUtils.toBufferedImage( _mainVesselNode.toImage() );
        
        // Tile the image up into several sub-images that will look as if they
        // are fragments of the exploding vessel and create a moving node for
        // each.
        int numCols = 4;
        int numRows = numCols;
        int tileWidth = vesselImage.getWidth() / numCols;
        int tileHeight = vesselImage.getHeight() / numRows;
        double tileOriginX = _mainVesselNode.getOffset().getX() - (((double)numCols / 2) * (double)tileWidth);
        double tileOriginY = _mainVesselNode.getOffset().getY() - (((double)numRows / 2) * (double)tileHeight);
        for (int i = 0; i < numCols; i++){
            for (int j = 0; j < numRows; j++){
                
                // Figure out where the fragment image should be and how it should move.
                double xPos = tileOriginX + (double)(i * tileWidth);
                double yPos = tileOriginY + (double)(j * tileHeight);
                
                double xVel = _rand.nextDouble() * MAX_FRAGMENT_VELOCITY;
                if (xPos < 0){
                    xVel = -xVel;
                }
                
                double yVel = _rand.nextDouble() * MAX_FRAGMENT_VELOCITY;
                if (yPos < 0){
                    yVel = -yVel;
                }
                
                double spin = _rand.nextDouble() * MAX_FRAGMENT_SPIN_RATE;
                
                // Create the image.
                MovingImageNode imageNode = new MovingImageNode(
                        vesselImage.getSubimage( i * tileWidth, j * tileHeight, tileWidth, tileHeight),
                        xVel, yVel, spin, _clock ); 

                // Position the image.
                imageNode.setOffset( tileOriginX + (double)(i * tileWidth), 
                        tileOriginY + (double)(j * tileHeight));
                addChild(imageNode);
                
                // Save the image so that it can be disposed of later.
                _fragments.add(imageNode);
            }
        }
        
        // Hide the nodes that represent the unexploded containment vessel.
        _mainVesselNode.setVisible( false );
        _lowerRightHandle.setVisible( false );
        _upperRightHandle.setVisible( false );
        
    }
    
    /**
     * Handle a reset of the containment vessel.
     */
    private void handleResetOccurred(){
        
        // Get rid of any fragments that might exist.
        for ( Iterator iterator = _fragments.iterator(); iterator.hasNext(); ) {
            MovingImageNode fragment = (MovingImageNode) iterator.next();
            removeChild( fragment );
            fragment.cleanup();
        }
        _fragments.clear();
        
        // Set up the size and position of the vessel.
        setVesselNodeSizeAndPosition();
    }

    
    /**
     * Handle a mouse drag event, which for this node generally means that the
     * containment vessel size is being changed.
     */
    private void handleMouseDraggedEvent(PInputEvent event){

        final Point2D originPos = new Point2D.Double();
        final Point2D beforePos = new Point2D.Double();
        
        // Calculate the change in distance relative to the center of the canvas.
        originPos.setLocation( _canvas.getWidth() / 2, _canvas.getHeight() / 2 );
        beforePos.setLocation( event.getPosition().getX() - event.getDelta().width,
                event.getPosition().getY() - event.getDelta().height );
        double distanceDelta = originPos.distance( event.getPosition() ) - originPos.distance( beforePos );
        
        // Convert the radius to canvas coordinates so that we have
        // the right units for setting the new size of the containment vessel.
        PDimension unitDimension = new PDimension( 1, 1 );
        _canvas.getPhetRootNode().screenToWorld( unitDimension );
        double unitOfChange = unitDimension.getWidth();
        
        // Set the new size for the containment vessel.
        _containmentVessel.setRadius( _containmentVessel.getRadius() + distanceDelta * unitOfChange );
    }
}
