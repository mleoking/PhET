//ball object in the Model for Collision Lab

package{
	//import statements go here
	
	public class Ball {
		private var mass:Number;
		private var radius:Number;
		var position:TwoVector;
		var velocity:TwoVector;
		private var momentum:TwoVector;
		
		public function Ball(mass:Number, position:TwoVector, velocity:TwoVector ){
			this.mass = mass;
			//2D mass = rho*pi*r^2, so r = C*mass^1/2
			//3D mass = rho*(4*pi/3)*r^3, so r = C*mass^1/3
			//this.radius = 0.15*Math.sqrt(this.mass);   //radius in meters
			this.radius = 0.15*Math.pow(this.mass, 1/3)
			this.position = position;
			this.velocity = velocity;
			this.momentum = new TwoVector(this.mass*this.velocity.getX(),this.mass*this.velocity.getY());
		}//end of constructor
		
		//reset position to position at previous timestep
		public function backupOneStep():void{
			this.position.setXY(this.position.getXLast(), this.position.getYLast());
		}
		
		//following to used to reset existing ball object to initial configuration in Model
		public function setBall(mass:Number, position:TwoVector, velocity:TwoVector){
			this.mass = mass;
			this.radius = 0.15*Math.pow(this.mass, 1/3)
			this.position = position;
			this.velocity = velocity;
			this.momentum = new TwoVector(this.mass*this.velocity.getX(),this.mass*this.velocity.getY());
		}
		
		public function setMass(mass:Number):void{
			this.mass = mass;
			this.radius = 0.15*Math.pow(this.mass, 1/3)
			//this.radius = 0.15*Math.sqrt(this.mass);
		}
		
		public function getMass():Number{
			return this.mass;
		}
		
		public function getRadius():Number{
			return this.radius;
		}
		
		public function getMomentum():TwoVector{
			this.momentum.setXY(this.mass*this.velocity.getX(), this.mass*this.velocity.getY());
			return this.momentum;
		}
		
		public function getKE():Number{
			var speed:Number = this.velocity.getLength();
			var KE:Number = 0.5*this.mass*speed*speed;
			return KE;
		}
		
		public function reverseVelocity():void{
			this.velocity.flipVector();
		}
		
	}//end of class
	
}//end of package