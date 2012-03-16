// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.module.nuclearreactor.ControlRod;
import edu.colorado.phet.nuclearphysics.module.nuclearreactor.NuclearReactorModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

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
    
    private static final Color        CONTROL_ROD_ADJUSTER_COLOR = new Color(0x66cc00);
    private static final double       HANDLE_HEIGHT = 50;
    private static final float        HANDLE_WIDTH = 25;
    private static final float        HANDLE_THICKNESS = 12;
    private static final BasicStroke  HANDLE_STROKE = new BasicStroke(1.2f);
    private static final float        HANDLE_CORNER_WIDTH = 15;
    private static final Color        HANDLE_COLOR = Color.DARK_GRAY;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the model contain the set of control rods.
    private NuclearReactorModel _nuclearReactorModel;
    
    // A node representing a physical handle on the control rod adjuster, not 
    // to be confused with a file handle.
    private HandleNode _handle;
    
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
                _nuclearReactorModel.getReactorRect().getX()) + (1.5 * _nuclearReactorModel.getReactorWallWidth()));
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

        // Set ourself up to listen for and handle mouse dragging events on
        // the adjuster.
        addInputEventListener( new PDragEventHandler(){
            public void drag(PInputEvent event){
                handleDragEvent( event );
            }
        });
        
        // Add a cursor handler to make it clear to the user that they can
        // grab and move this thing.
        addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );

        // Create and add the handle.
//      GradientPaint handleGradientPaint = new GradientPaint((float)_handle.getOffset().getX(), 
//      (float)_handle.getOffset().getY(), Color.darkGray,
//      (float)_handle.getOffset().getX() + HANDLE_WIDTH, (float)_handle.getOffset().getY(), 
//      Color.white); 
        GradientPaint handleGradientPaint = new GradientPaint(0f, 
                (float)(HANDLE_HEIGHT / 2), Color.white,
                (float)(HANDLE_WIDTH * 1.5), (float)(HANDLE_HEIGHT / 2), 
                HANDLE_COLOR);
        _handle = new HandleNode(HANDLE_WIDTH, HANDLE_HEIGHT, HANDLE_THICKNESS, HANDLE_CORNER_WIDTH,
                handleGradientPaint, Color.BLACK, HANDLE_STROKE);
        _handle.rotate( Math.PI);
        _handle.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        _handle.setOffset( horizSize + HANDLE_WIDTH, -vertSize + HANDLE_HEIGHT + (2 * width));
        _handle.setPaint( Color.RED );

        addChild(_handle);
        
        // Add the textual label to the adjuster.
        PText label = new PText(NuclearPhysicsStrings.CONTROL_ROD_ADJUSTER_LABEL);
        label.setFont( new Font( NuclearPhysicsConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 ) );
        label.rotate( -Math.PI / 2 );
        addChild(label);
        label.setOffset( horizSize - width, -(vertSize / 3) + (label.getWidth() / 2));

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
    
    private void handleDragEvent(PInputEvent event){
        double yDelta = event.getDelta().height;
        _nuclearReactorModel.moveControlRods( yDelta );
    }
}
