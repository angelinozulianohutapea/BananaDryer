<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&height=280&color=0:F7A41D,100:FFD95A&text=BananaDryer&fontSize=60&fontColor=ffffff&animation=fadeIn"/>

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![ESP32](https://img.shields.io/badge/Hardware-ESP32-E7352C?style=for-the-badge&logo=espressif&logoColor=white)](https://espressif.com)
[![Retrofit](https://img.shields.io/badge/HTTP-Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white)](https://square.github.io/retrofit)
[![Room](https://img.shields.io/badge/Database-Room%20SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![IoT](https://img.shields.io/badge/Category-IoT-FF6B00?style=for-the-badge&logo=homeassistant&logoColor=white)](https://github.com)
[![Status](https://img.shields.io/badge/Status-Active-brightgreen?style=for-the-badge&logo=statuspage&logoColor=white)](https://github.com)
[![Version](https://img.shields.io/badge/Version-1.0.0-blue?style=for-the-badge&logo=semver&logoColor=white)](https://github.com)
[![Build](https://img.shields.io/badge/Build-Passing-success?style=for-the-badge&logo=github-actions&logoColor=white)](https://github.com)

<br/>

> **Sistem kendali mesin pengering pisang berbasis IoT**
> Dikontrol dari genggaman tangan melalui jaringan WiFi lokal.

<br/>

<img src="https://komarev.com/ghpvc/?username=angelinozulianohutapea&style=for-the-badge&color=F7A41D"/>

![Stars](https://img.shields.io/github/stars/angelinozulianohutapea/BananaDryer?style=for-the-badge&color=F7A41D)
![Forks](https://img.shields.io/github/forks/angelinozulianohutapea/BananaDryer?style=for-the-badge&color=FFD95A)
![Issues](https://img.shields.io/github/issues/angelinozulianohutapea/BananaDryer?style=for-the-badge&color=E7352C)
![Last Commit](https://img.shields.io/github/last-commit/angelinozulianohutapea/BananaDryer?style=for-the-badge&color=3DDC84)

</div>

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 📑 Quick Navigation

| | Section |
|--|---------|
| 📱 | [Tampilan Aplikasi](#-tampilan-aplikasi) |
| ✨ | [Fitur Utama](#-fitur-utama) |
| 🏗️ | [Arsitektur](#-arsitektur) |
| 🔌 | [Komunikasi & API](#-komunikasi--api) |
| 🗄️ | [Database](#-database) |
| 🔧 | [Hardware](#-hardware) |
| 🛠️ | [Teknologi](#-teknologi) |
| 🚀 | [Cara Penggunaan](#-cara-penggunaan) |
| ⚙️ | [Instalasi & Build](#-instalasi--build) |
| 👤 | [Developer](#-developer) |

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 📱 Tampilan Aplikasi

<div align="center">

<table>
<tr>
<td align="center"><b>Dashboard</b></td>
<td align="center"><b>Produksi</b></td>
<td align="center"><b>Riwayat</b></td>
<td align="center"><b>Detail</b></td>
<td align="center"><b>Pengaturan</b></td>
</tr>
<tr>
<td align="center">Monitoring real-time</td>
<td align="center">Konfigurasi mesin</td>
<td align="center">Log sesi produksi</td>
<td align="center">Detail sesi</td>
<td align="center">Koneksi ESP32</td>
</tr>
</table>

<br/>

<img src="assets/demo.gif" width="900"/>

</div>

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## ✨ Fitur Utama

<div align="center">
<table>
<tr>
<td align="center" width="33%">
<h3>📡 Monitoring Real-Time</h3>
Suhu & kelembaban diperbarui setiap 2 detik secara otomatis
</td>
<td align="center" width="33%">
<h3>📊 Grafik Suhu Live</h3>
Visualisasi perubahan suhu selama proses berlangsung
</td>
<td align="center" width="33%">
<h3>🟢 Indikator Koneksi</h3>
Status koneksi ESP32 terlihat langsung di dashboard
</td>
</tr>
<tr>
<td align="center" width="33%">
<h3>⚙️ Konfigurasi Mesin</h3>
Input TB1, TB2, DC Speed sebelum memulai proses
</td>
<td align="center" width="33%">
<h3>⏱️ Timer Akurat</h3>
Progress bar & countdown berdasarkan nilai TB1+TB2
</td>
<td align="center" width="33%">
<h3>🔄 Phase Indicator</h3>
Indikator fase Pengiris dan Pengering secara real-time
</td>
</tr>
<tr>
<td align="center" width="33%">
<h3>🗄️ Database Lokal</h3>
Riwayat produksi tersimpan di Room/SQLite secara persisten
</td>
<td align="center" width="33%">
<h3>🗑️ Swipe to Delete</h3>
Hapus riwayat dengan mudah menggunakan geser kiri/kanan
</td>
<td align="center" width="33%">
<h3>📄 Export PDF</h3>
Export seluruh riwayat produksi ke file PDF siap cetak
</td>
</tr>
<tr>
<td align="center" colspan="3">
<h3>🔔 Notifikasi Push</h3>
Notifikasi otomatis muncul saat mesin FINISHED atau ERROR
</td>
</tr>
</table>
</div>

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 🏗️ Arsitektur

Aplikasi menggunakan pola **MVVM (Model-View-ViewModel)** dengan Android Jetpack.

```
┌─────────────────────────────────────────────────────┐
│                   Android App                        │
│                                                      │
│   Fragment (View)                                    │
│        │  observes                                   │
│        ▼                                             │
│   ViewModel  ──────►  Repository                    │
│                            │                         │
│                    ┌───────┴───────┐                 │
│                    ▼               ▼                 │
│               Retrofit API     Room Database         │
│               (HTTP/REST)      (SQLite)              │
└────────────────────┼────────────────────────────────┘
                     │ HTTP
                     ▼
              ┌─────────────┐
              │    ESP32    │  Web Server
              └──────┬──────┘
                     │ UART 9600 baud
                     ▼
              ┌─────────────┐
              │ Arduino Nano│  Kontroler Aktuator
              └──────┬──────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
     DHT21        TB6600       L298N + Relay
  (Sensor)      (Stepper)     (DC + Pemanas)
```

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 📁 Struktur Project

<details>
<summary><b>🗂️ Klik untuk melihat struktur folder lengkap</b></summary>

<br/>

```
BananaDryer/
├── app/src/main/java/com/bananadryer/
│   ├── 📄 MainActivity.kt
│   ├── 📂 data/
│   │   ├── api/
│   │   │   ├── ApiService.kt          # Definisi endpoint HTTP
│   │   │   └── RetrofitClient.kt      # Konfigurasi Retrofit
│   │   ├── model/
│   │   │   ├── MonitorResponse.kt     # Model data monitoring
│   │   │   └── StartResponse.kt       # Model response START
│   │   └── repository/
│   │       └── BananaDryerRepository.kt
│   ├── 📂 database/
│   │   ├── AppDatabase.kt
│   │   ├── dao/HistoryDao.kt
│   │   └── entity/ProductionHistory.kt
│   ├── 📂 ui/
│   │   ├── dashboard/                 # Home + monitoring
│   │   ├── production/                # Konfigurasi & START
│   │   ├── history/                   # Daftar riwayat
│   │   ├── historydetail/             # Detail sesi
│   │   └── settings/                  # Pengaturan IP
│   └── 📂 utils/
│       ├── NotificationHelper.kt
│       ├── PdfExportUtil.kt
│       └── PrefsManager.kt
```

</details>

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 🔌 Komunikasi & API

### Topologi Jaringan

```
┌──────────┐   WiFi (LAN)   ┌──────────┐   UART   ┌─────────────┐
│ Android  │ ◄────────────► │  ESP32   │ ◄───────► │ Arduino     │
│   App    │   HTTP/REST    │ (Server) │  9600bps  │ Nano        │
└──────────┘                └──────────┘           └─────────────┘
```

### Endpoint ESP32

| Method | Endpoint | Fungsi |
|--------|----------|--------|
| `GET` | `/monitor` | Ambil suhu, kelembaban, status mesin |
| `POST` | `/start?tb1=&tb2=&dc=` | Kirim perintah START ke mesin |

### Contoh Response `/monitor`

```json
{
  "temperature": 28.5,
  "humidity": 65.0,
  "status": "RUNNING"
}
```

### Status Mesin

| Status | Keterangan |
|--------|------------|
| `IDLE` | Mesin tidak aktif |
| `RUNNING` | Proses sedang berjalan |
| `FINISHED` | Proses selesai |
| `ERROR` | Terjadi kesalahan |

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 🗄️ Database

Tabel `production_history`:

```
┌────────────────────┬──────────┬───────────────────────────┐
│ Kolom              │ Tipe     │ Keterangan                │
├────────────────────┼──────────┼───────────────────────────┤
│ id                 │ Int      │ Primary key, auto increment│
│ tanggal            │ String   │ Tanggal produksi           │
│ jamMulai           │ String   │ Jam mulai                  │
│ jamSelesai         │ String   │ Jam selesai                │
│ tb1                │ Long     │ Durasi pengiris (ms)       │
│ tb2                │ Long     │ Durasi pengering (ms)      │
│ dc                 │ Int      │ Kecepatan motor (0–255)    │
│ suhuAkhir          │ Float    │ Suhu akhir (°C)            │
│ kelembabanAkhir    │ Float    │ Kelembaban akhir (%)       │
│ status             │ String   │ FINISHED / ERROR           │
└────────────────────┴──────────┴───────────────────────────┘
```

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 🔧 Hardware

<div align="center">
<table>
<tr>
<td align="center" width="25%">
<h3>📡 ESP32</h3>
Web server HTTP<br/>+ komunikasi WiFi
</td>
<td align="center" width="25%">
<h3>🔲 Arduino Nano</h3>
Kontroler<br/>aktuator utama
</td>
<td align="center" width="25%">
<h3>🌡️ DHT21</h3>
Sensor suhu<br/>dan kelembaban
</td>
<td align="center" width="25%">
<h3>⚡ TB6600 #1</h3>
Driver stepper<br/>motor pengiris
</td>
</tr>
<tr>
<td align="center" width="25%">
<h3>⚡ TB6600 #2</h3>
Driver stepper<br/>motor pengering
</td>
<td align="center" width="25%">
<h3>🔌 L298N</h3>
Driver motor<br/>DC konveyor
</td>
<td align="center" colspan="2">
<h3>🔥 Relay 2 Channel</h3>
Kontrol elemen pemanas
</td>
</tr>
</table>
</div>

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 🛠️ Teknologi

<div align="center">

<p>
<img src="https://skillicons.dev/icons?i=kotlin,androidstudio,sqlite,arduino,git,github"/>
</p>

</div>

<br/>

| Komponen | Library | Versi |
|----------|---------|-------|
| Bahasa | Kotlin | 1.9.22 |
| Min SDK | Android 8.0 | API 26 |
| Target SDK | Android 15 | API 35 |
| HTTP Client | Retrofit + Gson | 2.9.0 |
| Database | Room (SQLite) | 2.6.1 |
| Navigasi | Jetpack Navigation | 2.7.7 |
| Async | Kotlinx Coroutines | 1.7.3 |
| PDF | iTextPDF | 5.5.13.3 |
| Build | AGP | 8.7.3 |

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 🚀 Cara Penggunaan

```
1. Nyalakan ESP32 → catat IP Address di Serial Monitor
         │
         ▼
2. Sambungkan HP ke WiFi yang sama dengan ESP32
         │
         ▼
3. Buka app → tab Seting → isi IP Address → Simpan
         │
         ▼
4. Tab Dashboard → tunggu indikator koneksi hijau ●
         │
         ▼
5. Tab Produksi → isi TB1, TB2, DC Speed → START MESIN
         │
         ▼
6. Pantau progres di Dashboard (progress bar + timer)
         │
         ▼
7. Notifikasi muncul saat FINISHED ✅
         │
         ▼
8. Tab Riwayat → lihat detail atau export PDF
```

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## ⚙️ Instalasi & Build

```bash
# Clone repository
git clone https://github.com/angelinozulianohutapea/BananaDryer.git

# Buka di Android Studio
# File → Open → pilih folder BananaDryer

# Sync Gradle, lalu Build → Run
```

**Requirements:**
- Android Studio Hedgehog atau lebih baru
- JDK 11
- Android device / emulator min API 26

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

## 👤 Developer

<div align="center">

<h2>Angelino Zuliano Hutapea</h2>

<p><b>Institut Teknologi Del</b></p>

<p><i>"Difficult does not mean impossible"</i></p>

[![GitHub](https://img.shields.io/badge/GitHub-angelinozulianohutapea-181717?style=for-the-badge&logo=github)](https://github.com/angelinozulianohutapea)

</div>

<img width="100%" src="https://capsule-render.vercel.app/api?type=rect&color=F7A41D&height=3"/>

<div align="center">

### 🍌 BananaDryer

**Smart Banana Dryer Monitoring & Control System**

Made with ❤️ using Kotlin, ESP32 and Arduino Nano

*Institut Teknologi Del — IoT Final Project*

</div>

<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&height=180&section=footer&color=0:F7A41D,100:FFD95A"/>
ENDOFFILE
