package com.github.thenestruo.msx.msxbiostools;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;

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

	private static final String TSV = "tsv";

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

	public static void main(String[] args) throws Exception {

		// Parses command line
		final Options options = options();
		final CommandLine command = new DefaultParser().parse(options, args);

		// Builds input file list
		final List<File> inputFiles = new ArrayList<>();
		final Queue<File> queue = new LinkedList<>();
		for (String arg : command.getArgList()) {
			queue.add(new File(arg));
		}
		while (!queue.isEmpty()) {
			final File file = queue.poll();
			if (FileUtils.isDirectory(file)) {
				queue.addAll(FileUtils.listFiles(file,
						FileFilterUtils.trueFileFilter(),
						FileFilterUtils.falseFileFilter()));
			} else if (file.canRead()) {
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
			for (Viewer viewer : TSV_VIEWERS) {
				headers.add(viewer.getHeader());
			}
			final StringBuilder output = new StringBuilder();
			try (final CSVPrinter csvPrinter = new CSVPrinter(
					output,
					CSVFormat.Builder
							.create()
							.setDelimiter('\t')
							.setHeader(headers.toArray(new String[0]))
							.build())) {
				for (File inputFile : inputFiles) {
					csvPrinter.printRecord(new MsxBiosToolsApp(inputFile)
							.view(TSV_VIEWERS, true)
							.getProperties()
							.values());
				}
			}
			System.out.println(output);

		} else {
			// TEXT

			for (File inputFile : inputFiles) {
				for (Map.Entry<String, String> e :
						new MsxBiosToolsApp(inputFile)
							.view(TEXT_VIEWERS, false)
							.getProperties()
							.entrySet()) {
					System.out.printf("%s: %s%n", e.getKey(), e.getValue());
				}
				System.out.println();
			}
		}
	}

	private static Options options() {

		final Options options = new Options();
		options.addOption(TSV, "Outputs TSV (Tab separated values)");
		for (final Viewer viewer : TSV_VIEWERS) {
			if (viewer instanceof Patcher) {
				final Patcher patcher = (Patcher) viewer;
				options.addOption(patcher.getKey(), patcher.getHelp());
			}
		}
		return options;
	}

	/** The input filename */
	private final String inputFilename;

	private final Map<String, String> properties = new LinkedHashMap<>();

	public MsxBiosToolsApp(final File file) {
		super();

		this.inputFilename = file.getAbsolutePath();
		this.properties.put("Filename", FilenameUtils.getBaseName(this.inputFilename));
	}

	public MsxBiosToolsApp view(List<Viewer> viewers, boolean includeEmpty) throws IOException {

		final byte[] bios = new byte[0x8000];
		try (final InputStream is = IOUtils.buffer(new FileInputStream(this.inputFilename))) {
			IOUtils.readFully(is, bios, 0x0000, 0x8000);
		} catch (EOFException e) {
			return this;
		}

		for (Viewer viewer : viewers) {
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
