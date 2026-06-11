# QuitDays — מדריך הצגה (Presentation Guide)

אפליקציית Android לגמילה מהרגלים. המשתמש מגדיר הרגל (עישון, סוכר...), מסמן כל יום אם היה "נקי" או "נשבר", ורואה רצף ימים, שיא, וכמה כסף חסך.

## ארכיטקטורה — MVVM

```
┌─────────────────────────────────────────────────────┐
│  UI (Activities / Fragments)                        │
│  מציג נתונים, מאזין ל-LiveData                       │
└──────────────────────┬──────────────────────────────┘
                       │ observe()
┌──────────────────────▼──────────────────────────────┐
│  HabitViewModel                                     │
│  מחבר בין המסכים לנתונים, שורד סיבוב מסך             │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│  HabitRepository (Singleton)                        │
│  נקודת גישה יחידה לנתונים, כתיבות ב-Thread רקע       │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│  Room Database (SQLite)  —  quitdays.db             │
│  טבלאות: habits, day_logs                           │
└─────────────────────────────────────────────────────┘
```

**למה MVVM?** ה-UI לא ניגש ישירות למסד הנתונים. כל שכבה עם אחריות אחת. `LiveData` מעדכן את המסך אוטומטית כשהנתונים משתנים.

## הקבצים (19 קבצי Java)

### `models/` — מודל הנתונים
| קובץ | תפקיד |
|------|-------|
| `Habit.java` | ישות Room — הרגל אחד (שם, קטגוריה, תאריך גמילה, עלות יומית, צבע). כולל enum `HabitCategory` ו-`HabitWithLogs` (הרגל + כל הלוגים שלו ב-Relation) |
| `DayLog.java` | ישות Room — רישום יום אחד (נקי/נשבר, דרגת תשוקה, הערות). אינדקס ייחודי על (habit_id, log_date) — אי אפשר לרשום פעמיים אותו יום |

### `db/` — מסד הנתונים
| קובץ | תפקיד |
|------|-------|
| `AppDatabase.java` | מגדיר את Room (גרסה 2 + Migration). מכיל בפנים `Converters` — ממיר LocalDate ו-enums למחרוזות כי SQLite לא מכיר אותם |
| `dao/AppDao.java` | כל שאילתות ה-SQL: הוספה, עדכון, מחיקה, שליפות עם LiveData |

### `repositories/`
| קובץ | תפקיד |
|------|-------|
| `HabitRepository.java` | Singleton. כל כתיבה רצה על `ExecutorService` (Thread רקע) — אסור לכתוב ל-DB מה-UI Thread. כולל לוגיקת "נשברתי": שומר שיא רצף ומאפס תאריך |

### `viewmodels/`
| קובץ | תפקיד |
|------|-------|
| `HabitViewModel.java` | ה-ViewModel היחיד. חושף LiveData למסכים. `switchMap` — כשמשנים habitId, כל הנתונים מתחלפים אוטומטית |

### `activities/` — מסכים
| קובץ | תפקיד |
|------|-------|
| `BaseActivity.java` | אבא של כל ה-Activities — כופה RTL ועוזר להצגת שגיאות |
| `SplashActivity.java` | מסך פתיחה 1.5 שניות |
| `MainActivity.java` | מסך ראשי — BottomNavigation עם 3 פרגמנטים |
| `AddHabitActivity.java` | מסך אחד לשני מצבים: הוספה (בלי Extra) ועריכה (עם `EXTRA_HABIT_ID` — הטופס מתמלא מראש) |
| `HabitDetailActivity.java` | פרטי הרגל: רצף, ימים נקיים, כסף שנחסך. כפתורים "היום היה נקי" / "נשברתי" עם דיאלוגים |

### `fragments/`
| קובץ | תפקיד |
|------|-------|
| `HabitsFragment.java` | רשימת הרגלים ב-RecyclerView + מצב ריק + FAB להוספה |
| `StatsFragment.java` | 4 כרטיסי סטטיסטיקה מצטברת על כל ההרגלים |
| `SettingsFragment.java` | הגדרות — הפעלת תזכורת יומית + בחירת שעה |

### `adapters/`
| קובץ | תפקיד |
|------|-------|
| `HabitsAdapter.java` | RecyclerView.Adapter — מציג כרטיס לכל הרגל, לחיצה פותחת פרטים |

### `utils/` + `workers/`
| קובץ | תפקיד |
|------|-------|
| `DateUtils.java` | חישוב רצף נוכחי, רצף הכי ארוך, כסף שנחסך |
| `ValidationUtils.java` | בדיקת קלט בטופס (שם 2–50 תווים, תאריך לא עתידי, עלות 0–10,000) |
| `NotificationUtil.java` | יצירת ערוץ והצגת התראות |
| `workers/DailyReminderWorker.java` | WorkManager — רץ כל יום בשעה שנבחרה, בודק ב-DB אם סומן היום, ואם לא — שולח תזכורת |

## ספריות (Libraries)

| ספרייה | שימוש |
|--------|-------|
| **Room** | ORM מעל SQLite — אנוטציות במקום SQL ידני |
| **LiveData + ViewModel** (Jetpack) | עדכון UI אוטומטי, שרידות סיבוב מסך |
| **WorkManager** | תזכורת יומית גם כשהאפליקציה סגורה |
| **Material Design** | כרטיסים, FAB, TextInputLayout, DatePicker, Slider |
| **RecyclerView** | רשימת הרגלים יעילה |

## זרימת נתונים לדוגמה — "סימנתי יום נקי"

1. משתמש לוחץ "היום היה נקי ✓" ב-`HabitDetailActivity`
2. נפתח דיאלוג (`dialog_log_clean.xml`) — Slider תשוקה + הערות
3. שמירה → `viewModel.markClean(...)` → `repository.logCleanDay(...)`
4. Repository יוצר `DayLog` עם סטטוס CLEAN וכותב ל-DB ב-Thread רקע
5. Room מעדכן את ה-LiveData → המסך מתעדכן לבד: ימים נקיים +1, כסף נחסך גדל

## שאלות שהמורה עשוי לשאול

- **למה Singleton ב-Repository?** עותק אחד של חיבור ל-DB לכל האפליקציה.
- **למה ExecutorService?** כתיבה ל-DB על ה-UI Thread תוקעת את המסך — Room זורק Exception.
- **מה זה TypeConverter?** SQLite שומר רק טיפוסים בסיסיים. ממירים `LocalDate` → String.
- **מה זה Migration?** כשמוסיפים עמודה (best_streak בגרסה 2) בלי למחוק נתוני משתמשים קיימים.
- **למה LiveData?** המסך נרשם פעם אחת ומקבל עדכונים אוטומטית, בלי דליפות זיכרון (מודע ל-Lifecycle).
