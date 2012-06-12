// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.linegraphing.common.view.LineFormsCanvas;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;

/**
 * Canvas for the "Point Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeCanvas extends LineFormsCanvas {

    // Constructor with default properties
    public PointSlopeCanvas( final PointSlopeModel model ) {
        this( model,
              new Property<Boolean>( true ),
              new Property<Boolean>( true ),
              new Property<Boolean>( true ),
              new Property<Boolean>( true ) );
    }

    /*
     * Constructor
     * @param model
     * @param linesVisible are lines visible on the graph?
     * @param interactiveLineVisible is the interactive line visible visible on the graph?
     * @param interactiveEquationVisible is the equation visible on the interactive line?
     * @param slopeVisible are the slope (rise/run) brackets visible on the graphed line?
     */
    private PointSlopeCanvas( final PointSlopeModel model,
                              Property<Boolean> linesVisible, Property<Boolean> interactiveLineVisible,
                              Property<Boolean> interactiveEquationVisible, Property<Boolean> slopeVisible ) {
        super( model,
               linesVisible, interactiveLineVisible, interactiveEquationVisible, slopeVisible,
               new PointSlopeGraphNode( model.graph, model.mvt, model.interactiveLine, model.savedLines, model.standardLines,
                                        model.riseRange, model.runRange, model.x1Range, model.y1Range,
                                        interactiveEquationVisible, linesVisible, interactiveLineVisible, slopeVisible ),
               new PointSlopeEquationControls( interactiveEquationVisible, model.interactiveLine,
                                               model.savedLines, linesVisible, model.riseRange, model.runRange, model.x1Range, model.y1Range ) );
    }
}
