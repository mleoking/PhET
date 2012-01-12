-injars deploy/unfuddletool.jar
-outjars deploy/unfuddletool_optimized.jar
-libraryjars <java.home>/lib/rt.jar

-keep public class edu.colorado.phet.unfuddletool.UnfuddleTool {
	public static void main( java.lang.String[] );
}
