package edu.colorado.phet.flashlauncher.util;

/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 3.1 (June 6, 2010)                         //
//  By Dem Pilafian                                    //
//  Supports:                                          //
//     Mac OS X, GNU/Linux, Unix, Windows XP/Vista/7   //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.flashlauncher.FlashLauncher;

public class BareBonesBrowserLaunch {

    static final String[] browsers = {"google-chrome", "firefox", "opera",
            "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla"};
    static final String errMsg = "Error attempting to launch web browser";

    public static void openURL( String url ) {
        FlashLauncher.println( "Starting OpenURL..." );
        try {  //attempt to use Desktop library from JDK 1.6+
            Class d = Class.forName( "java.awt.Desktop" );
            FlashLauncher.println( "found class " + d );
            d.getDeclaredMethod( "browse", new Class[]{java.net.URI.class} ).invoke(
                    d.getDeclaredMethod( "getDesktop", null ).invoke( null,null ),
                    new Object[]{java.net.URI.create( url )} );
            //above code mimicks:  java.awt.Desktop.getDesktop().browse()
        }
        catch( Exception ignore ) {  //library not available or failed
            String osName = System.getProperty( "os.name" );
            try {
                if ( osName.startsWith( "Mac OS" ) ) {
                    Method method = Class.forName( "com.apple.eio.FileManager" ).getDeclaredMethod(
                            "openURL", new Class[]{String.class} );
                    FlashLauncher.println( "found OSX Method" + method );
                    method.invoke( null,
                                   new Object[]{url} );
                }
                else if ( osName.startsWith( "Windows" ) ) {
                    FlashLauncher.println( "Starting browser on Windows" );
                    Runtime.getRuntime().exec(
                            "rundll32 url.dll,FileProtocolHandler " + url );
                }
                else { //assume Unix or Linux
                    FlashLauncher.println( "Starting browser on Unix/Linux" );
                    String browser = null;
                    for ( int i = 0; i < browsers.length; i++ ) {
                        String b = browsers[i];
                        if ( browser == null && Runtime.getRuntime().exec( new String[]
                                                                                   {"which", b} ).getInputStream().read() != -1 ) {
                            Runtime.getRuntime().exec( new String[]{browser = b, url} );
                        }
                    }
                    if ( browser == null ) { throw new Exception( Arrays.toString( browsers ) ); }
                }
            }
            catch( Exception e ) {
                JOptionPane.showMessageDialog( null, errMsg + "\n" + e.toString() );
            }
        }
    }

}