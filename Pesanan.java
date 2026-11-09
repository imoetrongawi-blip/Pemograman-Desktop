import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Pesanan {
    private static class Item {
        MenuItem menu;
        int qty;
        Item(MenuItem menu, int qty) { this.menu = menu; this.qty = qty; }
    }

    private ArrayList<Item> items = new ArrayList<>();
    private Diskon diskon = null;

    public void tambah(MenuItem m, int qty) { items.add(new Item(m, qty)); }

    public void setDiskon(Diskon d) { this.diskon = d; }

    public int jumlahItem() { return items.size(); }

    public double totalKotor() {
        double t = 0;
        for (Item it : items) t += it.menu.getHarga() * it.qty;
        return t;
    }

    public double totalAkhir() {
        double total = totalKotor();
        double potongan = 0;

        if (diskon != null) {
            double d = diskon.getDiskon();
            potongan = (d > 0 && d <= 100) ? total * (d / 100.0) : d;
        }

        boolean adaMinuman = false;
        double minTermurah = Double.MAX_VALUE;
        for (Item it : items) {
            if (it.menu.getKategori().equalsIgnoreCase("Minuman")) {
                adaMinuman = true;
                if (it.menu.getHarga() < minTermurah) minTermurah = it.menu.getHarga();
            }
        }
        double bogo = (total > 50000 && adaMinuman) ? minTermurah : 0;
        if (bogo == Double.MAX_VALUE) bogo = 0;

        double diskon10 = (total > 100000) ? total * 0.10 : 0;

        double setelah = total - potongan - bogo - diskon10;
        if (setelah < 0) setelah = 0;

        return setelah + (setelah * 0.10) + 20000;
    }

    public void cetakStruk() {
        double total = totalKotor();
        System.out.println("\n========== STRUK PEMBAYARAN ==========");
        for (Item it : items) {
            System.out.printf("%s x%d @%s = %s%n",
                    it.menu.getNama(), it.qty,
                    new java.text.DecimalFormat("Rp #,###").format(it.menu.getHarga()),
                    new java.text.DecimalFormat("Rp #,###").format(it.menu.getHarga() * it.qty));
        }
        System.out.println("--------------------------------------");
        System.out.printf("Total Pesanan : %s%n",
                new java.text.DecimalFormat("Rp #,###").format(total));

        if (diskon != null) {
            double d = diskon.getDiskon();
            double pot = (d > 0 && d <= 100) ? total * (d / 100.0) : d;
            System.out.printf("Diskon %s   : -%s%n", diskon.getNama(),
                    new java.text.DecimalFormat("Rp #,###").format(pot));
        }

        boolean adaMinuman = false;
        double minTermurah = Double.MAX_VALUE;
        for (Item it : items) {
            if (it.menu.getKategori().equalsIgnoreCase("Minuman")) {
                adaMinuman = true;
                if (it.menu.getHarga() < minTermurah) minTermurah = it.menu.getHarga();
            }
        }
        double bogo = (total > 50000 && adaMinuman) ? minTermurah : 0;
        if (bogo > 0) {
            System.out.printf("Promo BOGO  : -%s%n",
                    new java.text.DecimalFormat("Rp #,###").format(bogo));
        }

        double diskon10 = (total > 100000) ? total * 0.10 : 0;
        if (diskon10 > 0) {
            System.out.printf("Diskon 10%% : -%s%n",
                    new java.text.DecimalFormat("Rp #,###").format(diskon10));
        }

        double setelah = total - potonganDiskon() - bogo - diskon10;
        if (setelah < 0) setelah = 0;
        double pajak = setelah * 0.10;

        System.out.println("--------------------------------------");
        System.out.printf("Total Setelah Promo : %s%n",
                new java.text.DecimalFormat("Rp #,###").format(setelah));
        System.out.printf("Pajak (10%%)         : %s%n",
                new java.text.DecimalFormat("Rp #,###").format(pajak));
        System.out.printf("Biaya Pelayanan     : %s%n", "Rp 20.000");
        System.out.println("======================================");
        System.out.printf("TOTAL AKHIR         : %s%n",
                new java.text.DecimalFormat("Rp #,###").format(totalAkhir()));
        System.out.println("======================================\n");
    }

    private double potonganDiskon() {
        if (diskon == null) return 0;
        double d = diskon.getDiskon();
        return (d > 0 && d <= 100) ? totalKotor() * (d / 100.0) : d;
    }

    public void simpanStruk(String file) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("STRUK PEMBAYARAN RESTORAN");
            w.newLine();
            w.write("========================");
            w.newLine();
            for (Item it : items) {
                w.write(it.menu.getNama() + " x" + it.qty +
                        " = " + new java.text.DecimalFormat("Rp #,###").format(it.menu.getHarga() * it.qty));
                w.newLine();
            }
            w.write("----------------------------------------");
            w.newLine();
            w.write("TOTAL AKHIR : " + new java.text.DecimalFormat("Rp #,###").format(totalAkhir()));
            w.newLine();
            w.write("========================");
            w.newLine();
        }
    }

    public void muatStruk(String file) throws IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) System.out.println(line);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File struk tidak ditemukan.");
        }
    }
}
