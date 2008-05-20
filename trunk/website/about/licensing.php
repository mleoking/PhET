<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class LicensingPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_help_email = PHET_HELP_EMAIL;

        print <<<EOT
            <p>All PhET software is Copyright (c) The University of Colorado, under the GNU General Public License (GPL). Anyone can have access to the source code and make changes in it. According to the GPL, the source code for any changes someone makes to the software must, in turn, be made publicly available by the party that makes the changes.</p>

            <p>You may freely distribute copies of this software, but you may not change the copyright or the license. If you modify this software and distribute it you are required to license your copyrighted modifications under an GPL-compatible license and to make the entire source code for your derivation available to anybody you distribute the software to.</p>

            <p>For more information on the GPL, please see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>. The license itself can be found in its entirety at <a href="http://www.gnu.org/licenses/gpl.html">http://www.gnu.org/licenses/gpl.html</a>.</p>

            <p>This program is distributed in the hope that it will be useful, but <strong>WITHOUT ANY WARRANTY</strong>; without even the implied warranty of <strong>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE</strong>. See the GNU General Public License for more details.</p>

            <p>For additional licensing options, please contact PhET at <a href="mailto:{$phet_help_email}">{$phet_help_email}</a>.</p>

            <h1>Source Code</h1>

            <p>The source code for all PhET simulations is hosted at <a href="http://sourceforge.net/projects/phet/">SourceForge</a>. The latest version can always be found there. To access the source code, you will need a Subversion client such as <a href="http://tortoisesvn.tigris.org/">TortoiseSVN</a> (Windows-only) or <a href="http://www.syntevo.com/smartsvn/download.jsp">SmartSVN</a> (all platforms).</p>

EOT;
    }

}

$page = new LicensingPage("Licensing", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>
