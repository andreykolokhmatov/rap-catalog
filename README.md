# Рэп-каталог — каркас на Compose Multiplatform

Лабораторная В1. Каталог хип-хоп исполнителей на Kotlin Multiplatform.
Предметка своя, покедекс из воркшопа не использован.

## Запуск

```bash
./gradlew :desktopApp:run                        # ведущий таргет вехи
./gradlew :desktopApp:run -Plocale=en            # то же, но стартует на английском
./gradlew :webApp:wasmJsBrowserDevelopmentRun    # бонусом, один раз
./gradlew :androidApp:assembleDebug              # если настроен SDK
```

iOS собирается только на macOS с Xcode — бонус сверх программы.

## Источник данных

[MusicBrainz](https://musicbrainz.org/doc/MusicBrainz_API), эндпоинты для В2:

```
/ws/2/artist?query=tag:hip-hop&limit=25&offset=0&fmt=json
/ws/2/artist/{mbid}?inc=aliases+genres+tags+release-groups+artist-rels&fmt=json
/ws/2/release-group?artist={mbid}&type=album&fmt=json
```

Сети в В1 нет: список живёт на моках, **33 записи**, и они не выдуманы — MBID,
имена, страны, даты, теги и полные студийные дискографии (410 релиз-групп)
 выгружены из MusicBrainz. Поля модели
названы как в ответе API, поэтому в В2 моки заменятся сетью, а не перепишутся.

Что учесть в В2: обязательный `User-Agent` с контактом, лимит 1 запрос в секунду,
пагинация через `limit`/`offset`. CORS у MusicBrainz открыт, так что web
заработает без прокси.

## Раскладка

| Путь | Что это |
|---|---|
| `domain/Artist.kt` | модель записи, поля как в ответе MusicBrainz |
| `domain/Genre.kt` | закрытый список жанров, разбор строки API в тип |
| `domain/ArtistRepository.kt` | интерфейс каталога; в В2 меняется только реализация |
| `data/MockArtists.kt` | 33 записи — единственное место с предметными данными |
| `data/ArtistRepositoryImpl.kt` | реализация на моках |
| `Screen.kt` | маршруты: `data object List`, `data class Detail(id)` |
| `ui/navigation/Navigator.kt` | бэкстек — обычный список объектов |
| `ui/navigation/AppNavDisplay.kt` | хост Navigation 3, прямые и обратные переходы |
| `list/`, `detail/` | состояние, намерения, ViewModel и фабрика на каждый экран |
| `ui/AppTheme.kt` | светлая и тёмная схемы, единственное место с цветами темы |
| `ui/GenreStyle.kt` | цвет жанра — цвет данных, от темы не зависит |
| `ui/AppLocale.kt` | переключение языка, `expect`/`actual` по таргетам |
| `composeResources/drawable` | 33 фото артистов **локально**: на сеть в В1 закладываться нельзя |
| `ui/ArtistPhotos.kt` | MBID → снимок; `null` — рисуется буквенная плитка |
| `composeResources/values`, `values-en` | подписи, две локали |
| `tools/check-strings.py` | сверяет ключи локалей |
| `docs/CREDITS.md` | авторы и лицензии снимков |

## Правила, по которым это писалось

**Строк в коде нет.** Любая подпись, которую видит пользователь, лежит в
`composeResources`. Проверка:

```bash
python3 tools/check-strings.py
```

На Windows `python3` часто уводит в заглушку Microsoft Store. Рабочий вариант:

```bash
py -3 tools/check-strings.py
```

**Данные каталога на английском — это не недоделка.** Локализуется интерфейс,
а не содержимое: русских названий альбомов в MusicBrainz нет.

**Экран рисуется из состояния.** Вниз в composable уходят состояние и колбэк,
а не ViewModel целиком — иначе компилятор выведет её как `Unstable` и убьёт
пропуск рекомпозиции. Отчёт компилятора включён в `shared/build.gradle.kts`,
вывод — в `shared/build/compose_compiler`.

**Навигация — Compose Navigation 3.** Маршрут это объект, а не строка. UI-часть
взята мультиплатформенная (`org.jetbrains.androidx.navigation3:navigation3-ui`),
runtime — гугловский. Обратные переходы заданы отдельно от прямых.

**Тема и язык живут в памяти сессии.** На диск ничего не пишется — это В3.

## Известные ограничения вехи

- Переключение языка работает на desktop и Android. В web локаль браузера
  доступна только на чтение, поэтому там кнопка языка подписи не меняет;
  веха ведётся и проверяется на desktop.
- Картинок нет: MusicBrainz фотографий не отдаёт, обложки живут в Cover Art
  Archive и приедут в В2. Вместо аватара — первая буква имени на круге цвета
  ведущего жанра.
