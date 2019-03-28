CREATE USER 'jkp'@'%' IDENTIFIED BY 'PASSWORD';
GRANT ALL PRIVILEGES ON `eletronic_data_interchange`.* TO 'jkp'@'%';
FLUSH PRIVILEGES;