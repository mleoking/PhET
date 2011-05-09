/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.FocusTraversalPolicy;
import org.aswing.JWindow;

/**
 * A FocusTraversalPolicy that determines traversal order based on the order
 * of child Components in a Container. From a particular focus cycle root, the
 * policy makes a pre-order traversal of the Component hierarchy, and traverses
 * a Container's children according to the ordering of the array returned by
 * <code>Container.getComponent(i:Number)</code>. Portions of the hierarchy that are
 * not visible and displayable will not be searched.
 * <p>
 * By default, ContainerOrderFocusTraversalPolicy implicitly transfers focus
 * down-cycle. That is, during normal forward focus traversal, the Component
 * traversed after a focus cycle root will be the focus-cycle-root's default
 * Component to focus. This behavior can be disabled using the
 * <code>setImplicitDownCycleTraversal</code> method.
 * <p>
 * By default, methods of this class with return a Component only if it is
 * visible, displayable, enabled, and focusable. Subclasses can modify this
 * behavior by overriding the <code>accept</code> method.
 * <p>
 * When searching for first/last/next/previous Component,
 * if a focus traversal policy provider is encountered, its focus traversal
 * policy is used to perform the search operation.
 * 
 * @author iiley 
 */
class org.aswing.ContainerOrderFocusTraversalPolicy implements FocusTraversalPolicy {
	
	private static var found:Object;
	private var implicitDownCycleTraversal:Boolean;
	
	public function ContainerOrderFocusTraversalPolicy(){
		if(found == undefined){
			found = {value:false};
		}
		implicitDownCycleTraversal = true;
	}

    /**
     * Returns the Component that should receive the focus after aComponent.
     * aContainer must be a focus cycle root of aComponent or a focus traversal policy provider.
     * <p>
     * ContainerOrderFocusTraversalPolicy implicitly transfers
     * focus down-cycle. That is, during normal forward focus traversal, the
     * Component traversed after a focus cycle root will be the focus-cycle-
     * root's default Component to focus.
     * <p>
     * If aContainer is {@link org.aswing.Container#isFocusTraversalPolicyProvider}focus
     * traversal policy provider</a>, the focus is always transferred down-cycle.
     *
     * @param aContainer a focus cycle root of aComponent or a focus traversal policy provider
     * @param aComponent a (possibly indirect) child of aContainer, or
     *        aContainer itself
     * @return the Component that should receive the focus after aComponent, or
     *         null if no suitable Component can be found
     * @throws Error if aContainer is not a focus cycle
     *         root of aComponent or focus traversal policy provider, or if either aContainer or
     *         aComponent is null
     */	
	public function getComponentAfter(aContainer : Container, aComponent : Component) : Component {
		checkContainerComponent(aContainer, aComponent);
		found.value = false;
		var retval:Component = findComponentAfter(aContainer, aComponent, found);
		if (retval != null){
			//trace("It is " + retval);
			return retval;
		}else if (found.value){
			//trace("Didn't find next component in aContainer - falling back to the first");
			retval = getFirstComponent(aContainer);
			//trace("It is " + retval);
			return retval;
		}else{
			//trace("//After component is null");
			return null;
		}
	}

	public function getComponentBefore(aContainer : Container, aComponent : Component) : Component {
		checkContainerComponent(aContainer, aComponent);
		found.value = false;
		var retval:Component = findComponentBefore(aContainer, aComponent, found);
		if (retval != null){
			return retval;
		}else if (found.value){
			//"Didn't find next component in aContainer - falling back to the last
			return getLastComponent(aContainer);
		}else{
			//After component is null
			return null;
		}
		return null;
	}
	
    /**
     * Returns the first Component in the traversal cycle. This method is used
     * to determine the next Component to focus when traversal wraps in the
     * forward direction.
     *
     * @param aContainer the focus cycle root or focus traversal policy provider whose first
     *        Component is to be returned
     * @return the first Component in the traversal cycle of aContainer,
     *         or null if no suitable Component can be found
     * @throws Error if aContainer is null
     */
	public function getFirstComponent(aContainer : Container) : Component {
		if (aContainer == null){
			trace("aContainer cannot be null");
			throw new Error("aContainer cannot be null");
		}
		if (! (aContainer.isVisible() && aContainer.isDisplayable())){
			return null;
		}
		if (accept(aContainer)){
			return aContainer;
		}
		var n:Number = aContainer.getComponentCount();
		for (var i:Number = 0; i < n; i++){
			var comp:Component = aContainer.getComponent(i);
			if ((comp instanceof Container) && (! (Container(comp)).isFocusCycleRoot())){
				var retval:Component = null;
				var cont:Container = Container(comp);
				if (cont.isFocusTraversalPolicyProvider()){
					var policy:FocusTraversalPolicy = cont.getFocusTraversalPolicy();
					retval = policy.getDefaultComponent(cont);
				}else{
					retval = getFirstComponent(Container(comp));
				}
				if (retval != null){
					return retval;
				}
			}else if (accept(comp)){
				return comp;
			}
		}
		return null;
	}

    /**
     * Returns the last Component in the traversal cycle. This method is used
     * to determine the next Component to focus when traversal wraps in the
     * reverse direction.
     *
     * @param aContainer the focus cycle root or focus traversal policy provider whose last
     *        Component is to be returned
     * @return the last Component in the traversal cycle of aContainer,
     *         or null if no suitable Component can be found
     * @throws Error if aContainer is null
     */
	public function getLastComponent(aContainer : Container) : Component {
		if (aContainer == null){
			trace("aContainer cannot be null");
			throw new Error("aContainer cannot be null");
		}
		if (! (aContainer.isVisible() && aContainer.isDisplayable())){
			return null;
		}
		for (var i:Number = (aContainer.getComponentCount() - 1); i >= 0; i--){
			var comp:Component = aContainer.getComponent(i);
			if ((comp instanceof Container) && (! (Container(comp)).isFocusCycleRoot())){
				var retval:Component = null;
				var cont:Container = Container(comp);
				if (cont.isFocusTraversalPolicyProvider()){
					var policy:FocusTraversalPolicy = cont.getFocusTraversalPolicy();
					retval = policy.getLastComponent(cont);
				}else{
					retval = getLastComponent(Container(comp));
				}
				if (retval != null){
					return retval;
				}
			}else if (accept(comp)){
				return comp;
			}
		}
		if (accept(aContainer)){
			return aContainer;
		}
		return null;
	}


    /**
     * Returns the default Component to focus. This Component will be the first
     * to receive focus when traversing down into a new focus traversal cycle
     * rooted at aContainer. The default implementation of this method
     * returns the same Component as <code>getFirstComponent</code>.
     *
     * @param aContainer the focus cycle root or focus traversal policy provider whose default
     *        Component is to be returned
     * @return the default Component in the traversal cycle of aContainer,
     *         or null if no suitable Component can be found
     * @see #getFirstComponent()
     * @throws Error if aContainer is null
     */
	public function getDefaultComponent(aContainer : Container) : Component {
		return getFirstComponent(aContainer);
	}

	public function getInitialComponent(window : JWindow) : Component {
		var def:Component = getDefaultComponent(window);
		if ((def == null) && window.isFocusable()){
			def = window;
		}
		return def;
	}
	
    /**
     * Sets whether this ContainerOrderFocusTraversalPolicy transfers focus
     * down-cycle implicitly. If <code>true</code>, during normal forward focus
     * traversal, the Component traversed after a focus cycle root will be the
     * focus-cycle-root's default Component to focus. If <code>false</code>,
     * the next Component in the focus traversal cycle rooted at the specified
     * focus cycle root will be traversed instead. The default value for this
     * property is <code>true</code>.
     *
     * @param implicitDownCycleTraversal whether this
     *        ContainerOrderFocusTraversalPolicy transfers focus down-cycle
     *        implicitly
     * @see #getImplicitDownCycleTraversal()
     * @see #getFirstComponent()
     */	
	public function setImplicitDownCycleTraversal(implicitDownCycleTraversal:Boolean):Void{
		this.implicitDownCycleTraversal = implicitDownCycleTraversal;
	}

    /**
     * Returns whether this ContainerOrderFocusTraversalPolicy transfers focus
     * down-cycle implicitly. If <code>true</code>, during normal forward focus
     * traversal, the Component traversed after a focus cycle root will be the
     * focus-cycle-root's default Component to focus. If <code>false</code>,
     * the next Component in the focus traversal cycle rooted at the specified
     * focus cycle root will be traversed instead.
     *
     * @return whether this ContainerOrderFocusTraversalPolicy transfers focus
     *         down-cycle implicitly
     * @see #setImplicitDownCycleTraversal()
     * @see #getFirstComponent()
     */	
	public function getImplicitDownCycleTraversal():Boolean{
		return implicitDownCycleTraversal;
	}	

	//--------------------------------------------------------------------
	
	
	private function findComponentAfter(aContainer:Container, aComponent:Component, found:Object):Component{
		if (!(aContainer.isVisible() && aContainer.isDisplayable())){
			return null;
		}
		if (found.value){
			if (accept(aContainer)){
				return aContainer;
			}
		} else if (aContainer == aComponent){
			found.value = true;
		}
		var n:Number = aContainer.getComponentCount();
		for (var i:Number = 0; i < n; i++) {
			var comp:Component = aContainer.getComponent(i);
			if ((comp instanceof Container) && (!(Container(comp)).isFocusCycleRoot())){
				var retval:Component = null;
				if (Container(comp).isFocusTraversalPolicyProvider()){
					var cont:Container = Container(comp);
					var policy:FocusTraversalPolicy = cont.getFocusTraversalPolicy();
					if (found.value){
						retval = policy.getDefaultComponent(cont);
					}else{
						found.value = cont.isAncestorOf(aComponent);
						if (found.value){
							if (aComponent == policy.getLastComponent(cont)){
								retval = null;
							}else{
								retval = policy.getComponentAfter(cont, aComponent);
							}
						}
					}
				}else{
					retval = findComponentAfter(Container(comp), aComponent, found);
				}
				if (retval != null){
					return retval;
				}
			}else if (found.value){
				if (accept(comp)){
					return comp;
				}
			}else if (comp == aComponent){
				found.value = true;
			}
			if (found.value 
				&& getImplicitDownCycleTraversal() 
				&& (comp instanceof Container) 
				&& (Container(comp)).isFocusCycleRoot())
			{
				var cont:Container = Container(comp);
				var retval:Component = cont.getFocusTraversalPolicy().getDefaultComponent(cont);
				if (retval != null){
					return retval;
				}
			}
		}
		return null;
	}
	
	private function findComponentBefore(aContainer:Container, aComponent:Component, found:Object):Component{
		if (! (aContainer.isVisible() && aContainer.isDisplayable())){
			return null;
		}
		for (var i:Number = (aContainer.getComponentCount() - 1); i >= 0; i--){
			var comp:Component = aContainer.getComponent(i);
			if (comp == aComponent){
				found.value = true;
			} else if ((comp instanceof Container) && (!(Container(comp)).isFocusCycleRoot())){
				var retval:Component = null;
				if ((Container(comp)).isFocusTraversalPolicyProvider()){
					var cont:Container = Container(comp);
					var policy:FocusTraversalPolicy = cont.getFocusTraversalPolicy();
					if (found.value){
						retval = policy.getLastComponent(cont);
					}else{
						found.value = cont.isAncestorOf(aComponent);
						if (found.value){
							if (aComponent == policy.getFirstComponent(cont)){
								retval = null;
							}else{
								retval = policy.getComponentBefore(cont, aComponent);
							}
						}
					}
				}else{
					retval = findComponentBefore(Container(comp), aComponent, found);
				}
				if (retval != null){
					return retval;
				}
			}else if (found.value){
				if (accept(comp)){
					return comp;
				}
			}
		}
		if (found.value){
			if (accept(aContainer)){
				return aContainer;
			}
		}else if (aContainer == aComponent){
			found.value = true;
		}
		return null;
	}
	
	private function checkContainerComponent(aContainer : Container, aComponent : Component):Void{
		if (aContainer == null || aComponent == null){
			trace(aContainer + " and aComponent cannot be null");
			throw new Error("aContainer and aComponent cannot be null");
		}else if ((!aContainer.isFocusTraversalPolicyProvider()) && (!aContainer.isFocusCycleRoot())){
			trace(aContainer + " should be focus cycle root or focus traversal policy provider");
			throw new Error("aContainer should be focus cycle root or focus traversal policy provider");
		}else if (aContainer.isFocusCycleRoot() && (! aComponent.isFocusCycleRootOfContainer(aContainer))){
			trace("aContainer is not a focus cycle root of aComponent");
			throw new Error("aContainer is not a focus cycle root of aComponent");
		}		
	}
	
	public function accept(aComponent:Component):Boolean{
		if (!(((aComponent.isVisible() 
				&& aComponent.isDisplayable()) 
				&& aComponent.isFocusable()) 
				&& aComponent.isEnabled())){
			return false;
		}else{
			return true;
		}
	}
}
