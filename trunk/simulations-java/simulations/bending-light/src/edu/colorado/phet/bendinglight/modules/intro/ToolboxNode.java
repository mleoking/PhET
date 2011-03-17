// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;

import edu.colorado.phet.bendinglight.model.IntensityMeter;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.IntensityMeterNode;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.CHARACTERISTIC_LENGTH;

/**
 * Toolbox from which the user can drag (or otherwise enable) tools.
 *
 * @author Sam Reid
 */
public class ToolboxNode extends VBox {
    public static final int ICON_WIDTH = 110;

    public ToolboxNode( final BendingLightCanvas canvas,
                        final ModelViewTransform transform,
                        final PNode protractorTool,
                        final PNode[] moreTools,
                        final IntensityMeter intensityMeter,
                        final BooleanProperty showNormal ) {
        super( 2 );
        final PText titleLabel = new PText( "Toolbox" ) {{
            setFont( BendingLightCanvas.labelFont );
        }};
        addChild( titleLabel );
        addChild( protractorTool );
        for ( PNode moreTool : moreTools ) {
            addChild( moreTool );
        }

        final double modelWidth = CHARACTERISTIC_LENGTH * 62;
        final double modelHeight = modelWidth * 0.7;
        IntensityMeterNode iconNode = new IntensityMeterNode( transform, new IntensityMeter( modelWidth * 0.3, -modelHeight * 0.3, modelWidth * 0.4, -modelHeight * 0.3 ) {{
            enabled.setValue( true );
        }} );
        int sensorIconHeight = (int) ( iconNode.getFullBounds().getHeight() / iconNode.getFullBounds().getWidth() * ICON_WIDTH );
        addChild( new IntensitySensorTool( canvas, transform, intensityMeter, modelWidth, modelHeight, this, iconNode, sensorIconHeight ) );

        final PImage normalLineThumbnail = new PImage( new NormalLine( transform, modelHeight, 9, 30, 30 ).toImage( 5, 67, new Color( 0, 0, 0, 0 ) ) );
        final PSwing showNormalCheckBox = new PSwing( new PropertyCheckBox( "Show Normal", showNormal ) {{
            setFont( BendingLightCanvas.labelFont );
            setBackground( new Color( 0, 0, 0, 0 ) );
        }} );
        addChild( showNormalCheckBox );
        addChild( normalLineThumbnail );
    }
}