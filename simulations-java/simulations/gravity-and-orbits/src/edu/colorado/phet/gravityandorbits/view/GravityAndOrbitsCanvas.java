/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsConstants;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsDefaults;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas template.
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private GravityAndOrbitsModel model;
    private PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    private ModelViewTransform2D modelViewTransform2D;

    public GravityAndOrbitsCanvas( final GravityAndOrbitsModel model, GravityAndOrbitsModule gravityAndOrbitsModule ) {
        super( GravityAndOrbitsDefaults.VIEW_SIZE );
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        this.model = model;

        setBackground( GravityAndOrbitsConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        modelViewTransform2D = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.5 ) ),
                2E-9,
                true );

        BodyNode sunNode = new BodyNode( model.getSun(), modelViewTransform2D );
        addChild( sunNode );

        BodyNode planetNode = new BodyNode( model.getPlanet(), modelViewTransform2D );
        addChild( planetNode );

        final TraceNode node = new TraceNode( model.getPlanet(), modelViewTransform2D, gravityAndOrbitsModule.getTracesProperty() );
        addChild( node );
    }

    public void addChild( PNode node ) {
        _rootNode.addChild( node );
    }
}
