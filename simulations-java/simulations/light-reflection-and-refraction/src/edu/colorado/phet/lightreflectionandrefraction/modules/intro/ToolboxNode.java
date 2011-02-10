// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
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
    public ToolboxNode( final LightReflectionAndRefractionCanvas canvas, final ModelViewTransform transform,
                        final BooleanProperty showProtractor, final double x, final double y, BooleanProperty showNormal, final IntensityMeter intensityMeter ) {
        final PText titleLabel = new PText( "Toolbox" ) {{
            setFont( ControlPanelNode.labelFont );
        }};
        addChild( titleLabel );
        final int ICON_HEIGHT = 100;
        final BufferedImage image = BufferedImageUtils.multiScaleToHeight( LightReflectionAndRefractionApplication.RESOURCES.getImage( "protractor.png" ), ICON_HEIGHT );
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


        final double modelWidth = LRRModel.redWavelength * 62;
        final double modelHeight = modelWidth * 0.7;

        IntensityMeter intensityMeterIcon = new IntensityMeter( modelWidth * 0.3, -modelHeight * 0.3, modelWidth * 0.4, -modelHeight * 0.3 );
        intensityMeterIcon.enabled.setValue( true );
        final IntensityMeterNode iconNode = new IntensityMeterNode( transform, intensityMeterIcon );
        final Image myIcon = iconNode.toImage( (int) ( 100.0 * iconNode.getFullBounds().getWidth() / iconNode.getFullBounds().getHeight() ), 100, new Color( 0, 0, 0, 0 ) );
        try {
            ImageIO.write( (RenderedImage) myIcon, "PNG", new File( "C:/Users/Sam/Desktop/test-" + System.currentTimeMillis() ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        System.out.println( "myIcon = " + myIcon );
        final PImage sensor = new PImage( myIcon ) {{
            addInputEventListener( new PBasicInputEventHandler() {
                IntensityMeterNode node = null;

                public void mouseDragged( PInputEvent event ) {
                    intensityMeter.enabled.setValue( true );
                    setVisible( false );
                    if ( node == null ) {
                        node = new IntensityMeterNode( transform, intensityMeter );
                        node.translate( -node.getFullBounds().getWidth() - 20, 0 );//Center on the mouse
                        canvas.addChild( node );
                    }
                    node.doDrag( event );
                }
            } );
            addInputEventListener( new CursorHandler() );
            setOffset( protractor.getFullBounds().getMaxX() + 10, protractor.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }};
        addChild( sensor );

        addChild( new PSwing( new PropertyCheckBox( "Show Normal", showNormal ) {{setBackground( new Color( 0, 0, 0, 0 ) );}} ) {{
            setOffset( sensor.getFullBounds().getMaxX() + 10, sensor.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
        titleLabel.setOffset( getFullBounds().getWidth() / 2 - titleLabel.getFullBounds().getWidth() / 2, 0 );
    }
}