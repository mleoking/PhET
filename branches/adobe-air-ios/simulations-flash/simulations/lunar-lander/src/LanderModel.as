//Lunar Lander Model, M.Dubson. Started June 1, 2006
class ModelLander{
	private var pos:Object; 	//position in meters. "position" is reserved word
	private var velocity:Object;	//in meters/second
	//private var acceleration:Object; //in m/s^2
	private var angle:Number;	//in radians
	private var altitude:Number;	//in meters
	private var massEmpty:Number; 	//in kg
	private var fuelInitial:Number;	//in kg
	private var fuelRemaining:Number; //in kg
	private var currentMass:Number; 
	private var thrustMax:Number; //max rate of momentum transfer in kg*m/s^2
	private var thrust:Number;	//rate of momentum transfer in kg*m/s^2
	
	function ModelLander(pos:Object, velocity:Object, massEmpty:Number, fuelInitial:Number; thrustMax:Number) {
		this.pos = pos;  //XX problem with reserved word?
		this.velocity = velocity;
		this.acceleration.aX = 0; //set initial acceleration to zero
		this.acceleration.aY = 0;
		this.angle = 0;
		this.massEmpty = massEmpty;
		this.fuelInitial = fuelInitial;
		this.fuelRemaining = fuelInitial;
		this.currentMass = massEmpty + fuelInitial;
		this.thrustMax = thrustMax;
		this.thrust = 0;
	}//end of constructor
	
	function evolveOneTimeStep(dt:Number):Void{
		var xPos = this.pos.x;
		var yPos = this.pos.y;
		var vX = this.velocity.x;
		var vY = this.velocity.y;
		var m = this.currentMass;
		var theta = this.angle; //want single call to angle and thrust, in case these are changing
		var force = this.thrust;
		var aX = (force*Math.cos(theta)/m) - Constants.little_g;
		var aY = force*Math.sin(theta)/m;
		var xPos = xPos + dt*vX + (0.5)*dt*dt*aX;
		var yPos = yPos + dt*vY + (0.5)*dt*dt*aY;
		var vX = vX + aX*dt;
		var vY = vY + aY*dt;
		this.pos = {xCoord:xPos, yCoord:yPos};
		this.velocity = {xCoord:vX, yCoord:vY};
	}
	
	function startMotion():Void{
		
	}
	function stopMotion():Void{
		
	}
	function crash():Void{
		
	}
	function setPos(pos:Number):Void{this.pos = pos;}  //pos = position
	function setVelocity(velocity:Number):Void{this.velocity = velocity;}
	//function setAcceleration(acceleration:Number):Void{this.acceleration = acceleration;}
	
	function setThrust(thrust:Number):Void{
		if(this.fuelRemaining > 0 && thrust < this.thrustMax){
			this.thrust = thrust;
		}else{
			this.thrust = 0;
		}
		if(thrust > this.thrustMax){trace("Error: requested thrust exceeds maximum.");}
	}//end of setThrust()
		
	
}//end of classLander