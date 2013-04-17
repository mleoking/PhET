/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.ContainerParser;
import org.aswing.MCPanel;
import org.aswing.ASWingUtils;

/**
 * Parses {@link org.aswing.MCPanel} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.MCPanelParser extends ContainerParser {
	
	private static var ATTR_MOVIE_CLIP:String = "movie-clip";
	
	/**
	 * Constructor.
	 */
	public function MCPanelParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, panel:MCPanel, namespace:AwmlNamespace) {
		
		panel = super.parse(awml, panel, namespace);
		
		return panel;
	}

	private function getMovieClip(awml:XMLNode):MovieClip {
		return getAttributeAsMovieClip(awml, ATTR_MOVIE_CLIP, ASWingUtils.getRootMovieClip());
	}

    private function getClass(Void):Function {
    	return MCPanel;	
    }
    
    private function getArguments(awml:XMLNode):Array {
    	return [getMovieClip(awml)];
    }    

}
