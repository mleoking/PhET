<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class LicensingPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_help_email = PHET_HELP_EMAIL;

        print <<<EOT

        <p><strong>For non-commercial use and distribution of sims:</strong></p>
        <blockquote><table width="486" border="0">
          <tr>
            <td width="32"><a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-nc/3.0/us/88x31.png" /></a></td>
            <td width="578"><span xmlns:dc="http://purl.org/dc/elements/1.1/" href="http://purl.org/dc/dcmitype/InteractiveResource" property="dc:title" rel="dc:type">PhET Interactive Simulations</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="http://phet.colorado.edu" property="cc:attributionName" rel="cc:attributionURL">The PhET Team, University of Colorado</a> are licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/">Creative Commons Attribution-Noncommercial 3.0 United States License</a>.</td>
          </tr>
        </table>
          <em>What does this mean? </em><br>
        The interactive simulations developed by The PhET Team may be freely used and/or redistributed by third parties (e.g. students, educators, school districts, museums, etc.) as long as that use or distribution  does <strong>not </strong>involve commercial uses (e.g. reselling the simulations, distributing the simulations through a website that makes money off of ads, etc.). If you are interested in commercial uses, see next section. </blockquote>
        <p><strong>For commercial use and distribution of sims:</strong>
        <blockquote>
        If you are interested in alternative license options, please contact PhET at <a href="mailto:phethelp@colorado.edu" mailto:phethelp@colorado.edu>phethelp@colorado.edu</a>.
        </blockquote>
        <p><strong>Source code for sims:</strong>  
        <blockquote>
          <table width="485" border="0">
            <tr>
              <td width="32"><a rel="license" href="http://creativecommons.org/licenses/GPL/2.0/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/GPL/2.0/88x62.png" /></a></td>
              <td width="443">The PhET sourcecode is licensed under a <A 
        href="http://creativecommons.org/licenses/GPL/2.0/" rel=license>Creative Commons GNU General Public License</A>. </td>
            </tr>
          </table>

              <em> What does this mean? </em><br>
              The source code for the simulations is available from <a href="https://phet.unfuddle.com/projects/9404/repositories/23262/browse/head/trunk">Unfuddle</a>. Anyone can have access to the source code and make changes in it. The source code for any changes someone makes to the software must, in turn, be made publicly available by the party that makes the changes.  </p>
        </blockquote>

            <h1>Source Code</h1>

            <p>For information on accessing PhET source code, please see <a href="../about/source-code.php">here</a>.</p>

EOT;
    }
}

$page = new LicensingPage("Licensing", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>
