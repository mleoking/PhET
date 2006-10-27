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

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.view.MoleculePaints;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeBC;
import edu.colorado.phet.molecularreactions.model.MoleculeC;
import edu.colorado.phet.molecularreactions.model.MoleculeAB;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * MoleculePopulationsPieChartNode
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsPieChartNode extends PNode {

    public MoleculePopulationsPieChartNode( ComplexModule module, Rectangle2D bounds ) {

        setBounds( bounds );

        MoleculePopulationsPieChart pieChart = new MoleculePopulationsPieChart( module, bounds, 1 );
        addChild( pieChart );

        // The pie chart gets put in the middle of the pane. Otherwise, seams show in the striped
        // paints. This means I have to reposition the pie chart here. It's ugly, but I don't feel
        // like taking the time right now to figure out the thing with the striped paints.
        pieChart.setOffset( -bounds.getWidth() / 6, 0 );

        // Legend
        double paintSwatchWidth = 15;
        double paintSwatchHeight = 10;
        Rectangle2D rect = new Rectangle2D.Double( 0, -paintSwatchHeight / 2, paintSwatchWidth, paintSwatchHeight );
        PPath mAPaintNode = new PPath( rect );
        PPath mBCPaintNode = new PPath( rect );
        PPath mABPaintNode = new PPath( rect );
        PPath mCPaintNode = new PPath( rect );
        mAPaintNode.setPaint( MoleculePaints.getPaint( MoleculeA.class ) );
        mBCPaintNode.setPaint( MoleculePaints.getPaint( MoleculeBC.class ) );
        mABPaintNode.setPaint( MoleculePaints.getPaint( MoleculeAB.class ) );
        mCPaintNode.setPaint( MoleculePaints.getPaint( MoleculeC.class ) );

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
        this.addChild( new PPath( bounds ));

        Insets paintSwatchInsets = new Insets( 5, 0, 5, 80 );
        Insets textInsets = new Insets( 5, 0, 5, 50 );
        double yPaintSwatchOffset = 50;
        double yTextAdjustment = -5;
        mAPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                               yPaintSwatchOffset );
        mATextNode.setOffset( bounds.getWidth() - textInsets.right,
                              yPaintSwatchOffset + yTextAdjustment );
        yPaintSwatchOffset += + paintSwatchInsets.top + paintSwatchInsets.bottom + 10;
        mBCPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                                yPaintSwatchOffset );
        mBCTextNode.setOffset( bounds.getWidth() - textInsets.right,
                               yPaintSwatchOffset + yTextAdjustment );
        yPaintSwatchOffset += + paintSwatchInsets.top + paintSwatchInsets.bottom + 10;
        mABPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                                yPaintSwatchOffset );
        mABTextNode.setOffset( bounds.getWidth() - textInsets.right,
                               yPaintSwatchOffset + yTextAdjustment );
        yPaintSwatchOffset += + paintSwatchInsets.top + paintSwatchInsets.bottom + 10;
        mCPaintNode.setOffset( bounds.getWidth() - paintSwatchInsets.right,
                               yPaintSwatchOffset );
        mCTextNode.setOffset( bounds.getWidth() - textInsets.right,
                              yPaintSwatchOffset + yTextAdjustment );

    }
}
