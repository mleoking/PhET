package edu.colorado.phet.densityandbuoyancy.view.units {
public interface UnitContainer {
    function getUnits():Units;

    function addUnitsChangeListener():Function;

    function removeUnitsChangeListener():Function;
}
}