<?php

//
// ** THIS FEATURE IS EXPRIMENTAL **
//
// Exceptions are not used much in the PhET code, but they are
// useful from time to time.  The general behavior of the PhET
// code (and PHP's general philosophy geneally supports this
// practice) is to be as permissive as possible.  Exceptions
// if untrapped stop rendering a page, and then visitors get
// to see broken pages.
//
// When used properly exceptions point out that things are
// operating out of spec and need to be fixed.  Unfortunately
// one needs to test by rendering web pages to make sure they
// are working properly.
//
// Enter unit tests.  Lightly used and experimental at this point,
// they provide a way for testing much of the functionality,
// ensure things are working properly, reduce bugs, etc.
//
// The idea is that the only time exceptions are used in the
// PhET code is when they either don't change the existing
// behavior (such as a die statement), or they have been
// thoroughly unit tested.
//
// Current method is to sandwich names between Phet and Exception,
// so a database exception is PhetDBException().  Kinda clunky, I'm
// being safe, prehaps overly so, and not polluting the global
// namespace with possible double exceptions.  Sure is ugly, maybe
// I'll change it?

class PhetException extends Exception {};

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
        $this->db_error_message = (is_null($db_error_message)) ? mysql_error() : $db_error_message;
        $this->db_error_code = (is_null($db_error_code)) ? mysql_errno() : $db_error_code;
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