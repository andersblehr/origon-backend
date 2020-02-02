package co.origon.api.repository;

public interface RepositoryFactory {
  <E> Repository<E> repositoryFor(Class<E> clazz);
}
