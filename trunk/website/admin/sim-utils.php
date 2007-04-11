<?php
    include_once("db.inc");

    // run error checks
    // check for missing fields
    function verify_field($field_name) {
        if ($field_name == null) {
            print "<center><font color=red><b>!!ERROR!!</b> You must include a <u>$field_name</u> for your Simulation! Your simulation was not edited.";

            print "<br><br> Please fill in all required fields<br><br></b></font>";

            //include 'updateexistingsim.php';

            exit();
        }
    }

    function gather_verify_sim_vars() {
        global $simid, $simtitle, $simrating, $simdesc, $simurl, $thumburl, $simsize, $usertips, $teachingideas, $learninggoals;
    
        $simid          = $_REQUEST['simid'];
        $simtitle       = $_REQUEST['title'];
        $simrating      = $_REQUEST['rating'];
        $simdesc        = $_REQUEST['simdesc'];
        $simurl         = $_REQUEST['simurl'];
        $thumburl       = $_REQUEST['thumburl'];
        $simsize        = $_REQUEST['simsize'];
        $usertips       = $_REQUEST['usertips'];
        $teachingideas  = $_REQUEST['teachingideas'];
        $learninggoals  = $_REQUEST['learninggoals'];
    
        //title
        verify_field($simtitle);

        //rating
        verify_field($simrating);
    
        //desc
        verify_field($simdesc);
    
        //system requirements
        verify_field($simdesc);
    
        //simulation file (type, size)
        verify_field($simurl);
    
        verify_field($simsize);
    
        verify_field($thumburl);

        global $simtype, $printtype;

        //find out type of SIM from URL
        global $stype = substr("$simurl", -3);

        if ($stype == "nlp") {
            $simtype   = "0"; 
            $printtype ="Java"; /*java*/
        }
        else if ($stype == "swf" || $stype == "tml" || $stype == "htm") {
            $simtype   = "1"; 
            $printtype ="Flash"; /*flash*/
        }
        else {
            print "<center><font color=red><b>!!ERROR!!</b> The url for your file doesn't have the appropriate file extension (must be swf, jnlp, html or htm)! Your simulation was not edited.";

            print "<br><br> Please check that you have the correct URL for your Simulation<br><br></b></font>";

            //include 'updateexistingsim.php';

            exit();
        }

        //check if sim url is valid http://
        global $ucheck = substr("$simurl", 0, 4); 
        
        if ($ucheck != "http") { 
            print "<center><font color=red><b>!!ERROR!!</b> The url for your file doesn't appear to be a proper url (it must start with http://)! Your simulation was not edited.";

            print "<br><br> Please check that you have the correct URL for your Simulation<br><br></b></font>";

            //include 'updateexistingsim.php';

            exit();
        }

    //check if thumbnail url is valid http://
        global $tcheck = substr("$thumburl", 0, 4); 

        if ($tcheck != "http") {
            print "<center><font color=red><b>!!ERROR!!</b> The url for your thumbnail doesn't appear to be a proper url (it must start with http://)! Your simulation was not edited.";

            print "<br><br> Please check that you have the correct URL for your Thumbnail<br><br></b></font>";

            //include 'updateexistingsim.php';

            exit();
        }

        //check that thumbnail file is a .gif
        global $ttype = substr("$thumburl", -3);    

        if ($ttype != "gif" && $ttype != "jpg" && $ttype != "peg") {
            print "<center><font color=red><b>!!ERROR!!</b> The url for your Thumbnail file doesn't have the appropriate file extension (must be gif, jpg, or jpeg)! Your simulation was not edited.";

            print "<br><br> Please check that you have the correct URL for your Thumbnail<br><br></b></font>";

            include 'updateexistingsim.php';

            exit();
        }

        global $rate_sim;

        //explain rating
        if ($simrating == "0") {
            $rate_sim = "Beta Minus";
        }
        else if ($simrating == "1") {
            $rate_sim = "Beta Plus";
        }
        else if ($simrating == "2") { 
            $rate_sim = "Beta";
        }
        else if ($simrating == "3") { 
            $rate_sim = "Star";
        }
        else if ($simrating == "4") { 
            $rate_sim = "Alpha";
        }
        
        global $mac, $mac_print;

        if (isset($HTTP_GET_VARS['mac_check'])) {
            $mac       = "1";
            $mac_print = "No Mac";
        }
        else {
            $mac       = "0";
            $mac_print = "Mac Compatible";
        }
    }

?>