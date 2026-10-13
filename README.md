# ifs18005-java-oop — Studi Kasus Java Exception & Generic

Lima aplikasi konsol dengan **Clean Architecture**, **Java Generics**, dan **Custom Checked Exception**.

| Folder | Entity | Fitur |
|---|---|---|
| `catatan-keuangan/` | `Transaction`, `TransactionType` | tambah pemasukan/pengeluaran, cari, urutkan, lihat saldo, hapus |
| `buku-tamu/` | `Guest` | daftarkan tamu, cari, hapus |
| `inventaris-barang/` | `Item` | tambah, ubah stok, cari, urutkan, hapus |
| `jadwal-kegiatan/` | `Activity`, `Day` | tambah, ubah parsial, cari, urutkan, hapus |
| `kontak-teman/` | `Contact` | tambah, ubah parsial, cari, urutkan, hapus |

## Struktur Layer (sama di setiap studi kasus)

```
src/
├── domain/
│   ├── entity/        # Entity + enum (SortOption membawa Comparator-nya sendiri)
│   ├── exception/     # DomainException, EntityNotFoundException, ValidationException
│   └── repository/    # IRepository<T, ID> + port spesifik (I<Entity>Repository)
├── usecase/           # Logika bisnis, tanpa I/O, melempar domain exception
├── adapter/
│   ├── repository/    # InMemoryRepository<T, ID> + implementasi konkret
│   └── presenter/     # Format output layar
├── framework/
│   ├── view/          # UI konsol + try-catch exception domain
│   └── util/          # InputUtil (baca & konversi input)
└── App.java           # Composition Root
```

Dependency Rule: `domain ← usecase ← adapter / framework`.

## Compile & Jalankan

```bash
cd inventaris-barang            # pilih salah satu studi kasus
javac -d output -sourcepath src src/App.java
java -cp output App
```

## Menjalankan Seluruh Test Case

```bash
bash run-tests.sh
```

Setiap `test-cases/TC-0X.tc` adalah input; `TC-0X.expected` adalah baris keluaran
yang harus muncul **berurutan** pada output program.

## Catatan Desain

- **Satu sumber validasi**: aturan bisnis (kosong, stok ≤ 0, format waktu/email, hari tidak dikenal)
  ada di use case dan dilaporkan lewat `ValidationException`; view hanya menampilkan pesannya
  melalui `presenter.showError(...)`.
- **Tanpa duplikasi**: konversi angka (`parseInt`/`parseLong`), pembacaan menu, dan perintah batal `x`
  dipusatkan di `InputUtil`; format daftar dipusatkan di `printList` pada presenter.
- **Tanpa nilai `null` ambigu pada alur menu**: pemetaan pilihan urutkan memakai
  `Map<String, SortOption>` + `Optional`.
- **Salinan defensif**: `findAll()` dan `findBy()` tidak mengembalikan referensi list internal;
  `update()` benar-benar menimpa entitas dan menolak ID yang belum tersimpan.
- **Urutan menu Urutkan (Catatan Keuangan)**: 1 Jumlah terbesar, 2 Jumlah terkecil,
  3 Pemasukan dulu, 4 Pengeluaran dulu (sesuai keluaran TC-02).
