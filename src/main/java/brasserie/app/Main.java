package brasserie.app;

import brasserie.model.Company;
import brasserie.service.DayService;
import brasserie.service.FinanceService;
import brasserie.ui.MainWindow;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Company company = new Company("Ma Brasserie");

            // Création du service financier partagé
            FinanceService financeService = new FinanceService();

            // On transmet le même service à DayService
            DayService dayService = new DayService(financeService);

            MainWindow window = new MainWindow(
                    company,
                    dayService
            );

            window.setVisible(true);
        });
    }
}