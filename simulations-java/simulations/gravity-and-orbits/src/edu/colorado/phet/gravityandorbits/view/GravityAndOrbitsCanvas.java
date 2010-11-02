/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsConstants;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas template.
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private GravityAndOrbitsModel model;

    // View 
    private PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GravityAndOrbitsCanvas( GravityAndOrbitsModel model ) {
        super( GravityAndOrbitsDefaults.VIEW_SIZE );
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        this.model = model;

        setBackground( GravityAndOrbitsConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

    }


    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */

    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GravityAndOrbitsConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
