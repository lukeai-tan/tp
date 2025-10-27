package internity.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import internity.logic.ArgumentParser;
import internity.core.InternityException;
import internity.core.Internship;
import internity.core.InternshipList;
import internity.utils.DateFormatter;

class AddCommandTest {
    @BeforeEach
    void setup() {
        // Clear any previously added internships before each test
        InternshipList.clear();
    }

    /**
     * Tests that a valid command correctly parses all fields
     * and adds a new internship to the InternshipList.
     */
    @Test
    void execute_validInput_addsInternshipSuccessfully() throws InternityException {
        String args = "company/Google role/Software Engineer deadline/01-12-2025 pay/120000";
        AddCommand command = ArgumentParser.parseAddCommandArgs(args);
        command.execute();

        // Validate that one internship was added
        assertEquals(1, InternshipList.size(), "Expected one internship to be added");

        Internship added = InternshipList.get(0);
        assertEquals("Google", added.getCompany());
        assertEquals("Software Engineer", added.getRole());
        assertEquals(DateFormatter.parse("01-12-2025").toString(), added.getDeadline().toString());
        assertEquals(120000, added.getPay());
    }

    /**
     * Tests that the command throws an exception if one of the fields is missing.
     */
    @Test
    void execute_missingField_throwsException() {
        String args = "company/Google role/Software Engineer pay/1000"; // missing deadline

        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args),
                "Expected InternityException when deadline field is missing");
    }

    /**
     * Tests that the command throws an exception for invalid pay values.
     */
    @Test
    void execute_invalidPay_throwsException() {
        String args = "company/Amazon role/Data Analyst deadline/01-12-2025 pay/notANumber";

        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args),
                "Expected InternityException for non-numeric pay");
    }

    /**
     * Tests that the command does not signal application exit.
     */
    @Test
    void isExit_returnsFalse() throws InternityException {
        String args = "company/Test role/Tester deadline/01-01-2026 pay/1000";
        AddCommand command = ArgumentParser.parseAddCommandArgs(args);
        assertFalse(command.isExit(), "AddCommand should not terminate the application");
    }
}
