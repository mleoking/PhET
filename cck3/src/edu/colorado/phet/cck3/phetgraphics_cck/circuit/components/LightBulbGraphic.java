/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck3.common.RoundGradientPaint;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 27, 2004
 * Time: 9:13:28 PM
 * Copyright (c) May 27, 2004 by Sam Reid
 */
public class LightBulbGraphic implements Graphic {
    private Rectangle2D bounds;
    private Ellipse2D.Double bulb;
    private Paint paint;
    private Rectangle2D.Double conductor;
    private Stroke baseStroke = new BasicStroke( 3 );
    private Stroke linestroke = new BasicStroke( 1 );
    private ArrayList spiralLines;
    private Point2D rad;
    private Point2D pin;
    private Stroke bulboutline = new BasicStroke( 1 );
    private ArrayList brighties = new ArrayList();
    private Color brightyColor;
    private BasicStroke brightyStroke;
    private Ellipse2D.Double insulator;
    private Color insulatorColor = Color.white;
    private Rectangle2D.Double tip;
    private Color tipColor = Color.lightGray;
    private Stroke tipStroke = new BasicStroke( 2 );
    private double intensity = -1;//SO WE GET an initialization call.

    public LightBulbGraphic( Rectangle2D bounds ) {
        this.bounds = bounds;

        //reserve the top part for the bulb.
        double fracInsulator = .1;
        double fracTip = .04;
//        double fracConductor = .204;
//        double fracConductor = .304;
        double fracConductor = .23504;
        double fracBulb = 1 - fracInsulator - fracTip - fracConductor;
        double fracConductorWidth = .5;
        double fracTipWidth = .08;
        double fracBulbOverlap = .06;

        double bulbY = bounds.getY();
        double bulbHeight = bounds.getHeight() * fracBulb;
        double bulbWidth = bounds.getWidth();
        double bulbX = bounds.getX();

        double conductorWidth = bounds.getWidth() * fracConductorWidth;
        double conductorX = bounds.getX() + ( bounds.getWidth() - conductorWidth ) / 2;
        double conductorY = bulbHeight + bulbY;
        double conductorHeight = fracConductor * bounds.getHeight();

        double insulatorX = conductorX;
        double insulatorHeight = fracInsulator * bounds.getHeight();
        double insulatorY = conductorY + conductorHeight - insulatorHeight / 2;
        double insulatorWidth = conductorWidth;

        double tipWidth = bounds.getWidth() * fracTipWidth;
        double tipHeight = bounds.getHeight() * fracTip;
        double tipX = bounds.getX() + ( bounds.getWidth() - tipWidth ) / 2;
        double tipY = insulatorY + insulatorHeight;

        bulb = new Ellipse2D.Double( bulbX, bulbY, bulbWidth, bulbHeight + fracBulbOverlap * bounds.getHeight() );

        pin = getPin( bulb );
        rad = new Point2D.Double( bounds.getWidth() / 2, bounds.getHeight() / 2 );
        paint = new RoundGradientPaint( pin.getX(), pin.getY(), Color.white, rad, Color.yellow );
        conductor = new Rectangle2D.Double( conductorX, conductorY, conductorWidth, conductorHeight );

        insulator = new Ellipse2D.Double( insulatorX, insulatorY, insulatorWidth, insulatorHeight );

        tip = new Rectangle2D.Double( tipX, tipY, tipWidth, tipHeight );

        //put angled spiralLines down the conductor rectangle.
        int numLines = 4;
        double dy = conductor.getHeight() / ( numLines );
        spiralLines = new ArrayList();
        for( int i = 0; i < numLines; i++ ) {
            double x1 = conductor.getX();
            double y1 = conductor.getY() + i * dy;
            double x2 = conductor.getX() + conductor.getWidth();
            double y2 = y1 + dy / 2;
            Line2D.Double line = new Line2D.Double( x1, y1, x2, y2 );
            spiralLines.add( line );
        }
        setIntensity( 0 );
    }

    private Point2D getPin( Ellipse2D.Double bulb ) {
        double fracX = .2;
        double fracY = .3;
        return new Point2D.Double( bulb.getX() + bulb.getWidth() * fracX, bulb.getY() + bulb.getHeight() * fracY );
    }

    public void paint( Graphics2D g ) {
        Stroke origStroke = g.getStroke();
        g.setColor( brightyColor );
        g.setStroke( brightyStroke );

        for( int i = 0; i < brighties.size(); i++ ) {
            Line2D.Double aDouble = (Line2D.Double)brighties.get( i );
            g.draw( aDouble );
        }

        g.setColor( Color.black );
        g.setStroke( tipStroke );
        g.draw( tip );
        g.setColor( tipColor );
        g.fill( tip );

        g.setStroke( bulboutline );
        g.setColor( Color.black );
        g.draw( bulb );

//        System.out.println( "intensity = " + intensity );
        if( intensity > 0 ) {
            g.setPaint( paint );
            g.fill( bulb );
        }

        g.setColor( insulatorColor );
        g.fill( insulator );

        g.setColor( Color.black );
        g.setStroke( baseStroke );
        g.draw( conductor );
        g.setColor( Color.lightGray );
        g.fill( conductor );

        g.setStroke( linestroke );
        g.setColor( Color.black );
        for( int i = 0; i < spiralLines.size(); i++ ) {
            Line2D.Double aDouble = (Line2D.Double)spiralLines.get( i );
            g.draw( aDouble );
        }
        g.setStroke( origStroke );
    }

    static Point2D center( Ellipse2D r ) {
        return new Point2D.Double( r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2 );
    }

    public void setIntensity( double intensity ) {
        if( this.intensity == intensity ) {
            return;
        }
        this.intensity = intensity;

        Color yellow = Color.yellow;
        Color pointColor = new Color( 1, 1, 1.0f, (float)intensity );
        Color backgroundColor = new Color( yellow.getRed() / 255.0f, yellow.getGreen() / 255.0f, yellow.getBlue() / 255.0f, (float)intensity );
        paint = new RoundGradientPaint( pin.getX(), pin.getY(), pointColor, rad, backgroundColor );

        int maxBrighties = 40;
        int numBrighties = (int)( intensity * maxBrighties );
        double maxDistance = bulb.getWidth() * 3;
        double distance = intensity * maxDistance;
        double distance0 = Math.max( bulb.getWidth() / 2, bulb.getHeight() / 2 ) * 1.05;
        this.brighties.clear();
        double angleFromDown = Math.PI / 4;
        double startAngle = Math.PI / 2 + angleFromDown;//-Math.PI/2+angleFromDown;
        double endAngle = startAngle + Math.PI * 2 - angleFromDown * 2;//*3/2-angleFromDown;

        double dTheta = Math.abs( endAngle - startAngle ) / ( numBrighties - 1 );

        double angle = startAngle;
        Point2D origin = center( bulb );
        double maxStrokeWidth = 3.5;
        double minStrokeWidth = .5;
        double strokeWidth = minStrokeWidth + intensity * maxStrokeWidth;
        for( int i = 0; i < numBrighties; i++ ) {
            AbstractVector2D vec = ImmutableVector2D.Double.parseAngleAndMagnitude( distance0, angle );
            AbstractVector2D vec1 = ImmutableVector2D.Double.parseAngleAndMagnitude( distance + distance0, angle );

//            System.out.println( "angle = " + angle );
//            System.out.println("angle/Math.PI/2 = " + angle / Math.PI / 2);
            Point2D end = vec.getDestination( origin );
            Point2D end2 = vec1.getDestination( origin );
            Line2D.Double line = new Line2D.Double( end, end2 );
            brighties.add( line );
            angle += dTheta;
        }
        this.brightyStroke = new BasicStroke( (float)strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        this.brightyColor = backgroundColor;
    }

    public static void main( String[] args ) {
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setSize( 800, 800 );
        final ApparatusPanel panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        Color backgroundColor = new Color( 166, 177, 204 );//not so bright
        panel.setBackground( backgroundColor );
        final LightBulbGraphic lbg = new LightBulbGraphic( new Rectangle2D.Double( 180, 150, 60, 60 * 1.4 ) );
        panel.addGraphic( lbg );

        final LightBulbGraphic bulb2 = new LightBulbGraphic( new Rectangle2D.Double( 500, 350, 60, 60 * 1.4 ) );
        panel.addGraphic( bulb2 );

        final JSpinner js = new JSpinner( new SpinnerNumberModel( 0, 0, 1, .0060 ) );
        js.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "LightBulbGraphic.Bulb1Title" ) ) );
        js.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double watts = ( (Number)js.getValue() ).doubleValue();
                lbg.setIntensity( watts );
                panel.repaint();
            }
        } );

        final JSpinner js2 = new JSpinner( new SpinnerNumberModel( 0, 0, 1, .006 ) );
        js2.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "LightBulbGraphic.Bulb2Title" ) ) );
        js2.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double intensity = ( (Number)js2.getValue() ).doubleValue();
                bulb2.setIntensity( intensity );
                panel.repaint();
            }
        } );

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
        buttonPanel.add( js );
        buttonPanel.add( js2 );

        JPanel content = new JPanel( new BorderLayout() );
        content.add( panel, BorderLayout.CENTER );
        content.add( buttonPanel, BorderLayout.SOUTH );
        jf.setContentPane( content );
        jf.setVisible( true );
    }

    public Shape getCoverShape() {
        return conductor;
    }

    public Rectangle2D getFullShape() {
        Rectangle2D fullShape = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        for( int i = 0; i < brighties.size(); i++ ) {
            Line2D.Double aDouble = (Line2D.Double)brighties.get( i );
            fullShape = fullShape.createUnion( aDouble.getBounds2D() );
        }
        return fullShape;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }
}
