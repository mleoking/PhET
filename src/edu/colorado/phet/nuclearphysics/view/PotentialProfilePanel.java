/**
 * Class: PotentialProfilePanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class PotentialProfilePanel extends ApparatusPanel {
    private NucleusGraphic nucleusGraphic;
    private PotentialProfileGraphic profileGraphic;
    private Point2D.Double origin;

    public PotentialProfilePanel() {
        origin = new Point2D.Double( 250, 600 );
        this.setBackground( backgroundColor );
    }

    private void addNucleus( NucleusGraphic nucleusGraphic ) {
        this.nucleusGraphic = nucleusGraphic;
    }

    public void addGraphic( Graphic graphic ) {
        if( graphic instanceof PotentialProfileGraphic ) {
            addPotentialProfile( (PotentialProfileGraphic)graphic );
        }
        if( graphic instanceof NucleusGraphic ) {
            addNucleus( (NucleusGraphic)graphic );
        }
        else {
            super.addGraphic( graphic );
        }
    }

    public void removeGraphic( Graphic graphic ) {
        if( graphic instanceof NucleusGraphic ) {
            removeNucleus();
        }
        else {
            super.removeGraphic( graphic );
        }
    }

    protected void paintComponent( Graphics graphics ) {

        // Center the profile in the panel
        origin.setLocation( this.getWidth() / 2, this.getHeight() * 2 / 3 );
        double xWell = origin.getX();
        double yWell = origin.getY() - profileGraphic.getProfile().getWellPotential() - 10;
        Graphics2D g2 = (Graphics2D)graphics;

        super.paintComponent( graphics );

        // Draw axes
        g2.setColor( axisColor );
        g2.setStroke( axisStroke );
        Line2D.Double xAxis = new Line2D.Double( 0, origin.getY(), this.getWidth(), origin.getY() );
        Line2D.Double yAxis = new Line2D.Double( origin.getX(), 0, origin.getX(), this.getHeight() );
        g2.draw( xAxis );
        g2.draw( yAxis );


        if( nucleusGraphic != null ) {
            AffineTransform atx = scaleInPlaceTx( .3, xWell, yWell );
            AffineTransform orgTx = g2.getTransform();
            g2.setTransform( atx );
            nucleusGraphic.paint( g2, (int)xWell, (int)yWell );
            g2.setTransform( orgTx );
        }
    }

    private void addPotentialProfile( PotentialProfileGraphic profileGraphic ) {
        this.profileGraphic = profileGraphic;
        this.profileGraphic.setOrigin( origin );
        super.addGraphic( profileGraphic );
    }

    private void removeNucleus() {
        nucleusGraphic = null;
    }


    //
    // Statics
    //
    private static Color axisColor = new Color( 100, 100, 100 );
    private static Stroke axisStroke = new BasicStroke( 1f );
    private static Color backgroundColor = new Color( 230, 255, 255 );

    public static AffineTransform scaleInPlaceTx( double scale, double x, double y ) {
        AffineTransform atx = new AffineTransform();
        atx.translate( x, y );
        atx.scale( scale, scale );
        atx.translate( -x, -y );
        return atx;
    }
}
