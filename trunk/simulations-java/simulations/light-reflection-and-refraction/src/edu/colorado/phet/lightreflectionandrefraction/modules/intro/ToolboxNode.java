// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class ToolboxNode extends PNode {
    public ToolboxNode( final LightReflectionAndRefractionCanvas canvas, final ModelViewTransform transform, final BooleanProperty showProtractor, final double x, final double y, BooleanProperty showNormal ) {
        final PText titleLabel = new PText( "Toolbox" ) {{
            setFont( ControlPanelNode.labelFont );
        }};
        addChild( titleLabel );
        final BufferedImage image = BufferedImageUtils.multiScaleToHeight( LightReflectionAndRefractionApplication.RESOURCES.getImage( "protractor.png" ), 100 );
        final PImage protractor = new PImage( image ) {{
            setOffset( 0, titleLabel.getFullBounds().getMaxY() );
            addInputEventListener( new PBasicInputEventHandler() {
                ProtractorNode node = null;

                public void mouseDragged( PInputEvent event ) {
                    showProtractor.setValue( true );
                    setVisible( false );
                    if ( node == null ) {
                        final Point2D positionRelativeTo = event.getPositionRelativeTo( getParent().getParent().getParent() );//why?
                        Point2D model = transform.viewToModel( positionRelativeTo );
                        node = new ProtractorNode( transform, showProtractor, model.getX(), model.getY() );
                        node.translate( -node.getFullBounds().getWidth() / 2, node.getFullBounds().getHeight() / 2 );//Center on the mouse
                        canvas.addChild( node );
                    }
                    node.doDrag( event );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};
        addChild( protractor );

        addChild( new PSwing( new PropertyCheckBox( "Show Normal", showNormal ) {{setBackground( new Color( 0, 0, 0, 0 ) );}} ) {{
            setOffset( protractor.getFullBounds().getMaxX() + 10, protractor.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
        titleLabel.setOffset( getFullBounds().getWidth() / 2 - titleLabel.getFullBounds().getWidth() / 2, 0 );
    }
}