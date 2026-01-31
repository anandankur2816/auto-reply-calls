# Auto Reply Calls 📱

An Android app that automatically sends SMS replies to missed calls based on your current status. Never leave callers hanging when you're busy, in a meeting, driving, or at lunch!

## ✨ Features

- **🍽️ At Lunch** - "Hey! I'm at lunch right now. Will call you back shortly."
- **📋 In Meeting** - "I'm currently in a meeting and can't take calls. I'll get back to you as soon as I'm free."
- **🚗 Driving** - "I'm driving right now and can't answer. I'll call you back when I reach safely."
- **🔕 Busy** - "I'm a bit busy at the moment. Will return your call soon!"
- **✍️ Custom** - Set your own personalized message

### Additional Features
- **One-tap status switching** - Quickly change your status with a single tap
- **Spam prevention** - Won't send multiple messages to the same number within 5 minutes
- **Works in background** - No need to keep the app open
- **Auto-signature** - Messages include "-Sent from Auto Reply Calls"

## 🤔 Problem It Solves

We've all been there:
- You're in an important meeting and can't answer
- You're driving and it's unsafe (and illegal!) to pick up
- You're at lunch with friends and don't want to be rude
- You're simply busy and will call back later

Without this app, callers are left wondering if you're ignoring them. **Auto Reply Calls** instantly lets them know you saw their call and will get back to them!

## 🏗️ How It Works

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Incoming      │     │   CallReceiver   │     │   SmsSender     │
│   Call          │ ──► │   (Broadcast     │ ──► │   (Sends SMS    │
│                 │     │    Receiver)     │     │    with status) │
└─────────────────┘     └──────────────────┘     └─────────────────┘
         │                       │
         │                       ▼
         │              ┌──────────────────┐
         │              │ PreferencesManager│
         │              │ (Checks if       │
         │              │  enabled + spam) │
         │              └──────────────────┘
         │
         ▼
┌─────────────────┐
│  RINGING → IDLE │
│  (Missed Call!) │
└─────────────────┘
```

1. **CallReceiver** listens for phone state changes
2. Detects missed calls (RINGING → IDLE without answering)
3. **PreferencesManager** checks if auto-reply is enabled and spam prevention
4. **SmsSender** sends the appropriate status message via SMS

## 📋 Required Permissions

| Permission | Why It's Needed |
|------------|-----------------|
| `READ_PHONE_STATE` | To detect incoming calls |
| `READ_CALL_LOG` | To identify the caller's number |
| `SEND_SMS` | To send the auto-reply message |

## 🚀 Getting Started

### Prerequisites
- Android Studio (or command-line build tools)
- JDK 17+
- Android SDK 34

### Build from Source

```bash
# Clone the repository
git clone https://github.com/anandankur2816/auto-reply-calls.git
cd auto-reply-calls

# Build debug APK
./gradlew assembleDebug

# The APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

### Build Release APK

For release builds, set up signing credentials:

```bash
# Set environment variables
export SIGNING_STORE_PASSWORD=your_keystore_password
export SIGNING_KEY_ALIAS=your_key_alias
export SIGNING_KEY_PASSWORD=your_key_password

# Build release APK
./gradlew assembleRelease
```

## 🔧 Tech Stack

- **Language:** Kotlin
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Architecture:** Simple MVVM-like with SharedPreferences
- **UI:** View Binding + Material Design

## 📁 Project Structure

```
app/src/main/java/com/autoreply/calls/
├── MainActivity.kt          # Main UI with status selection
├── data/
│   ├── PreferencesManager.kt  # SharedPreferences wrapper
│   └── StatusType.kt          # Enum for status types
├── receiver/
│   └── CallReceiver.kt        # BroadcastReceiver for call states
└── sms/
    └── SmsSender.kt           # SMS sending utility
```

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest new features
- Submit pull requests

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Material Design for the beautiful UI components
- The Android community for inspiration

---

**Made with ❤️ for those who are too busy to answer but too polite to ignore!**
