class Vector{
	var x:Number;
	var y:Number;
	
	function Vector(x:Number, y:Number){
		this.x = x;
		this.y = y;
	}//end of constructor
	
	function setComponents(x:Number, y:Number):Void{
		this.x = x;
		this.y = y;
	}//end of constructor
	
	function duplicateVector():Vector{
		var vectorCopy:Vector = new Vector(this.x, this.y);
		return vectorCopy;
	}
	
	function getMagnitude():Number{
		var magnitude:Number = Math.sqrt(this.x*this.x + this.y*this.y);
		return magnitude;
	}
	
	function addTo(vector:Vector):Vector{
		var sum:Vector = new Vector(this.x + vector.x, this.y + vector.y);
		return sum;
	}
	
	function multiply(factor:Number):Vector{
		var product:Vector = new Vector(this.x*factor, this.y*factor);
		return product;
	}
}//end of class