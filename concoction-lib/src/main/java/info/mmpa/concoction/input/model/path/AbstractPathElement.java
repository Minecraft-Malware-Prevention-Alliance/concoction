package info.mmpa.concoction.input.model.path;

/**
 * Common outline of path elements, declaring shared behaviors.
 */
public abstract class AbstractPathElement implements PathElement {
	@Override
	public int compareTo(PathElement o) {
		return fullDisplay().compareTo(o.fullDisplay());
	}
}
