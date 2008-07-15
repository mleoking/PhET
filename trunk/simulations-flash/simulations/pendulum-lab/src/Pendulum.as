
class Pendulum{
	private var labelNbr;			//1 for 1st pendulum, 2 for second pendulum, etc.
	private var mass:Number;		//mass of pendulum
	private var mPowThird;			//mass^1/3
	//private var drag;				// drag is global variable: alpha_drag = (drag/mass^1/3)*omega
	private var length:Number;		//length of string from pivot to center of mass
	private var theta:Number;		//angle of pendulum from vertical, in radians, +CCW, -CW
	private var omega:Number;		//angular velocity in rad/s
	private var alpha:Number;		//angular acceleration in rad/s^2
	private var speed:Number;		//speed of mass in m/s
	private var tanVel:Number;		//tangential component of velocity in m/s
	private var h:Number;			//height of mass above minimum in m
	var KE:Number;			//kinetic energy in joules
	var PE:Number;			//potential energy in joules
	var thermalE:Number;	//thermal energy in joules
	var E:Number;			//total energy E = KE + PE
	private var t:Number;			//time in seconds
	private var dt:Number;			//time step in seconds
	private var updateCount:Number;
	private var timeFactor:Number;	//timefactor = 10 means make time go 10 time slower
	//private var g:Number;			//global variable: acceleration due to gravity in m/s^2
	var view:Object;				//view of pendulum on main stage
	var controller:Object;			//view of controlPanel on main stage
	var energyGraph:Object;			//view of energyGraph
	var lastTime:Number;
	var intervalID:Number;
	var period:Number;
	private var gettingPeriod:Boolean;
	private var roughStartTime:Number;		//starting time to nearest millisecond
	private var startPeriodTime:Number;		//starting time interpellated to high precision
	private var stopPeriodTime:Number;		//stop time interpellated to high precision
	private var tripCount:Number;			//photogate timer must be tripped 3 times in succession to get period
	
	function Pendulum(mass:Number, length:Number, labelNbr:Number){
		this.labelNbr = labelNbr;
		this.mass = mass;
		this.mPowThird = Math.pow(this.mass, 1/3);
		this.energyGraph = new EnergyGraph(this);
		//_global.drag = 0.0;
		this.gettingPeriod = false;
		this.length = length;
		//this.g = 9.81;
		this.theta = 0;
		this.dt = _global.r*0.02;		//in seconds 
		
	}//end of constructor
	
	
	function setAngleInDeg(newAngleInDeg:Number):Void{
		//trace("setAngleInDeg called for "+this.labelNbr);
		this.theta = newAngleInDeg*Math.PI/180;
		this.omega = 0;
		this.alpha = -(_global.g/this.length)*Math.sin(this.theta) - (_global.drag/this.mPowThird)*this.omega;
		this.h = this.length*(1-Math.cos(this.theta));
		this.E = this.mass*_global.g*this.h;		//initially energy is all PE
		this.updateEnergy();
		this.updateView();
		this.updateGraph();
	}
	
	function getAngleInDeg():Number{
		return this.theta*180/Math.PI;
	}
	
	function startMotion():Void{
		this.t = 0;
		this.updateCount = 0;
		this.lastTime = getTimer();
		this.intervalID = setInterval(this, "evolve", 1); //dt/1000);
	}//end of startMotion()
	
	function stopMotion():Void{
		clearInterval(this.intervalID);
	}//end of endMotion()
	
	
	function evolve():Void{			//evolve forward in time one time step, velocity Verlet 
		var realTime:Number = getTimer();
		var timeSinceLastStep = realTime - this.lastTime;
		this.lastTime = realTime;
		
		this.dt = Math.min(0.05, _global.r*timeSinceLastStep/1000);
		//trace("this.dt: "+this.dt);
		_root.dtLabel.text = timeSinceLastStep;
		var oldTheta = this.theta;
		this.theta = this.theta + this.omega*dt + (0.5)*this.alpha*dt*dt;
		var oldAlpha:Number = this.alpha;
		//this.alpha = -(9.8/this.length)*Math.sin(this.theta) - (0/this.mPowThird)*this.omega;
		this.alpha = -(_global.g/this.length)*Math.sin(this.theta) - (_global.drag/this.mPowThird)*this.omega;
		//var ratio:Number = ((_global.drag/this.mPowThird)*this.omega)/((_global.g/this.length)*Math.sin(this.theta));
		//trace("drag to weight"+ratio);
		this.omega = this.omega + (0.5)*(this.alpha + oldAlpha)*dt;
		this.updateEnergy();
		var oldTime = this.t;
		this.t += this.dt;
		this.updateCount += 1;
		if(this.gettingPeriod){
			if(tripCount == 1 || tripCount == 2){
				this.updatePhotogate();
			}
			if(Math.abs(this.theta) > 0.0001 && Math.abs(this.theta)/this.theta != Math.abs(oldTheta)/oldTheta){
				//trace("pendulum " + this.labelNbr + " vertical passed at time " + this.t);
				this.tripCount += 1;
				if(tripCount == 1){
					//trace("period started..");
					this.view.startPeriodArc();
					this.roughStartTime = this.t;
					//var interpTime:Number = oldTime + (oldTheta/(oldTheta - this.theta))*(this.t - oldTime);
					//trace("interpolated time is " + interpTime);
					this.startPeriodTime = oldTime + (oldTheta/(oldTheta - this.theta))*(this.t - oldTime);
				}else if (tripCount == 3){
					//this.stopPeriodTime = this.t;
					this.stopPeriodTime = oldTime + (oldTheta/(oldTheta - this.theta))*(this.t - oldTime);
					this.period = this.stopPeriodTime - this.startPeriodTime;
					this.gettingPeriod = false;
					this.reportPeriod();
				}
			}
		}//if(gettingPeriod)
		//redraw screen every 4 time steps, needed for precision of numerical integration
		if(updateCount%2 == 0){
			this.updateView();
			this.updateGraph();
		}
		//trace("t: "+this.t+"    theta: "+this.theta*180/Math.PI);
	}
	
	function updateEnergy():Void{
		this.h = this.length*(1-Math.cos(this.theta));
		this.PE = this.mass*_global.g*this.h;
		this.tanVel = this.length*this.omega;
		this.KE = (0.5)*this.mass*this.tanVel*this.tanVel;
		this.thermalE = this.E - (this.KE + this.PE);
	}
	
	function setLength(lengthInMeters:Number):Void{
		var thermalENow:Number = this.thermalE;
		var oldLength:Number = this.length;
		this.length = lengthInMeters;
		this.omega =  this.omega*oldLength/this.length;	//conserve angular momentum
		//recompute Energy
		this.h = this.length*(1-Math.cos(this.theta));
		this.PE = this.mass*_global.g*this.h;
		this.tanVel = this.length*this.omega;
		this.KE = (0.5)*this.mass*this.tanVel*this.tanVel;
		this.E = this.KE + this.PE + thermalENow;
		this.view.resetLength();
	}
	
	function setMass(massInKg:Number):Void{
		this.mass = massInKg;
		this.mPowThird = Math.pow(this.mass, 1/3);
		
		//recompute Energy
		this.PE = this.mass*_global.g*this.h;
		this.tanVel = this.length*this.omega;
		this.KE = (0.5)*this.mass*this.tanVel*this.tanVel;
		this.E = this.KE + this.PE + this.thermalE;
		this.view.resetMass();
	}
	
	function setDrag(drag:Number):Void{
		_global.drag = drag;
	}
	
	function getLength():Number{
		return this.length;
	}
	
	function getLabelNbr():Number{
		return this.labelNbr;
	}
	
	function getMass():Number{
		return this.mass;
	}
	
	function startPhotogate():Void{
		//trace("pendulum "+ this.labelNbr +" started");
		this.gettingPeriod = true;
		this.tripCount = 0;
		//this.view.startPeriodArc();
	}
	
	function reportPeriod():Void{
		this.controller.updatePeriod();
		this.view.fadeOutPeriodArc();
	}
	
	function updatePhotogate():Void{
		var gateTime:Number = this.t - this.roughStartTime;
		this.controller.updatePhotogate(gateTime);
	}
	
	function registerView(aView:Object):Void{
		this.view = aView;
	}
	
	function registerController(aController:Object):Void{
		//trace("registerController called on pendulum "+this.labelNbr);
		this.controller = aController;
	}
	
	function registerGraphView(graph:Object):Void{
		this.energyGraph = graph;
	}
	
	function updateGraph():Void{
		this.energyGraph.update();
	}
	
	function updateView():Void{
		this.view.update();
	}
	
}//end of class
	
	
	