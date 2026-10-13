package framework.view;

import adapter.presenter.ItemPresenter;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.ItemUseCase;

/**
 * Tampilan konsol aplikasi inventaris barang.
 * Menerima input user, memanggil use case, dan menangani exception domain.
 * Layer ini tidak mengandung logika bisnis — hanya interaksi user.
 */
public class ItemView {
    /** Pemetaan pilihan menu pengurutan ke {@link SortOption} domain. */
    private static final Map<String, SortOption> SORT_OPTIONS = Map.of(
            "1", SortOption.NAME_ASC,
            "2", SortOption.NAME_DESC,
            "3", SortOption.QUANTITY_ASC,
            "4", SortOption.QUANTITY_DESC);

    private static final String INVALID_ID_MESSAGE = "ID tidak valid!";
    private static final String INVALID_QUANTITY_MESSAGE = "Jumlah stok tidak valid!";

    private final ItemUseCase itemUseCase;
    private final ItemPresenter presenter;

    public ItemView(ItemUseCase itemUseCase, ItemPresenter presenter) {
        this.itemUseCase = itemUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showItems(itemUseCase.getAllItems());
            printMenu();

            String input = InputUtil.inputChoice("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addItem();
                case "2" -> updateStock();
                case "3" -> searchItem();
                case "4" -> sortItem();
                case "5" -> removeItem();
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
        System.out.println("1. Tambah");
        System.out.println("2. Ubah Stok");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah barang baru. */
    private void addItem() {
        System.out.println("[Menambah Barang]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (InputUtil.isCancel(name)) {
            return;
        }

        String strQuantity = InputUtil.input("Jumlah Stok");
        int quantity;
        try {
            quantity = InputUtil.parseInt(strQuantity, INVALID_QUANTITY_MESSAGE);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
            return;
        }

        String category = InputUtil.input("Kategori");

        try {
            presenter.showAddSuccess(itemUseCase.addItem(name, quantity, category));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form ubah stok barang berdasarkan ID. */
    private void updateStock() {
        System.out.println("[Mengubah Stok Barang]");
        String strId = InputUtil.input("[ID Barang] yang diubah (x Jika Batal)");
        if (InputUtil.isCancel(strId)) {
            return;
        }

        int id;
        int quantity;
        try {
            id = InputUtil.parseInt(strId, INVALID_ID_MESSAGE);
            quantity = InputUtil.parseInt(InputUtil.input("Stok Baru"), INVALID_QUANTITY_MESSAGE);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
            return;
        }

        try {
            itemUseCase.updateStock(id, quantity);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form cari barang berdasarkan nama. */
    private void searchItem() {
        System.out.println("[Mencari Barang]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(itemUseCase.searchItems(keyword), keyword);
        }
    }

    /** Form urutkan barang berdasarkan pilihan user. */
    private void sortItem() {
        System.out.println("[Mengurutkan Barang]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Nama (A-Z)");
        System.out.println("2. Nama (Z-A)");
        System.out.println("3. Jumlah (Terkecil)");
        System.out.println("4. Jumlah (Terbesar)");
        System.out.println("x. Batal");

        String input = InputUtil.inputChoice("Pilih");
        if (InputUtil.isCancel(input)) {
            return;
        }

        Optional.ofNullable(SORT_OPTIONS.get(input)).ifPresentOrElse(
                option -> presenter.showSortedItems(itemUseCase.sortItems(option)),
                presenter::showInvalidSortOption);
    }

    /** Form hapus barang berdasarkan ID. */
    private void removeItem() {
        System.out.println("[Menghapus Barang]");
        String strId = InputUtil.input("[ID Barang] yang dihapus (x Jika Batal)");

        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = InputUtil.parseInt(strId, INVALID_ID_MESSAGE);
            itemUseCase.removeItem(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}
