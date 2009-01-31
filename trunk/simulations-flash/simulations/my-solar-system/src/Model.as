class Model extends Observable{
	private var G:Number; 	//Big G = Newton's gravitational constant
	private var N:Number;	//number of bodies in system 
	private var bodies:Array; //array of bodies
	private var configs:Array; //array of configurations of bodies
	//private var initialBodies:Array; //array of initial bodies, needed for resetting after collision
	private var forces:Array; //array of forces, force[i][j] = force on body i due to body j
	private var maxAccel:Number; //maximum acceleration in system
	//private var times:Array;
	private var stepTimes:Array;	//array of values of dt = timestep
	private var nbrStepsPerFrame:Number; 	//nbr of steps between screen redraws
	private var nbrStepsPerFrame_arr:Array;
	private var velCM:Vector; //velocity of Center-of-Mass
	private var time:Number;
	private var timeStep:Number; //time step
	private var intervalID:Number;
	public var integrationOn:Boolean;
	public var RKintegrationOn:Boolean;	//true if Runge-Kutta integration used, obsolete
	public var steppingForward:Boolean;  //true if in middle of stepForward()
	public var resettingN:Boolean;  //true if number of bodies being reset
	public var cmMotionRemoved:Boolean;
	public var wantCMMotionRemoved:Boolean;
	private var initialView:Object;  //if type is set to InitialView, get class name conflict error message
	private var trajectoryView:Object;
	private var collisionJustOccurred:Boolean;
	
	function Model(){
		this.G = 10000; //overall scale
		this.time = 0;
		this.maxAccel = 0;
		this.timeStep = 0.009;//0.005;
		this.nbrStepsPerFrame = 4;
		//array of stepTimes units of what? years?
		this.stepTimes = new Array(0.0005, 0.00075, 0.0011, 0.0017, 0.0025, 0.004, 0.006, 0.010, 0.015, 0.025, 0.04); 
		this.nbrStepsPerFrame_arr = new Array(25, 20, 16, 12, 10, 8, 6, 5, 4, 3, 2);//(25, 15, 15, 10, 10, 6, 6, 4, 4, 2, 2);
		this.velCM = new Vector(0,0);
		this.setN(2);
		this.integrationOn = false;
		this.steppingForward = false;
		this.RKintegrationOn = false;
		this.resettingN = false;
		this.cmMotionRemoved = false;
		this.wantCMMotionRemoved = true;
		this.collisionJustOccurred = false;
		
	}//end of constructor
	
	public function registerView(view:Object):Void{
		super.addObserver(view);
	}
	
	public function registerInitialView(view:Object):Void{
		this.initialView = view;
	}
	
	public function registerTrajectoryView(view:Object):Void{
		this.trajectoryView = view;
	}
	
	
	function setN(N:Number):Void{
		this.stopIntegration();
		this.resettingN = true;
		//reset ComboBox to default
		_root.controlPanel_mc.myComboBox.selectedIndex = 0;
		this.bodies = new Array(N);
		//this.initialBodies = new Array(N);
		this.N = this.bodies.length;
		//set up initial positions, velocities of bodies (4 bodies max);
		this.setInitialBodies();
		this.forces = new Array(this.N);
		for(var i = 0; i < this.N; i++){
			this.forces[i] = new Array(this.N);
			this.forces[i][i] = new Vector(0,0);
		}
		this.setForcesAndAccels();
		this.reset();
		this.setVelCM();
		super.notifyObservers();
		this.resettingN = false;
	}
	
	function getN():Number{
		return this.N;
	}
	

	function setForcesAndAccels():Void{ //compute current forces and  acceleration of all bodies in system
		//update forces matrix
		//var zeroForce:Vector = new Vector(0,0); 
		//indices i and j start at 0!
		for (var i = 0; i < this.N ; i++){
			//this.forces[i][i] = zeroForce;  //diagonal zeroForce forces now set in setN(N)
			for(var j = i+1; j < this.N; j++){
				this.forces[i][j] = this.getForce(i, j);
				this.forces[j][i] = new Vector(-this.forces[i][j].x, -this.forces[i][j].y);
			}
		}
		//update accelerations of bodies
		for (var n = 0; n < this.N; n++){
			bodies[n].acc.x = 0;
			bodies[n].acc.y = 0;
			var massN = bodies[n].mass;
			for (var m = 0; m < this.N; m++){
				bodies[n].acc.x += forces[n][m].x/massN;
				bodies[n].acc.y += forces[n][m].y/massN;
				var currentAccel:Number = this.bodies[n].acc.getMagnitude();
				if(currentAccel > this.maxAccel){
					this.maxAccel = currentAccel;
					if(this.maxAccel*this.timeStep > 150){
						this.collisionJustOccurred = true;
						//var index_arr:Array = this.getIndicesOfClosestBodies(); //dist_arr[0],dist_arr[1] = indices of two colliding bodies, dist_arr[2] = distance between them
						//trace("maxA*dt = " + this.maxAccel*this.timeStep);
						//trace("i:"+index_arr[0] + "   j:"+index_arr[1] + "      r:"+index_arr[2]);
					}
				}
			}
		}
	}//end of setForcesAndAccels()
	
	function getForce(i:Number, j:Number):Vector{  //vector force on body 1 from body 2
		var body1 = bodies[i];
		var body2 = bodies[j];
		var GM1M2 = this.G*body1.mass*body2.mass;
		var delX:Number = body2.pos.x - body1.pos.x;	//x2 - x1;
		var delY:Number = body2.pos.y - body1.pos.y;	//y2 - y1;
		var distSq:Number = delX*delX + delY*delY;
		var dist:Number = Math.sqrt(distSq);
		var force:Vector = new Vector(0,0);
		var product:Number = GM1M2/(distSq*dist);  
		force.x = product*delX;
		force.y = product*delY;
		return force;
	}
	
	
	function getDistance(i:Number, j:Number):Number{  //distance between body i and body j; i,j = 0,1..N
		var body1 = bodies[i];
		var body2 = bodies[j];
		var delX:Number = body2.pos.x - body1.pos.x;	//x2 - x1;
		var delY:Number = body2.pos.y - body1.pos.y;	//y2 - y1;
		var distSq:Number = delX*delX + delY*delY;
		var dist:Number = Math.sqrt(distSq);
		return dist;
	}
	
	function collideBodies(i:Number, j:Number):Void{ //when bodies i and j (i,j = 0,1..N-1) collide, combine them into single body
		//trace("indices are i:"+i+"   j:"+j);
		this.stopIntegration();//interrupt integration while bodies combining
		//trace("Before collision, after stopIntegration.   this.steppingForward = " + this.steppingForward);
		//this.traceCurrentVelCM();
		var bodyA:Body = this.bodies[i];
		var bodyB:Body = this.bodies[j];
		var totalMass:Number = bodyA.mass + bodyB.mass;
		var velXCM:Number = (1/totalMass)*(bodyA.mass*bodyA.vel.x + bodyB.mass*bodyB.vel.x);
		var velYCM:Number = (1/totalMass)*(bodyA.mass*bodyA.vel.y + bodyB.mass*bodyB.vel.y);
		var velCM:Vector = new Vector(velXCM, velYCM);
		var posXCM:Number = (1/totalMass)*(bodyA.mass*bodyA.pos.x + bodyB.mass*bodyB.pos.x);
		var posYCM:Number = (1/totalMass)*(bodyA.mass*bodyA.pos.y + bodyB.mass*bodyB.pos.y);
		var posCM:Vector = new Vector(posXCM, posYCM);
		var largerMass = Math.max(bodyA.mass, bodyB.mass);
		//var indexToKeep:Number;
		var indexToHide:Number;
		var bigBody:Body;
		var smallBody:Body;
		if(largerMass == bodyA.mass){
			//indexToKeep = i;
			indexToHide = j;
			bigBody = bodyA;
			smallBody = bodyB;
		}else{
			//indexToKeep = j;
			indexToHide = i;
			bigBody = bodyB;
			smallBody = bodyA;
		}
		//trace("bodyA.mass = " + bodyA.mass + "     largerMass = " + largerMass + "      indexToHide = "+indexToHide);
		
		bigBody.mass = totalMass;
		bigBody.pos = posCM;
		bigBody.vel = velCM;
		smallBody.setCollisionMass(0);
		//get smaller body off stage, heading away, out-of-sight
		smallBody.pos.setComponents(3000,0);
		smallBody.vel.setComponents(10,0);
		smallBody.preAcc.setComponents(0,0);
		
		//this.removeBody(indexToKill);
		this.setForcesAndAccels();
		this.trajectoryView.hideBody(indexToHide);
		this.trajectoryView.showExplosion(posCM);
		this.maxAccel = 0;
		this.startIntegration();
	}//end of collide bodies
	
	function removeBody(i:Number):Void{
		this.bodies.splice(i,1);
		this.N = this.bodies.length;
	}
	
	function getIndicesOfClosestBodies():Array{
		var minDist = 10000;
		var dist_arr:Array = new Array(3);
		for(var i:Number = 0; i < this.N; i++){
			for(var j:Number = i+1; j < this.N; j++){
				var distIJ:Number = this.getDistance(i,j);
				if(distIJ < minDist){
					minDist = distIJ;
					dist_arr[0] = i;
					dist_arr[1] = j;
					dist_arr[2] = minDist;
				}
			}
		}
		return dist_arr;
	}//end of getIndicesOfClosestBodies()
	
	
	function setVelCM():Void{	//set velocity of center-of-mass
		var M:Number = 0; //total mass
		var sumMVX:Number = 0;
		var sumMVY:Number = 0;
		for(var i:Number = 0; i < this.N; i++){
			M += this.bodies[i].mass;
			sumMVX += this.bodies[i].mass*this.bodies[i].initVel.x;
			sumMVY += this.bodies[i].mass*this.bodies[i].initVel.y;
		}
		this.velCM.x = sumMVX/M;
		this.velCM.y = sumMVY/M;
		//trace("this.velCM.y: "+this.velCM.y);
	}
	
	function startIntegration():Void{
		//trace("startIntegration called.  this.integrationOn = "+this.integrationOn);
		if(!this.integrationOn){
			this.integrationOn = true;
			this.initialView.setTextFieldType("dynamic");
			this.makeFirstStep();
			//trace("After first step:");
			//this.traceCurrentVelCM();
			this.intervalID = setInterval(this, "stepForwardNTimes", 10, this.nbrStepsPerFrame); 
		}
	}//end of startIntegration
	
	
	function stopIntegration():Void{
		if(this.integrationOn && !this.steppingForward){
			clearInterval(this.intervalID);
			this.integrationOn = false;
		}
	}//end of stopIntegration()
	
	function reset():Void{
		clearInterval(this.intervalID);
		//this.N = this.initialBodies.length;
		for (var i = 0; i < this.N; i++){
			//this.bodies = new Array(N);
			//this.bodies[i] = this.initialBodies[i].duplicateBody();
			this.bodies[i].mass = this.bodies[i].initMass;
			this.bodies[i].pos = this.bodies[i].initPos.duplicateVector();
			this.bodies[i].vel = this.bodies[i].initVel.duplicateVector();
		}//end of for loop
		this.trajectoryView.showAllBodies();
		this.maxAccel = 0;
		this.time = 0;
		this.initialView.setElapsedTime(this.time);
		this.cmMotionRemoved = false;
		this.integrationOn = false;
		this.initialView.setTextFieldType("input");
		this.steppingForward = false;
		super.notifyObservers();
	}//end of reset()
	
	function setSpeed(speedSetting:Number):Void{
		//trace("model.setSpeed function called, setting = "+speedSetting);
		switch(speedSetting){
			case 0:
				this.shiftSpeed(0); 
				break;
			case 1:
				this.shiftSpeed(1);  
				break;
			case 2:
				this.shiftSpeed(2); 
				break;
			case 3:
				this.shiftSpeed(3); 
				break;
			case 4:
				this.shiftSpeed(4); 
				break;
			case 5:
				this.shiftSpeed(5);  
				break;
			case 6:
				this.shiftSpeed(6);  
				break;
			case 7:
				this.shiftSpeed(7); 
				break;
			case 8:
				this.shiftSpeed(8); 
				break;
			case 9:
				this.shiftSpeed(9);  
				break;
			case 10:
				this.shiftSpeed(10);  
				break;
		}//end of switch
	}//end of setSpeed()
	
	function shiftSpeed(index:Number):Void{ //index is 0, 1, 2, .. 9
		if(this.integrationOn){
			this.stopIntegration();
			this.maxAccel = 0;
			this.timeStep = this.stepTimes[index];
			this.nbrStepsPerFrame = this.nbrStepsPerFrame_arr[index];
			this.startIntegration();
		}else{
			this.maxAccel = 0;
			this.timeStep = this.stepTimes[index];
			this.nbrStepsPerFrame = this.nbrStepsPerFrame_arr[index];
		}
		if(index == 10){
			_quality = "LOW";  //trading screen resolution for extra speed
		}else{
			_quality = "HIGH";
		}
	}//end of shiftSpeed()
	
	
	//Following function needed to start Classic Verlet algorithm
	//Now used merely to remove center-of-mass motion
	function makeFirstStep():Void{
		this.setForcesAndAccels();
		var dt = this.timeStep;
		this.time += dt;
		if(this.wantCMMotionRemoved && !this.cmMotionRemoved){
				this.removeCMMotion();
		}
		/* //Following code needed to start classic Verlet
		for(var i:Number = 0; i < this.N; i++){
			var pos:Vector = this.bodies[i].pos;
			var posCopy:Vector = pos.duplicateVector();
			var vel:Vector = this.bodies[i].vel;
			var acc:Vector = this.bodies[i].acc;
			pos.x = pos.x + vel.x*dt + 0.5*acc.x*dt*dt;
			pos.y = pos.y + vel.y*dt + 0.5*acc.y*dt*dt;
			vel.x = vel.x + acc.x*dt;
			vel.y = vel.y + acc.y*dt;
			this.bodies[i].prePos = posCopy;
		}//end of for loop
		this.setForcesAndAccels();
		super.notifyObservers();
		*/
	}//end of makeFirstStep()
	
	
	//Classic Verlet algorithm, not self-starting, excellent position-, poor velocity-accuracy
	//Abandoned code, velocity Verlet works better
	function stepForward():Void{
		this.steppingForward = true;
		var dt:Number = this.timeStep;
		
		for(var i:Number = 0; i < this.N; i++){
			var pos:Vector = this.bodies[i].pos;
			var posCopy:Vector = pos.duplicateVector();
			var prePos:Vector = this.bodies[i].prePos;  //pos at time t minus 1
			var vel:Vector = this.bodies[i].vel;
			var acc:Vector = this.bodies[i].acc;
			this.time += dt;
			pos.x = 2*pos.x - prePos.x + acc.x*dt*dt;
			pos.y = 2*pos.y - prePos.y + acc.y*dt*dt;
			vel.x = (pos.x - prePos.x)/(2*dt);
			vel.y = (pos.y - prePos.y)/(2*dt);
			this.bodies[i].prePos = posCopy;
			//super.notifyObservers();
		}//end of for loop
		this.setForcesAndAccels();
		this.steppingForward = false;
	}//end of stepForward()
	
	//Velocity Verlet algorithm, self-starting, excellent position- and velocity-accuracy
	function stepForwardVelocityVerlet():Void{
		this.steppingForward = true;
		var dt:Number = this.timeStep;
		this.time += dt;
		for(var i:Number = 0; i < this.N; i++){
			var pos:Vector = this.bodies[i].pos;
			var vel:Vector = this.bodies[i].vel;
			var acc:Vector = this.bodies[i].acc;
			var accCopy:Vector = acc.duplicateVector();
			//this.time += dt;
			pos.x = pos.x + vel.x*dt + (0.5)*acc.x*dt*dt;
			pos.y = pos.y + vel.y*dt + (0.5)*acc.y*dt*dt;
			this.bodies[i].preAcc = accCopy;
			//super.notifyObservers();
		}//end of for loop
		this.setForcesAndAccels();
		for(var i:Number = 0; i < this.N; i++){
			var vel:Vector = this.bodies[i].vel;
			var acc:Vector = this.bodies[i].acc;
			var preAcc:Vector = this.bodies[i].preAcc;
			vel.x = vel.x + (0.5)*(acc.x + preAcc.x)*dt;
			vel.y = vel.y + (0.5)*(acc.y + preAcc.y)*dt;
		}
		this.steppingForward = false;
		if(this.collisionJustOccurred){
			var index_arr:Array = this.getIndicesOfClosestBodies();
			this.collideBodies(index_arr[0],index_arr[1]);
			super.notifyObservers();
			this.collisionJustOccurred = false;
			//trace("after 1st step, after collion " + this.traceCurrentVelCM());
		}
		
	}//end of stepForwardVelocityVerlet()
	
	//4th-order Runge-Kutta integration, works, but does not conserve energy as well as Verlet, orbits not as stable
	//Currently abandoned
	function stepForwardRK():Void{
		//Runge-Kutta algorithm, self-starting
		var dt:Number = this.timeStep;
		var N:Number = this.N;
		var k1v:Array = new Array(N);
		var k1r:Array = new Array(N);
		var k2v:Array = new Array(N);
		var k2r:Array = new Array(N);
		var k3v:Array = new Array(N);
		var k3r:Array = new Array(N);
		var k4v:Array = new Array(N);
		var k4r:Array = new Array(N);
		for(var i:Number = 0; i < N; i++){ //round 1
			this.bodies[i].posCopy = this.bodies[i].pos.duplicateVector();
			k1v[i] = this.bodies[i].acc.multiply(dt);
			k1r[i] = this.bodies[i].vel.multiply(dt);
			this.bodies[i].pos = this.bodies[i].posCopy.addTo(k1r[i].multiply(0.5));
		}//end of forloop
		this.setForcesAndAccels();
		for(var i:Number = 0; i < N; i++){ //round 2
			k2v[i] = this.bodies[i].acc.multiply(dt);
			k2r[i] = this.bodies[i].vel.addTo(k1v[i].multiply(0.5)).multiply(dt);
			this.bodies[i].pos = this.bodies[i].posCopy.addTo(k2r[i].multiply(0.5));
		}//end of for loop
		
		this.setForcesAndAccels();
		for(var i:Number = 0; i < N; i++){ //round 3
			k3v[i] = this.bodies[i].acc.multiply(dt);
			k3r[i] = this.bodies[i].vel.addTo(k2v[i].multiply(0.5)).multiply(dt);
			this.bodies[i].pos = this.bodies[i].posCopy.addTo(k3r[i]);
			//trace("k3v[" + i + "].x = " + k3v[i].x);
			//trace("k3r[" + i + "].x = " + k3r[i].x);
		}//end of for loop
		
		this.setForcesAndAccels();
		for(var i:Number = 0; i < N; i++){ //round 4
			k4v[i] = this.bodies[i].acc.multiply(dt);
			k4r[i] = this.bodies[i].vel.addTo(k3v[i]).multiply(dt);
		}//end of for loop
		for(var i:Number = 0; i < N; i++){ //update
			this.bodies[i].pos = this.bodies[i].posCopy.addTo(k1r[i].multiply(1/6));
			this.bodies[i].pos = this.bodies[i].pos.addTo(k2r[i].multiply(1/3));
			this.bodies[i].pos = this.bodies[i].pos.addTo(k3r[i].multiply(1/3));
			this.bodies[i].pos = this.bodies[i].pos.addTo(k4r[i].multiply(1/6));
			this.bodies[i].vel = this.bodies[i].vel.addTo(k1v[i].multiply(1/6));
			this.bodies[i].vel = this.bodies[i].vel.addTo(k2v[i].multiply(1/3));
			this.bodies[i].vel = this.bodies[i].vel.addTo(k3v[i].multiply(1/3));
			this.bodies[i].vel = this.bodies[i].vel.addTo(k4v[i].multiply(1/6));		
		}//end of for loop
		this.setForcesAndAccels();
		//this.showPositions();
	}//end of setForwardRK();
	
	function stepForwardNTimes(Nsteps:Number):Void{
		//var startTime:Number = getTimer();
		for(var i:Number = 1; i <= Nsteps; i++){
			//this.stepForward();
			this.stepForwardVelocityVerlet();
		}
		this.initialView.setElapsedTime(this.time);
		//trace("KE = " + this.getTotalEnergy()[0] + "     PE = " + this.getTotalEnergy()[1] + "   Tot Energy  = " + this.getTotalEnergy()[2]);
		//trace("velCM.x = " + this.getCurrentVelCM().x + "     velCM.y = " + this.getCurrentVelCM().y);
		super.notifyObservers();
		updateAfterEvent();
		//var deltaTime:Number = getTimer() - startTime;
		//trace(deltaTime);
	}
	
	function removeCMMotion():Void{
		if(!this.cmMotionRemoved){
			for(var i:Number = 0; i < this.N; i++){
				//trace("removeCMMotion called");
				var vel:Vector = this.bodies[i].vel;
				vel.x = vel.x - this.velCM.x;
				vel.y = vel.y - this.velCM.y;
			}
			this.cmMotionRemoved = true;
		}
	}
	
	function addCMMotion():Void{
		if(this.cmMotionRemoved){
			//trace("addCMMotion called");
			for(var i:Number = 0; i < this.N; i++){
				var vel:Vector = this.bodies[i].vel;
				vel.x = vel.x + this.velCM.x;
				vel.y = vel.y + this.velCM.y;
			}
		this.cmMotionRemoved = false;
		}
	}
	
	function getTotalEnergy():Array{
		var energyArray:Array = new Array(3);
		var energy:Number = 0;
		var KE:Number = 0;
		var PE:Number = 0;
		var vx:Number;
		var vy:Number;
		var vSqrd:Number;
		var body1:Body;
		var body2:Body;
		for(var i:Number = 0; i < this.N; i++){
			body1 = this.bodies[i];
			vx = body1.vel.x;
			vy = body1.vel.y;
			vSqrd = vx*vx + vy*vy;
			KE += (0.5)*body1.mass*vSqrd;
			for(var j:Number = i+1 ; j < this.N; j++){
				body2 = this.bodies[j];
				var GM1M2 = this.G*body1.mass*body2.mass;
				var delX:Number = body2.pos.x - body1.pos.x;	//x2 - x1;
				var delY:Number = body2.pos.y - body1.pos.y;	//y2 - y1;
				var dist:Number = Math.sqrt(delX*delX + delY*delY);
				PE += -GM1M2/dist;
			}//end of for j loop
		}//end of for i loop
		energy = KE + PE;
		energyArray = [KE, PE, energy];
		return energyArray;
	}//end of getTotalEnergy()
	
	//following code for debugging only
	function traceCurrentVelCM():Void{	//set velocity of center-of-mass
		var currentVelCM:Vector = new Vector(0,0);
		var M:Number = 0; //total mass
		var sumMVX:Number = 0;
		var sumMVY:Number = 0;
		for(var i:Number = 0; i < this.N; i++){
			M += this.bodies[i].mass;
			sumMVX += this.bodies[i].mass*this.bodies[i].vel.x;
			sumMVY += this.bodies[i].mass*this.bodies[i].vel.y;
		}
		currentVelCM.x = this.velCM.x + sumMVX/M;
		currentVelCM.y = this.velCM.y + sumMVY/M;
		//trace("currentVelCM.x = " + currentVelCM.x + "     currentVelCM.y = " + currentVelCM.y);
		//trace("m*v of body 0 = " + this.bodies[0].mass*this.bodies[0].vel.x);
		//trace("m*v of body 1 = " + this.bodies[1].mass*this.bodies[1].vel.x);
		//trace("this.velCM.y: "+this.velCM.y);
	}//end of getCurrentVelCM()
	
	function getInitPosOfBodyI(i:Number):Vector{
		return this.bodies[i].initPos;
	}
	function setInitPosOfBodyI(i:Number, pos:Vector):Void{
		_root.controlPanel_mc.myComboBox.selectedIndex = 0;
		//trace("model.setInitPosOfBody "+i+" called.");
		this.bodies[i].initPos = pos;
		this.bodies[i].pos = pos.duplicateVector();
		//this.initialBodies[i].initPos = pos;
		//this.initialBodies[i].pos = pos.duplicateVector();
		//this.setForcesAndAccels();
		super.notifyObservers();
	}
	function getInitVelOfBodyI(i:Number):Vector{
		return this.bodies[i].initVel;
	}
	function setInitVelOfBodyI(i:Number, vel:Vector):Void{
		_root.controlPanel_mc.myComboBox.selectedIndex = 0;
		this.bodies[i].initVel = vel;
		this.bodies[i].vel = vel.duplicateVector();
		//this.initialBodies[i].initVel = vel;
		//this.initialBodies[i].vel = vel.duplicateVector();
		this.setVelCM();  //reset velocity of center-of-mass
		//trace("this.velCM.x: " + this.velCM.x + "     this.velCM.y:" + this.velCM.y);
		super.notifyObservers();
	}
	function getPosOfBodyI(i:Number):Vector{
		return this.bodies[i].pos;
	}
	function getVelOfBodyI(i:Number):Vector{
		return this.bodies[i].vel;
	}
	function getMassOfBodyI(i:Number):Number{
		//trace("getMassOfBodyI: "+i+" called");
		return this.bodies[i].mass;
	}
	function setMassOfBodyI(i:Number, mass:Number):Void{
		_root.controlPanel_mc.myComboBox.selectedIndex = 0;
		if(this.cmMotionRemoved){
			this.addCMMotion();
			this.bodies[i].setMass(mass);
			this.setVelCM();
			this.removeCMMotion();
		}else{
			this.bodies[i].setMass(mass);
			this.setVelCM();
		}
		super.notifyObservers();
	}
	
	function setInitialBodies():Void{
		var body1 = new Body(200, new Vector(0,0), new Vector(0,-1));  //new Body(mass, pos, vel)
		var body2 = new Body(10, new Vector(142,0), new Vector(0,140)); 
		var body3 = new Body(0, new Vector(166,0), new Vector(0,74)); 
		var body4 = new Body(0, new Vector(-84,0), new Vector(0,-133)); 
		var initialBodies:Array = [body1, body2, body3, body4];
		for(var i = 0; i < this.N; i++){
			this.bodies[i] = initialBodies[i];//this.initialBodies[i].duplicateBody();
		}
	}//end of setInitialBodies;
	
	function setInitialConfig(configNbr:Number):Void{
		this.stopIntegration();
		this.resettingN = true;
		
		var body1:Body; 
		var body2:Body;
		var body3:Body; 
		var body4:Body; 
		var initialBodies:Array;
		//Config 0
		if(configNbr == 1){ //Sun and planet
			//trace("Sun and planet " + configNbr);
			body1 = new Body(200, new Vector(0,0), new Vector(0,0));//new Body(mass, pos, vel)
			body2 = new Body(10, new Vector(150,0), new Vector(0,120));
			initialBodies = new Array(body1, body2);
		}else if(configNbr == 2){ //Sun, planet, moon
			//trace("Sun, planet, moon" + configNbr);
			body1 = new Body(200, new Vector(0,0), new Vector(0,0));
			body2 = new Body(10, new Vector(160,0), new Vector(0,120));
			body3 = new Body(0, new Vector(140,0), new Vector(0,53));
			initialBodies = new Array(body1, body2, body3);
		}else if(configNbr == 3){ //Sun, planet, comet
			//trace("Sun, planet, comet" + configNbr);
			body1 = new Body(200, new Vector(0,0), new Vector(0,0));
			body2 = new Body(1, new Vector(150,0), new Vector(0,120));
			body3 = new Body(0, new Vector(-220,130), new Vector(-15,-28));
			initialBodies = new Array(body1, body2, body3);
		}else if(configNbr == 4){//Binary star, planet
			//trace("Binary star, planet" + configNbr);
			body1 = new Body(150, new Vector(-100,0), new Vector(0,-60));
			body2 = new Body(120, new Vector(100,0), new Vector(0,50));
			body3 = new Body(0, new Vector(-50,0), new Vector(0,120));
			initialBodies = new Array(body1, body2, body3);
		}else if(configNbr == 5){//Trojan asteroids
			//trace("Trojan asteroids" + configNbr);
			body1 = new Body(200, new Vector(0,0), new Vector(0,0));
			body2 = new Body(5, new Vector(150,0), new Vector(0,119));
			body3 = new Body(0, new Vector(75,-130), new Vector(103,60));
			body4 = new Body(0, new Vector(75,130), new Vector(-103,60));
			initialBodies = new Array(body1, body2, body3, body4)
		}else if(configNbr == 6){//4 star ballet
			//trace("4 Star ballet" + configNbr);
			body1 = new Body(120, new Vector(-100,100), new Vector(-50,-50));
			body2 = new Body(120, new Vector(100,100), new Vector(-50,50));
			body3 = new Body(120, new Vector(100,-100), new Vector(50,50));
			body4 = new Body(120, new Vector(-100,-100), new Vector(50,-50));
			initialBodies = new Array(body1, body2, body3, body4)
		}else if(configNbr == 7){//slingshot
			body1 = new Body(200, new Vector(1,0), new Vector(0,-1));
			body2 = new Body(10, new Vector(131,55), new Vector(-55,115));
			body3 = new Body(0, new Vector(-6,-128), new Vector(83,0));
			initialBodies = new Array(body1, body2, body3);
		}else if(configNbr == 8){//double slingshot
			body1 = new Body(200, new Vector(0,0), new Vector(0,-1));
			body2 = new Body(5, new Vector(0,-112), new Vector(134,0));
			body3 = new Body(4, new Vector(186,-5), new Vector(1,111));
			body4 = new Body(0, new Vector(70,72), new Vector(-47,63));
			initialBodies = new Array(body1, body2, body3, body4)
		}else if(configNbr == 9){//hyperbolics
			body1 = new Body(250, new Vector(-50,-25), new Vector(0,0));
			body2 = new Body(0, new Vector(300,50), new Vector(-120,0));
			body3 = new Body(0, new Vector(300,120), new Vector(-120,0));
			body4 = new Body(0, new Vector(300,190), new Vector(-120,0));
			initialBodies = new Array(body1, body2, body3, body4)
		}else if(configNbr == 10){//elipses
			body1 = new Body(250, new Vector(-200,0), new Vector(0,0));
			body2 = new Body(0, new Vector(-115,0), new Vector(0,151));
			body3 = new Body(0, new Vector(50,0), new Vector(0,60));
			body4 = new Body(0, new Vector(220,0), new Vector(0,37));
			initialBodies = new Array(body1, body2, body3, body4)
		}else if(configNbr == 11){//elipses
			body1 = new Body(60, new Vector(-115,-3), new Vector(0,-154));
			body2 = new Body(70, new Vector(102,0), new Vector(1,150));
			body3 = new Body(55, new Vector(-77,-2), new Vector(-1,42));
			body4 = new Body(62, new Vector(135,0), new Vector(-1,-52));
			initialBodies = new Array(body1, body2, body3, body4)
		}
		
		this.N = initialBodies.length;
		this.bodies = new Array(this.N);
		for(var i = 0; i < this.N; i++){
			this.bodies[i] = initialBodies[i];
		}
		//set up initial positions, velocities of bodies (4 bodies max);
		this.forces = new Array(this.N);
		for(var i = 0; i < this.N; i++){
			this.forces[i] = new Array(this.N);
			this.forces[i][i] = new Vector(0,0);
		}
		this.setForcesAndAccels();
		this.reset();
		this.setVelCM();
		super.notifyObservers();
		this.resettingN = false;
	}//end of setInitialConfig()
	
	function getRand(size:Number):Number{
		return (Math.random()*2*size - size);
	}
	//testing code
	function showPositions():Void{
		for(var i:Number = 0; i < this.N; i++){
			trace("body" + (i+1) + "    x:" + this.bodies[i].pos.x + "    y:" + this.bodies[i].pos.y);
		}
	}
	
	function showPosArray():Void{
		for(var i:Number = 0; i < this.N; i++){
			for(var s:Number = 0; s < this.bodies[i].acc_arr.length; s++){
				trace("body"+(i+1)+"  pos_arr["+s+"].x = "+this.bodies[i].pos_arr[s].x);
				//trace("body"+(i+1)+"  acc_arr["+s+"].x = "+this.bodies[i].acc_arr[s].x);
			}
		}
	}
	
	

}//end of class Model

//*******************Unused Code*****************************
/*

	function startSuperIntegration():Void{
		if(!this.integrationOn){
			this.integrationOn = true;
			this.makeFirstStep();
			this.makeNext5Steps();
			this.intervalID = setInterval(this, "superStepForwardNTimes", 20, 3);
		}
	}//end of startSuperIntegration()
	
	//for starting up super-Verlet algorithm
	function makeNext5Steps():Void{
		for(var step:Number = 1; step <= 6; step++){
			for(var i:Number = 0; i < this.N; i++){
				this.bodies[i].pos_arr[6 - step] = this.bodies[i].pos.duplicateVector();
				this.bodies[i].acc_arr[6 - step] = this.bodies[i].acc.duplicateVector();
				//trace("pos.x of body "+ (i+1) + ":   "+this.bodies[i].pos_arr[4 - step].x);
				//trace("acc.x of body "+ (i+1) + ":   "+this.bodies[i].acc_arr[4 - step].x);
			}
			this.stepForward();
		}
		this.notifyObservers();
	}
	
	function superStepForward():Void{  //stepForward with super-Verlet algorithm
		var dt:Number = this.timeStep;
		for(var i:Number = 0; i < this.N; i++){
			var newPos:Vector = new Vector(0,0);
			var posCopy:Vector = this.bodies[i].pos.duplicateVector();
			var prePos:Vector = this.bodies[i].prePos;
			var vel:Vector = this.bodies[i].vel;
			var acc:Vector = this.bodies[i].acc;
			var bod:Body = this.bodies[i];
			//Classic Verlet
			newPos.x = 2*bod.pos_arr[0].x - bod.pos_arr[1].x + bod.acc.x*dt*dt;
			newPos.y = 2*bod.pos_arr[0].y - bod.pos_arr[1].y + bod.acc.y*dt*dt;
			
			//5th-order symmetric multistep
			//newPos.x = bod.pos_arr[0].x + bod.pos_arr[2].x - bod.pos_arr[3].x + (1.25*acc.x + 0.5*bod.acc_arr[1].x + 1.25*bod.acc_arr[2].x)*dt*dt;
			//newPos.y = bod.pos_arr[0].y + bod.pos_arr[2].y - bod.pos_arr[3].y + (1.25*acc.y + 0.5*bod.acc_arr[1].y + 1.25*bod.acc_arr[2].y)*dt*dt;
			
			//newPos.x = 3*bod.pos_arr[0].x - 3*bod.pos_arr[1].x + bod.pos_arr[2].x + (acc.x - bod.acc_arr[1].x)*dt*dt;
			//newPos.y = 3*bod.pos_arr[0].y - 3*bod.pos_arr[1].y + bod.pos_arr[2].y + (acc.y - bod.acc_arr[1].y)*dt*dt;
			
			//newPos.x = 4*bod.pos_arr[0].x - 6*bod.pos_arr[1].x + 4*bod.pos_arr[2].x - bod.pos_arr[3].x + (acc.x - 2*bod.acc_arr[1].x + bod.acc_arr[2].x)*dt*dt;
			//newPos.y = 4*bod.pos_arr[0].y - 6*bod.pos_arr[1].y + 4*bod.pos_arr[2].y - bod.pos_arr[3].y + (acc.y - 2*bod.acc_arr[1].y + bod.acc_arr[2].y)*dt*dt;
			
			//7th-order symmetric multi-step
			//newPos.x = bod.pos_arr[0].x + bod.pos_arr[4].x - bod.pos_arr[5].x + (1/48)*(67*(acc.x + bod.acc_arr[4].x) - 8*(bod.acc_arr[1].x + bod.acc_arr[3].x) + 122*bod.acc_arr[2].x)*dt*dt;
			//newPos.y = bod.pos_arr[0].y + bod.pos_arr[4].y - bod.pos_arr[5].y + (1/48)*(67*(acc.y + bod.acc_arr[4].y) - 8*(bod.acc_arr[1].y + bod.acc_arr[3].y) + 122*bod.acc_arr[2].y)*dt*dt;
			
			vel.x = (newPos.x - prePos.x)/(2*dt);
			vel.y = (newPos.y - prePos.y)/(2*dt);
			bod.prePos = posCopy;
			this.bodies[i].pos = newPos;
			
			bod.pos_arr.unshift(newPos);	//shove new position into front of position array, all others get shifted downstream
			bod.pos_arr.splice(-1, 1);  //delete last element in position array
			//trace("pos.x of body "+ (i+1) + ":   "+this.bodies[i].pos_arr[0].x);
		}//end of for loop

		this.setForcesAndAccels();

		for(var i:Number = 0; i < this.N; i++){
			var bod:Body = this.bodies[i];
			var newAcc:Vector = bod.acc.duplicateVector();
			bod.acc_arr.unshift(newAcc);	
			bod.acc_arr.splice(-1, 1);
			//trace("acc.x of body "+ (i+1) + ":   "+this.bodies[i].acc_arr[0].x);
		}
	}//end of superStepForward()
	
	function superStepForwardNTimes(N:Number):Void{
		for(var i:Number = 1; i <= N; i++){
			this.superStepForward();
		}
		//trace("KE = " + this.getTotalEnergy()[0] + "     PE = " + this.getTotalEnergy()[1] + "   Tot Energy  = " + this.getTotalEnergy()[2]);
		super.notifyObservers();
	}//end of superStepForwardNTimes
	
*/