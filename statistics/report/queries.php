<?php
    // script to be used indirectly. given a few paramters (compatible with $_GET for query-string
    // variables), it crafts an efficient corresponding set of queries to yield the result.
	
	// combine an array into a list for a WHERE clause
	function where_array($arr) {
		if(sizeof($arr) == 0) {
			return "";
		}
		if(sizeof($arr) == 1) {
			return " WHERE {$arr[0]}";
		}
		return " WHERE (" . join(" AND ", $arr) . ")";
	}

	// combine an array into a list of additional tables to use for joining
	function tables_array($arr) {
		if(sizeof($arr) == 0) {
			return "";
		}
		return ", " . join(", ", $arr);
	}

	// combine an array of groups into a list for the GROUP BY clause
	function group_array($arr) {
		if(sizeof($arr) == 0) {
			return "";
		}
		return " GROUP BY " . join(", ", $arr);
	}

	// parse format for numeric-field comparisons, including equality
	// returns the phrase to append to test for the desired condition
	function plain_cmp($val) {
		if($val == "null") {
			return " IS NULL";
		}
		if($val == "not_null") {
			return " IS NOT NULL";
		}
		if(substr($val, 0, 10) == "less_than:") {
			return " < " . substr($val, 10);
		}
		if(substr($val, 0, 16) == "less_than_equal:") {
			return " <= " . substr($val, 16);
		}
		if(substr($val, 0, 13) == "greater_than:") {
			return " > " . substr($val, 13);
		}
		if(substr($val, 0, 19) == "greater_than_equal:") {
			return " >= " . substr($val, 19);
		}
		return " = {$val}";
	}

	// direct string equality with the ability to test for NULL
	function string_equal($val) {
		if($val == "null") {
			return " IS NULL";
		}
		return " = '{$val}'";
	}

	// build a query list based on the variables of the associative array $arr.
	//
	// for many scripts, the queries should be obtained with report_query($_GET)
	// otherwise, it can be used as report_query(array("query" => "session_count"))
	function report_query($arr) {
		$query = array();
		$session_where = array();
		$tables = array();
		$group_by = array();
		$pre_select = "";
		$order_by = "";

		// signifies whether we are including session_flash_info / session_java_info into the joins
		$flash_info = false;
		$java_info = false;

		// identifies the type of query being performed
		$query_name = $arr['query'];


		//////////
		// Restriction of values included. Goes into the WHERE clause

		if($arr['sim_type'] !== null) {
			if($arr['sim_type'] == "flash") {
				array_push($session_where, "session.sim_type != 0");
			} else if($arr['sim_type'] == "java") {
				array_push($session_where, "session.sim_type = 0");
			}
		}

        // testing for equality for strings normalized in separate tables
		$strnorm_wheres = array(
	        "sim_project" => array("session", "sim_project"),
	        "sim_name" => array("session", "sim_name"),
	        "sim_deployment" => array("session", "deployment"),
	        "sim_distribution_tag" => array("session", "distribution_tag"),
	        "host_simplified_os" => array("session", "simplified_os"),

	        "host_flash_version_type" => array("session_flash_info", "flash_version_type"),
	        "host_flash_domain" => array("session_flash_info", "flash_domain"),
	        "host_flash_os" => array("session_flash_info", "flash_os"),

	        "host_java_os_name" => array("session_java_info", "java_os_name"),
	        "host_java_os_version" => array("session_java_info", "java_os_version"),
	        "host_java_os_arch" => array("session_java_info", "java_os_arch"),
	        "host_java_vendor" => array("session_java_info", "java_vendor"),
	        "host_java_webstart_version" => array("session_java_info", "java_webstart_version"),
	        "host_java_timezone" => array("session_java_info", "java_timezone")
		);
		foreach($strnorm_wheres as $field_name => $table_names) {
		    if($arr[$field_name] !== null) {
		        if($table_names[0] == "session_flash_info") {
		            $flash_info = true;
                } else if($table_names[0] == "session_java_info") {
                    $java_info = true;
                }
		        array_push($query, "SELECT (@sub_{$field_name} := {$table_names[1]}.id) FROM {$table_names[1]} WHERE {$table_names[1]}.name = '{$arr[$field_name]}'; ");
			    array_push($session_where, "{$table_names[0]}.{$field_name} = @sub_{$field_name}");
            }
		}

        // testing for numbers/boolean with comparisons
		$plain_wheres = array(
		    "sim_dev" => "session",
		    "sim_major_version" => "session",
		    "sim_minor_version" => "session",
		    "sim_dev_version" => "session",
		    "sim_svn_revision" => "session",
		    "sim_version_timestamp" => "session",
		    "host_flash_version_major" => "session_flash_info",
		    "host_flash_version_minor" => "session_flash_info",
		    "host_flash_version_revision" => "session_flash_info",
		    "host_flash_version_build" => "session_flash_info",
		    "host_flash_time_offset" => "session_flash_info",
		    "host_flash_accessibility" => "session_flash_info",
		    "host_java_version_major" => "session_java_info",
		    "host_java_version_minor" => "session_java_info",
		    "host_java_version_maintenance" => "session_java_info"
		);
		foreach($plain_wheres as $field_name => $table_name) {
		    if($arr[$field_name] !== null) {
		        if($table_name == "session_flash_info") {
		            $flash_info = true;
                } else if($table_name == "session_java_info") {
                    $java_info = true;
                }
		        array_push($session_where, "{$table_name}.{$field_name}" . plain_cmp($arr[$field_name]));
            }
		}

        // testing for non-normalized strings
		$string_wheres = array(
		    "sim_locale_language" => "session",
		    "sim_locale_country" => "session",
		    "sim_sessions_since" => "session",
		    "sim_total_sessions" => "session",
		    "host_locale_language" => "session",
		    "host_locale_country" => "session"
		);
		foreach($string_wheres as $field_name => $table_name) {
		    if($arr[$field_name] !== null) {
		        array_push($session_where, "{$table_name}.{$field_name}" . string_equal($arr[$field_name]));
            }
		}
		
		//////////
		// Grouping of values, for the GROUP BY clause

		$group = $arr['group'];
		
		if($group) {
			switch($group) {
				case "week":
					$pre_select .= "YEARWEEK(session.timestamp) as week, ";
					array_push($group_by, "week");
					break;
				case "month":
					$pre_select .= "CONCAT(YEAR(session.timestamp), '-', IF(LENGTH(MONTH(session.timestamp)) = 1, CONCAT('0', MONTH(session.timestamp)), MONTH(session.timestamp) )) as month, MONTHNAME(session.timestamp) as monthname, ";
					array_push($group_by, "month");
					break;
				case "version":
					$pre_select .= "CONCAT(session.sim_major_version, '.', session.sim_minor_version, '.', session.sim_dev_version, ' (', session.sim_svn_revision, ')') AS version, ";
					array_push($group_by, "version");
					break;
				case "sim_locale":
				    $pre_select .= "IF(session.sim_locale_country IS NULL, session.sim_locale_language, CONCAT(session.sim_locale_language, '_', session.sim_locale_country)) as sim_locale, ";
				    array_push($group_by, "sim_locale");
				    break;
				case "host_locale":
				    $pre_select .= "IF(session.host_locale_country IS NULL, session.host_locale_language, CONCAT(session.host_locale_language, '_', session.host_locale_country)) as host_locale, ";
				    array_push($group_by, "host_locale");
				    break;
				case "os":
					$pre_select .= "simplified_os.name as os, ";
					array_push($session_where, "session.host_simplified_os = simplified_os.id");
					array_push($tables, "simplified_os");
					array_push($group_by, "host_simplified_os");
					break;
				case "sim_dev":
					$pre_select .= "IF(session.sim_dev, 'true', 'false') AS sim_dev, ";
					array_push($group_by, "sim_dev");
					break;
				case "sim_type":
					$pre_select .= "IF(session.sim_type = 0, 'java', 'flash') AS sim_type, ";
					array_push($group_by, "sim_type");
					break;
				case "sim_name":
					$pre_select .= "sim_name.name as sim_name, ";
					array_push($session_where, "session.sim_name = sim_name.id");
					array_push($tables, "sim_name");
					array_push($group_by, "sim_name");
					break;
				case "sim_project":
					$pre_select .= "sim_project.name as sim_project, ";
					array_push($session_where, "session.sim_project = sim_project.id");
					array_push($tables, "sim_project");
					array_push($group_by, "sim_project");
					break;
				case "project_name":
					$pre_select .= "sim_project.name as sim_project, sim_name.name as sim_name, ";
					array_push($session_where, "session.sim_project = sim_project.id");
					array_push($session_where, "session.sim_name = sim_name.id");
					array_push($tables, "sim_project");
					array_push($tables, "sim_name");
					array_push($group_by, "sim_project");
					array_push($group_by, "sim_name");
					break;
				case "sim_deployment":
					$pre_select .= "deployment.name as sim_deployment, ";
					array_push($session_where, "session.sim_deployment = deployment.id");
					array_push($tables, "deployment");
					array_push($group_by, "sim_deployment");
					break;
				case "sim_distribution_tag":
					$pre_select .= "distribution_tag.name as sim_distribution_tag, ";
					array_push($session_where, "session.sim_distribution_tag = distribution_tag.id");
					array_push($tables, "distribution_tag");
					array_push($group_by, "sim_distribution_tag");
					break;
				case "host_simplified_os":
					$pre_select .= "simplified_os.name as host_simplified_os, ";
					array_push($session_where, "session.host_simplified_os = simplified_os.id");
					array_push($tables, "simplified_os");
					array_push($group_by, "host_simplified_os");
					break;
				case "host_flash_version_type":
					$flash_info = true;
					$pre_select .= "flash_version_type.name as host_flash_version_type, ";
					array_push($session_where, "session_flash_info.host_flash_version_type = flash_version_type.id");
					array_push($tables, "flash_version_type");
					array_push($group_by, "host_flash_version_type");
					break;
				case "host_flash_domain":
					$flash_info = true;
					$pre_select .= "flash_domain.name as host_flash_domain, ";
					array_push($session_where, "session_flash_info.host_flash_domain = flash_domain.id");
					array_push($tables, "flash_domain");
					array_push($group_by, "host_flash_domain");
					break;
				case "host_flash_os":
					$flash_info = true;
					$pre_select .= "flash_os.name as host_flash_os, ";
					array_push($session_where, "session_flash_info.host_flash_os = flash_os.id");
					array_push($tables, "flash_os");
					array_push($group_by, "host_flash_os");
					break;
				case "host_flash_version":
					$flash_info = true;
					$pre_select .= "CONCAT(session_flash_info.host_flash_version_major, ',', session_flash_info.host_flash_version_minor, ',', session_flash_info.host_flash_version_revision, ',', session_flash_info.host_flash_version_build) as host_flash_version, ";
					array_push($group_by, "host_flash_version");
					break;
				case "host_java_os_name":
					$java_info = true;
					$pre_select .= "java_os_name.name as host_java_os_name, ";
					array_push($session_where, "session_java_info.host_java_os_name = java_os_name.id");
					array_push($tables, "java_os_name");
					array_push($group_by, "host_java_os_name");
					break;
				case "host_java_os_version":
					$java_info = true;
					$pre_select .= "java_os_version.name as host_java_os_version, ";
					array_push($session_where, "session_java_info.host_java_os_version = java_os_version.id");
					array_push($tables, "java_os_version");
					array_push($group_by, "host_java_os_version");
					break;
				case "host_java_os_arch":
					$java_info = true;
					$pre_select .= "java_os_arch.name as host_java_os_arch, ";
					array_push($session_where, "session_java_info.host_java_os_arch = java_os_arch.id");
					array_push($tables, "java_os_arch");
					array_push($group_by, "host_java_os_arch");
					break;
				case "host_java_os":
					$java_info = true;
					$pre_select .= "CONCAT(java_os_name.name, ' ', java_os_version.name, ' (', java_os_arch.name, ')') as host_java_os, ";
					array_push($session_where, "session_java_info.host_java_os_name = java_os_name.id");
					array_push($session_where, "session_java_info.host_java_os_version = java_os_version.id");
					array_push($session_where, "session_java_info.host_java_os_arch = java_os_arch.id");
					array_push($tables, "java_os_name");
					array_push($tables, "java_os_version");
					array_push($tables, "java_os_arch");
					array_push($group_by, "host_java_os_name");
					array_push($group_by, "host_java_os_version");
					array_push($group_by, "host_java_os_arch");
					break;
				case "host_java_vendor":
					$java_info = true;
					$pre_select .= "java_vendor.name as host_java_vendor, ";
					array_push($session_where, "session_java_info.host_java_vendor = java_vendor.id");
					array_push($tables, "java_vendor");
					array_push($group_by, "host_java_vendor");
					break;
				case "host_java_webstart_version":
					$java_info = true;
					$pre_select .= "java_webstart_version.name as host_java_webstart_version, ";
					array_push($session_where, "session_java_info.host_java_webstart_version = java_webstart_version.id");
					array_push($tables, "java_webstart_version");
					array_push($group_by, "host_java_webstart_version");
					break;
				case "host_java_timezone":
					$java_info = true;
					$pre_select .= "java_timezone.name as host_java_timezone, ";
					array_push($session_where, "session_java_info.host_java_timezone = java_timezone.id");
					array_push($tables, "java_timezone");
					array_push($group_by, "host_java_timezone");
					break;
				default:
					if(substr($group, 0, 10) == "host_flash") {
						$flash_info = true;
						$pre_select .= "session_flash_info.{$group} as {$group}, ";
						array_push($group_by, $group);
					} else if(substr($group, 0, 9) == "host_java") {
						$java_info = true;
						$pre_select .= "session_java_info.{$group} as {$group}, ";
						array_push($group_by, $group);
					} else {
						$pre_select .= "session.{$group} as {$group}, ";
						array_push($group_by, $group);
					}
			}
		}

		//////////
		// Order and maximum # of rows returned

		$order = $arr['order'];
		
		if($order) {
			if(substr($order, 0, 5) == "desc:") {
				$order_by = " ORDER BY " . substr($order, 5) . " DESC";
			} else {
				$order_by = " ORDER BY {$order}";
			}
		}
		
		$limit = $arr['limit'];
		if($order && $limit) {
			$order_by .= " LIMIT {$limit}";
		}
		
		// if we are including one of these tables, add the join condition and table
		if($flash_info) {
			array_push($session_where, "session.id = session_flash_info.session_id");
			array_push($tables, "session_flash_info");
		}
		if($java_info) {
			array_push($session_where, "session.id = session_java_info.session_id");
			array_push($tables, "session_java_info");
		}

		// build the strings for clauses
		$session_where = where_array($session_where);
		$tables = tables_array($tables);
		$group_by = group_array($group_by);

		// change things depending on the query type desired
		switch($query_name) {
			case "empty":
				array_push($query, "SELECT {$pre_select}NULL FROM session{$tables}{$session_where}{$group_by}{$order_by}; ");
				break;
			case "message_count":
				array_push($query, "SELECT {$pre_select}COUNT(*) as message_count FROM session{$tables}{$session_where}{$group_by}{$order_by}; ");
				break;
			case "session_count":
				array_push($query, "SELECT {$pre_select}SUM(session.sim_sessions_since) as session_count FROM session{$tables}{$session_where}{$group_by}{$order_by}; ");
				break;
			case "sim_type":
				if($arr['sim_name']) {
					array_push($query, "SELECT DISTINCT IF(sim_type = 0, 'java', 'flash') AS sim_type FROM session WHERE sim_name = @sid; ");
				} else { die("cannot have sim_type query without sim_name"); }
				break;
		}
		
		return $query;
	}

	// returns the mysql result handle for the associative array $arr
	function report_result($arr) {
		$queries = report_query($arr);
		$result = null;
		foreach($queries as $query) {
			$result = mysql_query($query) or die(mysql_error());
		}
		return $result;
	}

	// if the query is expected to return a single value in a single row/column, extract and return it
	function report_single_value($arr) {
		$result = report_result($arr);
		$row = mysql_fetch_row($result);
		return $row[0];
	}

	// return a printable string which contains a description of the query, and links to the
	// table and CSV results for the query
	function report_table($desc, $args) {
		return "{$desc} [<a href='query-table.php?{$args}'>table</a>] [<a href='query-csv.php?{$args}'>csv</a>]<br />";
	}
	
	// used to extract fieldnames and display each table
	function display_result($result) {
		$num_rows = mysql_num_rows($result);
		print "<table border=1>\n";
		print "<tr>\n";
		$fields_num = mysql_num_fields($result);
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			print "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		print "</tr>\n";
		while($get_info = mysql_fetch_row($result)) {
			print "<tr>\n";
			foreach($get_info as $field) {
				print "\t<td><font face=arial size=2/>{$field}</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	function display_options($result) {
		$num_rows = mysql_num_rows($result);
		while($get_info = mysql_fetch_row($result)) {
			foreach($get_info as $field) {
				if($field !== null) {
					print "<option value='{$field}'>{$field}</option>";
				}
			}
		}
	}
	
?>