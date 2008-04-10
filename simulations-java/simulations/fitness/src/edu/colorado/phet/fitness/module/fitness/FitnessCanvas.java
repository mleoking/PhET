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
import edu.colorado.phet.fitness.control.CaloriePanel;
import edu.colorado.phet.fitness.control.HumanControlPanel;
import edu.colorado.phet.fitness.view.HumanNode;
import edu.colorado.phet.fitness.view.ScaleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * FitnessCanvas is the canvas for FitnessModule.
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
    private RulerNode rulerNode;
    private PSwing humanControlPanelPSwing;

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
                return AffineTransform.getTranslateInstance( getWidth() * 0.15,
                                                             getHeight() * 0.6 );
            }
        } );
        _model = model;

        setBackground( FitnessConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        _rootNode.addChild( new HumanNode( model.getHuman() ) );
        _rootNode.addChild( new ScaleNode( model.getHuman() ) );
        setZoomEventHandler( new PZoomEventHandler() );
//        setPanEventHandler( new PPanEventHandler() );

        rulerNode = createRulerNode();
        addWorldChild( rulerNode );

        HumanControlPanel humanControlPanel = new HumanControlPanel( model.getHuman() );
        humanControlPanelPSwing = new PSwing( humanControlPanel );
        addScreenChild( humanControlPanelPSwing );

        CaloriePanel caloriePanel = new CaloriePanel();
        caloriePanel.setOffset( humanControlPanelPSwing.getFullBounds().getWidth(), 0 );
        addScreenChild( caloriePanel );

        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING  );
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING  );
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING  );
    }

    private RulerNode createRulerNode() {
        final RulerNode rulerNode = new RulerNode( 1, 0.1, 0.2, new String[]{"0.0", "0.25", "0.5", "0.75", "1.0"}, new PhetDefaultFont(), "m", new PhetDefaultFont(), 4, 0.04, 0.02 );
        rulerNode.rotate( Math.PI * 3 / 2 );
        rulerNode.addInputEventListener( new PDragEventHandler() );
        rulerNode.addInputEventListener( new CursorHandler() );
        rulerNode.setBackgroundStroke( new BasicStroke( 0.01f ) );
        rulerNode.setFontScale( 0.007 );
        rulerNode.setUnitsSpacing( 0.001 );
        rulerNode.setTickStroke( new BasicStroke( 0.01f ) );
        rulerNode.setOffset( 0.7, -1 );
        return rulerNode;
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

        humanControlPanelPSwing.setOffset( 0, getHeight() - humanControlPanelPSwing.getFullBounds().getHeight() );

        //XXX lay out nodes
    }
}