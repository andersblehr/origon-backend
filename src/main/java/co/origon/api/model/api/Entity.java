package co.origon.api.model.api;

public interface Entity<E extends Entity> {
    default E key(String key) {
        throw new RuntimeException("Default Entity::key implementation invoked, must be overridden in concrete class");
    }
}
