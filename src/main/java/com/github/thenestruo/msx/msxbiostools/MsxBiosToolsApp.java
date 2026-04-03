package com.github.thenestruo.msx.msxbiostools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.tinylog.Logger;

import com.github.thenestruo.commons.Strings;
import com.github.thenestruo.commons.io.Paths;
import com.github.thenestruo.msx.msxbiostools.MsxBiosToolsApp.ViewText;
import com.github.thenestruo.msx.msxbiostools.MsxBiosToolsApp.ViewTsv;
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

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "msxbiostool", sortOptions = false, subcommands = { ViewText.class, ViewTsv.class }) // , Patch.class })
public class MsxBiosToolsApp implements Callable<Integer> {

	public static void main(final String... args) {
		System.exit(new CommandLine(new MsxBiosToolsApp()).execute(args));
	}

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "shows usage")
	private boolean help;

	@Override
	public Integer call() throws Exception {
		return 0;
	}

	@Command(name = "view", sortOptions = false, description = "view MSX BIOS information as plain text")
	public static class ViewText implements Callable<Integer> {

		private static final List<Viewer> VIEWERS = Arrays.asList(
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
						BorderColor.INSTANCE));

		@Parameters(paramLabel = "input", description = "input MSX BIOS file or directory")
		private List<Path> inputPaths;

		@Override
		public Integer call() throws Exception {

			// Builds input file list
			final List<Path> actualInputPaths = new ArrayList<>();
			for (final Queue<Path> queue = new LinkedList<>(this.inputPaths); !queue.isEmpty();) {
				final Path inputPath = queue.poll();
				if (Files.isDirectory(inputPath)) {
					Files.list(inputPath).filter(Predicate.not(Files::isDirectory)).forEach(queue::add);
				} else if (Files.isReadable(inputPath)) {
					actualInputPaths.add(inputPath);
				}
			}
			if (actualInputPaths.isEmpty()) {
				return 10;
			}

			// Visualizes the files
			for (final Path inputPath : actualInputPaths) {
				this.logValuesOf(inputPath);
				Logger.info("");
			}

			return 0;
		}

		public void logValuesOf(final Path inputPath) throws IOException {

			final byte[] bios;
			try (final InputStream is = Files.newInputStream(inputPath)) {
				bios = is.readNBytes(0x8000);
			}

			for (final Viewer viewer : VIEWERS) {
				if (viewer.canView(bios)) {
					Logger.info("{}: {}", viewer.getKey(), viewer.getValue(bios));
				}
			}
		}
	}

	@Command(name = "tsv", sortOptions = false, description = "view MSX BIOS information as TSV")
	public static class ViewTsv implements Callable<Integer> {

		private static final List<Viewer> VIEWERS = Arrays.asList(
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
				Msx1HasSlotfix.INSTANCE);

		@Parameters(paramLabel = "input", description = "input MSX BIOS file(s)")
		private List<Path> inputPaths;

		@Override
		public Integer call() throws Exception {

			// Builds input file list
			final List<Path> actualInputPaths = new ArrayList<>();
			for (final Queue<Path> queue = new LinkedList<>(this.inputPaths); !queue.isEmpty();) {
				final Path inputPath = queue.poll();
				if (Files.isDirectory(inputPath)) {
					Files.list(inputPath).filter(Predicate.not(Files::isDirectory)).forEach(queue::add);
				} else if (Files.isReadable(inputPath)) {
					actualInputPaths.add(inputPath);
				}
			}
			if (actualInputPaths.isEmpty()) {
				return 10;
			}

			// Visualizes the files
			final List<String> headers = new ArrayList<>();
			headers.add("Filename");
			for (final Viewer viewer : VIEWERS) {
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
				for (final Path inputPath : actualInputPaths) {
					csvPrinter.printRecord(this.recordOfValuesOf(inputPath));
				}
			}
			Logger.info(output);

			return 0;
		}

		private List<String> recordOfValuesOf(final Path inputPath)
				throws IOException {

			final byte[] bios;
			try (final InputStream is = Files.newInputStream(inputPath)) {
				bios = is.readNBytes(0x8000);
			}

			final List<String> values = new ArrayList<>();
			for (final Viewer viewer : VIEWERS) {
				final String value = viewer.canView(bios) ? viewer.getValue(bios) : null;
				values.add(Strings.isBlank(value) ? "-" : value);
			}
			return values;
		}
	}

	@Command(name = "patch", sortOptions = false, description = "Patch")
	public static class Patch implements Callable<Integer> {

		private static final List<Patcher> PATCHERS = Arrays.asList(
//				Crc32.INSTANCE,
//				MsxVersion.INSTANCE,
				Frequency.INSTANCE,
//				CountryBasicVersion.INSTANCE,
//				CountryKeyboardType.INSTANCE,
//				CountryDateFormat.INSTANCE,
//				CountryCharacterSet.INSTANCE,
//				SystemFontAddress.INSTANCE,
//				SystemFont.INSTANCE,
				KeyboardScanAndRepeat.INSTANCE,
				Delay.INSTANCE,
				ScreenMode.INSTANCE,
				Screen0Width.INSTANCE,
				BorderColor.INSTANCE
//				Msx1HasNdevfix.INSTANCE,
//				Msx1HasSlotfix.INSTANCE
		);

		@Parameters(index = "0", arity = "1", paramLabel = "input", description = "input MSX BIOS file")
		private Path inputPath;

		@Parameters(index = "1",
				arity = "0..1",
				paramLabel = "output",
				description = "output patched MSX BIOS file (optional, defaults to <input>-patched)")
		private Path outputPath;

		@Override
		public Integer call() throws Exception {

			final byte[] bios;
			try (final InputStream is = Files.newInputStream(this.inputPath)) {
				bios = is.readNBytes(0x8000);
			}

//			final Map<Patcher, String> values = patchers
//					.stream()
//					.filter(p -> command.hasOption(p.getKey()))
//					.map(p -> Pair.of(p, command.getOptionValue(p.getKey())))
//					.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
//
//			Logger.info("values = {}", values);
//
//			new MsxBiosToolsApp(inputFile)
//					.patch(values);

			throw new UnsupportedOperationException("Unimplemented method 'patch'");
		}

		private Path outputPath(final String suffix) {

			return this.outputPath != null ? this.outputPath : Paths.append(this.inputPath, suffix);
		}
	}
}
