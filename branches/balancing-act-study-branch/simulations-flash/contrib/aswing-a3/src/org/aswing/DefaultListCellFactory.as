/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * The default list cell factory for JList.
 * @see org.aswing.JList
 * @author iiley
 */
public class DefaultListCellFactory extends DefaultListTextCellFactory{
	
	public function DefaultListCellFactory(shareCelles:Boolean=true, sameHeight:Boolean=true){
		super(DefaultListCell, shareCelles, sameHeight);
	}
}
}