package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

//import com.google.javascript.jscomp.CompilationLevel;
//import com.google.javascript.jscomp.CompilerOptions;
//import com.google.javascript.jscomp.JSSourceFile;

public class HTML5Project extends PhetProject {
    public HTML5Project( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public HTML5Project( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    @Override public Simulation getSimulation( final String simulationName, final Locale locale ) {
        return new Simulation( simulationName, simulationName, "<javascript>", new String[0], false );
    }

    @Override public Locale[] getLocales() {
        return new Locale[0];
    }

    @Override public File getLocalizationFile( final Locale locale ) {
        return null;
    }

    @Override public File getTranslationFile( final Locale locale ) {
        return null;
    }

    @Override protected File getTrunkAbsolute() {
        return getProjectDir().getParentFile().getParentFile().getParentFile();
    }

    @Override public String getAlternateMainClass() {
        return null;
    }

    @Override public String getProdServerDeployPath() {
        return null;
    }

    @Override public String getLaunchFileSuffix() {
        return "html";
    }

    @Override public boolean build() throws Exception {

        //TODO: Clear deploy dir?

        //Copy everything from source to destination
        for ( File sourceRoot : getSourceRoots() ) {
            FileUtils.copyRecursive( sourceRoot, getDeployDir() );
        }

        //Create zip and add to deploy directory for use with Ludei cloud compiler
        AutoZip.deleteOldZipAndCreateNewZip( getSourceRoots()[0], new File( getDeployDir(), "autozip.zip" ) );

//        File[] sourceRoots = getSourceRoots();
//        String text = "";
//        for ( File sourceRoot : sourceRoots ) {
//            text = text + "\n\n" + loadAllText( sourceRoot );
//        }
//        String compiled = compile( text );
//        FileUtils.writeString( new File(getDeployDir(),getName()+"-compiled.js") );
        return true;
    }

    private String loadAllText( final File f ) throws IOException {
        if ( f.isFile() ) {
            return FileUtils.loadFileAsString( f );
        }
        else {
            File[] files = f.listFiles( new FilenameFilter() {
                @Override public boolean accept( final File dir, final String name ) {
                    return name.endsWith( ".js" );
                }
            } );
            String text = "";
            for ( File sourceRoot : files ) {
                text = text + "\n\n" + loadAllText( sourceRoot );
            }
            return text;
        }
    }

    @Override public String getListDisplayName() {
        return null;
    }

    @Override public void runSim( final Locale locale, final String simulationName ) {
    }

    @Override public boolean isTestable() {
        return false;
    }

    /**
     * See http://blog.bolinfest.com/2009/11/calling-closure-compiler-from-java.html
     *
     * @param code JavaScript source code to compile.
     * @return The compiled version of the code.
     */
    public static String compile( String code ) {
        return code;
//        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
//
//        CompilerOptions options = new CompilerOptions();
//        // Advanced mode is used here, but additional options could be set, too.
//        CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel( options );
//
//        // To get the complete set of externs, the logic in
//        // CompilerRunner.getDefaultExterns() should be used here.
//        JSSourceFile extern = JSSourceFile.fromCode( "externs.js", "function alert(x) {}" );
//
//        // The dummy input name "input.js" is used here so that any warnings or
//        // errors will cite line numbers in terms of input.js.
//        JSSourceFile input = JSSourceFile.fromCode( "input.js", code );
//
//        // compile() returns a Result, but it is not needed here.
//        compiler.compile( extern, input, options );
//
//        // The compiler is responsible for generating the compiled code; it is not
//        // accessible via the Result.
//        return compiler.toSource();
    }

    public static void main( String[] args ) {
        String compiledCode = compile( "function hello(name) {" +
                                       "alert('Hello, ' + name);" +
                                       "}" +
                                       "hello('New user');" );
        System.out.println( compiledCode );
    }

    public static HTML5Project[] getProjects( final File trunk ) {
        File[] html5SimDir = new File( trunk, "simulations-html/simulations" ).listFiles( new FileFilter() {
            public boolean accept( final File pathname ) {
                return pathname.isDirectory() && !pathname.getName().toLowerCase().equals( ".svn" );
            }
        } );
        HTML5Project[] x = new HTML5Project[html5SimDir.length];
        for ( int i = 0; i < html5SimDir.length; i++ ) {
            File file = html5SimDir[i];
            try {
                x[i] = new HTML5Project( file );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return x;
    }
}