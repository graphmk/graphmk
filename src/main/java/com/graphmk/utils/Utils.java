package com.graphmk.utils;

import com.graphmk.mkcore.interfaces.Vertex;

import java.io.*;
import java.text.Normalizer;

public class Utils {

    public static void flushToFile(final File file, final String content) {
        try {
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            final FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            final BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content + "\n");
            bw.close();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String printGMLVertex(final Vertex v) {
        final String delim = " ";
        String props = "";
        props = props + "id" + delim + toASCII(escStr(v.getId())) + delim;
        for (final String o : v.getPropertyKeys()) {
            props = props + delim + o.toString() + delim + toASCII(escStr(v.getProperty(o))) + delim;
        }
        final String toRet = " node [" + props + "]";
        return toRet;
    }

    private static Object escStr(final Object s) {
        if (s instanceof String) {
            return "\"" + s + "\"";
        }
        return s;
    }

    public static String printGMLEdge(final Vertex source, final Vertex dest) {
        final String delim = " ";
        String props = "";
        props = props + delim + "source" + delim + toASCII(escStr(source.getId())) + delim + "target" + delim + toASCII(escStr(dest.getId())) + delim;
        final String toRet = " edge [" + props + "]";
        return toRet;
    }

    public static Object toASCII(final Object s) {
        if (s instanceof String) {
            final String s2 = Normalizer.normalize((CharSequence)s, Normalizer.Form.NFKD);
            final String regex = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
            String s3 = "encode_error";
            try {
                s3 = new String(s2.replaceAll(regex, "").getBytes("ascii"), "ascii");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return s3;
        }
        return s;
    }

}
