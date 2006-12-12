/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view.bargraphs;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.EnergyLookAndFeel;
import edu.colorado.phet.ec3.EnergySkateParkSimulationPanel;
import edu.colorado.phet.ec3.model.EnergySkateParkModel;
import edu.colorado.phet.piccolo.nodes.ShadowHTMLGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:16:45 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class BarGraphSet extends PNode {
    private EnergySkateParkSimulationPanel rampPanel;
    private EnergySkateParkModel rampPhysicalModel;
    private ModelViewTransform1D transform1D;
    private double barChartHeight;
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
    private boolean minimized = false;
    private ValueAccessor[] valueAccessors;

    public BarGraphSet( EnergySkateParkSimulationPanel energySkateParkSimulationPanel, EnergySkateParkModel energySkateParkModel,
                        String title, ModelViewTransform1D transform1D ) {
        this.rampPanel = energySkateParkSimulationPanel;
        this.rampPhysicalModel = energySkateParkModel;
        this.transform1D = transform1D;
        topY = 0;
        barChartHeight = 400;
        barWidth = 20;
        dw = 7;
        sep = barWidth + dw;
        titleGraphic = new ShadowHTMLGraphic( title );
        titleGraphic.setColor( Color.black );
        titleGraphic.setShadowColor( Color.blue );
        titleGraphic.setFont( getTitleFont() );

        JButton max = new JButton( "" + title );
        max.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
        max.setBackground( Color.green );
        maximizeButton = new PSwing( energySkateParkSimulationPanel, max );
    }

    private Font getTitleFont() {
        return new Font( "Lucida Sans", Font.BOLD, 18 );
    }

    public void setBarChartHeight( double baselineY ) {
        this.barChartHeight = baselineY;
        xAxis.setBarChartHeight( baselineY );
        yAxis.setBarChartHeight( baselineY );
        for( int i = 0; i < barGraphics.size(); i++ ) {
            BarGraphic2D barGraphic2D = (BarGraphic2D)barGraphics.get( i );
            barGraphic2D.setBarHeight( baselineY );
        }
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
        this.minimized = minimized;
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

        relayoutPiccolo();
    }

    private void relayoutPiccolo() {
        //rampPanel.relayoutPiccolo();
        //TODO
    }

    public boolean isMinimized() {
        return minimized;
    }

    public EnergyLookAndFeel getLookAndFeel() {
        return rampPanel.getEnergyConservationModule().getEnergyLookAndFeel();
    }

    public ModelViewTransform1D getTransform1D() {
        return new ModelViewTransform1D( transform1D );
    }

    public void setTransform1D( ModelViewTransform1D modelViewTransform1D ) {
        transform1D.setTransform( modelViewTransform1D );
    }

    private class XAxis extends PNode {
        private PPath path;

        public XAxis() {
            int yValue = transform1D.modelToView( 0 );
//            System.out.println( "yValue = " + yValue );
            path = new PPath( createLinePath() );
            addChild( path );
            path.setStrokePaint( new Color( 255, 150, 150 ) );
            path.setStroke( new BasicStroke( 3 ) );
        }

        private Line2D.Double createLinePath() {
            return new Line2D.Double( 0, barChartHeight, background.getFullBounds().getWidth(), barChartHeight );
        }

        public void setBarChartHeight( double baselineY ) {
            path.setPathTo( createLinePath() );
        }
    }

    private class YAxis extends PNode {
        private PPath path;

        public YAxis() {
            GeneralPath shape = createShape();
            path = new PPath( shape );
            path.setPaint( Color.black );
            addChild( path );
        }

        private GeneralPath createShape() {
            Point2D origin = new Point2D.Double( 0, barChartHeight );
            Point2D dst = new Point2D.Double( 0, topY + 25 );
            try {
                Arrow arrow = new Arrow( origin, dst, 8, 8, 3 );
                GeneralPath shape = arrow.getShape();
                return shape;
            }
            catch( RuntimeException re ) {
                re.printStackTrace();
                return new GeneralPath();
            }
        }

        public void setBarChartHeight( double baselineY ) {
            path.setPathTo( createShape() );
        }
    }

    public double getMaxDisplayableEnergy() {//TODO! trace dependencies on this (nondynamic ones)
        return Math.abs( transform1D.viewToModelDifferential( (int)( barChartHeight - topY ) ) );
    }

    private void addBarGraphic( BarGraphic2D barGraphic ) {
        addChild( barGraphic );
        barGraphics.add( barGraphic );
    }

    protected void finishInit( ValueAccessor[] valueAccessors ) {
        this.valueAccessors = valueAccessors;
        double w = valueAccessors.length * ( sep + dw ) - sep;
//        System.out.println( "width = " + barWidth );
        background = new PPath( new Rectangle2D.Double( 0, topY, 2 + w, 1000 ) );
        background.setPaint( null );
        background.setStroke( new BasicStroke() );
        background.setStrokePaint( null );
        addChild( background );
        xAxis = new XAxis();
        addChild( xAxis );

        yAxis = new YAxis();
        addChild( yAxis );
        for( int i = 0; i < valueAccessors.length; i++ ) {
            final ValueAccessor accessor = valueAccessors[i];
            final BarGraphic2D barGraphic = new BarGraphic2D( accessor.getName(), transform1D,
                                                              accessor.getValue( rampPhysicalModel ), (int)( i * sep + dw ), (int)barWidth,
                                                              (int)barChartHeight, dx, dy, accessor.getColor(), new Font( "Lucida Sans", Font.BOLD, 14 ) );
            addBarGraphic( barGraphic );
        }
        addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                update();
            }
        } );
        addChild( titleGraphic );
        addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
//        addMinimizeButton();
    }

    public void update() {
        if( getVisible() ) {
            for( int i = 0; i < barGraphics.size(); i++ ) {
                BarGraphic2D barGraphic2D = (BarGraphic2D)barGraphics.get( i );
                barGraphic2D.setValue( valueAccessors[i].getValue( rampPhysicalModel ) );
            }
        }
    }

    protected void addClockListener( ClockListener ClockListener ) {
        rampPanel.getEnergyConservationModule().getClock().addClockListener( ClockListener );
    }
}
