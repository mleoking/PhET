/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.DefaultDecimalFormat;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
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

public class IntensityReader extends PhetPNode {
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private IClock clock;
    private CrosshairGraphic crosshairGraphic;
    private TextReadout textReadout;
    private StripChartJFCNode stripChartJFCNode;
    private boolean detached = true;
    private Vector2D originalDisplacement;
    private boolean constrainedToMidline = false;
    private boolean allowAttachment = false;

    public IntensityReader( String title, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        this.waveModel = waveModel;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.clock = clock;
        textReadout = new TextReadout();
        crosshairGraphic = new CrosshairGraphic( this, 10, 15 );
        stripChartJFCNode = new StripChartJFCNode( 175, 120, WIStrings.getString( "time.s" ), title );
        CrosshairConnection crosshairConnection = new CrosshairConnection( this );
        addChild( textReadout );
        addChild( crosshairConnection );
        addChild( stripChartJFCNode );
        addChild( crosshairGraphic );

        stripChartJFCNode.addInputEventListener( new PairDragHandler() );
        CursorHandler cursorHandler = new CursorHandler( Cursor.HAND_CURSOR );
        addInputEventListener( cursorHandler );

        stripChartJFCNode.setOffset( -stripChartJFCNode.getFullBounds().getWidth() - crosshairGraphic.getFullBounds().getWidth() / 2.0,
                                     -stripChartJFCNode.getFullBounds().getHeight() / 2.0 );
        textReadout.setOffset( 0, crosshairGraphic.getFullBounds().getHeight() );
        textReadout.setVisible( false );

        double crosshairOffsetDX = crosshairGraphic.getFullBounds().getWidth() * 1.25;
        crosshairGraphic.translate( crosshairOffsetDX, 0 );
        originalDisplacement = getDisplacement();
//        System.out.println( "originalDisplacement = " + originalDisplacement );
        update();
    }

    private Vector2D getDisplacement() {
        return new Vector2D.Double( stripChartJFCNode.getFullBounds().getCenter2D(), crosshairGraphic.getFullBounds().getCenter2D() );
    }

    public void setConstrainedToMidline( boolean constrainedToMidline ) {
        this.constrainedToMidline = constrainedToMidline;
        update();
    }

    class PairDragHandler extends PDragEventHandler {
        protected void drag( PInputEvent event ) {
            super.drag( event );
            if( !detached ) {
                crosshairGraphic.translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        }
    }

    public StripChartJFCNode getStripChartJFCNode() {
        return stripChartJFCNode;
    }

    public CrosshairGraphic getCrosshairGraphic() {
        return crosshairGraphic;
    }

    public void update() {
        if( constrainedToMidline ) {
            Point2D pt = new Point2D.Double( crosshairGraphic.getGlobalTranslation().getX(), latticeScreenCoordinates.getScreenRect().getY() + latticeScreenCoordinates.getScreenRect().getHeight() / 2 );
            detachCrosshair();
            crosshairGraphic.setGlobalTranslation( pt );
        }
        //get the coordinate in the wavefunctiongraphic.
        Point2D location = crosshairGraphic.getGlobalTranslation();
        location.setLocation( location.getX() + 1, location.getY() + 1 );//todo this line seems necessary because we are off somewhere by 1 pixel
        Point cellLocation = latticeScreenCoordinates.toLatticeCoordinates( location.getX(), location.getY() );
        if( waveModel.containsLocation( cellLocation.x, cellLocation.y ) ) {
            double value = waveModel.getAverageValue( cellLocation.x, cellLocation.y, 1 );
            textReadout.setText( WIStrings.getString( "magnitude.0" ) + new DefaultDecimalFormat( "0.00" ).format( value ) );
            stripChartJFCNode.addValue( clock.getSimulationTime(), value );
        }
        else {
            textReadout.setText( "" );
            stripChartJFCNode.addValue( clock.getSimulationTime(), 0.0 );
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
            readout = new PText( WIStrings.getString( "value" ) );
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
        private CrosshairDragHandler listener;
        private IntensityReader intensityReader;

        public CrosshairGraphic( IntensityReader intensityReader, int innerRadius, int outerRadius ) {
            this.intensityReader = intensityReader;
            Ellipse2D.Double aShape = new Ellipse2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2 );
            PPath circle = new PPath( aShape );
            circle.setStrokePaint( Color.red );
            circle.setStroke( new BasicStroke( 2 ) );
//            setPaint( new Color( 0, 0, 0, 0 ) );//to make it grabbable inside

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
            //to ensure the entire object is grabbable
            PPath overlay = new PPath( new Rectangle2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 ) );
            overlay.setPaint( new Color( 255, 255, 255, 0 ) );
            overlay.setStrokePaint( new Color( 255, 255, 255, 0 ) );
            addChild( overlay );
            listener = new CrosshairDragHandler();
            addInputEventListener( listener );
        }

        class CrosshairDragHandler extends PDragEventHandler {
            protected void drag( PInputEvent event ) {
                super.drag( event );
                intensityReader.detachCrosshair();
            }

            protected void superdrag( PInputEvent event ) {
                super.drag( event );
            }

            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                intensityReader.crosshairDropped( event );
            }
        }

        public void drag( PInputEvent event ) {
            listener.superdrag( event );
        }
    }

    private void crosshairDropped( PInputEvent event ) {
        double threshold = 30;
        if( MathUtil.isApproxEqual( getDisplacement().getX(), originalDisplacement.getX(), threshold )
            && MathUtil.isApproxEqual( getDisplacement().getY(), originalDisplacement.getY(), threshold ) ) {
            attachCrosshair();
        }
    }

    private void attachCrosshair() {
        if( allowAttachment ) {
            detached = false;
            crosshairGraphic.setOffset( stripChartJFCNode.getFullBounds().getCenterX() + originalDisplacement.getX() - crosshairGraphic.getFullBounds().getWidth() / 2,
                                        stripChartJFCNode.getFullBounds().getCenterY() + originalDisplacement.getY() );
        }
    }

    private void detachCrosshair() {
        detached = true;
    }
}
