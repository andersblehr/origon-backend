package co.origon.api.repository.api;

public interface RepositoryFactory {
  <E> Repository<E> repositoryFor(Class<E> clazz);
}
