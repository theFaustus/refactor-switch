package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;

public interface CommissionApplierStrategyResolver {
    CommissionApplierStrategy getCommissionApplier(CardType cardType);
}
