package edu.colorado.phet.translationstringsdiff;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * This class creates the list of localization files based on the provided
 * file name stem.
 * 
 * @author John
 *
 */
public class LocalizationFileList extends ArrayList {

	String m_localizationFileNameStem;
	
	public LocalizationFileList(String localizationFileNameStem) {
		
		m_localizationFileNameStem = localizationFileNameStem;
		
		// Create file for looking at files.
	    FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.startsWith(m_localizationFileNameStem);
	        }
	    };
		File dir = new File(".");
	    String[] children = dir.list(filter);

	    for (int i = 0; i < children.length; i++){
	    	add(children[i]);
	    }
	}
}
