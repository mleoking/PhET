package edu.colorado.phet.javaversionchecker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;


/**
 * This file checks on the Java version, if it is too low, then an dialog is shown, otherwise the application launches.
 * This file can only use code and be compiled in the lowest common denominator API (currently Java 1.4)
 */
public class JavaVersionChecker {
    public static void main( String[] args ) throws IOException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        String javaVersion = System.getProperty( "java.version" );
        if ( javaVersion.startsWith( "1.4" ) || javaVersion.startsWith( "1.3" ) || javaVersion.startsWith( "1.2" ) || javaVersion.startsWith( "1.1" ) ) {
            showJavaVersionErrorDialog();
        }
        else {
            launchApplication( args );
        }
    }

    private static void launchApplication( String[] args ) throws ClassNotFoundException, InvocationTargetException, IOException, NoSuchMethodException, IllegalAccessException {

        //access through reflection so this class can be compiled independently of anything
        Class c = Class.forName( "edu.colorado.phet.common.phetcommon.application.JARLauncher" );

        Method method = c.getMethod( "main", new Class[]{args.getClass()} );
        method.invoke( null, new Object[]{args} );
    }

    private static void showJavaVersionErrorDialog() throws IOException {


        String version = System.getProperty( "java.version" );
        StringTokenizer st = new StringTokenizer( version, "." );
        int major = Integer.parseInt( st.nextToken() );
        int minor = Integer.parseInt( st.nextToken() );

        String html = "<html>PhET requires Java 1.5 or higher.<br>" +
                      "You have Java " + major + "." + minor + ".<br>" +
                      "<br>" +
                      "Please visit <a href=\"http://www.java.com\">www.java.com</a> to get the latest version of Java.</html>";
        JEditorPane jEditorPane = new JEditorPane( "text/html", html );

        jEditorPane.setEditorKit( new HTMLEditorKit() );
        jEditorPane.setText( html );
        jEditorPane.setEditable( false );
        jEditorPane.setBackground( new JPanel().getBackground() );
        jEditorPane.addHyperlinkListener( new HyperlinkListener() {
            public void hyperlinkUpdate( HyperlinkEvent event ) {
                if ( event.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                    try {
                        getBasicService().showDocument( event.getURL() );
                    }
                    catch( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );
        JFrame frame = new JFrame( "Java Version" );
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( jEditorPane, BorderLayout.CENTER );
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton( "Exit" );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        buttonPanel.add( okButton );
        contentPane.add( buttonPanel, BorderLayout.SOUTH );
        contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        frame.setContentPane( contentPane );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        frame.setLocation( (int) ( screenSize.getWidth() / 2 - frame.getWidth() / 2 ),
                           (int) ( screenSize.getHeight() / 2 - frame.getHeight() / 2 ) );
        frame.setVisible( true );
    }

    public static boolean isJavaWebStart() {
        return System.getProperty( "javawebstart.version" ) != null;
    }

    public static BasicService getBasicService() throws Exception {
        if ( isJavaWebStart() ) {
            return (BasicService) ServiceManager.lookup( BasicService.class.getName() );
        }
        else {
            return new LocalBasicService();
        }
    }

    private static class LocalBasicService implements BasicService {

        public boolean showDocument( URL url ) {
            BrowserControl.displayURL( url.toExternalForm() );
            return true;
        }

        public URL getCodeBase() {
            File f = File.listRoots()[0];
            try {
                return f.toURL();
            }
            catch( MalformedURLException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }

        public boolean isOffline() {
            return true;
        }

        public boolean isWebBrowserSupported() {
            return true;
        }

        public static class BrowserControl {
            public static void displayURL( String url ) {
                String cmd = null;
                try {
                    if ( getOperatingSystem() == OS_WINDOWS ) {
                        // cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
                        cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                        Process p = Runtime.getRuntime().exec( cmd );
                    }
                    else if ( getOperatingSystem() == OS_MACINTOSH ) {
                        cmd = "open " + url;
                        Process p = Runtime.getRuntime().exec( cmd );
                    }

                    else {
                        launchBrowserOnLinux( url );
                    }
                }
                catch( IOException x ) {
                    // couldn't exec browser
                    System.err.println( "Could not invoke browser, command=" + cmd );
                    System.err.println( "Caught: " + x );
                }
                catch( Exception e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            private static final String WIN_PATH = "rundll32";
            private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
            private static final String UNIX_PATH = "netscape";
            private static final String UNIX_FLAG = "-remote openURL";
        }

    }

    /**
     * Gets the operating system type.
     *
     * @return OS_WINDOWS, OS_MACINTOSH, or OS_OTHER
     */
    public static int getOperatingSystem() {

        // Get the operating system name.
        String osName = "";
        try {
            osName = System.getProperty( "os.name" ).toLowerCase();
        }
        catch( Throwable t ) {
            t.printStackTrace();
        }

        // Convert to one of the operating system constants.
        int os = OS_OTHER;
        if ( osName.indexOf( "windows" ) >= 0 ) {
            os = OS_WINDOWS;
        }
        else if ( osName.indexOf( "mac" ) >= 0 ) {
            os = OS_MACINTOSH;
        }

        return os;
    }

    public static final int OS_WINDOWS = 0;
    public static final int OS_MACINTOSH = 1;
    public static final int OS_OTHER = 2;

    //http://www.java2s.com/Code/Java/Development-Class/LaunchBrowserinMacLinuxUnix.htm
    public static void launchBrowserOnLinux( String url ) throws Exception, InterruptedException {
        String[] browsers = {
                "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "galeon", "iceweasel"};
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
