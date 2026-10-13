#!/usr/bin/env bash
# Compile dan jalankan seluruh test case (TC-01 s.d. TC-04) untuk semua studi kasus.
# Penggunaan (dari root proyek):  bash run-tests.sh
# Output program dicocokkan berurutan dengan baris-baris pada TC-0X.expected.
# File sementara disimpan di direktori temporer dan otomatis dibersihkan.

cd "$(dirname "$0")" || exit 1

STUDIES=(catatan-keuangan buku-tamu inventaris-barang jadwal-kegiatan kontak-teman)
temp_dir=$(mktemp -d "${TMPDIR:-/tmp}/java-oop-tests.XXXXXX") || exit 1
trap 'rm -rf "$temp_dir"' EXIT HUP INT TERM

# Memeriksa bahwa setiap baris expected muncul berurutan pada output aktual.
check() {
    local actual="$1" expected="$2" position=1 line offset
    while IFS= read -r line || [ -n "$line" ]; do
        [ -z "$line" ] && continue
        offset=$(tail -n +"$position" "$actual" | grep -nF -- "$line" | head -n 1 | cut -d: -f1)
        if [ -z "$offset" ]; then
            echo "    baris tidak ditemukan: $line"
            return 1
        fi
        position=$((position + offset))
    done < "$expected"
}

failed=0
for study in "${STUDIES[@]}"; do
    echo "== $study"
    output_dir="$temp_dir/$study/output"
    mkdir -p "$output_dir"

    if ! javac -encoding UTF-8 -d "$output_dir" -sourcepath "$study/src" "$study/src/App.java"; then
        echo "  GAGAL compile"
        failed=1
        continue
    fi

    for input_file in "$study"/test-cases/TC-*.tc; do
        name=$(basename "$input_file" .tc)
        actual="$temp_dir/$study/$name.out"

        if ! java -cp "$output_dir" App < "$input_file" > "$actual"; then
            echo "  $name GAGAL (program keluar dengan error)"
            failed=1
        elif check "$actual" "$study/test-cases/$name.expected"; then
            echo "  $name LULUS"
        else
            echo "  $name GAGAL"
            failed=1
        fi
    done
done
exit $failed
