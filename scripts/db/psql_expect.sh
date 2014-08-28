#!/usr/bin/expect
set username [lindex $argv 0]
set password [lindex $argv 1]
set file_path [lindex $argv 2]
set db_name [lindex $argv 3]
set root_path [file rootname $file_path]
set ext [file extension $file_path]
set fpath "$root_path$ext"
spawn psql -a -d $db_name -U $username -f $fpath
expect "Password for user $username:"
send "$password\r"
expect eof

