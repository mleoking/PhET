/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.EmptyLayout;
import org.aswing.FocusManager;
import org.aswing.FocusTraversalPolicy;
import org.aswing.geom.Dimension;
import org.aswing.LayoutManager;

/**
 * Container can contain many component to be his child, all children are in its bounds,
 * and it moved, all children moved. It be removed(destroy) all children will be removed.
 * @author iiley
 */
class org.aswing.Container extends Component{
	/**
	 * Event include source and the component just removed.
	 * onComponentAdded(source:Component, child:Component)
	 */	
	public static var ON_COM_ADDED:String = "onComponentAdded";
	
	/**
	 * Event include source and the component just added.
	 * onComponentRemoved(source:Component, child:Component)
	 */	
	public static var ON_COM_REMOVED:String = "onComponentRemoved";
	
	private var children:Array;
	private var layout:LayoutManager;
	
	/**
     * Indicates whether this Component is the root of a focus traversal cycle.
     * Once focus enters a traversal cycle, typically it cannot leave it via
     * focus traversal unless one of the up- or down-cycle keys is pressed.
     * Normal traversal is limited to this Container, and all of this
     * Container's descendants that are not descendants of inferior focus cycle
     * roots.
     *
     * @see #setFocusCycleRoot()
     * @see #isFocusCycleRoot()
     */
    private var focusCycleRoot:Boolean;    
    /**
     * The focus traversal policy that will manage keyboard traversal of this
     * Container's children, if this Container is a focus cycle root. If the
     * value is null, this Container inherits its policy from its focus-cycle-
     * root ancestor. If all such ancestors of this Container have null
     * policies, then the current FocusManager's default policy is
     * used. If the value is non-null, this policy will be inherited by all
     * focus-cycle-root children that have no keyboard-traversal policy of
     * their own (as will, recursively, their focus-cycle-root children).
     * <p>
     * If this Container is not a focus cycle root, the value will be
     * remembered, but will not be used or inherited by this or any other
     * Containers until this Container is made a focus cycle root.
     *
     * @see #setFocusTraversalPolicy()
     * @see #getFocusTraversalPolicy()
     */
    private var focusTraversalPolicy:FocusTraversalPolicy;
    
    /**
     * Stores the value of focusTraversalPolicyProvider property.
     * @see #setFocusTraversalPolicyProvider
     */
    private var focusTraversalPolicyProvider:Boolean;
	
	/**
	 * Abstract Container
	 * 
	 * @see Component
	 */
	private function Container(){
		super();
		setName("Container");
		children = new Array();
		layout = new EmptyLayout();
		focusCycleRoot = false;
		focusTraversalPolicyProvider = false;
	}
	
	public function setLayout(layout:LayoutManager):Void{
		this.layout = layout;
		revalidate();
	}
	
	public function getLayout():LayoutManager{
		return layout;
	}
	
    /** 
     * Invalidates the container.  The container and all parents
     * above it are marked as needing to be laid out.  This method can
     * be called often, so it needs to execute quickly.
     * @see #validate()
     * @see #doLayout()
     * @see org.aswing.LayoutManager
     */
    public function invalidate():Void {
    	layout.invalidateLayout(this);
    	super.invalidate();
    }
	
    /** 
     * Validates this container and all of its subcomponents.
     * <p>
     * The <code>validate</code> method is used to cause a container
     * to lay out its subcomponents again. It should be invoked when
     * this container's subcomponents are modified (added to or
     * removed from the container, or layout-related information
     * changed) after the container has been displayed.
     *
     * @see #append()
     * @see Component#invalidate()
     * @see org.aswing.Component#revalidate()
     */
    public function validate():Void {
    	if(!valid){
    		doLayout();
    		for(var i:Number=0; i<children.length; i++){
    			children[i].validate();
    		}
    		valid = true;
    	}
    }
    
	/**
	 * layout this container
	 */
	public function doLayout():Void{
		super.doLayout();
		if(displayable && isVisible())
			layout.layoutContainer(this);
	}
	
	/**
	 * Create and add this component to a Container.
	 * the method must only can call in a Container's method,
	 * else the Container's layout maybe wrong and Container event will not be called
	 */
	public function addTo(parent:Container):Void{
		super.addTo(parent);
		if(displayable){
			for(var i:Number=0; i<children.length; i++){
				children[i].addTo(this);
			}
		}
	}
	
	/**
	 * Destroy a Container.
	 * A Container was called destroy, it will call its children destroy first,
	 * This can ensure that all its children as not displayable after it be removed.
	 * @see org.aswing.Component#destroy()
	 */
	public function destroy():Void{
		for(var i:Number=0; i<children.length; i++){
			children[i].destroy();
		}
		super.destroy();
	}
		
	/**
	 * This is just for AsWing internal use of child component, call <code>createMovieClip</code> instead if you want 
	 * to creat movie clip on this component.
	 * @param nameStart the name starter of the movieclip
	 * @return the movie clip created
	 * @see #createMovieClip()
	 */
	public function createChildMC(nameStart:String):MovieClip{
		return creater.createMC(target_mc, nameStart);
	}
	
	
	/**
	 * On Component just can add to one Container.
	 * So if the com has a parent, it will remove from its parent first, then add to 
	 * this container. 
	 * This method is shortcut of <code>insert(-1, com, constraints)</code>.
	 * @param com the component to be added
	 * @param constraints an object expressing layout contraints for this component
	 * @see #insert()
	 */
	public function append(com:Component, constraints:Object):Void{
	    insert(-1, com, constraints);
	}
	
	/**
	 * Add component to spesified index.
	 * So if the com has a parent, it will remove from its parent first, then add to 
	 * this container. 
	 * @param i index the position at which to insert the component, or less than 0 value to append the component to the end 
	 * @param com the component to be added
	 * @param constraints an object expressing layout contraints for this component
	 * @throws Error when index > children count
	 * @throws Error when add container's parent(or itself) to itself
	 * @see Component#removeFromContainer()
	 * @see #append()
	 */
	public function insert(i:Number, com:Component, constraints:Object):Void{
		if(i > getComponentCount()){
			trace("illegal component position when insert comp to container");
			throw new Error("illegal component position when insert comp to container");
		}
		if(com instanceof Container){
			for(var cn:Container = this; cn != null; cn = cn.getParent()) {
                if (cn == com) {
                	trace("adding container's parent to itself");
                	throw new Error("adding container's parent to itself");
                }
            }
		}
		if(com.getParent() != null){
			com.removeFromContainer();
		}	
		com.addTo(this);
		if(i < 0){
			children.push(com);
		}else{
			children.splice(i, 0, com);
		}
		layout.addLayoutComponent(com, (constraints == undefined) ? com.getConstraints() : constraints);
		dispatchEvent(createEventObj(ON_COM_ADDED, com));	
		
		if (valid) {
			invalidate();
	    }			
	}
	
	/**
	 * Remove the specified child component.
	 * @return the component just removed, null if the component is not in this container.
	 */
	public function remove(com:Component):Component{
		var i:Number = getIndex(com);
		if(i >= 0){
			return removeAt(i);
		}
		return null;
	}
	
	/**
	 * Remove the specified index child component.
	 * @param i the index of component.
	 * @return the component just removed. or null there is not component at this position.
	 */	
	public function removeAt(i:Number):Component{
		if(i < 0){
			return null;
		}
		var com:Component = children[i];
		if(com != null){
			layout.removeLayoutComponent(com);
			children.splice(i, 1);
			dispatchEvent(createEventObj(ON_COM_REMOVED, com));
			com.destroy();
			com.parent = null;
			
			if (valid) {
				invalidate();
		    }			
		}
		return com;
	}
	
	/**
	 * Remove all child components.
	 */
	public function removeAll():Void{
		while(children.length > 0){
			removeAt(children.length - 1);
		}
	}
	
	/**
	 * Returns whether the container contains specified component as child.
	 */
	public function contains(com:Component):Boolean{
		return getIndex(com) >= 0;
	}
	
    /** 
     * Gets the nth(index) component in this container.
     * @param  n   the index of the component to get.
     * @return the n<sup>th</sup> component in this container. returned null if 
     * the index if out of bounds.  
     * @see #getComponentCount()
     */
	public function getComponent(index:Number):Component{
		return children[index];
	}
	
	/**
	 * Returns the index of the child component in this container.
	 * @return the index of the specified child component.
	 * @see #getComponent()
	 */
	public function getIndex(com:Component):Number{
		for(var i:Number=0; i<children.length; i++){
			if(com == children[i]){
				return i;
			}
		}
		return -1;
	}
	
    /** 
     * Gets the number of components in this container.
     * @return    the number of components in this container.
     * @see       #getComponent()
     */	
	public function getComponentCount():Number{
		return children.length;
	}
	
    /**
     * Checks if the component is contained in the component hierarchy of
     * this container.
     * @param c the component
     * @return     <code>true</code> if it is an ancestor; 
     *             <code>false</code> otherwise.
     */
    public function isAncestorOf(c:Component):Boolean{
		var p:Container = c.getParent();
		if (c == null || p == null) {
		    return false;
		}
		while (p != null) {
		    if (p == this) {
				return true;
		    }
		    p = p.getParent();
		}
		return false;
    }	
	
    /**
     * Sets whether this Container is the root of a focus traversal cycle. Once
     * focus enters a traversal cycle, typically it cannot leave it via focus
     * traversal unless one of the up- or down-cycle keys is pressed. Normal
     * traversal is limited to this Container, and all of this Container's
     * descendants that are not descendants of inferior focus cycle roots. Note
     * that a FocusTraversalPolicy may bend these restrictions, however. For
     * example, ContainerOrderFocusTraversalPolicy supports implicit down-cycle
     * traversal.
     * <p>
     * The alternative way to specify the traversal order of this Container's 
     * children is to make this Container a 
     * {@link org.aswing.Container#isFocusTraversalPolicyProvider}focus traversal policy provider</a>.  
     *
     * @param focusCycleRoot indicates whether this Container is the root of a
     *        focus traversal cycle
     * @see #isFocusCycleRoot()
     * @see #setFocusTraversalPolicy()
     * @see #getFocusTraversalPolicy()
     * @see org.aswing.ContainerOrderFocusTraversalPolicy
     * @see #setFocusTraversalPolicyProvider()
     */
    public function setFocusCycleRoot(focusCycleRoot:Boolean):Void {
        this.focusCycleRoot = focusCycleRoot;
    }
	
    /**
     * Returns whether this Container is the root of a focus traversal cycle.
     * Once focus enters a traversal cycle, typically it cannot leave it via
     * focus traversal unless one of the up- or down-cycle keys is pressed.
     * Normal traversal is limited to this Container, and all of this
     * Container's descendants that are not descendants of inferior focus
     * cycle roots. Note that a FocusTraversalPolicy may bend these
     * restrictions, however. For example, ContainerOrderFocusTraversalPolicy
     * supports implicit down-cycle traversal.
     *
     * @return whether this Container is the root of a focus traversal cycle
     * @see #setFocusCycleRoot()
     * @see #setFocusTraversalPolicy()
     * @see #getFocusTraversalPolicy()
     * @see ContainerOrderFocusTraversalPolicy
     */
    public function isFocusCycleRoot():Boolean {
        return focusCycleRoot;
    }
    
    /**
     * Returns whether the specified Container is the focus cycle root of this
     * Container's focus traversal cycle. Each focus traversal cycle has only
     * a single focus cycle root and each Container which is not a focus cycle
     * root belongs to only a single focus traversal cycle. Containers which
     * are focus cycle roots belong to two cycles: one rooted at the Container
     * itself, and one rooted at the Container's nearest focus-cycle-root
     * ancestor. This method will return <code>true</code> for both such
     * Containers in this case.
     *
     * @param container the Container to be tested
     * @return <code>true</code> if the specified Container is a focus-cycle-
     *         root of this Container; <code>false</code> otherwise
     * @see #isFocusCycleRoot()
     * @see Component#isFocusCycleRootOfContainer()
     */
	public function isFocusCycleRootOfContainer(container:Container):Boolean{
		if (isFocusCycleRoot() && (container == this)){
			return true;
		}else{
			return super.isFocusCycleRootOfContainer(container);
		}
	}    
    
    /**
     * Sets the focus traversal policy that will manage keyboard traversal of
     * this Container's children, if this Container is a focus cycle root. If
     * the argument is null, this Container inherits its policy from its focus-
     * cycle-root ancestor. If the argument is non-null, this policy will be 
     * inherited by all focus-cycle-root children that have no keyboard-
     * traversal policy of their own (as will, recursively, their focus-cycle-
     * root children).
     * <p>
     * If this Container is not a focus cycle root, the policy will be
     * remembered, but will not be used or inherited by this or any other
     * Containers until this Container is made a focus cycle root.
     *
     * @param policy the new focus traversal policy for this Container
     * @see #getFocusTraversalPolicy()
     * @see #setFocusCycleRoot()
     * @see #isFocusCycleRoot()
     */
    public function setFocusTraversalPolicy(policy:FocusTraversalPolicy):Void {
	    this.focusTraversalPolicy = policy;
    }

    /**
     * Returns the focus traversal policy that will manage keyboard traversal
     * of this Container's children, or null if this Container is not a focus
     * cycle root. If no traversal policy has been explicitly set for this
     * Container, then this Container's focus-cycle-root ancestor's policy is
     * returned. 
     *
     * @return this Container's focus traversal policy, or null if this
     *         Container is not a focus cycle root.
     * @see #setFocusTraversalPolicy()
     * @see #setFocusCycleRoot()
     * @see #isFocusCycleRoot()
     */
    public function getFocusTraversalPolicy():FocusTraversalPolicy {
        if (!isFocusTraversalPolicyProvider() && !isFocusCycleRoot()) {
	    	return null;
		}
 
		var policy:FocusTraversalPolicy = this.focusTraversalPolicy;
		if (policy != null) {
		    return policy;
		}
	 
		var rootAncestor:Container = getFocusCycleRootAncestor();
		if (rootAncestor != null) {
		    return rootAncestor.getFocusTraversalPolicy();
		} else {
		    return FocusManager.getCurrentManager().getDefaultFocusTraversalPolicy();
		}
    }

    /**
     * Returns whether the focus traversal policy has been explicitly set for
     * this Container. If this method returns <code>false</code>, this
     * Container will inherit its focus traversal policy from an ancestor.
     *
     * @return <code>true</code> if the focus traversal policy has been
     *         explicitly set for this Container; <code>false</code> otherwise.
     */
    public function isFocusTraversalPolicySet():Boolean {
        return (focusTraversalPolicy != null);
    }

    /**
     * Sets whether this container will be used to provide focus
     * traversal policy. Container with this property as
     * <code>true</code> will be used to acquire focus traversal policy
     * instead of closest focus cycle root ancestor.
     * @param provide indicates whether this container will be used to
     *                provide focus traversal policy
     * @see #setFocusTraversalPolicy()
     * @see #getFocusTraversalPolicy()
     * @see #isFocusTraversalPolicyProvider()
     */
    public function setFocusTraversalPolicyProvider(provider:Boolean):Void {
    	focusTraversalPolicyProvider = provider;
    }
    
    /**
     * Returns whether this container provides focus traversal
     * policy. If this property is set to <code>true</code> then when
     * keyboard focus manager searches container hierarchy for focus
     * traversal policy and encounters this container before any other
     * container with this property as true or focus cycle roots then
     * its focus traversal policy will be used instead of focus cycle
     * root's policy.
     * @see #setFocusTraversalPolicy()
     * @see #getFocusTraversalPolicy()
     * @see #setFocusCycleRoot()
     * @see #setFocusTraversalPolicyProvider()
     * @return <code>true</code> if this container provides focus traversal
     *         policy, <code>false</code> otherwise
     */
    public function isFocusTraversalPolicyProvider():Boolean {
        return focusTraversalPolicyProvider;
    }
	
    /**
     * Transfers the focus down one focus traversal cycle. If this Container is
     * a focus cycle root, then the focus owner is set to this Container's
     * default Component to focus, and the current focus cycle root is set to
     * this Container. If this Container is not a focus cycle root, then no
     * focus traversal operation occurs.
     *
     * @see       Component#requestFocus()
     * @see       #isFocusCycleRoot()
     * @see       #setFocusCycleRoot()
     */
    public function transferFocusDownCycle():Void {
        if (isFocusCycleRoot()) {
	    	FocusManager.getCurrentManager().setCurrentFocusCycleRoot(this);
	    	var toFocus:Component = getFocusTraversalPolicy().getDefaultComponent(this);
		    if (toFocus != null) {
		        toFocus.requestFocus();
		    }
		}
    }
    
	private function findTraversalRoot():Container{
		var currentFocusCycleRoot:Container = FocusManager.getCurrentManager().getCurrentFocusCycleRoot();
		var root:Container;
		if (currentFocusCycleRoot == this){
			root = this;
		}else{
			root = getFocusCycleRootAncestor();
			if (root == null){
				root = this;
			}
		}
		if (root != currentFocusCycleRoot){
			FocusManager.getCurrentManager().setCurrentFocusCycleRoot(root);
		}
		return root;
	}
	
	public function transferFocus():Boolean{
		if (isFocusCycleRoot()){
			var root:Container = findTraversalRoot();
			var comp:Component = this;
			var anc:Container;
			while ((((root != null) 
					&& ((anc = root.getFocusCycleRootAncestor()) != null)) 
					&& (! ((root.isShowing() && root.isFocusable()) 
							&& root.isEnabled()))))				
			{
				comp = root;
				root = anc;
			}
			if (root != null){
				var policy:FocusTraversalPolicy = root.getFocusTraversalPolicy();
				var toFocus:Component;
				toFocus = policy.getComponentAfter(root, comp);
				if (toFocus == null){
					toFocus = policy.getDefaultComponent(root);
				}
				if (toFocus != null){
					return toFocus.requestFocus();
				}
			}
			return false;
		}else{
			return super.transferFocus();
		}
	}
	
	public function transferFocusBackward():Boolean{
		if (isFocusCycleRoot()){
			var root:Container = findTraversalRoot();
			var comp:Component = this;
			while (((root != null) && 
					(! ((root.isShowing() && root.isFocusable()) && root.isEnabled()))))
			{
				comp = root;
				root = comp.getFocusCycleRootAncestor();
			}
			if (root != null){
				var policy:FocusTraversalPolicy = root.getFocusTraversalPolicy();
				var toFocus:Component;
				toFocus = policy.getComponentBefore(root, comp);
				if (toFocus == null){
					toFocus = policy.getDefaultComponent(root);
				}
				if (toFocus != null){
					return toFocus.requestFocus();
				}
			}
			return false;
		}else{
			return super.transferFocusBackward();
		}
	}    
		
	/**
	 * call the ui, if ui return null, ehn call layout to count.
	 */
	private function countMinimumSize():Dimension{
		var size:Dimension = null;
		if(ui != null){
			size = ui.getMinimumSize(this);
		}
		if(size == null){
			size = layout.minimumLayoutSize(this);
		}
		if(size == null){//this should never happen
			size = super.countMinimumSize();
		}
		return size;
	}
	
	/**
	 * call the ui, if ui return null, ehn call layout to count.
	 */
	private function countMaximumSize():Dimension{
		var size:Dimension = null;
		if(ui != null){
			size = ui.getMaximumSize(this);
		}
		if(size == null){
			size = layout.maximumLayoutSize(this);
		}
		if(size == null){//this should never happen
			size = super.countMaximumSize();
		}
		return size;
	}
	
	/**
	 * call the ui, if ui return null, ehn call layout to count.
	 */
	private function countPreferredSize():Dimension{
		var size:Dimension = null;
		if(ui != null){
			size = ui.getPreferredSize(this);
		}
		if(size == null){
			size = layout.preferredLayoutSize(this);
		}
		if(size == null){//this should never happen
			size = super.countPreferredSize();
		}
		return size;
	}



	/**
	 * When child component pressed.
	 * @see org.aswing.JWindow#__onChildPressed()
	 */
	public function __onChildPressed(child:Component):Void{
		parent.__onChildPressed(child);
	}

}
