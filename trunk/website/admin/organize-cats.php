<?php
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("sim-utils.php");
	include_once("web-utils.php");
	include_once("db-utils.php");
	
	// Compute all category orders:
    $category_rows = mysql_query(SQL_SELECT_ALL_VISIBLE_CATEGORIES, $connection);
    $cat_orders    = array();
    
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
        
        db_exec_query("DELETE FROM `category` WHERE `cat_id`='$cat_id' ");
    }
    
    function do_add() {
        global $cat_name, $cat_orders;
        
        $cat_order = get_next_order_number(end($cat_orders));
        
        db_exec_query("INSERT INTO `category` (`cat_name`, `cat_order`) VALUES ('$cat_name', '$cat_order') ");
    }
    
    function do_move_up() {
        global $cat_id, $cat_order;
        
        $new_cat_order = get_previous_order_number($cat_order);
        
        // Swap the orders of the two adjacents:
        db_exec_query("UPDATE `category` SET `cat_order`='$new_cat_order' WHERE `cat_id`='$cat_id' ");
        
        $updated_cat_order = $new_cat_order + 1;
        
        db_exec_query("UPDATE `category` SET `cat_order`='$updated_cat_order' WHERE `cat_order`='$new_cat_order' AND `cat_id`<>'$cat_id' ");
    }
    
    function do_move_down() {
        global $cat_id, $cat_order;
        
        $new_cat_order = get_next_order_number($cat_order);
        
        // Swap the orders of the two adjacents:        
        db_exec_query("UPDATE `category` SET `cat_order`='$new_cat_order' WHERE `cat_id`='$cat_id' ");
        
        $updated_cat_order = $new_cat_order - 1;
        
        db_exec_query("UPDATE `category` SET `cat_order`='$updated_cat_order' WHERE `cat_order`='$new_cat_order' AND `cat_id`<>'$cat_id' ");        
    }
    
    function do_rename() {
        global $cat_id, $cat_name;
        
        db_exec_query("UPDATE `category` SET `cat_name`='$cat_name' WHERE `cat_id`='$cat_id' ");
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
    
    $category_rows = mysql_query(SQL_SELECT_ALL_VISIBLE_CATEGORIES, $connection);
    
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