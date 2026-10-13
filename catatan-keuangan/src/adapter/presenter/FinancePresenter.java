package adapter.presenter;

import domain.entity.Transaction;
import domain.entity.TransactionType;
import java.util.List;

/**
 * Presenter yang memformat data dari use case menjadi output layar.
 * Memisahkan logika tampilan dari entity, use case, dan view.
 */
public class FinancePresenter {

    /** Memformat satu transaksi menjadi baris teks. */
    private String format(Transaction transaction) {
        return String.format("[%d] %s | Rp %d | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                typeLabel(transaction.getType()));
    }

    /** Menerjemahkan jenis transaksi menjadi label tampilan. */
    private String typeLabel(TransactionType type) {
        return switch (type) {
            case INCOME -> "Pemasukan";
            case EXPENSE -> "Pengeluaran";
        };
    }

    /** Helper umum untuk menampilkan daftar transaksi. */
    private void printList(List<Transaction> transactions, String header, String emptyMessage) {
        System.out.println(header);

        if (transactions.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(format(transaction));
        }
    }

    /** Menampilkan semua transaksi beserta saldo. */
    public void showTransactions(List<Transaction> transactions, long balance) {
        printList(transactions, "Daftar Transaksi:", "- Belum ada transaksi!");
        System.out.printf("Saldo: Rp %d%n", balance);
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Transaction> transactions, String keyword) {
        printList(transactions, "Hasil Pencarian: \"" + keyword + "\"", "- Transaksi tidak ditemukan!");
    }

    /** Menampilkan transaksi yang sudah diurutkan beserta saldo. */
    public void showSortedTransactions(List<Transaction> transactions, long balance) {
        printList(transactions, "Daftar Transaksi (Terurut):", "- Belum ada transaksi!");
        System.out.printf("Saldo: Rp %d%n", balance);
    }

    /** Menampilkan saldo saat ini. */
    public void showCurrentBalance(long balance) {
        System.out.printf("Saldo saat ini: Rp %d%n", balance);
    }

    public void showAddSuccess(Transaction transaction) {
        System.out.printf("Berhasil menambah transaksi: %s%n", format(transaction));
    }

    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus transaksi.");
    }

    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus transaksi dengan ID: %d.%n", id);
    }

    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    public void showInvalidSortOption() {
        System.out.println("[!] Pilihan tidak valid!");
    }

    /** Menampilkan pesan kegagalan validasi dari domain. */
    public void showError(String message) {
        System.out.println("[!] " + message);
    }
}
