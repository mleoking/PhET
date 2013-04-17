//Observer interface has a single update function with two parameter:
//obs:Observable is reference to the changed Subject=the Observable class
//infoObj:Object is an optional object containing the change information.  
//infoObj is used if the Subject pushes the change
interface Observer {
	public function update(model:ChargeGroup):Void;
}