/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 11:30:56 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class TutorialChartFrame extends PaintImmediateDialog {
    public TutorialChartFrame( String title, JFreeChart chart, JFrame owner ) {
        super( owner, title );
        setContentPane( new ChartPanel( chart ) );
        setSize( 400, 300 );
    }

    public void finishPaint( Graphics g ) {
    }
}
