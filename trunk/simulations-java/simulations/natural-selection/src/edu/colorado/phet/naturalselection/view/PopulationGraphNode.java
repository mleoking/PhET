package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class PopulationGraphNode extends PNode {

    public PPath bar;
    public PPath axis;
    public PText label;
    public PText quantity;

    public PopulationGraphNode() {
        axis = new PPath();
        bar = new PPath();
        label = new PText( "# Rabbits" );
        quantity = new PText( "23" );
        
        drawBar();
        drawAxis();
        drawLabels();
    }

    private void drawAxis() {

        axis.setStroke( new BasicStroke( 3f ) );
        axis.setStrokePaint( Color.BLACK );
        //axis.setPaint( Color.WHITE );

        GeneralPath path = new GeneralPath();
        path.moveTo( 15, 170 );
        path.lineTo( 65, 170 );
        //path.closePath();
        axis.setPathTo( path );

        conditionalAdd( axis );
    }

    private void drawLabels() {
        label.setOffset( 40 - label.getWidth() / 2, 180 );
        conditionalAdd( label );

        quantity.setOffset( 40 - quantity.getWidth() / 2, 20 );

        conditionalAdd( quantity );
    }

    private void drawBar() {
        bar.setStroke( new BasicStroke( 1f ) );
        bar.setStrokePaint( Color.BLACK );
        bar.setPaint( Color.WHITE );


        GeneralPath path = new GeneralPath();
        path.moveTo( 25, 40 );
        path.lineTo( 25, 170 );
        path.lineTo( 55, 170 );
        path.lineTo( 55, 40 );
        path.closePath();
        bar.setPathTo( path );

        conditionalAdd( bar );
    }

    private void conditionalAdd( PNode node ) {
        if ( node.getParent() == null ) {
            addChild( node );
        }
    }
}
