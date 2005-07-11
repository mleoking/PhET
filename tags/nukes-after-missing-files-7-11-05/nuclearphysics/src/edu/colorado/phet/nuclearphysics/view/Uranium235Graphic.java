/**
 * Class: Uranium235Graphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

// todo: make this a CompositePhetGraphic. The nucleusTx should be replaced with setLocation()
public class Uranium235Graphic extends NucleusGraphic {
    private static Font isotopeFont = new Font( "SansSerif", Font.BOLD, 16 );
    private static Font elementFont = new Font( "SansSerif", Font.BOLD, 34 );
    //    private static Font isotopeFont = new Font( "Serif", Font.BOLD, 12 );
    //    private static Font elementFont = new Font( "Serif", Font.BOLD, 30 );
    private static Color color = Color.yellow;
    private static AffineTransform nucleusTx = new AffineTransform();
    private static Stroke fontOutlineStroke = new BasicStroke( 1f );

    private boolean displayLabel = true;

    public Uranium235Graphic( Component component, Nucleus nucleus ) {
        super( component, nucleus );
    }

    public void setDisplayLabel( boolean displayLabel ) {
        this.displayLabel = displayLabel;
    }

    public void paint( Graphics2D g ) {
//        nucleusTx.setToTranslation( getNucleus().getPosition().getX(), getNucleus().getPosition().getY() );
        super.paint( g );

        AffineTransform orgTx = g.getTransform();
//        g.transform( nucleusTx );

        if( displayLabel ) {
            GraphicsUtil.setAntiAliasingOn( g );
            FontRenderContext frc = g.getFontRenderContext();

            g.setColor( color );
            g.setFont( isotopeFont );
            FontMetrics fm = g.getFontMetrics();
            g.drawString( SimStrings.get( "Uranium235Graphic.Number" ), -fm.stringWidth( SimStrings.get( "Uranium235Graphic.Number" ) ), 0 );

            int dy = fm.getHeight() * 3 / 4;
            g.setColor( color );
            g.setFont( elementFont );
            g.drawString( SimStrings.get( "Uranium235Graphic.Symbol" ), 0, dy );
        }
        g.setTransform( orgTx );
    }
}
