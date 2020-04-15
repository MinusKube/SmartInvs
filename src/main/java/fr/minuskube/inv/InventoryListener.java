package fr.minuskube.inv;

import java.util.function.Consumer;

/**
 * An event listener for clickable items inside a SmartInvs that returns a generic type. By
 * utilizing generics type, we do not need to do type-casting which in return will remove
 * ClassCastException at runtime. If there is no type provided at the time of creation, the
 * compiler will produce a warning that "GenericsType is a raw type".
 * <p>
 * If you want to suppress these warnings you can use the @SuppressWarnings("rawtypes")
 * annotation to suppress the compiler warning.
 *
 * @param <T> Generic type which should be parameterized. If not type is provided, the type
 *           becomes Object.
 */
public class InventoryListener<T> {
    /**
     * Type Class Object. This field allows instantiation of classes in a type-safe manner. Where <T> represents the class itself, enabling
     * you to avoid unpleasant effects of type erasure by storing Class<T> a generic class or passing it in as a parameter to a generic
     * method. Also, note that 'T' by itself would not be sufficient to complete the task at hand. The type of 'T' is erased, so it becomes
     * an {@link Object} under the hood.
     * <p>
     * It is also important to have access to the 'Class<T>' object inside the 'select' method. Since 'netInstance' returns an object of type
     * <T>, the compiler can perform type checking, eliminating a cast.
     */
    private Class<T> type;
    /**
     * Consumer function. This field is being used as the assignment target for a lambda expression, or a method reference. The
     * consumer's function descriptor is 'T -> ()', which means an object if type T is input to the lambda with no return value.
     */
    private Consumer<T> consumer;

    /**
     * This constructor is used to explicitly declare the class generic type  and consumer
     * generics type,
     *
     * @param type generic type
     * @param consumer generic consumer
     */
    public InventoryListener(Class<T> type, Consumer<T> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    /**
     * Will insert type-casting if necessary and will provide type-checking at compile time. This
     * will ensure that no new classes are created for parameterized types. This feature is called
     * @see "<a href="https://docs.oracle.com/javase/tutorial/java/generics/erasure.html" target="_top">Type Erasure</a>".
     *
     * @param t Replaces the bounded type parameter T with the first bound interface.
     */
    public void accept(T t) { consumer.accept(t); }

    /**
     * Apply generics type for return and get the type for the inventory.
     *
     * @return the clicked inventory type
     */
    public Class<T> getType() { return type; }

}
