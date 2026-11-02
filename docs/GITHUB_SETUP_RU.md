# Оформление GitHub

Repository name: `VulkanMod-Flashback-Edition`

Description:
`Experimental VulkanMod fork with Flashback replay and export compatibility for Minecraft 1.21.10 (Fabric). By MrIniuserYT.`

Topics:
`minecraft`, `fabric`, `vulkan`, `vulkanmod`, `flashback`, `replay`, `minecraft-mod`, `experimental`

## Создание репозитория

1. Сначала проверьте права на публикацию кода, изображений и собранных зависимостей.
2. Создайте репозиторий с указанным именем. Для предварительной проверки можно Private.
3. Не добавляйте с сайта новый LICENSE поверх существующего: лицензия upstream уже включена.
4. Распакуйте этот GitHub-пакет и загрузите содержимое корня (README.md, src, gradlew и т. д.),
   НЕ сам ZIP и НЕ предыдущий приватный FULL-sources архив.
5. Не загружайте reference/, local-deps/*.jar, logs, build и приватные реплеи.
6. Когда адрес репозитория известен, обновите contact.sources в fabric.mod.json
   на настоящий адрес этого форка. Не оставляйте адрес upstream как адрес своих исходников.
7. Не добавляйте токены GitHub в файлы или сообщения.

## Git (если установлен)

Замените OWNER на свой настоящий GitHub login; это шаблон, не готовый адрес.

```sh
git init
git branch -M main
git add .
git commit -m "Initial experimental Flashback compatibility fork"
git remote add origin https://github.com/OWNER/VulkanMod-Flashback-Edition.git
git push -u origin main
```

Секреты и gitignored зависимости не добавлять через git add -f.

## Первый релиз

После локальной сборки и проверки точного JAR:
Releases → Draft a new release.

Tag: `v0.5.7-flashback.5-rollback.1-experimental`
Title: `VulkanMod: Flashback Edition — Minecraft 1.21.10 · Experimental 1`
Body: docs/RELEASE_NOTES.md (только раздел Release body).
Обязательно отметить **Pre-release**.

Прикрепить обычный remapped JAR и соответствующий sources JAR из build/libs.
Не прикреплять ImGui API, Flashback, старые FULL-sources ZIP или пользовательские логи.
GitHub автоматически создаёт архив исходников текущего репозитория; его не ставят в mods.

Репозиторий/релиз этим пакетом автоматически НЕ создаются и НЕ публикуются.
Пришлите реальный URL созданного репозитория, если нужна помощь с адресами/настройками.
