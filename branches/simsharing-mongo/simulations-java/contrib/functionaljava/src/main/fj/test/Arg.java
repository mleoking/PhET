package fj.test;

import fj.F;
import fj.Show;

import static fj.Show.anyShow;
import static fj.Show.showS;

/**
 * An argument used in a property that may have undergone shrinking following falsification.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 406 $</li>
 *          <li>$LastChangedDate: 2010-06-05 08:23:15 +1000 (Sat, 05 Jun 2010) $</li>
 *          <li>$LastChangedBy: tonymorris $</li>
 *          </ul>
 */
public final class Arg<T> {
  private final T value;
  private final int shrinks;

  private Arg(final T value, final int shrinks) {
    this.value = value;
    this.shrinks = shrinks;
  }

  /**
   * Construct a property argument with the given value and number of shrinks.
   *
   * @param value   The value to construct an argument with.
   * @param shrinks The number of shrinks to construct an argument with.
   * @return A new argument.
   */
  public static <T> Arg<T> arg(final T value, final int shrinks) {
    return new Arg<T>(value, shrinks);
  }

  /**
   * Returns the argument's value.
   *
   * @return The argument's value.
   */
  public Object value() {
    return value;
  }

  /**
   * Returns the argument's number of shrinks following falsification.
   *
   * @return The argument's number of shrinks following falsification.
   */
  public int shrinks() {
    return shrinks;
  }

  /**
   * The rendering of an argument (uses {@link Object#toString()} for the argument value).
   */
  public static final Show<Arg<?>> argShow = showS(new F<Arg<?>, String>() {
    public String f(final Arg<?> arg) {
      return anyShow().showS(arg.value) +
          (arg.shrinks > 0 ? " (" + arg.shrinks + " shrink" + (arg.shrinks == 1 ? "" : 's') + ')' : "");
    }
  });
}
