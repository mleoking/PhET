/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * StripChartDialog
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChartDialog extends JDialog {

    public StripChartDialog( MRModule module ) throws HeadlessException {
        super( PhetUtilities.getPhetFrame(), false );
        final MoleculePopulationsStripChart stripChart = new MoleculePopulationsStripChart( module.getMRModel(),
                                                                                            module.getClock(),
                                                                                            500,
                                                                                            0,
                                                                                            20,
                                                                                            1 );
        ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setPreferredSize( new Dimension( 400, 200 ) );

        // Dialog
        JDialog stripChartDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
        stripChartDlg.setResizable( false );
        PhetPCanvas stripChartCanvas = new PhetPCanvas();
        PNode stripChartNode = new PSwing( stripChartCanvas, chartPanel );
        stripChartCanvas.addScreenChild( stripChartNode );
        stripChartCanvas.setPreferredSize( new Dimension( chartPanel.getPreferredSize() ) );

        // Add a rescale button
        JButton rescaleBtn = new JButton( SimStrings.get( "StripChart.rescale" ) );
        rescaleBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stripChart.rescale();
            }
        } );
        PSwing rescaleNode = new PSwing( stripChartCanvas, rescaleBtn );
        rescaleNode.setOffset( chartPanel.getPreferredSize().getWidth() - rescaleNode.getFullBounds().getWidth() - 10,
                               chartPanel.getPreferredSize().getHeight() - rescaleNode.getFullBounds().getHeight() - 10 );
        stripChartCanvas.addScreenChild( rescaleNode );

        stripChartDlg.getContentPane().add( stripChartCanvas );
        stripChartDlg.pack();
    }
}
