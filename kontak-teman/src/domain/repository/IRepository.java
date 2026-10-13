package domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Kontrak Generic Repository Port untuk operasi CRUD dan pencarian.
 *
 * @param <T>  tipe entitas domain
 * @param <ID> tipe data identifier entitas
 */
public interface IRepository<T, ID> {
    /** Mengambil semua entitas dalam list baru (salinan defensif). */
    List<T> findAll();

    /** Mencari satu entitas berdasarkan ID. */
    Optional<T> findById(ID id);

    /**
     * Menyimpan entitas baru.
     *
     * @throws NullPointerException jika entitas null
     */
    T save(T entity);

    /** Menghapus entitas berdasarkan ID. Mengembalikan true jika berhasil dihapus. */
    boolean deleteById(ID id);

    /**
     * Menyimpan perubahan entitas yang sudah ada.
     *
     * @throws NullPointerException     jika entitas null
     * @throws IllegalArgumentException jika ID entitas belum tersimpan
     */
    void update(T entity);

    /**
     * Mencari entitas yang memenuhi predikat dan mengembalikannya dalam list baru.
     *
     * @throws NullPointerException jika predikat null
     */
    List<T> findBy(Predicate<T> predicate);
}
