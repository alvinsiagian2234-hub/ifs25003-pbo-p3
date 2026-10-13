package framework.view;

import adapter.presenter.FinancePresenter;
import domain.entity.SortOption;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.FinanceUseCase;

/**
 * Tampilan konsol aplikasi catatan keuangan.
 * Menerima input user, memanggil use case, dan menangani exception domain.
 * Layer ini tidak mengandung logika bisnis — hanya interaksi user.
 */
public class FinanceView {
    /** Pemetaan pilihan menu pengurutan ke {@link SortOption} domain. */
    private static final Map<String, SortOption> SORT_OPTIONS = Map.of(
            "1", SortOption.AMOUNT_DESC,
            "2", SortOption.AMOUNT_ASC,
            "3", SortOption.INCOME_FIRST,
            "4", SortOption.EXPENSE_FIRST);


    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showTransactions(financeUseCase.getAllTransactions(), financeUseCase.getBalance());
            printMenu();

            String input = InputUtil.inputChoice("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addTransaction(TransactionType.INCOME);
                case "2" -> addTransaction(TransactionType.EXPENSE);
                case "3" -> searchTransaction();
                case "4" -> sortTransaction();
                case "5" -> presenter.showCurrentBalance(financeUseCase.getBalance());
                case "6" -> removeTransaction();
                case InputUtil.EXIT -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah Pemasukan");
        System.out.println("2. Tambah Pengeluaran");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Lihat Saldo");
        System.out.println("6. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah transaksi (pemasukan atau pengeluaran). */
    private void addTransaction(TransactionType type) {
        System.out.println(type == TransactionType.INCOME ? "[Menambah Pemasukan]" : "[Menambah Pengeluaran]");
        String description = InputUtil.input("Keterangan (x Jika Batal)");
        if (InputUtil.isCancel(description)) {
            return;
        }

        String strAmount = InputUtil.input("Jumlah (x Jika Batal)");
        if (InputUtil.isCancel(strAmount)) {
            return;
        }

        try {
            long amount = InputUtil.parseLong(strAmount, "Jumlah tidak valid!");
            presenter.showAddSuccess(financeUseCase.addTransaction(description, amount, type));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form cari transaksi berdasarkan keterangan. */
    private void searchTransaction() {
        System.out.println("[Mencari Transaksi]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(financeUseCase.searchTransactions(keyword), keyword);
        }
    }

    /** Form urutkan transaksi berdasarkan pilihan user. */
    private void sortTransaction() {
        System.out.println("[Mengurutkan Transaksi]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Jumlah (Terbesar)");
        System.out.println("2. Jumlah (Terkecil)");
        System.out.println("3. Tipe (Pemasukan dulu)");
        System.out.println("4. Tipe (Pengeluaran dulu)");
        System.out.println("x. Batal");

        String input = InputUtil.inputChoice("Pilih");
        if (InputUtil.isCancel(input)) {
            return;
        }

        Optional.ofNullable(SORT_OPTIONS.get(input)).ifPresentOrElse(
                option -> presenter.showSortedTransactions(
                        financeUseCase.sortTransactions(option), financeUseCase.getBalance()),
                presenter::showInvalidSortOption);
    }

    /** Form hapus transaksi berdasarkan ID. */
    private void removeTransaction() {
        System.out.println("[Menghapus Transaksi]");
        String strId = InputUtil.input("[ID Transaksi] yang dihapus (x Jika Batal)");

        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = InputUtil.parseInt(strId, "ID tidak valid!");
            financeUseCase.removeTransaction(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}
