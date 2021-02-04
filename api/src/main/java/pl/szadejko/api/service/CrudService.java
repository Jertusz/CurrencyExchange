package pl.szadejko.api.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class CrudService<T extends DbEntity> {
    public JpaRepository<T, Long> repository;

    public CrudService(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public T getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        Optional<T> item = repository.findById(id);

        if (item.isPresent()) {
            repository.delete(item.orElseThrow());
        }
    }

    public List<T> getAll() {
        return repository.findAll();
    }

    public abstract T createOrUpdate(T updateEntity);
}
