// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;

/**
 * Factory for creating the different "Shape" levels.
 * This pattern is used so that levels can be created dynamically and resampled when needed.
 *
 * @author Sam Reid
 */
public interface ShapeLevelFactory {
    public ShapeLevel createLevel( int level );
}