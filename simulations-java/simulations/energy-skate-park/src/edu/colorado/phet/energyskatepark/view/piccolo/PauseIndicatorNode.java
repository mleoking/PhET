// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkUtils;
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
 */

public class PauseIndicatorNode extends PNode {

    private EnergySkateParkModule module;
    private int insetX = 5;
    private int insetY = 5;

    public PauseIndicatorNode( final EnergySkateParkModule module ) {
        this.module = module;
        PImage im = PImageFactory.create( "energy-skate-park/images/icons/java/media/Pause24.gif" );
        addChild( im );
        ShadowPText text = new ShadowPText( EnergySkateParkStrings.getString( "message.paused" ) );
        addChild( text );
        text.setOffset( im.getFullBounds().getMaxX() + 5, 0 );
        text.setFont( new PhetFont( Font.BOLD, 16 ) );
        text.setTextPaint( Color.red );
        text.setShadowColor( Color.black );
        module.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {

            public void pauseChanged() {
                updateVisibility();
            }
        } );

        JButton resume = new JButton( EnergySkateParkStrings.getString( "time.resume" ) );
        EnergySkateParkUtils.fixButtonOpacity( resume );
        resume.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordOrLiveMode();
                module.getClock().start();
            }
        } );
        PSwing pSwing = new PSwing( resume );
        pSwing.setOffset( text.getFullBounds().getMaxX() + 5, text.getFullBounds().getY() );
        addChild( pSwing );

        updateVisibility();

    }

    private void updateVisibility() {
        if( module.getTimeSeriesModel().isPaused() ) {
            showMe();
        }
        else {
            hideMe();
        }
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
