<?php

class PhetDBException extends PhetException {
    protected $query;
    protected $db_error_message;
    protected $db_error_code;

    public function __construct($query,
                                $db_error_message = null,
                                $db_error_code = null,
                                $message = null,
                                $code = 0) {
        parent::__construct($message, $code);
        $this->query = $query;
        $this->db_error_message = (is_null($db_error_message)) ?
            mysql_error() : $db_error_message;
        $this->db_error_code = (is_null($db_error_code)) ?
            mysql_errno() : $db_error_code;
    }

    public function getQuery() {
        return $this->query;
    }

    public function getDBErrorMessage() {
        return $this->db_error_message;
    }

    public function getDBErrorCode() {
        return $this->db_error_code;
    }

    public function __toString() {
        $str = __CLASS__ . ": Database Error\n";
        $str .= "  Query: {$this->query}\n";
        $str .= "  DB Error Message: {$this->db_error_message}\n";
        $str .= "  DB Error Code: {$this->db_error_code}\n";
        $str .= parent::__toString();
        return $str;
    }
}

?>