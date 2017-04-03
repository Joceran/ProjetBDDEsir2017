package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import org.joda.time.DateTime;

public class Main {

    public static DateTime dateDeDepartLaPlusGrandeStockee = new DateTime(2000, 1, 1, 0, 0, 0);
    public static DateTime dateDeDepartLaPlusGrandeAstockee = new DateTime(2000, 1, 1, 0, 0, 0);
    public static int i = 0;
    public static boolean firstTime = true;

    public static void main(String[] args) throws IOException, InterruptedException, FileNotFoundException, SQLException {
        DateTime now;
        BufferedWriter fsor;
        File f;
        int cpt = 0;
        while (true) {
            now = DateTime.now();
            telechargerFichier();
            f = new File("/root/fichier.csv");
            if (f.exists()) {
                lireFichier(now);
                enregistrerDateLaPlusGrande();
            }
            cpt++;
            fsor = new BufferedWriter(new FileWriter("/root/nbboucle.txt"));
            fsor.write("tourne depuis " + (cpt * 10 / 60));
            fsor.close();
            Runtime.getRuntime().exec("rm /root/fichier.csv");
            Thread.sleep(10 * 60 * 1000);//on attend 10 minutes 10*60*1000
        }
    }//fin méthode main

    public static void telechargerFichier() throws IOException {
        Path des = new File("/root/fichier.csv").toPath();
        try {
            Files.copy(new URL("https://data.explore.star.fr"
                    + "/explore/dataset/tco-bus-circulation-passages-tr"
                    + "/download/?format=csv&timezone=Europe"
                    + "/Berlin&use_labels_for_header=true").openStream(), des, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            erreur(e.toString());
        }
    }

    public static boolean ligneBonne(DateTime now, String ligne) {
        DateTime dateDepart;
        ArrayList<String> res = new ArrayList<>();
        int cptPointVirgule = 0;
        int i;
        for (i = 0; i < ligne.length() && cptPointVirgule < 10; i++) {
            if (ligne.charAt(i) == ';') {
                cptPointVirgule++;
            }
        }
        StringBuilder temp = new StringBuilder("");
        for (int y = i; y < ligne.length() && ligne.charAt(y) != '+'; y++) {
            if (ligne.charAt(y) != '-' && ligne.charAt(y) != ':' && ligne.charAt(y) != 'T') {
                temp.append(ligne.charAt(y));
            } else if (ligne.charAt(y) == '-' || ligne.charAt(y) == ':' || ligne.charAt(y) == 'T') {
                res.add(temp.toString());
                temp.delete(0, temp.length());
            }
        }
        res.add(temp.toString());
        temp.delete(0, temp.length());

        if (res.size() >= 6) {

            dateDepart = new DateTime(Integer.parseInt(res.get(0)),
                    Integer.parseInt(res.get(1)),
                    Integer.parseInt(res.get(2)),
                    Integer.parseInt(res.get(3)),
                    Integer.parseInt(res.get(4)),
                    Integer.parseInt(res.get(5)));

            if (firstTime == false) {
                if (now.isAfter(dateDepart) && dateDepart.isAfter(dateDeDepartLaPlusGrandeStockee)) {
                    if (dateDepart.isAfter(dateDeDepartLaPlusGrandeStockee) && dateDepart.isAfter(dateDeDepartLaPlusGrandeAstockee)) {
                        dateDeDepartLaPlusGrandeAstockee = dateDepart;
                    }
                    return true;
                }
            } else {
                if (now.isAfter(dateDepart)) {
                    if (dateDepart.isAfter(dateDeDepartLaPlusGrandeAstockee)) {
                        dateDeDepartLaPlusGrandeAstockee = dateDepart;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static void enregistrerDateLaPlusGrande() throws IOException {
        try {
            FileOutputStream fichier;
            fichier = new FileOutputStream("/root/derniere_date.ser");
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(fichier);
            if (dateDeDepartLaPlusGrandeAstockee.isAfter(dateDeDepartLaPlusGrandeStockee)) {
                oos.writeObject(dateDeDepartLaPlusGrandeAstockee);
            } else {
                oos.writeObject(dateDeDepartLaPlusGrandeStockee);
            }
            oos.flush(); // pour forcer le tampon à se vider dans le fichier 
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            erreur(e.toString());
            System.exit(1);
        }
    }

    public static void lireFichier(DateTime now) throws FileNotFoundException, IOException, SQLException {
        BufferedReader fent;
        fent = new BufferedReader(new FileReader(new File("/root/fichier.csv")));
        String enr;
        boolean firstline = true;
        File fe;
        fe = new File("/root/derniere_date.ser");
        if (fe.exists()) {
            try {
                FileInputStream fichier;
                fichier = new FileInputStream(fe);
                ObjectInputStream ois;
                ois = new ObjectInputStream(fichier);
                dateDeDepartLaPlusGrandeStockee = (DateTime) ois.readObject();//on récupère l'ancienne plus grande date
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                erreur(e.toString());
                System.exit(1);
            }
            firstTime = false;
        }

        String url = "jdbc:mysql://148.60.11.205:3306/labonnesastar?autoReconnect=true&useSSL=false";
        Connection conn = DriverManager.getConnection(url, "root", "projet");

        while ((enr = fent.readLine()) != null) // null en cas de fin de fichier
        {
            if (firstline) {
                firstline = false;
            } else {
                if (ligneBonne(now, enr)) {
                    i++;
                    enregistrerLigneDansBDD(enr, conn);
                }
            }
        }
        fent.close();
        conn.close();
    }

    public static void enregistrerLigneDansBDD(String ligne, Connection conn) throws IOException {
        StringBuilder champ = new StringBuilder();
        int cptPointVirgule = 0;
        int cpt = 0;
        String[] tableau = new String[8];
        for (int i = 0; i < ligne.length(); i++) {
            if (ligne.charAt(i) == ';') {
                if (cptPointVirgule == 0
                        || cptPointVirgule == 2
                        || cptPointVirgule == 4
                        || cptPointVirgule == 7
                        || cptPointVirgule == 8
                        || cptPointVirgule == 9
                        || cptPointVirgule == 10
                        || cptPointVirgule == 11) {
                    tableau[cpt] = champ.toString();
                    cpt++;
                }
                champ.delete(0, champ.length());
                cptPointVirgule++;

            } else {
                champ.append(ligne.charAt(i));
            }
        }
        /*
        tableau[0] = id_ligne
        tableau[1] = code_sens
        tableau[2] = id_point_arret
        tableau[3] = arrivee_theorique
        tableau[4] = depart_theorique
        tableau[5] = arrivee
        tableau[6] = depart
        tableau[7] = id_course
         */

        System.out.println("(id_ligne=" + tableau[0] + ",code_sens=" + tableau[1] + ",id_point_arret=" + tableau[2]
                + "',depart_theorique='" + heureFormatSQL(tableau[4]) + "',depart='" + heureFormatSQL(tableau[6])
                + "',difference=" + getDifferenceEntreDeuxDates(tableau[6], tableau[4]));

        try {
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO passage (id_ligne,"
                    + " code_sens, id_point_arret,"
                    + " arrivee_theorique, arrivee, depart_theorique, depart, ecart_depart, id_course)VALUES "
                    + "(" + tableau[0] + "," + tableau[1] + "," + tableau[2]
                    + ",'" + heureFormatSQL(tableau[3]) + "','" + heureFormatSQL(tableau[5])
                    + "','" + heureFormatSQL(tableau[4]) + "','" + heureFormatSQL(tableau[6])
                    + "'," + getDifferenceEntreDeuxDates(tableau[6], tableau[4]) + "," + tableau[7] + ")");
            st.close();
        } catch (SQLException e) {
            erreur(e.toString());
        }
    }

    public static String heureFormatSQL(String heureCSV) {
        StringBuilder res = new StringBuilder();
        res.append(heureCSV.substring(0, heureCSV.indexOf("+")));
        return res.toString().replace('T', ' ');
    }

    public static long getDifferenceEntreDeuxDates(String departReel, String departPrevu) {
        DateTime dateDepartReel, dateDepartPrevu;
        ArrayList<String> liste = new ArrayList<>();
        StringBuilder temp = new StringBuilder("");
        int y;
        for (y = 0; y < departReel.length() && departReel.charAt(y) != '+'; y++) {
            if (departReel.charAt(y) != '-' && departReel.charAt(y) != ':' && departReel.charAt(y) != 'T') {
                temp.append(departReel.charAt(y));
            } else if (departReel.charAt(y) == '-' || departReel.charAt(y) == ':' || departReel.charAt(y) == 'T') {
                liste.add(temp.toString());
                temp.delete(0, temp.length());
            }
        }
        liste.add(temp.toString());
        temp.delete(0, temp.length());

        dateDepartReel = new DateTime(Integer.parseInt(liste.get(0)),
                Integer.parseInt(liste.get(1)),
                Integer.parseInt(liste.get(2)),
                Integer.parseInt(liste.get(3)),
                Integer.parseInt(liste.get(4)),
                Integer.parseInt(liste.get(5))
        );

        liste.clear();

        for (y = 0; y < departPrevu.length() && departPrevu.charAt(y) != '+'; y++) {
            if (departPrevu.charAt(y) != '-' && departPrevu.charAt(y) != ':' && departPrevu.charAt(y) != 'T') {
                temp.append(departPrevu.charAt(y));
            } else if (departPrevu.charAt(y) == '-' || departPrevu.charAt(y) == ':' || departPrevu.charAt(y) == 'T') {
                liste.add(temp.toString());
                temp.delete(0, temp.length());
            }
        }
        liste.add(temp.toString());
        temp.delete(0, temp.length());

        dateDepartPrevu = new DateTime(Integer.parseInt(liste.get(0)),
                Integer.parseInt(liste.get(1)),
                Integer.parseInt(liste.get(2)),
                Integer.parseInt(liste.get(3)),
                Integer.parseInt(liste.get(4)),
                Integer.parseInt(liste.get(5))
        );

        liste.clear();

        return (dateDepartReel.getMillis() - dateDepartPrevu.getMillis()) / 1000;
    }

    public static void enregistrerListeLigneBusDansBDD(Connection conn) throws FileNotFoundException, IOException, SQLException {
        BufferedReader fent;
        fent = new BufferedReader(new FileReader(new File("F:\\Téléchargements\\tco-bus-topologie-lignes-td.csv")));
        String enr;
        boolean firstline = true;

        while ((enr = fent.readLine()) != null) // null en cas de fin de fichier
        {
            if (firstline) {
                firstline = false;
            } else {
                try {
                    Statement st = conn.createStatement();
                    st.executeUpdate("INSERT INTO ligne_bus (id_ligne, nom_ligne )VALUES (" + Integer.parseInt(getChampDansLigne(enr, 1)) + ", '" + getChampDansLigne(enr, 3).replace('\'', ' ') + "') ");
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    erreur(e.toString());
                }
                System.out.println("INSERT INTO ligne_bus (id_ligne, nom_ligne )VALUES (" + Integer.parseInt(getChampDansLigne(enr, 1)) + ", '" + getChampDansLigne(enr, 3).replace('\'', ' ') + "') ");
            }
        }
        fent.close();
    }

    public static void enregistrerListeArretDansBDD(Connection conn) throws FileNotFoundException, IOException, SQLException {
        BufferedReader fent;
        fent = new BufferedReader(new FileReader(new File("F:\\Téléchargements\\tco-bus-topologie-pointsarret-td.csv")));
        String enr;
        boolean firstline = true;

        while ((enr = fent.readLine()) != null) // null en cas de fin de fichier
        {
            if (firstline) {
                firstline = false;
            } else {
                try {
                    Statement st = conn.createStatement();
                    st.executeUpdate("INSERT INTO arret_bus (id_arret, nom_arret, commune_arret)VALUES (" + getChampDansLigne(enr, 1) + ", '" + getChampDansLigne(enr, 3).replace('\'', ' ') + "','" + getChampDansLigne(enr, 5).replace('\'', ' ') + "') ");
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    erreur(e.toString());
                }
                System.out.println("INSERT INTO arret_bus (id_arret, nom_arret, commune_arret)VALUES (" + getChampDansLigne(enr, 1) + ", '" + getChampDansLigne(enr, 3).replace('\'', ' ') + "','" + getChampDansLigne(enr, 5).replace('\'', ' ') + "') ");
            }
        }
        fent.close();
    }

    public static String getChampDansLigne(String ligne, int colonneDuChamp) {
        if (colonneDuChamp == 1) {
            return ligne.substring(0, ligne.indexOf(";"));
        } else {
            StringBuilder res = new StringBuilder();
            int cptPointVirgule = 0;
            int i;
            for (i = 0; i < ligne.length() && cptPointVirgule < colonneDuChamp - 1; i++) {
                if (ligne.charAt(i) == ';') {
                    cptPointVirgule++;
                }
            }
            while (ligne.charAt(i) != ';') {
                res.append(ligne.charAt(i));
                i++;
            }
            return res.toString();
        }
    }

    public static void erreur(String s) throws IOException {
        File fe;
        ArrayList<String> liste;
        liste = new ArrayList<>();
        fe = new File("/root/erreur.txt");
        BufferedWriter fsor;
        fsor = new BufferedWriter(new FileWriter("/root/erreur.txt"));
        if (fe.exists()) {
            BufferedReader fent;
            fent = new BufferedReader(new FileReader(fe));
            String enr;
            while ((enr = fent.readLine()) != null) // null en cas de fin de fichier
            {
                liste.add(enr + "\r\n");
            }
            fent.close();

            for (String sa : liste) {
                fsor.write(sa);
            }
        }
        fsor.write(s);
        fsor.close();
        liste.clear();
    }
}//fin class main
