/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.CellEditor;

/**
 * CellEditorListener defines the interface for an object that listens
 * to changes in a CellEditor
 *
 * @author iiley
 */
interface org.aswing.event.CellEditorListener {
	
    /** This tells the listeners the editor has ended editing */
    public function editingStopped(source:CellEditor):Void;

    /** This tells the listeners the editor has canceled editing */
    public function editingCanceled(source:CellEditor):Void;
}