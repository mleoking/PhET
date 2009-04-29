/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.PedigreeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * The piccolo canvas where the generation charts are drawn in. Allows changing between the charts
 *
 * @author Jonathan Olson
 */
public class GenerationChartCanvas extends PhetPCanvas {

    private NaturalSelectionModel model;
    private PedigreeNode pedigreeNode;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public GenerationChartCanvas( NaturalSelectionModel model ) {
        // TODO: allow the generation chart to change size
        super( NaturalSelectionDefaults.GENERATION_CHART_SIZE );
        setPreferredSize( NaturalSelectionDefaults.GENERATION_CHART_SIZE );

        this.model = model;

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // add both of the charts

        pedigreeNode = new PedigreeNode( model );

        rootNode.addChild( pedigreeNode );

        pedigreeNode.setOffset( new Point2D.Double( 0, 30 ) );

        PText title = new PText( "Generation Chart" );
        title.setFont( new PhetFont( 16, true ) );
        title.translate( ( getPreferredSize().getWidth() - title.getWidth() ) / 2, 5 );
        rootNode.addChild( title );
    }

    public void reset() {
        pedigreeNode.reset();
    }

}
