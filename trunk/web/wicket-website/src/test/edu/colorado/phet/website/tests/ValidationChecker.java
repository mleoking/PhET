package edu.colorado.phet.website.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Checks w3c validation of pages
 */
public class ValidationChecker {

    public static boolean isValid( String uri ) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL( "http://validator.w3.org/check?uri=" + URLEncoder.encode( uri, "UTF-8" ) );
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod( "GET" );
            conn.setReadTimeout( 30000 );
            conn.setInstanceFollowRedirects( false );
            conn.setDoInput( true );
            conn.connect();

            BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
            try {
                String line;
                while ( ( line = reader.readLine() ) != null ) {
                    if ( line.trim().equals( "[Valid]" ) ) {
                        return true;
                    }
                    if ( line.trim().equals( "[Invalid]" ) ) {
                        return false;
                    }
                }
            }
            finally {
                reader.close();
            }
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        finally {
            if ( conn != null ) {
                conn.disconnect();
            }
        }
        return false;
    }

    public static void check( String uri ) {
        boolean valid = isValid( uri );
        if ( valid ) {
            //System.out.println( "OK\t" + uri );
        }
        else {
            System.out.println( "BAD!\t" + uri );
        }
    }

    public static void main( String[] args ) {
        check( "http://phetsims.colorado.edu" );
        check( "http://phetsims.colorado.edu/ar/" );
        check( "http://phetsims.colorado.edu/ar/about" );
        check( "http://phetsims.colorado.edu/ar/about/contact" );
        check( "http://phetsims.colorado.edu/ar/about/news" );
        check( "http://phetsims.colorado.edu/ar/about/sponsors" );
        check( "http://phetsims.colorado.edu/ar/contributions/view/2708" );
        check( "http://phetsims.colorado.edu/ar/donate" );
        check( "http://phetsims.colorado.edu/ar/for-teachers" );
        check( "http://phetsims.colorado.edu/ar/for-teachers/browse-activities" );
        check( "http://phetsims.colorado.edu/ar/for-teachers/submit-activity" );
        check( "http://phetsims.colorado.edu/ar/for-teachers/workshops" );
        check( "http://phetsims.colorado.edu/ar/for-translators" );
        check( "http://phetsims.colorado.edu/ar/for-translators/translation-utility" );
        check( "http://phetsims.colorado.edu/ar/get-phet" );
        check( "http://phetsims.colorado.edu/ar/get-phet/full-install" );
        check( "http://phetsims.colorado.edu/ar/get-phet/one-at-a-time" );
        check( "http://phetsims.colorado.edu/ar/#main-content" );
        check( "http://phetsims.colorado.edu/ar/research" );
        check( "http://phetsims.colorado.edu/ar_SA/" );
        check( "http://phetsims.colorado.edu/ar_SA/about" );
        check( "http://phetsims.colorado.edu/ar_SA/about/contact" );
        check( "http://phetsims.colorado.edu/ar_SA/about/licensing" );
        check( "http://phetsims.colorado.edu/ar_SA/about/#main-content" );
        check( "http://phetsims.colorado.edu/ar_SA/about/news" );
        check( "http://phetsims.colorado.edu/ar_SA/about/source-code" );
        check( "http://phetsims.colorado.edu/ar_SA/about/sponsors" );
        check( "http://phetsims.colorado.edu/ar_SA/contributions/view/2708" );
        check( "http://phetsims.colorado.edu/ar_SA/donate" );
        check( "http://phetsims.colorado.edu/ar_SA/for-teachers" );
        check( "http://phetsims.colorado.edu/ar_SA/for-teachers/browse-activities" );
        check( "http://phetsims.colorado.edu/ar_SA/for-teachers/legend" );
        check( "http://phetsims.colorado.edu/ar_SA/for-teachers/submit-activity" );
        check( "http://phetsims.colorado.edu/ar_SA/for-teachers/workshops" );
        check( "http://phetsims.colorado.edu/ar_SA/for-translators" );
        check( "http://phetsims.colorado.edu/ar_SA/for-translators/translation-utility" );
        check( "http://phetsims.colorado.edu/ar_SA/get-phet" );
        check( "http://phetsims.colorado.edu/ar_SA/get-phet/full-install" );
        check( "http://phetsims.colorado.edu/ar_SA/get-phet/one-at-a-time" );
        check( "http://phetsims.colorado.edu/ar_SA/#main-content" );
        check( "http://phetsims.colorado.edu/ar_SA/research" );
        check( "http://phetsims.colorado.edu/ar_SA/sign-in?dest=%2F" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/aphid-maze" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/circuit-construction-kit-ac" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/curve-fitting" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/discharge-lamps" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/electric-hockey" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/forces-1d" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/magnets-and-electromagnets" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/mass-spring-lab" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/maze-game" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/my-solar-system" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/rotation" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/category/by-level" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/category/by-level/university" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/category/chemistry" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/category/physics/electricity-magnets-and-circuits" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/signal-circuit" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/index" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/index.php" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/keyword/conservationOfEnergy" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/keyword/force" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/keyword/springs" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/keyword/thermalEnergy" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/#software-requirements" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/translated" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/translated/ar" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/translated/fr" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/translated/hr" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/translated/vi" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/translated/zh_CN" );
        check( "http://phetsims.colorado.edu/ar_SA/simulations/translated/zh_TW" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/#teaching-ideas" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/#translated-versions" );
        check( "http://phetsims.colorado.edu/ar_SA/simulation/travoltage" );
        check( "http://phetsims.colorado.edu/ar_SA/troubleshooting" );
        check( "http://phetsims.colorado.edu/ar_SA/?wicket:bookmarkablePage=:edu.colorado.phet.website.authentication.SignInPage" );
        check( "http://phetsims.colorado.edu/ar/sign-in?dest=%2F" );
        check( "http://phetsims.colorado.edu/ar/sign-in?dest=%2Far%2F" );
        check( "http://phetsims.colorado.edu/ar/simulation/aphid-maze" );
        check( "http://phetsims.colorado.edu/ar/simulation/balloons-and-buoyancy" );
        check( "http://phetsims.colorado.edu/ar/simulation/circuit-construction-kit-ac" );
        check( "http://phetsims.colorado.edu/ar/simulation/curve-fitting" );
        check( "http://phetsims.colorado.edu/ar/simulation/discharge-lamps" );
        check( "http://phetsims.colorado.edu/ar/simulation/forces-1d" );
        check( "http://phetsims.colorado.edu/ar/simulation/magnets-and-electromagnets" );
        check( "http://phetsims.colorado.edu/ar/simulation/mass-spring-lab" );
        check( "http://phetsims.colorado.edu/ar/simulation/my-solar-system" );
        check( "http://phetsims.colorado.edu/ar/simulation/rotation" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/by-level" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/by-level/elementary-school" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/by-level/university" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/math/applications" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/new" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/physics/electricity-magnets-and-circuits" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/physics/heat-and-thermodynamics" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/physics/motion" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/physics/sound-and-waves" );
        check( "http://phetsims.colorado.edu/ar/simulations/category/physics/work-energy-and-power" );
        check( "http://phetsims.colorado.edu/ar/simulation/signal-circuit" );
        check( "http://phetsims.colorado.edu/ar/simulations/index.php" );
        check( "http://phetsims.colorado.edu/ar/simulations/keyword/force" );
        check( "http://phetsims.colorado.edu/ar/simulations/keyword/harmonicMotion" );
        check( "http://phetsims.colorado.edu/ar/simulations/keyword/springs" );
        check( "http://phetsims.colorado.edu/ar/simulations/keyword/thermalEnergy" );
        check( "http://phetsims.colorado.edu/ar/simulations/translated/ar" );
        check( "http://phetsims.colorado.edu/ar/simulations/translated/es" );
        check( "http://phetsims.colorado.edu/ar/simulations/translated/vi" );
        check( "http://phetsims.colorado.edu/ar/simulations/translated/zh_CN" );
        check( "http://phetsims.colorado.edu/ar/simulations/translated/zh_TW" );
        check( "http://phetsims.colorado.edu/ar/simulation/#translated-versions" );
        check( "http://phetsims.colorado.edu/ar/simulation/travoltage" );
        check( "http://phetsims.colorado.edu/ar/?wicket:bookmarkablePage=:edu.colorado.phet.website.authentication.SignInPage" );
        check( "http://phetsims.colorado.edu/en/about" );
        check( "http://phetsims.colorado.edu/en/about/contact" );
        check( "http://phetsims.colorado.edu/en/about/licensing" );
        check( "http://phetsims.colorado.edu/en/about/#main-content" );
        check( "http://phetsims.colorado.edu/en/about/news" );
        check( "http://phetsims.colorado.edu/en/about/source-code" );
        check( "http://phetsims.colorado.edu/en/about/sponsors" );
        check( "http://phetsims.colorado.edu/en/contributions/view/2708" );
        check( "http://phetsims.colorado.edu/en/contributions/view/#main-content" );
        check( "http://phetsims.colorado.edu/en/donate" );
        check( "http://phetsims.colorado.edu/en/for-teachers" );
        check( "http://phetsims.colorado.edu/en/for-teachers/activity-guide" );
        check( "http://phetsims.colorado.edu/en/for-teachers/browse-activities" );
        check( "http://phetsims.colorado.edu/en/for-teachers/legend" );
        check( "http://phetsims.colorado.edu/en/for-teachers/manage-activities" );
        check( "http://phetsims.colorado.edu/en/for-teachers/submit-activity" );
        check( "http://phetsims.colorado.edu/en/for-teachers/workshops" );
        check( "http://phetsims.colorado.edu/en/for-translators" );
        check( "http://phetsims.colorado.edu/en/for-translators/#main-content" );
        check( "http://phetsims.colorado.edu/en/for-translators/translation-utility" );
        check( "http://phetsims.colorado.edu/en/for-translators/website" );
        check( "http://phetsims.colorado.edu/en/get-phet" );
        check( "http://phetsims.colorado.edu/en/get-phet/full-install" );
        check( "http://phetsims.colorado.edu/en/get-phet/#main-content" );
        check( "http://phetsims.colorado.edu/en/get-phet/one-at-a-time" );
        check( "http://phetsims.colorado.edu/en/#main-content" );
        check( "http://phetsims.colorado.edu/en/#pub_1" );
        check( "http://phetsims.colorado.edu/en/register?dest=/" );
        check( "http://phetsims.colorado.edu/en/research" );
        check( "http://phetsims.colorado.edu/en/search?q=Energy" );
        check( "http://phetsims.colorado.edu/en/search?q=Energy+Skate+Park" );
        check( "http://phetsims.colorado.edu/en/search?q=Equations" );
        check( "http://phetsims.colorado.edu/en/search?q=Equilibrium" );
        check( "http://phetsims.colorado.edu/en/search?q=Estimation" );
        check( "http://phetsims.colorado.edu/en/sign-in?dest=%2F" );
        check( "http://phetsims.colorado.edu/en/simulation/alpha-decay" );
        check( "http://phetsims.colorado.edu/en/simulation/aphid-maze" );
        check( "http://phetsims.colorado.edu/en/simulation/aphid-maze/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/arithmetic" );
        check( "http://phetsims.colorado.edu/en/simulation/atomic-interactions" );
        check( "http://phetsims.colorado.edu/en/simulation/balloons" );
        check( "http://phetsims.colorado.edu/en/simulation/balloons-and-buoyancy" );
        check( "http://phetsims.colorado.edu/en/simulation/balloons-and-buoyancy/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/band-structure" );
        check( "http://phetsims.colorado.edu/en/simulation/battery-resistor-circuit" );
        check( "http://phetsims.colorado.edu/en/simulation/battery-voltage" );
        check( "http://phetsims.colorado.edu/en/simulation/beta-decay" );
        check( "http://phetsims.colorado.edu/en/simulation/blackbody-spectrum" );
        check( "http://phetsims.colorado.edu/en/simulation/bound-states" );
        check( "http://phetsims.colorado.edu/en/simulation/calculus-grapher" );
        check( "http://phetsims.colorado.edu/en/simulation/charges-and-fields" );
        check( "http://phetsims.colorado.edu/en/simulation/circuit-construction-kit-ac" );
        check( "http://phetsims.colorado.edu/en/simulation/circuit-construction-kit-ac/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/circuit-construction-kit-dc" );
        check( "http://phetsims.colorado.edu/en/simulation/color-vision" );
        check( "http://phetsims.colorado.edu/en/simulation/conductivity" );
        check( "http://phetsims.colorado.edu/en/simulation/#content" );
        check( "http://phetsims.colorado.edu/en/simulation/covalent-bonds" );
        check( "http://phetsims.colorado.edu/en/simulation/#credits" );
        check( "http://phetsims.colorado.edu/en/simulation/curve-fitting" );
        check( "http://phetsims.colorado.edu/en/simulation/curve-fitting/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/davisson-germer" );
        check( "http://phetsims.colorado.edu/en/simulation/discharge-lamps" );
        check( "http://phetsims.colorado.edu/en/simulation/discharge-lamps/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/eating-and-exercise" );
        check( "http://phetsims.colorado.edu/en/simulation/efield" );
        check( "http://phetsims.colorado.edu/en/simulation/electric-hockey" );
        check( "http://phetsims.colorado.edu/en/simulation/electric-hockey/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/energy-skate-park" );
        check( "http://phetsims.colorado.edu/en/simulation/equation-grapher" );
        check( "http://phetsims.colorado.edu/en/simulation/estimation" );
        check( "http://phetsims.colorado.edu/en/simulation/faraday" );
        check( "http://phetsims.colorado.edu/en/simulation/faradays-law" );
        check( "http://phetsims.colorado.edu/en/simulation/forces-1d" );
        check( "http://phetsims.colorado.edu/en/simulation/forces-1d/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/fourier" );
        check( "http://phetsims.colorado.edu/en/simulation/friction" );
        check( "http://phetsims.colorado.edu/en/simulation/gas-properties" );
        check( "http://phetsims.colorado.edu/en/simulation/gene-machine-lac-operon" );
        check( "http://phetsims.colorado.edu/en/simulation/generator" );
        check( "http://phetsims.colorado.edu/en/simulation/geometric-optics" );
        check( "http://phetsims.colorado.edu/en/simulation/glaciers" );
        check( "http://phetsims.colorado.edu/en/simulation/gravity-force-lab" );
        check( "http://phetsims.colorado.edu/en/simulation/greenhouse" );
        check( "http://phetsims.colorado.edu/en/simulation/hydrogen-atom" );
        check( "http://phetsims.colorado.edu/en/simulation/ladybug-motion-2d" );
        check( "http://phetsims.colorado.edu/en/simulation/lasers" );
        check( "http://phetsims.colorado.edu/en/simulation/lunar-lander" );
        check( "http://phetsims.colorado.edu/en/simulation/magnet-and-compass" );
        check( "http://phetsims.colorado.edu/en/simulation/magnets-and-electromagnets" );
        check( "http://phetsims.colorado.edu/en/simulation/magnets-and-electromagnets/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/#main-content" );
        check( "http://phetsims.colorado.edu/en/simulation/mass-spring-lab" );
        check( "http://phetsims.colorado.edu/en/simulation/mass-spring-lab/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/maze-game" );
        check( "http://phetsims.colorado.edu/en/simulation/maze-game/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/microwaves" );
        check( "http://phetsims.colorado.edu/en/simulation/molecular-motors" );
        check( "http://phetsims.colorado.edu/en/simulation/motion-2d" );
        check( "http://phetsims.colorado.edu/en/simulation/moving-man" );
        check( "http://phetsims.colorado.edu/en/simulation/mri" );
        check( "http://phetsims.colorado.edu/en/simulation/my-solar-system" );
        check( "http://phetsims.colorado.edu/en/simulation/my-solar-system/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/natural-selection" );
        check( "http://phetsims.colorado.edu/en/simulation/nuclear-fission" );
        check( "http://phetsims.colorado.edu/en/simulation/ohms-law" );
        check( "http://phetsims.colorado.edu/en/simulation/optical-quantum-control" );
        check( "http://phetsims.colorado.edu/en/simulation/optical-tweezers" );
        check( "http://phetsims.colorado.edu/en/simulation/pendulum-lab" );
        check( "http://phetsims.colorado.edu/en/simulation/photoelectric" );
        check( "http://phetsims.colorado.edu/en/simulation/ph-scale" );
        check( "http://phetsims.colorado.edu/en/simulation/plinko-probability" );
        check( "http://phetsims.colorado.edu/en/simulation/projectile-motion" );
        check( "http://phetsims.colorado.edu/en/simulation/quantum-tunneling" );
        check( "http://phetsims.colorado.edu/en/simulation/quantum-wave-interference" );
        check( "http://phetsims.colorado.edu/en/simulation/radioactive-dating-game" );
        check( "http://phetsims.colorado.edu/en/simulation/radio-waves" );
        check( "http://phetsims.colorado.edu/en/simulation/reactants-products-and-leftovers" );
        check( "http://phetsims.colorado.edu/en/simulation/reactions-and-rates" );
        check( "http://phetsims.colorado.edu/en/simulation/resistance-in-a-wire" );
        check( "http://phetsims.colorado.edu/en/simulation/reversible-reactions" );
        check( "http://phetsims.colorado.edu/en/simulation/rotation" );
        check( "http://phetsims.colorado.edu/en/simulation/rotation/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/rutherford-scattering" );
        check( "http://phetsims.colorado.edu/en/simulations/category/biology" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level/elementary-school" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level/elementary-school/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level/high-school" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level/#main-content" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level/middle-school" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level/university" );
        check( "http://phetsims.colorado.edu/en/simulations/category/by-level/university/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/chemistry" );
        check( "http://phetsims.colorado.edu/en/simulations/category/chemistry/general" );
        check( "http://phetsims.colorado.edu/en/simulations/category/chemistry/quantum" );
        check( "http://phetsims.colorado.edu/en/simulations/category/cutting-edge-research" );
        check( "http://phetsims.colorado.edu/en/simulations/category/earth-science" );
        check( "http://phetsims.colorado.edu/en/simulations/category/featured" );
        check( "http://phetsims.colorado.edu/en/simulations/category/#main-content" );
        check( "http://phetsims.colorado.edu/en/simulations/category/math" );
        check( "http://phetsims.colorado.edu/en/simulations/category/math/applications" );
        check( "http://phetsims.colorado.edu/en/simulations/category/math/applications/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/math/#main-content" );
        check( "http://phetsims.colorado.edu/en/simulations/category/math/tools" );
        check( "http://phetsims.colorado.edu/en/simulations/category/new" );
        check( "http://phetsims.colorado.edu/en/simulations/category/new/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/electricity-magnets-and-circuits" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/electricity-magnets-and-circuits/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/heat-and-thermodynamics" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/heat-and-thermodynamics/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/light-and-radiation" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/#main-content" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/motion" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/motion/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/quantum-phenomena" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/sound-and-waves" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/sound-and-waves/index" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/work-energy-and-power" );
        check( "http://phetsims.colorado.edu/en/simulations/category/physics/work-energy-and-power/index" );
        check( "http://phetsims.colorado.edu/en/simulation/self-driven-particle-model" );
        check( "http://phetsims.colorado.edu/en/simulation/semiconductor" );
        check( "http://phetsims.colorado.edu/en/simulation/signal-circuit" );
        check( "http://phetsims.colorado.edu/en/simulation/signal-circuit/changelog" );
        check( "http://phetsims.colorado.edu/en/simulations/index" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/1d" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/acceleration" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/acCircuits" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/alternatingCurrent" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/ammeter" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/angularPosition" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/angularVelocity" );
        check( "http://phetsims.colorado.edu/en/simulations/keyword/astronomy" );
        check( "http://phetsims.colorado.edu/en/simulation/#software-requirements" );
        check( "http://phetsims.colorado.edu/en/simulation/soluble-salts" );
        check( "http://phetsims.colorado.edu/en/simulation/sound" );
        check( "http://phetsims.colorado.edu/en/simulation/states-of-matter" );
        check( "http://phetsims.colorado.edu/en/simulation/stern-gerlach" );
        check( "http://phetsims.colorado.edu/en/simulations/translated" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/ar" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/cs" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/da" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/de" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/el" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/es" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/es_CO" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/et" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/eu" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/fa" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/fi" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/fr" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/ga" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/hr" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/hu" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/in" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/it" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/iw" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/ja" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/ka" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/km" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/ko" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/lt" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/#main-content" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/mk" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/mn" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/nb" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/nl" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/pl" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/pt" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/pt_BR" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/ro" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/ru" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/sk" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/sl" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/sr" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/tn" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/tr" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/uk" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/vi" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/zh_CN" );
        check( "http://phetsims.colorado.edu/en/simulations/translated/zh_TW" );
        check( "http://phetsims.colorado.edu/en/simulation/stretching-dna" );
        check( "http://phetsims.colorado.edu/en/simulation/#teaching-ideas" );
        check( "http://phetsims.colorado.edu/en/simulation/the-ramp" );
        check( "http://phetsims.colorado.edu/en/simulation/torque" );
        check( "http://phetsims.colorado.edu/en/simulation/#translated-versions" );
        check( "http://phetsims.colorado.edu/en/simulation/travoltage" );
        check( "http://phetsims.colorado.edu/en/simulation/travoltage/changelog" );
        check( "http://phetsims.colorado.edu/en/simulation/vector-addition" );
        check( "http://phetsims.colorado.edu/en/simulation/wave-interference" );
        check( "http://phetsims.colorado.edu/en/simulation/wave-on-a-string" );
        check( "http://phetsims.colorado.edu/en/troubleshooting" );
        check( "http://phetsims.colorado.edu/en/troubleshooting/flash" );
        check( "http://phetsims.colorado.edu/en/troubleshooting/java" );
    }
}
