CREATE TABLE `passage` 
( `id` INT NOT NULL AUTO_INCREMENT , 
`id_ligne` INT NOT NULL , 
`code_sens` INT NOT NULL , 
`id_point_arret` INT NOT NULL , 
`arrivee_theorique` DATETIME NOT NULL , 
`arrivee` DATETIME NOT NULL , 
`depart_theorique` DATETIME NOT NULL , 
`depart` DATETIME NOT NULL , 
`ecart_depart` DOUBLE NOT NULL , 
`id_course` INT NULL , PRIMARY KEY (`id`));

CREATE TABLE `ligne_bus` ( `id_ligne` INT NOT NULL , `nom_ligne` VARCHAR(255) NOT NULL , PRIMARY KEY (`id_ligne`));

CREATE TABLE `arret_bus` ( `id_arret` INT NOT NULL , `nom_arret` VARCHAR(255) NOT NULL , `commune_arret` VARCHAR(255) NOT NULL , PRIMARY KEY (`id_arret`));

/*scp -r /home/a/Desktop/dist root@148.60.11.231:/root*/

/*
 GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'projet' WITH GRANT OPTION;
 FLUSH PRIVILEGES;
/etc/init.d/mysql start
*/


/*
	
A simple approach is to add a line in /etc/rc.local :

su - USER_FOOBAR -c /PATH/TO/MY_APP &

or if you want to run the command as root :

/PATH/TO/MY_APP &

(the trailing ampersand backgrounds the process and allows the rc.local to continue executing)

If you want a full init script, debian distro have a template file, so :

cp /etc/init.d/skeleton /etc/init.d/your_app

and adapt it a bit.


*/