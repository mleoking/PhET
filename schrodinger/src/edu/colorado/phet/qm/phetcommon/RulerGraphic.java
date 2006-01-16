/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 16, 2006
 * Time: 2:37:08 PM
 * Copyright (c) Jan 16, 2006 by Sam Reid
 */

public class RulerGraphic extends PNode {
    private PPath base;
    private String[] readings;
    private String units;
    private int horizontalInset;
    private int numMinorTicksBetweenMajors = 5;
    private double majorTickHeight = 10;
    private double minorTickHeight = 6;

    public RulerGraphic( String[] readings, String units, int width, int height ) {
        this.units = units;
        Color backgroundColor = new Color( 236, 225, 113 );
        base = new PPath();
        base.setPaint( backgroundColor );
        base.setStrokePaint( Color.black );
        base.setStroke( new BasicStroke() );
        this.readings = readings;
        horizontalInset = 10;

        setBounds( 0, 0, width, height );

    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        removeAllChildren();
        base.setPathToRectangle( (float)x, (float)y, (float)width, (float)height );
        addChild( base );

        double rulerDist = width - horizontalInset * 2;
        double distBetweenMajorReadings = rulerDist / ( readings.length - 1 );
        double distBetweenMinor = distBetweenMajorReadings / numMinorTicksBetweenMajors;
        for( int i = 0; i < readings.length; i++ ) {
            String reading = readings[i];
            PText pText = new PText( reading );
            double xVal = distBetweenMajorReadings * i + horizontalInset;
            double yVal = height / 2 - pText.getFullBounds().getHeight() / 2;
            pText.setOffset( xVal - pText.getFullBounds().getWidth() / 2, yVal );
            addChild( pText );

            DoubleGeneralPath tickPath = createTickPair( xVal, height, majorTickHeight );
//            PPath majorTick = new PPath( tickPath.getGeneralPath(), new BasicStroke( 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER ) );
            PPath majorTick = new PPath( tickPath.getGeneralPath(), new BasicStroke() );
            majorTick.setStrokePaint( Color.black );
            addChild( majorTick );

            if( i < readings.length - 1 ) {
                for( int k = 1; k < numMinorTicksBetweenMajors; k++ ) {
                    DoubleGeneralPath pair = createTickPair( xVal + k * distBetweenMinor, height, minorTickHeight );
                    PPath minorTick = new PPath( pair.getGeneralPath(), new BasicStroke() );
                    minorTick.setStrokePaint( Color.black );
                    addChild( minorTick );
                }
            }

            if( i == 0 ) {
                PText unitsGraphic = new PText( units );
                addChild( unitsGraphic );
                unitsGraphic.setOffset( pText.getOffset().getX() + pText.getFullBounds().getWidth() + 5, pText.getOffset().getY() );
            }
        }
    }

    private DoubleGeneralPath createTickPair( double xVal, double height, double tickSize ) {
        DoubleGeneralPath tickPath = new DoubleGeneralPath( xVal, 0 );
        tickPath.lineTo( xVal, tickSize );
        tickPath.moveTo( xVal, height - tickSize );
        tickPath.lineTo( xVal, height );
        return tickPath;
    }

    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        JFrame frame = new JFrame();
        frame.setContentPane( pCanvas );
        frame.setSize( 800, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        pCanvas.getLayer().addChild( new RulerGraphic( new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}, "nm", 650, 40 ) );
        frame.show();
    }
}
