/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.chart;

import edu.colorado.phet.cck.common.CCKStrings;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 18, 2005
 * Time: 11:28:29 PM
 * Copyright (c) Dec 18, 2005 by Sam Reid
 */

public abstract class AbstractFloatingChart extends PhetPNode {
    private IClock clock;
    private TextReadout textReadout;
    private StripChartJFCNode stripChartJFCNode;
    private PSwing closeButtonPSwing;
    private JButton closeButton;
    private ClockAdapter clockListener;

    public AbstractFloatingChart( PSwingCanvas pSwingCanvas, String title, IClock clock ) {
        this.clock = clock;
        textReadout = new TextReadout();
        stripChartJFCNode = new StripChartJFCNode( 200, 150, CCKStrings.getString( "time.sec" ), title );
        stripChartJFCNode.setDomainRange( 0, 5 );

        addChild( textReadout );
        addChild( stripChartJFCNode );

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
            closeButton = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/x-20.png" ) ) );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    close();
                }
            } );
            closeButtonPSwing = new PSwing( pSwingCanvas, closeButton );
            PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    updateCloseButtonLocation();
                }
            };
            updateCloseButtonLocation();
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
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.chartClosing();
        }
    }

    private void updateCloseButtonLocation() {
        closeButtonPSwing.setOffset( stripChartJFCNode.getFullBounds().getOrigin().getX(), stripChartJFCNode.getFullBounds().getOrigin().getY() - closeButtonPSwing.getFullBounds().getHeight() );
    }

    public StripChartJFCNode getStripChartJFCNode() {
        return stripChartJFCNode;
    }

    public void update() {
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
            readout = new PText( ( "value" ) );
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


}
