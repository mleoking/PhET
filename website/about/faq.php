<?php

    include_once("../admin/site-utils.php");
    include_once("../admin/sim-utils.php");    
    
    function print_content() {
        $no_mac = SIM_NO_MAC_IMAGE_HTML;
        
        print <<<EOT
            <h1>FAQ</h1>

            <p>This page contains commonly asked questions and answers. If you can't answer your question here, please notify us by email at the following address: <a href="mailto:phethelp@colorado.edu?Subject=Help"><span class="red">phethelp@colorado.edu</span></a>.</p>

            <div id="faq">
                <ul id="nav">
                    <li class="faq"><a href="#q1">Where can I get the source code for the PhET simulations?</a></li>
                </ul>
            </div>

            <h3 id="q1" >Where can I get the source code for the PhET simulations?</h3>

            <p>The source code for all PhET simulations is hosted at <a href="http://sourceforge.net/projects/phet/">SourceForge</a>. The latest version can always be found there. To access the source code, you will need a Subversion client such as <a href="http://tortoisesvn.tigris.org/">TortoiseSVN</a> (Windows-only) or <a href="http://www.syntevo.com/smartsvn/download.jsp">SmartSVN</a> (all platforms).</p>
EOT;
    }

    print_site_page('print_content', 8);
?>