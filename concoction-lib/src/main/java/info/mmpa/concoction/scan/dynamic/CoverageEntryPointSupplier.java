package info.mmpa.concoction.scan.dynamic;

import info.mmpa.concoction.input.model.ApplicationModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Supplier of entry points to cover logic defined in a {@link ApplicationModel} that has not been covered
 * by the invocation of entry points defined by a {@link EntryPointDiscovery}.
 *
 * @see EntryPointDiscovery Handles primary entry points for known cases.
 */
public interface CoverageEntryPointSupplier {
	/**
	 * @param model
	 * 		Model of the application to scan.
	 * @param context
	 * 		VM context with the application loaded in it.
	 *
	 * @return The next entry point to begin analysis with, or {@code null} if analysis is complete.
	 */
	@Nullable
	EntryPoint nextEntryPoint(@Nonnull ApplicationModel model, @Nonnull SsvmContext context);
}
