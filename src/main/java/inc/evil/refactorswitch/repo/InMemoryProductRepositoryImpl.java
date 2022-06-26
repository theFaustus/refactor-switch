package inc.evil.refactorswitch.repo;

import inc.evil.refactorswitch.domain.Category;
import inc.evil.refactorswitch.domain.Product;
import inc.evil.refactorswitch.domain.ProductStatus;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryProductRepositoryImpl implements ProductRepository {

    private Map<Long, Product> products = Map.of(
            1L, new Product(1L, "178-456-789", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING),
            2L, new Product(2L, "178-456-735", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING),
            3L, new Product(3L, "178-456-495", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING),
            4L, new Product(4L, "178-456-565", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING));

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }
}
