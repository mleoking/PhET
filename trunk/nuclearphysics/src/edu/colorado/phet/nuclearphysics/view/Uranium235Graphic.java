/**
 * Class: Uranium235Graphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Uranium235Graphic extends NucleusGraphic {
    private static Font isotopeFont = new Font( "SansSerif", Font.BOLD, 12 );
    private static Font elementFont = new Font( "SansSerif", Font.BOLD, 30 );
//    private static Font isotopeFont = new Font( "Serif", Font.BOLD, 12 );
//    private static Font elementFont = new Font( "Serif", Font.BOLD, 30 );
    private static Color color = Color.yellow;
    private static AffineTransform nucleusTx = new AffineTransform();
    private static Stroke fontOutlineStroke = new BasicStroke( 0.5f );

    private boolean displayLabel = true;

    public Uranium235Graphic( Nucleus nucleus ) {
        super( nucleus );
        this.nucleus = nucleus;
    }

    public void setDisplayLabel( boolean displayLabel ) {
        this.displayLabel = displayLabel;
    }

//    protected Image computeImage() {
//        Image img = super.computeImage();
//        Graphics2D g = (Graphics2D)img.getGraphics();
//        g.setColor( color );
//        g.setFont( isotopeFont );
//        FontMetrics fm = g.getFontMetrics();
//        g.drawString( "235", -fm.stringWidth( "235" ), 0 );
//        int dy = fm.getHeight() * 3 / 4;
//        g.setFont( elementFont );
//        nucleusTx.setToTranslation( img.getWidth( null ) / 2, img.getHeight( null ) / 2 );
//        AffineTransform orgTx = g.getTransform();
//        g.transform( nucleusTx );
//        g.drawString( "U", 0, dy );
//        g.setTransform( orgTx );
//        return img;
//    }

    public void paint( Graphics2D g ) {
        nucleusTx.setToTranslation( nucleus.getLocation().getX(), nucleus.getLocation().getY() );
        super.paint( g );

        AffineTransform orgTx = g.getTransform();
        g.transform( nucleusTx );

        if( displayLabel ) {
            g.setColor( color );
            g.setFont( isotopeFont );
            FontMetrics fm = g.getFontMetrics();
            g.drawString( "235", -fm.stringWidth( "235" ), 0 );
            int dy = fm.getHeight() * 3 / 4;
            g.setFont( elementFont );
            g.drawString( "U", 0, dy );

//            FontRenderContext frc = g.getFontRenderContext();
//                    TextLayout tl = new TextLayout( "U", elementFont, frc);
//                    Shape shape =  tl.getOutline(null);
//            g.setColor( Color.black );
//            g.setStroke( fontOutlineStroke );
//            AffineTransform outlineTx = AffineTransform.getTranslateInstance( 0, dy );
//            g.transform( outlineTx );
//            g.draw( shape );
        }
        g.setTransform( orgTx );
    }
}
