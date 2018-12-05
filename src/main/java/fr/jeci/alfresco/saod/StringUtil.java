package fr.jeci.alfresco.saod;

import java.text.DecimalFormat;

import org.springframework.context.MessageSource;

public class StringUtil {
	final static String[] units = new String[] { "B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String readableFileSize(double size) {
		if (size <= 0) {
			return "0";
		}
		if (size > Math.pow(2, 89)) {
			return "gt 512 YB !";
		}
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String format(MessageSource source, String key, Object... args) {
		final String msg = source.getMessage(key, null, null);
		return String.format(msg, args);
	}
}
