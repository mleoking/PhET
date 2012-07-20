// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.macro;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.conductivity.ConductivityResources;
import edu.colorado.phet.conductivity.common.ArrowShape;
import edu.colorado.phet.conductivity.common.TransformGraphic;

public class EnergyTextGraphic extends TransformGraphic {

    public EnergyTextGraphic( ModelViewTransform2D modelviewtransform2d, MutableVector2D phetvector ) {
        super( modelviewtransform2d );
        loc = phetvector;
        text = ConductivityResources.getString( "EnergyTextGraphic.EnergyText" );
        font = new PhetFont( Font.PLAIN, 36 );
        smallFont = new PhetFont( Font.PLAIN, 18 );
    }

    public void paint( Graphics2D graphics2d ) {
        graphics2D = graphics2d;
        if ( trfShape == null ) {
            recompute();
        }
        graphics2d.setColor( Color.blue );
        graphics2d.fill( trfShape );
        graphics2d.fill( arrowShape );
        graphics2d.setColor( Color.black );
        graphics2d.fill( lowShape );
        graphics2d.fill( highShape );
    }

    private MutableVector2D getTopCenter( Rectangle2D rectangle2d ) {
        return new MutableVector2D( rectangle2d.getX() + rectangle2d.getWidth() / 2D, rectangle2d.getY() );
    }

    public void recompute() {
        GlyphVector glyphvector = font.createGlyphVector( graphics2D.getFontRenderContext(), text );
        Shape shape = glyphvector.getOutline();
        Point point = getTransform().modelToView( loc );
        AffineTransform affinetransform = new AffineTransform();
        affinetransform.translate( point.x - 15, point.y );
        affinetransform.rotate( -1.5707963267948966D );
        trfShape = affinetransform.createTransformedShape( shape );
        AbstractVector2D phetvector = getTopCenter( trfShape.getBounds2D() );
        phetvector = phetvector.minus( 0.0D, 40D );
        Vector2D phetvector1 = phetvector.plus( 0.0D, -200D );
        arrowShape = ( new ArrowShape( phetvector, phetvector1, 50D, 50D, 20D ) ).getArrowPath();
        highShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(),
                                                 ConductivityResources.getString( "EnergyTextGraphic.HighText" ) ).getOutline( (float) phetvector1.getX() - 20F,
                                                                                                                               (float) phetvector1.getY() - 20F );
        lowShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(),
                                                ConductivityResources.getString( "EnergyTextGraphic.LowText" ) ).getOutline( (float) phetvector.getX() - 20F,
                                                                                                                             (float) phetvector.getY() + 20F );
    }

    public void update() {
        trfShape = null;
    }

    private Font font;
    private String text;
    private MutableVector2D loc;
    private Shape trfShape;
    private Shape arrowShape;
    private Graphics2D graphics2D;
    private Font smallFont;
    private Shape highShape;
    private Shape lowShape;
}
