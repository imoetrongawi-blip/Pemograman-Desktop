import java.text.DecimalFormat;

public abstract class MenuItem {
    private String nama;
    private double harga;
    private String kategori;
    private static final DecimalFormat FMT = new DecimalFormat("Rp #,###");

    public MenuItem(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getNama() { return nama; }
    public double getHarga() { return harga; }
    public String getKategori() { return kategori; }
    public void setHarga(double harga) { this.harga = harga; }

    protected String format(double v) { return FMT.format(v); }

    public abstract void tampilMenu();
}

class Makanan extends MenuItem {
    private String jenisMakanan;

    public Makanan(String nama, double harga, String jenisMakanan) {
        super(nama, harga, "Makanan");
        this.jenisMakanan = jenisMakanan;
    }

    public String getJenisMakanan() { return jenisMakanan; }

    @Override
    public void tampilMenu() {
        System.out.printf("%s [Makanan] - %s - %s%n", getNama(), format(getHarga()), jenisMakanan);
    }
}

class Minuman extends MenuItem {
    private String jenisMinuman;

    public Minuman(String nama, double harga, String jenisMinuman) {
        super(nama, harga, "Minuman");
        this.jenisMinuman = jenisMinuman;
    }

    public String getJenisMinuman() { return jenisMinuman; }

    @Override
    public void tampilMenu() {
        System.out.printf("%s [Minuman] - %s - %s%n", getNama(), format(getHarga()), jenisMinuman);
    }
}

class Diskon extends MenuItem {
    private double diskon;

    public Diskon(String nama, double diskon) {
        super(nama, 0, "Diskon");
        this.diskon = diskon;
    }

    public double getDiskon() { return diskon; }

    @Override
    public void tampilMenu() {
        System.out.printf("%s [Diskon] - %.0f%%%n", getNama(), diskon);
    }
}
