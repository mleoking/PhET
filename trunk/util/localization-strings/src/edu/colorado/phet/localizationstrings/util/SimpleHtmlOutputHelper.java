package edu.colorado.phet.localizationstrings.util;

public class SimpleHtmlOutputHelper {
	
	public static void printPageHeader(){
		System.out.println("<html>");
		System.out.println("<body>");
	}
	
	public static void printClose(){
		System.out.println("</body>");
		System.out.println("</html>");
	}
	
	public static void printHeading(String text, int level){
		if ((level > 5) || (level < 1)){
			System.err.println("Invalid header level: " + level);
			level = 1;
		}
		System.out.println("<h" + level + ">" + text + "</h" + level + ">");
	}
	
	public static void printStartList(){
		System.out.println("<ul>");
	}

	public static void printListItem(String text){
		System.out.println("<li>" + text + "</li>");
	}
	
	public static void printEndList(){
		System.out.println("</ul>");
	}

	public static void printTextBreak(){
		System.out.println("<br>");
	}
	
	public static void printParagraph(String text){
		System.out.print("<p>");
		System.out.print(text);
		System.out.print("</p>");
		
	}
}
