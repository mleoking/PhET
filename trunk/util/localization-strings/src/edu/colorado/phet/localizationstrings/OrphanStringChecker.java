/* Copyright 2006, University of Colorado */

package edu.colorado.phet.localizationstrings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Properties;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.localizationstrings.util.FileFinder;
import edu.colorado.phet.localizationstrings.util.SimpleHtmlOutputHelper;
import edu.colorado.phet.localizationstrings.util.WildCardFileFilter;

/**
 * Main class for a standalone, command line application that looks for orphan
 * strings in the properties files.  An "orphan string" is a string that is
 * defined in the main (i.e. English) localization property file but that does
 * not appear literally in the source code. (If the string may still be used 
 * if its key is constructed or loaded programmatically.)
 * 
 * @author John Blanco
 */
public class OrphanStringChecker {
	
	public static final String DATE_FORMAT = "MMM dd yyyy HH:mm:ss";
	public static final String COMMON_CODE_BASE_DIR = "/common/";
	public static final String COMMON_STRINGS_PATH = COMMON_CODE_BASE_DIR + "/phetcommon/data/phetcommon/localization/";
	public static final String COMMON_STRINGS_FILE_NAME = "phetcommon-strings.properties";

	/**
	 * Check the base directory for orphan strings and output the resulting
	 * report to the standard output.  An "orphan string" is a string that is
	 * defined in the English version of the localized strings but is not used
	 * in the source code.
	 * 
	 * @param baseDirectory - The directory on the local machine where the
	 * Java sims and common code reside, which as of this writing is something
	 * like <local-path>/simulations-java.
	 */
	public static void checkForOrphanStrings(File baseDirectory){
		
        // Output the header portion of the report.
        SimpleHtmlOutputHelper.printPageHeader();
        SimpleHtmlOutputHelper.printHeading("Orphan String Report", 1);
        String explanationOfReport = "This report lists the strings that appear in the English version of the " +
        		"localization file but that do NOT appear in any Java or Scala files associated with the simulation.";
        SimpleHtmlOutputHelper.printParagraph(explanationOfReport);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        SimpleHtmlOutputHelper.printParagraph("Base Directory: " + baseDirectory.getAbsolutePath());
        SimpleHtmlOutputHelper.printParagraph("Report generated " + sdf.format(Calendar.getInstance().getTime()));
        
		// Locate the simulations.
        String[] simulations = PhetProject.getSimNames( baseDirectory );
        
        if (simulations.length == 0){
        	System.out.println("No simulations found in base directory " + baseDirectory.getAbsolutePath());
            SimpleHtmlOutputHelper.printClose();
        	return;
        }
        
        // Locate and load the common strings.
        String commonStringsDirName = baseDirectory.getAbsolutePath() + COMMON_STRINGS_PATH;
        File commonStringsDir = new File(commonStringsDirName);
        ArrayList commonPropertyNames = new ArrayList();
        if (!commonStringsDir.isDirectory()){
        	SimpleHtmlOutputHelper.printParagraph("WARNING: Could not locate directory for common strings: " + commonStringsDirName);
        }
        else{
        	ArrayList commonSrcFiles = 
        		getSourceFiles(new File(baseDirectory.getAbsolutePath() + COMMON_CODE_BASE_DIR));

        	// Locate the English version of the common localization strings file.
            File commonEnglishStringsFile = new File(commonStringsDirName + COMMON_STRINGS_FILE_NAME);
            if (!commonEnglishStringsFile.isFile()){
            	System.out.println("Could not find common English localization strings file " + commonEnglishStringsFile.getAbsolutePath());
            }
            else{
            	// Load the properties from the property file.
            	Properties commonStringsProps = new Properties();
            	try {
            		commonStringsProps.load(new FileInputStream(commonEnglishStringsFile));
    			} catch (FileNotFoundException e) {
    				System.out.println("File not found: " + commonEnglishStringsFile.getAbsolutePath());
    				e.printStackTrace();
    	            SimpleHtmlOutputHelper.printClose();
    				return;
    			} catch (IOException e) {
    				System.out.println("IO exception on file: " + commonEnglishStringsFile.getAbsolutePath());
    				e.printStackTrace();
    	            SimpleHtmlOutputHelper.printClose();
    				return;
    			}
    			
            	commonPropertyNames = getPropertyNames(commonStringsProps);
            	
            	// Eliminate property names that are found in the source code.
            	eliminateUsedProperties(commonPropertyNames, commonSrcFiles);
            }
        }
        
        SimpleHtmlOutputHelper.printHeading("Simulations", 2);
        
        // For each simulation...
        for ( int i = 0; i < simulations.length; i++ ) {
            SimpleHtmlOutputHelper.printHeading("Simulation: " + simulations[i], 3);
            
            // Find the localization directory.
            String localizationDirName = baseDirectory.getAbsolutePath() + "/simulations/" + simulations[i] + 
            	"/data/" + simulations[i] + "/localization/";
            File localizationDir = new File( localizationDirName );
            if (!localizationDir.exists() || !localizationDir.isDirectory()){
            	System.out.println("Could not find localization directory " + localizationDirName);
            	continue;
            }
            
            // Locate the English version of the localization strings file.
            File englishStringsFile = new File(localizationDirName + simulations[i] + "-strings.properties");
            if (!englishStringsFile.isFile()){
            	System.out.println("Could not find English localization file " + englishStringsFile.getAbsolutePath());
            	continue;
            }
            
            String simDirectoryName = baseDirectory.getAbsolutePath() + "/simulations/" + simulations[i];
            ArrayList sourceFiles = getSourceFiles( new File (simDirectoryName));

            // Make sure we found some source files.
            if (sourceFiles.size() == 0){
            	System.out.println("WARNING: No source files located for simulation: " + simulations[i]);
            	continue;
            }

        	// Load the properties from the property file.
        	Properties localizationStringProps = new Properties();
        	try {
				localizationStringProps.load(new FileInputStream(englishStringsFile));
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + englishStringsFile.getAbsolutePath());
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				System.out.println("IO exception on file: " + englishStringsFile.getAbsolutePath());
				e.printStackTrace();
				continue;
			}
			
			SimpleHtmlOutputHelper.printHeading("Properties File: " + englishStringsFile.getName(), 4);
			
			// Obtain a list of the property names.
			ArrayList unusedProperties = getPropertyNames(localizationStringProps);
			ListIterator iter = unusedProperties.listIterator();
			
			// Go through the list and remove any standard property names
			// that wouldn't show up in the source files but that are known to
			// be used elsewhere.
			while (iter.hasNext()){
				String propName = (String)iter.next();
				if ((propName.endsWith(".name")) || (propName.endsWith(".description"))){
					iter.remove();
				}
			}
			
			// Eliminate the sim properties that are used in the source files.
			eliminateUsedProperties(unusedProperties, sourceFiles);
			
			// Output the list of unused strings.
			SimpleHtmlOutputHelper.printHeading("Orphan Strings for this Sim", 5);
			if (unusedProperties.size() == 0){
				System.out.println("No orphan strings found.");
			}
			else{
				SimpleHtmlOutputHelper.printStartList();
				for (int j = 0; j < unusedProperties.size(); j++){
		    		SimpleHtmlOutputHelper.printListItem((String)unusedProperties.get(j));
				}
				SimpleHtmlOutputHelper.printEndList();
			}
			
			// Check for an eliminate any common properties that are used in
			// these source files.
			eliminateUsedProperties(commonPropertyNames, sourceFiles);
        }
        
        // Output the list of unused common code strings.
		SimpleHtmlOutputHelper.printHeading("Orphan Strings for Common Code", 2);
		if (commonPropertyNames.size() == 0){
			System.out.println("No orphan strings found.");
		}
		else{
			SimpleHtmlOutputHelper.printStartList();
			for (int j = 0; j < commonPropertyNames.size(); j++){
	    		SimpleHtmlOutputHelper.printListItem((String)commonPropertyNames.get(j));
			}
			SimpleHtmlOutputHelper.printEndList();
		}
        
        SimpleHtmlOutputHelper.printClose();
	}

	/**
	 * Eliminate the properties from the supplied list that are found to be
	 * used somewhere within the source code.
	 * 
	 * @param propertyNames
	 * @param sourceFiles
	 * @return
	 */
	private static void eliminateUsedProperties( ArrayList propertyNames, ArrayList sourceFiles ){
		
		ListIterator iter = propertyNames.listIterator();
		
		// For each source code file...
		for (int srcFileIndex = 0; (srcFileIndex < sourceFiles.size()) && (!propertyNames.isEmpty()); srcFileIndex++){
			
			// Skip files that are from the SVN directories.
			String srcFileName = ((File)sourceFiles.get(srcFileIndex)).getAbsolutePath();
			if (srcFileName.contains("svn-base")){
				continue;
			}
			
			// Create a buffered reader for the source file.
			BufferedReader srcFileReader;
			try {
				srcFileReader = new BufferedReader(new FileReader((File)(sourceFiles.get(srcFileIndex))));
			} catch (FileNotFoundException e) {
				System.out.println("Error reading file " + srcFileName);
				e.printStackTrace();
				continue;
			}

			String lineOfSourceFile;
			try {
				// For each line of the source code file...
				while ((lineOfSourceFile = srcFileReader.readLine()) != null) {
					// A very small optimization: skip blank lines.
					if (lineOfSourceFile.length() == 0){
						continue;
					}
					// For each property, see if it can be found on this line of the Java file.
					while (iter.hasNext()){
						String propName = (String)iter.next();
						if (lineOfSourceFile.contains(propName)){
				    		iter.remove();
				    	}
					}
					iter = propertyNames.listIterator();
		        }
			} catch (IOException e) {
				System.out.println("Error while reading file " + 
						((File)sourceFiles.get(srcFileIndex)).getAbsolutePath());
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * Get a list of the source files (Java and Scala) that exist beneath the
	 * given directory.
	 * 
	 * @return List of source files found.
	 */
	private static ArrayList getSourceFiles( File dir ){

		ArrayList sourceFiles = new ArrayList();
		
		if (!dir.isDirectory()){
			SimpleHtmlOutputHelper.printParagraph("ERROR: " + dir.getAbsolutePath() + "is not a directory.");
			return sourceFiles;
		}

        File sourceDir = new File( dir.getAbsolutePath() );

        // Find the Java source files.
        FileFinder.findFiles(sourceDir, new WildCardFileFilter("*\\.java"), sourceFiles);
        
        // Find the Scala source files (if any).
        FileFinder.findFiles(sourceDir, new WildCardFileFilter("*\\.scala"), sourceFiles);
        
        return sourceFiles;
	}

	/**
	 * Make an array list of the names of the properties found in the given
	 * properties variable.
	 */
	private static ArrayList getPropertyNames(Properties properties){
		ArrayList propertyNames = new ArrayList();
		Enumeration propNamesEnum = properties.propertyNames();
	    while (propNamesEnum.hasMoreElements()){
	    	propertyNames.add(propNamesEnum.nextElement());
	    }
	    return propertyNames;
	}

	private static void printUsage(){
		System.out.println("Usage: CheckForOrphanStrings <base-directory>, where the base-directory is the");
		System.out.println("location of the simulations-java directory on the local machine.");
	}
	
	public static void main (String[] args) {
		
		// Verify that this was run with reasonable arguments.
		if ( args.length != 1 ){
			printUsage();
			System.exit(1);
		}
		
		File baseDirectory = new File( args[0] );
		
		if (!baseDirectory.isDirectory()){
			System.out.println("Cannot locate base directory " + args[0]);
			printUsage();
			System.exit(1);
		}
		
		OrphanStringChecker.checkForOrphanStrings( baseDirectory );
	}
}
