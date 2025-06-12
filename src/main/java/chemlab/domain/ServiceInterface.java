package chemlab.domain;

public interface ServiceInterface<T> {
	boolean isValid(T obj);
}
