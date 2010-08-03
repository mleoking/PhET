package edu.colorado.phet.densityflex.view {
public class ConstantDensityObject extends DefaultDensityObject {
    public function ConstantDensityObject() {
        super();
        function massChanged():void{
            getVolume().value = getMass().value / getDensity().value;
        }
        getMass().addListener(massChanged);
        
        function volumeChanged():void{
            getMass().value = getVolume().value * getDensity().value;
        }
        getVolume().addListener(volumeChanged);
    }
}
}