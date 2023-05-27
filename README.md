## Превью
Доступно на Youtube: https://youtu.be/grZKU0CZgu0

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/grZKU0CZgu0/0.jpg)](https://youtu.be/grZKU0CZgu0)

## API
Краткая карта доступных методов (точки содержат websocket методы)

![endpoint-map.png](readme.src%2Fendpoint-map.png)

> ### ALERT
> Инструкция ниже не проверялась на обновленной архитектуре проекта и может быть недействительна.

## Запросы на сервер

- Для docker: `http://localhost:8092`
- Для IntelliJ IDEA: `http://localhost:8092`

## Инструкция для Docker [Ubuntu]
#### Конфигурация Spring
Убедитесь, что `application.properties` имеет в качестве URL базы данных
доменное имя postgres:
```properties
spring.datasource.url=jdbc:postgresql://postgres/users
                                        ^^^^^^^^ - должно быть так.
```
Такое условие должно выполняться и во всех остальных URL на базу данных:
```properties
spring.liquibase.url=jdbc:postgresql://postgres/users
```
Это необходимо, так как следующий пункт выстраивает связь между контейнерами: они
могут обращаться друг к другу с использованием своих имен в качестве
доменного имени.

#### Создаем сеть, внутри которой будут работать контейнеры
```bash
docker network rm gameserver-network
docker network create --driver bridge --subnet=172.0.0.0/16 gameserver-network
```

#### Создаем базу данных для сервиса аутентификации
```bash
docker stop postgres
docker rm postgres
docker run --name postgres -e POSTGRES_PASSWORD=admin --network=gameserver-network -e POSTGRES_DB=users -e POSTGRES_USER=admin -e POSTGRES_INITDB_ARGS="-E UTF8" -d -p 5432:5432 postgres
```

#### Создаем `war` пакет, используя `bootWar`
```bash
./gradlew bootWar
```

#### Запускаем проект внутри контейнера
```bash
docker stop tdg-container
docker rm tdgs-container
docker image rm tdgs-container
docker build -t tdgs-container .
docker run -d -p 8092:8092 --publish 8076:8091 --network=gameserver-network --name tdgs-container -d tdgs-container
```
Перед запуском будет выполнена компиляция проекта, которая описана в `Dockerfile`:
```dockerfile
FROM openjdk:19
WORKDIR "/app/tdg"
COPY "build/libs/TDGameServer-*.war" "towerdefensegame-server.war"
ENTRYPOINT ["java", "-jar", "towerdefensegame-server.war"]
```

Однако, стоит заметить, данные конфигурации могут быть недействительными для пользователей
Windows (как минимум, здесь используется `bash`).

## Конфигурация для IntelliJ IDEA
Файл `application.properties` должен содержать конфигурации следующего вида:
```properties
spring.liquibase.url=jdbc:postgresql://localhost:5432/users
spring.datasource.url=jdbc:postgresql://localhost:5432/users
```
