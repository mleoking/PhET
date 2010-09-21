
class Body{
	public var mass:Number;
	public var pos:Vector;	//current position vector
	public var posCopy:Vector;//copy of current position vector, need for Runge-Kutta algorithm
	public var prePos:Vector; //position at previous time step
	public var vel:Vector;	//velocity vector
	public var acc:Vector; //acceleration vector
	public var preAcc:Vector; //acceleration in previous time step
	public var initMass:Number; //initial mass, could change due to collisions
	public var initPos:Vector; //initial position
	public var initVel:Vector; //initial velocity
	public var pos_arr:Array;	//current and last 3 positions
	public var acc_arr:Array;	//current and last 3 accelerations
	
	function Body(initMass:Number, initPos:Vector, initVel:Vector){
		this.initMass = Math.max(initMass, 0.001);
		this.initPos = initPos;
		this.initVel = initVel;
		this.mass = this.initMass;  
		this.pos = this.initPos.duplicateVector(); //new Vector(this.initPos.x, this.initPos.y);
		this.vel = this.initVel.duplicateVector(); //new Vector(this.initVel.x, this.initVel.y);
		this.prePos = new Vector(0,0);
		this.acc = new Vector(0,0);  //current acceleration
		this.preAcc = new Vector(0,0);
		this.pos_arr = new Array(6);
		this.acc_arr = new Array(6);
	}
	
	function setMass(mass:Number):Void{
		this.initMass = Math.max(mass, 0.000001);
		this.mass = this.initMass;
	}
	
	function setPos(pos:Vector):Void{
		this.pos = pos.duplicateVector();
		this.initPos = pos.duplicateVector();
	}
	
	function setVel(vel:Vector):Void{
		this.vel = vel.duplicateVector();
		this.initVel = vel.duplicateVector();
	}
	
	function setMassPosVel(mass:Number, pos:Vector, vel:Vector):Void{
		this.setMass(mass);
		this.setPos(pos);
		this.setVel(vel);
	}
	
	function setCollisionMass(mass:Number):Void{
		this.mass = Math.max(mass, 0.000001);
	}
	
	function duplicateBody():Body{
		var bodyCopy:Body = new Body(this.mass, this.initPos, this.initVel);
		return bodyCopy;
	}
	
}//end of class