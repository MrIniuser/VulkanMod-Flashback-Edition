# VulkanMod: Flashback Edition

**English** · [Русский](#русский)

A community-made VulkanMod fork focused on **Flashback compatibility for Minecraft 1.21.10**.

Maintained by **MrIniuserYT**.

> Experimental prerelease. The project owner reports that the restored export implementation works in their setup. This does not establish compatibility across all hardware, settings, or mods. Export may be slow, and its preview may appear vertically flipped even when the saved video is correctly oriented.

## English

### About

VulkanMod: Flashback Edition adds a compatibility layer between VulkanMod's Vulkan renderer and Flashback's replay interface and video export features. The goal is to allow recording, viewing, and exporting Flashback replays while using VulkanMod.

### Features

- Compatibility integration for Flashback's replay interface.
- Vulkan-backed frame capture for video export.
- Custom rendering integration for Flashback's interface.
- Custom project icons and branding.
- Gradle Wrapper and standalone helper checks included.

### Requirements

- Minecraft **1.21.10**.
- Fabric Loader **0.17.3 or newer**.
- Fabric API compatible with Minecraft 1.21.10.
- Flashback **0.39.10** for replay and export features.
- A Vulkan-capable GPU and compatible graphics drivers.
- **JDK 21** for building from source.

### Installation

1. Install Fabric Loader for Minecraft 1.21.10.
2. Install the required Fabric API and Flashback versions.
3. Download the mod JAR from this repository's **Releases**, when available.
4. Place it in your instance's `mods` folder.
5. Remove the original VulkanMod JAR before launching.

**Do not install this fork alongside the original VulkanMod: both use the same mod ID.**

Flashback is installed separately and is not bundled with this fork.

### Building

Install JDK 21. Obtain the Flashback-specific `imgui-binding-1.90.0.jar` from a source you are authorized to access, then place it at:

```text
local-deps/imgui-binding-1.90.0.jar
```

Windows:

```bat
gradlew.bat clean build
```

Linux / macOS:

```sh
chmod +x gradlew
./gradlew clean build
```

Build output: `build/libs/`. Use the included wrapper, not a system Gradle 8.10 installation. Internet access is required to resolve build dependencies.

A standard imgui-java JAR is **not** a replacement: this project imports the Flashback-specific `imgui.moulberry90` API. This local dependency is `compileOnly` and is not included in the resulting mod JAR.

See [build instructions](docs/BUILDING.md) and [local dependency instructions](local-deps/README.md).

### Known limitations and validation

- Export can be slow.
- Export preview may appear vertically flipped even when the resulting video is correctly oriented.
- Other rendering mods, Flashback features, and hardware combinations are not guaranteed to work.
- Standalone CPU tests, Java syntax checks, and resource checks are not a full build or in-game validation.
- The sanitized repository layout has not received a full build in the preparation environment.
- GitHub Actions runs standalone checks, **not a full mod build**.

Back up important worlds and replays before testing. See [validation notes](docs/VALIDATION.md).

### Reporting issues

Use the issue template and include the mod and Minecraft versions; Fabric Loader, Fabric API, and Flashback versions; GPU and driver; reproduction steps; export settings where applicable; and relevant logs.

**Remove access tokens, authentication arguments, and personal information before sharing logs.**

---

## Русский

Неофициальный форк VulkanMod с доработками совместимости с **Flashback для Minecraft 1.21.10**.

Автор форка — **MrIniuserYT**.

> Экспериментальная версия. По сообщению владельца проекта, возвращённый вариант экспорта работает в его конфигурации. Это не гарантирует совместимость со всеми видеокартами, настройками и модами. Экспорт может быть медленным, а предпросмотр — перевёрнутым при правильной ориентации итогового видео.

### О проекте

VulkanMod: Flashback Edition добавляет слой совместимости между Vulkan-рендерером VulkanMod, интерфейсом реплеев и экспортом видео Flashback. Цель — дать возможность записывать, просматривать и экспортировать реплеи Flashback при использовании VulkanMod.

### Возможности

- Интеграция совместимости с интерфейсом реплеев Flashback.
- Захват кадров через Vulkan для экспорта видео.
- Интеграция отрисовки интерфейса Flashback.
- Собственные иконки и оформление проекта.
- Gradle Wrapper и отдельные проверки вспомогательного кода.

### Требования

- Minecraft **1.21.10**.
- Fabric Loader **0.17.3 или новее**.
- Fabric API для Minecraft 1.21.10.
- Flashback **0.39.10** для реплеев и экспорта.
- Видеокарта и драйвер с поддержкой Vulkan.
- **JDK 21** для сборки из исходников.

### Установка

1. Установите Fabric Loader для Minecraft 1.21.10.
2. Установите необходимые версии Fabric API и Flashback.
3. Скачайте игровой JAR форка из раздела **Releases**, когда он будет опубликован.
4. Поместите его в папку `mods` своего игрового профиля.
5. Перед запуском удалите JAR оригинального VulkanMod.

**Не устанавливайте форк вместе с оригинальным VulkanMod: у них одинаковый ID мода.**

Flashback устанавливается отдельно и не входит в состав форка.

### Сборка

Установите JDK 21. Получите специальный `imgui-binding-1.90.0.jar` из источника, к которому у вас есть законный доступ, и поместите его в:

```text
local-deps/imgui-binding-1.90.0.jar
```

Windows:

```bat
gradlew.bat clean build
```

Linux / macOS:

```sh
chmod +x gradlew
./gradlew clean build
```

Результат: `build/libs/`. Используйте включённый Gradle Wrapper, а не системный Gradle 8.10. Для загрузки зависимостей нужен интернет.

Обычный imgui-java **не подходит**: проект использует API Flashback `imgui.moulberry90`. Локальная зависимость подключается через `compileOnly` и не встраивается в итоговый JAR мода.

Подробнее: [сборка](docs/BUILDING.md) и [локальная зависимость](local-deps/README.md).

### Ограничения и проверки

- Экспорт может быть медленным.
- Предпросмотр может быть перевёрнут при правильной ориентации итогового видео.
- Работа с другими модами рендеринга, всеми функциями Flashback и любым оборудованием не гарантируется.
- Проверки CPU-кода, синтаксиса Java и ресурсов не заменяют полную сборку и тестирование в игре.
- Полная сборка очищенной версии репозитория в среде подготовки не выполнена.
- GitHub Actions запускает отдельные проверки, **а не полную сборку мода**.

Перед тестированием сохраняйте резервные копии важных миров и реплеев. См. [сведения о проверках](docs/VALIDATION.md).

### Сообщения об ошибках

Используйте шаблон Issue. Укажите версии форка, Minecraft, Fabric Loader, Fabric API и Flashback; видеокарту и драйвер; шаги воспроизведения; настройки экспорта и соответствующие логи.

**Перед отправкой логов удалите токены доступа, параметры авторизации и личные данные.**

---

## Credits / Благодарности

- **MrIniuserYT** — fork maintainer / автор форка.
- **xCollateral and VulkanMod contributors** — [original VulkanMod](https://github.com/xCollateral/VulkanMod) / оригинальный VulkanMod.
- **Moulberry and Flashback contributors** — Flashback.

This project is unofficial and is not endorsed by the original VulkanMod or Flashback developers.

Неофициальный проект, не представляющий разработчиков оригинальных VulkanMod и Flashback.

## License / Лицензия

See [LICENSE](LICENSE), [LICENSE-GPL-3.0.txt](LICENSE-GPL-3.0.txt), and [NOTICE.md](NOTICE.md). Original copyright and license notices are retained. Flashback and its dependencies remain subject to their own licenses and are not bundled in this repository.

См. [LICENSE](LICENSE), [LICENSE-GPL-3.0.txt](LICENSE-GPL-3.0.txt) и [NOTICE.md](NOTICE.md). Уведомления об авторских правах и лицензии исходного проекта сохранены. Flashback и его зависимости распространяются на собственных условиях и не включены в этот репозиторий.

## Maintainer links / Для автора репозитория

- [Changelog](CHANGELOG.md)
- [Release notes](docs/RELEASE_NOTES.md)
- [Инструкция публикации на GitHub](docs/GITHUB_SETUP_RU.md)
- [Contributing](CONTRIBUTING.md)
