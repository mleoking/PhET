// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.charts;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class TemporalChartSliderNode extends MotionSliderNode.Vertical {
    private final TemporalChart temporalChart;

    public TemporalChartSliderNode( final TemporalChart temporalChart, Color highlightColor ) {
        this( temporalChart, new PImage( ColorArrows.createArrow( highlightColor ) ), highlightColor );
    }

    public TemporalChartSliderNode( final TemporalChart temporalChart, final PNode sliderThumb, Color highlightColor ) {
        super( new Range( temporalChart.getMinRangeValue(), temporalChart.getMaxRangeValue() ), 0.0, new Range( 0, 100 ), sliderThumb, highlightColor );
        this.temporalChart = temporalChart;

        final SimpleObserver updateLayoutBasedOnChart = new SimpleObserver() {
            public void update() {
                setViewRange( 0, temporalChart.getViewDimension().getHeight() );
            }
        };
        temporalChart.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLayoutBasedOnChart.update();
            }
        } );
        updateLayoutBasedOnChart.update();

        temporalChart.getDataModelBounds().addObserver( new SimpleObserver() {
            public void update() {
                setModelRange( temporalChart.getMinRangeValue(), temporalChart.getMaxRangeValue() );
            }
        } );
    }
}