package brasserie.service;

import brasserie.model.Company;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DayServiceTest {

    @Test
    void shouldAdvanceToNextDay() {
        Company company = new Company("Ma Brasserie");
        DayService dayService = new DayService();

        int initialDay = company.getCurrentDay();

        dayService.advanceDay(company);

        assertEquals(initialDay + 1, company.getCurrentDay());
    }

    @Test
    void shouldAdvanceSeveralDays() {
        Company company = new Company("Ma Brasserie");
        DayService dayService = new DayService();

        dayService.advanceDay(company);
        dayService.advanceDay(company);
        dayService.advanceDay(company);

        assertEquals(4, company.getCurrentDay());
    }
}