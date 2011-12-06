// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.scripts.git;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Move locally created git repositories to Unfuddle
 *
 * @author Sam Reid
 */
public class CreateUnfuddleRepos {
    String s = "C:\\workingcopy\\phet\\svn\\trunk\\build-tools\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flash\\common-as3\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flash\\contrib\\aswing-a3\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flash\\contrib\\box2d\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flash\\flash-launcher\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flash\\simulations\\calculus-grapher\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flash\\simulations\\charges-and-fields\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flash\\simulations\\collision-lab\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flex\\simulations\\charges-and-fields-flex\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flex\\simulations\\density-and-buoyancy\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flex\\simulations\\normal-modes\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-flex\\simulations\\resonance\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\charts\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\chemistry\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\collision\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\controls\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\games\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\java-version-checker\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\jfreechart-phet\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\jme-phet\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\jmol-phet\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\mechanics\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\motion\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\phetcommon\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\phetgraphics\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\photon-absorption\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\piccolo-phet\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\quantum\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\record-and-playback\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\scala-common\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\spline\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\common\\timeseries\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\apple\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\beanshell\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\functionaljava\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\functionaljava\\demo\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\jade\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\Jama\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\jbox2d\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\jfreechart\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\jme3\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\jmol\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\JSci\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\log4j\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\lwjgl\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\piccolo2d\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\poly2tri-core\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\slf4j\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\think-tank-maths\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\acid-base-solutions\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\advanced-acid-base-solutions\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\balance-and-torque\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\balancing-chemical-equations\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\balloons\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\battery-resistor-circuit\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\battery-voltage\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\bending-light\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\bound-states\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\build-a-molecule\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\build-an-atom\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\capacitor-lab\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\charges-and-fields-scala\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\circuit-construction-kit\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\color-vision\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\conductivity\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\dilutions\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\discharge-lamps\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\eating-and-exercise\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\efield\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\electric-hockey\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\energy-skate-park\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\faraday\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\fluid-pressure-and-flow\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\force-law-lab\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\forces-1d\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\fourier\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\fractions\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\gene-expression-basics\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\gene-network\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\glaciers\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\gravity-and-orbits\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\greenhouse\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\hydrogen-atom\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\ideal-gas\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\inside-magnets\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\java-common-strings\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\ladybug-motion-2d\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\lasers\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\maze-game\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\membrane-channels\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\microwaves\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\modes-example\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\molecule-polarity\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\molecule-shapes\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\molecules-and-light\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\motion-2d\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\motion-series\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\moving-man\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\mri\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\mvc-example\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\natural-selection\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\neuron\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\nuclear-physics\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\optical-quantum-control\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\optical-tweezers\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\ph-scale\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\phetgraphics-demo\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\photoelectric\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\plate-tectonics\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\quantum-tunneling\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\quantum-wave-interference\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\radio-waves\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\reactants-products-and-leftovers\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\reactions-and-rates\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\rotation\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\rutherford-scattering\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\self-driven-particle-model\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\semiconductor\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\signal-circuit\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\simsharing-test-sim\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\soluble-salts\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\sound\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\states-of-matter\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\sugar-and-salt-solutions\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\test-java-project\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\test-lwjgl-project\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\the-ramp\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\titration\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\travoltage\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\wave-interference\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\work-energy\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\team\\cmalley\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\team\\jblanco\\experiments\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\team\\jolson\\utilities\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\team\\reids\\scenic\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\licensing\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\localization-strings\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\phet-updater\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\simsharing\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\simsharing-analysis\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\simsharing-cassandra\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\testing\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\timesheet\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\translation-strings-diff\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\translation-utility\n" +
               "C:\\workingcopy\\phet\\svn\\trunk\\util\\unfuddle";

    public static void main( String[] args ) {
        new CreateUnfuddleRepos().start();
    }

    public static final String template = "curl -i -u samreid:Em3beU42 -X POST \\\n" +
                                          "  -H 'Accept: application/xml' \\\n" +
                                          "  -H 'Content-Type: application/xml' \\\n" +
                                          "  -d \"<repository><abbreviation>fractions</abbreviation><title>fractions</title><system>git</system><projects><project id='9404'/></projects></repository>\" \\\n" +
                                          "  'https://phet.unfuddle.com/api/v1/repositories'\n" +
                                          "cd ~/Desktop/test4/svn2git/checkouts/\n" +
                                          "git clone ~/Desktop/test4/svn2git/repos/fractions\n" +
                                          "cd fractions\n" +
                                          "git remote add unfuddle git@phet.unfuddle.com:phet/fractions.git\n" +
                                          "git config remote.unfuddle.push refs/heads/master:refs/heads/master\n" +
                                          "git push unfuddle master";

    private void start() {
        ArrayList<File> files = getFiles();
        System.out.println( "files.size() = " + files.size() );
//        System.out.println( "hashset.size() = " + names.size() );

        for ( File file : files ) {
            System.out.println( "# " + file.getAbsolutePath() + ", " + file.getAbsolutePath() );
            String replaced = FileUtils.replaceAll( template, "fractions", file.getName() );
            System.out.println( replaced );
            System.out.println();
        }
    }

    public ArrayList<File> getFiles() {
        StringTokenizer st = new StringTokenizer( s, "\n" );
        ArrayList<File> files = new ArrayList<File>();
//        HashSet<String> names = new HashSet<String>();
        while ( st.hasMoreTokens() ) {
            String next = st.nextToken();
            File file = new File( next );
            files.add( file );

//            names.add( file.getName() );
        }
        return files;
    }
}
