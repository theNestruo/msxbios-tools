package com.github.thenestruo.msx.msxbiostools.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewerGroup implements Viewer {

	private final String description;

	private final List<Viewer> delegates;

	public ViewerGroup(Viewer ... delegates) {
		this(null, delegates);
	}

	public ViewerGroup(String description, Viewer ... delegates) {
		super();

		this.description = description;
		this.delegates = Arrays.asList(delegates);
	}

	@Override
	public String getDescription() {

		if (this.description != null) {
			return this.description;
		}

		List<String> list = new ArrayList<>();
		for (Viewer delegate : this.delegates) {
			list.add(delegate.getDescription());
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
