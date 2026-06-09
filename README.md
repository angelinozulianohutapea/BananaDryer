<div align="center">

# 🍌 BananaDryer

### Aplikasi Android untuk Monitoring & Kontrol Mesin Pengering Pisang Otomatis

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![ESP32](https://img.shields.io/badge/Hardware-ESP32-E7352C?style=for-the-badge&logo=espressif&logoColor=white)](https://espressif.com)
[![License](https://img.shields.io/badge/License-Academic-blue?style=for-the-badge)](LICENSE)

<br/>

> Sistem kendali mesin pengering pisang berbasis IoT — dikontrol dari genggaman tangan melalui jaringan WiFi lokal.

<br/>

</div>

---

## 📱 Tampilan Aplikasi

| Dashboard | Produksi | Riwayat | Detail | Pengaturan |
|:---------:|:--------:|:-------:|:------:|:----------:|
| Monitoring real-time | Konfigurasi mesin | Log sesi produksi | Detail sesi | Koneksi ESP32 |

---

## ✨ Fitur Utama

| Fitur | Keterangan |
|-------|------------|
| 📡 **Monitoring Real-time** | Suhu & kelembaban diperbarui setiap 2 detik |
| 📊 **Grafik Suhu Live** | Visualisasi perubahan suhu selama proses |
| 🟢 **Indikator Koneksi** | Status koneksi ESP32 terlihat langsung |
| ⚙️ **Konfigurasi Mesin** | Input TB1, TB2, DC Speed sebelum proses |
| ⏱️ **Timer Akurat** | Progress bar & countdown berdasarkan TB1+TB2 |
| 🔄 **Phase Indicator** | Indikator fase Pengiris dan Pengering |
| 🗄️ **Database Lokal** | Riwayat produksi tersimpan di Room/SQLite |
| 🗑️ **Swipe to Delete** | Hapus riwayat dengan geser kiri/kanan |
| 📄 **Export PDF** | Export seluruh riwayat ke file PDF |
| 🔔 **Notifikasi Push** | Notifikasi otomatis saat FINISHED atau ERROR |

---

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

---

## 📁 Struktur Project

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

---

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

---

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

---

## 🔧 Hardware

| Komponen | Fungsi |
|----------|--------|
| ESP32 | Web server HTTP + komunikasi WiFi |
| Arduino Nano | Kontroler aktuator utama |
| DHT21 | Sensor suhu dan kelembaban |
| TB6600 #1 | Driver stepper motor pengiris |
| TB6600 #2 | Driver stepper motor pengering |
| L298N | Driver motor DC konveyor |
| Relay 2 Channel | Kontrol elemen pemanas |

---

## 🛠️ Teknologi

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

---

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

---

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

---

## 👤 Developer

**Angelino Zuliano Hutapea**
Institut Teknologi Del

---

<div align="center">

Tetapma BPJS 

</div>
