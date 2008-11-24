/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.component.ComponentParser;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.LayoutManager;

/**
 * Parses {@link org.aswing.Container} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ContainerParser extends ComponentParser {
	
    private static var ATTR_ON_COMPONENT_ADDED:String = "on-component-added";
    private static var ATTR_ON_COMPONENT_REMOVED:String = "on-component-removed";
	
	
	/**
	 * Private Constructor.
	 */
	private function ContainerParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, container:Container, namespace:AwmlNamespace) {
		
		container = super.parse(awml, container, namespace);
		
        // init events
        attachEventListeners(container, Container.ON_COM_ADDED, getAttributeAsEventListenerInfos(awml, ATTR_ON_COMPONENT_ADDED));
        attachEventListeners(container, Container.ON_COM_REMOVED, getAttributeAsEventListenerInfos(awml, ATTR_ON_COMPONENT_REMOVED));
		
		return container;
	}
	
	private function parseChild(awml:XMLNode, nodeName:String, container:Container, namespace:AwmlNamespace):Void {

		super.parseChild(awml, nodeName, container, namespace);

		if (AwmlUtils.isComponentNode(nodeName)) {
			var component:Component = AwmlParser.parse(awml, null, namespace);
			if (component != null) append(container, component, component.getAwmlIndex());
		} else if (AwmlUtils.isLayoutNode(nodeName)) {
			var layout:LayoutManager = AwmlParser.parse(awml);
			if (layout != null) setLayout(container, layout);
		}
	}
	
	/**
	 * Appends <code>component</code> to the <code>container</code>.
	 * 
	 * @param container the container to add the component to
	 * @param component the component to add to the container
	 * @param index (optional) the index to insert component into container 
	 */
	private function append(container:Container, component:Component, index:Number):Void {
		if (index != null) {
			container.insert(index, component);
		} else {
			container.append(component);
		}
	}
	
	/**
	 * Set <code>layout</code> to the <code>container</code>.
	 * 
	 * @param container the container to set layout
	 * @param layout the layout to set 
	 */
	private function setLayout(container:Container, layout:LayoutManager):Void {
		container.setLayout(layout);
	}
	
}
