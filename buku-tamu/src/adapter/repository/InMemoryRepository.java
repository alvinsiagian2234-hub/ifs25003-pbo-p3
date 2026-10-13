package adapter.repository;

import domain.repository.IRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Base generic in-memory repository yang mengimplementasikan {@link IRepository}.
 * Menghilangkan duplikasi kode penyimpanan list dan pencarian untuk berbagai entitas.
 *
 * @param <T>  tipe entitas domain
 * @param <ID> tipe data identifier unik entitas
 */
public abstract class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    /** Penyimpanan data entitas di memori. */
    protected final List<T> data = new ArrayList<>();

    /** Fungsi untuk mengekstrak ID dari objek entitas. */
    private final Function<T, ID> idExtractor;

    protected InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "ID extractor tidak boleh null.");
    }

    @Override
    public List<T> findAll() {
        // Salinan defensif agar data internal tidak termutasi dari luar
        return new ArrayList<>(data);
    }

    @Override
    public Optional<T> findById(ID id) {
        return data.stream()
                .filter(item -> hasId(item, id))
                .findFirst();
    }

    @Override
    public T save(T entity) {
        data.add(Objects.requireNonNull(entity, "Entitas tidak boleh null."));
        return entity;
    }

    @Override
    public boolean deleteById(ID id) {
        return data.removeIf(item -> hasId(item, id));
    }

    @Override
    public void update(T entity) {
        Objects.requireNonNull(entity, "Entitas tidak boleh null.");

        int index = indexOfId(idExtractor.apply(entity));
        if (index < 0) {
            throw new IllegalArgumentException("Entitas yang akan diperbarui tidak ditemukan.");
        }
        data.set(index, entity);
    }

    @Override
    public List<T> findBy(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "Predikat pencarian tidak boleh null.");

        return data.stream()
                .filter(predicate)
                .toList();
    }

    /** Memeriksa apakah entitas memiliki ID yang dimaksud. */
    private boolean hasId(T item, ID id) {
        return Objects.equals(idExtractor.apply(item), id);
    }

    /** Mencari posisi entitas dengan ID tertentu; -1 jika tidak ada. */
    private int indexOfId(ID id) {
        for (int i = 0; i < data.size(); i++) {
            if (hasId(data.get(i), id)) {
                return i;
            }
        }
        return -1;
    }
}
