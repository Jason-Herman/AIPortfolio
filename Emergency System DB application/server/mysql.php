<?php


class mysql
{
    static $db_address = array(
        "host" => "team070.gatech.systems",
        "login" => "team070",
        "password" => "yellowjackets",
        "db" => "erms",
        "port" => 3306,
        "options" => "SET NAMES utf8"
    );
    private $connect;
    private $error = null;

    function mysql($connString)
    {
        $c = new mysqli($connString["host"], $connString["login"], $connString["password"], $connString["db"], $connString["port"]);
        if (!mysqli_connect_errno()) {
            if ($connString["options"] != '') {
                $c->query($connString["options"]);
            }
            $this->connect = $c;
        } else {
            $this->error = mysqli_connect_error();
        }
    }

    public function Connect()
    {
        return $this->connect;
    }


    public function GetError()
    {
        return $this->error;
    }

}

?>
