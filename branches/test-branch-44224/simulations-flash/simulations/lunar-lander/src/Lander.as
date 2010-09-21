//Lunar Lander Model, M.Dubson. Started June 1, 2006
class Lander{
	private var t:Number; 	//time in seconds since start
	private var lastT:Number; //last time (in ms) when position updated
	private var intervalID:Number; 
	private var pos:Object; 	//position in meters. "position" is reserved word
	private var velocity:Object;	//in meters/second
	private var acceleration:Object; //in m/s^2
	private var angle:Number;	//in radians
	public var softLandingSpeed:Number;
	public var hardLandingSpeed:Number;
	public var damageLandingSpeed:Number;
	
	
	//state conditions
	private var inMotion:Boolean;  //if simulation on
	//public var inFlight:Boolean;
	public var crashState:String; //"inFlight", "softLanded", "hardLanded", "crashLanded" (crashed into boulder handled by view)
	public var boulderCrash:Boolean;
	public var landingSpeed:Number;
	//public var crashed:Boolean;
	//public var safeLanded:Boolean;
	//public var crashLanded:Boolean;
	
	private var altitude:Number;	//in meters
	private var massEmpty:Number; 	//in kg
	private var initialFuel:Number;	//in kg
	//private var fuelAcc:Number;  //acceleration of fuel = thrust per unit mass of fuel in m/s^2
	private var specificI:Number;  //specific impulse in N*s/kg = m/s
	private var remainingFuel:Number; //in kg
	private var currentMass:Number; 
	public var maxThrust:Number; //max rate of momentum transfer in kg*m/s^2
	private var thrust:Number;	//rate of momentum transfer in kg*m/s^2
	private var thrustOn:Boolean;
	private var fuelLowAlarmSounded:Boolean;
	private var fuelOutAlarmSounded:Boolean;
	private var pi = Math.PI;
	private var sinTheta:Number;
	private var cosTheta:Number;
	private var lastCount:Number; //used for de-bugging only
	
	private var mySoundMaker:SoundMaker;
	private var theView:MovieClip;
	
	function Lander(pos:Object, velocity:Object, soundTarget:MovieClip, soundTarget2:MovieClip, theView:MovieClip) {
		this.pos = pos;  //XX problem with reserved word?
		this.velocity = velocity;
		//this.soundTarget = soundTarget;
		this.acceleration = new Object();
		this.mySoundMaker = new SoundMaker(soundTarget, soundTarget2);
		this.theView = theView;
		this.initialize();
	}//end of constructor
	
	function initialize():Void{
		this.acceleration.x = 0; //set initial acceleration to zero
		this.acceleration.y = 0;
		this.angle = 0;
		this.cosTheta = 1;
		this.sinTheta = 0;
		this.massEmpty = 6839;  //kg  (descent+ascent stages, including ascent fuel)
		this.initialFuel = 8165/10; //8165 kg is full descent fuel for LEM	(descent stage fuel only)
		this.remainingFuel = initialFuel;
		this.currentMass = massEmpty + initialFuel;
		this.maxThrust = 45000;  //in newtons
		this.thrust = 0;
		this.specificI = 3050;
		this.softLandingSpeed = 2.0;
		this.hardLandingSpeed = 6.0;
		this.damageLandingSpeed = 12;
		this.mySoundMaker.soundThrustOff();
		this.mySoundMaker.soundAlarm();
		this.boulderCrash = false;
		this.fuelLowAlarmSounded = false;
		this.fuelOutAlarmSounded = false;
	}
	
	function reInitialize(xInit:Number, yInit:Number):Void{
		this.pos.x = xInit;
		this.pos.y = yInit;
		this.velocity.x = 0;
		this.velocity.y = 0;
		this.initialize();
	}
	
	function evolveOneTimeStep():Void{
		var dt:Number = (getTimer() - this.lastT)/1000;  //time elapsed since last loop, in seconds
		this.t += dt;
		var xPos = this.pos.x;
		var yPos = this.pos.y;
		var vX = this.velocity.x;
		var vY = this.velocity.y;
		var m = this.currentMass;
		var aY;
		var aX;
		//var theta = this.angle; //want single call to angle and thrust, in case these are changing
		var force = this.thrust;
		if(yPos >= 0){
			if(yPos > 0){
				crashState = "inFlight";
				landingSpeed = undefined;
				}
			aY = (force*cosTheta/m) - Constants.g;
			aX = force*sinTheta/m;
		}else if(yPos < 0 && crashState == "inFlight"){
			this.landingSpeed = Math.pow(vX*vX+vY*vY,0.5);
			aY = aX = 0;
			vY = vX = 0;
			yPos = 0;
			//this.pos.y = 0;
			//this.angle = 0;
			this.cosTheta = 1;
			this.sinTheta = 0;
			this.setThrust(0);
			if(this.landingSpeed < this.softLandingSpeed && this.angle < 0.2){
				crashState = "softLanded";
				this.angle = 0;
			}else if(this.landingSpeed < this.hardLandingSpeed && this.angle < 0.2){
				crashState = "hardLanded";
				this.angle = 0;
			}else{
				crashState = "crashLanded";
				this.setRemainingFuel(0);
				this.angle = 0;
				mySoundMaker.soundExplosion(1);
			}
			//trace("landing speed = " + this.landingSpeed + "    "+crashState);
			//trace("vX:"+vX+"    vY:"+vY)
		}
		
		
		var xPos = xPos + dt*vX + (0.5)*dt*dt*aX;
		var yPos = yPos + dt*vY + (0.5)*dt*dt*aY;
		var vX = vX + aX*dt;
		var vY = vY + aY*dt;
		if(yPos <= 0 && vY < 0 && crashState != "inFlight"){
			yPos = 0;
			vX = vY = 0;
			aX = aY = 0;
		}
		this.pos = {x:xPos, y:yPos};
		this.velocity = {x:vX, y:vY};
		this.acceleration = {x:aX, y:aY};
		this.useFuel(dt);
		this.lastT = getTimer(); 
	}//end of evolveOneTimeStep()
	
	function startMotion():Void{
		this.t = 0; 
		this.lastT = getTimer(); 
		this.lastCount = 1;
		this.intervalID = setInterval(this, "evolveOneTimeStep", 40);
		this.crashState = "inFlight";
		this.landingSpeed = undefined;
		this.inMotion = true;
	}
	
	function stopMotion():Void{
		clearInterval(this.intervalID);
		this.inMotion = false;
	}
	
	function restartMotion():Void{
		this.lastT = getTimer(); 
		this.intervalID = setInterval(this, "evolveOneTimeStep", 40);
		this.inMotion = true;
	}
	
	function crash():Void{
		this.stopMotion();
		trace("CRASH!");
	}
	
	function setPos(pos:Object):Void{this.pos = pos;}  //pos = position
	function setVelocity(velocity:Object):Void{this.velocity = velocity;}
	function setAcceleration(acceleration:Object):Void{this.acceleration = acceleration;}
	
	function useFuel(dt:Number):Void{
		this.remainingFuel -= this.thrust*dt/this.specificI;  //thrust = del_p/del_t = v*del_m/del_t, del_m = thrust*del_t/v
		if(this.remainingFuel/this.initialFuel < 0.1 && !fuelLowAlarmSounded){
			mySoundMaker.soundAlarm(1);
			this.fuelLowAlarmSounded = true;
		}
		if(this.remainingFuel == 0 && !fuelOutAlarmSounded ){
			mySoundMaker.soundAlarm(3);
			fuelOutAlarmSounded = true;
		}
			
		this.currentMass = this.massEmpty + this.remainingFuel;
		if(remainingFuel < 0){
			this.remainingFuel = 0;
			this.thrust = 0;
		}
	}//end of useFuel
	
	function increaseThrust():Void{
		setThrust(this.thrust + 0.05*this.maxThrust);
		//trace("upThrust call successful.")
	}//end of increaseThrust()
	
	function decreaseThrust():Void{
		setThrust(this.thrust - 0.05*this.maxThrust);
		//trace("downThrust call successful.")
	}//end of decreaseThrust
		
	function setThrust(thrust:Number):Void{
		if(this.remainingFuel > 0){
			if(thrust >= 0 && thrust <= this.maxThrust){
				this.thrust = thrust;
				thrustOn = true;
				if(thrust == this.maxThrust){
					mySoundMaker.soundThrust();
				}else if(thrust > 0 && thrust < this.maxThrust){
					mySoundMaker.adjustThrustSound(100*thrust/maxThrust)
				}else if(thrust == 0){
					mySoundMaker.soundThrustOff();
					mySoundMaker.adjustThrustSoundOff();
				}
			}else if(thrust > this.maxThrust){
					this.thrust = maxThrust;
					mySoundMaker.adjustThrustSound(100*thrust/maxThrust);
			}//else{
				//this.thrust = 0;
				//mySoundMaker.soundThrustOff();
				//thrustOn = false;
			//trace("Out of fuel");
			//}
		}
	}//end of setThrust()
	
	function angleRight(){  //+3 degree change of angle
		if(this.crashState != "crashLanded"){
			this.theView.landerHolder_mc.lander_mc.rightThrust_mc.gotoAndPlay("begin");
			mySoundMaker.soundSideThrust(1);
		}
		if(this.crashState == "inFlight"){
			this.angle += 3*pi/180;
			this.cosTheta = Math.cos(this.angle);
			this.sinTheta = Math.sin(this.angle);
			//trace("angleRight call successful.")
		}
	}
	
	function angleLeft(){
		if(this.crashState != "crashLanded"){
			this.theView.landerHolder_mc.lander_mc.leftThrust_mc.gotoAndPlay("begin");
			mySoundMaker.soundSideThrust(1);
		}
		if(this.crashState == "inFlight"){
			this.angle -= 3*pi/180;
			this.cosTheta = Math.cos(this.angle);
			this.sinTheta = Math.sin(this.angle);
			//trace("angleLeft call successful.")
		}
	}
	
	function getFuelInitial():Number{
		return this.initialFuel;
	}
	
	function setRemainingFuel(remainingFuel:Number):Void{
		this.remainingFuel = remainingFuel;
	}
}//end of classLander