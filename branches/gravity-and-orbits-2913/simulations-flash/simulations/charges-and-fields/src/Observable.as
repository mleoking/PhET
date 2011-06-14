//Taken from "Essential ActionScript 2.0" 1st Ed. by Colin Moock, p.356

class Observable{
	private var changed:Boolean = false;
	private var observers:Array;
	
	//constructor
	function Observable(){
		observers = new Array();
	}//end of constructor
	
	public function addObserver(obs:Observer):Boolean{
		//don't add observers more than once
		for (var i:Number = 0; i < observers.length; i++){
			if(this.observers[i] == obs){
				return false;  
			}
		}
		this.observers.push(obs);
		//trace("observer count: "+this.countObservers());
		return true;
	}//end of addObserver(obs)
	
	public function removeObserver(obs:Observer):Boolean{
		var len:Number = this.observers.length;
		for (var i = 0; i < len; i++){
			if(observers[i]==obs){
				this.observers.splice(i,1);
				//trace("observer count: "+this.countObservers());
				return true;
			}
		}
		return false;
	}//end of removeObserver(obs)
	
	public function notifyObservers(infoObj:Object):Void{
		if(infoObj == undefined){
			infoObj = null;
			//trace("infoObj undefined");
		}
		if(!this.changed){
			trace("notifyObservers() called, but no change occured.");
			return;
		}
		//make local copy of observer array, so array does not change while we are processing it
		var observersSnapshot:Array = this.observers.slice(0);
		this.clearChanged();
		//Invoke update on all observers.  Count backwards because it is quicker, says Moock (Really? Is it simply because initialization of counter is set only once? -MD)
		
		for (var i:Number = observersSnapshot.length-1; i >=0; i--){
			//every observer must implement interface Observer and have an update() method
			observersSnapshot[i].update(this, infoObj);
		}
		
	}//end of notifyObservers(infoObj)
	
	public function clearObservers():Void{
		this.observers = new Array();
	}
	
	//Why do we need this method?
	public function setChanged():Void{
		this.changed =  true;
	}
	
	private function clearChanged():Void{
		this.changed = false;
	}
	
	public function hasChanged():Boolean{
		return changed;
	}
	
	public function countObservers():Number{
		return this.observers.length;
	}
	
}//end of class Observable