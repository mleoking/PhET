package edu.colorado.phet.chargesandfields {

import mx.events.SliderEvent;

public class Model {

    // named ChargeGroup.as in the Flash version

    public static var k : Number = 0.5 * 1E6; // prefactor in E-field equation: E= k*Q/r^2

    [Bindable]
    public static var kScaled : Number = Math.log(k);

    private static const RtoD : Number = 180 / Math.PI; //convert radians to degrees

    public var chargeArray : Array;
    public var sensorArray : Array;

    public function Model() {

        chargeArray = new Array;
        sensorArray = new Array();

    }

    public function addCharge(charge : Charge) : void {
        chargeArray.push(charge);
        // TODO: setChanged or notifyObservers?
    }

    /*
     public function addSensor(sensor : Sensor) {
     // TODO: lots of stuff
     }
     */

    public static function setKScaled(event : SliderEvent) : void {
        k = Math.exp(event.value);
        kScaled = Math.log(k);
    }

    public function removeCharge(charge : Charge) : Boolean {

        // pull out removal from array into helper function somewhere
        var len : uint = chargeArray.length;
        for (var i : uint = 0; i < chargeArray.length; i++) {
            if (charge == chargeArray[i]) {
                chargeArray.splice(i, 1);
                // TODO: setChanged or notifyObservers?

                return true;
            }
        }

        return false;
    }

    public function hasCharges() : Boolean {
        return chargeArray.length != 0;
    }

    /*
     public function removeSensor(sensor : Sensor) {
     // TODO: implement
     }
     */

    public function getE(x : Number, y : Number) : Array {

        // TODO: optimize function for AS3

        var eMag : Number; //Magnitude of E-field
        var eAng : Number; //Angle of E-field
        var len : uint = chargeArray.length;
        var sumX : Number = 0;
        var sumY : Number = 0;

        for (var i : uint = 0; i < len; i++) {
            var xi : Number = chargeArray[i].modelX;
            var yi : Number = chargeArray[i].modelY;
            var distSq : Number = (x - xi) * (x - xi) + (y - yi) * (y - yi)
            var distPow : Number = Math.pow(distSq, 1.5);
            sumX = sumX + chargeArray[i].q * (x - xi) / distPow;
            sumY = sumY + chargeArray[i].q * (y - yi) / distPow;
        }
        var EX : Number = k * sumX;	//prefactor depends on units
        var EY : Number = k * sumY;

        eMag = Math.sqrt(EX * EX + EY * EY);
        eAng = RtoD * Math.atan2(EY, EX);
        //trace("  sumX:  "+sumX+"  sumY:  "+sumY+"   sumY/sumX:  "+sumY/sumX+"   angel: "+EAng);

        // TODO: turn this returny goodness into a class
        return [eMag, eAng, EX, EY];
    }

    /*
     private function combineRGB(red : Number, green : Number, blue : Number) : uint {
     return (red << 16) | (green << 8) | blue;
     }
     */

    //returns voltage and color nbr (RGB values) associated with voltage
    public function getV(x : Number, y : Number) : Array {
        var len : uint = chargeArray.length;
        var sumV : Number = 0;
        var maxV : Number = 20000;//voltage at which color will saturate
        var red : Number;
        var green : Number;
        var blue : Number;

        var xi : Number;
        var yi : Number;
        var dist : Number;

        for (var i : uint = 0; i < len; i++) {
            var charge : Charge = chargeArray[i];
            xi = charge.modelX;
            yi = charge.modelY;

            dist = Math.sqrt((x - xi) * (x - xi) + (y - yi) * (y - yi));
            sumV += charge.q / dist;
        }
        sumV *= k;	//prefactor depends on units

        //set color associated with voltage
        if (sumV > 0) {
            red = 255;
            green = blue = Math.max(0, (1 - (sumV / maxV)) * 255);
        } else {
            blue = 255;
            red = green = Math.max(0, (1 - (-sumV / maxV)) * 255);
        }

        return [ sumV, (red << 16) | (green << 8) | blue ];
    }

    public function getVColor(x : Number, y : Number) : int {
        var len : int = chargeArray.length;
        var sumV : Number = 0;

        var xi : Number;
        var yi : Number;

        var charge : Charge;


        for (var i : int = 0; i < len; i++) {
            charge = chargeArray[i];
            xi = x - charge.modelX;
            yi = y - charge.modelY;

            sumV += charge.q / Math.sqrt(xi * xi + yi * yi);
        }


        //sumV *= k;	//prefactor depends on units

        var red : int; // = 0;
        var green : int; // = 0;
        var blue : int; // = 0;

        var scaled : Number = sumV * ( k / 20000 ); // voltage will saturate at 20000


        //set color associated with voltage
        if (sumV > 0) {
            red = 255;
            green = blue = Math.max(0, ( 1 - scaled ) * 255);
            /*
             if( scaled < 1 ) {
             green = blue = ( 1 - scaled ) * 255;
             }
             */
        } else {
            blue = 255;
            red = green = Math.max(0, ( 1 + scaled ) * 255);
            /*
             if( scaled > -1 ) {
             red = green = ( 1 + scaled ) * 255;
             }
             */
        }

        return (red << 16) | (green << 8) | blue;
    }

    public function getEX(x : Number, y : Number) : Number {
        var sum : Number = 0;
        for (var i : uint = 0; i < chargeArray.length; i++) {
            var xi : Number = chargeArray[i].modelX;
            var yi : Number = chargeArray[i].modelY;
            var distSq : Number = (x - xi) * (x - xi) + (y - yi) * (y - yi);
            sum = sum + chargeArray[i].q * (x - xi) / Math.pow(distSq, 1.5);
        }
        sum = k * sum;	//prefactor depends on units
        return sum;
    }

    //return angle of E-field at position (x,y)
    public function getEY(x:Number, y:Number):Number {
        var sum : Number = 0;
        for (var i : uint = 0; i < chargeArray.length; i++) {
            var xi : Number = chargeArray[i].modelX;
            var yi : Number = chargeArray[i].modelY;
            var distSq : Number = (x - xi) * (x - xi) + (y - yi) * (y - yi);
            sum = sum + chargeArray[i].q * (y - yi) / Math.pow(distSq, 1.5);
        }
        sum = k * sum;	//prefactor depends on units
        return sum;
    }

    //starting at (xInit, yInit), find final position distance delS along equipotential
    public function getMoveToSameVPos(VInit:Number, delS:Number, xInit:Number, yInit:Number):Array {
        var E0_array : Array = this.getE(xInit, yInit);  //E_array = [EMag, EAng, EX, EY]
        //var VInit = getV(xInit, yInit)[0];	//getV(x,y) returns [V,color]
        var EInit : Number = E0_array[0];
        //var EAngInit = E0_array[1];
        var EXInit : Number = E0_array[2];
        var EYInit : Number = E0_array[3];
        var xMid : Number = xInit - delS * EYInit / EInit;
        var yMid : Number = yInit + delS * EXInit / EInit;
        var E1_array : Array = this.getE(xMid, yMid);  //E_array = [EMag, EAng, EX, EY]
        var VMid : Number = this.getV(xMid, yMid)[0];  //returns [voltage:Number, colorNbr:Number]
        var EMid : Number = E1_array[0];
        //var EAngPost = E1_array[1];
        var EXMid : Number = E1_array[2];
        var EYMid : Number = E1_array[3];
        var delX : Number = (VMid - VInit) * EXMid / (EMid * EMid);
        var xFinal : Number = xMid + delX;
        var yFinal : Number = yMid + delX * EYMid / EXMid;
        return [xFinal,yFinal];
    }

    //starting at (xInit,yInit) move along E-field direction and get (x,y) position at which voltage is targetV
    //this function unused at present
    public function getTargetVPos(targetV:Number, xInit:Number, yInit:Number):Array {
        var E_array : Array = this.getE(xInit, yInit);  //E_array = [EMag, EAng, EX, EY]
        var VInit : Number = this.getV(xInit, yInit)[0];  //returns [voltage:Number, colorNbr:Number]
        var EInit : Number = E_array[0];
        // UNUSED!!! var EAngPost : Number = E_array[1];
        var EXInit : Number = E_array[2];
        var EYInit : Number = E_array[3];
        var delX : Number = (VInit - targetV) * EXInit / (EInit * EInit);
        var xFinal : Number = xInit + delX;
        var yFinal : Number = yInit + delX * EYInit / EXInit ;
        //var delEAng = Math.abs(EAngPost-EAngPre);
        //delEAng = Math.min(delEAng, 360-delEAng);
        //trace("delEAng: "+delEAng);
        return [xFinal,yFinal];
    }//end of getTargetVPos

    // TODO: event handlers, etc

}
}