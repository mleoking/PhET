/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicCapacitorGraphic extends SchematicPlatedGraphic {
    private Capacitor capacitor;
    private CompositePhetGraphic text;

    public SchematicCapacitorGraphic( Component parent, Capacitor component, ModelViewTransform2D transform, double wireThickness ) {
        super( parent, component, transform, wireThickness, 0.325, 3, 3 );
        component.addListener( new Capacitor.Listener() {
            public void chargeChanged() {
                update();
            }
        } );
        this.capacitor = component;

        text = new CompositePhetGraphic( parent );
        update();
    }

    public void paint( Graphics2D g ) {
        super.paint( g );
        text.paint( g );
    }

    private void update() {
        double charge = capacitor.getCharge();
        System.out.println( "charge = " + charge );
        ModelViewTransform2D transform = super.getModelViewTransform2D();
        Capacitor component = capacitor;
        Point2D src = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dst = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
        double viewThickness = transform.modelToViewDifferentialY( getWireThickness() );

        ImmutableVector2D vector = new ImmutableVector2D.Double( src, dst );
        Point2D cat = vector.getScaledInstance( getFracDistToPlate() ).getDestination( src );
        Point2D ano = vector.getScaledInstance( 1 - getFracDistToPlate() ).getDestination( src );
        AbstractVector2D east = vector.getInstanceOfMagnitude( 1 );
        AbstractVector2D north = east.getNormalVector();

        double maxCharge = 0.2;
        text.clear();
        Point2D root = east.getInstanceOfMagnitude( 7 ).getDestination( cat );
        for( int i = 0; i < 10; i++ ) {
            Point2D plus = north.getInstanceOfMagnitude( i * 7 ).getDestination( root );
//            PhetTextGraphic graphic = new PhetTextGraphic( getComponent(), new Font( "Lucida Sans", Font.BOLD, 12 ), "+", Color.black, (int)plus.getX(), (int)plus.getY() );
//            graphic.setPosition( (int)( plus.getX()-graphic.getWidth()/2 ),(int)(plus.getY()));
            PhetShapeGraphic graphic = new PhetShapeGraphic( getComponent(), createPlusGraphic( plus ), Color.blue );
            text.addGraphic( graphic );
        }
    }

    public Rectangle determineBounds() {
        Rectangle s = super.determineBounds();
        if( text != null && text.determineBounds() != null ) {
            s = s.union( text.determineBounds() );
        }
        return s;
    }

    private Shape createPlusGraphic( Point2D plus ) {
        double w = 2;
        Area area = new Area( new Rectangle2D.Double( plus.getX() - w, plus.getY(), w * 2 + 1, 1 ) );
        area.add( new Area( new Rectangle2D.Double( plus.getX(), plus.getY() - w, 1, w * 2 + 1 ) ) );
        return area;
    }
}
