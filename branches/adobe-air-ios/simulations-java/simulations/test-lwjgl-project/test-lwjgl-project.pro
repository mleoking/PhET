-keep class com.jmex.awt.input.AWTKeyInput
-keep class com.jmex.physics.impl.ode.OdePhysicsSpace$OdeFactory

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

-keep class org.lwjgl.openal.EFX10{*;}