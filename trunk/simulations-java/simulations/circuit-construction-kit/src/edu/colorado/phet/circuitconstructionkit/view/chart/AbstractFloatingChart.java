/*  */
package edu.colorado.phet.circuitconstructionkit.view.chart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import org.jfree.data.Range;

import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Dec 18, 2005
 * Time: 11:28:29 PM
 */

public abstract class AbstractFloatingChart extends PhetPNode {
    private IClock clock;
    private TextReadout textReadout;
    private StripChartJFCNode stripChartJFCNode;
    private PSwing closeButtonPSwing;
    private JButton closeButton;
    private ClockAdapter clockListener;
    private ChartZoomControl chartZoomControl;

    public AbstractFloatingChart( PSwingCanvas pSwingCanvas, String title, IClock clock ) {
        this.clock = clock;
        textReadout = new TextReadout();
        stripChartJFCNode = new StripChartJFCNode( 200, 150, CCKStrings.getString( "time.sec" ), title );
        stripChartJFCNode.setDomainRange( 0, 5 );

        addChild( textReadout );
        addChild( stripChartJFCNode );
        chartZoomControl = new ChartZoomControl( this );
        addChild( chartZoomControl );

        CursorHandler cursorHandler = new CursorHandler( Cursor.HAND_CURSOR );
        addInputEventListener( cursorHandler );

        textReadout.setOffset( 0, 0 );
        textReadout.setVisible( false );

        update();
        clockListener = new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                update();
            }
        };
        clock.addClockListener( clockListener );
        try {
            closeButton = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/x-20.png" ) ) );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    close();
                }
            } );
            closeButtonPSwing = new PSwing( closeButton );
            PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    updateButtonLocations();
                }
            };
            updateButtonLocations();
            stripChartJFCNode.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, listener );
            stripChartJFCNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
            addChild( closeButtonPSwing );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public IClock getClock() {
        return clock;
    }

    public void zoomOut() {
        zoom( 1.2 );
    }

    private void zoom( double v ) {
        Range range = stripChartJFCNode.getVerticalRange();
        stripChartJFCNode.setVerticalRange( range.getLowerBound() * v, range.getUpperBound() * v );
    }

    public void zoomIn() {
        zoom( 0.8 );
    }

    public static interface ValueReader {
        double getValue( double x, double y );
    }

    private void close() {
        clock.removeClockListener( clockListener );
        notifyCloseButtonPressed();
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void chartClosing();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyCloseButtonPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.chartClosing();
        }
    }

    private void updateButtonLocations() {
//        closeButtonPSwing.setOffset( stripChartJFCNode.getFullBounds().getOrigin().getX(), stripChartJFCNode.getFullBounds().getOrigin().getY() - closeButtonPSwing.getFullBounds().getHeight() );
        closeButtonPSwing.setOffset( stripChartJFCNode.getFullBounds().getMaxX()-closeButtonPSwing.getFullBounds().getWidth(), 
                                     stripChartJFCNode.getFullBounds().getOrigin().getY() - closeButtonPSwing.getFullBounds().getHeight() );
        chartZoomControl.setOffset( stripChartJFCNode.getFullBounds().getOrigin().getX() , stripChartJFCNode.getFullBounds().getOrigin().getY() - chartZoomControl.getFullBounds().getHeight() );
    }

    public StripChartJFCNode getStripChartJFCNode() {
        return stripChartJFCNode;
    }

    public void update() {
        updateTextBackground();
    }

    protected void updateTextBackground() {
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
            readout = new PText( ( "value" ) );
            readout.setFont( new PhetFont( Font.BOLD, 14 ) );
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


}
