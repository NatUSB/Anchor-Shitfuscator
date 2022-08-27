package wtf.krypton.obfuscator.util;

import java.util.Arrays;

public interface UniqueStringGenerator {

    void reset();

    String next();

    /**
     * Increments similar to a number
     * Last number that is < Z is incremented, all values after the incremented value are reset to 'A'.
     * If all values are Z then increase length
     */
    class Default implements UniqueStringGenerator {

        char[] chars = new char[0];

        @Override
        public void reset() {
            chars = new char[0];
        }

        @Override
        public String next() {
            for (int n = chars.length - 1; n >= 0; n--) {
                if (chars[n] < 'Z') {
                    chars[n]++;
                    return new String(chars);
                }
                chars[n] = 'A';
            }
            chars = new char[chars.length + 1];
            Arrays.fill(chars, 'A');
            return new String(chars);
        }

    }

}