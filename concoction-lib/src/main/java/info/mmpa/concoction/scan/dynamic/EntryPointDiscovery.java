package info.mmpa.concoction.scan.dynamic;

import info.mmpa.concoction.input.model.ApplicationModel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A function that maps a {@link ApplicationModel} to entry points for initial dynamic analysis.
 * The list does not have to be complete such that execution of all entry-points covers the whole
 * application's declared logic. These are just what should be scanned <i>first</i>.
 *
 * @see CoverageEntryPointSupplier Handles creating entry points to cover application logic not visited by these entry points.
 */
public interface EntryPointDiscovery {
	/**
	 * @param model
	 * 		Model of the application to scan.
	 * @param context
	 * 		VM context with the application loaded in it.
	 *
	 * @return List of entry points to begin execution with inside the VM for analysis.
	 */
	@Nonnull
	List<EntryPoint> createEntryPoints(@Nonnull ApplicationModel model, @Nonnull SsvmContext context);
}
