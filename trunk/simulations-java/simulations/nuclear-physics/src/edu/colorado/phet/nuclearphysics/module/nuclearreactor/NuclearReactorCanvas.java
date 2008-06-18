/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics.view.NuclearReactorNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the nuclear reactor tab of this simulation.
 *
 * @author John Blanco
 */
public class NuclearReactorCanvas extends PhetPCanvas{
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Canvas dimensions.
    private final double CANVAS_WIDTH = 700;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * 0.87;

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 2.5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private NuclearReactorModel _nuclearReactorModel;
    private NuclearReactorNode  _nuclearReactorNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public NuclearReactorCanvas(NuclearReactorModel nuclearReactorModel) {

        _nuclearReactorModel = nuclearReactorModel;
        
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
        
        // Add the reactor node to the canvas.
        _nuclearReactorNode = new NuclearReactorNode(_nuclearReactorModel, this);
        addWorldChild( _nuclearReactorNode );
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

}
