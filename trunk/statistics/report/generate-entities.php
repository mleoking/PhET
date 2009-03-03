<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	$result = mysql_query("SELECT * FROM user");
	
	$num_initial_rows = mysql_num_rows($result);
	
	$data = array();
	
	while($get_info = mysql_fetch_row($result)) {
		
		// TODO: don't read entries by index!!
		$row = array(
			'pref' => $get_info[1],
			'install' => $get_info[2],
			'total' => $get_info[3],
			'first' => $get_info[4],
			'last' => $get_info[5]
		);
		
		array_push($data, $row);
	}
	
	function print_rows($rows) {
		print "<table border=1 style='border-bottom: 2px solid red' cellpadding=3>";
		foreach($rows as $row) {
			print "<tr>";
			foreach($row as $a => $b) {
				if($b === null) {
					print "<td>${a} => --</td>";
				} else {
					print "<td>${a} => ${b}</td>";
				}
			}
			print "</tr>";
		}
		print "</table>";
	}
	
	function extract_pref($row) {
		return $row['pref'];
	}
	
	function extract_install($row) {
		$val = $row['install'];
		if($val === null) {
			return "NULL";
		}
		return $val;
	}
	
	class Entity {
		public $rows = array();
		public $dirty = true;
		public $num_prefs = null;
		public $num_installs = null;
		public $first = null;
		public $last = null;
		public $total = null;
		
		public function insert_row($row) {
			array_push($this->rows, $row);
		}
		
		public function process() {
			$this->reorder();
			$this->calculate_counts();
			$this->collapse();
		}
		
		public function reorder() {
			$this->rows = array_values($this->rows);
		}
		
		public function calculate_counts() {
			$prefs = array_unique(array_map("extract_pref", $this->rows));
			$this->num_prefs = count($prefs);
			
			$installs = array_unique(array_map("extract_install", $this->rows));
			$this->num_installs = count($installs);
			foreach($installs as $install) {
				if($install == "NULL") {
					$this->num_installs -= 1;
				}
			}
		}
		
		public function collapse() {
			$totalcounts = array();
			
			foreach($this->rows as $row) {
				// consolidate preferences counts
				if($totalcounts[$row['pref']]) {
					$totalcounts[$row['pref']] = max($row['total'], $totalcounts[$row['pref']]);
				} else {
					$totalcounts[$row['pref']] = $row['total'];
				}
				
				if($this->first === null || $this->first > $row['first']) {
					$this->first = $row['first'];
				}
				
				if($this->last === null || $this->last < $row['last']) {
					$this->last = $row['last'];
				}
			}
			
			$this->total = array_sum($totalcounts);
		}
		
		public static function entity_of($row) {
			$e = new Entity();
			$e->insert_row($row);
			return $e;
		}
		
		public static function associated($a, $b) {
			foreach($a->rows as $arow) {
				foreach($b->rows as $brow) {
					if($arow['pref'] == $brow['pref']) {
						return true;
					}
					if(!empty($arow['install']) && $arow['install'] == $brow['install']) {
						return true;
					}
				}
			}
			return false;
		}
		
		public static function combine($a, $b) {
			$entity = new Entity();
			foreach($a->rows as $row) {
				$entity->insert_row($row);
			}
			foreach($b->rows as $row) {
				$entity->insert_row($row);
			}
			return $entity;
		}
	}
	
	function clean(&$entities) {
		foreach(array_keys($entities) as $dirty_entity_key) {
			$dirty_entity = $entities[$dirty_entity_key];
			if(!$dirty_entity->dirty) {
				continue;
			}
			
			foreach(array_keys($entities) as $entity_key) {
				if($entity_key == $dirty_entity_key) {
					continue;
				}
				
				//print "checking ${dirty_entity_key} against ${entity_key}<br/>";
				
				$entity = $entities[$entity_key];
				if(Entity::associated($dirty_entity, $entity)) {
					//print "Association!<br/>";
					unset($entities[$dirty_entity_key]);
					unset($entities[$entity_key]);
					array_push($entities, Entity::combine($entity, $dirty_entity));
					return false;
				}
			}
			$dirty_entity->dirty = false;
		}
		return true;
	}
	
	function full_clean(&$entities) {
		while(!clean($entities)) {
		}
	}
	
	function create_entities($data) {
		$entities = array();
		
		foreach($data as $row) {
			//print "Inserting row<br/>";
			array_push($entities, Entity::entity_of($row));
			//print_entities($entities);
			full_clean($entities);
			//print_entities($entities);
		}
		
		return $entities;
	}
	
	function process_entities(&$entities) {
		foreach($entities as $entity) {
			$entity->process();
		}
	}
	
	function print_entities(&$entities) {
		print "<p style='border-left: 2px solid blue; border-top: 2px solid blue'>";
		foreach($entities as $entity) {
			if($entity->num_prefs !== null) {
				$val = $entity->num_prefs;
				print "Prefs: ${val}    ";
			}
			if($entity->num_installs !== null) {
				$val = $entity->num_installs;
				print "Installs: ${val}    ";
			}
			if($entity->total !== null) {
				$val = $entity->total;
				print "total: ${val}    ";
			}
			if($entity->first !== null) {
				$val = $entity->first;
				print "first: ${val}    ";
			}
			if($entity->last !== null) {
				$val = $entity->last;
				print "last: ${val}    ";
			}
			print "<br/>";
			print_rows($entity->rows);
		}
		print "</p>";
	}
	
	function write_entities(&$entities) {
		// remove table if it exists (it probably does exist)
		mysql_query("DROP TABLE IF EXISTS entity;");
		
		// create table with correct structure
		$str = <<<QUE
CREATE TABLE entity (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	
	preferences_count SMALLINT UNSIGNED,
	installation_count SMALLINT UNSIGNED,
	user_total_sessions MEDIUMINT UNSIGNED,
	first_seen DATETIME,
	last_seen DATETIME
);
QUE;
		mysql_query($str);
		
		// insert all of the data into the table
		$str = "INSERT INTO entity (preferences_count, installation_count, user_total_sessions, first_seen, last_seen) VALUES ";
		$count = 0;
		foreach($entities as $entity) {
			if($count > 0) {
				$str .= ", ";
			}
			$count = $count + 1;
			
			$str .= "(" . implode(", ", array($entity->num_prefs, $entity->num_installs, $entity->total, "'" . $entity->first . "'", "'" . $entity->last . "'")) . ")";
		}
		$str .= ";";
		if($count == 0) {
			return;
		}
		mysql_query($str);
	}
	
	
	$entities = create_entities($data);
	process_entities($entities);
	write_entities($entities);
	//print_entities($entities);
	
	print "Entities generated";
	
?>