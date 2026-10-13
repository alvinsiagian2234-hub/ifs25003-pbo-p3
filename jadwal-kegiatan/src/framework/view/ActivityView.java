package framework.view;

import adapter.presenter.ActivityPresenter;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import java.util.Map;
import java.util.Optional;
import usecase.ActivityUseCase;

/**
 * Tampilan konsol aplikasi jadwal kegiatan.
 * Menerima input user, memanggil use case, dan menangani exception domain.
 * Layer ini tidak mengandung logika bisnis — hanya interaksi user.
 */
public class ActivityView {
    /** Pemetaan pilihan menu pengurutan ke {@link SortOption} domain. */
    private static final Map<String, SortOption> SORT_OPTIONS = Map.of(
            "1", SortOption.DAY,
            "2", SortOption.TIME,
            "3", SortOption.TITLE_ASC,
            "4", SortOption.TITLE_DESC);

    private final ActivityUseCase activityUseCase;
    private final ActivityPresenter presenter;

    public ActivityView(ActivityUseCase activityUseCase, ActivityPresenter presenter) {
        this.activityUseCase = activityUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showActivities(activityUseCase.getAllActivities());
            printMenu();

            String input = InputUtil.inputChoice("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addActivity();
                case "2" -> updateActivity();
                case "3" -> searchActivity();
                case "4" -> sortActivity();
                case "5" -> removeActivity();
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

    /** Form tambah kegiatan baru. */
    private void addActivity() {
        System.out.println("[Menambah Kegiatan]");
        String title = InputUtil.input("Judul (x Jika Batal)");
        if (InputUtil.isCancel(title)) {
            return;
        }

        String day = InputUtil.input("Hari (Senin-Minggu)");
        String time = InputUtil.input("Waktu (HH:mm)");

        try {
            presenter.showAddSuccess(activityUseCase.addActivity(title, day, time));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form ubah kegiatan; field yang dikosongkan tidak diubah. */
    private void updateActivity() {
        System.out.println("[Mengubah Kegiatan]");
        String strId = InputUtil.input("[ID Kegiatan] yang diubah (x Jika Batal)");
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

        String title = blankToNull(InputUtil.input("Judul Baru (Kosongkan jika tidak ingin mengubah)"));
        String day = blankToNull(InputUtil.input("Hari Baru (Kosongkan jika tidak ingin mengubah)"));
        String time = blankToNull(InputUtil.input("Waktu Baru (Kosongkan jika tidak ingin mengubah)"));

        try {
            activityUseCase.updateActivity(id, title, day, time);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    /** Form cari kegiatan berdasarkan judul. */
    private void searchActivity() {
        System.out.println("[Mencari Kegiatan]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(activityUseCase.searchActivities(keyword), keyword);
        }
    }

    /** Form urutkan kegiatan berdasarkan pilihan user. */
    private void sortActivity() {
        System.out.println("[Mengurutkan Kegiatan]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Hari");
        System.out.println("2. Waktu");
        System.out.println("3. Judul (A-Z)");
        System.out.println("4. Judul (Z-A)");
        System.out.println("x. Batal");

        String input = InputUtil.inputChoice("Pilih");
        if (InputUtil.isCancel(input)) {
            return;
        }

        Optional.ofNullable(SORT_OPTIONS.get(input)).ifPresentOrElse(
                option -> presenter.showSortedActivities(activityUseCase.sortActivities(option)),
                presenter::showInvalidSortOption);
    }

    /** Form hapus kegiatan berdasarkan ID. */
    private void removeActivity() {
        System.out.println("[Menghapus Kegiatan]");
        String strId = InputUtil.input("[ID Kegiatan] yang dihapus (x Jika Batal)");
        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = InputUtil.parseInt(strId, "ID tidak valid!");
            activityUseCase.removeActivity(id);
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
