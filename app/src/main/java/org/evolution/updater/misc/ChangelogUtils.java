package org.evolution.updater.misc;

import android.text.TextUtils;

public final class ChangelogUtils {

    private ChangelogUtils() {
    }

    public static boolean isUrl(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        String trimmed = value.trim();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }

    public static String buildStyledChangelog(String raw) {
        return getFormattedChangelog(raw);
    }

    public static String buildPreview(String raw) {
        return getPreview(raw, 8);
    }

    public static String getFormattedChangelog(String raw) {
        if (TextUtils.isEmpty(raw)) {
            return "";
        }

        String normalized = raw
                .replace("\r\n", "\n")
                .replace('\r', '\n');

        String[] lines = normalized.split("\n");
        StringBuilder out = new StringBuilder();
        boolean previousWasEmpty = true;

        for (String line : lines) {
            String trimmed = line == null ? "" : line.trim();

            if (trimmed.isEmpty()) {
                if (!previousWasEmpty) {
                    out.append('\n');
                    previousWasEmpty = true;
                }
                continue;
            }

            if (isHeading(trimmed)) {
                appendSectionSpacing(out);
                out.append(cleanHeading(trimmed)).append('\n');
                previousWasEmpty = false;
                continue;
            }

            if (isBullet(trimmed)) {
                out.append("• ").append(cleanBullet(trimmed)).append('\n');
                previousWasEmpty = false;
                continue;
            }

            out.append(trimmed).append('\n');
            previousWasEmpty = false;
        }

        return out.toString().trim();
    }

    public static String getPreview(String raw, int maxLines) {
        if (TextUtils.isEmpty(raw)) {
            return "";
        }

        String formatted = getFormattedChangelog(raw);
        if (TextUtils.isEmpty(formatted) || maxLines <= 0) {
            return formatted;
        }

        String[] lines = formatted.split("\n");
        StringBuilder out = new StringBuilder();
        int added = 0;

        for (String line : lines) {
            if (added >= maxLines) {
                break;
            }
            out.append(line).append('\n');
            added++;
        }

        if (lines.length > maxLines) {
            out.append("…");
        }

        return out.toString().trim();
    }

    private static boolean isHeading(String line) {
        return line.startsWith("#")
                || line.endsWith(":")
                || line.matches("^[A-Z][A-Za-z0-9 /+&_\\-]+:$");
    }

    private static boolean isBullet(String line) {
        return line.startsWith("- ")
                || line.startsWith("* ")
                || line.startsWith("• ")
                || line.startsWith("•");
    }

    private static String cleanHeading(String line) {
        String cleaned = line.replaceFirst("^#+\\s*", "").trim();
        if (cleaned.endsWith(":")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
        }
        return cleaned;
    }

    private static String cleanBullet(String line) {
        return line.replaceFirst("^[-*•]\\s*", "").trim();
    }

    private static void appendSectionSpacing(StringBuilder out) {
        if (out.length() == 0) {
            return;
        }

        if (out.charAt(out.length() - 1) != '\n') {
            out.append('\n');
        }

        if (out.length() < 2 || out.charAt(out.length() - 2) != '\n') {
            out.append('\n');
        }
    }
}
