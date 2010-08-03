package edu.colorado.phet.densityflex.components {

//TODO: should the model contain many objects (to represent the custom object), or have one mutable model that can e.g. change from lead to styrofoam?
public interface IDensityObject {
    function getVolume():NumericProperty;
    function getMass():NumericProperty;
    function getDensity():NumericProperty;
}
}