package internity.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import internity.commands.AddCommand;
import internity.commands.ListCommand;
import internity.commands.UpdateCommand;
import internity.commands.UsernameCommand;
import internity.core.Date;
import internity.core.InternityException;
import internity.core.Internship;
import internity.core.InternshipList;

class ArgumentParserTest {
    
    @BeforeEach
    void setUp() {
        InternshipList.clear();
        InternshipList.add(new Internship("Google", "SWE", new Date(1, 1, 2025), 8000));
    }

    @Test
    void parseAddCommandArgs_validArgs_returnsAddCommand() throws Exception {
        String args = "company/Microsoft role/Intern deadline/10-10-2025 pay/5000";
        AddCommand command = ArgumentParser.parseAddCommandArgs(args);
        assertNotNull(command);

        command.execute();
        Internship added = InternshipList.get(1);
        assertEquals("Microsoft", added.getCompany());
        assertEquals("Intern", added.getRole());
        Date deadline = added.getDeadline();
        assertEquals(10, deadline.getDay());
        assertEquals(10, deadline.getMonth());
        assertEquals(2025, deadline.getYear());
        assertEquals(5000, added.getPay());
    }

    @Test
    void parseAddCommandArgs_payZero_addsInternshipWithZeroPay() throws Exception {
        String args = "company/Microsoft role/Intern deadline/10-10-2025 pay/0";
        AddCommand command = ArgumentParser.parseAddCommandArgs(args);
        assertNotNull(command);

        command.execute();
        Internship added = InternshipList.get(1);
        assertEquals("Microsoft", added.getCompany());
        assertEquals("Intern", added.getRole());
        assertEquals(10, added.getDeadline().getDay());
        assertEquals(10, added.getDeadline().getMonth());
        assertEquals(2025, added.getDeadline().getYear());
        assertEquals(0, added.getPay());
    }

    @Test
    void parseAddCommandArgs_negativePay_throwsInternityException() {
        String args = "company/Microsoft role/Intern deadline/10-10-2025 pay/-1000";
        assertThrows(InternityException.class,
                () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_floatingPointPay_throwsInternityException() {
        String args = "company/Microsoft role/Intern deadline/10-10-2025 pay/5000.50";
        assertThrows(InternityException.class,
                () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_validArgsWithExtraSpaces_returnsAddCommand() throws Exception {
        String args = "company/Microsoft   role/Intern   deadline/15-12-2025   pay/5000";
        AddCommand command = ArgumentParser.parseAddCommandArgs(args);
        assertNotNull(command);
    }

    @Test
    void parseAddCommandArgs_nullArgs_throwsException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(null));
    }

    @Test
    void parseAddCommandArgs_blankArgs_throwsException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs("   "));
    }

    @Test
    void parseAddCommandArgs_missingCompanyTag_throwsException() {
        String args = "role/Intern deadline/15-12-2025 pay/5000";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_missingDeadlineTag_throwsException() {
        String args = "company/Microsoft role/Intern pay/5000";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_wrongOrder_throwsException() {
        String args = "role/Intern company/Microsoft deadline/15-12-2025 pay/5000";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_emptyCompany_throwsException() {
        String args = "company/ role/Intern deadline/15-12-2025 pay/5000";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_emptyRole_throwsException() {
        String args = "company/Microsoft role/ deadline/15-12-2025 pay/5000";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_negativePay_throwsException() {
        String args = "company/Microsoft role/Intern deadline/15-12-2025 pay/-100";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_nonNumericPay_throwsException() {
        String args = "company/Microsoft role/Intern deadline/15-12-2025 pay/fivek";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_invalidDateFormat_throwsException() {
        String args = "company/Microsoft role/Intern deadline/2025/12/15 pay/5000";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_companyNameTooLong_throwsException() {
        String longCompany = "A".repeat(InternshipList.COMPANY_MAXLEN + 1);
        String args = String.format("company/%s role/Intern deadline/15-12-2025 pay/5000", longCompany);
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_roleTooLong_throwsException() {
        String longRole = "B".repeat(InternshipList.ROLE_MAXLEN + 1);
        String args = String.format("company/Microsoft role/%s deadline/15-12-2025 pay/5000", longRole);
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_incompleteTags_throwsException() {
        String args = "company/Microsoft role deadline/15-12-2025 pay/5000";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseAddCommandArgs_extraTags_throwsException() {
        String args = "company/Microsoft role/Intern deadline/15-12-2025 pay/5000 extra/field";
        assertThrows(InternityException.class, () -> ArgumentParser.parseAddCommandArgs(args));
    }

    @Test
    void parseUpdateCommandArgs_validSingleField_returnsCommand() throws InternityException {
        UpdateCommand command = ArgumentParser.parseUpdateCommandArgs("1 company/Meta");
        assertNotNull(command);
    }

    @Test
    void parseUpdateCommandArgs_multipleFields_returnsCommand() throws InternityException {
        UpdateCommand command = ArgumentParser.parseUpdateCommandArgs("1 company/Meta role/AI Intern pay/9000");
        assertNotNull(command);
    }

    @Test
    void parseUpdateCommandArgs_invalidIndex_throwsException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseUpdateCommandArgs("abc company/Meta"));
    }

    @Test
    void parseUpdateCommandArgs_missingFieldTag_throwsException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseUpdateCommandArgs("1 Meta"));
    }

    @Test
    void parseUpdateCommandArgs_emptyCompany_throwsException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseUpdateCommandArgs("1 company/ "));
    }

    @Test
    void parseDeleteCommandArgs_nonNumericIndex_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseDeleteCommandArgs("abc"));
    }

    @Test
    void parseDeleteCommandArgs_blankArgs_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseDeleteCommandArgs("   "));
    }

    @Test
    void parseDeleteCommandArgs_nullArgs_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseDeleteCommandArgs(null));
    }

    @Test
    void parseListCommandArgs_sortAsc_returnsAscendingListCommand() throws InternityException {
        ListCommand command = ArgumentParser.parseListCommandArgs("sort/asc");
        assertNotNull(command);
    }

    @Test
    void parseListCommandArgs_sortDesc_returnsDescendingListCommand() throws InternityException {
        ListCommand command = ArgumentParser.parseListCommandArgs("sort/desc");
        assertNotNull(command);
    }

    @Test
    void parseListCommandArgs_sortAscWithWhitespace_returnsAscendingListCommand() throws InternityException {
        ListCommand command = ArgumentParser.parseListCommandArgs("sort/   asc");
        assertNotNull(command);
    }

    @Test
    void parseListCommandArgs_sortDescWithWhitespace_returnsAscendingListCommand() throws InternityException {
        ListCommand command = ArgumentParser.parseListCommandArgs("sort/   desc");
        assertNotNull(command);
    }

    @Test
    void parseListCommandArgs_invalidSortOrder_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseListCommandArgs("sort/upwards"));
    }

    @Test
    void parseListCommandArgs_nullSortSuffix_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseListCommandArgs("/sort"));
    }

    @Test
    void parseListCommandArgs_blankSortSuffix_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseListCommandArgs("/sort   "));
    }

    @Test
    void parseListCommandArgs_missingSortPrefix_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseListCommandArgs("asc"));
    }

    @Test
    void parseListCommandArgs_multipleSortArgs_throwsInternityException() {
        assertThrows(InternityException.class, () -> ArgumentParser.parseListCommandArgs("sort/asc sort/desc"));
    }

    @Test
    void parseUsernameCommandArgs_validArgs_shouldReturnUsernameCommand() throws Exception {
        String validUsername = "Jesse Pinkman";
        UsernameCommand command = ArgumentParser.parseUsernameCommandArgs(validUsername);
        assertNotNull(command, "Returned UsernameCommand should not be null");
    }

    @Test
    void parseUsernameCommandArgs_nullArgs_shouldThrowInternityException() {
        InternityException exception = assertThrows(
                InternityException.class,
                () -> ArgumentParser.parseUsernameCommandArgs(null),
                "Expected InternityException for null input"
        );
        assertTrue(exception.getMessage().toLowerCase().contains("invalid"),
                "Exception message should indicate invalid username command");
    }

    @Test
    void parseUsernameCommandArgs_blankArgs_shouldThrowInternityException() {
        InternityException exception = assertThrows(
                InternityException.class,
                () -> ArgumentParser.parseUsernameCommandArgs("   "),
                "Expected InternityException for blank input"
        );
        assertTrue(exception.getMessage().toLowerCase().contains("invalid"),
                "Exception message should indicate invalid username command");
    }

    @Test
    void parseUsernameCommandArgs_emptyString_shouldThrowInternityException() {
        InternityException exception = assertThrows(
                InternityException.class,
                () -> ArgumentParser.parseUsernameCommandArgs(""),
                "Expected InternityException for empty input"
        );
        assertTrue(exception.getMessage().toLowerCase().contains("invalid"),
                "Exception message should indicate invalid username command");
    }
}
