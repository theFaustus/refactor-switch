package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class CommissionApplierStrategyResolverImpl implements CommissionApplierStrategyResolver {

    private final List<CommissionApplierStrategy> commissionApplierStrategies;
    private final DefaultCommissionApplierStrategyImpl defaultCommissionApplierStrategy;

    @Override
    public CommissionApplierStrategy getCommissionApplier(CardType cardType) {
        return commissionApplierStrategies.stream()
                .filter(s -> s.getCardType() == cardType)
                .findFirst()
                .orElse(defaultCommissionApplierStrategy);
    }
}
