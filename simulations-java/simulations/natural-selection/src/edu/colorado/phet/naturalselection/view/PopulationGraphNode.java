package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class PopulationGraphNode extends PNode {

    public PPath bar;
    public PText label;
    public PText quantity;

    public PopulationGraphNode() {
        bar = new PPath();

        bar.setStroke( new BasicStroke( 1f ) );
        bar.setStrokePaint( Color.BLACK );
        bar.setPaint( Color.WHITE );
        

        GeneralPath path = new GeneralPath();
        path.moveTo( 15, 40 );
        path.lineTo( 15, 180 );
        path.lineTo( 65, 180 );
        path.lineTo( 65, 40 );
        path.closePath();
        bar.setPathTo( path );

        addChild( bar );

        label = new PText( "# Rabbits" );
        //label.setJustification( 0.5f );
        label.setOffset( 40 - label.getWidth() / 2, 180 );
        addChild( label );

        quantity = new PText( "23" );
        quantity.setOffset( 40 - quantity.getWidth() / 2, 20 );
        //quantity.setJustification( 0.5f );
        addChild( quantity );
    }
}
