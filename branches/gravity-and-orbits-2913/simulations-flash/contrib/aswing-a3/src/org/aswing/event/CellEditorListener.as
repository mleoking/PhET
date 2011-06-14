/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.event { 

import org.aswing.CellEditor;

/**
 * CellEditorListener defines the interface for an object that listens
 * to changes in a CellEditor
 *
 * @author iiley
 */
public interface CellEditorListener {
	
    /** This tells the listeners the editor has ended editing */
    function editingStopped(source:CellEditor):void;

    /** This tells the listeners the editor has canceled editing */
    function editingCanceled(source:CellEditor):void;
}
}