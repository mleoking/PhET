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

import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.modules.ComplexModule;
import edu.colorado.phet.reactionsandrates.view.MoleculePaints;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * MoleculePopulationsPieChartNode
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsPieChartNode extends PNode {
    private PPath mAPaintNode;
    private PPath mBCPaintNode;
    private PPath mABPaintNode;
    private PPath mCPaintNode;

    public MoleculePopulationsPieChartNode( ComplexModule module, Rectangle2D bounds ) {

        setBounds( bounds );

        // Legend
        createLegend( module.getMRModel(), bounds );

//        Rectangle2D chartBounds = new Rectangle2D.Double( bounds.getX(),
//                                                          bounds.getY(),
//                                                          bounds.getWidth() - 80,
//                                                          bounds.getHeight() );
//        MoleculePopulationsPieChart pieChart = new MoleculePopulationsPieChart( module, chartBounds, 1 );
        MoleculePopulationsPieChart pieChart = new MoleculePopulationsPieChart( module, bounds, 1 );
        addChild( pieChart );

        // The pie chart gets put in the middle of the pane. Otherwise, seams show in the striped
        // paints. This means I have to reposition the pie chart here. It's ugly, but I don't feel
        // like taking the time right now to figure out the thing with the striped paints.
        pieChart.setOffset( -bounds.getWidth() / 6, 0 );

        // Title
        PText title = new PText( MRConfig.RESOURCES.getLocalizedString( "StripChart.title" ) );
        title.setFont( MRConfig.CHART_TITLE_FONT );
        addChild( title );
        double x = bounds.getWidth() - title.getFullBounds().getWidth() - 20;
        title.setOffset( x, title.getFullBounds().getHeight() / 2 );

    }

    private void createLegend( MRModel model, Rectangle2D bounds ) {
        double paintSwatchWidth = 15;
        double paintSwatchHeight = 10;
        Rectangle2D rect = new Rectangle2D.Double( 0, -paintSwatchHeight / 2, paintSwatchWidth, paintSwatchHeight );
        mAPaintNode = new PPath( rect );
        mBCPaintNode = new PPath( rect );
        mABPaintNode = new PPath( rect );
        mCPaintNode = new PPath( rect );

        EnergyProfile profile = model.getEnergyProfile();

        setLegendMoleculePaints( profile );

        model.addListener( new MRModel.ModelListenerAdapter() {
            public void notifyEnergyProfileChanged( EnergyProfile profile ) {
                setLegendMoleculePaints( profile );
            }
        } );

        PText mATextNode = new PText( "A" );
        PText mBCTextNode = new PText( "BC" );
        PText mABTextNode = new PText( "AB" );
        PText mCTextNode = new PText( "C" );

        addChild( mAPaintNode );
        addChild( mBCPaintNode );
        addChild( mABPaintNode );
        addChild( mCPaintNode );

        addChild( mATextNode );
        addChild( mBCTextNode );
        addChild( mABTextNode );
        addChild( mCTextNode );
        this.addChild( new PPath( bounds ) );

        Insets paintSwatchInsets = new Insets( 5, 0, 5, 80 );
        Insets textInsets = new Insets( 5, 0, 5, 50 );
        double yPaintSwatchOffset = 50;
        double yTextAdjustment = -5;
        mAPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                               yPaintSwatchOffset );
        mATextNode.setOffset( bounds.getWidth() - textInsets.right,
                              yPaintSwatchOffset + yTextAdjustment );
        yPaintSwatchOffset += +paintSwatchInsets.top + paintSwatchInsets.bottom + 10;
        mBCPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                                yPaintSwatchOffset );
        mBCTextNode.setOffset( bounds.getWidth() - textInsets.right,
                               yPaintSwatchOffset + yTextAdjustment );
        yPaintSwatchOffset += +paintSwatchInsets.top + paintSwatchInsets.bottom + 10;
        mABPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                                yPaintSwatchOffset );
        mABTextNode.setOffset( bounds.getWidth() - textInsets.right,
                               yPaintSwatchOffset + yTextAdjustment );
        yPaintSwatchOffset += +paintSwatchInsets.top + paintSwatchInsets.bottom + 10;
        mCPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                               yPaintSwatchOffset );
        mCTextNode.setOffset( bounds.getWidth() - textInsets.right,
                              yPaintSwatchOffset + yTextAdjustment );
    }

    private void setLegendMoleculePaints( EnergyProfile profile ) {
        mAPaintNode.setPaint( MoleculePaints.getPaint( MoleculeA.class, profile ) );
        mBCPaintNode.setPaint( MoleculePaints.getPaint( MoleculeBC.class, profile ) );
        mABPaintNode.setPaint( MoleculePaints.getPaint( MoleculeAB.class, profile ) );
        mCPaintNode.setPaint( MoleculePaints.getPaint( MoleculeC.class, profile ) );
    }
}
