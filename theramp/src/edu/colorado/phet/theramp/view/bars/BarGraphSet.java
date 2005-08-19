/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.ShadowHTMLGraphic;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.common.BarGraphic2D;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampFontSet;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

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
    private double y;
    private double barWidth;
    private double dw;
    private double sep;
    private int dx = 10;
    private int dy = -10;
    private double topY;
    private ShadowHTMLGraphic titleGraphic;
    private XAxis xAxis;
    private PPath background;
    private YAxis yAxis;
    private PSwing minButNode;
    private PNode maximizeButton;
    private ArrayList barGraphics = new ArrayList();
    private double scale = 0.8;

    public BarGraphSet( RampPanel rampPanel, RampPhysicalModel rampPhysicalModel, String title, ModelViewTransform1D transform1D ) {
        this.rampPanel = rampPanel;
        this.rampPhysicalModel = rampPhysicalModel;
        this.transform1D = transform1D;
//        topY = (int)( rampPanel.getRampBaseY() * 0.82 ) + 120;
        topY = ((int)( rampPanel.getRampBaseY() * 0.82 ) + 35)*scale;
        y = 550*scale;
//        barWidth = 23 * scale;
        barWidth = 15* scale;
        dw = 10 * scale;
        sep = barWidth + dw;
        titleGraphic = new ShadowHTMLGraphic( title );
        titleGraphic.setColor( Color.black );
        titleGraphic.setShadowColor( Color.blue );
//        titleGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );
        titleGraphic.setFont( RampFontSet.getFontSet().getBarGraphTitleFont() );

//        addMinimizeButton();
        JButton max = new JButton( "" + title );
        max.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
//        max.setFont( new Font( "Lucida Sans",Font.BOLD, 20) );
        max.setFont( RampFontSet.getFontSet().getNormalButtonFont() );
        maximizeButton = new PSwing( rampPanel, max );
    }


    private void setHasChild( boolean hasChild, PNode child ) {
        if( hasChild && !this.isAncestorOf( child ) ) {
            addChild( child );
        }
        else if( !hasChild && this.isAncestorOf( child ) ) {
            removeChild( child );
        }

    }

    protected void addMinimizeButton() {
        JButton minBut = null;
        try {
            minBut = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/min15.jpg" ) ) );
            minBut.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setMinimized( true );
                }
            } );
            minBut.setMargin( new Insets( 2, 2, 2, 2 ) );
            minButNode = new PSwing( rampPanel, minBut );
            minButNode.setOffset( 5, topY + 10 );
            addChild( minButNode );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        titleGraphic.setOffset( minButNode.getFullBounds().getMaxX() + 2, topY + 10 );

        maximizeButton.setOffset( minButNode.getOffset() );
    }

    public void setMinimized( boolean minimized ) {
        setHasChild( !minimized, this.background );
        setHasChild( !minimized, this.xAxis );
        setHasChild( !minimized, this.yAxis );
        for( int i = 0; i < barGraphics.size(); i++ ) {
            BarGraphic2D barGraphic2D = (BarGraphic2D)barGraphics.get( i );
            setHasChild( !minimized, barGraphic2D );
        }
        setHasChild( !minimized, this.minButNode );
        setHasChild( !minimized, titleGraphic );

        setHasChild( minimized, maximizeButton );
    }

    private class XAxis extends PNode {
        public XAxis() {
            int yValue = transform1D.modelToView( 0 );
            System.out.println( "yValue = " + yValue );
            PPath path = new PPath( new Line2D.Double( 0, y, background.getFullBounds().getWidth(), y ) );
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
        return Math.abs( transform1D.viewToModelDifferential( (int)( y - topY ) ) );
    }

    protected void addClockTickListener( ClockTickListener clockTickListener ) {
        rampPanel.getRampModule().getClock().addClockTickListener( clockTickListener );
    }

    protected void finishInit( ValueAccessor[] workAccess ) {
        double w = workAccess.length * ( sep + dw ) - sep;
        System.out.println( "width = " + barWidth );
        background = new PPath( new Rectangle2D.Double( 0, topY, 5 * 2 + w, 1000 ) );
//        background.setPaint( Color.white );
        background.setPaint( null);
        background.setStroke( new BasicStroke() );
//        background.setStrokePaint( Color.black );
        background.setStrokePaint( null);
        addChild( background );
        xAxis = new XAxis();
        addChild( xAxis );

        yAxis = new YAxis();
        addChild( yAxis );
        for( int i = 0; i < workAccess.length; i++ ) {
            final ValueAccessor accessor = workAccess[i];
            final BarGraphic2D barGraphic = new BarGraphic2D( accessor.getName(), transform1D,
                                                              accessor.getValue( rampPhysicalModel ), (int)( i * sep + dw ), (int)barWidth,
                                                              (int)y, dx, dy, accessor.getColor() );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampPhysicalModel ) );
                }
            } );

            addBarGraphic( barGraphic );
        }

        addChild( titleGraphic );
        addMinimizeButton();
    }

    private void addBarGraphic( BarGraphic2D barGraphic ) {
        addChild( barGraphic );
        barGraphics.add( barGraphic );
    }
}
