/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.JToolTip;

/**
 * Shared instance Tooltip to saving instances.
 * @author iiley
 */
class org.aswing.overflow.JSharedToolTip extends JToolTip {
	
	private static var sharedInstance:JSharedToolTip;
	
	private var targetedComponent:Component;
	
	public function JSharedToolTip() {
		super();
		setName("JSharedToolTip");
	}
	
	/**
	 * Returns the shared JSharedToolTip instance.
	 * @return a singlton shared instance.
	 */
	public static function getSharedInstance():JSharedToolTip{
		if(sharedInstance == undefined){
			sharedInstance = new JSharedToolTip();
		}
		return sharedInstance;
	}
	
    /**
     * Registers a component for tooltip management.
     *
     * @param component  a <code>JComponent</code> object to add
     */
	public function registerComponent(c:Component):Void{
		c.removeEventListener(compListener);
		c.addEventListener(compListener);
		if(getTargetComponent() == c){
			setTipText(c.getToolTipText());
		}
	}
	

    /**
     * Removes a component from tooltip control.
     *
     * @param component  a <code>JComponent</code> object to remove
     */
	public function unregisterComponent(c:Component):Void{
		c.removeEventListener(compListener);
		if(getTargetComponent() == c){
			disposeToolTip();
			targetedComponent = null;
		}
	}
	
	/**
	 * Registers a component that the tooltip describes. 
	 * The component c may be null and will have no effect. 
	 * <p>
	 * This method is overrided just to call registerComponent of this class.
	 * @param the JComponent being described
	 * @see #registerComponent()
	 */
	public function setTargetComponent(c:Component):Void{
		registerComponent(c);
	}
	
	/** 
	 * Returns the lastest targeted component. 
	 * @return the lastest targeted component. 
	 */
	public function getTargetComponent():Component{
		return targetedComponent;
	}
	
	//-------------
	private function __compRollOver(source:Component):Void{
		if(source.getToolTipText() != null && isWaitThenPopupEnabled()){
			targetedComponent = source;
			setTipText(targetedComponent.getToolTipText());
			startWaitToPopup();
		}
	}
	
	private function __compRollOut(source:Component):Void{
		if(source == targetedComponent && isWaitThenPopupEnabled()){
			disposeToolTip();
			targetedComponent = null;
		}
	}	
}