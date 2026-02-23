# Правила работы с репозиторием

Этот репозиторий содержит всё приложение (frontend и backend).

Данный документ описывает процесс работы команды: как брать задачу, создавать ветку, делать коммиты, проводить код-ревью и вливать изменения.

Все участники проекта обязаны соблюдать данный процесс.

---

## Основная идея процесса

Мы используем:
- фиксированные ветки (integration и production)
- feature-ветки для задач
- Pull Request (PR) для ревью кода
- Jira как единственный источник задач

Работа напрямую в основных ветках запрещена.

---

## Постоянные ветки (fixed branches)

В репозитории существуют 4 постоянные ветки:

| Ветка              | Назначение                          |
|--------------------|------------------------------------|
| main               | production backend                 |
| development        | интеграционная backend ветка       |
| main-front         | production frontend                |
| development-front  | интеграционная frontend ветка      |

### Правила
- `main` и `main-front` — только стабильный код
- `development` и `development-front` — интеграция задач
- прямые push в любые из этих веток запрещены
- изменения попадают туда только через Pull Request

---

## 1. Взятие задачи (Jira)

Любое изменение кода должно быть связано с задачей в Jira.

### Шаги
1. Открыть Jira
2. Выбрать задачу из backlog или назначенных
3. Назначить задачу на себя
4. Перевести задачу в статус **In Progress**

Работа без задачи в Jira запрещена.

---

## 2. Создание рабочей ветки

Ветка создаётся от:
- `development` для backend
- `development-front` для frontend

### Backend
```bash
git checkout development
git pull
git checkout -b dev/<name>/back/<feature-name>
```

### Frontend
```bash
git checkout development-front
git pull
git checkout -b dev/<name>/front/<feature-name>
```

Создавать ветки от `main` или `main-front` запрещено.

---

## 3. Публикация ветки (Push)

После создания ветки её нужно отправить в удалённый репозиторий:

```bash
git push -u origin dev/<name>/front/<feature-name>
```

С этого момента работа ведётся только в этой ветке.

Коммитить напрямую в `main`, `development`, `main-front`, `development-front` запрещено.

---

## 4. Правила коммитов

Все коммиты должны соответствовать формату.

### Формат
```
JIRA-ID type: краткое описание
```

### Типы

| Тип       | Описание                              |
|-----------|--------------------------------------|
| feat      | новая функциональность               |
| fix       | исправление ошибки                   |
| refactor  | изменение без изменения поведения    |
| test      | тесты                                |
| docs      | документация                         |
| chore     | конфигурация / сборка                |

### Примеры
```
JIRA-123 feat: добавить регистрацию пользователя
JIRA-231 fix: исправить null pointer при логине
JIRA-88 refactor: упростить валидацию
JIRA-90 test: добавить тесты авторизации
JIRA-12 docs: обновить readme
```

### Требования
Коммит должен быть:
- небольшим
- понятным
- посвящён одной задаче

### Запрещено коммитить
- секреты
- `.env`
- логи
- build-артефакты
- временные файлы

---

## 5. Обновление ветки перед Pull Request

Перед созданием Pull Request необходимо обновить ветку:

### Backend
```bash
git checkout development
git pull
git checkout dev/<name>/back/<feature-name>
git rebase development
git push --force-with-lease
```

### Frontend
```bash
git checkout development-front
git pull
git checkout dev/<name>/front/<feature-name>
git rebase development-front
git push --force-with-lease
```

---

## 6. Pull Request (PR)

Когда задача завершена:

1. Запушить последние изменения
2. Создать Pull Request:
    - backend → `development`
    - frontend → `development-front`
3. Прикрепить задачу Jira
4. Запросить code review

### Правила
- Нельзя мержить собственный PR
- Требуется минимум 1 approval
- Все комментарии должны быть решены

---

## 7. Merge в production

После того как задача протестирована:

- `development` → `main`
- `development-front` → `main-front`

Merge выполняет только ответственный разработчик/maintainer.

---

## 8. Definition of Done

Задача считается завершённой только если:

- код запушен
- создан Pull Request
- код-ревью пройдено
- изменения влиты в development / development-front
- задача переведена в **Done** в Jira

---

## Важные правила

- Всегда работать по задаче Jira
- Всегда использовать отдельную ветку
- Всегда создавать Pull Request
- Не обходить code review
- Не пушить напрямую в основные ветки