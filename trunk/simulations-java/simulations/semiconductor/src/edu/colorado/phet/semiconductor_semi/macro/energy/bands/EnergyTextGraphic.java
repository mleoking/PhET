package edu.colorado.phet.semiconductor_semi.macro.energy.bands;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.common_semiconductor.view.graphics.shapes.ArrowShape;
import edu.colorado.phet.common_semiconductor.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_semiconductor.view.util.SimStrings;
import edu.colorado.phet.semiconductor_semi.common.TransformGraphic;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jan 19, 2004
 * Time: 3:49:26 PM
 *
 */
public class EnergyTextGraphic extends TransformGraphic {
    private Font font;
    private String text;
    private PhetVector loc;
    private Shape trfShape;
    private Shape arrowShape;
    private Graphics2D graphics2D;
    private Font smallFont;
    private Shape highShape;
    private Shape lowShape;

    public EnergyTextGraphic( ModelViewTransform2D transform, PhetVector loc ) {
        super( transform );
        this.loc = loc;
        text = SimStrings.get( "EnergyTextGraphic.EnergyText" );
        font = new Font( "Lucida Sans", 0, 36 );
        smallFont = new Font( "Dialog", 0, 18 );
    }

    public void paint( Graphics2D graphics2D ) {
        this.graphics2D = graphics2D;
        if( trfShape == null ) {
            recompute();
        }
        graphics2D.setColor( Color.blue );
        graphics2D.fill( trfShape );
        graphics2D.fill( arrowShape );

        graphics2D.setColor( Color.black );
        graphics2D.fill( lowShape );
        graphics2D.fill( highShape );

    }

    private PhetVector getTopCenter( Rectangle2D bounds2D ) {
        return new PhetVector( bounds2D.getX() + bounds2D.getWidth() / 2, bounds2D.getY() );
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

        PhetVector topPoint = getTopCenter( trfShape.getBounds2D() );
        topPoint = topPoint.getSubtractedInstance( 0, 40 );
        PhetVector tipLocation = topPoint.getAddedInstance( 0, -200 );
        arrowShape = new ArrowShape( topPoint, tipLocation, 50, 50, 20 ).getArrowShape();

        highShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(), SimStrings.get( "EnergyTextGraphic.HighText" ) ).getOutline( (float)tipLocation.getX() - 20, (float)tipLocation.getY() - 20 );
        lowShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(), SimStrings.get( "EnergyTextGraphic.LowText" ) ).getOutline( (float)topPoint.getX() - 20, (float)topPoint.getY() + 20 );
    }

    public void update() {
        trfShape = null;
    }

}
