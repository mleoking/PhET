class SGBox{
	var boxNumber:Number;  		//1, 2, or 3 since up to 3 boxes in SG box chain
	private var angle:Number;  	//angle(degrees) of box relative to up-direction: -180 to +180
	private var outlet:Number;	//+1 = up let thru, -1 = down let thru, or 0 = both let thru 
	var statistics:Object;		//number of particles detected up, number detected down
	var numParticlesThru:Number;	//number of particles that have passed through SG box
	var outputPartIsUp:Boolean; 		//true if outgoing particle detected in up state, initially = undefined;
	var particleGetsThru:Boolean;
	var angleOfInputPart:Number;	//angle of incoming particle relative to absolute +y direction
	var angleOfOutputPart:Number;
	var probUp:Number;
	
	//static var RADIUS:Number = 30;	//distance from center to exit hole (TBD, in what units?)
	//var x:Number;		//x position
	//var y:Number;
	//var z:Number;
	
	function SGBox(i:Number, outlet:Number){ 
		this.angle = 0;
		this.boxNumber = i;
		if(outlet == 1 || outlet == -1 || outlet == 0){
			this.outlet = outlet;
		}else{
			trace("Invalid output number on SGBox " + i);
		}
		this.statistics = {numUp:0, numDown:0};
		outputPartIsUp = undefined;
		//trace("SGBox constructor called. boxNumber is " + this.boxNumber);
	}
	
	function getAngle():Number{
		return this.angle;
	}
	
	function getAngleInRads():Number{
		return this.angle*Math.PI/180;
	}
	
	function getOutletRadius():Number{
		var r:Number;
		if(this.outlet == 1){
			r = 1; //r = 1.04;
		}else if (this.outlet == -1){
			r = -1; //r = -0.96;
		}
		//return this.outlet;
		return r;
	}
	
	function getOutlet():Number{
		return this.outlet;
	}
	
	function setOutlet(outlet:Number):Void{
		if(outlet == 1 || outlet == -1 || outlet == 0){
			this.outlet = outlet;
		}else{
			trace("Invalid value for outlet:" + outlet);
		}
		//trace("outlet for box "+ this.boxNumber + "  =  " + this.outlet);
	}
	
	function setAngle(angle:Number){  //angle is in degrees
		if(angle <= 180 && angle > -180){
			this.angle = angle;
		}else{
			trace("Invalid angle input on SG box " + this.boxNumber + ".  Angle input = " + angle);
		}
	}
	
	//transfer incoming particle thru SG Box and detect at output
	function processParticle(inputAngle:Number):Number{
		this.angleOfInputPart = inputAngle;
		//trace("angleOfPart before SG" + this.boxNumber + " is " + this.angleOfInputPart);
		//trace("angle:" + angle);
		var amplitude:Number = Math.cos(((this.angle - inputAngle)/2)*Math.PI/180);
		this.probUp = amplitude*amplitude;
		this.numParticlesThru += 1;
		//trace("probUp:"+probUp+"    outlet:"+this.outlet);
		if(Math.random() < this.probUp){
			this.statistics.numUp += 1;
			this.outputPartIsUp = true;
			this.angleOfOutputPart = this.angle;
		}else{
			this.statistics.numDown += 1;
			this.outputPartIsUp = false;
			if(this.angle > 0){
				this.angleOfOutputPart = this.angle - 180;
			}else if(this.angle <= 0){
				this.angleOfOutputPart = this.angle + 180;
			}
		}//end of if..else
		//trace("angleOfOutputPart after SG" + this.boxNumber + " is " + angleOfOutputPart + "    particleIsUp:" + this.outputPartIsUp);
		var up:Boolean = this.outputPartIsUp;
		if((up && this.outlet == 1) || (!up && this.outlet == -1) ||this.outlet == 0){
			this.particleGetsThru = true;
		}else{
			this.particleGetsThru = false;
		}
		//trace("particleGetsThru:"+ this.particleGetsThru);
		return this.angleOfOutputPart;
	}
	
}//end of class definition