```markdown
# GeoQuiz

A simple True/False geography quiz app for Android, built in Java.

## Features

- **11 geography & space questions** — answer True or False and get instant feedback.
- **Rotation-safe scoring** — your progress and score survive switching between
  portrait and landscape; the score no longer resets to 0 on rotation.
- **Anti-cheat lock** — once you answer a question, its True/False buttons lock
  so you can't tap both options to inflate your success rate.
- **In-app language switching** — toggle between English and Sinhala (සිංහල)
  at runtime with a single tap, no need to change your phone's system language.
- **Material Design UI** — question card, colored answer buttons, and a
  progress indicator ("Question X of Y").

## Tech stack

- Java
- Android SDK (AppCompat, Material Components, ConstraintLayout)
- `AppCompatDelegate.setApplicationLocales()` for per-app language switching

## Project structure

```
app/src/main/
├── AndroidManifest.xml
├── java/uk/ac/wlv/geoquiz/
│   ├── MainActivity.java
│   └── Question.java
└── res/
    ├── layout/activity_main.xml          (portrait)
    ├── layout-land/activity_main.xml     (landscape)
    ├── values/
    │   ├── colors.xml
    │   └── strings.xml
    ├── values-si/strings.xml             (Sinhala)
    └── xml/locales_config.xml
```

## How it works

### Rotation-safe scoring
`MainActivity` overrides `onSaveInstanceState()` to persist the current
question index, the number of questions answered, the number correct, and
which individual questions have already been answered. `onCreate()` restores
all of this from the saved `Bundle`, so rotating the device (or switching
language, which also recreates the Activity) never resets your progress.

### Anti-cheat lock
Each `Question` tracks an `answered` flag. Once you tap True or False on a
question, both buttons are disabled for that question until you navigate to
a different, unanswered one — so you can't tap both options back-to-back to
inflate your success rate.

### Language switching
Tapping the language button calls
`AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(...))`,
which recreates the Activity in the new language using the matching
`values-si/strings.xml` or `values/strings.xml` resources. Because state is
saved and restored (see above), the score and current question are preserved
across the switch.

## Getting started

1. Clone the repo:
   ```
   git clone https://github.com/<your-username>/GeoQuiz.git
   ```
2. Open the folder in Android Studio.
3. Let Gradle sync, then run on an emulator or device.

## Requirements

Make sure `app/build.gradle` includes at least:
```
implementation "androidx.appcompat:appcompat:1.6.1"
implementation "com.google.android.material:material:1.12.0"
implementation "androidx.constraintlayout:constraintlayout:2.1.4"
```
And that `Theme.GeoQuiz` in `themes.xml` has a `Theme.MaterialComponents...`
(or `Theme.Material3...`) parent, since the UI uses Material buttons and cards.

## Author

Dilmi — BSc (Hons) Computer Science (Software Engineering) Top-Up,
CINEC Campus / University of Wolverhampton.
```
