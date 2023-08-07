-- changeset Salavat:1
/* Описание changeset
Обьявление списка_изменений/changeset, что записываются в бд в таблицу `databasechangelog`
ВНИМАНИЕ! все параметры к нему должны записываться в одну строку
ВНИМАНИЕ! параметры задаются при создании и впоследствии их лучше не менять вообще

    Пробелы влияют на синтаксис, лишних быть не должно

ВНИМАНИЕ! При возникновении ошибки в sql коде, по умолчанию проект крашится, выливая тонну ошибок(матов), чего быть не должно

    Каждый changeset по умолчанию выполняется единожды при запуске программы, ему присваивается порядковый номер
помечается выполненным и впоследствии будет игнорироваться
несколько changeset могут уживаться в одном файле

    Весь sql код до конца файла или до следующего changeset - одна миграция (даже если там ХУЕВА КУЧА sql команд)

Описание синтаксиса:
Salavat - поле changeset author
1 - поле changeset id
runAlways (default:false) - параметр changeset, если истинно, программа при кажом запуске будет выполнять этот changeset
runOnChange (default:false) - параметр changeset, если истинно, программа при кажом изменении текста в changeset будет выполнять этот changeset
*/
/* Описание таблицы `databasechangelog`:
ВНИМАНИЕ! вручную в бд запись нельзя изменять (как и что-либо в бд вручную, исключая контролируемые тесты в тестовых бд с предварительным сохранением дампа)
ВСЕ поля не уникальны, PRIMARY KEY и FOREIGN KEY отсутствуют

    id VARCHAR(255)             -   обязателен, текст для идентификации; сам id будем писать следующего вида:
                                    {yyyy.MM.dd}_{author}_{table}_{auto increment}-{OPERATORS}, где
                                    auto increment - максимальное значение этой последовательности в данный момент + 1, начинается с 1,
                                    OPERATORS - перечисление типов changeset по их порядку через дефис, сами типы:
                                    СТ  -   создение таблицы
                                    UТ  -   изменение таблицы
                                    DT  -   удаление таблицы
                                    IC  -   добавление столбца(ов)
                                    UC  -   изменение столбца(ов)
                                    DС  -   удаление столбца(ов)
                                    IR  -   добавление записи(ей)
                                    UR  -   изменение записи(ей)
                                    DR  -   удаление записи(ей)
    author VARCHAR(255)         -   обязателен, текст для различия авторов;здесь будем писать свои имена, имена латиницей с заглавной буквы
    filename VARCHAR(255)       -   имя файла, в котором записан; НЕ ТРОГАЙ
    dateexecuted timestamp      -   время (последнего) запуска, автомат; НЕ ТРОГАЙ
    orderexecuted integer       -   порядковый номер, по которому библиотека проходится по changeset'ам, автоматом присваевается самый большой+1,
                                    можно делать одинаковые значения для ветвления миграции как в git, но мы этого делать не будем
                                    ;НЕ ТРОГАЙ
    exectype VARCHAR(10)        -   системный статус;НЕ ТРОГАЙ
    md5sum VARCHAR(35)          -   метаданные, по которой библиотека понимает был ли изменен changeset;НЕ ТРОГАЙ
    description VARCHAR(255)    -   метаданные для работы, из-за нашего выбора sql над xml, yaml, там всегда в значении sql;НЕ ТРОГАЙ
    comments VARCHAR(255)       -   здесь мы будем писать описание миграции и примечание к нему следующего вида:
                                    .
    tag VARCHAR(255)            -   методанные для работы с changeset, НЕ ПУТАТЬ С labels!; НЕ ТРОГАЙ
    liquibase VARCHAR(20)       -   метаданные для присваивания версии библиотеки, используемая при написании/выполнении этого changeset'а
    contexts VARCHAR(255)       -   пользовательский тип для фильтрации, обычно dev, test, prod. По умолчанию библиотека выполняет все changeset'ы, вне зависимости от
                                    их указанного контекста, но при добавления контекста к атрибутам команд, в зависимости от них будут исполненны только конкретные,
                                    непомеченные будут исполнены в любом случае;
                                    .
    labels VARCHAR(255)         -   тег/теги, которые мы присваиваем changeset'ам для фильтрации их по подобию контекста; здесь мы будем писать так:
                                    .
    deployment_id VARCHAR(10)   -   метаданные для ХЗ чего;НЕ ТРОГАЙ
*/

-- comment Это поле comment, здесь мы ОБЯЗАТЕЛЬНО будем писать краткое описание и примечания к записи

DROP TABLE IF EXISTS test_table CASCADE;
CREATE TABLE IF NOT EXISTS test_table
(
id   BIGINT,
name VARCHAR(50)
);

-- rollback DROP TABLE IF EXISTS test_table CASCADE
/* Описание rollback
rollback - та херь, что используется для отката, т.е. без прописи ее мы конечно смогли бы откатиться назад, но в бд не было бы изменений!
обязательно писать и обязательно в конце changeset'а для логического заверщения миграции

ВНИМАНИЕ! Необходимо проверять работу rollback'ов перед пушем в git, так же они не должны затрагивать другие changeset'ы без необходимости,
должны быть автоматорными, за исключением первого заполнения таблицы данными - только там можно указывать DROP ALL ROWS, во всех остальных случаях -
по id добавленных строк в этом changeset
*/

-- changeset Salavat:2
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM test_table
/* Описание условий
Условие, по которым библиотека решает, запускать ли код changeset'а или нет. В sql доступно только одно условие:
-sql-check expectedResult    -   сравнивает в sql формате переданное в условие значение с результатом кода,
при их равенстве запускает changeset, иначе - скипает
*/

INSERT INTO test_table
VALUES
(1, 'name1'),
(2, 'name2');
-- rollback DELETE FROM test_table WHERE true


/* Главные принципы миграций:

1) Не трогать бд ручными запросами, если речь идет не о тестовом бд
2) Разделение миграций на dev, prod и просто migrations:
   migrations   -   общие миграции, которые касаются и dev и prod
   dev          -   свои миграции, которые должны быть протестированы
   prod         -   миграции прода, в основном инсерты, которые доступны только для прода

   Миграции писать вначале в dev, который будет заполнен только своими миграциями, не распределенные по папкам, а после их проверки на своей ветки
   перед мерджем в main они будут перемещены либо в migrations, либо в prod
3) Не редактировать не dev-миграции, уже спушенные в main миграции не должны быть редактированы так или иначе, вместо этого нужно создать новые
4) Не использовать DROP TABLE для редактирования столбцов старых таблиц, уже спушенное нельзя удалять
5) Использовать только оговоренные скрипты только оговоренным способом и только КАПСОМ:

   CT) только через IF NOT EXISTS,
   атрибуты колонок AUDIT - под вопросом
   индексы добавлять отдельным запросом после создания таблицы
   автоинкремент указывать только для BIGINT|SMALLINT через SERIAL

         CREATE TABLE IF NOT EXISTS table_name(
             id  VARCHAR(..)|BIGINT|SMALLINT     [SERIAL]    [PRIMARY KEY]   [UNIQUE]    [NOT NULL]   [DEFAULT],
             date TIMESTAMP,
             FOREIGN KEY (column_name)
                 REFERENCES table_name [(reference_column_name)]
                     ON DELETE CASCADE
                     ON UPDATE CASCADE
         );

        CREATE INDEX IF NOT EXISTS table_column_to_table_column_index ON table_name [USING method]
         (
             column_name [ASC | DESC] [NULLS {FIRST | LAST}],
             ...
         );


        UT) Только через IF EXISTS
            ALTER TABLE IF EXISTS table_name
                RENAME TO new_table_name;

            CREATE INDEX IF NOT EXISTS table_column_to_table_column_index ON table_name [USING method]
            (
                column_name [ASC | DESC] [NULLS {FIRST | LAST}],
                ...
            );


        DT) Только через IF EXISTS
            DROP TABLE IF EXISTS table_name [CASCADE | RESTRICT];


        IC) Только через IF EXISTS, добавлять поля только без атрибутов, атрибуты добавлять в с.м. UC
            ALTER TABLE IF EXISTS table_name
                ADD COLUMN IF NOT EXISTS column_name DATA_TYPE
                [,ADD COLUMN column_name DATA_TYPE]
                [...];

        UC) Только через IF EXISTS, где это возможно (большинство команд доступны для нескольких колонок):
            ALTER TABLE IF EXISTS table_name
                RENAME COLUMN column_name TO new_column_name;

            ALTER TABLE IF EXISTS table_name
                ALTER COLUMN column_name [SET DATA] TYPE data_type [USING expression];

            ALTER TABLE IF EXISTS table_name
                ALTER COLUMN column_name SET DEFAULT expression;
            ALTER TABLE IF EXISTS table_name
                ALTER COLUMN column_name DROP DEFAULT;

            ALTER TABLE IF EXISTS table_name
                ALTER COLUMN column_name SET NOT NULL;
            ALTER TABLE IF EXISTS table_name
                ALTER COLUMN column_name DROP NOT NULL;

            ALTER TABLE IF EXISTS table_name
                ALTER COLUMN column_name ADD GENERATED {ALWAYS | BY DEFAULT} AS IDENTITY [( sequence_options )];
            SET GENERATED | SET sequence_option | RESTART - под вопросом
            ALTER TABLE IF EXISTS table_name
                ALTER COLUMN column_name DROP IDENTITY IF EXISTS;

            ALTER TABLE IF EXISTS table_name
                ADD UNIQUE (column_name);
            ALTER TABLE IF EXISTS table_name
                ADD PRIMARY KEY (column_name);


        DC) Только через IF EXISTS,
            ALTER TABLE table_name
                DROP COLUMN IF EXISTS column_name;


        IR) Прописывать все поля без NULL, за исключением автогенерируемых (они NULL)
            INSERT INTO table_name(column1, column2, …)
                VALUES (value1, value2, …);


        IR) Прописывать все поля без NULL, за исключением id, его нельзя менять вообще
            UPDATE table_name
            SET column1 = value1,
                column2 = value2,
                ...
            WHERE condition


        DR) TRUNCATE не использовать!
            DELETE FROM table_name
                WHERE condition
*/

