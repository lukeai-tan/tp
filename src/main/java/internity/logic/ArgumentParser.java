package internity.logic;

import java.util.logging.Logger;

import internity.commands.AddCommand;
import internity.commands.DeleteCommand;
import internity.commands.FindCommand;
import internity.commands.ListCommand;
import internity.commands.UpdateCommand;
import internity.commands.UsernameCommand;
import internity.core.Date;
import internity.core.InternityException;
import internity.core.InternshipList;
import internity.utils.DateFormatter;

/**
 * A utility class responsible for parsing command-line arguments for various commands
 * such as Add, Delete, Find, Update and List.
 */
public final class ArgumentParser {
    private static final int ADD_COMMAND_PARTS = 4;
    private static final int IDX_COMPANY = 0;
    private static final int IDX_ROLE = 1;
    private static final int IDX_DEADLINE = 2;
    private static final int IDX_PAY = 3;

    private static final Logger logger = Logger.getLogger(ArgumentParser.class.getName());

    /**
     * Private constructor to prevent instantiation of the ArgumentParser class.
     */
    private ArgumentParser() {
    }

    /**
     * Parses the arguments for Add Command to create an {@link AddCommand} instance.
     *
     * @param args arguments for {@link AddCommand}
     * @return an instance of {@link AddCommand} constructed from the parsed arguments.
     * @throws InternityException if the arguments are missing or invalid.
     */
    public static AddCommand parseAddCommandArgs(String args) throws InternityException {
        if (args == null || args.isBlank()) {
            throw InternityException.invalidAddCommand();
        }

        assert !args.isBlank() : "Arguments cannot be blank after validation";

        try {
            String[] parts = args.split("\\s+(?=company/|role/|deadline/|pay/)");
            if (parts.length != ADD_COMMAND_PARTS ||
                    !parts[IDX_COMPANY].startsWith("company/") ||
                    !parts[IDX_ROLE].startsWith("role/") ||
                    !parts[IDX_DEADLINE].startsWith("deadline/") ||
                    !parts[IDX_PAY].startsWith("pay/")) {
                logger.severe("One or more arguments of Add command is in the wrong order.");
                throw InternityException.invalidAddCommand();
            }

            logger.info("Successfully parsed 4 arguments of AddCommand.");

            String company = parts[IDX_COMPANY].substring("company/".length()).trim();
            String role = parts[IDX_ROLE].substring("role/".length()).trim();
            Date deadline = DateFormatter.parse(parts[IDX_DEADLINE].substring("deadline/".length()).trim());
            int pay = Integer.parseInt(parts[IDX_PAY].substring("pay/".length()).trim());

            // throw exception on empty input or invalid pay
            if (company.isEmpty() || role.isEmpty() || pay < 0) {
                logger.severe("One or more arguments of Add command is empty or invalid.");
                throw InternityException.invalidAddCommand();
            }

            // throw exception on exceeding max length
            if (company.length() > InternshipList.COMPANY_MAXLEN ||
                    role.length() > InternshipList.ROLE_MAXLEN) {
                logger.severe("One or more arguments exceeded max length.");
                throw InternityException.invalidAddCommand();
            }

            return new AddCommand(company, role, deadline, pay);
        } catch (Exception e) {
            logger.severe("Error executing Add Command: " + e.getMessage());
            throw InternityException.invalidAddCommand();
        }
    }

    /**
     * Parses the arguments for Delete Command to create an {@link DeleteCommand} instance.
     *
     * @param args arguments for {@link DeleteCommand}
     * @return an instance of {@link DeleteCommand} constructed from the parsed arguments.
     * @throws InternityException if the arguments are missing or invalid.
     */
    public static DeleteCommand parseDeleteCommandArgs(String args) throws InternityException {
        if (args == null || args.isBlank()) {
            throw InternityException.invalidDeleteCommand();
        }
        try {
            int oneBasedIndex = Integer.parseInt(args.trim());

            int zeroBasedIndex = oneBasedIndex - 1;

            return new DeleteCommand(zeroBasedIndex);

        } catch (NumberFormatException e) {
            throw InternityException.invalidInternshipIndex();
        }
    }

    /**
     * Parses the arguments for Find Command to create a {@link FindCommand} instance.
     *
     * @param args arguments for {@link FindCommand}
     * @return an instance of {@link FindCommand} constructed from the parsed arguments.
     * @throws InternityException if the arguments are missing.
     */
    public static FindCommand parseFindCommandArgs(String args) throws InternityException {
        if (args == null || args.isBlank()) {
            throw InternityException.invalidFindCommand();
        }
        return new FindCommand(args);
    }

    /**
     * Parses the arguments for Update Command to create an {@link UpdateCommand} instance.
     *
     * @param args arguments for {@link UpdateCommand}
     * @return an instance of {@link UpdateCommand} constructed from the parsed arguments.
     * @throws InternityException if the arguments are missing or invalid.
     */
    public static UpdateCommand parseUpdateCommandArgs(String args) throws InternityException {
        String trimmed = requireArgs(args);
        String[] idxAndTagged = splitIndexAndTagged(trimmed);
        int index = parseOneBasedIndex(idxAndTagged[0]);
        String tagged = requireTagged(idxAndTagged[1]);

        String[] parts = tagged.split("\\s+(?=company/|role/|deadline/|pay/|status/)");
        String company = null;
        String role = null;
        Date deadline = null;
        Integer pay = null;
        String status = null;

        try {
            for (String part : parts) {
                String p = part.trim();
                if (p.isEmpty()) {
                    continue;
                }
                if (p.startsWith("company/")) {
                    company = valueAfterTag(p, "company/");
                    if (company.isEmpty()) {
                        throw InternityException.emptyField("company/");
                    }
                } else if (p.startsWith("role/")) {
                    role = valueAfterTag(p, "role/");
                    if (role.isEmpty()) {
                        throw InternityException.emptyField("role/");
                    }
                } else if (p.startsWith("deadline/")) {
                    String d = valueAfterTag(p, "deadline/");
                    deadline = DateFormatter.parse(d);
                } else if (p.startsWith("pay/")) {
                    String payStr = valueAfterTag(p, "pay/");
                    int payVal = Integer.parseInt(payStr);
                    if (payVal < 0) {
                        throw new NumberFormatException();
                    }
                    pay = payVal;
                } else if (p.startsWith("status/")) {
                    status = valueAfterTag(p, "status/");
                    if (status.isEmpty()) {
                        throw InternityException.emptyField("status/");
                    }
                } else {
                    throw InternityException.unknownUpdateField(p);
                }
            }
        } catch (NumberFormatException e) {
            throw InternityException.invalidPayFormat();
        }

        if (company == null && role == null && deadline == null && pay == null && status == null) {
            throw InternityException.noUpdateFieldsProvided();
        }

        return new UpdateCommand(index, company, role, deadline, pay, status);
    }

    /**
     * Parses the arguments for List Command to create an {@link ListCommand} instance.
     *
     * @param args arguments for {@link ListCommand}
     * @return an instance of ListCommand constructed from the parsed arguments.
     *      Returns a default ListCommand if no arguments are provided.
     * @throws InternityException if the arguments are missing or invalid.
     */
    public static ListCommand parseListCommandArgs(String args) throws InternityException {
        if (args == null || args.isBlank()) {
            return new ListCommand(ListCommand.orderType.DEFAULT); // Default order
        }

        if (!args.startsWith("sort/")) {
            throw InternityException.invalidListCommand();
        }

        String[] splitArgs = args.split("\\s+sort/");
        if (splitArgs.length > 1) {
            throw InternityException.invalidListCommand();
        }

        String order = splitArgs[0].substring("sort/".length()).trim();
        if (order.equals("asc")) {
            return new ListCommand(ListCommand.orderType.ASCENDING);
        } else if (order.equals("desc")) {
            return new ListCommand(ListCommand.orderType.DESCENDING);
        } else {
            throw InternityException.invalidListCommand();
        }
    }

    private static String requireArgs(String args) throws InternityException {
        if (args == null || args.isBlank()) {
            throw InternityException.invalidUpdateFormat();
        }
        return args.trim();
    }

    private static String requireTagged(String tagged) throws InternityException {
        if (tagged.isBlank()) {
            throw InternityException.noUpdateFieldsProvided();
        }
        return tagged;
    }

    private static String[] splitIndexAndTagged(String trimmed) throws InternityException {
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace < 0) {
            throw InternityException.invalidUpdateFormat();
        }
        String indexToken = trimmed.substring(0, firstSpace).trim();
        String tagged = trimmed.substring(firstSpace + 1).trim();
        return new String[] { indexToken, tagged };
    }

    private static int parseOneBasedIndex(String indexToken) throws InternityException {
        try {
            return Integer.parseInt(indexToken) - 1;
        } catch (NumberFormatException e) {
            throw InternityException.invalidIndexForUpdate();
        }
    }

    private static String valueAfterTag(String token, String tag) {
        return token.substring(tag.length()).trim();
    }

    public static UsernameCommand parseUsernameCommandArgs(String args) throws InternityException {
        if (args == null || args.isBlank()) {
            throw InternityException.invalidUsernameCommand();
        }
        return new UsernameCommand(args);
    }
}
