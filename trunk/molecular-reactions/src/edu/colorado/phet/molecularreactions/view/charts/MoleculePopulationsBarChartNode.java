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

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsBarChart;
import edu.colorado.phet.molecularreactions.view.icons.MoleculeIcon;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * MoleculePopulationsBarChartNode
 * <p>
 * A PNode that has a MoleculePopulationsBarChart and icons beneath
 * it for each type of molecule in the chart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsBarChartNode extends PNode {

    public MoleculePopulationsBarChartNode( ComplexModule module, Dimension size, PhetPCanvas phetPCanvas ) {
        final MoleculePopulationsBarChart barChart = new MoleculePopulationsBarChart( module.getMRModel(), module.getClock(), 0, MRConfig.BAR_CHART_MAX_Y, 1 );
        ChartPanel barChartPanel = new ChartPanel( barChart.getChart() );
        Insets barChartInsets = new Insets( 0, 10, 0,0);
        barChartPanel.setPreferredSize( new Dimension( (int)size.getWidth() - barChartInsets.left + barChartInsets.right,
                                                       (int)( size.getHeight() - 40 ) ) );
        PSwing barChartPSwing = new PSwing( phetPCanvas, barChartPanel );
        barChartPSwing.setOffset( barChartInsets.left,0 );

        this.addChild( barChartPSwing );
        EnergyProfile profile = module.getMRModel().getEnergyProfile();
        PNode mANode = new PImage( new MoleculeIcon( MoleculeA.class, profile ).getImage() );
        PNode mBCNode = new PImage( new MoleculeIcon( MoleculeBC.class, profile ).getImage() );
        PNode mABNode = new PImage( new MoleculeIcon( MoleculeAB.class, profile ).getImage() );
        PNode mCNode = new PImage( new MoleculeIcon( MoleculeC.class, profile ).getImage() );
        this.addChild( mANode );
        this.addChild( mBCNode );
        this.addChild( mABNode );
        this.addChild( mCNode );
        this.addChild( new PPath( new Rectangle(0,0, (int)size.getWidth(), (int)size.getHeight() )));

        double y = barChartPSwing.getFullBounds().getHeight() + 18;
        double xIncr = 58;
        double x = 84 + barChartInsets.left;
        mANode.setOffset( x - mANode.getFullBounds().getWidth() / 2,
                          y - mANode.getFullBounds().getHeight() / 2 );
        x += xIncr;
        mBCNode.setOffset( x - mBCNode.getFullBounds().getWidth() / 2,
                           y - mBCNode.getFullBounds().getHeight() / 2 );
        x += xIncr;
        mABNode.setOffset( x - mABNode.getFullBounds().getWidth() / 2,
                           y - mABNode.getFullBounds().getHeight() / 2 );
        x += xIncr;
        mCNode.setOffset( x - mCNode.getFullBounds().getWidth() / 2,
                          y - mCNode.getFullBounds().getHeight() / 2 );

        // Add a rescale button
        JButton rescaleBtn = new JButton( SimStrings.get( "StripChart.rescale"));
        rescaleBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                barChart.rescale();
            }
        } );
        PSwing rescaleNode = new PSwing( phetPCanvas, rescaleBtn );
        rescaleNode.setOffset( 5,
                               getFullBounds().getHeight() - rescaleNode.getFullBounds().getHeight() - 10);
        addChild( rescaleNode );

    }
}
