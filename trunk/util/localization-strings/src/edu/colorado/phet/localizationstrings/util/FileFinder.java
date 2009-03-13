package edu.colorado.phet.localizationstrings.util;

import java.io.File;
import java.util.*;

public class FileFinder {
	
	public static void findFiles(File directory, WildCardFileFilter filter, ArrayList fileList) {
		File[] filesToAdd = directory.listFiles( filter );
		for (int i=0; i<filesToAdd.length; i++){
			fileList.add(filesToAdd[i]);
		}
		File[] files = directory.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()){
				findFiles(files[i], filter, fileList);
			}
		}
	}
}