package framework.view;

import adapter.presenter.GuestPresenter;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.GuestUseCase;

/**
 * Tampilan konsol aplikasi buku tamu.
 * Menerima input user, memanggil use case, dan menangani exception domain.
 * Layer ini tidak mengandung logika bisnis — hanya interaksi user.
 */
public class GuestView {

    private final GuestUseCase guestUseCase;
    private final GuestPresenter presenter;

    public GuestView(GuestUseCase guestUseCase, GuestPresenter presenter) {
        this.guestUseCase = guestUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showGuests(guestUseCase.getAllGuests());
            printMenu();

            String input = InputUtil.inputChoice("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> registerGuest();
                case "2" -> searchGuest();
                case "3" -> removeGuest();
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
        System.out.println("1. Daftarkan Tamu");
        System.out.println("2. Cari");
        System.out.println("3. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form pendaftaran tamu baru. */
    private void registerGuest() {
        System.out.println("[Mendaftarkan Tamu]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (InputUtil.isCancel(name)) {
            return;
        }

        String purpose = InputUtil.input("Tujuan Kunjungan (x Jika Batal)");
        if (InputUtil.isCancel(purpose)) {
            return;
        }

        try {
            presenter.showRegisterSuccess(guestUseCase.registerGuest(name, purpose));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form cari tamu berdasarkan nama. */
    private void searchGuest() {
        System.out.println("[Mencari Tamu]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(guestUseCase.searchGuests(keyword), keyword);
        }
    }

    /** Form hapus tamu berdasarkan ID. */
    private void removeGuest() {
        System.out.println("[Menghapus Tamu]");
        String strId = InputUtil.input("[ID Tamu] yang dihapus (x Jika Batal)");

        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = InputUtil.parseInt(strId, "ID tidak valid!");
            guestUseCase.removeGuest(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}
