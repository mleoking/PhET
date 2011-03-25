//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.units {
/**
 * Adds Property<T> style listeners to Units.  Could be rewritten with Property<T>.
 */
public interface UnitContainer {
    function getUnits(): Units;

    function addUnitsChangeListener(): Function;

    function removeUnitsChangeListener(): Function;
}
}