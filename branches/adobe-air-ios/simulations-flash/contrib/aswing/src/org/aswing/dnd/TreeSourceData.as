/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.dnd.SourceData;
import org.aswing.tree.TreePath;
import org.aswing.util.ArrayUtils;

/**
 * TreeSourceData is the draging source date of a Tree component.
 * 
 * @see org.aswing.overflow.JTree
 * @see org.aswing.overflow.JListTree
 * @author iiley
 */
class org.aswing.dnd.TreeSourceData extends SourceData {
	
	private var paths:Array;
	private var trimedPaths:Array;
	
	/**
	 * Create a TreeSourceData.
	 * @param name the name
	 * @param paths the array of the draging paths, TreePath[]
	 */
	public function TreeSourceData(name : String, paths:Array) {
		super(name, data);
		this.paths = paths.concat();
		
		var childrenPaths:Array = new Array();
		var n:Number = paths.length;
		for(var i:Number=0; i<n; i++){
			var path:TreePath = TreePath(paths[i]);
			for(var j:Number=0; j<n; j++){
				if(i != j){
					if(isChildOf(path, TreePath(paths[j]))){
						childrenPaths.push(path);
					}
				}
			}
		}
		
		trimedPaths = new Array();
		for(var i:Number=0; i<paths.length; i++){
			if(ArrayUtils.indexInArray(childrenPaths, paths[i]) >= 0){
				trimedPaths.push(paths[i]);
			}
		}
	}
	
	private function isChildOf(child:TreePath, parent:TreePath):Boolean{
		return (!child.equals(parent)) && parent.isDescendant(child);
	}
	
	public function getPaths():Array{
		return paths.concat();
	}
	
	/**
	 * Returns the paths array that trimed the children of parent.
	 * This means that if a path in the arry is another path's child in the array, 
	 * only the parent will be included.
	 */
	public function getTrimPaths():Array{
		return trimedPaths.concat();
	}
}