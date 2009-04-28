/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.Bunny;

/**
 * The piccolo node for bunnies in the main simulation canvas
 *
 * @author Jonathan Olson
 */
public class BunnyNode extends NaturalSelectionSprite implements Bunny.BunnyListener {

    /**
     * The graphical representation of the bunny
     */
    private DisplayBunnyNode displayBunnyNode;

    private SpritesNode handler;

    /**
     * Constructor
     *
     * @param colorPhenotype The color
     * @param teethPhenotype The teeth
     * @param tailPhenotype  The tail
     */
    public BunnyNode( Allele colorPhenotype, Allele teethPhenotype, Allele tailPhenotype, SpritesNode handler ) {
        this.handler = handler;
        displayBunnyNode = new DisplayBunnyNode( colorPhenotype, teethPhenotype, tailPhenotype );
        addChild( displayBunnyNode );
    }

    /**
     * Set the 3d location of the bunny
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void setSpriteLocation( double x, double y, double z ) {
        if ( x > getSpriteX() ) {
            displayBunnyNode.setFlipped( false );
        }
        else if ( x < getSpriteX() ) {
            displayBunnyNode.setFlipped( true );
        }
        super.setSpriteLocation( x, y, z );

        reposition();
    }

    public void setFlipped( boolean flipped ) {
        displayBunnyNode.setFlipped( flipped );
    }

    /**
     * Moves the piccolo node to the 2d location referenced by the 3d sprite location
     */
    public void reposition() {
        // how much to scale the bunny by
        double scaleFactor = getCanvasScale() * 0.25;

        setScale( scaleFactor );

        // the width and height of the bunny when scaled
        double scaledWidth = displayBunnyNode.getBunnyWidth() * scaleFactor;
        double scaledHeight = displayBunnyNode.getBunnyHeight() * scaleFactor;

        Point2D canvasLocation = getCanvasLocation();

        Point2D.Double location = new Point2D.Double( canvasLocation.getX() - scaledWidth / 2, canvasLocation.getY() - scaledHeight );

        setOffset( location );
    }

    public void onBunnyInit( Bunny bunny ) {

    }

    public void onBunnyDeath( Bunny bunny ) {
        //setVisible( false );
        handler.removeChildSprite( this );
    }

    public void onBunnyReproduces( Bunny bunny ) {

    }

    public void onBunnyAging( Bunny bunny ) {

    }

    public void onBunnyChangeColor( Allele allele ) {
        displayBunnyNode.setColor( allele );
    }

    public void onBunnyChangeTeeth( Allele allele ) {
        displayBunnyNode.setTeeth( allele );
    }

    public void onBunnyChangeTail( Allele allele ) {
        displayBunnyNode.setTail( allele );
    }

    public void onBunnyChangePosition( double x, double y, double z ) {
        setSpriteLocation( x, y, z );
    }
}
