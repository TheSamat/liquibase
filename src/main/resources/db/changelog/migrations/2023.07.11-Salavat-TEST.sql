-- liquibase formatted sql
/* Описание миграционного_файла/changelogfile
    Обязательная строка для обьявления changelogfile
ВНИМАНИЕ! библиотека проверяет однострочные(дефисные) комменты, не стоит комитить что либо вне многострочных /**/ комитов

    Сами миграции мы будем писать следующим способом:
   Если sql команды не связаны друг с другом, то лучше разделять их по разным changeset
   Каждый changelogfile после их пуша в git не трогать
*/

-- changeset author:id -runAlways:true
SET timezone TO 'GMT';
SET TIME ZONE 'Asia/Bishkek';

-- rollback SET timezone TO 'GMT';