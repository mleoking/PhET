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
                            <td>$crutch_html</td>   <td>To get the most benefit from this simulation, we strongly recommend it be used in conjunction with supplementary material.</td>
                        </tr>
                        <tr>
                            <td>$no_mac_html</td>   <td>This simulation is not supported on the Macintosh platform.</td>
                        </tr>
                        <tr>
                            <td>$alpha_html</td>   <td>This simulation is an early preview version, and may not work correctly.</td>
                        </tr>
                        <tr>
                            <td>$beta_minus_html</td>   <td>This simulation is an early release version.</td>
                        </tr>
                        <tr>
                            <td>$beta_html</td>   <td>This simulation is a mid-release version.</td>
                        </tr>
                        <tr>
                            <td>$beta_plus_html</td>   <td>This simulation is a late-release version.</td>
                        </tr>
                        <tr>
                            <td>$check_html</td>   <td>This simulation has been extensively tested and refined, and is widely regarded by the teacher community and PhET team.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
EOT;
    }
    
    print_site_page('print_legend', -1);
?>