//class describes a single electrical charge
class Charge{
	
	public var q: Number;			//value of charge
	//public var tagNbr:Number;		//unique identifying number
	public var magnitude:Number;	//magnitude of charge
	public var sign:Number;			//sign is +1(positive) or -1(negative)
	public var x:Number;					//x-position
	public var y:Number;					//y-position
	private var myChargeGroup:ChargeGroup;
	
	//constructor does not return anything
	function Charge(q:Number,myChargeGroup:ChargeGroup,x:Number,y:Number){
		this.myChargeGroup = myChargeGroup;
		this.setCharge(q);
		this.x = x;		
		this.y = y;
	}
	
	function setCharge(q):Void{
		this.q = q;
		this.magnitude = Math.abs(q);
		if(q == 0){
			this.sign = 0;
		}else{
			this.sign = q/this.magnitude;
		}
	}
	

	function changeSign():Void{
		this.q = -1*this.q;
		this.sign = -1*this.sign;
	}
		
	//accessing a property directly is faster than accessing thru a method
	//may want to make x,y public properties 
	function setPosition(x:Number,y:Number):Void{
		this.x = x;
		this.y = y;
		this.myChargeGroup.setChanged();
		this.myChargeGroup.notifyObservers();
	}
	
	function getX():Number{
		return this.x;
	}
	
	function getY():Number{
		return this.y;
	}
	
	
}//end of class Charge