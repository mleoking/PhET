/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.PopulationGraphNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the current bunny population with a bar chart. Contained in the control panel
 *
 * @authr Jonathan Olson
 */
public class PopulationCanvas extends PhetPCanvas implements NaturalSelectionModel.NaturalSelectionModelListener {

    /**
     * The canvas size for the Population Canvas
     */
    public static final Dimension CANVAS_SIZE = new Dimension( 80, 200 );

    private PNode rootNode;
    private PopulationGraphNode populationGraphNode;
    private NaturalSelectionModel model;

    /**
     * Constructor
     *
     * @param model The Natural Selection model
     */
    public PopulationCanvas( NaturalSelectionModel model ) {
        super( CANVAS_SIZE );

        this.model = model;

        rootNode = new PNode();
        addWorldChild( rootNode );

        // add the piccolo node to display the bar chart
        populationGraphNode = new PopulationGraphNode( NaturalSelectionDefaults.DEFAULT_NUMBER_OF_BUNNIES );
        rootNode.addChild( populationGraphNode );

        setPreferredSize( CANVAS_SIZE );

        // gag the default border that surrounds PhetPCanvas
        setBorder( null );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        this.model.addListener( this );
    }

    public void reset() {
        populationGraphNode.reset();
    }


    //----------------------------------------------------------------------------
    // Event Handlers
    //----------------------------------------------------------------------------

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void onNewBunny( Bunny bunny ) {
        // when a new bunny arrives, let the piccolo node know. it will keep track of the deaths
        bunny.addListener( populationGraphNode );
        populationGraphNode.updatePopulation( model.getPopulation() );
    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}
