/**
 * Class: Uranium235Graphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Uranium238Graphic extends NucleusGraphic {
    private static Font isotopeFont = new Font( "SansSerif", Font.BOLD, 12 );
    private static Font elementFont = new Font( "SansSerif", Font.BOLD, 30 );
    //    private static Font isotopeFont = new Font( "Serif", Font.BOLD, 12 );
    //    private static Font elementFont = new Font( "Serif", Font.BOLD, 30 );
    private static Font font = new Font( "Serif", Font.BOLD, 18 );
    private static Color color = Color.cyan;
    //    private static Color color = Color.green;
    private static AffineTransform nucleusTx = new AffineTransform();

    public Uranium238Graphic( Component component, Nucleus nucleus ) {
        super( component, nucleus );
        this.nucleus = nucleus;
    }

    public void paint( Graphics2D g ) {
        nucleusTx.setToTranslation( nucleus.getPosition().getX(), nucleus.getPosition().getY() );
        super.paint( g );

        AffineTransform orgTx = g.getTransform();
        g.transform( nucleusTx );

        g.setColor( color );
        g.setFont( isotopeFont );
        FontMetrics fm = g.getFontMetrics();
        g.drawString( SimStrings.get( "Uranium238Graphic.Number" ), -fm.stringWidth( SimStrings.get( "Uranium238Graphic.Number" ) ), 0 );
        int dy = fm.getHeight() * 3 / 4;
        g.setFont( elementFont );
        g.drawString( SimStrings.get( "Uranium238Graphic.Symbol" ), 0, dy );

        g.setTransform( orgTx );
    }
}
