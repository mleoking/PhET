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
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class PotentialProfilePanel extends ApparatusPanel {
    private NucleusGraphic nucleusGraphic;
    private PotentialProfileGraphic profileGraphic;
    private PotentialProfile potentialProfile;
    private Point2D.Double origin;

    public PotentialProfilePanel( PotentialProfile potentialProfile ) {
        origin = new Point2D.Double( 250, 600 );
        this.setBackground( backgroundColor );
        this.potentialProfile = potentialProfile;
        profileGraphic = new PotentialProfileGraphic( potentialProfile );
        addGraphic( profileGraphic );
    }

    private void setNucleusGraphic( NucleusGraphic nucleusGraphic ) {
        this.nucleusGraphic = nucleusGraphic;
    }

    public PotentialProfile getPotentialProfile() {
        return potentialProfile;
    }

    public void addGraphic( Graphic graphic ) {
        if( graphic instanceof PotentialProfileGraphic ) {
            addPotentialProfile( (PotentialProfileGraphic)graphic );
        }
        if( graphic instanceof NucleusGraphic ) {
            setNucleusGraphic( (NucleusGraphic)graphic );
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
            nucleusGraphic.paintPotentialRendering( g2, (int)xWell, (int)yWell );
            g2.setTransform( orgTx );
        }

        // Paint a dot on the hill for the spot at the same level as the well
        double yTest = -potentialProfile.getWellPotential();
        for( int j = 0; j < 1; j++ ) {
            double xTest = potentialProfile.getAlphaDecayX();
            g2.setColor( Color.red );
            g2.fillOval( (int)xTest + (int)origin.getX() - 5, (int)origin.getY() + (int)yTest - 5, 10, 10 );
        }

    }

    private void addPotentialProfile( PotentialProfileGraphic profileGraphic ) {
        if( this.profileGraphic != null ) {
            removeGraphic( this.profileGraphic );
        }
        this.profileGraphic = profileGraphic;
        this.profileGraphic.setOrigin( origin );
        super.addGraphic( profileGraphic );
    }

    public void removeNucleus() {
        nucleusGraphic = null;
    }

    public void setNucleus( Nucleus nucleus ) {
        nucleusGraphic = new NucleusGraphic( nucleus );
        this.setNucleusGraphic( nucleusGraphic );
        setPotentialProfile( nucleus.getPotentialProfile() );
    }

    public void setPotentialProfile( PotentialProfile potentialProfile ) {
        this.potentialProfile = potentialProfile;
        this.addPotentialProfile( new PotentialProfileGraphic( potentialProfile ) );
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
