# MoneyMap 💰

MoneyMap is a modern, intuitive personal finance and expense tracking application built for Android. It helps users gain control of their financial life by providing a clear overview of their spending habits, transaction history, and visual insights.

## ✨ Features

-   **Dashboard:** A comprehensive overview of your financial health, showing current balances and recent activity.
-   **Transaction Management:** Easily log income and expenses, categorize transactions, and edit or delete them as needed.
-   **Insights & Reports:** Visual representations of your spending patterns using interactive charts to help you identify where your money goes.
-   **Categorization:** Organize transactions into categories (e.g., Food, Transport, Entertainment) for better tracking.
-   **Dark/Light Mode:** Full support for Material 3 dynamic theming and user-preferred display modes.
-   **Local Storage:** Your data stays private and secure on your device using Room database.

## 🛠️ Tech Stack

-   **Language:** [Kotlin](https://kotlinlang.org/)
-   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Modern Android UI)
-   **Design System:** [Material 3](https://m3.material.io/)
-   **Architecture:** MVVM (Model-View-ViewModel)
-   **Database:** [Room](https://developer.android.com/training/data-storage/room)
-   **Navigation:** [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
-   **Charts:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
-   **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
-   **Preferences:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

## 🏗️ Project Structure

```text
com.example.moneymap/
├── data/           # Database, Models, and Repositories
├── preferences/    # User settings and DataStore implementation
├── ui/             # UI Components and Screens
│   ├── add/        # Add/Edit Transaction screens
│   ├── dashboard/  # Main overview screen
│   ├── reports/    # Analytics and Charts
│   ├── settings/   # App settings and preferences
│   ├── transactions/ # Transaction history list
│   └── theme/      # Material 3 theme definitions
└── util/           # Helper classes and extensions
```

## 🚀 Getting Started

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/MoneyMap.git
    ```
2.  **Open in Android Studio:**
    Import the project into the latest version of Android Studio (Ladybug or newer recommended).
3.  **Build and Run:**
    Select the `app` module and run it on an emulator or physical device running Android 8.0 (API 26) or higher.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
