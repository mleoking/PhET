/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.Bunny;

public class BunnyNode extends NaturalSelectionSprite implements Bunny.BunnyListener {
    /*
    private PImage whiteImage;
    private PImage brownImage;

    private boolean isWhite;
      */

    private DisplayBunnyNode displayBunnyNode;

    public BunnyNode( Allele colorPhenotype, Allele teethPhenotype, Allele tailPhenotype ) {
        displayBunnyNode = new DisplayBunnyNode( colorPhenotype, teethPhenotype, tailPhenotype );
        addChild( displayBunnyNode );
    }

    public void setSpriteLocation( double x, double y, double z ) {
        super.setSpriteLocation( x, y, z );

        reposition();
    }

    public void reposition() {
        double scaleFactor = getCanvasScale() * 0.25;
        setScale( scaleFactor );

        double scaledWidth = displayBunnyNode.getBunnyWidth() * scaleFactor;
        double scaledHeight = displayBunnyNode.getBunnyHeight() * scaleFactor;

        Point2D canvasLocation = getCanvasLocation();

        Point2D.Double location = new Point2D.Double( canvasLocation.getX() - scaledWidth / 2, canvasLocation.getY() - scaledHeight );

        setOffset( location );
    }

    public void onBunnyInit( Bunny bunny ) {

    }

    public void onBunnyDeath( Bunny bunny ) {
        setVisible( false );
    }

    public void onBunnyReproduces( Bunny bunny ) {

    }

    public void onBunnyAging( Bunny bunny ) {

    }

    public void onBunnyChangeColor( Allele allele ) {
        displayBunnyNode.setColor( allele );
    }
}
