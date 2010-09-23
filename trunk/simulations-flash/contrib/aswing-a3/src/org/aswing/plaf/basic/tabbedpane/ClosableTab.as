package org.aswing.plaf.basic.tabbedpane{

import org.aswing.Component;

/**
 * The closable tab has a close button.
 * @author iiley
 */
public interface ClosableTab extends Tab{
	
	function getCloseButton():Component;
	
}
}