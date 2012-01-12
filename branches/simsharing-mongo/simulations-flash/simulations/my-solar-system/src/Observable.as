class Observable{
	private var observers:Array;
	
	function Observable(){
		observers = new Array();
	}//end of constructor
	
	public function addObserver(obj:Object):Boolean{
		for(var i = 0; i < this.observers.length; i++){
			if(observers[i] == obj){
				return false;
			}
		}
		this.observers.push(obj);
		return true;
	}//end of addObserver
	
	public function removeObserver(obj:Object):Boolean{
		for(var i = 0; i < this.observers.length; i++){
			if(this.observers[i] == obj){
				this.observers.splice(i,1);
				return true;
			}
		}
		return false;
	}//end of removeObserver
	
	public function notifyObservers():Void{
		//notify observers in same order that they subscribed
		//all observers must have an update() function
		for(var i = 0; i < this.observers.length; i++){
			this.observers[i].update();
		}
	}//end of notifyObservers
	
	public function clearAllObservers():Void{
		this.observers = new Array();
	}
}//end of class Observable