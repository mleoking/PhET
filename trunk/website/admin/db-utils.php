<?php
    include_once("db.inc");
    
    function verify_mysql_result($result, $statement) {
        if (!$result && $statement !== "") {
            $message  = 'Invalid query: ' . mysql_error() . "\n";
            $message .= 'Whole query: ' . $statement;

            die($message);
        }
    }

    function run_sql_statement($statement) {
        global $connection;
    
        $result = mysql_query($statement, $connection);
    
        verify_mysql_result($result, $statement);
    
        return $result;
    }
    
    function insert_row_into_table($table_name, $array) {
        $insert_st = "INSERT INTO $table_name ";
        
        if (count($array) > 0) {
            $insert_st .= '(';
            
            $is_first = true;
            
            foreach($array as $key => $value) {
                if ($is_first) {
                    $is_first = false;
                }
                else {
                    $insert_st .= ', ';
                }
                
                $insert_st .= '`';                
                $insert_st .= "$key";                
                $insert_st .= '`';                
            }
            
            $insert_st .= ')';
            
            $insert_st .= ' VALUES(';
            
            $is_first = true;
            
            foreach($array as $key => $value) {
                if ($is_first) {
                    $is_first = false;
                }
                else {
                    $insert_st .= ', ';
                }
                
                $insert_st .= "'";
                $insert_st .= "$value";
                $insert_st .= "'";
            }
            
            $insert_st .= ') ';
        }
        
        run_sql_statement($insert_st);
        
        return mysql_insert_id();
    }
    
    function update_table($table_name, $update_array, $id_field_name = null, $id_field_value = null) {
        $heading_st = "UPDATE `$table_name` SET ";
        
        $content_st = '';
        
        $first_item_already_printed = false;
        
        foreach($update_array as $key => $value) {
            if ($key !== $id_field_name) {
                if ($first_item_already_printed) {
                    $content_st .= ", ";
                }

                $content_st .= " `$key`='$value' ";
            
                $first_item_already_printed = true;
            }
        }
        
        if ($id_field_name !== null && $id_field_value !== null) {
            $footer_st = " WHERE `$id_field_name`='$id_field_value' ";
        }
        else {
            $footer_st = '';
        }
            
        run_sql_statement($heading_st.$content_st.$footer_st);
        
        return true;
    }
?>