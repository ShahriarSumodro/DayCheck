# Day Check

A personal productivity app for managing daily notes and tasks, inspired by Google Keep and Google Calendar.

## Features

- **Monthly Calendar View**: Visual calendar showing dates with notes
- **Daily Note Management**: Create, edit, and organize notes for specific dates
- **Checklist Support**: Add checklist items to notes with completion tracking
- **Reminders**: Set notifications for important notes and tasks
- **Archive System**: Completed notes are automatically archived
- **Search & Filter**: Find notes quickly with search and filtering options
- **Dark Mode**: Follows system theme preferences
- **Local Storage**: All data stored locally using Room database

## Architecture

- **Language**: Kotlin
- **UI**: XML layouts with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room for local persistence
- **Navigation**: Android Navigation Component
- **Notifications**: AlarmManager for reliable reminders

## Project Structure

```
app/src/main/java/com/daycheck/app/
├── data/
│   ├── dao/                    # Data Access Objects
│   ├── db/                     # Database configuration
│   ├── models/                 # Entity classes
│   └── repository/             # Repository pattern implementation
├── notifications/              # Notification system
├── ui/
│   ├── adapters/              # RecyclerView adapters
│   ├── dialogs/               # Dialog fragments
│   ├── fragments/             # Main UI fragments
│   └── MainActivity.kt        # Main activity
├── viewmodel/                 # ViewModels for MVVM
└── DayCheckApplication.kt     # Application class
```

## Setup Instructions

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or later
- Android SDK 21+ (Android 5.0)
- Kotlin 1.9.10+

### Installation

1. **Clone or download the project**
   ```bash
   git clone <repository-url>
   cd DayCheck2
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the DayCheck2 folder and select it

3. **Sync the project**
   - Android Studio will automatically sync Gradle files
   - Wait for the sync to complete

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

### Build Configuration

The project uses the following key dependencies:

- **AndroidX Libraries**: Core, AppCompat, Material Design
- **Room Database**: For local data persistence
- **Navigation Component**: For fragment navigation
- **Lifecycle Components**: ViewModel and LiveData
- **WorkManager**: For background tasks
- **Coroutines**: For asynchronous operations

## Usage

### Creating Notes

1. Open the app to see the monthly calendar
2. Tap on any date to view that day's notes
3. Tap the "+" button to create a new note
4. Fill in the note details:
   - Title (optional)
   - Content/body
   - Link (optional)
   - Checklist items (optional)
   - Reminder (optional)

### Managing Notes

- **Complete Notes**: Check the checkbox to mark as complete
- **Edit Notes**: Tap on a note to edit it
- **Delete Notes**: Use the menu button on each note
- **Archive**: Completed notes are automatically archived

### Calendar Features

- **Month Navigation**: Use arrow buttons to navigate months
- **Date Indicators**: Green dots show dates with notes
- **Quick Access**: Tap any date to view that day's notes

### Archive & Search

- **View Archived Notes**: Use the Archive tab
- **Search Notes**: Use the search bar to find specific notes
- **Filter Notes**: Filter by completion status
- **Restore Notes**: Move notes back from archive

### Settings

- **Notifications**: Enable/disable reminder notifications
- **Theme**: Follow system theme or set manually
- **Data Management**: Clear all app data if needed

## Database Schema

### Notes Table
- `id`: Primary key (auto-generated)
- `title`: Note title (optional)
- `body`: Note content
- `link`: Associated URL (optional)
- `date`: Date the note belongs to
- `hasChecklist`: Boolean flag for checklist support
- `isCompleted`: Completion status
- `isArchived`: Archive status
- `reminderTimestamp`: Reminder time (optional)
- `color`: Note color (optional)
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

### Checklist Items Table
- `id`: Primary key (auto-generated)
- `noteId`: Foreign key to notes table
- `text`: Checklist item text
- `isChecked`: Completion status
- `order`: Display order

## Key Files

### Main Activity
- `MainActivity.kt`: Hosts navigation and manages fragments

### Fragments
- `CalendarFragment.kt`: Monthly calendar view
- `DayFragment.kt`: Daily notes view
- `ArchiveFragment.kt`: Archived notes management
- `SettingsFragment.kt`: App settings and preferences

### Data Layer
- `AppDatabase.kt`: Room database configuration
- `NoteDao.kt`: Database operations for notes
- `ChecklistDao.kt`: Database operations for checklist items
- `NoteRepository.kt`: Repository pattern implementation

### ViewModels
- `CalendarViewModel.kt`: Calendar state management
- `DayViewModel.kt`: Daily notes management
- `ArchiveViewModel.kt`: Archive functionality
- `SettingsViewModel.kt`: Settings management

### Adapters
- `CalendarAdapter.kt`: Calendar grid display
- `NotesAdapter.kt`: Notes list display
- `ChecklistInputAdapter.kt`: Checklist item input

## Customization

### Themes
- Modify `res/values/styles.xml` for light theme
- Modify `res/values-night/colors.xml` for dark theme
- Update `res/values/colors.xml` for custom colors

### Layouts
- All layouts are in `res/layout/`
- Modify XML files to change UI appearance
- Use Material Design 3 components for consistency

### Functionality
- Add new features by extending ViewModels
- Modify database schema in `models/` package
- Add new fragments in `ui/fragments/`

## Troubleshooting

### Common Issues

1. **Build Errors**: Ensure all dependencies are properly synced
2. **Database Issues**: Clear app data and restart
3. **Notification Issues**: Check notification permissions
4. **Theme Issues**: Verify color resources are properly defined

### Performance Tips

- The app uses Room database for efficient local storage
- RecyclerView adapters use DiffUtil for optimal performance
- Images and icons are vector-based for scalability

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is for personal use and educational purposes.

## Support

For issues or questions, please check the troubleshooting section or create an issue in the repository.