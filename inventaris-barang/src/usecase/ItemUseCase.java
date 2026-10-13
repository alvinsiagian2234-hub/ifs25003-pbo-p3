package usecase;

import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IItemRepository;
import java.util.List;

/**
 * Use case yang menangani logika bisnis inventaris barang.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception domain.
 */
public class ItemUseCase {
    private static final String INVALID_QUANTITY_MESSAGE = "Jumlah stok tidak valid!";

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IItemRepository itemRepository;

    public ItemUseCase(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /** Mengambil semua barang. */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /** Menambahkan barang baru setelah validasi nama, stok, dan kategori. */
    public Item addItem(String name, int quantity, String category) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Nama barang tidak boleh kosong!");
        }
        validateQuantity(quantity);
        if (category == null || category.isBlank()) {
            throw new ValidationException("Kategori tidak boleh kosong!");
        }
        return itemRepository.save(name.trim(), quantity, category.trim());
    }

    /** Mengubah stok barang; melempar exception jika ID tidak ditemukan atau stok tidak valid. */
    public void updateStock(int id, int quantity) throws EntityNotFoundException, ValidationException {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Barang", id));

        validateQuantity(quantity);
        item.changeQuantity(quantity);
        itemRepository.update(item);
    }

    /** Menghapus barang berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeItem(int id) throws EntityNotFoundException {
        if (!itemRepository.deleteById(id)) {
            throw new EntityNotFoundException("Barang", id);
        }
    }

    /** Mencari barang yang namanya mengandung kata kunci (case-insensitive). */
    public List<Item> searchItems(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return itemRepository.findBy(item -> item.getName().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan barang sesuai kriteria {@link SortOption}. */
    public List<Item> sortItems(SortOption option) {
        return itemRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Stok harus lebih dari nol. */
    private void validateQuantity(int quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException(INVALID_QUANTITY_MESSAGE);
        }
    }
}
