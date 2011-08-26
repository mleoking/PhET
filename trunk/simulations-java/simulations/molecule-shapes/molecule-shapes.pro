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