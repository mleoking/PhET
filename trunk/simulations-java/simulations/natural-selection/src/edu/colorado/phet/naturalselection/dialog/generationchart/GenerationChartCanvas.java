/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.dialog.generationchart;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.GenerationChartNode;
import edu.colorado.phet.naturalselection.view.HeredityChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * The piccolo canvas where the generation charts are drawn in. Allows changing between the charts
 *
 * @author Jonathan Olson
 */
public class GenerationChartCanvas extends PhetPCanvas {
    // current types of generation charts
    public static final int TYPE_HEREDITY = 0;
    public static final int TYPE_GENERATION = 1;

    // the last type the user viewed
    public static int lastType = NaturalSelectionDefaults.DEFAULT_GENERATION_CHART;

    private NaturalSelectionModel model;
    private GenerationChartNode generationChartNode;
    private HeredityChartNode heredityChartNode;

    /**
     * Constructor
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
        
        heredityChartNode = new HeredityChartNode( model );
        generationChartNode = new GenerationChartNode( model );

        rootNode.addChild( heredityChartNode );
        rootNode.addChild( generationChartNode );

        // set it to the last used type
        select( lastType );
    }

    public void reset() {
        heredityChartNode.reset();
        generationChartNode.reset();

        select( NaturalSelectionDefaults.DEFAULT_GENERATION_CHART );
    }

    /**
     * Change the type of chart being viewed
     * @param chartType The type of chart, for now either TYPE_HEREDITY or TYPE_GENERATION
     */
    public void select( int chartType ) {
        lastType = chartType;

        if ( chartType == TYPE_HEREDITY ) {
            heredityChartNode.setVisible( true );
            generationChartNode.setVisible( false );
        }
        else {
            generationChartNode.setVisible( true );
            heredityChartNode.setVisible( false );
        }
    }

    /**
     * Used to reset the last generation chart type when no instance is available
     */
    public static void resetType() {
        lastType = NaturalSelectionDefaults.DEFAULT_GENERATION_CHART;
    }

}
