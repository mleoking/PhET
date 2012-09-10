// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;

/**
 * @author Sam Reid
 */
public interface ShapeLevelFactory {
    public ShapeLevel createLevel( int level );
}