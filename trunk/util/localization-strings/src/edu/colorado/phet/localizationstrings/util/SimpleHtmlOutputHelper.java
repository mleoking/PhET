package edu.colorado.phet.localizationstrings.util;

public class SimpleHtmlOutputHelper {
	
	public static void printHtmlHeader(){
		System.out.println("<html>");
		System.out.println("<body>");
	}
	
	public static void printHtmlClose(){
		System.out.println("</body>");
		System.out.println("</html>");
	}
	
	public static void printHtmlHeading(String text, int level){
		if ((level > 5) || (level < 1)){
			System.err.println("Invalid header level: " + level);
			level = 1;
		}
		System.out.println("<h" + level + ">" + text + "</h" + level + ">");
	}
	
	public static void printHtmlStartList(){
		System.out.println("<ul>");
	}

	public static void printHtmlListItem(String text){
		System.out.println("<li>" + text + "</li>");
	}
	
	public static void printHtmlEndList(){
		System.out.println("</ul>");
	}

	public static void printHtmlBreak(){
		System.out.println("<br>");
	}
	
	public static void printHtmlParagraph(String text){
		System.out.print("<p>");
		System.out.print(text);
		System.out.print("</p>");
		
	}
}
