// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import java.awt.*;

import javax.swing.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;

public class TutorialChartFrame extends PaintImmediateDialog {
    public TutorialChartFrame( String title, JFreeChart chart, JFrame owner ) {
        super( owner, title );
        setContentPane( new ChartPanel( chart ) );
        setSize( 400, 300 );
    }

    public void finishPaint( Graphics g ) {
    }
}
