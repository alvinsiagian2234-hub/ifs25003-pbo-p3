package domain.entity;

/**
 * Jenis transaksi keuangan.
 * Urutan deklarasi dipakai oleh {@link SortOption} untuk pengurutan berdasarkan tipe.
 */
public enum TransactionType {
    /** Uang masuk. */
    INCOME,

    /** Uang keluar. */
    EXPENSE
}
