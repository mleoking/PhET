-libraryjars <java.home>/lib/rt.jar
-injars      build/arithmetic_en.jar
-injars      ../simulations-java/contrib/javaws/jnlp.jar
-outjars     build/arithmetic_en-pro.jar
-ignorewarnings

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}