package domain.entity;

/**
 * Entity inti yang merepresentasikan satu kegiatan pada jadwal.
 * Berada di layer domain — bebas dari urusan tampilan maupun penyimpanan.
 */
public class Activity {
    /** ID unik kegiatan, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Judul kegiatan. */
    private String title;

    /** Hari pelaksanaan kegiatan. */
    private Day day;

    /** Waktu mulai kegiatan dengan format HH:mm. */
    private String time;

    public Activity(int id, String title, Day day, String time) {
        this.id = id;
        this.title = title;
        this.day = day;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Day getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    /** Mengubah judul kegiatan. */
    public void changeTitle(String title) {
        this.title = title;
    }

    /** Mengubah hari pelaksanaan kegiatan. */
    public void changeDay(Day day) {
        this.day = day;
    }

    /** Mengubah waktu mulai kegiatan. */
    public void changeTime(String time) {
        this.time = time;
    }
}
