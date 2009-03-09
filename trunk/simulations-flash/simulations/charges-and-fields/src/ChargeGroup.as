//Class describing a collection (group) of source charges and test charges
class ChargeGroup extends Observable{
	private var k:Number = 0.5*1E6;  			//prefactor in E-field equation: E= k*Q/r^2
	private var RtoD:Number = 180/Math.PI;		//convert radians to degrees
	public var charge_array:Array;		//source charges make E-field
	public var sensor_array:Array;		//test charges measure E-field
	
	function ChargeGroup(){
		charge_array = new Array();
		sensor_array = new Array();
		}
	
	function addCharge(charge1:Charge):Void{
		this.charge_array.push(charge1);		//new charge added to end of array
		this.setChanged();
		this.notifyObservers();
		//trace("Charge added.  Array length is: "+ charge_array.length);
	}
	
	function addSensor(charge1:Charge):Void{
		this.sensor_array.push(charge1);
		
	}
	
	function removeCharge(charge:Charge):Boolean{				//removes 1 array element at index i
		var len:Number = this.charge_array.length;
		for(var i = 0; i < len; i++){
			if(this.charge_array[i] == charge){
				this.charge_array.splice(i,1);
				this.setChanged();
				this.notifyObservers();
				//trace("Charge "+i+" removed. Array length is: "+this.charge_array.length);
				return true;
			}
		}
		return false;
	}
	
	function hasCharges() : Boolean {
		return charge_array.length != 0;
	}
	
	function removeSensor(i:Number):Void{
		if( i >= 0 && i < this.sensor_array.length){
			this.sensor_array.splice(i,1);  //removes 1 array element at index i
		}else{
			trace("TestChargeArray index out of bounds or array is empty.")
		}
	}
	
	function getE(x:Number,y:Number):Array {
		var EMag:Number;	//Magnitude of E-field
		var EAng:Number;	//Angle of E-field
		var Qarr:Array = this.charge_array;
		var len:Number = Qarr.length;
		var sumX:Number = 0;
		var sumY:Number = 0;
		for(var i = 0; i < len; i++){
			var xi = Qarr[i].x;
			var yi = Qarr[i].y;
			var distSq = (x - xi)*(x - xi) + (y - yi)*(y - yi)
			var distPow = Math.pow(distSq, 1.5);
			sumX = sumX + Qarr[i].q*(x - xi)/distPow;
			sumY = sumY + Qarr[i].q*(y - yi)/distPow;
		}
		var EX = this.k*sumX;	//prefactor depends on units
		var EY = this.k*sumY;
		
		EMag = Math.sqrt(EX*EX+EY*EY);
		EAng = this.RtoD*Math.atan2(EY, EX);
		//trace("  sumX:  "+sumX+"  sumY:  "+sumY+"   sumY/sumX:  "+sumY/sumX+"   angel: "+EAng);
		return [EMag, EAng, EX, EY];
	}
	
	//returns voltage and color nbr (RGB values) associated with voltage
	function getV(x:Number,y:Number):Array{
		var V:Number;	//Voltage at point x, y
		var Qarr:Array = this.charge_array;
		var len:Number = Qarr.length;
		var sumV:Number = 0;
		var maxV = 20000;//voltage at which color will saturate
		var red:Number;
		var green:Number;
		var blue:Number;
		var colorNbr;  	//RGB number of color associated with voltage
		
		for(var i = 0; i < len ; i++){
			var xi = Qarr[i].x;
			var yi = Qarr[i].y;
			var dist = Math.sqrt((x - xi)*(x - xi) + (y - yi)*(y - yi));
			sumV = sumV+ Qarr[i].q/dist;
		}
		sumV = this.k*sumV;	//prefactor depends on units
		//set color associated with voltage
			if(sumV>0){
				red =255;
				green = blue = Math.max(0,(1-(sumV/maxV))*255);
			}else{
				blue = 255;
				red = green = Math.max(0,(1-(-sumV/maxV))*255);
			}
		colorNbr = combineRGB(red,green,blue);
		//trace("R: "+red+"    G: "+green+"   B: "+blue);
		function combineRGB(red:Number,green:Number,blue:Number):Number{
			var RGB = (red<<16)|(green<<8)|blue;
			return RGB;
		}
		return [sumV,colorNbr];
	}
	
	
	function getEX(x:Number, y:Number):Number{
		var Qarr:Array = this.charge_array;
		var sum:Number = 0;
		for(var i = 0; i < Qarr.length ; i++){
			var xi = Qarr[i].x;
			var yi = Qarr[i].y;
			var distSq = (x - xi)*(x - xi) + (y - yi)*(y - yi);
			sum = sum + Qarr[i].q*(x - xi)/Math.pow(distSq,1.5);
		}
		sum = k*sum;	//prefactor depends on units
		return sum;
	}
	//return angle of E-field at position (x,y)
	function getEY(x:Number, y:Number):Number{
		var Qarr:Array = this.charge_array;
		var sum:Number = 0;
		for(var i = 0; i < Qarr.length ; i++){
			var xi = Qarr[i].x;
			var yi = Qarr[i].y;
			var distSq = (x - xi)*(x - xi) + (y - yi)*(y - yi);
			sum = sum + Qarr[i].q*(y - yi)/Math.pow(distSq,1.5);
		}
		sum = k*sum;	//prefactor depends on units
		return sum;
	}
	
	//starting at (xInit, yInit), find final position distance delS along equipotential
	function getMoveToSameVPos(VInit:Number, delS:Number, xInit:Number, yInit:Number):Array{
		var E0_array = this.getE(xInit,yInit);  //E_array = [EMag, EAng, EX, EY]
		//var VInit = getV(xInit, yInit)[0];	//getV(x,y) returns [V,color]
		var EInit = E0_array[0];
		//var EAngInit = E0_array[1];
		var EXInit = E0_array[2];
		var EYInit = E0_array[3];
		var xMid = xInit - delS*EYInit/EInit;
		var yMid = yInit + delS*EXInit/EInit;
		var E1_array = this.getE(xMid,yMid);  //E_array = [EMag, EAng, EX, EY]
		var VMid = this.getV(xMid, yMid)[0];  //returns [voltage:Number, colorNbr:Number]
		var EMid = E1_array[0];
		//var EAngPost = E1_array[1];
		var EXMid = E1_array[2];
		var EYMid = E1_array[3];
		var delX = (VMid - VInit)*EXMid/(EMid*EMid);
		var xFinal = xMid + delX;
		var yFinal = yMid + delX*EYMid/EXMid;
		return [xFinal,yFinal];
	}
	
	//starting at (xInit,yInit) move along E-field direction and get (x,y) position at which voltage is targetV 
	//this function unused at present
	function getTargetVPos(targetV:Number,xInit:Number,yInit:Number):Array{
		var E_array = this.getE(xInit,yInit);  //E_array = [EMag, EAng, EX, EY]
		var VInit = this.getV(xInit, yInit)[0];  //returns [voltage:Number, colorNbr:Number]
		var EInit = E_array[0];
		var EAngPost = E_array[1];
		var EXInit = E_array[2];
		var EYInit = E_array[3];
		var delX = (VInit - targetV)*EXInit/(EInit*EInit);
		var xFinal = xInit + delX;
		var yFinal = yInit + delX*EYInit/EXInit ;
		//var delEAng = Math.abs(EAngPost-EAngPre);
		//delEAng = Math.min(delEAng, 360-delEAng);
		//trace("delEAng: "+delEAng);
		return [xFinal,yFinal];
	}//end of getTargetVPos
	
	//override notifyObservers method
	public function notifyObservers():Void{
		if(!this.changed){
			trace("notifyObservers() called, but no change occured.");
			return;
		}
		//make local copy of observer array, so array does not change while we are processing it
		var observersSnapshot:Array = this.observers.slice(0);
		this.clearChanged();
		//Invoke update on all observers.  Count backwards because it is quicker, says Moock (Really? Is it simply because initialization of counter is set only once? -MD)
		
		for (var i:Number = observersSnapshot.length-1; i >=0; i--){
			//every observer must implement interface Observer and have an update() method
			observersSnapshot[i].update(this);
		}
	}//end of notifyObservers
	
}//end of class ChargeGroup