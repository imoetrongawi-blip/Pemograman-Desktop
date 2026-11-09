import java.util.Scanner;

public class RestoranApp {
    private static Menu menu = new Menu();
    private static Scanner sc = new Scanner(System.in);
    private static final String FILE_MENU = "menu.txt";
    private static final String FILE_STRUK = "struk.txt";

    public static void main(String[] args) {
        try { menu.muat(FILE_MENU); } catch (Exception ignored) {}
        if (menu.jumlah() == 0) {
            menu.tambah(new Makanan("Nasi Goreng", 25000, "Goreng"));
            menu.tambah(new Makanan("Ayam Bakar", 30000, "Bakar"));
            menu.tambah(new Makanan("Mie Goreng", 20000, "Goreng"));
            menu.tambah(new Minuman("Es Teh Manis", 5000, "Es"));
            menu.tambah(new Minuman("Jus Jeruk", 10000, "Jus"));
            menu.tambah(new Diskon("Diskon 10%", 10));
        }

        boolean running = true;
        while (running) {
            System.out.println("\n==================================");
            System.out.println("     Aplikasi Kasir Restoran");
            System.out.println("==================================");
            System.out.println("1. Menu Pelanggan (Pemesanan)");
            System.out.println("2. Manajemen Menu (Pemilik)");
            System.out.println("3. Keluar");
            System.out.print("Pilih opsi (1-3): ");

            String pilih = sc.nextLine().trim();
            switch (pilih) {
                case "1" -> menuPelanggan();
                case "2" -> menuManajemen();
                case "3" -> {
                    try { menu.simpan(FILE_MENU); } catch (Exception ignored) {}
                    running = false;
                    System.out.println("Terima kasih!");
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
        }
        sc.close();
    }

    private static void menuPelanggan() {
        if (menu.jumlah() == 0) {
            System.out.println("Menu kosong.");
            return;
        }
        Pesanan pesan = new Pesanan();

        while (true) {
            menu.tampilkan();
            System.out.println("Ketik 'selesai' jika selesai, 'diskon' untuk pilih diskon.");
            System.out.print("Masukkan nomor menu: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("selesai")) {
                if (pesan.jumlahItem() == 0) {
                    System.out.println("Belum ada pesanan.");
                    return;
                }
                break;
            }
            if (input.equalsIgnoreCase("diskon")) {
                pilihDiskon(pesan);
                continue;
            }

            int nomor;
            try { nomor = Integer.parseInt(input); }
            catch (NumberFormatException e) {
                System.out.println("Input tidak valid.");
                continue;
            }

            MenuItem item;
            try { item = menu.cari(nomor); }
            catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            if (item instanceof Diskon) {
                System.out.print("Terapkan diskon '" + item.getNama() + "'? (Ya/Tidak): ");
                if (sc.nextLine().trim().equalsIgnoreCase("Ya")) pesan.setDiskon((Diskon) item);
                continue;
            }

            int qty = 0;
            while (true) {
                System.out.print("Jumlah: ");
                try {
                    qty = Integer.parseInt(sc.nextLine().trim());
                    if (qty > 0) break;
                    System.out.println("Jumlah harus > 0.");
                } catch (NumberFormatException e) {
                    System.out.println("Input harus angka.");
                }
            }
            pesan.tambah(item, qty);
            System.out.println("-> Ditambahkan.");
        }

        pesan.cetakStruk();
        try { pesan.simpanStruk(FILE_STRUK); System.out.println("Struk disimpan ke " + FILE_STRUK); }
        catch (Exception e) { System.out.println("Gagal simpan struk: " + e.getMessage()); }
    }

    private static void pilihDiskon(Pesanan pesan) {
        boolean ada = false;
        for (int i = 0; i < menu.jumlah(); i++) {
            MenuItem it = menu.cari(i + 1);
            if (it instanceof Diskon) {
                if (!ada) System.out.println("\n--- DISKON ---");
                ada = true;
                System.out.printf("%d. %s%n", (i + 1), it.getNama());
            }
        }
        if (!ada) { System.out.println("Tidak ada diskon."); return; }

        System.out.print("Pilih nomor diskon (0 batal): ");
        try {
            int n = Integer.parseInt(sc.nextLine().trim());
            if (n == 0) return;
            MenuItem it = menu.cari(n);
            if (it instanceof Diskon) pesan.setDiskon((Diskon) it);
        } catch (Exception e) { System.out.println("Input tidak valid."); }
    }

    private static void menuManajemen() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- MANAJEMEN MENU ---");
            System.out.println("1. Tambah Menu");
            System.out.println("2. Ubah Harga");
            System.out.println("3. Hapus Menu");
            System.out.println("4. Kembali");
            System.out.print("Pilih (1-4): ");
            String p = sc.nextLine().trim();
            switch (p) {
                case "1" -> tambahMenu();
                case "2" -> ubahHarga();
                case "3" -> hapusMenu();
                case "4" -> kembali = true;
                default -> System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void tambahMenu() {
        System.out.print("Nama: ");
        String nama = sc.nextLine().trim();
        if (nama.isEmpty()) { System.out.println("Nama tidak boleh kosong."); return; }

        double harga = 0;
        while (true) {
            System.out.print("Harga: ");
            try {
                harga = Double.parseDouble(sc.nextLine().trim());
                if (harga > 0) break;
                System.out.println("Harga > 0.");
            } catch (NumberFormatException e) { System.out.println("Input angka."); }
        }

        System.out.println("1. Makanan  2. Minuman  3. Diskon");
        System.out.print("Pilih (1-3): ");
        String j = sc.nextLine().trim();
        switch (j) {
            case "1":
                System.out.print("Jenis makanan: ");
                menu.tambah(new Makanan(nama, harga, sc.nextLine().trim()));
                break;
            case "2":
                System.out.print("Jenis minuman: ");
                menu.tambah(new Minuman(nama, harga, sc.nextLine().trim()));
                break;
            case "3":
                menu.tambah(new Diskon(nama, harga));
                break;
            default:
                System.out.println("Jenis tidak valid.");
                return;
        }
        try { menu.simpan(FILE_MENU); } catch (Exception ignored) {}
        System.out.println("-> Ditambahkan.");
    }

    private static void ubahHarga() {
        if (menu.jumlah() == 0) { System.out.println("Kosong."); return; }
        menu.tampilkan();
        int idx = pilihIndex("ubah harga");
        if (idx == -1) return;

        double baru = 0;
        while (true) {
            System.out.print("Harga baru: ");
            try {
                baru = Double.parseDouble(sc.nextLine().trim());
                if (baru > 0) break;
                System.out.println("Harga > 0.");
            } catch (NumberFormatException e) { System.out.println("Input angka."); }
        }

        System.out.print("Yakin? (Ya/Tidak): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Ya")) {
            menu.ubahHarga(idx, baru);
            try { menu.simpan(FILE_MENU); } catch (Exception ignored) {}
            System.out.println("-> Diperbarui.");
        } else {
            System.out.println("-> Dibatalkan.");
        }
    }

    private static void hapusMenu() {
        if (menu.jumlah() == 0) { System.out.println("Kosong."); return; }
        menu.tampilkan();
        int idx = pilihIndex("hapus");
        if (idx == -1) return;

        MenuItem it = menu.cari(idx + 1);
        System.out.print("Yakin hapus '" + it.getNama() + "'? (Ya/Tidak): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Ya")) {
            menu.hapus(idx);
            try { menu.simpan(FILE_MENU); } catch (Exception ignored) {}
            System.out.println("-> Dihapus.");
        } else {
            System.out.println("-> Dibatalkan.");
        }
    }

    private static int pilihIndex(String aksi) {
        while (true) {
            System.out.print("Nomor menu untuk " + aksi + ": ");
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n >= 1 && n <= menu.jumlah()) return n - 1;
                System.out.println("Di luar jangkauan.");
            } catch (NumberFormatException e) { System.out.println("Input angka."); }
        }
    }
}
