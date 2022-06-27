package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import inc.evil.refactorswitch.domain.ProductStatus;
import inc.evil.refactorswitch.exceptions.ProductNotFoundException;
import inc.evil.refactorswitch.exceptions.ProductPaymentException;
import inc.evil.refactorswitch.repo.ProductRepository;
import inc.evil.refactorswitch.service.commission.CommissionApplierStrategy;
import inc.evil.refactorswitch.service.commission.DefaultCommissionApplierStrategyImpl;
import inc.evil.refactorswitch.service.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class PaymentServiceImpl implements PaymentService {

    private final ProductRepository productRepository;
    private final PaymentClient paymentClient;

    private final List<CommissionApplierStrategy> commissionApplierStrategies;
    private final DefaultCommissionApplierStrategyImpl defaultCommissionApplierStrategy;

    @Override
    public void payForProduct(Long productId, Client client) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id %s not found", productId)));

        double updatedPrice = commissionApplierStrategies.stream()
                .filter(s -> s.getCardType() == client.getCard().getType())
                .findFirst()
                .orElse(defaultCommissionApplierStrategy)
                .applyCommission(client, product);

        PaymentResponse paymentResponse = paymentClient.debitCard(client.getCard(), updatedPrice);
        if (paymentResponse.isSuccess()) {
            product.setStatus(ProductStatus.PAID);
            //process delivery, etc
            log.info("Product {} paid with success", productId);
        } else {
            //alert the user & other business logic
            log.warn("Product {} payment failed with [{}]", productId, paymentResponse.getErrorMessage());
            throw new ProductPaymentException(paymentResponse.getErrorMessage());
        }
    }

}
