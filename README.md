# AdventureMaker

AdventureMaker — мобильное Android-приложение для путешествий по российским городам.  
Проект помогает находить интересные места на карте, сохранять их в избранное и использовать полезные инструменты для поездок.

Данный документ описывает процесс работы команды: как брать задачу, создавать ветку, делать коммиты, проводить code review и вливать изменения.

Соблюдение данного процесса обязательно для всех участников проекта.
## Полезные ссылки

- GitHub: [https://github.com/Penguins-of-Madagascar-BMSTU/AdventureMaker/tree/main](https://github.com/Penguins-of-Madagascar-BMSTU/AdventureMaker/tree/main)
- Jira: [https://codingeveryday.atlassian.net/jira/software/projects/SCRUM/boards/1](https://codingeveryday.atlassian.net/jira/software/projects/SCRUM/boards/1)

## Документация проекта

- [Техническое задание](docs/TZ.md)
- [Команда проекта](docs/team.md)
- [Таблица рисков](docs/risk_table.md)
- [PERT-диаграмма](docs/pert_diagram.md)
- [P3.express](docs/p3express.md)
- [Правила работы с репозиторием](CONTRIBUTING.md)

Мы используем:

* отдельные интеграционные ветки для backend и frontend
* feature-ветки для задач
* Pull Request (PR) для code review
* Jira как единственный источник задач

**Прямые изменения в main запрещены.**

Работа в общих ветках без PR запрещена.

## Технологии

- Kotlin, Android SDK, Jetpack Compose
- Clean Architecture + MVVM
- Koin (DI)
- Retrofit
- Firebase Authentication / Realtime Database
- 2GIS SDK


В репозитории существуют 3 постоянные ветки:

| Ветка             | Назначение                    |
| ----------------- | ----------------------------- |
| main              | production (релизная версия)  |
| development-back  | интеграционная ветка backend  |
| development-front | интеграционная ветка frontend |

### Правила

* `main` — только стабильный, протестированный код
* `development-back` — интеграция backend задач
* `development-front` — интеграция frontend задач
* **push напрямую в эти ветки запрещён**
* изменения попадают туда только через Pull Request

## Структура проекта

Основной Android-проект находится в `frontend/AdventureMaker`.

- `app` — UI, навигация, экраны
- `domain` — бизнес-логика и сущности
- `data` — репозитории, API, Firebase, интеграции

## Как запустить проект

### 1) Требования

Любое изменение кода обязано быть связано с задачей в Jira.

### Шаги

1. Открыть Jira
2. Выбрать задачу
3. Назначить её на себя (Assign)
4. Перевести в статус **In Progress**
- Android Studio (актуальная версия)
- JDK 11+
- Android SDK (под `compileSdk 36`)

### 2) Настройка ключей в `gradle.properties`

В файле `frontend/AdventureMaker/gradle.properties` должны быть заданы:

---

## 2. Создание рабочей ветки

### ВАЖНО

Ветка **всегда** создаётся:

* от `development-back` — если это backend
* от `development-front` — если это frontend

Создание веток от `main` строго запрещено.

### Формат имени ветки

```
dev/<name>/<feature-name>
```

Примеры:

```
dev/rockio/login-endpoint
dev/oliesa/fix-order-validation
dev/danil/add-user-page
```

---

### Backend

```bash
git fetch origin
git checkout development-back
git pull origin development-back
git checkout -b dev/<name>/<feature-name>
```

### Frontend

```bash
git fetch origin
git checkout development-front
git pull origin development-front
git checkout -b dev/<name>/<feature-name>
```

---

## 3. Публикация ветки (Push)

После создания ветки её необходимо отправить в удалённый репозиторий:

```bash
git push -u origin dev/<name>/<feature-name>
```

С этого момента вся работа ведётся **только** в этой ветке.

Запрещено коммитить в:

* main
* development-back
* development-front

---

Ниже — переписанный раздел.
Jira полностью убран из коммитов и перенесён в **PR-процесс**.
Формат сделан универсальным (по сути — conventional commits, но без перегруза правилами).

Просто вставь это вместо старых пунктов **4–8**.

---

## 4. Правила коммитов

Мы используем единый формат сообщений коммитов.
Коммит должен описывать **что именно изменилось в коде**, а не номер задачи.

Номер Jira указывается **в названии Pull Request**, а не в коммитах.

---

### Формат коммита

```
type(scope): краткое описание
```

После заголовка можно (и желательно) добавить подробное описание:

```
type(scope): краткое описание

- что сделано
- что изменилось
- важные детали реализации
```

---

### Что такое `scope`

`scope` — это часть системы, которую затрагивает изменение.

Примеры scope:

| Backend  | Frontend     | Общее  |
| -------- | ------------ | ------ |
| auth     | login-form   | build  |
| user     | profile-page | config |
| order    | header       | docker |
| security | routing      | ci     |
| database | api-client   | deps   |

---

### Типы коммитов

| Тип      | Когда используется                            |
| -------- | --------------------------------------------- |
| feature  | новая функциональность                        |
| fix      | исправление ошибки                            |
| refactor | изменение кода без изменения поведения        |
| perf     | улучшение производительности                  |
| test     | тесты                                         |
| docs     | документация                                  |
| style    | форматирование, линтеры, без изменения логики |
| build    | сборка, зависимости                           |
| chore    | служебные изменения                           |

---

### Примеры хороших коммитов

```
feature(auth): add refresh token support

- добавлен endpoint обновления токена
- добавлено хранение refresh token в базе
- обновлена security конфигурация
```

```
fix(user): prevent null pointer on registration
```

```
refactor(order): simplify validation logic
```

```
build(deps): update spring boot version
```

```
docs(readme): update setup instructions
```

---

### Требования к коммиту

Коммит должен быть:

* небольшим
* логически завершённым
* посвящён одной задаче
* читаемым без открытия кода

**Плохой коммит:**

```
fix
```

**Плохой коммит:**

```
changes
```

**Хороший коммит:**

```
fix(auth): validate expired jwt token
```

---

### Что запрещено коммитить

* секреты
* `.env`
* логи
* build-артефакты
* `.idea`
* `node_modules`
* `target`
* временные файлы
* локальные конфиги

---

## 5. Обновление ветки перед Pull Request

Перед созданием Pull Request ветка **обязательно синхронизируется** с интеграционной веткой.

### Backend

```bash
git checkout development-back
git pull origin development-back
git checkout dev/<name>/<feature-name>
git rebase development-back
git push --force-with-lease
```

### Frontend

```bash
git checkout development-front
git pull origin development-front
git checkout dev/<name>/<feature-name>
git rebase development-front
git push --force-with-lease
```

---

## 6. Pull Request (PR)

Когда задача завершена:

1. Запушить последние изменения
2. Создать Pull Request

| Тип задачи | Куда создаётся PR     |
| ---------- | --------------------- |
| Backend    | → `development-back`  |
| Frontend   | → `development-front` |

### Название PR

В **названии Pull Request обязательно указывается Jira-задача**:

```
JIRA-123 Add refresh token support
```

(номер задачи указывается только здесь, не в коммитах)

### Описание PR должно содержать

* что сделано
* как проверить
* возможные побочные эффекты

---

### Правила PR

* запрещено мержить собственный PR
* минимум 1 approval
* все комментарии должны быть решены
* CI должен быть зелёным

---

## 7. Merge в production

После тестирования:

```
development-back  → main
development-front → main
```

Merge в `main` выполняет только maintainer/ответственный разработчик.

**Прямые PR в `main` запрещены.**

---

## 8. Definition of Done

Задача считается завершённой только если:

* код запушен
* создан Pull Request
* пройден code review
* изменения влиты в development-back / development-front
```properties
apiKey2GIS=YOUR_2GIS_KEY
s3AccessKey=YOUR_S3_ACCESS_KEY
s3SecretKey=YOUR_S3_SECRET_KEY
s3Region=YOUR_S3_REGION
s3Endpoint=YOUR_S3_ENDPOINT
bucketName=YOUR_BUCKET_NAME
```

Без этих значений сборка модуля `data` не пройдет.

### 3) Сборка и запуск (CLI)

```bash
cd frontend/AdventureMaker
./gradlew assembleDebug
```

Для запуска на устройстве/эмуляторе удобнее открыть проект в Android Studio и запустить конфигурацию `app`.

## Процесс разработки

* всегда работать в отдельной ветке
* всегда создавать Pull Request
* не обходить code review
* никогда не пушить напрямую в `main`
* никогда не создавать ветки от `main`
- задачи ведутся через Jira;
- разработка через feature-ветки и Pull Request;
- прямые изменения в `main` запрещены.
