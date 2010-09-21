package edu.colorado.phet.flashlauncher.util;

//See http://www.centerkey.com/java/browser/

/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 1.5 (December 10, 2005)                    //
//  By Dem Pilafian                                    //
//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import java.lang.reflect.Method;

import javax.swing.*;

import edu.colorado.phet.flashlauncher.FlashLauncher;

public class BareBonesBrowserLaunch {

    private static final String errMsg = "Error attempting to launch web browser";

    public static void openURL( String url ) {
        String osName = System.getProperty( "os.name" );
        FlashLauncher.println( "osName = " + osName );
        try {
            if ( osName.startsWith( "Mac OS" ) ) {
                FlashLauncher.println( "Mac" );
                Class fileMgr = Class.forName( "com.apple.eio.FileManager" );
                FlashLauncher.println( "fileMgr = " + fileMgr );
                Method openURL = fileMgr.getDeclaredMethod( "openURL",
                                                            new Class[]{String.class} );
                FlashLauncher.println( "openURL = " + openURL );
                FlashLauncher.println( "url = " + url );
                openURL.invoke( null, new Object[]{url} );
                FlashLauncher.println( "invocation finished" );
            }
            else if ( osName.startsWith( "Windows" ) ) {
                Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + url );
            }
            else { //assume Unix or Linux
                String[] browsers = {
                        "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;
                for ( int count = 0; count < browsers.length && browser == null; count++ ) {
                    if ( Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]} ).waitFor() == 0 ) {
                        browser = browsers[count];
                    }
                }
                if ( browser == null ) {
                    throw new Exception( "Could not find web browser" );
                }
                else {
                    Runtime.getRuntime().exec( new String[]{browser, url} );
                }
            }
        }
        catch( Exception e ) {
            JOptionPane.showMessageDialog( null, errMsg + ":\n" + e.getLocalizedMessage() );
        }
    }

}

