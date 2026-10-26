ФОКУС — ПЕРВАЯ АТТЕСТАЦИЯ. Kotlin + XML Views.

КАК НАДЁЖНО ЗАПУСТИТЬ В ANDROID STUDIO
======================================
1. File -> New -> New Project -> "Empty Views Activity" (НЕ Compose / Empty Activity).
2. Name = FocusPlanner; Package name = com.example.focusplanner; Language = Kotlin.
3. Дождись, чтобы Gradle Sync завершился УСПЕШНО (зависимости Android должны скачаться).
4. Из данного архива перенеси в свой новый проект:
   - app/src/main/java/com/example/focusplanner/MainActivity.kt
   - app/src/main/AndroidManifest.xml
   - app/src/main/res/layout/activity_main.xml
   - все app/src/main/res/drawable/fp_*.xml
   - app/src/main/res/values/focus_colors.xml и focus_styles.xml
5. Run -> запусти на Android Emulator или телефоне.

В архиве также есть settings.gradle.kts, build.gradle.kts и app/build.gradle.kts:
это пример конфигурации Gradle для проекта c AGP 8.7.3, Gradle 8.9,
Kotlin 2.0.21, compileSdk 35 и JDK 17. Они нужны для СИНХРОНИЗАЦИИ,
иначе Android Studio показывает "Unresolved reference: android".
Если создаёшь проект заново, можно оставить сгенерированные Gradle-файлы;
не надо менять их ради этого макета.

Важно: в архив НЕ добавлен gradle-wrapper.jar. Поэтому надёжнее идти
через новый Empty Views Activity, который сам создаёт Gradle Wrapper.
Вручную открывать скачанный архив как готовый проект может не сработать,
пока не будет добавлен Wrapper из проекта Android Studio.

Что демонстрирует экран: ScrollView, адаптивные карточки с layout_weight,
заголовки, прогресс, блок задач, карточка пары, нижнее меню.
Кнопки показывают Toast; остальная информация демонстрационная.

Если снова "Unresolved reference: android":
- Убедись, что открыт корень проекта, а не только app/src.
- File -> Sync Project with Gradle Files.
- Settings -> Build, Execution, Deployment -> Build Tools -> Gradle:
  Gradle JDK 17 или встроенный JDK Android Studio (если подходит).
- Settings -> Languages & Frameworks -> Android SDK: установлен Android SDK 35.
- Если Sync красный, смотри именно первую ошибку на вкладке Build, Kotlin менять бесполезно.
