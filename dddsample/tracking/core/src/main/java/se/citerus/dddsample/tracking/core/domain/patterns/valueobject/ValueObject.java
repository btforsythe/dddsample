package se.citerus.dddsample.tracking.core.domain.patterns.valueobject;

/**
 * A tag interface used to indicate a value object.
 * 
 * TODO eliminate type parameter, sameValueAs method
 */
public interface ValueObject<T> {

  /**
   * TODO BTF this is stupid. Use {@link #equals(Object)}.
   * 
   * Value objects compare by the values of their attributes, they don't have an identity.
   *
   * @param other The other value object.
   * @return <code>true</code> if the given value object's and this value object's attributes are the same.
   */
  boolean sameValueAs(T other);

}