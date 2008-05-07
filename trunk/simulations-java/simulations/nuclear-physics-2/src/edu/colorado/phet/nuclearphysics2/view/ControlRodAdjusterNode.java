/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.nuclearphysics2.module.nuclearreactor.ControlRod;
import edu.colorado.phet.nuclearphysics2.module.nuclearreactor.NuclearReactorModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This node attaches to the control rods in a model to allow the user to
 * move the rods up and down.  It is a little unusual amongst such nodes
 * because it doesn't have a direct analog in the model.
 *
 * @author John Blanco
 */
public class ControlRodAdjusterNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final Color        CONTROL_ROD_ADJUSTER_COLOR = Color.GREEN;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the model contain the set of control rods.
    private NuclearReactorModel _nuclearReactorModel;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public ControlRodAdjusterNode(NuclearReactorModel nuclearReactorModel){
        
        _nuclearReactorModel = nuclearReactorModel;

        // Register as a listener for position change notifications from the
        // first control rod inside the nuclear reactor.  The action we take
        // when we get this event define how this node moves.  It is assumed
        // that all the other control rods stay positionally in sync with the
        // first one.
        ControlRod controlRod = (ControlRod)_nuclearReactorModel.getControlRodsReference().get( 0 );
        controlRod.addListener( new ControlRod.Listener(){
            public void positionChanged(){
                ControlRod controlRod = (ControlRod)_nuclearReactorModel.getControlRodsReference().get( 0 );
                setOffsetBasedOnControlRod( controlRod );
            }
        });
        
        // Create the (somewhat complicated) path for the control rod adjuster.
        float width = (float)controlRod.getRectangleReference().getWidth();
        float vertSize = (float)(controlRod.getRectangleReference().getHeight() + width);
        float horizSize = (float)(_nuclearReactorModel.getReactorRect().getWidth() - (controlRod.getPosition().getX() -
                _nuclearReactorModel.getReactorRect().getX()) + (2 * _nuclearReactorModel.getReactorWallWidth()));
        GeneralPath adjusterShape = new GeneralPath();
        adjusterShape.moveTo( 0, 0 );
        adjusterShape.lineTo( horizSize - width, 0 );
        adjusterShape.lineTo( horizSize - width, -(vertSize - width) );
        adjusterShape.lineTo( horizSize, -(vertSize - width) );
        adjusterShape.lineTo( horizSize, width );
        adjusterShape.lineTo( 0, width );
        adjusterShape.lineTo( 0, 0 );
        PPath controlRodAdjuster = new PPath(adjusterShape);
        controlRodAdjuster.setPaint( CONTROL_ROD_ADJUSTER_COLOR );
        addChild(controlRodAdjuster);

        // Set ourself up to listen for and handle mouse dragging events.
        addInputEventListener( new PDragEventHandler(){
            
            public void drag(PInputEvent event){
                double yDelta = event.getDelta().height;
                _nuclearReactorModel.moveControlRods( yDelta );
            }
        });
        
        // Add a cursor handler to make it clear to the user that they can
        // grab and move this thing.
        addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );

        // Set our offset to correspond to the bottom of the first control rod.
        setOffsetBasedOnControlRod( controlRod );
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    private void setOffsetBasedOnControlRod(ControlRod controlRod){
        setOffset(controlRod.getPosition().getX(),
                controlRod.getPosition().getY() + controlRod.getRectangleReference().getHeight());
    }
}
