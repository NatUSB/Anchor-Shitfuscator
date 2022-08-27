package wtf.krypton.obfuscator.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@UtilityClass
public class ManifestUtil {

    public String getMainClass(String jarIn) {
        ZipFile jar = null;

        try {
            jar = new ZipFile(jarIn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert jar != null;
        Enumeration<? extends ZipEntry> entries = jar.entries();

        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if(entry.getName().equals("META-INF/MANIFEST.MF")) {
                InputStream stream = null;
                try {
                    stream = jar.getInputStream(entry);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert stream != null;
                InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);

                BufferedReader br = new BufferedReader(isr);

                for(String line : br.lines().collect(Collectors.toList())) {
                    if(line.startsWith("Main-Class: ")) {
                        return line.substring(12);
                    }
                }
            }
        }

        return "";
    }

}
