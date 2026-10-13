package usecase;

import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Use case yang menangani logika bisnis kontak teman.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception domain.
 */
public class ContactUseCase {
    /** Nomor telepon: 8-15 digit, boleh diawali tanda plus. */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{8,15}$");

    /** Email sederhana: nama@domain.tld. */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IContactRepository contactRepository;

    public ContactUseCase(IContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /** Mengambil semua kontak. */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /** Menambahkan kontak baru setelah validasi nama, telepon, dan email. */
    public Contact addContact(String name, String phone, String email) throws ValidationException {
        String validName = validateName(name);
        String validPhone = validatePhone(phone);
        String validEmail = validateEmail(email);
        return contactRepository.save(validName, validPhone, validEmail);
    }

    /**
     * Mengubah nama, telepon, dan/atau email kontak secara parsial.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     * Semua input divalidasi terlebih dahulu agar perubahan bersifat atomik.
     */
    public void updateContact(int id, String name, String phone, String email)
            throws EntityNotFoundException, ValidationException {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kontak", id));

        String newName = name == null ? null : validateName(name);
        String newPhone = phone == null ? null : validatePhone(phone);
        String newEmail = email == null ? null : validateEmail(email);

        if (newName != null) {
            contact.changeName(newName);
        }
        if (newPhone != null) {
            contact.changePhone(newPhone);
        }
        if (newEmail != null) {
            contact.changeEmail(newEmail);
        }
        contactRepository.update(contact);
    }

    /** Menghapus kontak berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeContact(int id) throws EntityNotFoundException {
        if (!contactRepository.deleteById(id)) {
            throw new EntityNotFoundException("Kontak", id);
        }
    }

    /** Mencari kontak yang namanya mengandung kata kunci (case-insensitive). */
    public List<Contact> searchContacts(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return contactRepository.findBy(contact -> contact.getName().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan kontak sesuai kriteria {@link SortOption}. */
    public List<Contact> sortContacts(SortOption option) {
        return contactRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    private String validateName(String name) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Nama kontak tidak boleh kosong!");
        }
        return name.trim();
    }

    private String validatePhone(String phone) throws ValidationException {
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Nomor telepon tidak valid!");
        }
        return trimmed;
    }

    private String validateEmail(String email) throws ValidationException {
        String trimmed = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Email tidak valid!");
        }
        return trimmed;
    }
}
