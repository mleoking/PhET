/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.common.timeseries.TimeSeriesModelListenerAdapter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 7:23:02 PM
 *
 */

public class PauseIndicator extends PNode {

    private EnergySkateParkModule module;
    private EnergySkateParkSimulationPanel canvas;
    private EnergySkateParkRootNode ec3RootNode;
    private int insetX = 5;
    private int insetY = 5;

    public PauseIndicator( final EnergySkateParkModule module, EnergySkateParkSimulationPanel canvas, EnergySkateParkRootNode ec3RootNode ) {
        this.module = module;
        this.canvas = canvas;
        this.ec3RootNode = ec3RootNode;
        PImage im = PImageFactory.create( "energy-skate-park/images/icons/java/media/Pause24.gif" );
        addChild( im );
        ShadowPText text = new ShadowPText( EnergySkateParkStrings.getString( "the.simulation.is.paused" ) );
        addChild( text );
        text.setOffset( im.getFullBounds().getMaxX() + 5, 0 );
        text.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
        text.setTextPaint( Color.red );
        text.setShadowColor( Color.black );
        module.getTimeSeriesModel().addListener( new TimeSeriesModelListenerAdapter() {
            public void liveModeStarted() {
                hideMe();
            }

            public void recordingStarted() {
                hideMe();
            }

            public void recordingPaused() {
                showMe();
            }

            public void playbackStarted() {
                hideMe();
            }

            public void playbackPaused() {
                showMe();
            }

            public void liveModePaused() {
                showMe();
            }
        } );

        JButton record = new JButton( EnergySkateParkStrings.getString( "go" ) );
        record.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getTimeSeriesModel().startLiveMode();
            }
        } );
        PSwing pSwing = new PSwing( record );
        pSwing.setOffset( text.getFullBounds().getMaxX() + 5, text.getFullBounds().getY() );
        addChild( pSwing );

        if( module.getTimeSeriesModel().isPaused() ) {
            showMe();
        }
        else {
            hideMe();
        }

//        hideMe();
    }

    private void hideMe() {
        setVisible( false );
    }

    private void showMe() {
        setVisible( true );
    }

    public void relayout() {
        JComponent simulationPanel = module.getSimulationPanel();
        int x = insetX;
        int y = (int)( simulationPanel.getHeight() - getFullBounds().getHeight() - insetY );
        if( getOffset().getX() != x || getOffset().getY() != y ) {
            setOffset( x, y );
        }
    }

}
