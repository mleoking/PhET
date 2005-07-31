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

public class Uranium235Graphic extends NucleusGraphic {
    private static Font isotopeFont = new Font( "SansSerif", Font.BOLD, 16 );
    private static Font elementFont = new Font( "SansSerif", Font.BOLD, 34 );
    private static Color color = Color.yellow;
    private static AffineTransform nucleusTx = new AffineTransform();
    private static Stroke fontOutlineStroke = new BasicStroke( 1f );

    private boolean displayLabel = true;

    public Uranium235Graphic( Nucleus nucleus ) {
        super( nucleus );
        this.nucleus = nucleus;
    }

    public void setDisplayLabel( boolean displayLabel ) {
        this.displayLabel = displayLabel;
    }

    public void paint( Graphics2D g ) {
        super.paint( g );

        if( displayLabel ) {
            nucleusTx.setToTranslation( nucleus.getPosition().getX(), nucleus.getPosition().getY() );
            AffineTransform orgTx = g.getTransform();
            g.transform( nucleusTx );

            GraphicsUtil.setAntiAliasingOn( g );

            g.setColor( color );
            g.setFont( isotopeFont );
            FontMetrics fm = g.getFontMetrics();
            g.drawString( SimStrings.get( "Uranium235Graphic.Number" ), -fm.stringWidth( SimStrings.get( "Uranium235Graphic.Number" ) ), 0 );

            int dy = fm.getHeight() * 3 / 4;
            g.setColor( color );
            g.setFont( elementFont );
            g.drawString( SimStrings.get( "Uranium235Graphic.Symbol" ), 0, dy );
            g.setTransform( orgTx );
        }
    }
}
