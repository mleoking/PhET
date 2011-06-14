/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * The default list cell factory for JComboBox drop down list.
 * @see org.aswing.JComboBox
 * @author iiley
 */
public class DefaultComboBoxListCellFactory extends DefaultListTextCellFactory{
	
	public function DefaultComboBoxListCellFactory(shareCelles:Boolean=true, sameHeight:Boolean=true){
		super(DefaultComboBoxListCell, shareCelles, sameHeight);
	}
}
}