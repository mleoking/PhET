-dontoptimize
-dontobfuscate
-dontusemixedcaseclassnames
-verbose
-ignorewarnings

#uncomment the option below to display unused classes, methods and fields
#-printusage

-keep class * extends edu.colorado.phet.common.phetcommon.util.IProguardKeepClass {
    <fields>;
    <methods>;
}

##########################################################
#The following lines of code are to support jme3
#Resolves the problem: Exception in thread "LWJGL Renderer Thread" java.lang.NoSuchMethodError: getPointer
-keep class * extends org.lwjgl.PointerWrapper{
    <fields>;
    <methods>;
}

#see http://www.java-gaming.org/index.php?;topic=17815.0
-keep class org.lwjgl.LWJGLUtil {*;}
-keep class org.lwjgl.LWJGLException {*;}

-keep class org.lwjgl.opengl.DisplayMode {*;}
-keep class org.lwjgl.opengl.WindowsDisplay {*;}
-keep class org.lwjgl.opengl.WindowsFileVersion {*;}
-keep class org.lwjgl.opengl.PixelFormat {*;}
-keep class org.lwjgl.opengl.GL11 {public *;}
-keep class org.lwjgl.opengl.GL12 {public *;}
-keep class org.lwjgl.opengl.GL13 {public *;}
-keep class org.lwjgl.opengl.ARBBufferObject {public *;}
-keep class org.lwjgl.opengl.GLContext {public *;}
-keep class org.lwjgl.opengl.Display {public *;}

-keep class org.lwjgl.opengl.WindowsDirectInput {*;}
-keep class org.lwjgl.opengl.WindowsDirectInput3 {*;}
-keep class org.lwjgl.opengl.WindowsDirectInput8 {*;}
-keep class org.lwjgl.opengl.WindowsDirectInputDevice {*;}
-keep class org.lwjgl.opengl.WindowsDirectInputDevice3 {*;}
-keep class org.lwjgl.opengl.WindowsDirectInputDevice8 {*;}
-keep class org.lwjgl.opengl.WindowsDirectInputDeviceObjectCallback {*;}

-keep class org.lwjgl.openal.ALCcontext {*;}
-keep class org.lwjgl.openal.ALCdevice {<init>(long);*;}
-keep class org.lwjgl.openal.AL10 {*;}
-keep class org.lwjgl.openal.AL11 {*;}
-keep class org.lwjgl.openal.ALC10 {*;}
-keep class org.lwjgl.openal.ALC11 {*;}

-keep class org.lwjgl.BufferUtils {*;}

-keep class net.java.games.input.OSXEnvironmentPlugin {*;}
-keep class net.java.games.input.LinuxEnvironmentPlugin {*;}
-keep class net.java.games.input.DirectInputEnvironmentPlugin {*;}
-keep class net.java.games.input.RawInputEnvironmentPlugin {*;}
-keep class net.java.games.input.OSXHIDDeviceIterator {*;}

-keep class com.jme3.asset.plugins.ClasspathLocator{*;}
-keep class com.jme3.audio.plugins.WAVLoader{*;}
-keep class com.jme3.audio.plugins.OGGLoader{*;}
-keep class com.jme3.material.plugins.J3MLoader{*;}
-keep class com.jme3.font.plugins.BitmapFontLoader{*;}
-keep class com.jme3.texture.plugins.DDSLoader{*;}
-keep class com.jme3.texture.plugins.PFMLoader{*;}
-keep class com.jme3.texture.plugins.HDRLoader{*;}
-keep class com.jme3.texture.plugins.TGALoader{*;}
-keep class com.jme3.export.binary.BinaryImporter{*;}
-keep class com.jme3.scene.plugins.OBJLoader{*;}
-keep class com.jme3.scene.plugins.MTLLoader{*;}
-keep class com.jme3.scene.plugins.ogre.MeshLoader{*;}
-keep class com.jme3.scene.plugins.ogre.SkeletonLoader{*;}
-keep class com.jme3.scene.plugins.ogre.MaterialLoader{*;}
-keep class com.jme3.scene.plugins.ogre.SceneLoader{*;}
-keep class com.jme3.scene.plugins.blender.BlenderModelLoader{*;}
-keep class com.jme3.shader.plugins.GLSLLoader{*;}

-keep class org.lwjgl.openal.EFX10{*;}

# End support for jme3
##########################################################


-keep class * extends javax.swing.plaf.ComponentUI {
    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
}

#Necessary for JMol-phet classes if you are using PDB file content, as is currently done in Sugar and Salt Solutions
-keep class org.jmol.adapter.readers.cifpdb.PdbReader{
<fields>;
<methods>;
}

##########
# SimSharing features that use Akka
# TODO: move this to simsharing build file somehow

-keep public class akka.remote.**{
    <fields>;
    <methods>;
}

-keep public class akka.event.**{
    <fields>;
    <methods>;
}

-keep public class akka.serialization.**{
    <fields>;
    <methods>;
}

-keep class com.google.protobuf.**{
    <fields>;
    <methods>;
}

-keep class akka.actor.**{
    <fields>;
    <methods>;
}

-keep class edu.colorado.phet.simsharing.**{
    <fields>;
    <methods>;
}

-keep class ch.qos.logback.core.rolling.RollingFileAppender{
    <fields>;
    <methods>;
}

-keep class ch.qos.logback.core.rolling.TimeBasedRollingPolicy{
    <fields>;
    <methods>;
}

-keep class org.slf4j.impl.StaticLoggerBinder{
    <fields>;
    <methods>;
}

-keep class com.thinktankmaths.logging.CustomFormatter{
    <fields>;
    <methods>;
}

#
##############
# JMol (test build-a-molecule if this is changed)

# we actually don't need other things under org.jmol.shape, so leave this as-is
-keep class org.jmol.adapter.readers.xml.XmlCmlReader
-keep class org.jmol.shape.Balls
-keep class org.jmol.shape.BallsRenderer
-keep class org.jmol.shape.Sticks
-keep class org.jmol.shape.SticksRenderer
-keep class org.jmol.shape.Frank
-keep class org.jmol.shape.FrankRenderer
-keep class org.jmol.shape.Uccage
-keep class org.jmol.shape.UccageRenderer
-keep class org.jmol.shape.Bbcage
-keep class org.jmol.shape.BbcageRenderer
-keep class org.jmol.shape.Measures
-keep class org.jmol.shape.MeasuresRenderer

##############

#JMonkeyengine dependencies
#TODO: factor out to project properties files
-keep class com.jmex.awt.input.AWTKeyInput
-keep class com.jmex.physics.impl.ode.OdePhysicsSpace$OdeFactory

# Also keep - Enumerations. Keep a method that is required in enumeration
# classes.
-keepclassmembers class * extends java.lang.Enum {
    public **[] values();
}

-keep public class edu.colorado.phet.common.util.services.**{
    public protected *;
}

-keep public class javax.jnlp.**{
    public protected *;
}

-keep public class bsh.**{
    public protected *;
}

-keep public class gnu.xml.aelfred2.**{
    public protected *;
}

-keep public class javax.xml.parsers.**{
    public protected *;
}

-keep class org.jfree.chart.resources.JFreeChartResources

-keep class org.jfree.resources.JCommonResources

-keep class org.jfree.ui.about.Licenses

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep names - _class method names. Keep all .class method names. Useful for
# libraries that will be obfuscated again.
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String,boolean);
}

-keepnames class org.jfree.ui.about.Licenses

# Remove - System method calls. Remove all invocations of System
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.System {
    public static long currentTimeMillis();
    static java.lang.Class getCallerClass();
    public static int identityHashCode(java.lang.Object);
    public static java.lang.SecurityManager getSecurityManager();
    public static java.util.Properties getProperties();
    public static java.lang.String getProperty(java.lang.String);
    public static java.lang.String getenv(java.lang.String);
    public static java.lang.String mapLibraryName(java.lang.String);
    public static java.lang.String getProperty(java.lang.String,java.lang.String);
}

# Remove - Math method calls. Remove all invocations of Math
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.Math {
    public static double sin(double);
    public static double cos(double);
    public static double tan(double);
    public static double asin(double);
    public static double acos(double);
    public static double atan(double);
    public static double toRadians(double);
    public static double toDegrees(double);
    public static double exp(double);
    public static double log(double);
    public static double log10(double);
    public static double sqrt(double);
    public static double cbrt(double);
    public static double IEEEremainder(double,double);
    public static double ceil(double);
    public static double floor(double);
    public static double rint(double);
    public static double atan2(double,double);
    public static double pow(double,double);
    public static int round(float);
    public static long round(double);
    public static double random();
    public static int abs(int);
    public static long abs(long);
    public static float abs(float);
    public static double abs(double);
    public static int max(int,int);
    public static long max(long,long);
    public static float max(float,float);
    public static double max(double,double);
    public static int min(int,int);
    public static long min(long,long);
    public static float min(float,float);
    public static double min(double,double);
    public static double ulp(double);
    public static float ulp(float);
    public static double signum(double);
    public static float signum(float);
    public static double sinh(double);
    public static double cosh(double);
    public static double tanh(double);
    public static double hypot(double,double);
    public static double expm1(double);
    public static double log1p(double);
}

# Remove - Number method calls. Remove all invocations of Number
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.* extends java.lang.Number {
    public static java.lang.String toString(byte);
    public static java.lang.Byte valueOf(byte);
    public static byte parseByte(java.lang.String);
    public static byte parseByte(java.lang.String,int);
    public static java.lang.Byte valueOf(java.lang.String,int);
    public static java.lang.Byte valueOf(java.lang.String);
    public static java.lang.Byte decode(java.lang.String);
    public int compareTo(java.lang.Byte);
    public static java.lang.String toString(short);
    public static short parseShort(java.lang.String);
    public static short parseShort(java.lang.String,int);
    public static java.lang.Short valueOf(java.lang.String,int);
    public static java.lang.Short valueOf(java.lang.String);
    public static java.lang.Short valueOf(short);
    public static java.lang.Short decode(java.lang.String);
    public static short reverseBytes(short);
    public int compareTo(java.lang.Short);
    public static java.lang.String toString(int,int);
    public static java.lang.String toHexString(int);
    public static java.lang.String toOctalString(int);
    public static java.lang.String toBinaryString(int);
    public static java.lang.String toString(int);
    public static int parseInt(java.lang.String,int);
    public static int parseInt(java.lang.String);
    public static java.lang.Integer valueOf(java.lang.String,int);
    public static java.lang.Integer valueOf(java.lang.String);
    public static java.lang.Integer valueOf(int);
    public static java.lang.Integer getInteger(java.lang.String);
    public static java.lang.Integer getInteger(java.lang.String,int);
    public static java.lang.Integer getInteger(java.lang.String,java.lang.Integer);
    public static java.lang.Integer decode(java.lang.String);
    public static int highestOneBit(int);
    public static int lowestOneBit(int);
    public static int numberOfLeadingZeros(int);
    public static int numberOfTrailingZeros(int);
    public static int bitCount(int);
    public static int rotateLeft(int,int);
    public static int rotateRight(int,int);
    public static int reverse(int);
    public static int signum(int);
    public static int reverseBytes(int);
    public int compareTo(java.lang.Integer);
    public static java.lang.String toString(long,int);
    public static java.lang.String toHexString(long);
    public static java.lang.String toOctalString(long);
    public static java.lang.String toBinaryString(long);
    public static java.lang.String toString(long);
    public static long parseLong(java.lang.String,int);
    public static long parseLong(java.lang.String);
    public static java.lang.Long valueOf(java.lang.String,int);
    public static java.lang.Long valueOf(java.lang.String);
    public static java.lang.Long valueOf(long);
    public static java.lang.Long decode(java.lang.String);
    public static java.lang.Long getLong(java.lang.String);
    public static java.lang.Long getLong(java.lang.String,long);
    public static java.lang.Long getLong(java.lang.String,java.lang.Long);
    public static long highestOneBit(long);
    public static long lowestOneBit(long);
    public static int numberOfLeadingZeros(long);
    public static int numberOfTrailingZeros(long);
    public static int bitCount(long);
    public static long rotateLeft(long,int);
    public static long rotateRight(long,int);
    public static long reverse(long);
    public static int signum(long);
    public static long reverseBytes(long);
    public int compareTo(java.lang.Long);
    public static java.lang.String toString(float);
    public static java.lang.String toHexString(float);
    public static java.lang.Float valueOf(java.lang.String);
    public static java.lang.Float valueOf(float);
    public static float parseFloat(java.lang.String);
    public static boolean isNaN(float);
    public static boolean isInfinite(float);
    public static int floatToIntBits(float);
    public static int floatToRawIntBits(float);
    public static float intBitsToFloat(int);
    public static int compare(float,float);
    public boolean isNaN();
    public boolean isInfinite();
    public int compareTo(java.lang.Float);
    public static java.lang.String toString(double);
    public static java.lang.String toHexString(double);
    public static java.lang.Double valueOf(java.lang.String);
    public static java.lang.Double valueOf(double);
    public static double parseDouble(java.lang.String);
    public static boolean isNaN(double);
    public static boolean isInfinite(double);
    public static long doubleToLongBits(double);
    public static long doubleToRawLongBits(double);
    public static double longBitsToDouble(long);
    public static int compare(double,double);
    public boolean isNaN();
    public boolean isInfinite();
    public int compareTo(java.lang.Double);
    public <init>(byte);
    public <init>(short);
    public <init>(int);
    public <init>(long);
    public <init>(float);
    public <init>(double);
    public <init>(java.lang.String);
    public byte byteValue();
    public short shortValue();
    public int intValue();
    public long longValue();
    public float floatValue();
    public double doubleValue();
    public int compareTo(java.lang.Object);
    public boolean equals(java.lang.Object);
    public int hashCode();
    public java.lang.String toString();
}

# Remove - String method calls. Remove all invocations of String
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.String {
    public <init>();
    public <init>(byte[]);
    public <init>(byte[],int);
    public <init>(byte[],int,int);
    public <init>(byte[],int,int,int);
    public <init>(byte[],int,int,java.lang.String);
    public <init>(byte[],java.lang.String);
    public <init>(char[]);
    public <init>(char[],int,int);
    public <init>(java.lang.String);
    public <init>(java.lang.StringBuffer);
    public static java.lang.String copyValueOf(char[]);
    public static java.lang.String copyValueOf(char[],int,int);
    public static java.lang.String valueOf(boolean);
    public static java.lang.String valueOf(char);
    public static java.lang.String valueOf(char[]);
    public static java.lang.String valueOf(char[],int,int);
    public static java.lang.String valueOf(double);
    public static java.lang.String valueOf(float);
    public static java.lang.String valueOf(int);
    public static java.lang.String valueOf(java.lang.Object);
    public static java.lang.String valueOf(long);
    public boolean contentEquals(java.lang.StringBuffer);
    public boolean endsWith(java.lang.String);
    public boolean equalsIgnoreCase(java.lang.String);
    public boolean equals(java.lang.Object);
    public boolean matches(java.lang.String);
    public boolean regionMatches(boolean,int,java.lang.String,int,int);
    public boolean regionMatches(int,java.lang.String,int,int);
    public boolean startsWith(java.lang.String);
    public boolean startsWith(java.lang.String,int);
    public byte[] getBytes();
    public byte[] getBytes(java.lang.String);
    public char charAt(int);
    public char[] toCharArray();
    public int compareToIgnoreCase(java.lang.String);
    public int compareTo(java.lang.Object);
    public int compareTo(java.lang.String);
    public int hashCode();
    public int indexOf(int);
    public int indexOf(int,int);
    public int indexOf(java.lang.String);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(int);
    public int lastIndexOf(int,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.CharSequence subSequence(int,int);
    public java.lang.String concat(java.lang.String);
    public java.lang.String replaceAll(java.lang.String,java.lang.String);
    public java.lang.String replace(char,char);
    public java.lang.String replaceFirst(java.lang.String,java.lang.String);
    public java.lang.String[] split(java.lang.String);
    public java.lang.String[] split(java.lang.String,int);
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
    public java.lang.String toLowerCase();
    public java.lang.String toLowerCase(java.util.Locale);
    public java.lang.String toString();
    public java.lang.String toUpperCase();
    public java.lang.String toUpperCase(java.util.Locale);
    public java.lang.String trim();
}

# Remove - StringBuffer method calls. Remove all invocations of
# StringBuffer methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.StringBuffer {
    public <init>();
    public <init>(int);
    public <init>(java.lang.String);
    public <init>(java.lang.CharSequence);
    public java.lang.String toString();
    public char charAt(int);
    public int capacity();
    public int codePointAt(int);
    public int codePointBefore(int);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
}

# Remove - StringBuilder method calls. Remove all invocations of
# StringBuilder methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.StringBuilder {
    public <init>();
    public <init>(int);
    public <init>(java.lang.String);
    public <init>(java.lang.CharSequence);
    public java.lang.String toString();
    public char charAt(int);
    public int capacity();
    public int codePointAt(int);
    public int codePointBefore(int);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
}
