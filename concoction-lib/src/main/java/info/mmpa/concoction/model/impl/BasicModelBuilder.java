package info.mmpa.concoction.model.impl;

import info.mmpa.concoction.input.ClassesAndFiles;
import info.mmpa.concoction.input.InputReader;
import info.mmpa.concoction.input.archive.ArchiveLoadContext;
import info.mmpa.concoction.input.impl.PathInputReader;
import info.mmpa.concoction.input.impl.RawInputReader;
import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.InvalidModelException;
import info.mmpa.concoction.model.ModelBuilder;
import info.mmpa.concoction.model.ModelSource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basic model builder which delegates to input readers of the supported types.
 */
public class BasicModelBuilder implements ModelBuilder {
	private final List<ModelSource> sources = new ArrayList<>();
	private final InputReader<Path> pathReader = new PathInputReader();
	private final InputReader<byte[]> rawReader = new RawInputReader();

	@Nonnull
	@Override
	public ModelBuilder addSource(@Nonnull ArchiveLoadContext context, @Nonnull Path path) throws IOException {
		ClassesAndFiles input = pathReader.from(context,path);
		String identifier = path.getFileName().toString();
		sources.add(new BasicModelSource(identifier, input.getClasses(), input.getFiles()));
		return this;
	}

	@Nonnull
	@Override
	public ModelBuilder addSource(@Nonnull ArchiveLoadContext context,@Nonnull String identifier, @Nonnull byte[] raw) throws IOException {
		ClassesAndFiles input = rawReader.from(context,raw);
		sources.add(new BasicModelSource(identifier, input.getClasses(), input.getFiles()));
		return this;
	}

	@Nonnull
	@Override
	public ApplicationModel build() throws InvalidModelException {
		if (sources.isEmpty())
			throw new InvalidModelException("At least one source must be provided");
		ModelSource primarySource = sources.get(0);
		List<ModelSource> supportingSources = sources.size() > 1 ?
				sources.subList(1, sources.size()) :
				Collections.emptyList();
		return new BasicApplicationModel(primarySource, supportingSources);
	}
}
