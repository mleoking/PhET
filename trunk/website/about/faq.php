<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class FAQPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $no_mac = SIM_NO_MAC_IMAGE_HTML;

        $phet_help_email = PHET_HELP_EMAIL;

        print <<<EOT
            <p>This page contains commonly asked questions and answers. If you can't answer your question here, please notify us by email at the following address: <a href="mailto:{$phet_help_email}?Subject=Help"><span class="red">{$phet_help_email}</span></a>.</p>

            <div id="faq">
                <ul id="nav">
                    <li class="faq"><a href="#q1">Where can I get the source code for the PhET simulations?</a></li>
                </ul>
            </div>

            <h3 id="q1" >Where can I get the source code for the PhET simulations?</h3>

            <p>The source code for all PhET simulations is hosted at <a href="https://phet.unfuddle.com/projects/9404/repositories/23262/browse/head/trunk">Unfuddle</a> (login with username: guest and password: guest). The latest version can always be found there. To checkout the source code, you will need a Subversion client such as <a href="http://tortoisesvn.tigris.org/">TortoiseSVN</a> (Windows-only) or <a href="http://www.syntevo.com/smartsvn/download.jsp">SmartSVN</a> (all platforms), or a command-line client. To checkout the source code for all of PhET's Java simulations using a command-line client, use the following command:</p>
            <p class="code">svn checkout https://phet.unfuddle.com/svn/phet_svn/trunk/simulations-java simulations-java --username guest --password guest</p>
            <p>To checkout the source code for all of PhET's Flash simulations, replace "simulations-java" with "simulations-flash" in the above command.</p>

EOT;
    }

}

$page = new FAQPage("FAQ", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>