<?php

    include_once("web-utils.php");
    
    function db_verify_mysql_result($result, $statement) {
        if (!$result && $statement !== "") {
            $message  = 'Invalid query: ' . mysql_error() . "<br/>";
            $message .= 'Whole query: ' . $statement;

            die($message);
        }
    }
    
    function db_convert_condition_array_to_sql($condition, $fuzzy = false) {
        $query = '';
        
        $is_first = true;
        
        foreach($condition as $key => $value) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $query .= " AND ";
            }
            
            $value = mysql_real_escape_string($value);
            
            if ($fuzzy) {
                $query .= "`$key` LIKE '%$value%'";
            }
            else {
                $query .= "`$key`='$value'";
            }
        }
        
        return $query;
    }

    function db_exec_query($statement) {
        $result = mysql_query($statement);
    
        db_verify_mysql_result($result, $statement);
    
        return $result;
    }
    
    function db_get_all_rows($table_name) {
        return db_get_rows_by_condition($table_name);
    }
    
    function db_get_row_by_condition($table_name, $condition = array(), $fuzzy = false) {
        $rows = db_get_rows_by_condition($table_name, $condition, $fuzzy);
        
        if (!$rows || count($rows) == 0) return false;
        
        return $rows[0];
    }
    
    function db_get_rows_by_condition($table_name, $condition = array(), $fuzzy = false, $reformat = true) {
        if (!is_array($condition)) return array();
        
        $query = "SELECT * FROM `$table_name` ";
        
        $is_first = true;
        
        foreach($condition as $key => $value) {
            if ($is_first) {
                $is_first = false;
                
                $query .= " WHERE ";
            }
            else {
                $query .= " AND ";
            }
            
            $value = mysql_real_escape_string($value);
            
            if ($fuzzy) {
                $query .= "`$key` LIKE '%$value%'";
            }
            else {
                $query .= "`$key`='$value'";
            }
        }
        
        $result = db_exec_query($query);
        
        if (!$result) {
            return false;
        }
        
        $rows = array();
        
        while ($row = mysql_fetch_assoc($result)) {
            if ($reformat) {
                $rows[] = format_for_html($row);
            }
            else {
                $rows[] = $row;
            }
        }
        
        return $rows;
    }

	function db_search_for($table_name, $search_for, $fields_to_search) {
        $rows = array();

        $st = "SELECT * FROM `$table_name` WHERE ";          

        $is_first = true;

        foreach(preg_split('/( +)|( *, *)/i', $search_for) as $word) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $st .= " AND ";
            }

			if (count($fields_to_search) > 0) {
				$st .= "(";
				
				$is_first2 = true;
			
				foreach ($fields_to_search as $field) {
					if ($is_first2) {
						$is_first2 = false;
					}
					else {
						$st .= " OR ";
					}
					
					$st .= " `$field` LIKE '%$word%' ";
				}
			
				$st .= ")";
			}
        }

        $result = db_exec_query($st);

        while ($row = mysql_fetch_assoc($result)) {
            $rows[] = format_for_html($row);
        }

        return $rows;
	}
	
	function db_form_alternation_where_clause($table_name, $field_name, $field_values) {
		if (count($field_values) == 0) return '';
		
		$where = ' (';
		
		$is_first = true;
		
		foreach ($field_values as $field_value) {
			if ($is_first) {
				$is_first = false;
			}
			else {
				$where .= " OR";
			}
			
			$where .= " `$table_name`.`$field_name`='$field_value'";
		}
		
		$where .= ' )';
		
		return $where;
	}
    
    function db_get_row_by_id($table_name, $id_name, $id_value) {
        $query = "SELECT * FROM `$table_name` WHERE `$id_name`='$id_value' ";
        
        $rows = db_exec_query($query);
                
        if (!$rows) return false;

        $assoc = mysql_fetch_assoc($rows);
        
        if (!$assoc) return false;
        
        $cleaned = array();
        
        foreach($assoc as $key => $value) {
            // Get rid of escapes:
            $value = str_replace('\\', '', $value);
            
            $cleaned["$key"] = format_for_html("$value");
        }
        
        return $cleaned;
    }
    
    function db_delete_row($table_name, $array) {
        $delete_st = "DELETE FROM $table_name WHERE ";
        
        $is_first = true;
        
        foreach($array as $key => $value) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $delete_st .= ' AND ';
            }
            
            $value = mysql_real_escape_string($value);
            
            $delete_st .= "`$key`='$value'";
        }
        
        return db_exec_query($delete_st);
    }
    
    function db_insert_row($table_name, $array) {
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
                
                $value = mysql_real_escape_string($value);
                
                $insert_st .= "'";
                $insert_st .= "$value";
                $insert_st .= "'";
            }
            
            $insert_st .= ') ';
        }
        
        db_exec_query($insert_st);
        
        return mysql_insert_id();
    }
    
    function db_get_blank_row($table_name) {
        $row = array();
        
        $result = mysql_query("SHOW COLUMNS FROM `$table_name` ");
        
        if ($result) {
            while ($column = mysql_fetch_assoc($result)) {
                $field_name = $column['Field'];
                
                $row["$field_name"] = '';
            }
        }
        
        $row["${table_name}_id"] = -1;
        
        return $row;
    }
    
    function db_simplify_sql_timestamp($timestamp) {
        $time = strtotime($timestamp);
    
        return date('n/y', $time);
    }
    
    function db_update_table($table_name, $update_array, $id_field_name = null, $id_field_value = null) {
        if (count($update_array) == 0 || count($update_array) == 1 && isset($update_array["$id_field_name"])) {
            return true;
        }
        
        $heading_st = "UPDATE `$table_name` SET ";
        
        $content_st = '';
        
        $first_item_already_printed = false;
        
        foreach($update_array as $key => $value) {
            if ($key !== $id_field_name) {
                if ($first_item_already_printed) {
                    $content_st .= ", ";
                }
                
                $value = mysql_real_escape_string($value);

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
            
        $query = $heading_st.$content_st.$footer_st;
            
        return db_exec_query($query);
    }
?>