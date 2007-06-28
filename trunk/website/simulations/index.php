<?php

    include_once("../admin/db.inc");
    include_once("../admin/sim-utils.php");
    include_once("../admin/site-utils.php");
    include_once("../admin/web-utils.php");
    
    function print_content() {
        global $connection;
        
        if (isset($_REQUEST['cat'])) {
            $cat_encoding = $_REQUEST['cat'];
            
            $cat_id = sim_get_cat_id_by_cat_encoding($cat_encoding);
        }
        else {
            $cat_encoding = 'Top_Simulations';
            $cat_id       = 1;
        }

        $select_category_st = "SELECT * FROM `category` WHERE `cat_id`='$cat_id'";
        $category_rows      = mysql_query($select_category_st, $connection);
        
        // Print the category header -- e.g. 'Top Sims':
        if (!$category_rows) {
            print "<div class=\"productListHeader\"><h1>Invalid Category</h1></div>";
        }
        else {
            $category_row = mysql_fetch_assoc($category_rows);
            
            $cat_name = $category_row['cat_name'];

            print "<div class=\"productListHeader\"><h1>$cat_name</h1></div>";
        }

        $sim_limit = SIMS_PER_PAGE;

        if (isset($_REQUEST['st'])) {
            $sim_start_number = $_REQUEST['st'];
            
            if ($sim_start_number == -1) {
                $sim_start_number = 0;
                $sim_limit        = 999;
            }
        } 
        else { 
            $sim_start_number = 0; 
        }
        
        if (isset($_REQUEST['view_type'])) {
            $view_type = $_REQUEST['view_type'];
        }
        else {
            $view_type = "thumbs";
        }
        
        // This statement selects for all sims in the category, and orders by the sim sorting name:
        $simulations = sim_get_sims_by_cat_id($cat_id);
        
        $num_sims_in_category = count($simulations);
        
        if ($view_type == "thumbs") {
            $link = sim_get_category_link_by_cat_id($cat_id, 'index view', '&amp;view_type=index');
            
            // THUMBNAIL INDEX
            print "<div id=\"listing_type\">$link</div>";

            if ($num_sims_in_category > SIMS_PER_PAGE) {
                // Don't bother printing this section unless there are more sims than will fit on one page:
                print "<div id=\"pg\">\n";
                
                $num_pages = (int)ceil((float)$num_sims_in_category / (float)SIMS_PER_PAGE);

                for ($n = 0; $n < $num_pages; $n = $n + 1) {
                    $page_number = $n + 1;

                    $page_sim_start_number = SIMS_PER_PAGE * $n; 
                    
                    $link = sim_get_category_link_by_cat_id($cat_id, "$page_number", "&amp;st=$page_sim_start_number", 'pg');

                    print "$link";
                }
                
                $link = sim_get_category_link_by_cat_id($cat_id, "view all&raquo;", "&amp;st=-1", 'pg');

                print "$link";
                
                print "</div>\n";    
            }

            //--------------------------------------------------
            print "<div class=\"productList\">";

            $sim_number = -1;

            foreach($simulations as $simulation) {                 
                eval(get_code_to_create_variables_from_array($simulation));

                // Make sure the simulation is valid:
                if (is_numeric($sim_id)) {
                    ++$sim_number;

                    if ($sim_number <  $sim_start_number) continue;
                    if ($sim_number >= $sim_start_number + $sim_limit) break;

                    print "<div class=\"productEntry\">\n";

                    /*
                        <a href="#"><img src="../images/sims/baloon_static.jpg" width="130" height="97" alt="" /></a>

                        <p><a href="/">Balloons &amp; Static</a><br /></p>
                    */

                    $sim_url = sim_get_url_to_sim_page($sim_id);

                    $link_to_sim = "<a href=\"$sim_url\">";
                    

                    if ($sim_animated_image_url == "") {
                        $sim_animated_image_url = $sim_image_url;
                    }
                    
                    print <<<EOT
                        <a href="$sim_url"
                        
                            onMouseOver="now = new Date();                                         document.images['image_preview_$sim_id'].src =                                       '../admin/get-upload.php?url=$sim_animated_image_url&amp;' + now.getTime();"
                            
                            onMouseOut="document.images['image_preview_$sim_id'].src = '../admin/get-upload.php?url=$sim_image_url'; "
                            
                            >

                            <img src="../admin/get-upload.php?url=$sim_image_url" 
                                 name="image_preview_$sim_id"
                                 width="130" height="97" 
                                 alt="View $sim_name Simulation"
                                 title="Clear here to view the $sim_name simulation"
                             />
                        </a>
                    
EOT;

                    print "<br/><p>$link_to_sim$sim_name</a></p>\n";

                    // Close product:
                    print "</div>\n";
                }
            }
            
            print "</div>\n";
        }
        else {
            $link = sim_get_category_link_by_cat_id($cat_id, "thumbnail view", "&amp;view_type=thumbs");
                            
            print "<div id=\"listing_type\">$link</a></div>";
            
            // ALPHABETICAL INDEX
            
            print "<div id=\"pg\">\n";
            
            $last_printed_char = '';
            
            foreach($simulations as $simulation) {                
                eval(get_code_to_create_variables_from_array($simulation));
                
                $sim_sorting_name = get_sorting_name($sim_name);
                
                $cur_char = $sim_sorting_name[0];
                
                if ($cur_char !== $last_printed_char) {                        
                    print "<a class=\"pg\" href=\"#$cur_char\">$cur_char</a> ";
                    
                    $last_printed_char = $cur_char;
                }
            }
            
            print "</div>\n";
            
            print "<div class=\"productList\">";
            
            $last_printed_char = '';
            
            foreach($simulations as $simulation) {
                eval(get_code_to_create_variables_from_array($simulation));
                
                $sim_sorting_name = get_sorting_name($sim_name);
                
                $cur_char = $sim_sorting_name[0];
                
                if ($cur_char !== $last_printed_char) {                        
                    print "<h3 id=\"$cur_char\">$cur_char</h3>";
                    
                    $last_printed_char = $cur_char;
                }
                
                $sim_url = sim_get_url_to_sim_page($sim_id);
                
                print "<a href=\"$sim_url\">$sim_name</a><br />";
            }
            
            print "</div>";
        }    
            
        print <<<EOT
            <div class="clear">
                <p>
                    <a href="../teacher_ideas/browse.php?cat=$cat_encoding">related ideas &amp; activities</a> 
                </p>
            </div>
EOT;
    }

    print_site_page('print_content', 2);

?>