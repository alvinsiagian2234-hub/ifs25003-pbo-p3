package framework.util;

import domain.exception.ValidationException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Utility untuk membaca dan mengonversi input dari keyboard.
 * Berada di layer framework karena bergantung pada {@link System#in}.
 */
public final class InputUtil {
    /** Perintah untuk membatalkan operasi yang sedang berjalan. */
    public static final String CANCEL = "x";

    /** Perintah untuk keluar dari aplikasi. */
    public static final String EXIT = "x";

    /** Scanner bersama untuk seluruh aplikasi (satu instance cukup). */
    private static final Scanner scanner = new Scanner(System.in);

    private InputUtil() {
    }

    /**
     * Menampilkan prompt dan membaca satu baris input dari user.
     * Jika input sudah habis (mis. akhir file test case), dianggap sebagai perintah keluar.
     *
     * @param info label yang ditampilkan sebelum input
     * @return teks yang diketik user
     */
    public static String input(String info) {
        System.out.print(info + " : ");
        return scanner.hasNextLine() ? scanner.nextLine() : EXIT;
    }

    /**
     * Membaca pilihan menu dan menormalkannya (tanpa spasi tepi, huruf kecil).
     *
     * @param info label yang ditampilkan sebelum input
     */
    public static String inputChoice(String info) {
        return input(info).trim().toLowerCase(Locale.ROOT);
    }

    /** Memeriksa apakah input user adalah perintah pembatalan. */
    public static boolean isCancel(String value) {
        return CANCEL.equalsIgnoreCase(value.trim());
    }

    /**
     * Mengonversi teks menjadi bilangan bulat.
     *
     * @param value        teks yang dikonversi
     * @param errorMessage pesan jika teks bukan angka
     * @throws ValidationException jika teks bukan bilangan bulat
     */
    public static int parseInt(String value, String errorMessage) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(errorMessage);
        }
    }

    /**
     * Mengonversi teks menjadi bilangan bulat panjang.
     *
     * @param value        teks yang dikonversi
     * @param errorMessage pesan jika teks bukan angka
     * @throws ValidationException jika teks bukan bilangan bulat
     */
    public static long parseLong(String value, String errorMessage) throws ValidationException {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(errorMessage);
        }
    }
}
