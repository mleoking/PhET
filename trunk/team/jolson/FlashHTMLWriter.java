/*
 * Generation of HTML to wrap Flash simulations in.
 * Automatically handles inserting internationalization XML and other data
 *
 * author: Jonathan Olson
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FlashHTMLWriter {
	public static void writeHTML(String simName, String language, String country, String deployment, String distributionTag, String xmlFile, String htmlFile, String propertiesFile, String commonXmlFile) throws FileNotFoundException, UnsupportedEncodingException {
		/* Reads internationaliaztion data from an XML file, and generates the corresponding HTML file
		 * that will pass the data into Flash through FlashVars parameters.
		 *
		 * simName: example: "pendulum-lab"
		 * language: example: "en"
		 * country: example "US" or default "none"
		 * deployment: example "phet-website" ?
		 * distributionTag: example "Wiley"
		 * xmlFile: The file to read internationalization data from. Example: "pendulum-lab-strings_en.xml"
		 * htmlFile: File to output the HTML into.
		 * propertiesFile: example: "pendulum-lab.properties". Includes version information and background color
		 */
		
		// all of the fields from the <simName>.properties file
		String versionMajor = null;
		String versionMinor = null;
		String dev = null;
		String revision = null;
		String bgcolor = null;
		
		// parse the .properties file, store results in variables above
		File propFile = new File(propertiesFile);
		Scanner propScanner = new Scanner(propFile);
		propScanner.useDelimiter("[\n=]");
		while (propScanner.hasNext()) {
			String field = propScanner.next();
			String value = propScanner.next().trim();
			if(field.equals("version.major")) {
				versionMajor = value;
			} else if(field.equals("version.minor")) {
				versionMinor = value;
			} else if(field.equals("version.dev")) {
				dev = value;
			} else if(field.equals("version.revision")) {
				revision = value;
			} else if(field.equals("bgcolor")) {
				bgcolor = value;
			}
		}
		propScanner.close();
		
		// open and read all internationalization data from XML file
		File inFile = new File(xmlFile);
		Scanner scan = new Scanner(inFile);
		scan.useDelimiter("\\Z");
		String rawXML = scan.next();
		
		String bgint = String.valueOf(Integer.parseInt(bgcolor.substring(1), 16));
		
		// encode XML into UTF-8 form compatible for passing into Flash
		String encodedXML = URLEncoder.encode(rawXML, "UTF-8");
		
		// do the same for the common strings
		File commonInFile = new File(commonXmlFile);
		Scanner commonScan = new Scanner(commonInFile);
		commonScan.useDelimiter("\\Z");
		String rawCommonXML = commonScan.next();
		
		// encode XML into UTF-8
		String commonEncodedXML = URLEncoder.encode(rawCommonXML, "UTF-8");
		
		// prepare variables to be passed in
		String flashVars = "languageCode=" + language;
		flashVars += "&countryCode=" + country;
		flashVars += "&internationalization=" + encodedXML;
		flashVars += "&commonStrings=" + commonEncodedXML;
		flashVars += "&versionMajor=" + versionMajor;
		flashVars += "&versionMinor=" + versionMinor;
		flashVars += "&dev=" + dev;
		flashVars += "&revision=" + revision;
		flashVars += "&simName=" + simName;
		flashVars += "&simDeployment=" + deployment;
		flashVars += "&simDistributionTag=" + distributionTag;
		flashVars += "&bgColor=" + bgint;
		
		// prepare string of HTML file:
		String swfName = simName + ".swf";
		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n";
		html += "<head>\n";
		html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n";
		html += "<title>" + simName + "_" + language + (country.equals("none") ? "" : "_" + country) + "</title>\n"; // NOTE: change title? usually not seen.
		
		html += "</head>\n";
		html += "<body bgcolor=\"" + bgcolor + "\">\n"; // we want to get the correct background color!
		html += "<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0\" width=\"100%\" height=\"100%\" id=\"" + simName + "\" align=\"middle\">\n";
		html += "<param name=\"allowScriptAccess\" value=\"always\" />\n";
		html += "<param name=\"movie\" value=\"" + swfName + "\" />\n";
		html += "<param name=\"quality\" value=\"high\" />\n";
		html += "<param name=\"bgcolor\" value=\"" + bgcolor + "\" />\n"; // we want to get the correct background color!
		html += "<param name = \"FlashVars\"  value = \"" + flashVars + "\"/>\n";
		html += "<embed id=\"x_" + simName + "\" src=\"" + swfName + "\" quality=\"high\" bgcolor=\"" + bgcolor + "\" width=\"100%\" height=\"100%\" name=\"" + simName + "\" align=\"middle\" allowScriptAccess=\"always\" FlashVars = \"" + flashVars + "\" ";
		html += "type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" />\n";
		html += "</object>";
		html += "</body>";
		html += "</html>";
		
		// write to file
		FileOutputStream fileOut = new FileOutputStream(htmlFile);
		PrintStream printOut = new PrintStream(fileOut);
		printOut.println(html);
		printOut.close();
	}
	public static void main(String args[]) {
		try {
			String simName = "pendulum-lab";
			String language = "sk";
			String country = "none";
			if(args.length > 1) {
				simName = args[0];
				language = args[1];
			} else {
				System.err.println("Need at least a sim name and language");
				System.exit(1);
			}
			if(args.length > 2) {
				country = args[2];
			}
			
			
			// relative pathnames
			String locale = language + (country.equals("none") ? "" : "_" + country);
			
			String xmlFile = "simulations/" + simName + "/data/localization/" + simName + "-strings_" + locale + ".xml";
			
			// if sim localization does not exist, use english as a default
			if((new File(xmlFile)).exists() == false) {
				xmlFile = "simulations/" + simName + "/data/localization/" + simName + "-strings_" + "en" + ".xml";
				System.out.println("WARNING: could not find sim internationalization data for " + locale + ", defaulting to en");
			}
			
			String htmlFile = "simulations/" + simName + "/deploy/" + simName + "_" + locale + ".html";
			String propertiesFile = "simulations/" + simName + "/data/" + simName + ".properties";
			
			String commonXmlFile = "common/data/localization/common-strings_" + locale + ".xml";
			// if common localization does not exist, use english as a default
			if((new File(commonXmlFile)).exists() == false) {
				commonXmlFile = "common/data/localization/common-strings_" + "en" + ".xml";
				System.out.println("WARNING: could not find common internationalization data for " + locale + ", defaulting to en");
			}
			
			writeHTML(simName, language, country, "standalone-jar", "none", xmlFile, htmlFile, propertiesFile, commonXmlFile);
		} catch(FileNotFoundException e) {
			System.out.println("File Not Found: " + e.toString());
		} catch(UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding");
		}
	}
}
