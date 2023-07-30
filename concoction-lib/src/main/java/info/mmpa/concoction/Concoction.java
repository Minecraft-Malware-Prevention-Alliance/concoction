package info.mmpa.concoction;

import info.mmpa.concoction.model.ModelSource;
import info.mmpa.concoction.model.impl.BasicApplicationModel;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;
import info.mmpa.concoction.scan.standard.StandardScan;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main API entrypoint for concoction.
 * This is a builder to get a {@link Results} object.
 * Currently, this only implements static scanning as dynamic scanning is not hardened yet.
 */
public class Concoction {
    private final List<Path> modelPaths = new ArrayList<>();

    private final List<ModelSource> supportingSources = new ArrayList<>();

    private ModelSource primarySource;

    private Concoction() {}

    /**
     * The initialization point of the builder
     * @return A Concoction builder
     */
    public static Concoction builder() {
        return new Concoction();
    }

    /**
     * Adds a model path to the current builder
     * @param path The path of the JSON model to add.
     * @return Self
     */
    public Concoction model(Path path) {
        modelPaths.add(path);
        return this;
    }

    /**
     * Sets the primary source for this current builder
     * @param primarySource The primary source to set.
     * @return Self
     */
    public Concoction primarySource(ModelSource primarySource) {
        this.primarySource = primarySource;
        return this;
    }

    /**
     * Adds supporting sources for the {@link Concoction#primarySource}
     * @param sources The supporting sources to add
     * @return Self
     */
    public Concoction supportingSources(ModelSource... sources) {
        supportingSources.addAll(Arrays.asList(sources));
        return this;
    }

    /**
     * Scans the given application model with the models given.
     * @return The results of the scan
     * @throws IOException When a model couldn't be found.
     */
    public Results scan() throws IOException {
        // TODO: Implement dynamic scanning
        BasicApplicationModel applicationModel = new BasicApplicationModel(primarySource, supportingSources);

        List<InstructionsMatchingModel> matchingModels = new ArrayList<>();

        for (Path path : modelPaths) {
            matchingModels.add(InstructionsMatchingModel.fromJson(path));
        }

        StandardScan scan = new StandardScan(matchingModels);
        return scan.accept(applicationModel);
    }

}
