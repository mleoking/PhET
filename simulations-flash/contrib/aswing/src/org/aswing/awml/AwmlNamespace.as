/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.util.ArrayUtils;

/**
 * Namespace class provided container for components beloned to and child
 * namespaces.
 * 
 * @author Sadovskiy
 */
class org.aswing.awml.AwmlNamespace {
	
	/** Namespace name */
	private var name:String;
	
	/** Holds components belonged to current namespace */
	private var components:Array;
	
	/** Holds child namespaces */
	private var namespaces:Array;
	
	/**
	 * Constructs new AWML namespace instance.
	 * 
	 * @param namespaceName the name of the namespace
	 */
	public function AwmlNamespace(namespaceName:String) {
		name = namespaceName;
		components = new Array();
		namespaces = new Array();
	}
	
	/**
	 * Returns the name of the current namespace.
	 * 
	 * @return the namespace name
	 */
	public function getName():String {
		return name;	
	}
	
	/**
	 * Removes all components and child namespaces from the current namespace.
	 */
	public function removeAll(Void):Void {
		removeAllChildNamespaces();
		removeAllComponents();
	}
	
	/**
	 * Adds new child namespace with the specified <code>namespaceName</code>
	 * to the current namespace and return created instance.
	 * 
	 * @param namespaceName the child namespace name
	 * @return instance of the created child namespace  
	 */
	public function addChildNamespace(namespaceName:String):AwmlNamespace {
		var namespace:AwmlNamespace = new AwmlNamespace(namespaceName);
		namespaces.push(namespace);
		return namespace;		
	}

	/**
	 * Searches child namespaces for the namespace with the given <code>namespaceName</code> 
	 * and returns it or <code>null</code> if not found.
	 * 
	 * @param namespaceName the name of the namespace to search
	 * @return the found namespace or <code>null</code> 
	 */
	public function getChildNamespace(namespaceName:String):AwmlNamespace {
		for (var i = 0; i < namespaces.length; i++) {
			var namespace:AwmlNamespace = AwmlNamespace(namespaces[i]);
			if (namespace.getName() == namespaceName) return namespace; 
		}
		return null;	
	}

	/**
	 * Searches the current namespace and all its child namespace tree 
	 * for the namespace with the given <code>namespaceName</code> and returns 
	 * it or <code>null</code> if not found.
	 * 
	 * @param namespaceName the name of the namespace to search
	 * @return the found namespace or <code>null</code> 
	 */
	public function findNamespace(namespaceName:String):AwmlNamespace {
		if (getName() == namespaceName) return this;
		
		for (var i = 0; i < namespaces.length; i++) {
			var namespace:AwmlNamespace = AwmlNamespace(namespaces[i]).findNamespace(namespaceName);
			if (namespace != null) return namespace;
		}
		return null;	
	}

	/**
	 * Removes the given namespace from the child list of the 
	 * current namespace.
	 * 
	 * @param namespace the namespace to remove
	 */
	public function removeChildNamespace(namespace:AwmlNamespace):Void {
		ArrayUtils.removeFromArray(namespaces, namespace);
		namespace.removeAll();
	} 

	/**
	 * Removes all chald namespaces from the current namespace.
	 */
	public function removeAllChildNamespaces(Void):Void {
		for (var i = 0; i < namespaces.length; i++) {
			AwmlNamespace(namespaces[i]).removeAll();	
		}
		namespaces.splice(0);
	}

	/** 
	 * Adds passed-in <code>component</code> to the current namespace.
	 * 
	 * @param component the component to add to the namespace
	 */
	public function addComponent(component:Component):Void {
		components.push(component);
	}

	/**
	 * Searches current namespace for the component with the specified 
	 * <code>awmlID</code>.
	 * 
	 * @param awmlID the AWML ID of the component to search
	 * @return found component with the specified <code>awmlID</code>
	 * or <code>null</code> if component with the specified  
	 * <code>awmlID</code> doesn't exist
	 */
	public function getComponent(awmlID:String):Component {
		// search this namespace
		for (var i = 0; i < components.length; i++) {
			var component:Component = components[i];
			if (component.getAwmlID() == awmlID) return component;
		}
		return null;		
	}

	/** 
	 * Removes passed-in <code>component</code> from the current namespace.
	 * 
	 * @param component the component to remove from the namespace
	 */
	public function removeComponent(component:Component):Void {
		ArrayUtils.removeFromArray(components, component);
	}

	/** 
	 * Removes all components from the current namespace.
	 */
	public function removeAllComponents():Void {
		components.splice(0);
	}
	
	/**
	 * Searches current namespace and its child namespaces for
	 * component with the specified <code>awmlID</code>.
	 * 
	 * @param awmlID the AWML ID of the component to search
	 * @return found component with the specified <code>awmlID</code>
	 * or <code>null</code> if component with the specified  
	 * <code>awmlID</code> doesn't exist
	 */
	public function findComponent(awmlID:String):Component {
		// search this namespace
		var component:Component = getComponent(awmlID);
		if (component != null) return component;
		
		// search child namespaces
		for (var i = 0; i < namespaces.length; i++) {
			component = AwmlNamespace(namespaces[i]).findComponent(awmlID);
			if (component != null) return component;
		}
		return null;		
	}
	
	public function toString():String {
		return (name != null) ? name : "Root namespace";	
	}
}