package edu.colorado.phet.common.motion.graphs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.ui.TimeseriesResources;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 8:55:43 AM
 */

public class GraphTimeControlNode extends PNode {
    private PSwing goStopButton;
    private PSwing clearButton;
    private PNode seriesLayer = new PNode();
    private boolean editable = true;
    private boolean constructed = false;

    public GraphTimeControlNode( TimeSeriesModel timeSeriesModel ) {
        addChild( seriesLayer );

        goStopButton = new PSwing( new GoStopButton( timeSeriesModel ) );
        addChild( goStopButton );

        clearButton = new PSwing( new ClearButton( timeSeriesModel ) );
        addChild( clearButton );

        constructed = true;
        relayout();
    }

    public GraphControlSeriesNode addVariable( ControlGraphSeries series ) {
        GraphControlSeriesNode seriesNode = createGraphControlSeriesNode( series );
        seriesNode.setEditable( editable );
        seriesNode.setOffset( 0, seriesLayer.getFullBounds().getHeight() + 5 );
        seriesLayer.addChild( seriesNode );
        relayout();
        return seriesNode;
    }

    protected GraphControlSeriesNode createGraphControlSeriesNode( ControlGraphSeries series ) {
        return new GraphControlSeriesNode( series );
    }

    private void relayout() {
        if ( constructed ) {
            double dy = 5;
            seriesLayer.setOffset( 0, 0 );
            for ( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
                GraphControlSeriesNode child = (GraphControlSeriesNode) seriesLayer.getChild( i );
                child.relayout( dy );
            }
            goStopButton.setOffset( 0, seriesLayer.getFullBounds().getMaxY() + dy );
            clearButton.setOffset( 0, goStopButton.getFullBounds().getMaxY() + dy );
        }
    }

    public void setEditable( boolean editable ) {
        this.editable = editable;
        for ( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
            GraphControlSeriesNode child = (GraphControlSeriesNode) seriesLayer.getChild( i );
            child.setEditable( editable );
        }
        setHasChild( goStopButton, editable );
        setHasChild( clearButton, editable );
    }

    private void setHasChild( PNode child, boolean addChild ) {
        if ( addChild && !getChildrenReference().contains( child ) ) {
            addChild( child );
        }
        else if ( !addChild && getChildrenReference().contains( child ) ) {
            removeChild( child );
        }
    }

    public static class ClearButton extends JButton {
        private TimeSeriesModel graphTimeSeries;

        public ClearButton( final TimeSeriesModel graphTimeSeries ) {
            super(PhetCommonResources.getString( "Common.clear" ));
            this.graphTimeSeries = graphTimeSeries;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    graphTimeSeries.clear();
                }
            } );
            graphTimeSeries.addListener( new TimeSeriesModel.Adapter() {
                public void dataSeriesChanged() {
                    updateEnabledState();
                }
            } );

            updateEnabledState();
        }

        private void updateEnabledState() {
            setEnabled( graphTimeSeries.isThereRecordedData() );
        }
    }

    public static class GoStopButton extends JButton {
        private boolean goButton = true;
        private TimeSeriesModel timeSeriesModel;

        public GoStopButton( final TimeSeriesModel timeSeriesModel ) {
            super( PhetCommonResources.getString( "chart-time-control.go" ));
            this.timeSeriesModel = timeSeriesModel;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( isGoButton() ) {
                        timeSeriesModel.startRecording();
                    }
                    else {
                        timeSeriesModel.setPaused( true );
                    }
                }
            } );
            timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {

                public void modeChanged() {
                    updateGoState();
                }

                public void pauseChanged() {
                    updateGoState();
                }
            } );
            updateGoState();
        }

        private void updateGoState() {
            setGoButton( !timeSeriesModel.isRecording() );
        }

        private void setGoButton( boolean go ) {
            this.goButton = go;
            setText( goButton ? PhetCommonResources.getString( "chart-time-control.go" ) : PhetCommonResources.getString( "Common.StopwatchPanel.stop" ) );
            setIcon( new ImageIcon( TimeseriesResources.loadBufferedImage( goButton ? "icons/go.png" : "icons/stop.png" ) ) );
        }

        private boolean isGoButton() {
            return goButton;
        }
    }

}
