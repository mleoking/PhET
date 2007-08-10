<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    
    function print_legend() {
        global $SIM_RATING_TO_IMAGE_HTML;
        
        $crutch_html     = SIM_CRUTCH_IMAGE_HTML;
        $no_mac_html     = SIM_NO_MAC_IMAGE_HTML;
        $beta_html       = $SIM_RATING_TO_IMAGE_HTML[SIM_RATING_BETA];
        $beta_minus_html = $SIM_RATING_TO_IMAGE_HTML[SIM_RATING_BETA_MINUS]; 
        $beta_plus_html  = $SIM_RATING_TO_IMAGE_HTML[SIM_RATING_BETA_PLUS]; 
        $check_html      = $SIM_RATING_TO_IMAGE_HTML[SIM_RATING_CHECK]; 
        $alpha_html      = $SIM_RATING_TO_IMAGE_HTML[SIM_RATING_ALPHA]; 
		$from_phet_html  = FROM_PHET_IMAGE_HTML;
        
        print <<<EOT
            <h1>Legend</h1>
            
            <div id="simratinglegend">
                <table>
                    <thead>
                        <tr>
                            <td>Symbol</td> <td>Meaning</td>
                        </tr>
                    </thead>
                
                    <tbody>
                        <tr>
                            <td>$crutch_html</td>   <td>Guidance recommended: This simulation is very effective when used in conjunction with a lecture, homework  or other teacher designed activity.</td>
                        </tr>
                        
                        <tr>
                            <td>$alpha_html</td>    <td>This simulation is an early preview version, and may have functional or usability bugs.</td>
                        </tr>
                        
                        <tr>
                            <td>$beta_html</td>     <td>This simulation has undergone minimal testing, but may not have been fully refined or subjected to extensive user testing.</td>
                        </tr>
                        
                        <tr>
                            <td>$check_html</td>    <td>This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews.</td>
                        </tr>

                        <tr>
                            <td>$from_phet_html</td><td>This contribution was designed by PhET.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
EOT;
    }
    
    print_site_page('print_legend', 8);
?>