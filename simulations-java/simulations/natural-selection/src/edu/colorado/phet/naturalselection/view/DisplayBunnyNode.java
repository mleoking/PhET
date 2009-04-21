/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

public class DisplayBunnyNode extends PNode {

    private PImage whiteImage;
    private PImage brownImage;
    private PPath deadX;

    private Allele color;
    private Allele teeth;
    private Allele tail;

    private boolean dead;

    public DisplayBunnyNode( Allele color, Allele teeth, Allele tail ) {
        whiteImage = NaturalSelectionResources.getImageNode( "bunny_2_white.png" );
        brownImage = NaturalSelectionResources.getImageNode( "bunny_2_brown.png" );
        initDeadX();

        addChild( whiteImage );
        addChild( brownImage );
        addChild( deadX );

        setColor( color );
        setTeeth( teeth );
        setTail( tail );
        setDead( false );
    }

    private void initDeadX() {
        deadX = new PPath();

        deadX.setStroke( new BasicStroke( 20 ) );
        deadX.setStrokePaint( Color.RED );

        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( getBunnyWidth(), getBunnyHeight() );
        path.moveTo( getBunnyWidth(), 0 );
        path.lineTo( 0, getBunnyHeight() );
        deadX.setPathTo( path );
    }

    public void setDead( boolean dead ) {
        this.dead = dead;

        deadX.setVisible( dead );
    }

    public boolean isDead() {
        return dead;
    }

    public void setColor( Allele color ) {
        this.color = color;

        if ( color == ColorGene.WHITE_ALLELE ) {
            whiteImage.setVisible( true );
            brownImage.setVisible( false );
        }
        else if ( color == ColorGene.BROWN_ALLELE ) {
            whiteImage.setVisible( false );
            brownImage.setVisible( true );
        }
    }

    public void setTeeth( Allele teeth ) {
        this.teeth = teeth;
    }

    public void setTail( Allele tail ) {
        this.tail = tail;
    }


    public Allele getColor() {
        return color;
    }

    public Allele getTeeth() {
        return teeth;
    }

    public Allele getTail() {
        return tail;
    }

    public double getBunnyWidth() {
        return whiteImage.getWidth();
    }

    public double getBunnyHeight() {
        return whiteImage.getHeight();
    }

}
