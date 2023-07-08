package info.mmpa.concoction.scan.dynamic;

import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.output.Results;

import javax.annotation.Nonnull;

/**
 * Advanced behavioral pattern matching based scanning.
 */
public interface DynamicScan {
	// TODO: Need to flesh out how this system will be laid out
	//  - some discussion here: https://github.com/Minecraft-Malware-Prevention-Alliance/concoction/issues/1
	Results accept(@Nonnull ApplicationModel model);
}
