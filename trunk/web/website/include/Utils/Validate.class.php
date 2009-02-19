<?php

class Validate {
    private static $instance;

    private function __construct() {
    }

    static public function inst() {
        if (!isset(self::$instance)) {
            $class = __CLASS__;
            self::$instance = new $class;
        }

        return self::$instance;
    }

    public function validInt($value) {
        return 1 == preg_match('/^[0-9]+$/', $value);
    }

    public function validDate($value) {
        $result = preg_match('/^([0-9]{4})-([0-1]?[0-9])-([0-3]?[0-9])$/', $value, $regs);
        return 1 == $result;
    }

    public function parseDate($value) {
        $result = preg_match('/^([0-9]{4})-([0-1]?[0-9])-([0-3]?[0-9])$/', $value, $date);
        $timestamp = mktime(0, 0, 0, $date[2], $date[3], $date[1]);
        $processed_date_stamp = date('Y-m-d', $timestamp);
        return $processed_date_stamp;
    }
  }
?>