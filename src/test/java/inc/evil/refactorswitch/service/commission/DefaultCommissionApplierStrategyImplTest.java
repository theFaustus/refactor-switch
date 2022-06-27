package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultCommissionApplierStrategyImplTest {

    private DefaultCommissionApplierStrategyImpl defaultCommissionApplierStrategy = new DefaultCommissionApplierStrategyImpl();
    private Product product;
    private Card card;
    private Client client;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "178-456-789", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING);
        card = new Card("Sponge Bob", "8945-7898-7895-7895", LocalDate.MAX, "123", CardType.SIMPLE, true);
        client = new Client("978456789", "Sponge", "bob", card, true, 19);
    }

    @Test
    void applyCommission_whenCardTypeSIMPLE_appliesDEFAULT_COMISSION() {
        double updatedPrice = defaultCommissionApplierStrategy.applyCommission(client, product);

        assertThat(updatedPrice).isEqualTo(304.5);
    }

    @Test
    void getCardType_returnsSIMPLE() {
        assertThat(defaultCommissionApplierStrategy.getCardType()).isEqualTo(CardType.SIMPLE);
    }
}