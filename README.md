# AdventureMaker

AdventureMaker — мобильное Android-приложение для путешествий по российским городам.  
Проект помогает находить интересные места на карте, сохранять их в избранное и использовать полезные инструменты для поездок.

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

## Что есть в MVP

- карта и поиск мест;
- избранное;
- авторизация пользователя (Firebase);
- профиль;
- инструменты (экстренные номера, переводчик);
- базовая модульная архитектура (`presentation` / `domain` / `data`).

## Технологии

- Kotlin, Android SDK, Jetpack Compose
- Clean Architecture + MVVM
- Koin (DI)
- Retrofit
- Firebase Authentication / Realtime Database
- 2GIS SDK

## Структура проекта

Основной Android-проект находится в `frontend/AdventureMaker`.

- `app` — UI, навигация, экраны
- `domain` — бизнес-логика и сущности
- `data` — репозитории, API, Firebase, интеграции

## Как запустить проект

### 1) Требования

- Android Studio (актуальная версия)
- JDK 11+
- Android SDK (под `compileSdk 36`)

### 2) Настройка ключей в `gradle.properties`

В файле `frontend/AdventureMaker/gradle.properties` должны быть заданы:

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

- задачи ведутся через Jira;
- разработка через feature-ветки и Pull Request;
- прямые изменения в `main` запрещены.
