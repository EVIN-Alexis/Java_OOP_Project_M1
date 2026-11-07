package brasserie.service;

import brasserie.model.Customer;
import brasserie.model.ProfessionalCustomer;
import brasserie.model.RegularCustomer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerService {

    private List<Customer> customers;

    public CustomerService() {
        customers = new ArrayList<>();

        customers.add(new ProfessionalCustomer(
                "PDD",
                Arrays.asList("Blonde", "Rousse"),
                30, 100, 0.80f
        ));

        customers.add(new ProfessionalCustomer(
                "PDR",
                Arrays.asList("Rousse", "Brune"),
                50, 80, 0.95f
        ));

        customers.add(new ProfessionalCustomer(
                "BDE ISEN",
                Arrays.asList("Brune", "Rousse", "IPA"),
                24, 150, 1.10f
        ));

        customers.add(new ProfessionalCustomer(
                "Faluche",
                Arrays.asList("IPA", "Brune"),
                40, 120, 0.85f
        ));

        customers.add(new RegularCustomer(
                "Mathéo LESAGE--GANTE",
                Arrays.asList("Rousse"),
                6, 24, 1.50f
        ));

        customers.add(new RegularCustomer(
                "Alexis EVIN",
                Arrays.asList("Blonde", "Brune"),
                3, 18, 0.70f
        ));
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
}