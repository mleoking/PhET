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
 * defined in the main (i.e. English) localization property file but that is
 * not used in the source code.
 * 
 * @author John Blanco
 */
public class OrphanStringChecker {
	
	public static final String DATE_FORMAT = "MMM dd yyyy HH:mm:ss";

	/**
	 * Check the base directory for orphan strings and output the resulting
	 * report to the standard output.  The base directory is expected to be
	 * the full path on the local machine to the directory under which all
	 * the Java simulations reside, which is "<local-dir>/simulations-java"
	 * at the time of this writing.
	 */
	public static void checkForOrphanStrings(File baseDirectory){
		
        // Output the header portion of the report.
        SimpleHtmlOutputHelper.printHtmlHeader();
        SimpleHtmlOutputHelper.printHtmlHeading("Orphan String Report", 1);
        String explanationOfReport = "This report lists the strings that appear in the English version of the " +
        		"localization file but that do NOT appear in any Java or Scala files associated with the simulation.";
        SimpleHtmlOutputHelper.printHtmlParagraph(explanationOfReport);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        SimpleHtmlOutputHelper.printHtmlParagraph("Base Directory: " + baseDirectory.getAbsolutePath());
        SimpleHtmlOutputHelper.printHtmlParagraph("Report generated " + sdf.format(Calendar.getInstance().getTime()));
        
		// Locate the simulations.
        String[] simulations = PhetProject.getSimNames( baseDirectory );
        
        if (simulations.length == 0){
        	System.out.println("No simulations found in base directory " + baseDirectory.getAbsolutePath());
        	return;
        }
        
        // For each simulation...
        for ( int i = 0; i < simulations.length; i++ ) {
            SimpleHtmlOutputHelper.printHtmlHeading("Simulation: " + simulations[i], 2);
            
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
            
            ArrayList sourceFiles = new ArrayList();

            // Find the Java source files.
            String javaSourceDirName = baseDirectory.getAbsolutePath() + "/simulations/" + simulations[i] + "/src/";
            File javaSourceDir = new File( javaSourceDirName );
            if (javaSourceDir.exists() && javaSourceDir.isDirectory()){
                FileFinder.findFiles(javaSourceDir, new WildCardFileFilter("*\\.java"), sourceFiles);
            }
            
            // Find the Scala source files (if any).
            String scalaSourceDirName = baseDirectory.getAbsolutePath() + "/simulations/" + simulations[i] + "/scala-src/";
            File scalaSourceDir = new File( scalaSourceDirName );
            if (scalaSourceDir.exists() && scalaSourceDir.isDirectory()){
                FileFinder.findFiles(scalaSourceDir, new WildCardFileFilter("*\\.scala"), sourceFiles);
            }
            
            // Make a list of the Java and Scala files that make up this sim.
            if (sourceFiles.size() == 0){
            	System.out.println("Error: No source files located for simulation: " + simulations[i]);
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
			
			SimpleHtmlOutputHelper.printHtmlHeading("Properties File: " + englishStringsFile.getName(), 3);
			
			// Create a list of the property names.
			ArrayList unusedProperties = new ArrayList();
			Enumeration propNamesEnum = localizationStringProps.propertyNames();
		    while (propNamesEnum.hasMoreElements()){
		    	unusedProperties.add(propNamesEnum.nextElement());
		    }
			ListIterator iter = unusedProperties.listIterator();
			
			// Go through the list and remove any standard property names
			// that wouldn't show up in the Java files.
			while (iter.hasNext()){
				String propName = (String)iter.next();
				if ((propName.endsWith(".name")) || (propName.endsWith(".description"))){
					iter.remove();
				}
			}
			
			// Reset the iterator.
			iter = unusedProperties.listIterator();
			
			// For each source code file...
			for (int srcFileIndex = 0; (srcFileIndex < sourceFiles.size()) && (!unusedProperties.isEmpty()); srcFileIndex++){
				
				// Skip files that are from the SVN directories.
				String srcFileName = ((File)sourceFiles.get(srcFileIndex)).getAbsolutePath();
				if (srcFileName.contains("svn-base")){
					continue;
				}
				
				// Create a buffered reader for the Java source file.
				BufferedReader srcFileReader;
				try {
					srcFileReader = new BufferedReader(new FileReader((File)(sourceFiles.get(srcFileIndex))));
				} catch (FileNotFoundException e) {
					System.out.println("Error reading file " + srcFileName);
					e.printStackTrace();
					continue;
				}

				String lineOfJavaFile;
		    	try {
		    		// For each line of the Java source code file...
					while ((lineOfJavaFile = srcFileReader.readLine()) != null) {
						// A very small optimization: skip blank lines.
						if (lineOfJavaFile.length() == 0){
							continue;
						}
						// For each property, see if it can be found on this line of the Java file.
						while (iter.hasNext()){
							String propName = (String)iter.next();
							if (lineOfJavaFile.contains(propName)){
					    		iter.remove();
					    	}
						}
						iter = unusedProperties.listIterator();
	                }
				} catch (IOException e) {
					System.out.println("Error while reading file " + 
							((File)sourceFiles.get(srcFileIndex)).getAbsolutePath());
					e.printStackTrace();
					continue;
				}
			}

			// Output the list of unused strings.
			SimpleHtmlOutputHelper.printHtmlHeading("Orphan Strings", 4);
			if (unusedProperties.size() == 0){
				System.out.println("No orphan strings found.");
			}
			else{
				SimpleHtmlOutputHelper.printHtmlStartList();
				for (int j = 0; j < unusedProperties.size(); j++){
		    		SimpleHtmlOutputHelper.printHtmlListItem((String)unusedProperties.get(j));
				}
				SimpleHtmlOutputHelper.printHtmlEndList();
			}
        }
        
        SimpleHtmlOutputHelper.printHtmlClose();
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
