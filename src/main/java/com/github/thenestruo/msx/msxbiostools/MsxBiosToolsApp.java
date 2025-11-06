package com.github.thenestruo.msx.msxbiostools;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.tinylog.Logger;

import com.github.thenestruo.msx.msxbiostools.fields.BorderColor;
import com.github.thenestruo.msx.msxbiostools.fields.CountryBasicVersion;
import com.github.thenestruo.msx.msxbiostools.fields.CountryCharacterSet;
import com.github.thenestruo.msx.msxbiostools.fields.CountryDateFormat;
import com.github.thenestruo.msx.msxbiostools.fields.CountryKeyboardType;
import com.github.thenestruo.msx.msxbiostools.fields.Crc32;
import com.github.thenestruo.msx.msxbiostools.fields.Delay;
import com.github.thenestruo.msx.msxbiostools.fields.Frequency;
import com.github.thenestruo.msx.msxbiostools.fields.KeyboardScanAndRepeat;
import com.github.thenestruo.msx.msxbiostools.fields.Msx1HasNdevfix;
import com.github.thenestruo.msx.msxbiostools.fields.Msx1HasSlotfix;
import com.github.thenestruo.msx.msxbiostools.fields.MsxVersion;
import com.github.thenestruo.msx.msxbiostools.fields.Screen0Width;
import com.github.thenestruo.msx.msxbiostools.fields.ScreenMode;
import com.github.thenestruo.msx.msxbiostools.fields.SystemFont;
import com.github.thenestruo.msx.msxbiostools.fields.SystemFontAddress;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.support.Viewer;
import com.github.thenestruo.msx.msxbiostools.support.ViewerGroup;

public class MsxBiosToolsApp {

	private static final String HELP = "help";

	private static final String TSV = "tsv";
	private static final String PATCH = "patch";

	private static final List<Viewer> TEXT_VIEWERS = Arrays.asList(
			Crc32.INSTANCE,
			MsxVersion.INSTANCE,
			new ViewerGroup(
				"fixes", "Has SLOTFIX and NDEVFIX?",
				Msx1HasSlotfix.INSTANCE,
				Msx1HasNdevfix.INSTANCE),
			new ViewerGroup(
				"country",
				CountryKeyboardType.INSTANCE,
				CountryBasicVersion.INSTANCE,
				CountryCharacterSet.INSTANCE,
				CountryDateFormat.INSTANCE),
			new ViewerGroup(
				"font",
				SystemFontAddress.INSTANCE,
				SystemFont.INSTANCE),
			Frequency.INSTANCE,
			KeyboardScanAndRepeat.INSTANCE,
			Delay.INSTANCE,
			new ViewerGroup(
				"screen",
				ScreenMode.INSTANCE,
				Screen0Width.INSTANCE,
				BorderColor.INSTANCE)
		);

	private static final List<Viewer> TSV_VIEWERS = Arrays.asList(
			Crc32.INSTANCE,
			MsxVersion.INSTANCE,
			Frequency.INSTANCE,
			CountryBasicVersion.INSTANCE,
			CountryKeyboardType.INSTANCE,
			CountryDateFormat.INSTANCE,
			CountryCharacterSet.INSTANCE,
			SystemFontAddress.INSTANCE,
			SystemFont.INSTANCE,
			KeyboardScanAndRepeat.INSTANCE,
			Delay.INSTANCE,
			ScreenMode.INSTANCE,
			Screen0Width.INSTANCE,
			BorderColor.INSTANCE,
			Msx1HasNdevfix.INSTANCE,
			Msx1HasSlotfix.INSTANCE
		);

	public static void main(final String[] args) throws Exception {

		// Parses the command line
		final Options options = options(true);
		final CommandLine command;
		try {
			command = new DefaultParser().parse(options, args);
		} catch (final MissingOptionException e) {
			showUsage(false);
			return;
		}

		// Main options
		if (showUsage(command)) {
			return;
		}

		// Builds input file list
		final Queue<Path> queue = new LinkedList<>();
		for (final String arg : command.getArgList()) {
			queue.add(Path.of(arg));
		}
		final List<Path> inputFiles = new ArrayList<>();
		while (!queue.isEmpty()) {
			final Path file = queue.poll();
			if (Files.isDirectory(file)) {
				Files.list(file)
						.filter(Predicate.not(Files::isDirectory))
						.forEach((f) -> queue.add(f));
			} else if (Files.isReadable(file)) {
				inputFiles.add(file);
			}
		}

		// (sanity check)
		if (inputFiles.isEmpty()) {
			return;
		}

		if (command.hasOption(TSV)) {
			// TSV

			final List<String> headers = new ArrayList<>();
			headers.add("Filename");
			for (final Viewer viewer : TSV_VIEWERS) {
				headers.add(viewer.getHeader());
			}
			final StringBuilder output = new StringBuilder();
			try (final CSVPrinter csvPrinter = new CSVPrinter(
					output,
					CSVFormat.Builder
							.create()
							.setDelimiter('\t')
							.setHeader(headers.toArray(new String[0]))
							.get())) {
				for (final Path inputFile : inputFiles) {
					csvPrinter.printRecord(new MsxBiosToolsApp(inputFile)
							.view(TSV_VIEWERS, true)
							.getProperties()
							.values());
				}
			}
			Logger.info(output);

		} else {
			// TEXT

			for (final Path inputFile : inputFiles) {
				for (final Map.Entry<String, String> e
						: new MsxBiosToolsApp(inputFile)
							.view(TEXT_VIEWERS, false)
							.getProperties()
							.entrySet()) {
					Logger.info("{}: {}", e.getKey(), e.getValue());
				}
				Logger.info("");
			}
		}
	}

	private static Options options(final boolean includePatchOptions) {

		final Options options = new Options();
		options.addOption(HELP, "Shows usage");
		options.addOption(TSV, "Outputs TSV (Tab separated values) (view mode only)");
		options.addOption(PATCH, "Enables patch mode");

		if (includePatchOptions) {
			for (final Viewer viewer : TSV_VIEWERS) {
				if (viewer instanceof Patcher) {
					final Patcher patcher = (Patcher) viewer;
					options.addOption(patcher.getKey(), patcher.getPatchHelp() + " (patch mode only)");
				}
			}
		}

		return options;
	}

	private static boolean showUsage(final CommandLine command) throws IOException  {

		return command.hasOption(HELP)
				? showUsage(command.hasOption(PATCH))
				: false;
	}

	private static boolean showUsage(final boolean includePatchOptions) throws IOException {

		// (prints in proper order)
		HelpFormatter.builder()
				.setShowSince(false)
				.get().printHelp(
					"java -jar msxbiostools.jar <input MSX BIOS file>",
					null,
					options(includePatchOptions).getOptions(),
					null,
					true);

		return true;
	}

	/** The input filename */
	private final String inputFilename;

	private final Map<String, String> properties = new LinkedHashMap<>();

	public MsxBiosToolsApp(final Path file) {
		super();

		this.inputFilename = file.toAbsolutePath().toString();
		this.properties.put("Filename", FilenameUtils.getBaseName(this.inputFilename));
	}

	public MsxBiosToolsApp view(final List<Viewer> viewers, final boolean includeEmpty) throws IOException {

		final byte[] bios = new byte[0x8000];
		try (final InputStream is = Files.newInputStream(Path.of(this.inputFilename))) {
			IOUtils.readFully(is, bios, 0x0000, 0x8000);
		} catch (final EOFException e) {
			return this;
		}

		for (final Viewer viewer : viewers) {
			if (viewer.canView(bios) || includeEmpty) {
				this.properties.put(
						viewer.getKey(),
						StringUtils.defaultIfBlank(viewer.getValue(bios), "-"));
			}
		}

		return this;
	}

	public Map<String, String> getProperties() {

		return new LinkedHashMap<>(this.properties);
	}
}
