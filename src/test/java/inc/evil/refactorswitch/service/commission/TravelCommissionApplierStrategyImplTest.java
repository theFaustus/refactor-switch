package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TravelCommissionApplierStrategyImplTest {

    private TravelCommissionApplierStrategyImpl travelCommissionApplierStrategy = new TravelCommissionApplierStrategyImpl();
    private Product product;
    private Card card;
    private Client client;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "178-456-789", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.TRAVEL);
        card = new Card("Sponge Bob", "8945-7898-7895-7895", LocalDate.MAX, "123", CardType.TRAVEL, true);
        client = new Client("978456789", "Sponge", "bob", card, true, 19);
    }

    @Test
    void applyCommission_whenCardTypeTRAVELAndProductCategoryTRAVEL_appliesTRAVEL_COMISSION() {
        double updatedPrice = travelCommissionApplierStrategy.applyCommission(client, product);

        assertThat(updatedPrice).isEqualTo(304.2);
    }

    @Test
    void applyCommission_whenCardTypeTRAVELAndProductCategoryNotTRAVEL_appliesDEFAULT_COMISSION() {
        product.setCategory(Category.GAMING);

        double updatedPrice = travelCommissionApplierStrategy.applyCommission(client, product);

        assertThat(updatedPrice).isEqualTo(304.5);
    }

    @Test
    void getCardType_returnsTRAVEL() {
        assertThat(travelCommissionApplierStrategy.getCardType()).isEqualTo(CardType.TRAVEL);
    }
}