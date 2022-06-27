package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import inc.evil.refactorswitch.domain.ProductStatus;
import inc.evil.refactorswitch.exceptions.ProductNotFoundException;
import inc.evil.refactorswitch.exceptions.ProductPaymentException;
import inc.evil.refactorswitch.repo.ProductRepository;
import inc.evil.refactorswitch.service.commission.CommissionApplierStrategyResolver;
import inc.evil.refactorswitch.service.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class PaymentServiceImpl implements PaymentService {

    private final ProductRepository productRepository;
    private final PaymentClient paymentClient;

    private final CommissionApplierStrategyResolver commissionApplierResolver;

    @Override
    public void payForProduct(Long productId, Client client) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id %s not found", productId)));

        double updatedPrice = commissionApplierResolver.getCommissionApplier(client.getCard().getType()).applyCommission(client, product);

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
