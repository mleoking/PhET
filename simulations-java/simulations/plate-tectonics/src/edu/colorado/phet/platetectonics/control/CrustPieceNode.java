// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.umd.cs.piccolo.PNode;

/**
 * Stylized (cuboid with borders) piece of crust that can be dragged into open spots in the plate motion tab
 */
public class CrustPieceNode extends PNode {

    public final PlateType type;

    public CrustPieceNode( PlateType type, final float height, float intensity ) {
        this.type = type;
        final float topHeight = 20;
        final float rightWidth = 30;

        // given the main intensity, brighten the top and right side respectively
        float topIntensity = 1 - ( 1 - intensity ) * 0.5f;
        float rightIntensity = 1 - ( 1 - intensity ) * 0.3f;

        Color mainColor = new Color( intensity, intensity, intensity, 1f );
        Color topColor = new Color( topIntensity, topIntensity, topIntensity, 1f );
        Color rightColor = new Color( rightIntensity, rightIntensity, rightIntensity, 1f );
        Color strokePaint = new Color( 0.2f, 0.2f, 0.2f, 1f );
        BasicStroke stroke = new BasicStroke( 1 );

        // front
        addChild( new PhetPPath(
                new Rectangle2D.Double( 0, topHeight, CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, height - topHeight ),
                mainColor, stroke, strokePaint ) );

        // top
        addChild( new PhetPPath( new DoubleGeneralPath() {{
            moveTo( 0, topHeight );
            lineTo( rightWidth, 0 );
            lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH, 0 );
            lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, topHeight );
        }}.getGeneralPath(), topColor, stroke, strokePaint ) );

        // right
        addChild( new PhetPPath( new DoubleGeneralPath() {{
            moveTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, topHeight );
            lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH, 0 );
            lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH, height - topHeight );
            lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, height );
        }}.getGeneralPath(), rightColor, stroke, strokePaint ) );

        addInputEventListener( new LWJGLCursorHandler() );
    }
}
