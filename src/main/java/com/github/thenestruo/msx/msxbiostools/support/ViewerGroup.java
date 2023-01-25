package com.github.thenestruo.msx.msxbiostools.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewerGroup implements Viewer {

	private final String key;

	private final String header;

	private final List<Viewer> delegates;

	public ViewerGroup(String key, Viewer ... delegates) {
		this(key, null, delegates);
	}

	public ViewerGroup(String key, String header, Viewer ... delegates) {
		super();

		this.key = key;
		this.header = header;
		this.delegates = Arrays.asList(delegates);
	}

	@Override
	public String getKey() {

		return this.key;
	}

	@Override
	public String getHeader() {

		if (this.header != null) {
			return this.header;
		}

		List<String> list = new ArrayList<>();
		for (Viewer delegate : this.delegates) {
			list.add(delegate.getHeader());
		}
		return String.join(", ", list);
	}

	@Override
	public boolean canView(byte[] bios) {

		for (Viewer delegate : this.delegates) {
			if (delegate.canView(bios)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getValue(byte[] bios) {

		List<String> list = new ArrayList<>();
		for (Viewer delegate : this.delegates) {
			list.add(delegate.canView(bios)
					? delegate.getValue(bios)
					: "-");
		}
		return String.join(", ", list);
	}
}
