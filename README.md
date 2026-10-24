
# Akıllı Finans Android

**Akıllı Finans**, Borsa İstanbul hisselerini takip etmek, portföyünü görüntülemek ve temel finansal çarpanlar üzerinden hisse değerlemelerini incelemek amacıyla geliştirilen bir Android uygulamasıdır.

> **Project Status:** In Progress

## Features

### Portfolio & Watchlist

- Borsa İstanbul hisselerini kod, şirket adı, sektör, fiyat ve günlük yüzde değişimiyle görüntüleme
- Tüm hisseler ve favori hisseler arasında sekmeli gezinme
- Takip edilen hisse sayısı ve günlük getiri göstergeleri
- Hisseleri favorilere ekleme ve favorilerden çıkarma
- FastAPI backend üzerinden hisse verilerini yenileme

### Stock Details & Valuation Analysis

- F/K (P/E), PD/DD (P/B), FD/FAVÖK (EV/EBITDA), Net Borç/FAVÖK ve PEG çarpanlarını görüntüleme
- Hissenin F/K oranını sektör ortalamasıyla karşılaştırma
- Sektör ortalamalarına göre iskonto oranını hesaplama
- Finansal göstergeleri ve analiz sonuçlarını görsel kartlarla sunma

### Offline-First Support

- Room veritabanı üzerinden yerel veri saklama ve önbellekleme
- Sunucuya erişilemediğinde önbellekteki verileri görüntüleme
- Bağlantı durumunu kullanıcıya bildirme

### Backend Integration

- FastAPI tabanlı REST API ile haberleşme
- Retrofit üzerinden API istekleri
- `/api/stocks` ve `/api/stocks/{symbol}` uçları üzerinden hisse verilerine erişim

## Tech Stack

### Android

- Kotlin
- Jetpack Compose
- Room
- Retrofit

### Backend

- Python
- FastAPI
- REST API

## Architecture Overview

Android uygulaması, hisse verilerini FastAPI tabanlı backend üzerinden alır. Room, yerel veri saklama ve çevrimdışı erişim için kullanılır. Retrofit ise Android uygulaması ile REST API arasındaki iletişimi sağlar.

## Getting Started

1. Repository'yi bilgisayarına klonla.
2. Projeyi Android Studio ile aç.
3. Gerekli Gradle senkronizasyonunun tamamlanmasını bekle.
4. Backend gerektiren özellikleri kullanmak için FastAPI sunucusunun yapılandırılmış ve erişilebilir olduğundan emin ol.
5. Uygulamayı uyumlu bir Android emülatöründe veya cihazında çalıştır.

**Note:** Backend bağlantı adresi ve yerel ortam ayarları, kullandığın geliştirme ortamına göre yapılandırılmalıdır.

## Current Status

The application is under active development. Features, valuation logic, and documentation may change as development progresses.

## Disclaimer

This application is a software development project for financial data tracking and analysis. Its valuation indicators should not be interpreted as investment advice. Data accuracy depends on the configured data source and backend.

