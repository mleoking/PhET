// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

public class TestControlGraphAPI {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas contentPane = new PhetPCanvas();

        final ITemporalVariable variable = new DefaultTemporalVariable();
        RecordableModel recordableModel = new TestRecordableModel();
        ConstantDtClock clock = new ConstantDtClock( 1, 1.0 );
        TimeSeriesModel timeSeriesModel = new TimeSeriesModel( recordableModel, clock );
        ControlGraph controlGraph = new ControlGraph( contentPane, variable, "title", 0, 10, timeSeriesModel );
        contentPane.addScreenChild( controlGraph );

        controlGraph.setBounds( 0, 0, 600, 400 );

        frame.setContentPane( contentPane );

        frame.setSize( 800, 600 );
        frame.setVisible( true );

        final double freq = 1 / 10.0;
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                variable.setValue( Math.sin( System.currentTimeMillis() / 1000.0 * freq ) );
            }
        } );
        timer.start();
    }

    private static class TestRecordableModel implements RecordableModel {

        public void stepInTime( double simulationTimeChange ) {
        }

        public Object getState() {
            return null;
        }

        public void setState( Object o ) {
        }

        public void resetTime() {
        }

        public void clear() {
        }
    }
}
