/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.pedigree.PedigreeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * The piccolo canvas where the pedigree charts are drawn in. Allows changing between the charts
 *
 * @author Jonathan Olson
 */
public class PedigreeChartCanvas extends PhetPCanvas {

    private NaturalSelectionModel model;
    public PedigreeNode pedigreeNode;

    private PNode rootNode;

    private static final double TOP_PADDING = 5.0;

    private static final double PEDIGREE_TOP_PADDING = 30.0;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public PedigreeChartCanvas( NaturalSelectionModel model ) {
        super.setWorldTransformStrategy( new ConstantTransformStrategy( new AffineTransform() ) );

        setPreferredSize( NaturalSelectionDefaults.GENERATION_CHART_SIZE );

        this.model = model;

        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_GENERATION_CHART ) );

        rootNode = new PNode();
        addWorldChild( rootNode );

        pedigreeNode = new PedigreeNode( model );

        rootNode.addChild( pedigreeNode );

        pedigreeNode.setOffset( new Point2D.Double( 0, PEDIGREE_TOP_PADDING ) );

        setCenterPoint( 0 );

    }

    public void layoutNodes() {
        double bounds = this.getRoot().computeFullBounds( null ).width;

        // TODO: remove getSize() ?
        //title.translate( ( getSize().getWidth() - title.getWidth() ) / 2, TOP_PADDING );
        //generationCount.translate( getSize().getWidth() - generationCount.getTextWidth() - 20, TOP_PADDING );
    }

    public void setCenterPoint( double x ) {
        pedigreeNode.setOffset( new Point2D.Double( getWidth() / 2 - x, PEDIGREE_TOP_PADDING ) );
    }

    public void reset() {
        pedigreeNode.reset();
    }

    public void displayBunny( Bunny bunny ) {
        pedigreeNode.displayBunny( bunny );
    }
}
