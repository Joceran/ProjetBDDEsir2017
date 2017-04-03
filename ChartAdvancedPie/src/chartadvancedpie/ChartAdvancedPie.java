package chartadvancedpie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * An advanced pie chart with a variety of actions and settable properties.
 *
 * @see javafx.scene.chart.PieChart
 * @see javafx.scene.chart.Chart
 */
public class ChartAdvancedPie extends Application {

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));
        root.getChildren().add(createChart());
    }

    protected PieChart createChart() {
        ArrayList<PieChart.Data> listePieChart = new ArrayList<PieChart.Data>();
        String url = "jdbc:mysql://148.60.11.205:3306/labonnesastar?autoReconnect=true&useSSL=false";
        try {
            Connection conn = DriverManager.getConnection(url, "root", "projet");
            java.sql.Statement requete;
            requete = conn.createStatement();
            java.sql.ResultSet ensresul;
            ensresul = requete.executeQuery("select count(*), day(depart) from passage group by day(depart) asc");
            while (ensresul.next()) {
                PieChart.Data temp = new PieChart.Data(getJour(ensresul.getString(2)), Double.parseDouble(ensresul.getString(1)));
                listePieChart.add(temp);
            }
            ensresul.close();
            requete.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        listePieChart.set(0, new PieChart.Data("Vendredi", listePieChart.get(0).getPieValue() + listePieChart.get(listePieChart.size() - 1).getPieValue()));
        listePieChart.remove(listePieChart.size() - 1);
        pc = new PieChart(FXCollections.observableArrayList(listePieChart));
        pc.setId("BasicPie");
        pc.setTitle("Représentation du nombre de passages globaux chaque jour");

        return pc;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(test);
        init(primaryStage);
        primaryStage.show();
    }

    public String getJour(String s) {

        if (s.equals("10")) {
            return Jour.vendredi.toString();
        } else if (s.equals("11")) {
            return Jour.samedi.toString();
        } else if (s.equals("12")) {
            return Jour.dimanche.toString();
        } else if (s.equals("13")) {
            return Jour.lundi.toString();
        } else if (s.equals("14")) {
            return Jour.mardi.toString();
        } else if (s.equals("15")) {
            return Jour.mercredi.toString();
        } else if (s.equals("16")) {
            return Jour.jeudi.toString();
        } else {
            return Jour.vendredi.toString();
        }

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            test = "çamarche";
        } else {
            test = "marchepas";
        }
        launch(args);
    }

    public static String test = "toast";
    public static PieChart pc;

    enum Jour {
        lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche
    }
}
