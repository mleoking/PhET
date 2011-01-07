// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.tests;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.MutableColor;
import edu.colorado.phet.waveinterference.view.WaveChartGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Apr 14, 2006
 * Time: 4:19:58 PM
 */

public class ExpandableWaveChart extends PNode {
    private WaveChartGraphic waveChartGraphic;
    private PSwing expandPSwing;
    private boolean expanded = false;
    private PNode collapsePSwing;
    private ArrayList listeners = new ArrayList();
    private LatticeScreenCoordinates latticeScreenCoordinates;

    public ExpandableWaveChart( String title, LatticeScreenCoordinates latticeScreenCoordinates, WaveModel waveModel, MutableColor color, String distanceUnits, double minX, double maxX ) {
        this( new WaveChartGraphic( title, latticeScreenCoordinates, waveModel, color, distanceUnits, minX, maxX ), latticeScreenCoordinates );
    }

    public ExpandableWaveChart( WaveChartGraphic waveChartGraphic, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        JButton expand = new JButton( WIStrings.getString( "chart.show-graph" ) );
        expand.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setExpanded( true );
            }
        } );
        JButton collapse = null;
        try {
            collapse = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "wave-interference/images/x-20.png" ) ) );
            collapse.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setExpanded( false );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addChild( waveChartGraphic );
        expandPSwing = new PSwing( expand );
        collapsePSwing = new PSwing( collapse );
//        addChild( new PSwing( pSwingCanvas, collapse ) );
        addChild( waveChartGraphic );
        this.waveChartGraphic = waveChartGraphic;
        waveChartGraphic.addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocations();
            }
        } );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocations();
            }
        } );
        updateLocations();
        addChild( expandPSwing );
        update();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded( boolean expanded ) {
        this.expanded = expanded;
        update();
        notifyExpansionStateChanged();
    }

    private void update() {
        removeAllChildren();
        if ( expanded ) {
            addChild( waveChartGraphic );
            addChild( collapsePSwing );
            updateChart();
        }
        else {
            addChild( expandPSwing );
        }
        updateLocations();
    }

    private void updateLocations() {
//        double expandX = waveChartGraphic.getChartBounds().getMaxX() - expandPSwing.getFullBounds().getWidth();
//        double expandX = waveChartGraphic.getChartBounds().getCenterX() - expandPSwing.getFullBounds().getWidth();
        double expandX = waveChartGraphic.getChartBounds().getCenterX() - expandPSwing.getFullBounds().getWidth() / 2.0;
        double collapseX = waveChartGraphic.getChartBounds().getMaxX() - collapsePSwing.getFullBounds().getWidth();
        double buttonY = waveChartGraphic.getChartBounds().getY();
        expandPSwing.setOffset( expandX, buttonY );
        collapsePSwing.setOffset( collapseX, buttonY );
    }

    public void updateChart() {
        if ( waveChartGraphic.getVisible() ) {
            waveChartGraphic.updateChart();
        }
    }

    public void setColor( Color rootColor ) {
        waveChartGraphic.setCurveColor( rootColor );
    }

    public void reset() {
        setExpanded( false );
    }

    public static interface Listener {
        void expansionStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyExpansionStateChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).expansionStateChanged();
        }
    }
}
