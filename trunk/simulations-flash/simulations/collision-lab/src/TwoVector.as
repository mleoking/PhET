//TwoVector.  Simple 2-component real vector
//AS3 has built-in Vector Class, so named my class differently

package{
	
	public class TwoVector{
		private var xComponent:Number;
		private var yComponent:Number;
		private var length:Number;
		private var angle:Number;  //angle in degrees, to match rotation property
		
		public function TwoVector(xComp:Number, yComp:Number){
			this.xComponent = xComp;
			this.yComponent = yComp;
			this.setLength();
			this.setAngle();
		}//end of constructor
		
		private function setLength():void{
			this.length = Math.sqrt(xComponent*xComponent + yComponent*yComponent);
		}
		
		private function setAngle():void{
			this.angle = (180/Math.PI)*Math.atan2(yComponent, xComponent);
		}
		
		public function getLength():Number{
			return this.length;
		}
		
		public function getAngle():Number{
			return this.angle;  //angle in degrees
		}
		
		public function setX(x:Number):void{
			this.xComponent = x;
			this.setLength();
			this.setAngle();
		}
		
		public function setY(y:Number):void{
			this.yComponent = y;
			this.setLength();
			this.setAngle();
		}
		
		public function setXY(x:Number, y:Number):void{
			this.xComponent = x;
			this.yComponent = y;
			this.setLength();
			this.setAngle();
		}
		
		public function getX():Number{
			return this.xComponent;
		}
		
		public function getY():Number{
			return this.yComponent;
		}
		

	}//end of class
	
}//end of package