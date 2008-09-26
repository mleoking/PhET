package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.semiconductor.SemiconductorResources;
import edu.colorado.phet.semiconductor.common.TransformGraphic;
import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.shapes.ArrowShape;


/**
 * User: Sam Reid
 * Date: Jan 19, 2004
 * Time: 3:49:26 PM
 */
public class EnergyTextGraphic extends TransformGraphic {
    private Font font;
    private String text;
    private Vector2D.Double loc;
    private Shape trfShape;
    private Shape arrowShape;
    private Graphics2D graphics2D;
    private Font smallFont;
    private Shape highShape;
    private Shape lowShape;

    public EnergyTextGraphic( ModelViewTransform2D transform, Vector2D.Double loc ) {
        super( transform );
        this.loc = loc;
        text = SemiconductorResources.getString( "EnergyTextGraphic.EnergyText" );
        font = new PhetFont( Font.PLAIN, 36 );
        smallFont = new PhetFont( 18 );
    }

    public void paint( Graphics2D graphics2D ) {
        this.graphics2D = graphics2D;
        if ( trfShape == null ) {
            recompute();
        }
        graphics2D.setColor( Color.blue );
        graphics2D.fill( trfShape );
        graphics2D.fill( arrowShape );

        graphics2D.setColor( Color.black );
        graphics2D.fill( lowShape );
        graphics2D.fill( highShape );

    }

    private Vector2D.Double getTopCenter( Rectangle2D bounds2D ) {
        return new Vector2D.Double( bounds2D.getX() + bounds2D.getWidth() / 2, bounds2D.getY() );
    }

    public void recompute() {
        GlyphVector vector = font.createGlyphVector( graphics2D.getFontRenderContext(), text );
        Shape outline = vector.getOutline();
//        AffineTransform trf=AffineTransform.getRotateInstance(Math.PI/2);
        Point viewPt = getTransform().modelToView( loc );
        AffineTransform trf = new AffineTransform();
        trf.translate( viewPt.x - 15, viewPt.y );
        trf.rotate( -Math.PI / 2 );
        trfShape = trf.createTransformedShape( outline );

        AbstractVector2D topPoint = getTopCenter( trfShape.getBounds2D() );
        topPoint = topPoint.getSubtractedInstance( 0, 40 );
        AbstractVector2D tipLocation = topPoint.getAddedInstance( 0, -200 );
        arrowShape = new ArrowShape( topPoint, tipLocation, 50, 50, 20 ).getArrowShape();

        highShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(), SemiconductorResources.getString( "EnergyTextGraphic.HighText" ) ).getOutline( (float) tipLocation.getX() - 20, (float) tipLocation.getY() - 20 );
        lowShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(), SemiconductorResources.getString( "EnergyTextGraphic.LowText" ) ).getOutline( (float) topPoint.getX() - 20, (float) topPoint.getY() + 20 );
    }

    public void update() {
        trfShape = null;
    }

}
