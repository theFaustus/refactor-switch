package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommissionApplierStrategyResolverImplTest {

    @Mock
    private BusinessCommissionApplierStrategyImpl businessCommissionApplierStrategy;
    @Mock
    private DefaultCommissionApplierStrategyImpl defaultCommissionApplierStrategy;

    private CommissionApplierStrategyResolver commissionApplierStrategyResolver;

    @BeforeEach
    void setUp() {
        commissionApplierStrategyResolver = new CommissionApplierStrategyResolverImpl(List.of(businessCommissionApplierStrategy), defaultCommissionApplierStrategy);
    }

    @Test
    void getCommissionApplier_whenStrategyFound_returnsCorrespondingStrategy() {
        when(businessCommissionApplierStrategy.getCardType()).thenReturn(CardType.BUSINESS);

        CommissionApplierStrategy commissionApplier = commissionApplierStrategyResolver.getCommissionApplier(CardType.BUSINESS);

        assertThat(commissionApplier).isInstanceOf(BusinessCommissionApplierStrategyImpl.class);
    }

    @Test
    void getCommissionApplier_whenStrategyNotFound_returnsDefaultStrategy() {
        CommissionApplierStrategy commissionApplier = commissionApplierStrategyResolver.getCommissionApplier(CardType.SUBPRIME);

        assertThat(commissionApplier).isInstanceOf(DefaultCommissionApplierStrategyImpl.class);

    }
}