// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Todo: see EnergyPositionPlotCanvas for an error in the offset.
 */

public class GridNode extends PhetPNode {
    private final PNode lines = new PNode();
    private final PNode textLayer = new PNode();
    private Paint gridPaint = null;
    private final double highlightX;

    public GridNode( double minX, double minY, double maxX, double maxY, double dx, double dy, double highlightX ) {
        this.highlightX = highlightX;
        addChild( lines );
        addChild( textLayer );
        for ( double x = minX; x <= maxX; x += dx ) {
            lines.addChild( createXLineNode( minY, maxY, x ) );
            if ( x % 2 == 0 ) {
                String aText = "" + (int) x;
                if ( aText.equals( "0" ) ) {
                    aText = PhetCommonResources.formatValueUnits( "0", EnergySkateParkResources.getString( "units.meters" ) );
                }
                PText text = new PText( aText );
                text.setOffset( x + dx + dx * 0.2, minY );

                text.setScale( 0.03f );
                text.getTransformReference( true ).scale( 1, -1 );

                textLayer.addChild( text );
            }
        }
        for ( double y = minY; y <= maxY; y += dy ) {
            lines.addChild( createYLineNode( minX, maxX, y ) );
            if ( y % 2 == 0 ) {
                String aText = "" + (int) y;
                {
                    PText text = new PText( aText );
                    text.setOffset( dx * highlightX - dx * 0.05 - text.getFullBounds().getWidth() * 0.03f, y + dy * 0.05 );
                    text.setScale( 0.03f );
                    text.getTransformReference( true ).scale( 1, -1 );
                    textLayer.addChild( text );
                }

                if ( aText.equals( "0" ) ) {
                    String meters = EnergySkateParkResources.getString( "units.meters" );
                    PText text = new PText( meters );
                    text.setOffset( dx * highlightX + dx * 0.05, y + dy * 0.05 );
                    text.setScale( 0.03f );
                    text.getTransformReference( true ).scale( 1, -1 );
                    textLayer.addChild( text );
                }

            }
        }
        setPickable( false );
        setChildrenPickable( false );

        final int level = 128;
        setLinePaint( new Color( level, level, level ) );
        setFontPaint( Color.black );
    }

    public void setGridPaint( Paint paint ) {
        if ( this.gridPaint == null || !this.gridPaint.equals( paint ) ) {
            this.gridPaint = paint;
            setLinePaint( paint );
            setFontPaint( paint );
        }
    }

    private void setFontPaint( Paint paint ) {
        for ( int i = 0; i < textLayer.getChildrenCount(); i++ ) {
            PText t = (PText) textLayer.getChild( i );
            t.setTextPaint( paint );
        }
    }

    public void setLinePaint( Paint linePaint ) {
        for ( int i = 0; i < lines.getChildrenCount(); i++ ) {
            PPath line = (PPath) lines.getChild( i );
            line.setStrokePaint( linePaint );
        }
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( false );
        setChildrenPickable( false );
    }

    private PNode createYLineNode( double minX, double maxX, double y ) {
        PPath child = new PPath( new Line2D.Double( minX, y, maxX, y ) ) {

            //Use low quality rendering to improve performance when grid is turned on
            @Override protected void paint( PPaintContext paintContext ) {
                int renderQuality = paintContext.getRenderQuality();
                paintContext.setRenderQuality( PPaintContext.LOW_QUALITY_RENDERING );
                super.paint( paintContext );
                paintContext.setRenderQuality( renderQuality );
            }
        };
        boolean thickStroke = MathUtil.isApproxEqual( y, 0, 0.001 );

        //On 11/29/2011 It was requested to make every other line thick instead of every 5 lines.
        if ( (int) y % 2 == 0 ) {
            thickStroke = true;
        }
        child.setStroke( new BasicStroke( 0.01f * ( thickStroke ? 3 : 1 ) ) );
        return child;
    }

    private PNode createXLineNode( double minY, double maxY, double x ) {
        PPath child = new PPath( new Line2D.Double( x, minY, x, maxY ) ) {

            //Use low quality rendering to improve performance when grid is turned on
            @Override protected void paint( PPaintContext paintContext ) {
                int renderQuality = paintContext.getRenderQuality();
                paintContext.setRenderQuality( PPaintContext.LOW_QUALITY_RENDERING );
                super.paint( paintContext );
                paintContext.setRenderQuality( renderQuality );
            }
        };
        boolean thickStroke = MathUtil.isApproxEqual( x, highlightX, 0.001 );
        child.setStroke( new BasicStroke( 0.01f * ( thickStroke ? 3 : 1 ) ) );
        return child;
    }
}
