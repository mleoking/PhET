/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.DefaultDecimalFormat;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

import java.awt.*;
import java.awt.geom.*;

/**
 * User: Sam Reid
 * Date: Dec 18, 2005
 * Time: 11:28:29 PM
 * Copyright (c) Dec 18, 2005 by Sam Reid
 */

public class IntensityReader extends PComposite {
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private IClock clock;
    private CrosshairGraphic crosshairs;
    private TextReadout textReadout;
    private StripChartJFCNode stripChartJFCNode;

    public IntensityReader( String title, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        this.waveModel = waveModel;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.clock = clock;
        textReadout = new TextReadout();
        addChild( textReadout );

//        crosshairs = new CrosshairGraphic( 20, 25 );
        crosshairs = new CrosshairGraphic( 10, 15 );
        addChild( crosshairs );

//        stripChartJFCNode = new StripChartJFCNode( 225, 150, "Time (s)", "Amplitude" );
        stripChartJFCNode = new StripChartJFCNode( 175, 120, "Time (s)", title );
        addChild( stripChartJFCNode );

        addInputEventListener( new PDragEventHandler() );
        CursorHandler cursorHandler = new CursorHandler( Cursor.HAND_CURSOR );
        addInputEventListener( cursorHandler );

        stripChartJFCNode.setOffset( -stripChartJFCNode.getFullBounds().getWidth() - crosshairs.getFullBounds().getWidth() / 2.0,
                                     -stripChartJFCNode.getFullBounds().getHeight() / 2.0 );
        textReadout.setOffset( 0, crosshairs.getFullBounds().getHeight() );
        textReadout.setVisible( false );

        update();
    }

    public void update() {
        //get the coordinate in the wavefunctiongraphic.
        Point2D location = crosshairs.getGlobalTranslation();
        location.setLocation( location.getX() + 1, location.getY() + 1 );//todo this line seems necessary because we are off somewhere by 1 pixel
        Point cellLocation = latticeScreenCoordinates.toLatticeCoordinates( location.getX(), location.getY() );
        if( waveModel.containsLocation( cellLocation.x, cellLocation.y ) ) {
            double value = waveModel.getAverageValue( cellLocation.x, cellLocation.y, 1 );
            textReadout.setText( "Magnitude=" + new DefaultDecimalFormat( "0.00" ).format( value ) );
            stripChartJFCNode.addValue( clock.getSimulationTime(), value );
        }
        else {
            textReadout.setText( "" );
        }
        updateTextBackground();
    }

    private void updateTextBackground() {
        textReadout.updateBackground();
    }

    public void setReadoutVisible( boolean selected ) {
        textReadout.setVisible( selected );
    }

    public boolean isReadoutVisible() {
        return textReadout.getVisible();
    }

    static class TextReadout extends PhetPNode {
        private PText readout;
        private PPath textBackground;

        public TextReadout() {
            textBackground = new PPath();
            textBackground.setPaint( new Color( 255, 255, 255, 235 ) );
            addChild( textBackground );
            readout = new PText( "Value=???" );
            readout.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
            readout.setTextPaint( Color.blue );
            addChild( readout );
        }

        public void setText( String s ) {
            readout.setText( s );
        }

        public void updateBackground() {
            textBackground.setPathTo( RectangleUtils.expand( readout.getFullBounds(), 10, 10 ) );
        }
    }

    static class CrosshairGraphic extends PComposite {
        private static final Paint CROSSHAIR_COLOR = Color.white;
        private BasicStroke CROSSHAIR_STROKE = new BasicStroke( 2 );

        public CrosshairGraphic( int innerRadius, int outerRadius ) {
            Ellipse2D.Double aShape = new Ellipse2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2 );
            PPath circle = new PPath( aShape );
            circle.setStrokePaint( Color.red );
            circle.setStroke( new BasicStroke( 2 ) );
            setPaint( new Color( 0, 0, 0, 0 ) );//to make it grabbable inside

            PPath vertical = new PPath( new Line2D.Double( 0, -outerRadius, 0, outerRadius ) );
            vertical.setStroke( CROSSHAIR_STROKE );
            vertical.setStrokePaint( CROSSHAIR_COLOR );

            PPath horizontalLine = new PPath( new Line2D.Double( -outerRadius, 0, outerRadius, 0 ) );
            horizontalLine.setStroke( CROSSHAIR_STROKE );
            horizontalLine.setStrokePaint( CROSSHAIR_COLOR );

            Area backgroundShape = new Area();
            backgroundShape.add( new Area( new Rectangle2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 ) ) );
            backgroundShape.subtract( new Area( aShape ) );
            PPath background = new PPath( backgroundShape );
            background.setPaint( Color.lightGray );
            background.setStrokePaint( Color.gray );

            addChild( background );
            addChild( circle );
            addChild( vertical );
            addChild( horizontalLine );
        }
    }
}
