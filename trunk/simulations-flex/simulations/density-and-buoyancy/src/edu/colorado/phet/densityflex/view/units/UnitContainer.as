package edu.colorado.phet.densityflex.view.units {
public interface UnitContainer {
    function getUnits():Units;
    function addUnitsChangeListener():Function;
    function removeUnitsChangeListener():Function;
}
}