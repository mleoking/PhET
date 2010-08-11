package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.components.IDensityObject;
import edu.colorado.phet.densityflex.components.NumericProperty;
import edu.colorado.phet.densityflex.model.Substance;

public class DefaultDensityObject implements IDensityObject {
    private const volume:NumericProperty = new NumericProperty( "Volume", "m^3", 1.0 );
    private const mass:NumericProperty = new NumericProperty( "Mass", "kg", 1.0 );
    private const density:NumericProperty = new NumericProperty( "Density", "kg/m^3", 1.0 );
    private var _substance:Substance = Substance.STYROFOAM;
    private var substanceListeners:Array = new Array();

    public function DefaultDensityObject() {
        function massChanged():void {
            if ( isDensityFixed() ) {
                getVolume().value = getMass().value / getDensity().value;
            }
            else {
                //a change in mass or volume causes a change in density
                getDensity().value = getMass().value / getVolume().value;
            }
        }

        getMass().addListener( massChanged );

        function volumeChanged():void {
            if ( isDensityFixed() ) {
                getMass().value = getVolume().value * getDensity().value;
            }
            else { //custom object
                //a change in mass or volume causes a change in density
                getDensity().value = getMass().value / getVolume().value;
            }
        }

        getVolume().addListener( volumeChanged );

        function densityChanged():void {
            //TODO: Switch into "custom object" mode
            //This could be confusing because it will switch the behavior of the other sliders
            
            var changed:Boolean = false;
            for each ( var s:Substance in Substance.OBJECT_SUBSTANCES ) {
                if ( s.getDensity() == density.value ) {
                    substance = s;
                    changed = true;
                }
            }
            if (!changed){
                substance = new Substance("Custom",density.value);
            }
        }

        getDensity().addListener( densityChanged );
    }

    public function addSubstanceListener( listener:Function ):void {
        substanceListeners.push( listener );
    }

    public function set substance( substance:Substance ):void {
        if ( !this._substance.equals(substance) ) {
            this._substance = substance;
            this.density.value = substance.getDensity();
            for each ( var listener:Function in substanceListeners ) {
                listener();
            }
        }
    }

    public function get substance():Substance {
        return _substance;
    }

    private function isDensityFixed():Boolean {
        return true;
    }

    public function getVolume():NumericProperty {
        return volume;
    }

    public function getMass():NumericProperty {
        return mass;
    }

    public function getDensity():NumericProperty {
        return density;
    }

    public function getSubstance():Substance {
        return substance;
    }
}
}