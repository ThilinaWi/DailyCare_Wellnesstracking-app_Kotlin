# DailyCare - Wellness Tracking App 🌟

A comprehensive wellness tracking Android application built with Kotlin that helps users monitor their daily habits and mood patterns for better mental health and personal growth.

## 📱 Features

### 🎯 Core Functionality
- **Habits Tracker**: Easy-to-use habit management with progress tracking
- **Mood Journal**: Engaging mood logging with multiple entries per day
- **Enhanced Home Dashboard**: Real-time overview of your wellness data

### 🎨 User Experience
- **Modern UI**: Clean Material Design with soothing blue color scheme
- **Intuitive Navigation**: Bottom navigation with 5 main sections
- **Onboarding Flow**: Smooth introduction for new users

### 📊 Mood Tracking Features
- **Multiple Daily Entries**: Log your mood multiple times throughout the day
- **Timestamp Support**: Automatic time tracking for each mood entry
- **Custom Notes**: Add personal descriptions to your mood entries
- **Delete Functionality**: Remove specific mood entries with one tap
- **Smart History**: View mood history with "Today 14:30" format for recent entries

### 🏆 Habits Management
- **Full CRUD Operations**: Create, view, update, and delete habits
- **Progress Visualization**: Track your habit completion over time
- **Daily Tracking**: Mark habits as complete each day

## 🛠️ Technical Details

### Architecture
- **Language**: Kotlin
- **Architecture Pattern**: MVVM with Fragment-based navigation
- **Data Persistence**: SharedPreferences with JSON serialization
- **UI Framework**: ViewBinding with Material Design Components

### Dependencies
- Material Design Components
- MPAndroidChart for data visualization
- ViewPager2 for smooth navigation
- RecyclerView for efficient list displays
- WorkManager for background tasks

### Color Scheme
Following the 60/30/10 design rule with calming blue tones:
- **Primary (60%)**: Soft light blue tones (#E3F2FD, #BBDEFB, #B2EBF2)
- **Secondary (30%)**: Darker blue accents (#1976D2, #0D47A1, #1565C0)
- **Accent (10%)**: Vibrant blue highlights (#2196F3, #00BCD4, #03DAC6)

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34

### Installation
1. Clone the repository
```bash
git clone https://github.com/ThilinaWi/DailyCare_Wellnesstracking-app_Kotlin.git
```

2. Open the project in Android Studio

3. Build and run the app
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/dailycare/
│   │   ├── activities/          # Main activities
│   │   ├── fragments/           # Fragment implementations
│   │   ├── adapters/           # RecyclerView adapters
│   │   ├── models/             # Data models
│   │   └── utils/              # Utility classes
│   ├── res/
│   │   ├── layout/             # XML layouts
│   │   ├── values/             # Colors, strings, styles
│   │   └── drawable/           # Icons and graphics
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 🔧 Key Components

### Activities
- `MainActivity`: Entry point with onboarding/login routing
- `DashboardActivity`: Main app interface with bottom navigation

### Fragments
- `DashboardFragment`: Home overview with real-time data
- `HabitsFragment`: Complete habit management
- `MoodJournalFragment`: Mood tracking with crash-safe implementation
- `ProfileFragment`: User profile and settings

### Data Management
- `PreferencesManager`: Singleton for data persistence
- `MoodEntry`: Model for mood data with timestamp and descriptions
- `HabitEntry`: Model for habit tracking data

## 🛡️ Safety Features

- **Crash-Safe Implementation**: Comprehensive try-catch blocks
- **Defensive Programming**: Safe UI operations and fallback mechanisms
- **Data Integrity**: Reliable persistence with error handling
- **Memory Management**: Proper lifecycle management for fragments

## 📈 Future Enhancements

- [ ] Data export functionality
- [ ] Advanced analytics and insights
- [ ] Reminder notifications
- [ ] Cloud synchronization
- [ ] Social sharing features
- [ ] Customizable themes

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Developer

**Thilina Wijesinghe**
- GitHub: [@ThilinaWi](https://github.com/ThilinaWi)

## 📞 Support

If you have any questions or need help with the app, please create an issue in this repository.

---

*Built with ❤️ for better mental health and wellness tracking*