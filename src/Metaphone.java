public class Metaphone {
    //                                             ABCDEFGHIJKLMNOPQRSTUVWXYZ
    private static final char[] DEFAULT_MAPPING = "vBKTvFKHvJKLMNvPKRSTvFW*YS".toCharArray();

    private static char map(char c) {
        return DEFAULT_MAPPING[c - 'A'];
    }

    private static int CODE_LENGTH = 6;

    public static String encode(final String string) {
        String word = string.toUpperCase();
        word = word.replaceAll("[^A-Z]", "");
        if (word.length() == 0) {
            return "";
        } else if (word.length() == 1) {
            return word;
        }
        word = word.replaceFirst("^[KGP]N", "N");
        word = word.replaceFirst("^WR", "R");
        word = word.replaceFirst("^AE", "E");
        word = word.replaceFirst("^PF", "F");
        word = word.replaceFirst("^WH", "W");
        word = word.replaceFirst("^X", "S");

        // Transform input string to all caps
        final char[] input = word.toCharArray();
        int code_index = 0;
        final char[] code = new char[CODE_LENGTH];

        // Save previous character of word
        char prev_c = '?';
        for (int i = 0; i < input.length && code_index < CODE_LENGTH; i++) {
            final char c = input[i];

            if (c == prev_c) {
                // Especial rule for double letters
                if (c == 'C') {
                    // We have "cc".  The first "c" has already been mapped
                    // to "K".
                    if (i < input.length - 1 && "EIY".indexOf(input[i + 1]) >= 0) {
                        // Do nothing and let it do to cc[eiy] -> KS
                    } else {
                        // This "cc" is just one sound
                        continue;
                    }
                } else {
                    // It is not "cc", so ignore the second letter
                    continue;
                }
            }
            switch (c) {

                case 'A':
                case 'E':
                case 'I':
                case 'O':
                case 'U':
                // Keep a vowel only if it is the first letter
                    if (i == 0) code[code_index++] = c;
                    break;

                case 'F':
                case 'J':
                case 'L':
                case 'M':
                case 'N':
                case 'R':
                    code[code_index++] = c;
                    break;

                case 'Q':
                case 'V':
                case 'Z':
                    code[code_index++] = map(c);
                    break;

                // B -> B   only if NOT  MB$
                case 'B':
                    if (!(i == input.length - 1 && code_index > 0 && code[code_index - 1] == 'M'))
                        code[code_index++] = c;
                    break;

                case 'C':
                    if (i < input.length - 2 && input[i + 1] == 'I' && input[i + 2] == 'A') code[code_index++] = 'X';
                    else if (i < input.length - 1 && input[i + 1] == 'H' && i > 0 && input[i - 1] != 'S')
                        code[code_index++] = 'X';
                    else if (i < input.length - 1 && "EIY".indexOf(input[i + 1]) >= 0) code[code_index++] = 'S';
                    else code[code_index++] = 'K';
                    break;

                case 'D':
                    if (i < input.length - 2 && input[i + 1] == 'G' && "EIY".indexOf(input[i + 2]) >= 0)
                        code[code_index++] = 'J';
                    else code[code_index++] = 'T';
                    break;

                case 'G':
                    if (i < input.length - 1 && input[i + 1] == 'N') ;  // GN -> N  [GNED -> NED]
                    else if (i > 0 && input[i - 1] == 'D' && i < input.length - 1 && "EIY".indexOf(input[i + 1]) >= 0);
                        // DG[IEY] -> D[IEY]
                    else if (i < input.length - 1 && input[i + 1] == 'H'
                            && (i + 2 == input.length || "AEIOU".indexOf(input[i + 2]) < 0)) ;
                    else if (i < input.length - 1 && "EIY".indexOf(input[i + 1]) >= 0) code[code_index++] = 'J';
                    else code[code_index++] = map(c);
                    break;

                case 'H':
                    if (i > 0 && "AEIOUCGPST".indexOf(input[i - 1]) >= 0) ; // vH -> v
                    else if (i < input.length - 1 && "AEIOU".indexOf(input[i + 1]) < 0) ; // Hc -> c
                    else code[code_index++] = c;
                    break;

                case 'K':
                    if (i > 0 && input[i - 1] == 'C') ; // CK -> K
                    else code[code_index++] = map(c);
                    break;

                case 'P':
                    if (i < input.length - 1 && input[i + 1] == 'H') code[code_index++] = 'F';
                    else code[code_index++] = map(c);
                    break;

                case 'S':
                    if (i < input.length - 2 && input[i + 1] == 'I' && (input[i + 2] == 'A' || input[i + 2] == 'O'))
                        code[code_index++] = 'X';
                    else if (i < input.length - 1 && input[i + 1] == 'H') code[code_index++] = 'X';
                    else code[code_index++] = 'S';
                    break;

                case 'T':
                    // -TI[AO]- -> -XI[AO]-
                    // -TCH- -> -CH-
                    // -TH- -> -0-
                    // -T- -> -T-
                    if (i < input.length - 2 && input[i + 1] == 'I' && (input[i + 2] == 'A' || input[i + 2] == 'O'))
                        code[code_index++] = 'X';
                    else if (i < input.length - 1 && input[i + 1] == 'H') code[code_index++] = '0';
                    else if (i < input.length - 2 && input[i + 1] == 'C' && input[i + 2] == 'H') ; // drop letter
                    else code[code_index++] = 'T';
                    break;

                case 'W':
                case 'Y':
                    // -Wv- -> -Wv-;  -Wc- -> -c-
                    // -Yv- -> -Yv-;  -Yc- -> -c-
                    if (i < input.length - 1 && "AEIOU".indexOf(input[i + 1]) >= 0) code[code_index++] = map(c);
                    break;

                case 'X':
                    // -X- -> -KS-
                    code[code_index++] = 'K';
                    if (code_index < code.length) code[code_index++] = 'S';
                    break;

                default:
                    assert (false);
            }
            prev_c = c;
        }
        return new String(code, 0, code_index);
    }
}