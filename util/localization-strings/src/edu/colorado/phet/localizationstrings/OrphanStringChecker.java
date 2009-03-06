/* Copyright 2006, University of Colorado */

package edu.colorado.phet.localizationstrings;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.licensing.Config;
import edu.colorado.phet.licensing.reports.LicenseIssue;
import edu.colorado.phet.localizationstrings.util.FileFinder;
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
        
        for ( int i = 0; i < simulations.length; i++ ) {
            System.out.println( "Simulation: " + simulations[i] );
            
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
            
            // TODO temp. - Print a list of the localization files.
            for (int j = 0; j < localizationFiles.length; j++){
            	System.out.println("Localization file: " + localizationFiles[j].getAbsolutePath());
            }
            
            // Find the java source directory.
            String javaSourceDirName = m_baseDirectory.getAbsolutePath() + "/simulations/" + simulations[i] + 
            	"/src/";
            File javaSourceDir = new File( javaSourceDirName );
            if (!javaSourceDir.exists() || !javaSourceDir.isDirectory()){
            	System.out.println("Could not find java source directory " + javaSourceDirName);
            	continue;
            }
            
            // Make a list of the Java file that comprise this sim.
            ArrayList javaFiles = new ArrayList();
            FileFinder.findFiles(javaSourceDir, new WildCardFileFilter("*.java"), javaFiles);

            // See if the properties are used.
            for (int javaFileIndex = 0; javaFileIndex < javaFiles.size(); javaFileIndex++){
            	System.out.println("Java file: " + ((File)javaFiles.get(javaFileIndex)).getAbsolutePath());
                for (int propFileIndex = 0; propFileIndex < localizationFiles.length; propFileIndex++){
                	// Load the properites from the property file.
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
					
					boolean [] propertyUsed = new boolean[localizationStringProps.size()];
					
					for (int propIndex = 0; propIndex < localizationStringProps.size(); propIndex++){
					}
                }
            }
            
            // Load up
        }
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
