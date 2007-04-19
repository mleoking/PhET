<?php
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("web-utils.php");
	include_once("db-utils.php");
	
	// Compute all category orders:
	$select_categories_st = "SELECT * FROM `category` ";
    $category_rows        = mysql_query($select_categories_st, $connection);
    $cat_orders           = array();
    
    while ($category = mysql_fetch_assoc($category_rows)) {
        $cat_orders[] = $category['cat_order'];
    }
    
    // This sorts by values (category order)
    asort($cat_orders);
    
    // Reindex and remove duplicates:
    $cat_orders = array_unique(array_values($cat_orders));
    
    function get_previous_order_number($cat_order) {
        global $cat_orders;
        
        $previous_order = null;
        
        foreach ($cat_orders as $order) {
            if ($order < $cat_order) {
                $previous_order = $order;
            }
            else {
                break;
            }
        }
        
        if ($previous_order == null) return $cat_order - 1;
        
        return $previous_order;
    }
    
    function get_next_order_number($cat_order) {
        global $cat_orders;
        
        $next_order = null;
        
        foreach ($cat_orders as $order) {
            if ($order > $cat_order) {
                $next_order = $order;
                
                break;
            }
        }
        
        if ($next_order == null) return $cat_order + 1;
        
        return $next_order;
    }
	
    function do_delete() {
        global $cat_id;
        
        run_sql_statement("DELETE FROM `category` WHERE `cat_id`='$cat_id' ");
    }
    
    function do_add() {
        global $cat_name, $cat_orders;
        
        $cat_order = get_next_order_number(end($cat_orders));
        
        run_sql_statement("INSERT INTO `category` (`cat_name`, `cat_order`) VALUES ('$cat_name', '$cat_order') ");
    }
    
    function do_move_up() {
        global $cat_id, $cat_order;
        
        $new_cat_order = get_previous_order_number($cat_order);
        
        // Swap the orders of the two adjacents:
        run_sql_statement("UPDATE `category` SET `cat_order`='$new_cat_order' WHERE `cat_id`='$cat_id' ");
        
        $updated_cat_order = $new_cat_order + 1;
        
        run_sql_statement("UPDATE `category` SET `cat_order`='$updated_cat_order' WHERE `cat_order`='$new_cat_order' AND `cat_id`<>'$cat_id' ");
    }
    
    function do_move_down() {
        global $cat_id, $cat_order;
        
        $new_cat_order = get_next_order_number($cat_order);
        
        // Swap the orders of the two adjacents:        
        run_sql_statement("UPDATE `category` SET `cat_order`='$new_cat_order' WHERE `cat_id`='$cat_id' ");
        
        $updated_cat_order = $new_cat_order - 1;
        
        run_sql_statement("UPDATE `category` SET `cat_order`='$updated_cat_order' WHERE `cat_order`='$new_cat_order' AND `cat_id`<>'$cat_id' ");        
    }
    
    function do_rename() {
        global $cat_id, $cat_name;
        
        run_sql_statement("UPDATE `category` SET `cat_name`='$cat_name' WHERE `cat_id`='$cat_id' ");
    }

    gather_script_params_into_globals();

    if (isset($action)) {
        if ($action == "delete") {
            do_delete();
        }
        else if ($action == "add") {
            do_add();
        }
        else if ($action == "move_up") {
            do_move_up();
        }
        else if ($action == "move_down") {
            do_move_down();
        }
        else if ($action == "rename") {            
            do_rename();
        }
    }
    
    ?>
    
    <?php
    
    $select_categories_st = "SELECT * FROM `category` ORDER BY `cat_order` ASC ";
    $category_rows        = mysql_query($select_categories_st, $connection);
    
    while ($category = mysql_fetch_assoc($category_rows)) {
        $cat_id    = $category['cat_id'];
        $cat_name  = $category['cat_name'];
        $cat_order = $category['cat_order']; 
        
        print <<<EOT
            <form action="organize-cats.php" method="post">
                <p>
                    <input type="hidden" name="cat_id" value="$cat_id" />
                    <input type="hidden" name="action" value="rename"  />
                    
                    <input type="text" name="cat_name" value="$cat_name" maxlength="40" /> 
                    <a href="organize-cats.php?action=move_up&amp;cat_id=$cat_id&amp;cat_order=$cat_order">up</a>
                    <a href="organize-cats.php?action=move_down&amp;cat_id=$cat_id&amp;cat_order=$cat_order">down</a>
                    <a href="organize-cats.php?action=delete&amp;cat_id=$cat_id">delete</a>
                    
                    <input type="submit" value="rename" />
                </p>
            </form>
EOT;
    }
    
    ?>
    
    <form action="organize-cats.php" method="post">
        <p>
            <input type="text" name="cat_name" maxlength="40" /> 
            <input type="submit" value="add">
        </p>
    </form>