import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Menu {
    private ArrayList<MenuItem> daftar = new ArrayList<>();

    public void tambah(MenuItem item) { daftar.add(item); }

    public MenuItem cari(int nomor) {
        if (nomor < 1 || nomor > daftar.size()) {
            throw new IllegalArgumentException("Menu nomor " + nomor + " tidak ditemukan.");
        }
        return daftar.get(nomor - 1);
    }

    public int jumlah() { return daftar.size(); }

    public void ubahHarga(int index, double hargaBaru) {
        daftar.get(index).setHarga(hargaBaru);
    }

    public void hapus(int index) { daftar.remove(index); }

    public void tampilkan() {
        System.out.println("\n--- DAFTAR MENU ---");
        if (daftar.isEmpty()) {
            System.out.println("(kosong)");
            return;
        }
        for (int i = 0; i < daftar.size(); i++) {
            MenuItem item = daftar.get(i);
            System.out.printf("%d. %s [%s] - %s%n",
                    (i + 1), item.getNama(), item.getKategori(),
                    ((MenuItem) item).getHarga() >= 0 ? format(item.getHarga()) : "-");
        }
    }

    public void simpan(String file) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            for (MenuItem item : daftar) {
                String line;
                if (item instanceof Makanan) {
                    Makanan m = (Makanan) item;
                    line = "MAKANAN|" + item.getNama() + "|" + item.getHarga() + "|" + m.getJenisMakanan();
                } else if (item instanceof Minuman) {
                    Minuman m = (Minuman) item;
                    line = "MINUMAN|" + item.getNama() + "|" + item.getHarga() + "|" + m.getJenisMinuman();
                } else if (item instanceof Diskon) {
                    Diskon d = (Diskon) item;
                    line = "DISKON|" + item.getNama() + "|" + d.getDiskon();
                } else {
                    continue;
                }
                w.write(line);
                w.newLine();
            }
        }
    }

    @SuppressWarnings("unused")
    public void muat(String file) throws IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|");
                switch (p[0]) {
                    case "MAKANAN":
                        daftar.add(new Makanan(p[1], Double.parseDouble(p[2]), p[3]));
                        break;
                    case "MINUMAN":
                        daftar.add(new Minuman(p[1], Double.parseDouble(p[2]), p[3]));
                        break;
                    case "DISKON":
                        daftar.add(new Diskon(p[1], Double.parseDouble(p[2])));
                        break;
                }
            }
        } catch (java.io.FileNotFoundException e) {
            // file belum ada, biarkan kosong
        }
    }

    private String format(double v) {
        return new java.text.DecimalFormat("Rp #,###").format(v);
    }
}
