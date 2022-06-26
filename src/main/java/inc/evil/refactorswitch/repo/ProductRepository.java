package inc.evil.refactorswitch.repo;

import inc.evil.refactorswitch.domain.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
}
