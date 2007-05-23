<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."teacher_ideas/referrer.php");
    
    function sort_contributions($contributions, $sort_by, $order) {
        $keyed_contributions = array();
        $id_to_contribution  = array();
        
        foreach($contributions as $contribution) {
            $contribution_id = $contribution['contribution_id'];
            
            $id_to_contribution[$contribution_id] = $contribution;            
            
            $key = "ZZZZZZZZZ";
            
            if (isset($contribution["$sort_by"])) {
                $key = $contribution["$sort_by"];
            }
            
            $keyed_contributions[serialize($contribution)] = "$key";
        }
        
        if ($order == 'asc') {
            asort($keyed_contributions);
        }
        else {
            arsort($keyed_contributions);
        }
        
        $sorted_contributions = array();
        
        foreach($keyed_contributions as $serialized => $key) {
            $sorted_contributions[] = unserialize($serialized);
        }
        
        return $sorted_contributions;
    }
    
    function print_content() {    
        global $sort_by, $order, $next_order;
        
        $contributions = contribution_get_all_contributions();
        
        $contributions = contribution_explode_contributions($contributions);
        
        $contributions = sort_contributions($contributions, $sort_by, $order);
        
        $script_prefix = SITE_ROOT."teacher_ideas/browse.php?order=${next_order}&amp;sort_by=";
        
        $link_prefix  = "<a href=\"${script_prefix}";
        $link_postfix = "\">";
        $link_close   = "</a>";
                
        print <<<EOT
            <table>
                <thead>
                    <tr>
            
                        <td>${link_prefix}contribution_title${link_postfix}Title${link_close}</td>  
                        
                        <td>${link_prefix}contribution_authors${link_postfix}Author${link_close}</td>  
                        
                        <td>${link_prefix}contribution_level_desc${link_postfix}Level${link_close}</td>  
                        
                        <td>${link_prefix}contribution_type_desc${link_postfix}Type${link_close}</td>  
                        
                        <td>${link_prefix}sim_name${link_postfix}Simulations${link_close}</td>  
                        
                        <td>${link_prefix}contribution_date_updated${link_postfix}Updated${link_close}</td>  
            
                    </tr>
                </thead>
                
                <tbody>
EOT;
        
        foreach($contributions as $contribution) {
            contribution_print_summary3($contribution);
        }
        
        print <<<EOT

                </tbody>
                
            </table>
            
            <br/>
            <br/>
EOT;
    }
    
    if (isset($_REQUEST['sort_by'])) {
        $sort_by = $_REQUEST['sort_by'];
    }
    else {
        $sort_by = 'title';
    }
    
    if (isset($_REQUEST['order'])) {
        $order = $_REQUEST['order'];
    }
    else {
        $order = 'asc';
    }
    
    $next_order = 'desc';
    
    if ($order == 'desc') {
        $next_order = 'asc';
    }
    
    print_site_page('print_content', 3);

?>