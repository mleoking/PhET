/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck3.model.components.Capacitor;
import edu.colorado.phet.cck3.phetgraphics_cck.HasCapacitorClip;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicCapacitorGraphic extends SchematicPlatedGraphic implements HasCapacitorClip {
    private Capacitor capacitor;
    private CompositePhetGraphic text;
    private Color plusMinusColor = Color.black;

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

    protected void update() {
        super.update();
        double charge = capacitor.getCharge();
//        System.out.println( "charge = " + charge );
        ModelViewTransform2D transform = super.getModelViewTransform2D();
        Capacitor component = capacitor;
        Point2D src = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dst = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
//        double viewThickness = transform.modelToViewDifferentialY( getWireThickness() );

        ImmutableVector2D vector = new ImmutableVector2D.Double( src, dst );
        Point2D cat = vector.getScaledInstance( getFracDistToPlate() ).getDestination( src );
        Point2D ano = vector.getScaledInstance( 1 - getFracDistToPlate() ).getDestination( src );
        AbstractVector2D east = vector.getInstanceOfMagnitude( 1 );
        AbstractVector2D north = east.getNormalVector();

        double maxCharge = 0.2;
        double MAX_NUM_TO_SHOW = 12;
        int numToShow = (int)Math.min( Math.abs( capacitor.getCharge() ) / maxCharge * MAX_NUM_TO_SHOW,
                                       MAX_NUM_TO_SHOW );
        text.clear();
        addCathodeCharges( east.getInstanceOfMagnitude( 8 ).getDestination( cat ), numToShow, north, new ChargeGraphic() {
            public Shape createGraphic( Point2D center ) {
                if( capacitor.getCharge() <= 0 ) {
                    return createPlusGraphic( center );
                }
                else {
                    return createMinusGraphic( center );
                }
            }
        } );
        addAnodeCharges( east.getInstanceOfMagnitude( -8 ).getDestination( ano ), numToShow, north, new ChargeGraphic() {
            public Shape createGraphic( Point2D center ) {
                if( capacitor.getCharge() > 0 ) {
                    return createPlusGraphic( center );
                }
                else {
                    return createMinusGraphic( center );
                }
            }
        } );
    }

    public Shape getCapacitorClip() {
//        double charge = capacitor.getCharge();
//        System.out.println( "charge = " + charge );
        ModelViewTransform2D transform = super.getModelViewTransform2D();
        Capacitor component = capacitor;
        Point2D src = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dst = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
//        double viewThickness = transform.modelToViewDifferentialY( getWireThickness() );

        ImmutableVector2D vector = new ImmutableVector2D.Double( src, dst );
        Point2D cat = vector.getScaledInstance( getFracDistToPlate() ).getDestination( src );
        Point2D ano = vector.getScaledInstance( 1 - getFracDistToPlate() ).getDestination( src );

        Line2D.Double line = new Line2D.Double( cat, ano );
        Stroke str = new BasicStroke( 30, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
        return str.createStrokedShape( line );
    }

    private interface ChargeGraphic {

        Shape createGraphic( Point2D plus );
    }

    private void addCathodeCharges( Point2D root, int numToShow, AbstractVector2D north, ChargeGraphic cg ) {
        for( int i = 0; i < numToShow / 2; i++ ) {
            Point2D center = north.getInstanceOfMagnitude( i * 7 ).getDestination( root );
            PhetShapeGraphic graphic = new PhetShapeGraphic( getComponent(), cg.createGraphic( center ), plusMinusColor );
            text.addGraphic( graphic );
        }
        for( int i = 0; i < numToShow - numToShow / 2; i++ ) {
            Point2D center = north.getInstanceOfMagnitude( - ( i ) * 7 ).getDestination( root );
            PhetShapeGraphic graphic = new PhetShapeGraphic( getComponent(), cg.createGraphic( center ), plusMinusColor );
            text.addGraphic( graphic );
        }
    }

    private void addAnodeCharges( Point2D root, int numToShow, AbstractVector2D north, ChargeGraphic cg ) {
        for( int i = 0; i < numToShow / 2; i++ ) {
            Point2D center = north.getInstanceOfMagnitude( i * 7 ).getDestination( root );
            PhetShapeGraphic graphic = new PhetShapeGraphic( getComponent(), cg.createGraphic( center ), plusMinusColor );
            text.addGraphic( graphic );
        }
        for( int i = 0; i < numToShow - numToShow / 2; i++ ) {
            Point2D center = north.getInstanceOfMagnitude( - ( i ) * 7 ).getDestination( root );

            PhetShapeGraphic graphic = new PhetShapeGraphic( getComponent(), cg.createGraphic( center ), plusMinusColor );
            text.addGraphic( graphic );
        }
    }

    protected Rectangle determineBounds() {
        Rectangle s = super.determineBounds();
        if( text != null && text.getBounds() != null ) {
            s = s.union( text.getBounds() );
        }
        return s;
    }

    private Shape createPlusGraphic( Point2D loc ) {
        double w = 2;
        Area area = new Area( new Rectangle2D.Double( loc.getX() - w, loc.getY(), w * 2 + 1, 1 ) );
        area.add( new Area( new Rectangle2D.Double( loc.getX(), loc.getY() - w, 1, w * 2 + 1 ) ) );
        return area;
    }

    private Shape createMinusGraphic( Point2D loc ) {
        double w = 2;
//        area.add( new Area( new Rectangle2D.Double( loc.getX(), loc.getY() - w, 1, w * 2 + 1 ) ) );
        return new Area( new Rectangle2D.Double( loc.getX() - w, loc.getY(), w * 2 + 1, 1 ) );
    }
}
