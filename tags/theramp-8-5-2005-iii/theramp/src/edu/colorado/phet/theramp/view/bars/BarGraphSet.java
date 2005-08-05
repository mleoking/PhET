/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.piccolo.ShadowHTMLGraphic;
import edu.colorado.phet.theramp.common.BarGraphic2D;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:16:45 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class BarGraphSet extends PNode {
    private RampPanel rampPanel;
    private RampPhysicalModel rampPhysicalModel;
    private ModelViewTransform1D transform1D;
    private int y;
    private int barWidth;
    private int dw;
    private int sep;
    private int dx = 10;
    private int dy = -10;
    private int topY;
    private ShadowHTMLGraphic titleGraphic;
    private XAxis xAxis;
    private PPath energyBackground;
    private YAxis yAxis;

    public BarGraphSet( RampPanel rampPanel, RampPhysicalModel rampPhysicalModel, String title, ModelViewTransform1D transform1D ) {
        this.rampPanel = rampPanel;
        this.rampPhysicalModel = rampPhysicalModel;
        this.transform1D = transform1D;
        topY = (int)( rampPanel.getRampBaseY() * 0.82 ) + 120;
        y = 750;
        barWidth = 23;
        dw = 10;
        sep = barWidth + dw;
        titleGraphic = new ShadowHTMLGraphic( title );
        titleGraphic.setColor( Color.black );
        titleGraphic.setShadowColor( Color.blue );
        titleGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );
        titleGraphic.setOffset( 5, topY + 10 );

    }

    private class XAxis extends PNode {
        public XAxis() {
            int yValue = transform1D.modelToView( 0 );
            System.out.println( "yValue = " + yValue );
            PPath path = new PPath( new Line2D.Double( 0, y, energyBackground.getFullBounds().getWidth(), y ) );
            addChild( path );
            path.setStrokePaint( new Color( 255, 150, 150 ) );
            path.setStroke( new BasicStroke( 3 ) );
        }
    }

    private class YAxis extends PNode {
        public YAxis() {
            Point2D origin = new Point2D.Double( 0, y );
            Point2D dst = new Point2D.Double( 0, topY + 25 );
//            Vector2D vector = new Vector2D.Double( 0, y - topY + 100 );
            Arrow arrow = new Arrow( origin, dst, 8, 8, 3 );
            PPath path = new PPath( arrow.getShape() );
            path.setPaint( Color.black );
            addChild( path );
        }
    }

    protected RampLookAndFeel getLookAndFeel() {
        return rampPanel.getLookAndFeel();
    }

    public double getMaxDisplayableEnergy() {
        return Math.abs( transform1D.viewToModelDifferential( y - topY ) );
    }

    protected void addClockTickListener( ClockTickListener clockTickListener ) {
        rampPanel.getRampModule().getClock().addClockTickListener( clockTickListener );
    }

    public void setAccessors( ValueAccessor[] workAccess ) {
        int w = workAccess.length * ( sep + dw ) - sep;
        System.out.println( "width = " + barWidth );
        energyBackground = new PPath( new Rectangle2D.Double( 0, topY, 5 * 2 + w, 1000 ) );
        energyBackground.setPaint( Color.white );
        energyBackground.setStroke( new BasicStroke() );
        energyBackground.setStrokePaint( Color.black );
        addChild( energyBackground );
        xAxis = new XAxis();
        addChild( xAxis );

        yAxis = new YAxis();
        addChild( yAxis );
        for( int i = 0; i < workAccess.length; i++ ) {
            final ValueAccessor accessor = workAccess[i];
            final BarGraphic2D barGraphic = new BarGraphic2D( accessor.getName(), transform1D,
                                                              accessor.getValue( rampPhysicalModel ), i * sep + dw, barWidth
                                                              , y, dx, dy, accessor.getColor() );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampPhysicalModel ) );
                }
            } );
            addChild( barGraphic );
        }

        addChild( titleGraphic );
    }
}
