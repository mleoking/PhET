/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.tree.TreePath;

/**
 * @author iiley
 */
class org.aswing.tree.PathPlaceHolder {
    var isNew:Boolean;
    var path:TreePath;

    public function PathPlaceHolder(path:TreePath, isNew:Boolean) {
		this.path = path;
		this.isNew = isNew;
    }	
}