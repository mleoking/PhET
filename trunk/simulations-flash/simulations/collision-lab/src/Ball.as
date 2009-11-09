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
		}//end of constructor
		
		public function getMomentum():TwoVector{
			this.momentum.setXY(this.mass*this.velocity.getX(), this.mass*this.velocity.getY());
			return this.momentum;
		}
		
	}//end of class
	
}//end of package