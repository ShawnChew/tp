package seedu.address.model.event;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Event's start or end datetime in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidDateTime(String)}
 */
public class DateTime {

    public static final String RECOMMENDED_FORMAT = "dd/MM/yyyy";
    public static final String MESSAGE_CONSTRAINTS =
            "Dates should follow a valid format. Try " + RECOMMENDED_FORMAT;

    public static final String REGEX_YEAR = "([0-9]{4})";
    public static final String REGEX_MONTH_DIGITS = "(((0)?[0-9])|((1)[0-2]))";
    public static final String REGEX_MONTH_LETTERS =
            "((jan)|(feb)|(mar)|(apr)|(may)|(jun)|(jul)|(aug)|(sep)|(oct)|(nov)|(dec))";
    public static final String REGEX_MONTH = "(" + REGEX_MONTH_LETTERS + "|" + REGEX_MONTH_DIGITS + ")";
    public static final String REGEX_DAY = "([1-9]|[0-2][0-9]|(3)[0-1])";
    public static final String REGEX_DATE_DAY_SLASH =
            "(" + REGEX_DAY + "/" + REGEX_MONTH + "/" + REGEX_YEAR + ")|"
                    + "(" + REGEX_YEAR + "/" + REGEX_MONTH + "/" + REGEX_DAY + ")";
    public static final String REGEX_DATE_DAY_SPACE =
            "(" + REGEX_DAY + "\\s" + REGEX_MONTH + "\\s" + REGEX_YEAR + ")|"
                    + "(" + REGEX_YEAR + "\\s" + REGEX_MONTH + "\\s" + REGEX_DAY + ")";
    public static final String REGEX_DATE_DAY_DASH =
            "(" + REGEX_DAY + "-" + REGEX_MONTH + "-" + REGEX_YEAR + ")|"
                    + "(" + REGEX_YEAR + "-" + REGEX_MONTH + "-" + REGEX_DAY + ")";
    public static final String REGEX_DATE =
            REGEX_DATE_DAY_SLASH + "|" + REGEX_DATE_DAY_SPACE + "|" + REGEX_DATE_DAY_DASH;
    public static final String REGEX_SECONDS = "[0-5]?[0-9]";
    public static final String REGEX_MINUTES = "([0-5][0-9])";
    public static final String REGEX_HOURS = "([01][0-9]|2[0-3])";
    public static final String REGEX_TIME_COLON =
            "(" + REGEX_HOURS + ":" + REGEX_MINUTES + "(:" + REGEX_SECONDS + ")?)";
    public static final String REGEX_TIME_NO_SPACE =
            "(" + REGEX_HOURS + REGEX_MINUTES + "(" + REGEX_SECONDS + ")?)";
    public static final String REGEX_TIME = REGEX_TIME_COLON + "|" + REGEX_TIME_NO_SPACE;

    public static final Pattern VALIDATION_PATTERN = Pattern.compile(
            "(?<dateGroup>" + REGEX_DATE + ")(\\s(?<timeGroup>" + REGEX_TIME + "))?"
    );

    // Not an exhaustive list
    private static final Map<String, String> DATE_VALIDATION_REGEXS = new HashMap<>() {{
            put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
            put("^\\d{1,2}-\\d{1,2}-\\d{2}$", "dd-MM-yy");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
            put("^\\d{1,2}/\\d{1,2}/\\d{2}$", "dd/MM/yy");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        }};

    private static final Map<String, String> TIME_REGEXES = new HashMap<>() {{
            put("^\\d{1,2}:\\d{2}$", "HH:mm");
            put("^\\d{1,2}:\\d{2}:\\d{2}$", "HH:mm:ss");
            put("^\\d{1,2}\\d{2}$", "HHmm");
            put("^\\d{1,2}\\d{2}\\d{2}$", "HHmmss");
        }};

    public final LocalDate date;
    public final Optional<LocalTime> time;

    /**
     * Constructs a {@code DateTime}.
     *
     * @param dateTime A valid datetime string.
     */
    public DateTime(String dateTime) {
        requireNonNull(dateTime);
        checkArgument(isValidDateTime(dateTime), MESSAGE_CONSTRAINTS);
        this.date = parseDate(dateTime);
        this.time = parseTime(dateTime);
    }

    /**
     * Returns true if given string follows a valid datetime pattern.
     */
    public static String determineDateFormat(String dateString, Map<String, String> regexSet) {
        for (String regexp : regexSet.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return regexSet.get(regexp);
            }
        }
        return null;
    }

    /**
     * Returns true if an input string satisfies the required format and time
     * period, false otherwise.
     */
    public static boolean isValidDateTime(String dateString) {
        Matcher matcher = VALIDATION_PATTERN.matcher(dateString.toLowerCase());
        return matcher.matches();
    }

    // TODO
    public static boolean isValidValues(String dateString) {
        return false;
    }

    /**
     * Returns a LocalDate from a given date time string.
     */
    public static LocalDate parseDate(String dateString) {
        Matcher matcher = VALIDATION_PATTERN.matcher(dateString.toLowerCase());
        matcher.matches();
        String date = matcher.group("dateGroup");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(
                requireNonNull(determineDateFormat(date, DATE_VALIDATION_REGEXS)));
        return LocalDate.parse(date, dateFormat);
    }

    /**
     * Returns an Optional containing a LocalTime object if a
     * time is present in a given date time string. Otherwise,
     * return a empty Optional.
     */
    public static Optional<LocalTime> parseTime(String dateString) {
        Matcher matcher = VALIDATION_PATTERN.matcher(dateString.toLowerCase());
        matcher.matches();
        String time = matcher.group("timeGroup");
        if (time == null) {
            return Optional.empty();
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(
                requireNonNull(determineDateFormat(time, TIME_REGEXES)));
        return Optional.ofNullable(LocalTime.parse(time, dateFormat));
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofPattern(RECOMMENDED_FORMAT))
                + time.map(t -> " " + t).orElse("");
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DateTime // instanceof handles nulls
                && date.equals(((DateTime) other).date) // state check
                && time.equals(((DateTime) other).time)); // state check
    }

    @Override
    public int hashCode() {
        return date.hashCode() ^ time.hashCode();
    }

}
