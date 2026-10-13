package usecase;

import domain.entity.Activity;
import domain.entity.Day;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Use case yang menangani logika bisnis jadwal kegiatan.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception domain.
 */
public class ActivityUseCase {
    /** Format waktu yang diterima: 00:00 sampai 23:59. */
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** Mengambil semua kegiatan. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** Menambahkan kegiatan baru setelah validasi judul, hari, dan waktu. */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        String validTitle = validateTitle(title);
        Day validDay = parseDay(day);
        String validTime = validateTime(time);
        return activityRepository.save(validTitle, validDay, validTime);
    }

    /**
     * Mengubah judul, hari, dan/atau waktu kegiatan secara parsial.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     * Semua input divalidasi terlebih dahulu agar perubahan bersifat atomik.
     */
    public void updateActivity(int id, String title, String day, String time)
            throws EntityNotFoundException, ValidationException {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kegiatan", id));

        String newTitle = title == null ? null : validateTitle(title);
        Day newDay = day == null ? null : parseDay(day);
        String newTime = time == null ? null : validateTime(time);

        if (newTitle != null) {
            activity.changeTitle(newTitle);
        }
        if (newDay != null) {
            activity.changeDay(newDay);
        }
        if (newTime != null) {
            activity.changeTime(newTime);
        }
        activityRepository.update(activity);
    }

    /** Menghapus kegiatan berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeActivity(int id) throws EntityNotFoundException {
        if (!activityRepository.deleteById(id)) {
            throw new EntityNotFoundException("Kegiatan", id);
        }
    }

    /** Mencari kegiatan yang judulnya mengandung kata kunci (case-insensitive). */
    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return activityRepository.findBy(
                activity -> activity.getTitle().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan kegiatan sesuai kriteria {@link SortOption}. */
    public List<Activity> sortActivities(SortOption option) {
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    private String validateTitle(String title) throws ValidationException {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Judul kegiatan tidak boleh kosong!");
        }
        return title.trim();
    }

    private Day parseDay(String day) throws ValidationException {
        try {
            return Day.valueOf(day.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Hari tidak valid (Senin-Minggu)!");
        }
    }

    private String validateTime(String time) throws ValidationException {
        String trimmed = time.trim();
        if (!TIME_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Waktu tidak valid (gunakan format HH:mm)!");
        }
        return trimmed;
    }
}
