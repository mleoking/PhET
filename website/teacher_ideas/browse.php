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
    
    function get_contributions() {
        global $sort_by, $order, $next_order;
        
        $contributions = contribution_get_all_contributions();
        
        $contributions = contribution_explode_contributions($contributions);
        
        $contributions = sort_contributions($contributions, $sort_by, $order);
        
        return $contributions;        
    }
    
    function get_sorting_link($link_sort_by, $desc) {
        global $sort_by, $order, $next_order;
        
        $link_order = null;
        
        if ($sort_by == $link_sort_by) {
            $link_order = $next_order;
        }
        else {
            $link_order = $order;
        }
                
        $script = SITE_ROOT."teacher_ideas/browse.php?order=${link_order}&amp;sort_by=${link_sort_by}";
        
        $link = "<a href=\"${script}\">${desc}</a>";
        
        return <<<EOT
            <td>${link}</td>  
EOT;
    }
    
    function print_content() {    
        $contributions = get_contributions();
        
        $title  = get_sorting_link('contribution_title',        'Title');
        $author = get_sorting_link('contribution_authors',      'Author');
        $level  = get_sorting_link('contribution_level_desc',   'Level');
        $type   = get_sorting_link('contribution_type_desc',    'Type');
        $sims   = get_sorting_link('sim_name',                  'Simulations');
        $date   = get_sorting_link('contribution_date_updated', 'Updated');
                
        print <<<EOT
            <table>
                <thead>
                    <tr>
            
                        $title
                        
                        $author
                        
                        $level
                        
                        $type
                        
                        $sims
                        
                        $date
            
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