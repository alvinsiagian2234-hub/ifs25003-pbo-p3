package framework.view;

import adapter.presenter.ContactPresenter;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.ContactUseCase;

/**
 * Tampilan konsol aplikasi kontak teman.
 * Menerima input user, memanggil use case, dan menangani exception domain.
 * Layer ini tidak mengandung logika bisnis — hanya interaksi user.
 */
public class ContactView {
    /** Pemetaan pilihan menu pengurutan ke {@link SortOption} domain. */
    private static final Map<String, SortOption> SORT_OPTIONS = Map.of(
            "1", SortOption.NAME_ASC,
            "2", SortOption.NAME_DESC);

    private final ContactUseCase contactUseCase;
    private final ContactPresenter presenter;

    public ContactView(ContactUseCase contactUseCase, ContactPresenter presenter) {
        this.contactUseCase = contactUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showContacts(contactUseCase.getAllContacts());
            printMenu();

            String input = InputUtil.inputChoice("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addContact();
                case "2" -> updateContact();
                case "3" -> searchContact();
                case "4" -> sortContact();
                case "5" -> removeContact();
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
        System.out.println("2. Ubah");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah kontak baru. */
    private void addContact() {
        System.out.println("[Menambah Kontak]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (InputUtil.isCancel(name)) {
            return;
        }

        String phone = InputUtil.input("Telepon");
        String email = InputUtil.input("Email");

        try {
            presenter.showAddSuccess(contactUseCase.addContact(name, phone, email));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form ubah kontak; field yang dikosongkan tidak diubah. */
    private void updateContact() {
        System.out.println("[Mengubah Kontak]");
        String strId = InputUtil.input("[ID Kontak] yang diubah (x Jika Batal)");
        if (InputUtil.isCancel(strId)) {
            return;
        }

        int id;
        try {
            id = InputUtil.parseInt(strId, "ID tidak valid!");
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
            return;
        }

        String name = blankToNull(InputUtil.input("Nama Baru (Kosongkan jika tidak ingin mengubah)"));
        String phone = blankToNull(InputUtil.input("Telepon Baru (Kosongkan jika tidak ingin mengubah)"));
        String email = blankToNull(InputUtil.input("Email Baru (Kosongkan jika tidak ingin mengubah)"));

        try {
            contactUseCase.updateContact(id, name, phone, email);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form cari kontak berdasarkan nama. */
    private void searchContact() {
        System.out.println("[Mencari Kontak]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(contactUseCase.searchContacts(keyword), keyword);
        }
    }

    /** Form urutkan kontak berdasarkan pilihan user. */
    private void sortContact() {
        System.out.println("[Mengurutkan Kontak]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Nama (A-Z)");
        System.out.println("2. Nama (Z-A)");
        System.out.println("x. Batal");

        String input = InputUtil.inputChoice("Pilih");
        if (InputUtil.isCancel(input)) {
            return;
        }

        Optional.ofNullable(SORT_OPTIONS.get(input)).ifPresentOrElse(
                option -> presenter.showSortedContacts(contactUseCase.sortContacts(option)),
                presenter::showInvalidSortOption);
    }

    /** Form hapus kontak berdasarkan ID. */
    private void removeContact() {
        System.out.println("[Menghapus Kontak]");
        String strId = InputUtil.input("[ID Kontak] yang dihapus (x Jika Batal)");
        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = InputUtil.parseInt(strId, "ID tidak valid!");
            contactUseCase.removeContact(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /** Input kosong berarti field tidak diubah (direpresentasikan dengan null). */
    private String blankToNull(String value) {
        return value.isBlank() ? null : value;
    }
}
