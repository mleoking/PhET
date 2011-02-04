// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.view.LightRayNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas extends PhetPCanvas {
    private PNode rootNode;

    public LightReflectionAndRefractionCanvas( final LRRModel model ) {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, LRRModel.STAGE_SIZE ) );

        setBackground( Color.black );

        final ModelViewTransform transform = ModelViewTransform.createRectangleInvertedYMapping(
                new Rectangle2D.Double( -model.getWidth() / 2, -model.getHeight() / 2, model.getWidth(), model.getHeight() ),
                new Rectangle2D.Double( 0, 0, LRRModel.STAGE_SIZE.width, LRRModel.STAGE_SIZE.height ) );

        addChild( new PSwing( new VerticalLayoutPanel() {{
            add( new PropertyRadioButton<Boolean>( "On", model.laserOn, true ) {{
                setForeground( Color.white );
                setFont( new PhetFont( 18, true ) );
            }} );
            add( new PropertyRadioButton<Boolean>( "Off", model.laserOn, false ) {{
                setForeground( Color.white );
                setFont( new PhetFont( 18, true ) );
            }} );
            SwingUtils.setBackgroundDeep( this, Color.black );
        }} ) );
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                addChild( new LightRayNode( transform, lightRay ) );
            }
        } );
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}
