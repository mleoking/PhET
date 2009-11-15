//ball object in the Model for Collision Lab

package{
	//import statements go here
	
	public class Ball {
		var mass:Number;
		var radius:Number;
		var position:TwoVector;
		var velocity:TwoVector;
		private var momentum:TwoVector;
		
		public function Ball(mass:Number, position:TwoVector, velocity:TwoVector ){
			this.mass = mass;
			this.radius = 0.15;   //radius in meters 
			this.position = position;
			this.velocity = velocity;
			this.momentum = new TwoVector(this.mass*this.velocity.getX(),this.mass*this.velocity.getY());
		}//end of constructor
		
		//reset position to position at previous timestep
		public function backupOneStep():void{
			this.position.setXY(this.position.getXLast(), this.position.getYLast());
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