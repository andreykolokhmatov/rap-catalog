# Рэп-каталог на Compose Multiplatform

Каталог хип-хоп исполнителей на Kotlin Multiplatform. Предметка своя, покедекс
из воркшопа не использован. В1 — каркас на моках, В2 (ЛР4) — живые данные из сети.

## Запуск

```bash
./gradlew :desktopApp:run                        # ведущий таргет вехи
./gradlew :desktopApp:run -Plocale=en            # то же, но стартует на английском
./gradlew :webApp:wasmJsBrowserDevelopmentRun --continuous  # без --continuous Gradle сам его убьёт
./gradlew :androidApp:assembleDebug              # если настроен SDK
./gradlew :shared:jvmTest                        # тесты data-слоя и ViewModel
```

iOS собирается только на macOS с Xcode — бонус сверх программы.

## Источник данных

[MusicBrainz](https://musicbrainz.org/doc/MusicBrainz_API), как и было заявлено в В1. Моков больше нет.

| Что | Запрос |
|---|---|
| список и поиск | `GET artist?query=tag:"hip hop" AND tag:rap AND (type:person OR type:group) [AND artist:(слово*)]&limit=30&offset=…` |
| деталь | `GET artist/{id}?inc=aliases+tags+artist-rels+url-rels+release-groups&type=album` |
| справка «Об исполнителе» | Викиданные `wbgetentities` → параллельно `en/ru.wikipedia.org/api/rest_v1/page/summary/{title}` |
| фото | `query.wikidata.org/sparql`: P18 у элементов с MBID (P434), один запрос на страницу списка или на деталь со связанными |

MusicBrainz пускает не чаще запроса в секунду, иначе отвечает 503. Поэтому запросы
к нему идут через `RequestThrottle` (интервал 1,1 с, `Mutex` + `delay`), поиск уходит
через 400 мс после последнего нажатия, а на 503 Ktor сам повторяет запрос один раз
через 2 с (`HttpRequestRetry`). Связанные исполнители берутся из связей в ответе
детали, без отдельных запросов.

Справка и фото — дополнения с других серверов: их сбой не роняет экран, сбой
MusicBrainz — роняет. Адрес миниатюры Викисклада собирается из MD5 имени файла:
`Special:FilePath` отвечает редиректами без CORS, и в браузере картинка не загрузилась бы.

### Адреса — не в коде

Адреса API и `User-Agent` лежат в `api.properties` в корне. Задача
`generateApiConfig` в `shared/build.gradle.kts` делает из него объект `ApiConfig`
в `shared/build/generated`, в git он не попадает. Значение из `local.properties`
с префиксом `api.` перекрывает файл — так подставляется зеркало или тестовый сервер:

```properties
# local.properties
api.musicbrainz.url=https://beta.musicbrainz.org/ws/2/
```

Ключей у этих API нет. Если появятся — только в `local.properties`.

## Раскладка

| Путь | Что это |
|---|---|
| `domain/Artist.kt`, `ArtistPage.kt` | модели экрана: запись каталога и страница списка |
| `domain/ArtistRepository.kt` | интерфейс каталога, `suspend`-методы |
| `domain/LoadException.kt` | `sealed`: нет связи, ошибка сервера, непонятный ответ, нет записи |
| `data/network/HttpEngine.kt` | `expect val httpEngine`: OkHttp на Android, Java на desktop, Js в браузере, Darwin на iOS |
| `data/network/HttpClientFactory.kt` | один `HttpClient`: JSON, таймаут, повтор на 503, `User-Agent` |
| `data/network/LoadOrThrow.kt` | исключения Ktor и сериализации → `LoadException`, отмена проходит насквозь |
| `data/network/MusicBrainzApi.kt`, `WikiApi.kt` | метод на адрес, возвращают DTO |
| `data/network/RequestThrottle.kt` | очередь к MusicBrainz, не чаще раза в 1,1 с |
| `data/dto/` | модели ответа на `kotlinx.serialization`, отдельно от моделей экрана |
| `data/mapper/` | DTO → `Artist`: теги → жанры, альбомы, псевдонимы, связанные; адреса миниатюр |
| `data/NetworkArtistRepository.kt` | всё на `Dispatchers.Default`, справка и фото параллельно, кэш карточек в памяти |
| `data/DataModule.kt` | сборка data-слоя |
| `Screen.kt` | маршруты: `data object List`, `data class Detail(id)` |
| `ui/navigation/` | бэкстек и хост Navigation 3; у каждой записи стека своё хранилище ViewModel |
| `ui/list/`, `ui/detail/` | экран целиком: вёрстка, состояние, намерения, ViewModel и фабрика |
| `ui/ErrorReason.kt` | причина ошибки для экрана и её подпись |
| `ui/components/LoadStatus.kt` | индикатор загрузки, сообщение об ошибке с «Повторить» |
| `ui/components/ArtistAvatar.kt` | фото через Coil, буква на градиенте, пока фото нет |
| `ui/AppTheme.kt`, `GenreStyle.kt`, `AppLocale.kt` | тема, цвета жанров, язык |
| `composeResources/values`, `values-en` | подписи, две локали |
| `shared/compose-stability.conf` | доменные модели и `List` объявлены стабильными для компилятора Compose |
| `api.properties` | адреса API и `User-Agent` |
| `shared/src/commonTest/` | тесты: разбор ответов и ошибок на `MockEngine`, отмена в ViewModel |
| `docs/lab-1/` | отчёт по В1 |
| `docs/lab-4/screenshots/` | скриншоты по В2 |
| `tools/check-strings.py` | сверяет ключи локалей |

## Правила, по которым это писалось

**Асинхронность — корутины.** Репозиторий отдаёт `suspend`-функции, ViewModel
запускает их в `viewModelScope`, экран получает `StateFlow` и собирает его через
`collectAsStateWithLifecycle`. Колбэков и потоков руками нет.

**Отмена — не ошибка.** ViewModel ловит только `LoadException`. `loadOrThrow`
переводит в неё исключения Ktor, но `CancellationException` пробрасывает первой.
Отмена бывает в трёх местах, и ни одно не рисует ошибку:

- новый ввод в поиске: `collectLatest` отменяет и паузу, и запрос в сеть;
- «назад» с детали: `rememberViewModelStoreNavEntryDecorator` очищает ViewModel
  снятого экрана, `viewModelScope` отменяет загрузку;
- новый поиск во время догрузки страницы: `nextPageJob` отменяется.

**Тяжёлое — не на главном потоке.** Репозиторий работает на `Dispatchers.Default`:
там и запросы, и разбор JSON, и маппинг. Склейка страниц с отбрасыванием повторов —
тоже `withContext(Dispatchers.Default)`. В wasm поток один, там это просто
другая очередь того же потока.

**Модель ответа отдельно от модели экрана.** DTO — `internal` в `data/dto`,
наружу из data-слоя выходит только `Artist`. `Json { ignoreUnknownKeys = true }`:
новое поле в следующем релизе MusicBrainz ничего не сломает.

**Ошибка — не пустой список.** Список: `Loading | NotFound | Error(reason) | Loaded`,
под списком отдельно `NextPageStatus`: `Idle | Loading | Error(reason)`. Деталь:
`Loading | NotFound | Error(reason) | Loaded`. На ошибке — сообщение и «Повторить».

**Экран рисуется из состояния.** Вниз в composable уходят состояние и колбэк,
а не ViewModel целиком. Доменные модели неизменяемы, и `compose-stability.conf`
говорит об этом компилятору, поэтому параметры экранов `stable`, а сами экраны
`restartable skippable`. Отчёт компилятора пишется в `shared/build/compose_compiler`:

```bash
./gradlew :shared:compileKotlinJvm
grep -A3 -E "fun Artist(List|Detail)Screen" shared/build/compose_compiler/shared-composables.txt
```

Модулю явно задано имя `shared`: по умолчанию оно `RapCatalog:shared`, и на
NTFS двоеточие в имени файла уводит отчёт в альтернативный поток данных.

**Строк в коде нет.** Любая подпись лежит в `composeResources`. Проверка:

```bash
python3 tools/check-strings.py   # на Windows: py -3 tools/check-strings.py
```

**Данные каталога на английском — это не недоделка.** Локализуется интерфейс,
а не содержимое. Исключение — справка «Об исполнителе»: русская версия
(`biographyRu`) — первый абзац отдельной статьи русской Википедии, а не перевод.

**На диск ничего не пишется.** Тема и язык живут в памяти сессии, карточки
кэшируются в памяти репозитория, у Coil выключен дисковый кэш. Персистентность — В3.

## Известные ограничения

- Переключение языка работает на desktop и Android. В web локаль браузера
  доступна только на чтение, поэтому там кнопка языка подписи не меняет.
- В браузере `User-Agent` задать нельзя, MusicBrainz видит браузерный.
- У кого нет фото в Викиданных, остаётся буквенная плитка.
