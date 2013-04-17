//system of Stern-Gerlach boxes
class SGSystem{
	private var numBoxes:Number;  	//number of SG boxes in system of boxes, can be set only at construction
	var SGBoxes:Array;		//array of SG boxes
	var myViewCalculator:ViewCalculator;
	var firedParticleState:String;
	var zDist:Number; 		//distance along z-axis between SG boxes
	var mySpin:SpinParticle;		//spin 1/2 particle
	var numParticlesFired:Number; 	//number of spins fired through system

	//var statistics:Array;  //for each box, number of up spins and down spins detected
	
	function SGSystem(numBoxes:Number){
		this.numBoxes = numBoxes;
		//trace("SGSystem constructor called.  numBoxes = " + this.numBoxes);
		this.SGBoxes = new Array(this.numBoxes);
		for (var i:Number = 0; i < numBoxes; i++){
			//new SGBox(boxNumber, xCoord, yCoord, zCoord, BlockSetting)
			this.SGBoxes[i] = new SGBox(i+1, 1); //SGBox(i+1, outlet = +1 or -1 or 0); 
			//this.statistics[i] = new Object();
			//this.statistics[i] = {numUp:0, numDown:0};														
		}//end of for loop
		this.myViewCalculator = new ViewCalculator(this);
		this.numParticlesFired = 0;
		this.mySpin = new SpinParticle(0);  //default up state
		
	}//end of constructor
	
	function setAngleOfSGBox(boxNum:Number, angle:Number){  //boxNum is 1, 2, or 3, angle is in degrees
		var i:Number = boxNum - 1;
		this.SGBoxes[i].setAngle(angle);
		//if(boxNum < 3){
			//this.resetBoxPositions();
		//}
	}
	
	function setOutletOfSGBox(boxNum:Number, outlet:Number){
		var i:Number = boxNum - 1;
		this.SGBoxes[i].setOutlet(outlet);
	}
	
	function setFiredParticleState(particleState:String):Void{
		this.firedParticleState = particleState;
		if(this.firedParticleState == "up"){
			this.myViewCalculator.setSpinIndicator(1);
			this.mySpin.setAngle(0);
		}else if(this.firedParticleState == "down"){
			this.mySpin.setAngle(180);
			this.myViewCalculator.setSpinIndicator(3);
		}else if(this.firedParticleState == "middle"){
			this.mySpin.setAngle(-90);
			this.myViewCalculator.setSpinIndicator(2);
		}else if(this.firedParticleState == "unpolarized"){
			this.myViewCalculator.setSpinIndicator(4);
			var randomAngle = Math.random()*360 - 180;
			//trace(randomAngle);
			this.mySpin.setAngle(randomAngle);
		}else{
			trace("invalid particleState string:" + this.firedParticleState);
		}
		
		//if beam is on, then reset beam with call to beamOn()
		if(this.myViewCalculator.beamIsOn){
			this.myViewCalculator.beamOn();
		}
		//trace("firedParticleState:" + this.firedParticleState);
	}//end of beamOn()

	function fireParticle():Void{
		//if particle initially unpolorized, then spin the roulette wheel
		if(this.firedParticleState == "unpolarized"){
			var randomAngle = Math.random()*360 - 180;
			this.mySpin.setAngle(randomAngle);
		}
		//reset thru states
		for (var i:Number = 0; i < numBoxes; i++){
			this.SGBoxes[i].particleGetsThru = false;
		}
		this.threadBoxes();
		this.myViewCalculator.fireSpinBall();
	}//end of fireParticle();
	
	function fireNParticles(N:Number):Void{
		for(var i:Number = 1; i <= N; i++){
			this.fireParticle();
		}
	}

	//just to set probabilities so can be read by beamOn function and used to set alpha of beams
	function fireInvisibleParticle():Void{
		if(this.firedParticleState == "unpolarized"){
			var randomAngle = Math.random()*360 - 180;
			this.mySpin.setAngle(randomAngle);
		}
		//reset thru states
		for (var i:Number = 0; i < numBoxes; i++){
			this.SGBoxes[i].particleGetsThru = false;
		}
		this.threadBoxes();
	}
	
	//allow particles to pass thru the chain the SG boxes, updating statistics
	function threadBoxes():Void{
		var currentAngleOfPart = this.mySpin.getAngle();
		for (var i:Number = 0; i < numBoxes; i++){
			if(i == 0 || (i > 0 && this.SGBoxes[i-1].particleGetsThru)){
				currentAngleOfPart = this.SGBoxes[i].processParticle(currentAngleOfPart);
			}
		}
	}//end of threadBoxes()
	
	function getProbabilities():Array{
		this.fireInvisibleParticle();  //run a particle thru to set the probabilities
		var probUp:Array = new Array(this.numBoxes);
		for(var i:Number = 0; i < this.numBoxes; i++){
			probUp[i] = this.SGBoxes[i].probUp;
			//trace("probUp of box " + (i+1) + ": " + probUp[i]);
		}
		return probUp;
	}
	
	function resetParticlesFired():Void{
		this.numParticlesFired = 0;
		for (var i:Number = 0; i < numBoxes; i++){
			this.SGBoxes[i].statistics.numUp = 0;
			this.SGBoxes[i].statistics.numDown = 0;
			this.SGBoxes[i].numParticlesThru = 0;
		}
	}
	
	function getNumBoxes():Number{
		return this.numBoxes;
	}
	
	function testComm():String{
		trace("testComm() called.");
		return "testComm returns";
	}
	
}//end of class 
