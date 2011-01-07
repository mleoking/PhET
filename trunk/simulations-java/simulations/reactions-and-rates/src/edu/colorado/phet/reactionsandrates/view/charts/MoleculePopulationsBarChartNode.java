// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view.charts;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.modules.ComplexModule;
import edu.colorado.phet.reactionsandrates.view.icons.MoleculeIcon;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartPanel;

import java.awt.*;

/**
 * MoleculePopulationsBarChartNode
 * <p/>
 * A PNode that has a MoleculePopulationsBarChart and icons beneath
 * it for each type of molecule in the chart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsBarChartNode extends AbstractRescaleableChartNode implements Rescaleable {
    private final MoleculePopulationsBarChart barChart;
    private final PImage mANode = new PImage();
    private final PImage mBCNode = new PImage();
    private final PImage mABNode = new PImage();
    private final PImage mCNode = new PImage();

    public MoleculePopulationsBarChartNode( ComplexModule module, Dimension size, PhetPCanvas phetPCanvas ) {
        PhetPCanvas barChartCanvas = new PhetPCanvas();

        barChart = new MoleculePopulationsBarChart( module.getMRModel(), module.getClock(), 0, MRConfig.BAR_CHART_MAX_Y, 1 );
        ChartPanel barChartPanel = new ChartPanel( barChart.getChart() );

        Insets barChartInsets = new Insets( 0, 10, 0, 0 );

        barChartPanel.setPreferredSize( new Dimension( (int)size.getWidth() - barChartInsets.left + barChartInsets.right,
                                                       (int)( size.getHeight() - 40 ) ) );
        PSwing barChartPSwing = new PSwing( barChartPanel );
        barChartPSwing.setOffset( barChartInsets.left, 0 );

        barChartCanvas.addScreenChild( barChartPSwing );

        updateLegendGraphics( module.getMRModel().getEnergyProfile() );

        module.getMRModel().addListener( new MRModel.ModelListenerAdapter() {
            public void notifyEnergyProfileChanged( EnergyProfile profile ) {
                updateLegendGraphics( profile );
            }
        } );

        barChartCanvas.addScreenChild( mANode );
        barChartCanvas.addScreenChild( mBCNode );
        barChartCanvas.addScreenChild( mABNode );
        barChartCanvas.addScreenChild( mCNode );
        barChartCanvas.addScreenChild( new PPath( new Rectangle( 0, 0, (int)size.getWidth(), (int)size.getHeight() ) ) );

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
        addZoomControl( size, barChartCanvas, barChart );

        barChartCanvas.setOpaque( true );

        this.addChild( barChartCanvas.getPhetRootNode() );

    }

    private void updateLegendGraphics( EnergyProfile profile ) {
        mANode.setImage( new MoleculeIcon( MoleculeA.class, profile ).getImage() );
        mBCNode.setImage( new MoleculeIcon( MoleculeBC.class, profile ).getImage() );
        mABNode.setImage( new MoleculeIcon( MoleculeAB.class, profile ).getImage() );
        mCNode.setImage( new MoleculeIcon( MoleculeC.class, profile ).getImage() );
    }

    public void rescale() {
        barChart.rescale();
    }
}
