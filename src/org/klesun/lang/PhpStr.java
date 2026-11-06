package org.klesun.lang;

/** PHP string escaping rules: unknown sequences stay as is, unlike java */
public class PhpStr
{
    /** @see "https://www.php.net/manual/en/language.types.string.php#language.types.string.syntax.double" */
    public static String unescape(String str)
    {
        StringBuilder out = new StringBuilder(str.length());
        int i = 0;
        while (i < str.length()) {
            char ch = str.charAt(i);
            if (ch != '\\' || i + 1 >= str.length()) {
                out.append(ch);
                i++;
                continue;
            }
            char next = str.charAt(i + 1);
            int simple = "nrtvef\\$\"".indexOf(next);
            if (simple > -1) {
                out.append("\n\r\t\u000B\u001B\f\\$\"".charAt(simple));
                i += 2;
            } else if (next >= '0' && next <= '7') {
                int end = i + 1;
                while (end < str.length() && end < i + 4 && str.charAt(end) >= '0' && str.charAt(end) <= '7') {
                    end++;
                }
                out.append((char)(Integer.parseInt(str.substring(i + 1, end), 8) & 0xFF));
                i = end;
            } else if (next == 'x' && i + 2 < str.length() && Character.digit(str.charAt(i + 2), 16) > -1) {
                int end = i + 2;
                while (end < str.length() && end < i + 4 && Character.digit(str.charAt(end), 16) > -1) {
                    end++;
                }
                out.append((char)Integer.parseInt(str.substring(i + 2, end), 16));
                i = end;
            } else if (next == 'u' && i + 2 < str.length() && str.charAt(i + 2) == '{') {
                int close = str.indexOf('}', i + 3);
                int codePoint = close > -1 ? parseCodePoint(str.substring(i + 3, close)) : -1;
                if (codePoint > -1) {
                    out.appendCodePoint(codePoint);
                    i = close + 1;
                } else {
                    out.append(ch);
                    i++;
                }
            } else {
                out.append(ch);
                i++;
            }
        }
        return out.toString();
    }

    private static int parseCodePoint(String hex)
    {
        try {
            int codePoint = Integer.parseInt(hex, 16);
            return Character.isValidCodePoint(codePoint) ? codePoint : -1;
        } catch (NumberFormatException exc) {
            return -1;
        }
    }

    /** for a single-quoted js string literal */
    public static String escapeJs(String str)
    {
        StringBuilder out = new StringBuilder(str.length());
        for (char ch : str.toCharArray()) {
            switch (ch) {
                case '\\': out.append("\\\\"); break;
                case '\'': out.append("\\'"); break;
                case '"': out.append("\\\""); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                case ' ': out.append("\\u2028"); break;
                case ' ': out.append("\\u2029"); break;
                default:
                    if (ch < 0x20) {
                        out.append(String.format("\\u%04x", (int)ch));
                    } else {
                        out.append(ch);
                    }
            }
        }
        return out.toString();
    }
}
