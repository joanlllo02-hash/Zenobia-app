
<br/>

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/7xze/Zenobia-app/main/appicon/element/src/main/res/mipmap-xxxhdpi/ic_launcher.webp">
    <img src="https://raw.githubusercontent.com/7xze/Zenobia-app/main/appicon/element/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="Zenobia" width="192" height="192"/>
  </picture>
</p>

<h1 align="center">👑 زنوبيا — Zenobia</h1>

<p align="center">
  <strong>التواصل اللامركزي — بأيدينا</strong><br/>
  <strong>Decentral. Sovereign. Syrian.</strong><br/>
  <strong>فخر برمجة سورية — شركة 7X</strong>
</p>

<p align="center">
  <a href="#-المميزات">المميزات</a> •
  <a href="#-بنية-المشروع">البنية</a> •
  <a href="#-المعمارية">المعمارية</a> •
  <a href="#-طريقة-التشغيل">التشغيل</a> •
  <a href="#-المساهمة">المساهمة</a>
</p>

<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Kotlin-2.4.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin"/></a>
  <a href="#"><img src="https://img.shields.io/badge/Compose-BOM_2026.05-4285F4?logo=jetpackcompose&logoColor=white" alt="Compose"/></a>
  <a href="#"><img src="https://img.shields.io/badge/AGP-9.2.1-3DDC84?logo=android&logoColor=white" alt="AGP"/></a>
  <a href="#"><img src="https://img.shields.io/badge/minSdk-24-4CAF50" alt="minSdk"/></a>
  <a href="#"><img src="https://img.shields.io/badge/targetSdk-37-4CAF50" alt="targetSdk"/></a>
  <a href="#"><img src="https://img.shields.io/badge/license-AGPL--3.0_%7C_Commercial-blue" alt="License"/></a>
</p>

<hr/>

<p dir="rtl" align="right">
<strong>زنوبيا</strong> — تطبيق أندرويد متكامل لمنصة التواصل اللامركزي، مبني بهندسة برمجية سورية خالصة من فريق <strong>7X</strong>. 
<br/>
تطبيق آمن، سريع، ومفتوح المصدر يمنحك التحكم الكامل ببياناتك وتواصلك.

<br/><br/>
<strong>Zenobia</strong> — A fully-featured Android client for decentralized communication. 
Built by <strong>7X</strong>, engineered in Syria. Secure, fast, open-source. Your data, your rules.
</p>

<hr/>

---

## 👑 المميزات — Features

### 🔐 الخصوصية والأمان — Privacy & Security
- تشفير شامل (End-to-End Encryption)
- دعم قفل التطبيق بـ PIN / Biometrics
- المصادقة الثنائية (OIDC / SSO)
- تخزين مشفّر محلياً (SQLCipher)
- تقارير أعطال آلية عبر Sentry
- حماية متقدمة (Rust TLS)

### 💬 المحادثات — Messaging
- محادثات فردية وجماعية
- غرف ذات أدوار وصلاحيات
- الردود، الإشارات، التفاعلات
- دعم الرسائل الصوتية
- تحرير وحذف الرسائل
- بوتات الأوامر (Slash Commands)
- الرسائل المقرّرة (Polls / Events)

### 📎 الوسائط — Media
- مشاركة الصور والفيديو والمستندات
- المشغّل الوسائطي المتكامل
- التسجيل الصوتي
- معاينة الوسائط بتقنية Zoom
- تكامل مع الكاميرا مباشرة

### 🌍 المجتمع — Community
- غرف عامة وخاصة
- مساحات (Spaces) لتنظيم المجموعات
- دليل غرف عام
- إدارة الأعضاء والصلاحيات
- نظام دعوة متطور

### 🎨 التصميم — Design
- واجهة Compose Material3
- دعم الوضع الفاتح / الداكن
- أيقونات مخصصة
- دعم كامل لـ RTL (العربية)
- 40+ لغة مدعومة

### 🔧 متقدم — Advanced
- دعم حسابات متعددة
- إشعارات Firebase / UnifiedPush
- قفل التطبيق (Lock Screen)
- مشاركة الموقع عبر MapLibre
- تحويل النص إلى كلام
- تصدير واستيراد المحادثات
- Deep Link / OAuth

---

## 🧱 بنية المشروع — Project Structure

```
Zenobia/
│
├── app/                           # 📱 نقطة الدخول (Application + MainActivity)
│   ├── src/main/kotlin/com/zenobia/app/
│   │   ├── di/                    #   حقن التبعية (AppGraph, SessionGraph)
│   │   ├── initializer/           #   مهيئات الإقلاع (Crash, Cache, Platform)
│   │   ├── intent/                #   معالجة النوايا والروابط
│   │   ├── oidc/                  #   المصادقة OIDC
│   │   ├── ZenobiaApplication.kt #   نقطة بدء التطبيق
│   │   ├── MainActivity.kt       #   النشاط الرئيسي
│   │   └── MainNode.kt           #   عقدة التنقل الجذرية
│   └── src/main/res/              #   الموارد
│
├── appnav/                        # 🧭 التنقل — Navigation Graph
│   ├── RootFlowNode.kt           #   المُتنقّل الجذري
│   ├── LoggedInFlowNode.kt       #   التنقل بعد تسجيل الدخول
│   ├── NotLoggedInFlowNode.kt    #   التنقل قبل تسجيل الدخول
│   └── RoomFlowNode.kt           #   التنقل داخل الغرفة
│
├── appconfig/                     # ⚙️ إعدادات التطبيق
│
├── annotations/                   # 🏷️ أنوتيشن مخصصة (@ContributesNode)
│
├── codegen/                       # 🤖 KSP — توليد الكود التلقائي
│
├── features/                      # 🧩 الميزات (44 ميزة)
│   ├── messages/                  # 💬 المحادثات
│   ├── login/                     # 🔑 تسجيل الدخول
│   ├── home/                      # 🏠 الشاشة الرئيسية
│   ├── call/                      # 📞 المكالمات
│   ├── location/                  # 📍 الموقع
│   ├── preferences/               # ⚙️ الإعدادات
│   ├── securebackup/              # 🔐 النسخ الاحتياطي
│   ├── lockscreen/                # 🔒 شاشة القفل
│   ├── space/                     # 🗂️ المساحات
│   └── ... (34 أخرى)
│
├── libraries/                     # 📚 المكتبات (50 مكتبة)
│   ├── architecture/              # 🏛️ الأنماط المعمارية
│   ├── designsystem/              # 🎨 نظام التصميم
│   ├── matrix/                    # 🔗 طبقة التواصل اللامركزي
│   ├── matrixui/                  # 🖌️ واجهات التواصل
│   ├── network/                   # 🌐 الشبكات
│   ├── pushproviders/             # 🔔 الإشعارات
│   ├── rustsdk/                   # ⚡ Rust SDK
│   ├── encrypted-db/              # 🔐 قاعدة بيانات مشفرة
│   └── ... (42 أخرى)
│
├── services/                      # 🔌 الخدمات (5 خدمات)
│   ├── analytics/                 # 📊 التحليلات
│   ├── apperror/                  # ❌ إدارة الأخطاء
│   ├── appnavstate/               # 🧭 حالة التنقل
│   └── toolbox/                   # 🧰 صندوق الأدوات
│
├── tests/                         # 🧪 الاختبارات
│   ├── konsist/                   #   اختبارات المعمارية
│   ├── uitests/                   #   اختبارات الشاشات
│   ├── detekt-rules/             #   قواعد تحليل مخصصة
│   └── testutils/                 #   أدوات الاختبار
│
├── appicon/                       # 🖼️ أيقونات التطبيق
│   ├── element/                   #   الأيقونة الافتراضية
│   └── enterprise/                #   أيقونة المؤسسات
│
├── plugins/                       # 🛠️ إضافات Gradle المخصصة
│
├── tools/                         # 🔧 أدوات التطوير
│   ├── detekt/                    #   تحليل الكود
│   ├── lint/                      #   التدقيق
│   ├── docs/                      #   التوثيق
│   ├── release/                   #   الإصدارات
│   └── templates/                 #   قوالب الملفات
│
├── enterprise/                    # 🏢 ميزات المؤسسات
│
├── gradle/                        # 🐘 Gradle Wrapper
│
├── build.gradle.kts               # 📦 ملف البناء الرئيسي
├── settings.gradle.kts            # ⚙️ إعدادات المشروع
├── gradle.properties              # خصائص Gradle
└── gradlew / gradlew.bat          # سكريبتات البناء
```

---

## 🏛️ المعمارية — Architecture

```
          ┌─────────────────────────────────────┐
          │         MainActivity (NodeActivity)  │
          │           splash + edge-to-edge      │
          └──────────────┬──────────────────────┘
                         │
          ┌──────────────▼──────────────────────┐
          │            MainNode                 │
          │     PermanentNavModel — الجذر       │
          └──────────────┬──────────────────────┘
                         │
          ┌──────────────▼──────────────────────┐
          │         RootFlowNode                │
          │     BackStack — شجرة التنقل         │
          └──┬────────────┬───────────┬────────┘
             │            │           │
    ┌────────▼──┐  ┌─────▼─────┐  ┌──▼────────┐
    │ LoginFlow │  │ LoggedIn  │  │ BugReport │
    │ NotLogged │  │ Home/Rooms│  │ SignedOut │
    └───────────┘  └─────┬─────┘  └───────────┘
                         │
              ┌──────────▼──────────┐
              │     RoomFlowNode    │
              │  Timeline الرسائل   │
              └────────────────────┘
```

### 🔷 المبادئ المعمارية — Architectural Principles
- **Node-based Architecture** (Appyx) بدلاً من Navigation Component
- **Unidirectional Data Flow** (MVI-like)
- **Presenter Pattern** — State driven by Compose + Molecule
- **Metro DI** — حقن تبعية compile-time (أسرع من Dagger)
- **API / Impl / Test** — فصل صارم للواجهات عن التنفيذ
- **3-tier DI scoping**: `AppScope` → `SessionScope` → `RoomScope`
- **KSP Codegen** — `@ContributesNode` يولد factories و DI modules تلقائياً

### 🛠 التقنيات — Tech Stack

| المجال | التقنية |
|--------|---------|
| **اللغة** | Kotlin 2.4.0 |
| **الواجهات** | Jetpack Compose + Material3 BOM 2026.05 |
| **التنقل** | Bumble Appyx 1.7.1 |
| **حقن التبعية** | Metro 1.2.1 (Compile-time DI) |
| **المعالجة** | KSP 2.3.9 (Codegen) |
| **الشبكات** | OkHttp 5.4.0 + Retrofit 3.0.0 |
| **الصور** | Coil 3.5.0 |
| **قاعدة البيانات** | SQLDelight 2.3.2 + SQLCipher 4.16.0 |
| **الخريطة** | MapLibre 13.3.0 |
| **الصوت والفيديو** | AndroidX Media3 1.10.1 |
| **التحليلات** | PostHog + Sentry |
| **الاختبارات** | JUnit, MockK, Robolectric, Paparazzi, Roborazzi, Konsist |
| **الجودة** | Detekt, ktlint, OWASP, SonarQube, Kover |

---

## 📋 المتطلبات — Requirements

| المتطلب | الإصدار |
|---------|---------|
| **Android Studio** | Ladybug (2024.3+) أو أعلى |
| **JDK** | 21 (Gradle 9.x يتطلب JDK 21) |
| **Android SDK** | 35+ |
| **NDK** | r29 (أو أحدث) |
| **CMake** | 3.22.1+ |
| **Gradle** | 9.x (الموجود: 8.13 — يُنصح بالترقية) |
| **Kotlin** | 2.4.0 |
| **minSdk** | 24 (FOSS) / 33 (Enterprise) |

---

## 🚀 طريقة التشغيل — Build & Run

### 1️⃣ Clone المشروع

```bash
git clone https://github.com/7xze/Zenobia-app.git
cd Zenobia-app
```

### 2️⃣ إعداد SDK (اختياري)

عدّل `local.properties` ليشير إلى مسار SDK لديك:
```properties
sdk.dir=/path/to/android-sdk
ndk.dir=/path/to/android-ndk-r29
cmake.dir=/path/to/cmake-3.22.1
```

### 3️⃣ بناء المشروع

```bash
# بناء كامل مع اختبارات الجودة
./gradlew assembleGplayDebug

# اختبارات الوحدة
./gradlew testGplayDebugUnitTest

# فحص الجودة الشامل
./gradlew runQualityChecks

# فحص التبعيات الأمنية
./gradlew dependencyCheckAnalyze
```

### 4️⃣ تشغيل على الجهاز

```bash
# تثبيت APK
./gradlew installGplayDebug

# أو افتح Android Studio → اختَر run configuration
```

### 5️⃣ إصدار تجريبي (Nightly)

```bash
./gradlew assembleGplayNightly
```

### 🐳 Docker

```bash
docker build -t zenobia-builder .
docker run --rm -v $(pwd):/workspace zenobia-builder ./gradlew assembleGplayDebug
```

---

## 🧪 الجودة والاختبارات — Quality & Testing

| الفحص | الوصف | الأمر |
|-------|-------|-------|
| **Konsist** | فحص المعمارية والقواعد الهيكلية | `./gradlew :tests:konsist:test` |
| **Detekt** | تحليل الكود الثابت | `./gradlew detekt` |
| **ktlint** | تنسيق الكود | `./gradlew ktlintCheck` |
| **Lint** | فحص أندرويد | `./gradlew lint` |
| **OWASP** | فحص التبعيات الأمنية | `./gradlew dependencyCheckAnalyze` |
| **Screenshot** | اختبارات الشاشات | `./gradlew recordPaparazziDebug` |
| **Coverage** | تغطية الكود (Min 85%) | `./gradlew koverHtmlReport` |
| **كل الفحوص** | أمر شامل | `./gradlew runQualityChecks` |

---

## 📊 إحصائيات — Stats

| المقياس | القيمة |
|---------|--------|
| **موديولات** | 130+ |
| **ميزات** | 44 |
| **مكتبات** | 50 |
| **خدمات** | 5 |
| **لغات** | 40+ |
| **أسطر الكود** | ~500K+ |
| **Kotlin** | 99.2% |
| **C++ (JNI)** | 0.8% |

---

## 📜 الترخيص — License

**زنوبيا — Zenobia** مرخصة بموجب ترخيص مزدوج:

- **AGPL-3.0-only** — للمشاريع مفتوحة المصدر ([راجع الترخيص](./LICENSE-AGPL-3.0))
- **رخصة تجارية** — للاستخدام التجاري والمؤسسات ([راجع الترخيص](./LICENSE-COMMERCIAL))

```
© 2026-2027 7X. جميع الحقوق محفوظة.
فخر برمجة سورية — شركة 7X
```

---

## 👥 المساهمة — Contributing

نرحب بمساهماتكم! يرجى قراءة إرشادات المساهمة قبل إرسال Pull Request.

1. Fork المشروع
2. إنشاء فرع للميزة (`git checkout -b feature/my-feature`)
3. تنفيذ التغييرات
4. تشغيل الاختبارات (`./gradlew runQualityChecks`)
5. رفع الفرع (`git push origin feature/my-feature`)
6. فتح Pull Request

---

<p align="center">
  <strong>👑 زنوبيا — فخر برمجة سورية</strong><br/>
  <strong>7X — Engineering Intelligence</strong>
</p>

<p align="center">
  <a href="https://github.com/7xze">GitHub</a> •
  <a href="mailto:licensing@7x.sy">licensing@7x.sy</a>
</p>

<p align="center">
  <sub>Built with ❤️ from Syria</sub>
</p>
