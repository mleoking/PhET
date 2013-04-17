// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.nuclearphysics.module.nuclearreactor.ControlRod;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A node that represents a control rod in the view.
 *
 * @author John Blanco
 */
public class ControlRodNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final Color        CONTROL_ROD_COLOR = Color.BLUE;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the control rod that this node represents.
    private ControlRod _controlRod;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public ControlRodNode(ControlRod controlRod){
        
        _controlRod = controlRod;

        // Register as a listener for notifications from the control rod.
        _controlRod.addListener( new ControlRod.Listener(){
            public void positionChanged(){
                setOffset(_controlRod.getPosition());
            }
        });
        
        // Create a child node that represents the rectangular control rod.
        Rectangle2D rect = new Rectangle2D.Double(0, 0, _controlRod.getRectangleReference().getWidth(),
                _controlRod.getRectangleReference().getHeight());
        PPath controlRodPath = new PPath(rect);
        GradientPaint gradientPaint = new GradientPaint(0f, (float)(rect.getHeight() / 2), CONTROL_ROD_COLOR,
                (float)(rect.getWidth() * 1.2), (float)rect.getHeight() / 2, Color.white); 
        controlRodPath.setPaint( gradientPaint );
        addChild(controlRodPath);
        
        // Set our offset to correspond to the control rod location.
        setOffset(_controlRod.getPosition());
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
}
