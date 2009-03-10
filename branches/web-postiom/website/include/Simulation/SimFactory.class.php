<?php

// TODO: turn db-utils.php into a class and remove the dependency
require_once('include/db-utils.php');

// This is technically a simple factory.  Called SimFactory for ease of use.
class SimFactory {
    private static $instance;

    const PRE_IOM_COMPATIBLE = FALSE;

    // TODO: make these private
    const JAVA_TYPE = 0;
    const FLASH_TYPE = 1;

    private $webEncodedMap;
    private $idMap;
    private $simDBCache;
    private function __construct() {
        self::$instance = $this;
        $this->webEncodedMap = array();
        $this->idMap = array();
        $this->simDBCache = NULL;
    }

    private function __clone() {
        throw new RuntimeError("Not Implemented");
    }

    static public function inst() {
        if (!isset(self::$instance)) {
            $c = __CLASS__;
            self::$instance = new $c;
        }

        return self::$instance;
    }

    private function getSimDBData() {
        if (is_null($this->simDBCache)) {
            $this->simDBCache = db_get_all_rows('simulation');
        }

        return $this->simDBCache;
    }

    private function getWebEncodedNameToIdMap() {
        if (!empty($this->webEncodedMap)) {
            return $this->webEncodedMap;
        }

        $map = array();
        $simulations = $this->getSimDBData();
        foreach ($simulations as $simulation) {
            $name = web_encode_string($simulation['sim_name']);
            $this->webEncodedMap[$name] = $simulation;
        }
    
        return $this->webEncodedMap;
    }

    private function getFlashSimulation($db_data) {
        // Remove postIOM
        if (self::PRE_IOM_COMPATIBLE) {
            return new PreIomFlashSimulation($db_data);
        }

        return new FlashSimulation($db_data);
    }

    private function getJavaSimulation($db_data) {
        // Remove postIOM
        if (self::PRE_IOM_COMPATIBLE) {
            return new PreIomJavaSimulation($db_data);

        }

        return new JavaSimulation($db_data);
    }

    public function getFromWebEncodedName($sim_encoding) {
        $map = $this->getWebEncodedNameToIdMap();
        //$map = sim_get_name_to_sim_map();

        // Straight exact match with web encoded name
        foreach($map as $encoding => $sim) {
            if ($encoding == $sim_encoding) {
                return $this->getById($sim['sim_id']);
                //return $sim;
            }
        }

        // Too liberal ('a' matches to 'Gas Properties'), see ticket #754
        // Look for best match using substrings:
        if (strlen($sim_encoding) >= 3) {
            foreach($map as $encoding => $sim_id) {
                //$encoding = web_encode_string($name);

                $s1 = strtolower($sim_encoding);
                $s2 = strtolower($encoding);

                if (strpos($s1, $s2) !== false) {
                    return $this->getById($sim['sim_id']);
                    //return $sim;
                }
                else if (strpos($s2, $s1) !== false) {
                    return $this->getById($sim['sim_id']);
                    //return $sim;
                }
            }
        }

        // TODO: extract this into its own algorithm
        $best_dist = 9999999;
        $best_sim  = false;

        // Look for best match using Levenshtein distance function:
        foreach($map as $encoding => $sim) {
            //$encoding = web_encode_string($name);

            $distance = levenshtein(strtolower($sim_encoding), strtolower($encoding), 0, 2, 1);

            if ($distance < $best_dist && $distance !== -1) {
                $best_dist = $distance;
                $best_sim  = $sim;
            }
        }

        return $this->getById($best_sim['sim_id']);
    }

    public function getById($sim_id) {
        if (!empty($this->idMap) && isset($this->idMap[$sim_id])) {
            return $this->idMap[$sim_id];
        }

        $db_data = db_get_row_by_id('simulation', 'sim_id', $sim_id);
        if ($db_data === false) {
            throw new PhetSimException("Simulation ID #{$sim_id} does not exist");
        }

        if ($db_data['sim_type'] == self::JAVA_TYPE) {
            $this->idMap[$sim_id] = $this->getJavaSimulation($db_data);
        }
        else if ($db_data['sim_type'] == self::FLASH_TYPE) {
            $this->idMap[$sim_id] = $this->getFlashSimulation($db_data);
        }
        else {
            throw new PhetSimException("Bad simulation type received from database");
        }

        return $this->idMap[$sim_id];
    }

    public function getSimsByCatId($cat_id, $sort_alphabetically = false) {
        if ($sort_alphabetically) {
            $order = "`simulation`.`sim_sorting_name` ASC";
        }
        else {
            $order = "`simulation_listing`.`simulation_listing_order` ASC";
        }

        $sql = "SELECT DISTINCT `simulation`.`sim_id` ".
            "FROM `simulation`, `simulation_listing` ".
            "WHERE `simulation_listing`.`cat_id`='$cat_id' ".
            "AND `simulation`.`sim_id`=`simulation_listing`.`sim_id`".
            "ORDER BY ".$order;

        $sims = array();
        $sim_ids = db_get_rows_custom_query($sql);
        foreach ($sim_ids as $row) {
            $sims[] = $this->getById($row['sim_id']);
        }
           
        return $sims;
    }
}

?>