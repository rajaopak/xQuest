package id.rajaopak.xquest.util;

import id.rajaopak.xquest.XQuest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");
    private static final Pattern START_WITH_COLORS = Pattern.compile("(?i)^(" + net.md_5.bungee.api.ChatColor.COLOR_CHAR + "[0-9A-FK-ORX])+");
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\\\n|\\{nl}");
    private static final Pattern COLOR = Pattern.compile("(?i)" + ChatColor.COLOR_CHAR + "[0-9A-FK-ORX]");

    private static final String nmsVersion;

    static {
        nmsVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
    }

    public static String colors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString());
        }

        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static List<String> colors(List<String> text) {
        return text.stream().map(Utils::colors).collect(Collectors.toList());
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(colors(msg));
    }

    public static void sendMessage(CommandSender sender, List<String> msg) {
        for (String text : colors(msg)) {
            sender.sendMessage(text);
        }
    }

    public static void info(String msg) {
        XQuest.getInstance().getLogger().info(colors(msg));
    }

    public static void logSevere(String msg) {
        XQuest.getInstance().getLogger().severe(msg);
    }

    public static void logSevere(String msg, Throwable throwable) {
        XQuest.getInstance().getLogger().log(Level.SEVERE, msg, throwable);
    }

    public static List<String> wordWrap(@Nullable String rawString, int lineLength) {
        // A null string is a single line
        if (rawString == null) {
            return List.of("");
        }

        // A string shorter than the lineWidth is a single line
        if (rawString.length() <= lineLength && !rawString.contains("\n")) {
            return List.of(rawString);
        }

        char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        int lineColorChars = 0;
        String lastColor = "";

        for (int i = 0; i < rawChars.length; i++) {
            char c = rawChars[i];

            // skip chat color modifiers
            if (c == ChatColor.COLOR_CHAR || c == '&') {
                word.append(ChatColor.getByChar(rawChars[i + 1]));
                lineColorChars += 2;
                lastColor = ChatColor.getByChar(rawChars[i + 1]).toString();
                i++; // Eat the next character as we have already processed it
                continue;
            }

            if (c == ' ' || c == '\n') {
                String[] split = word.toString().split("(?<=\\G.{" + lineLength + "})");
                if (line.length() == 0 && word.length() > lineLength) { // special case: extremely long word begins a line
                    lines.addAll(Arrays.asList(split));
                } else if (line.length() + 1 + word.length() - lineColorChars == lineLength) { // Line exactly the correct length...newline
                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                    lines.add(lastColor + line);
                    line = new StringBuilder();
                    lineColorChars = 0;
                } else if (line.length() + 1 + word.length() - lineColorChars > lineLength) { // Line too long...break the line
                    for (String partialWord : split) {
                        lines.add(lastColor + line);
                        line = new StringBuilder(partialWord);
                    }
                    lineColorChars = 0;
                } else {
                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                }
                word = new StringBuilder();

                if (c == '\n') { // Newline forces the line to flush
                    lines.add(lastColor + line);
                    line = new StringBuilder();
                }
            } else {
                word.append(c);
            }
        }

        if (line.length() > 0) {
            lines.add(lastColor + line);
        }

        return lines;
    }

    public static String getNmsVersion() {
        return nmsVersion;
    }
}
