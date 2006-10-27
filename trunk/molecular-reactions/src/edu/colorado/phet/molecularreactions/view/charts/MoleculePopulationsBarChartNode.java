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

import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeAB;
import edu.colorado.phet.molecularreactions.model.MoleculeBC;
import edu.colorado.phet.molecularreactions.model.MoleculeC;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.util.BarChart;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsBarChart;
import edu.colorado.phet.molecularreactions.view.MoleculeIcon;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartPanel;

import java.awt.*;

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
        BarChart barChart = new MoleculePopulationsBarChart( module.getMRModel(), module.getClock(), 0, MRConfig.BAR_CHART_MAX_Y, 1 );
        ChartPanel barChartPanel = new ChartPanel( barChart.getChart() );
        barChartPanel.setPreferredSize( new Dimension( (int)size.getWidth(),
                                                       (int)( size.getHeight() - 40 ) ) );
        PSwing barChartPSwing = new PSwing( phetPCanvas, barChartPanel );

        this.addChild( barChartPSwing );
        PNode mANode = new PImage( new MoleculeIcon( MoleculeA.class ).getImage() );
        PNode mBCNode = new PImage( new MoleculeIcon( MoleculeBC.class ).getImage() );
        PNode mABNode = new PImage( new MoleculeIcon( MoleculeAB.class ).getImage() );
        PNode mCNode = new PImage( new MoleculeIcon( MoleculeC.class ).getImage() );
        this.addChild( mANode );
        this.addChild( mBCNode );
        this.addChild( mABNode );
        this.addChild( mCNode );
        this.addChild( new PPath( new Rectangle(0,0, (int)size.getWidth(), (int)size.getHeight() )));

        double y = barChartPSwing.getFullBounds().getHeight() + 18;
        double xIncr = 58;
        double x = 84;
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
    }
}
