package edu.colorado.phet.updater;

import java.io.File;
import java.io.IOException;

public class Updater {

	private void update(String project, String sim, String locale, File targetLocation) {
		
		// Download the new, updated version of the sim.
		download(project, targetLocation);
		
		// Execute the newly downloaded sim.
		launchSimulation( sim, locale, targetLocation );
		
	}

	private void launchSimulation(String sim, String locale, File targetLocation) {

		try {
			Runtime.getRuntime().exec("java -jar " + targetLocation.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void download(String project, File targetLocation) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		String project = args[0];
		String sim = args[1];
		String locale = args[2];
		File targetLocation = new File(args[3]);
		
		new Updater().update(project, sim, locale, targetLocation);
	}
}
