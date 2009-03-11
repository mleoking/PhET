/* Copyright 2006, University of Colorado */

package edu.colorado.phet.localizationstrings;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Properties;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.licensing.Config;
import edu.colorado.phet.licensing.reports.LicenseIssue;
import edu.colorado.phet.localizationstrings.util.FileFinder;
import edu.colorado.phet.localizationstrings.util.SimpleHtmlOutputHelper;
import edu.colorado.phet.localizationstrings.util.WildCardFileFilter;

/**
 * Main class for a standalone, command line application that looks for orphan
 * strings in the properties files.  An "orphan string" is a string that is
 * defined in a localization property file that is not used in the source
 * code.
 * 
 * @author John Blanco
 */
public class OrphanStringChecker {

	File m_baseDirectory;
	
	/**
	 * Constructor.
	 * 
	 * @param baseDirectory
	 */
	public OrphanStringChecker( File baseDirectory ) {
		m_baseDirectory = baseDirectory;
	}
	
	// TODO: Make this all static?
	/**
	 * Check the base directory for orphan strings and output the resulting
	 * report to the standard output.
	 */
	public void checkForOrphanStrings(){
		
		// Locate the simulations.
        String[] simulations = PhetProject.getSimNames( m_baseDirectory );
        
        if (simulations.length == 0){
        	System.out.println("No simulations found in base directory " + m_baseDirectory.getAbsolutePath());
        	return;
        }
        
        SimpleHtmlOutputHelper.printHtmlHeader();
        SimpleHtmlOutputHelper.printHtmlHeading("Orphan String Report", 1);
        
        for ( int i = 0; i < simulations.length; i++ ) {
            SimpleHtmlOutputHelper.printHtmlHeading("Simulation: " + simulations[i], 2);
            
            // Find the localization directory.
            String localizationDirName = m_baseDirectory.getAbsolutePath() + "/simulations/" + simulations[i] + 
            	"/data/" + simulations[i] + "/localization";
            File localizationDir = new File( localizationDirName );
            if (!localizationDir.exists() || !localizationDir.isDirectory()){
            	System.out.println("Could not find localization directory " + localizationDirName);
            	continue;
            }
            
            // Make a list of the localization property files.
            File[] localizationFiles = localizationDir.listFiles( new WildCardFileFilter("*.properties") );
            
            // Find the java source directory.
            String javaSourceDirName = m_baseDirectory.getAbsolutePath() + "/simulations/" + simulations[i] + "/src/";
            File javaSourceDir = new File( javaSourceDirName );
            if (!javaSourceDir.exists() || !javaSourceDir.isDirectory()){
            	System.out.println("Could not find java source directory " + javaSourceDirName);
            	continue;
            }
            
            // Make a list of the Java files that make up this sim.
            ArrayList javaFiles = new ArrayList();
            FileFinder.findFiles(javaSourceDir, new WildCardFileFilter("*.java"), javaFiles);

            // Go through the property files.
            for (int propFileIndex = 0; propFileIndex < localizationFiles.length; propFileIndex++){
            	
            	// Load the properties from the property file.
            	Properties localizationStringProps = new Properties();
            	try {
					localizationStringProps.load(new FileInputStream(localizationFiles[propFileIndex]));
				} catch (FileNotFoundException e) {
					System.err.println("File not found: " + localizationFiles[propFileIndex].getAbsolutePath());
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					System.err.println("IO exception on file: " + localizationFiles[propFileIndex].getAbsolutePath());
					e.printStackTrace();
					continue;
				}
				
				SimpleHtmlOutputHelper.printHtmlHeading("Properties File: " + localizationFiles[propFileIndex].getName(), 3);
				
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
					if ((propName.contains(".name")) || (propName.contains(".description"))){
//						System.out.println("Removing property " + propName);
						iter.remove();
					}
				}
				
				// Reset the iterator.
				iter = unusedProperties.listIterator();
				
				for (int javaFileIndex = 0; (javaFileIndex < javaFiles.size()) && (!unusedProperties.isEmpty()); javaFileIndex++){
					
					// Skip files that are from the SVN directories.
					String javaFileName = ((File)javaFiles.get(javaFileIndex)).getAbsolutePath();
					if (javaFileName.contains("svn-base")){
						continue;
					}
					
//					System.out.println("Java file name: " + javaFileName);
					
					// Create a buffered reader for the Java source file.
					BufferedReader javaFileReader;
					try {
						javaFileReader = new BufferedReader(new FileReader((File)(javaFiles.get(javaFileIndex))));
					} catch (FileNotFoundException e) {
						System.err.println("Error reading file " + javaFileName);
						e.printStackTrace();
						continue;
					}

					String lineOfJavaFile;
			    	try {
						while ((lineOfJavaFile = javaFileReader.readLine()) != null) {
//							System.out.println("Line of Java file: " + lineOfJavaFile);
							while (iter.hasNext()){
								String propName = (String)iter.next();
//								System.out.println("Property name = " + propName);
								if (lineOfJavaFile.contains(propName)){
//									System.out.println("Removing property " + propName);
//									System.out.println("Found on line: " + lineOfJavaFile);
						    		iter.remove();
						    	}
							}
							iter = unusedProperties.listIterator();
		                }
					} catch (IOException e) {
						System.err.println("Error while reading file " + 
								((File)javaFiles.get(javaFileIndex)).getAbsolutePath());
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
        }
        
        SimpleHtmlOutputHelper.printHtmlClose();
	}

	private static void printUsage(){
		System.out.println("Usage: CheckForOrphanStrings <base-directory>");
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
		
		OrphanStringChecker checker = new OrphanStringChecker( baseDirectory );
		checker.checkForOrphanStrings();
			
	}
}
