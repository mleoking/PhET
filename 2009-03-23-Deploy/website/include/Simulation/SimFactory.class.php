<?php

// TODO: turn db-utils.php into a class and remove the dependency
require_once('include/db-utils.php');

// A simple factory that produces sims.  Called SimFactory for ease of use.
class SimFactory {
    private static $instance;

    const PRE_IOM_COMPATIBLE = TRUE;

    // TODO: make these private
    const JAVA_TYPE = 0;
    const FLASH_TYPE = 1;

    private $webEncodedMap;
    private $projectSimNameMap;
    private $idMap;
    private $simDBCache;
    private function __construct() {
        self::$instance = $this;
        $this->webEncodedMap = array();
        $this->projectSimNameMap = array();
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
            $name = WebUtils::inst()->encodeString($simulation['sim_name']);
            $this->webEncodedMap[$name] = $simulation;
        }

        return $this->webEncodedMap;
    }

    private function getProjectSimNameToIdMap() {
        if (!empty($this->projectSimNameMap)) {
            return $this->projectSimNameMap;
        }

        $this->projectSimNameMap = array();
        foreach ($this->getSimDBData() as $sim) {
            if (!isset($this->projectSimNameMap[$sim['sim_dirname']])) {
                $this->projectSimNameMap[$sim['sim_dirname']] = array();
            }
            $this->projectSimNameMap[$sim['sim_dirname']][$sim['sim_flavorname']] = $sim['sim_id'];
        }

        return $this->projectSimNameMap;
    }

    private function getFlashSimulation($db_data, $pre_iom = self::PRE_IOM_COMPATIBLE) {
        assert(($pre_iom === TRUE) || ($pre_iom === FALSE));

        // Remove postIOM
        if ($pre_iom) {
            return new PreIomFlashSimulation($db_data);
        }

        return new FlashSimulation($db_data);
    }

    private function getJavaSimulation($db_data, $pre_iom = self::PRE_IOM_COMPATIBLE) {
        assert(($pre_iom === TRUE) || ($pre_iom === FALSE));


        // Remove postIOM
        if ($pre_iom) {
            return new PreIomJavaSimulation($db_data);
        }

        return new JavaSimulation($db_data);
    }

    public function getByWebEncodedName($sim_encoding, $pre_iom = self::PRE_IOM_COMPATIBLE) {
        $map = $this->getWebEncodedNameToIdMap();

        // Straight exact match with web encoded name
        foreach($map as $encoding => $sim) {
            if ($encoding == $sim_encoding) {
                return $this->getById($sim['sim_id'], $pre_iom);
            }
        }

        // Too liberal ('a' matches to 'Gas Properties'), see ticket #754
        // Look for best match using substrings:
        if (strlen($sim_encoding) >= 3) {
            foreach($map as $encoding => $sim_id) {
                $s1 = strtolower($sim_encoding);
                $s2 = strtolower($encoding);

                if (strpos($s1, $s2) !== false) {
                    return $this->getById($sim['sim_id']);
                }
                else if (strpos($s2, $s1) !== false) {
                    return $this->getById($sim['sim_id']);
                }
            }
        }

        // TODO: extract this into its own algorithm
        $best_dist = 9999999;
        $best_sim  = false;

        // Look for best match using Levenshtein distance function:
        foreach($map as $encoding => $sim) {
            $distance = levenshtein(strtolower($sim_encoding), strtolower($encoding), 0, 2, 1);

            if ($distance < $best_dist && $distance !== -1) {
                $best_dist = $distance;
                $best_sim  = $sim;
            }
        }

        return $this->getById($best_sim['sim_id']);
    }

    public function getById($sim_id, $pre_iom = self::PRE_IOM_COMPATIBLE) {
        if (!empty($this->idMap) && isset($this->idMap[$sim_id])) {
            return $this->idMap[$sim_id];
        }

        $db_data = db_get_row_by_id('simulation', 'sim_id', $sim_id);
        if ($db_data === false) {
            throw new PhetSimException("Simulation ID #{$sim_id} does not exist");
        }

        if ($db_data['sim_type'] == self::JAVA_TYPE) {
            $sim = $this->getJavaSimulation($db_data, $pre_iom);
        }
        else if ($db_data['sim_type'] == self::FLASH_TYPE) {
            $sim = $this->getFlashSimulation($db_data, $pre_iom);
        }
        else {
            throw new PhetSimException("Bad simulation type received from database");
        }

        $this->idMap[$sim_id] = new SimulationHTMLDecorator($sim);

        return $this->idMap[$sim_id];
    }

    public function getSimsByCatId($cat_id, $sort_alphabetically = false, $pre_iom = self::PRE_IOM_COMPATIBLE) {
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
            $sims[] = $this->getById($row['sim_id'], $pre_iom);
        }

        return $sims;
    }

    private function cmpSortingName($a, $b) {
        //var_dump("called", $a, $b);
        return strcmp($a->getSortingName(), $b->getSortingName());
    }

    public function getAllSims($sort = false, $pre_iom = self::PRE_IOM_COMPATIBLE) {
        $sims = array();
        $db_data = $this->getSimDBData();
        foreach ($db_data as $data) {
            $sims[] = $this->getById($data['sim_id'], $pre_iom);
        }

        if ($sort) {
            usort($sims, array($this, 'cmpSortingName'));
        }

        return $sims;
    }

    public function getByProjectAndSimName($project_name, $sim_name, $pre_iom = self::PRE_IOM_COMPATIBLE) {
        $map = $this->getProjectSimNameToIdMap();
        if (!isset($map[$project_name][$sim_name])) {
            throw new PhetSimException("Simulation with project name '{$project_name}' and sim name '{$sim_name}'");
        }

        return $this->getById($map[$project_name][$sim_name], $pre_iom);
    }
}

?>