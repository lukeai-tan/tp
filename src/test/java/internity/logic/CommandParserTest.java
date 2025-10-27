package internity.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import internity.commands.AddCommand;
import internity.commands.Command;
import internity.commands.DashboardCommand;
import internity.commands.DeleteCommand;
import internity.commands.ExitCommand;
import internity.commands.ListCommand;
import internity.commands.UpdateCommand;
import internity.commands.UsernameCommand;
import internity.core.InternityException;

/**
 * Unit tests for {@link CommandParser}.
 * These tests ensure that the parser correctly interprets input and returns the proper Command objects,
 * or throws InternityException for invalid inputs.
 */
class CommandParserTest {

    private CommandParser commandParser;

    @BeforeEach
    void setUp() {
        commandParser = new CommandParser();
    }

    @Test
    void parseInput_exitCommand_returnsExitCommand() throws InternityException {
        Command command = commandParser.parseInput("exit");
        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    void parseInput_listCommand_returnsListCommand() throws InternityException {
        Command command = commandParser.parseInput("list");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    void parseInput_addCommand_returnsAddCommand() throws InternityException {
        String input = "add company/Umbrella Corp role/Researcher deadline/10-10-2025 pay/1000";
        Command command = commandParser.parseInput(input);
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parseInput_deleteCommand_returnsDeleteCommand() throws InternityException {
        Command command = commandParser.parseInput("delete 1");
        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    void parseInput_updateCommand_returnsUpdateCommand() throws InternityException {
        Command command = commandParser.parseInput("update 1 status/Pending");
        assertInstanceOf(UpdateCommand.class, command);
    }

    @Test
    void parseInput_usernameCommand_returnsUsernameCommand() throws InternityException {
        Command command = commandParser.parseInput("username Badger");
        assertInstanceOf(UsernameCommand.class, command);
    }

    @Test
    void parseInput_dashboardCommand_returnsDashboardCommand() throws InternityException {
        Command command = commandParser.parseInput("dashboard");
        assertInstanceOf(DashboardCommand.class, command);
    }

    @Test
    void parseInput_nullInput_throwsInvalidInput() {
        InternityException exception = assertThrows(
                InternityException.class,
                () -> commandParser.parseInput(null)
        );
        assertEquals("Input cannot be null or blank", exception.getMessage());
    }

    @Test
    void parseInput_blankInput_throwsInvalidInput() {
        InternityException exception = assertThrows(
                InternityException.class,
                () -> commandParser.parseInput("   ")
        );
        assertEquals("Input cannot be null or blank", exception.getMessage());
    }

    @Test
    void parseInput_unknownCommand_throwsUnknownCommand() {
        InternityException exception = assertThrows(
                InternityException.class,
                () -> commandParser.parseInput("AbsoluteCinema")
        );
        assertEquals("Unknown command: absolutecinema", exception.getMessage());
    }
}

