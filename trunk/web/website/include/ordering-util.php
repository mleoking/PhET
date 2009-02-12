<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/db.php");
    require_once("include/sim-utils.php");
    require_once("include/web-utils.php");
    require_once("include/db-utils.php");

    function order_get_all_orders($table_name, $condition) {
        $rows = db_get_rows_by_condition($table_name, $condition, false, false);

        $orders = array();

        foreach($rows as $row) {
            $orders[] = $row["${table_name}_order"];
        }

        // This sorts by values (category order)
        asort($orders);

        // Reindex and remove duplicates:
        $orders = array_unique(array_values($orders));

        return $orders;
    }

    function order_get_order_number_by_id($table_name, $id) {
        $row = db_get_row_by_id($table_name, "${table_name}_id", $id);

        return $row["${table_name}_order"];
    }

    function order_get_previous_order_number($orders, $cur_order) {
        $previous_order = null;

        foreach ($orders as $order) {
            if ($order < $cur_order) {
                $previous_order = $order;
            }
            else {
                break;
            }
        }

        if ($previous_order == null) return $cur_order - 1;

        return $previous_order;
    }

    function order_get_next_order_number($orders, $cur_order) {
        $next_order = null;

        foreach ($orders as $order) {
            if ($order > $cur_order) {
                $next_order = $order;

                break;
            }
        }

        if ($next_order == null) return $cur_order + 1;

        return $next_order;
    }

    function order_get_sql_condition_postfix($condition) {
        if (!is_array($condition) || count($condition) == 0) return '';

        return " AND ".db_convert_condition_array_to_sql($condition);
    }

    function order_move_higher($table_name, $id, $condition) {
        $orders = order_get_all_orders($table_name, $condition);

        $order_number = order_get_order_number_by_id($table_name, $id);

        $new_order = order_get_previous_order_number($orders, $order_number);

        $condition_postfix = order_get_sql_condition_postfix($condition);

        // Swap the orders of the two adjacents:
        db_exec_query("UPDATE `$table_name` SET `${table_name}_order`='$new_order' WHERE `${table_name}_id`='$id' $condition_postfix ");

        $updated_order = $new_order + 1;

        db_exec_query("UPDATE `$table_name` SET `${table_name}_order`='$updated_order' WHERE `${table_name}_order`='$new_order' AND `${table_name}_id`<>'$id' $condition_postfix ");
    }

    function order_move_lower($table_name, $id, $condition) {
        $orders = order_get_all_orders($table_name, $condition);

        $order_number = order_get_order_number_by_id($table_name, $id);

        $new_order = order_get_next_order_number($orders, $order_number);

        $condition_postfix = order_get_sql_condition_postfix($condition);

        // Swap the orders of the two adjacents:
        db_exec_query("UPDATE `$table_name` SET `${table_name}_order`='$new_order' WHERE `${table_name}_id`='$id' $condition_postfix ");

        $updated_order = $new_order - 1;

        db_exec_query("UPDATE `$table_name` SET `${table_name}_order`='$updated_order' WHERE `${table_name}_order`='$new_order' AND `${table_name}_id`<>'$id' $condition_postfix ");
    }

?>