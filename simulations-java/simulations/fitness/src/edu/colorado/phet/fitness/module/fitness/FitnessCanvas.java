/* Copyright 2007, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.fitness.FitnessConstants;
import edu.colorado.phet.fitness.view.HumanNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * FitnessCanvas is the canvas for FitnessModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FitnessCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private FitnessModel _model;

    // View
    private PNode _rootNode;

    private final double CANVAS_WIDTH = 4;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * ( 3.0d / 4.0d );

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 1.01;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessCanvas( FitnessModel model ) {
//        super( new Rectangle2D.Double( -10,-10,20,20) );
        super( new PDimension( 10, 10 ) );

        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy( this,
                                                              new PDimension( CANVAS_WIDTH, CANVAS_HEIGHT ) ) {
            protected AffineTransform getPreprocessedTransform() {
                return AffineTransform.getTranslateInstance( getWidth() / WIDTH_TRANSLATION_FACTOR,
                                                             getHeight() / HEIGHT_TRANSLATION_FACTOR );
            }
        } );
        _model = model;

        setBackground( FitnessConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        _rootNode.addChild( new HumanNode( model.getHuman() ) );
        setZoomEventHandler( new PZoomEventHandler() );
//        setPanEventHandler( new PPanEventHandler() );

        final RulerNode rulerNode = new RulerNode( 1, 0.1, 0.2, new String[]{"0.0", "0.25", "0.5", "0.75", "1.0"}, new PhetDefaultFont(), "m", new PhetDefaultFont(), 4, 0.04, 0.02 );
        rulerNode.rotate( Math.PI *3/2 );
        rulerNode.addInputEventListener( new PDragEventHandler() );
        rulerNode.addInputEventListener( new CursorHandler() );
        rulerNode.setBackgroundStroke( new BasicStroke( 0.01f ) );
        rulerNode.setFontScale( 0.007 );
        rulerNode.setUnitsSpacing( 0.001 );
        rulerNode.setTickStroke( new BasicStroke( 0.01f ) );
        rulerNode.setOffset( 0.7, -1 );
        addWorldChild( rulerNode );
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */

    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( FitnessConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}