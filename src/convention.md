# Конвенция по проекту

## Общие по проекту:

- Все классы, используемые для системы могут храниться в главной папке, остальные - по своим entity-папкам
- (вне dev) Без код генерации (spring.jpa.hibernate.ddl-auto=validate) в комитах, вся бд в миграциях
- Все entity все равно будут иметь аннотации, а model - должны быть сопровождены спец model аннотациями
- (вне dev) Все таблицы должны сопровождаться миграцией тестовых записей, заполненных в полном обьеме, а изменения
  таблицы должны также изменять все записи в бд
- Все id - String хешированных с солью, в бд - VARCHAR()
- Только каскадная связь, хоть в бэке, хоть в бд
- Вся логика в сервисах, преобразования в mapper-классах, комменты в api
- Работаем только в одном git.lab репе, каждому по ветке, мержит только один человек
- Перед комитом (если возможно) писать коммиты, миграции, тесты к коду, протестировать
- Все ошибки - пользовательские на каждый entity/component, имеюшие Enum с exceptionCode, exceptionHeader,
  exceptionDescription
- Генерируем время в бэке, НЕ получаем с фронта
- Отказываемся от Date в пользу java.time.Local__, ставя вручную временную зону UTC, не беря время сервера/бэка/клиента
  по умолчанию:
    - `java.time.LocalDateTime`: columnDefinition = "TIME"
    - `java.time.LocalDate`: columnDefinition = "DATE"
    - `java.time.LocalDate`: columnDefinition = "TIMESTAMP"

## Именования

1. Папки:
    1. Все с маленькой
    2. По логике:
       | Слой | Входит |
       | -- | -- |
       | **App** | serviceImpl, controller |
       | **Config** | config, beanConfig, appConfig |
       | **Domain** | model, iservice, enum |
       | **Data** | entity, repository, mapper |
       | **Exception** | exception |
       | **Util** | util, filter |
       | **Security** | AES, JWT, passwordEncoder |
2. Классы:
    1. Именуются только как:
       - `Name`
       - `NameEnum`
       - `NameRepository`
       - `NameConfig (не nameConfiguration!)`
       - `NameController`
       - `NameMapper`
       - `NameService`
       - `NameServiceImpl`
       - `NameExсeption`
       - `NameExсeptionEnum`
       - `NameUtil`
       - `NameFilter`
    2. Имеют только единственное предназначение:
       - по config только для одного бина/библиотеки/настройки
       - для одного entity
       - без русского
       - в единственном числе
3. Поле/Аргумент/Возвращаемое значение
   - Учитывает число
   - в requestMondel должен быть id, а не обьект
   - Если у метода не хватает места для параметров, интерить их

## Конвенция по CRUD'ам

### Общие правила:

- PUT:/{id}
- Не использовать .builder(), использовать только полный конструктор
- Принимать только requestModel, отправлять только responseModel, дочерние поля так же должны быть model
- Принимаются только такие стандартные API:

**GET**:
`api/table/getAll`

```
Page<responseModel> getAll(page, size, filters...)
```

`api/table/getList`

```
List<responseModel> getList()
```

`api/table/{id}`

```
responseModel getById()
```

**POST**:
`api/table/create`

```
responseModel create (reqestModel)
```

**PUT**:
`api/table/update/{id}`

```
responseModel update(id, reqestModel)
```

**DELETE**:
`api/table/delete/{id}`

```
responseModel delete(id)
```


## Миграции

- В бд не копаемся вообще ни как
- Весь код в миграциях КАПСОМ за исключением названий таблиц, колонок, данных записей
- Все пользовательские комиты должны быть многострочными /**/
- Код должен быть максимально безопасным
- Каждый changeSet должен сопровождаться rollback'ом и при этом быть проверенным
- Применяться должны только следующие команды:
  `mvn liquibase:dropAll`
  `mvn liquibase:update`
  `mvn liquibase:rollback -Dliquibase.rollbackCount=0`
  `mvn liquibase:rollback -Dliquibase.rollbackDate=2023-07-11`
- Только каскадная связь
- Применяться должны только следующие стандартные команды:

### CT

- Только через IF NOT EXISTS
- Индексы добавлять отдельным запросом после создания таблицы
- авто инкремент указывать только для BIGINT|SMALLINT через SERIAL

```postgresql
CREATE TABLE IF NOT EXISTS table_name
(
    id        VARCHAR(..) [PRIMARY KEY] [UNIQUE] [NOT NULL] [CHECK] [DEFAULT],
    date_time TIMESTAMP,
    date      DATE,
    time      TIME,

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
```

### UT) 
- Только через IF EXISTS

```postgresql
ALTER TABLE IF EXISTS table_name
    RENAME TO new_table_name;

CREATE INDEX IF NOT EXISTS table_column_to_table_column_index ON table_name [USING method]
    (
    column_name [ASC | DESC] [NULLS {FIRST | LAST}],
    ...
    );
```

### DT) 
- Только через IF EXISTS

```postgresql   
 DROP TABLE IF EXISTS table_name [CASCADE | RESTRICT];
```

### IC) 
- Только через IF EXISTS, добавлять поля только без атрибутов, атрибуты добавлять в с.м. UC

```postgresql   
ALTER TABLE IF EXISTS table_name
    ADD COLUMN IF NOT EXISTS column_name DATA_TYPE
    [,ADD COLUMN column_name DATA_TYPE]
                [...];
```

### UC) 
- Только через IF EXISTS, где это возможно (большинство команд доступны для нескольких колонок):

```postgresql
ALTER TABLE IF EXISTS table_name
    RENAME COLUMN column_name TO new_column_name;
```

```postgresql
ALTER TABLE IF EXISTS table_name
    ALTER COLUMN column_name [SET DATA] TYPE data_type [USING expression];
```

```postgresql
ALTER TABLE IF EXISTS table_name
    ALTER COLUMN column_name SET DEFAULT expression;
ALTER TABLE IF EXISTS table_name
    ALTER COLUMN column_name DROP DEFAULT;
```

```postgresql
ALTER TABLE IF EXISTS table_name
    ALTER COLUMN column_name SET NOT NULL;
ALTER TABLE IF EXISTS table_name
    ALTER COLUMN column_name DROP NOT NULL;
```

```postgresql
ALTER TABLE IF EXISTS table_name
    ALTER COLUMN column_name ADD GENERATED {ALWAYS | BY DEFAULT} AS IDENTITY [( sequence_options )];
SET GENERATED | SET sequence_option | RESTART - под вопросом
ALTER TABLE IF EXISTS table_name
    ALTER COLUMN column_name DROP IDENTITY IF EXISTS;
```
```postgresql
ALTER TABLE IF EXISTS table_name
    ADD UNIQUE (column_name);
ALTER TABLE IF EXISTS table_name
    ADD PRIMARY KEY (column_name);
```

### DC) 
- Только через IF EXISTS,

```postgresql
   ALTER TABLE table_name
    DROP COLUMN IF EXISTS column_name;
```

### IR) 
- Прописывать все поля без NULL, за исключением автогенерируемых (они NULL)

```postgresql
   INSERT INTO table_name(column1, column2, …)
   VALUES (value1, value2, …);
```

### IR) 
- Прописывать все поля без NULL, за исключением id, его нельзя менять вообще

```postgresql
   UPDATE table_name
   SET column1 = value1,
       column2 = value2, ...
       WHERE condition
```

### DR) 
- TRUNCATE не использовать!

```postgresql        
   DELETE
   FROM table_name
   WHERE condition
```